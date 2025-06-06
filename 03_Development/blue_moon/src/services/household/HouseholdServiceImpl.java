package services.household;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Action;

import dao.household.HouseholdDAO;
import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.ResidentNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Resident;
import services.resident.ResidentServiceImpl;
import services.room.RoomService;
import services.room.RoomServiceImpl;
import utils.FieldVerifier.ValidationResult;
import utils.enums.ActionType;

public class HouseholdServiceImpl implements HouseholdService {
	private HouseholdDAO householdDAO;
	private ResidentServiceImpl residentService = new ResidentServiceImpl();
	private RoomService roomService = new RoomServiceImpl();

	public HouseholdServiceImpl() {
		this.householdDAO = new HouseholdDAO();
	}

	public List<Household> getAll() throws ServiceException {
		return householdDAO.findAll();
	}

	@Override
	public Household getHouseholdById(int householdId) throws HouseholdNotExist, ServiceException {
		return householdDAO.findById(householdId);
	}


	@Override
	public List<Resident> getResidents(int householdId) throws HouseholdNotExist, ServiceException {
		return residentService.getResidentsByHouseholdId(householdId);
	}

	@Override
	public List<Integer> getResidentIds(int householdId) throws HouseholdNotExist, ServiceException {
		
			List<Resident> residents = getResidents(householdId);
			return residents.stream()
				.map(Resident::getId)
				.collect(Collectors.toList());
		
	}

	@Override
	public int getResidentCount(int id) throws HouseholdNotExist, ServiceException {
		return getHouseholdById(id).getHouseholdSize();
	}
	
	@Override
	public void addHousehold(Household household)
			throws HouseholdAlreadyExistsException, ResidentNotFoundException, InvalidHouseholdDataException, HouseholdNotExist, SQLException, ServiceException {
		if (!validateAddUpdate(household,"",ActionType.ADD)) {
			return;
		}
		
		// 1. Create household first and get the generated ID
		int generatedId = householdDAO.add(household);
		household.setId(generatedId);
		
		// 2. Update all residents with the new household ID
		List<Resident> residents = household.getResidents();
		for (Resident resident : residents) {
			resident.setHouseholdId(generatedId);
		}
		// 3. Update all residents + room in one transaction
		try {
			residentService.updateResidents(residents);
			roomService.occupyRoom(household.getHouseNumber(), household.getId());
		} catch (ServiceException e) {
			// If resident update fails, we should rollback the household creation
			try {
				householdDAO.delete(generatedId);
			} catch (SQLException ex) {
				throw new SQLException("Failed to rollback household creation after resident update failure", ex);
			}
			throw new ResidentNotFoundException("Failed to update residents for new household: " + e.getMessage());
		}
	}

	@Override
	public void addResidentToHousehold(Household h, String residentCitizenId) throws HouseholdNotExist, ServiceException, SQLException {
		Resident m = residentService.getResidentByCitizenId(residentCitizenId);
		householdDAO.addResidentToHousehold(h,m);
	}

	@Override
	public void addResidentsToHousehold(Household h, List<String> residentCitizenIds)
			throws HouseholdNotExist, ServiceException, SQLException {
	
		for (String r : residentCitizenIds) {
			addResidentToHousehold(h, r);
		}
	}

	@Override
	public void updateHousehold(Household household, String oldRoomNumber) throws HouseholdNotExist, HouseholdAlreadyExistsException,
			ResidentNotFoundException, InvalidHouseholdDataException, SQLException, ServiceException {
		if (!validateAddUpdate(household, oldRoomNumber, ActionType.UPDATE)) {
			return;
		}

		// First handle room changes if room number has changed
		try {
			if (!oldRoomNumber.equals(household.getHouseNumber())) {
				// First vacate the old room
				roomService.vacateRoom(oldRoomNumber);
			}
		} catch (SQLException ex) {
			throw new SQLException("Failed to vacate old room: " + ex.getMessage(), ex);
		}

		// Then update the household
		householdDAO.update(household);

		// Finally occupy the new room if room number has changed
		try {
			if (!oldRoomNumber.equals(household.getHouseNumber())) {
				roomService.occupyRoom(household.getHouseNumber(), household.getId());
			}
		} catch (SQLException ex) {
			// If occupying new room fails, try to restore old room
			try {
				roomService.occupyRoom(oldRoomNumber, household.getId());
			} catch (SQLException restoreEx) {
				throw new SQLException("Failed to update room and restore failed: " + restoreEx.getMessage(), restoreEx);
			}
			throw new SQLException("Failed to occupy new room: " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean deleteHousehold(int id) throws HouseholdNotExist, SQLException {
		householdDAO.delete(id);
		return true;
	}

	@Override
	public void removeResident(Household h, String residentCitizenId) throws HouseholdNotExist, ServiceException, SQLException {
		Resident m = residentService.getResidentByCitizenId(residentCitizenId);
		householdDAO.removeResident(h,m);
		
	}

	private boolean validateAddUpdate(Household household, String oldRoomNumber, ActionType actionType)
			throws ResidentNotFoundException, HouseholdNotExist, InvalidHouseholdDataException, ServiceException, SQLException {
		//Deeper validation 
		
		// 1. Check if owner exists
		if (!doesResidentExist(household.getOwnerId())) {
			throw new ResidentNotFoundException(
					"Chủ hộ với ID '" + household.getOwnerId() + "' không tồn tại trong hệ thống");
		}

		// 2. Check if owner already belongs to another household
	    if (isResidentInAnotherHousehold(household.getOwnerId(), household.getId())) {
	        throw new InvalidHouseholdDataException("Chủ hộ đang thuộc về một hộ khẩu khác. Cần tách hộ trước khi thêm.");
	    }
		
		// 3. Check if room is already occupied
		if (roomExists(household.getHouseNumber(), oldRoomNumber, actionType)) {
			throw new InvalidHouseholdDataException("Phòng " + household.getHouseNumber() + " đã có người ở");
		}

		// 4. Check if phone is already used
		if (phoneExists(household.getPhone() , household.getId())) {
			throw new InvalidHouseholdDataException("Số điện thoại " + household.getPhone() + " đã được sử dụng bởi hộ khẩu khác");
		}

		// 5. Check if email is already used
		if (emailExists(household.getEmail(), household.getId())) {
			throw new InvalidHouseholdDataException("Email " + household.getEmail() + " đã được sử dụng bởi hộ khẩu khác");
		}

	    List<Integer> residentIds = household.getResidentIds();
		// 6. Validate all resident IDs exist and not in another household
		if (residentIds!= null && !residentIds.isEmpty()) {
			
			for (int rId : residentIds) {
				if (!doesResidentExist(rId)) {
					throw new ResidentNotFoundException(
							"Thành viên với ID '" + rId + "' không tồn tại trong hệ thống");
				}
				
				// Check resident is not already in another household
	            if (isResidentInAnotherHousehold(rId, household.getId())) {
	                throw new InvalidHouseholdDataException("Thành viên ID '" + rId + "' đã thuộc hộ khẩu khác.");
	            }
			}
		}

		// 7. Owner must be in the resident list
		if (residentIds != null && !residentIds.isEmpty()) {
			boolean ownerInResidents = residentIds.stream()
					.anyMatch(rId -> rId.equals(household.getOwnerId()));

			if (!ownerInResidents) {
				throw new InvalidHouseholdDataException(
						"Chủ hộ phải được bao gồm trong danh sách thành viên của hộ khẩu");
			}
		}

		// 8. No duplicate resident IDs
		if (residentIds != null && residentIds.size() > 1) {
			long uniqueResidentCount = residentIds.stream().distinct().count();

			if (uniqueResidentCount != residentIds.size()) {
				throw new InvalidHouseholdDataException("Danh sách thành viên có ID trùng lặp");
			}
		}

		return true;
	}

	private boolean isResidentInAnotherHousehold(int residentId, int currentHouseholdId) {
		try {
			Resident resident = residentService.getResidentById(residentId);
			return resident.getHouseholdId() != 0 && resident.getHouseholdId() != currentHouseholdId;
		} catch (ServiceException e) {
			return false;
		}
	}

	private boolean doesResidentExist(int residentId) {
		return residentService.residentExists(residentId);
	}

	@Override
	public boolean phoneExists(String phone, int householdId) throws ServiceException {
		Household h =  householdDAO.findByPhone(phone);
		return h != null && h.getId() != householdId;
	}

	@Override
	public boolean emailExists(String email, int householdId) throws ServiceException {
		Household h =  householdDAO.findByEmail(email);
		return h != null && h.getId() != householdId;
	}

	@Override
	public boolean roomExists(String roomNumber, String oldRoomNUmber, ActionType actionType) throws ServiceException, SQLException {
		if (actionType.equals(ActionType.ADD)) return !roomService.isRoomAvailable(roomNumber);
		else if (actionType.equals(ActionType.UPDATE)) return !roomService.isRoomAvailable(roomNumber) 
				&& !roomNumber.equals(oldRoomNUmber) ;
		return false; 
	}

}
