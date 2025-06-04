package services;

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
import exception.MemberNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Resident;

public class HouseholdServiceImpl implements HouseholdService {
	private HouseholdDAO householdDAO;
	private MemberServiceImpl memberService = new MemberServiceImpl();

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
	public void addMemberToHousehold(Household h, String memberId) throws HouseholdNotExist, ServiceException, SQLException {
		Resident m = memberService.getMemberById(memberId);
		householdDAO.addMemberToHousehold(h,m);
	}

	@Override
	public void removeMember(Household h, String memberId) throws HouseholdNotExist, ServiceException, SQLException {
		Resident m = memberService.getMemberById(memberId);
		householdDAO.removeMember(h,m);
		
	}

	@Override
	public List<Resident> getMembers(int householdId) throws HouseholdNotExist, ServiceException {
		try {
			return memberService.getMembersByHouseholdId(householdId);
		} catch (ServiceException e) {
			System.err.println("Error getting members for household " + householdId + ": " + e.getMessage());
			throw e;
		}
	}

	@Override
	public List<String> getMemberIds(int householdId) throws HouseholdNotExist {
		try {
			List<Resident> members = getMembers(householdId);
			return members.stream()
				.map(Resident::getId)
				.collect(Collectors.toList());
		} catch (ServiceException e) {
			System.err.println("Error getting member IDs for household " + householdId + ": " + e.getMessage());
			return new ArrayList<>(); // Return empty list if no members found
		}
	}

	@Override
	public void addHousehold(Household household)
			throws HouseholdAlreadyExistsException, MemberNotFoundException, InvalidHouseholdDataException, HouseholdNotExist, SQLException {
		if (!validateAdd(household)) {
			return;
		}
		
		// 1. Create household first and get the generated ID
		int generatedId = householdDAO.add(household);
		household.setId(generatedId);
		
		// 2. Update all members with the new household ID
		List<Resident> members = household.getMembers();
		for (Resident member : members) {
			member.setHouseholdId(generatedId);
			if (member.getId().equals(household.getOwnerId())) {
				member.setHouseholdHead(true);
				member.setRelationship("Chủ hộ");
			}
		}
		
		// 3. Update all members in one transaction
		try {
			memberService.updateMembers(members);
		} catch (ServiceException e) {
			// If member update fails, we should rollback the household creation
			try {
				householdDAO.delete(generatedId);
			} catch (SQLException ex) {
				throw new SQLException("Failed to rollback household creation after member update failure", ex);
			}
			throw new MemberNotFoundException("Failed to update members for new household: " + e.getMessage());
		}
	}

	@Override
	public void updateHousehold(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException,
			MemberNotFoundException, InvalidHouseholdDataException, SQLException {
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
	public int getMemberCount(int id) throws HouseholdNotExist, ServiceException {
		return getHouseholdById(id).getHouseholdSize();
	}

	private boolean validateAdd(Household household)
			throws HouseholdAlreadyExistsException, MemberNotFoundException, HouseholdNotExist, InvalidHouseholdDataException{
		// Call business rules validation
		return validateBusinessRules(household);
	}

	private boolean validateUpdate(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException,
			MemberNotFoundException, InvalidHouseholdDataException {
		// Apply business logic validations
		return validateBusinessRules(household);
	}

	/**
	 * Common business validation logic used by both add and update
	 * @throws HouseholdNotExist 
	 * @throws InvalidHouseholdDataException 
	 */
	private boolean validateBusinessRules(Household household)
			throws MemberNotFoundException, HouseholdNotExist, InvalidHouseholdDataException {
		
		// 1. Check if owner exists
		if (!doesMemberExist(household.getOwnerId())) {
			throw new MemberNotFoundException(
					"Chủ hộ với ID '" + household.getOwnerId() + "' không tồn tại trong hệ thống");
		}
		
		// 2. Check if owner already belongs to another household
	    if (isMemberInAnotherHousehold(household.getOwnerId(), household.getId())) {
	        throw new InvalidHouseholdDataException("Chủ hộ đang thuộc về một hộ khẩu khác. Cần tách hộ trước khi thêm.");
	    }

		// 3. Validate all member IDs exist and not in another household
		if (household.getMemberIds() != null && !household.getMemberIds().isEmpty()) {
			List<String> memberIds = household.getMemberIds();
			for (String memberId : memberIds) {
				if (!doesMemberExist(memberId)) {
					throw new MemberNotFoundException(
							"Thành viên với ID '" + memberId + "' không tồn tại trong hệ thống");
				}
				
				// Check member is not already in another household
	            if (isMemberInAnotherHousehold(memberId, household.getId())) {
	                throw new InvalidHouseholdDataException("Thành viên ID '" + memberId + "' đã thuộc hộ khẩu khác.");
	            }
			}
		}

		// 4. Owner must be in the member list
		if (household.getMemberIds() != null && !household.getMemberIds().isEmpty()) {
			boolean ownerInMembers = household.getMemberIds().stream()
					.anyMatch(member -> member.equals(household.getOwnerId()));

			if (!ownerInMembers) {
				throw new InvalidHouseholdDataException(
						"Chủ hộ phải được bao gồm trong danh sách thành viên của hộ khẩu");
			}
		}

		// 6. No duplicate member IDs
		if (household.getMemberIds() != null && household.getMemberIds().size() > 1) {
			long uniqueMemberCount = household.getMemberIds().stream().distinct().count();

			if (uniqueMemberCount != household.getMemberIds().size()) {
				throw new InvalidHouseholdDataException("Danh sách thành viên có ID trùng lặp");
			}
		}

		// 7. Validate household size
		if (household.getHouseholdSize() <= 0 || household.getHouseholdSize() > 20) {
			throw new InvalidHouseholdDataException("Số thành viên hộ khẩu phải từ 1 đến 20 người");
		}

		return true;
	}

	private boolean isMemberInAnotherHousehold(String memberId, int currentHouseholdId) {
		try {
			Resident member = memberService.getMemberById(memberId);
			return member.getHouseholdId() != 0 && member.getHouseholdId() != currentHouseholdId;
		} catch (ServiceException e) {
			return false;
		}
	}

	private boolean doesMemberExist(String memberId) {
		return memberService.memberExists(memberId);
	}

}
