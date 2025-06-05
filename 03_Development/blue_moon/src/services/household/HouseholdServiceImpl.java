package services.household;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dao.household.HouseholdDAO;
import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.ResidentNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Resident;
import services.resident.ResidentServiceImpl;

public class HouseholdServiceImpl implements HouseholdService {
	private HouseholdDAO householdDAO;
	private ResidentServiceImpl residentService = new ResidentServiceImpl();

	public HouseholdServiceImpl() {
		this.householdDAO = new HouseholdDAO();
	}

	public List<Household> getAllHouseholds() throws ServiceException {
		return householdDAO.findAll();
	}

	@Override
	public Household getHouseholdById(int householdId) throws HouseholdNotExist, ServiceException {
		return householdDAO.findById(householdId);
	}


	@Override
	public void addResidentToHousehold(Household h, int residentId) throws HouseholdNotExist, ServiceException, SQLException {
		Resident m = residentService.getResidentById(residentId);
		householdDAO.addResidentToHousehold(h,m);
	}

	@Override
	public void removeResident(Household h, int residentId) throws HouseholdNotExist, ServiceException, SQLException {
		Resident m = residentService.getResidentById(residentId);
		householdDAO.removeResident(h,m);
		
	}

	@Override
	public List<Resident> getResidents(int householdId) throws HouseholdNotExist, ServiceException {
		try {
			return residentService.getResidentsByHouseholdId(householdId);
		} catch (ServiceException e) {
			System.err.println("Error getting residents for household " + householdId + ": " + e.getMessage());
			throw e;
		}
	}

	@Override
	public List<Integer> getResidentIds(int householdId) throws HouseholdNotExist {
		try {
			List<Resident> residents = getResidents(householdId);
			return residents.stream()
				.map(Resident::getId)
				.collect(Collectors.toList());
		} catch (ServiceException e) {
			System.err.println("Error getting resident IDs for household " + householdId + ": " + e.getMessage());
			return new ArrayList<>(); // Return empty list if no residents found
		}
	}

	@Override
	public void addHousehold(Household household)
			throws HouseholdAlreadyExistsException, ResidentNotFoundException, InvalidHouseholdDataException, HouseholdNotExist, SQLException {
		if (!validateAdd(household)) {
			return;
		}
		
		// 1. Create household first and get the generated ID
		int generatedId = householdDAO.add(household);
		household.setId(generatedId);
		
		// 2. Update all residents with the new household ID
		List<Resident> residents = household.getResidents();
		for (Resident resident : residents) {
			resident.setHouseholdId(generatedId);
			if (resident.getId() == household.getOwnerId()) {
				resident.setHouseholdHead(true);
				resident.setRelationship("Chủ hộ");
			}
		}
		
		// 3. Update all residents in one transaction
		try {
			residentService.updateResidents(residents);
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
	public void addResidentsToHousehold(Household h, List<Integer> residentIds)
			throws HouseholdNotExist, ServiceException, SQLException {

		for (int r : residentIds) {
			addResidentToHousehold(h, r);
		}
	}

	@Override
	public void updateHousehold(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException,
			ResidentNotFoundException, InvalidHouseholdDataException, SQLException {
		if (!validateUpdate(household)) {
			return;
		}
		householdDAO.update(household);
	}

	@Override
	public boolean deleteHousehold(int id) throws HouseholdNotExist, SQLException {
		householdDAO.delete(id);
		return true;
	}

	@Override
	public int getResidentCount(int id) throws HouseholdNotExist, ServiceException {
		return getHouseholdById(id).getHouseholdSize();
	}

	private boolean validateAdd(Household household)
			throws HouseholdAlreadyExistsException, ResidentNotFoundException, HouseholdNotExist, InvalidHouseholdDataException{
		// Call business rules validation
		return validateBusinessRules(household);
	}

	private boolean validateUpdate(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException,
			ResidentNotFoundException, InvalidHouseholdDataException {
		// Apply business logic validations
		return validateBusinessRules(household);
	}

	/**
	 * Common business validation logic used by both add and update
	 * @throws HouseholdNotExist 
	 * @throws InvalidHouseholdDataException 
	 */
	private boolean validateBusinessRules(Household household)
			throws ResidentNotFoundException, HouseholdNotExist, InvalidHouseholdDataException {
		
		// 1. Check if owner exists
		if (!doesResidentExist(household.getOwnerId())) {
			throw new ResidentNotFoundException(
					"Chủ hộ với ID '" + household.getOwnerId() + "' không tồn tại trong hệ thống");
		}
		
		// 2. Check if owner already belongs to another household
	    if (isResidentInAnotherHousehold(household.getOwnerId(), household.getId())) {
	        throw new InvalidHouseholdDataException("Chủ hộ đang thuộc về một hộ khẩu khác. Cần tách hộ trước khi thêm.");
	    }

		// 3. Validate all resident IDs exist and not in another household
		if (household.getResidentIds() != null && !household.getResidentIds().isEmpty()) {
			List<Integer> residentIds = household.getResidentIds();
			for (int residentId : residentIds) {
				if (!doesResidentExist(residentId)) {
					throw new ResidentNotFoundException(
							"Thành viên với ID '" + residentId + "' không tồn tại trong hệ thống");
				}
				
				// Check resident is not already in another household
	            if (isResidentInAnotherHousehold(residentId, household.getId())) {
	                throw new InvalidHouseholdDataException("Thành viên ID '" + residentId + "' đã thuộc hộ khẩu khác.");
	            }
			}
		}

		// 4. Owner must be in the resident list
		if (household.getResidentIds() != null && !household.getResidentIds().isEmpty()) {
			boolean ownerInResidents = household.getResidentIds().stream()
					.anyMatch(resident -> resident.equals(household.getOwnerId()));

			if (!ownerInResidents) {
				throw new InvalidHouseholdDataException(
						"Chủ hộ phải được bao gồm trong danh sách thành viên của hộ khẩu");
			}
		}

		// 6. No duplicate resident IDs
		if (household.getResidentIds() != null && household.getResidentIds().size() > 1) {
			long uniqueResidentCount = household.getResidentIds().stream().distinct().count();

			if (uniqueResidentCount != household.getResidentIds().size()) {
				throw new InvalidHouseholdDataException("Danh sách thành viên có ID trùng lặp");
			}
		}

		// 7. Validate household size
		if (household.getHouseholdSize() <= 0 || household.getHouseholdSize() > 20) {
			throw new InvalidHouseholdDataException("Số thành viên hộ khẩu phải từ 1 đến 20 người");
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

}
