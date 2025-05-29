package services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import dao.HouseholdDAO;
import exception.HouseholdAlreadyExistsException;
import exception.HouseholdNotExist;
import exception.InvalidHouseholdDataException;
import exception.MemberNotFoundException;
import exception.ServiceException;
import models.Household;
import models.Member;

public class HouseholdServiceImpl implements HouseholdService {
	private HouseholdDAO householdDAO;
	private MemberServiceImpl memberService = new MemberServiceImpl();

	public HouseholdServiceImpl() {
		this.householdDAO = new HouseholdDAO();
	}

	public List<Household> getAllHouseholds() {
		return householdDAO.findAll();
	}

	@Override
	public Household getHouseholdById(int householdId) throws HouseholdNotExist {
		return householdDAO.findById(householdId);
	}


	@Override
	public void addMemberToHousehold(Household h, String memberId) throws HouseholdNotExist, ServiceException, SQLException {
		Member m = memberService.getMemberById(memberId);
		householdDAO.addMemberToHousehold(h,m);
	}

	@Override
	public void removeMember(Household h, String memberId) throws HouseholdNotExist, ServiceException, SQLException {
		Member m = memberService.getMemberById(memberId);
		householdDAO.removeMember(h,m);
		
	}

	@Override
	public List<Member> getMembers(int householdId) throws HouseholdNotExist, ServiceException {
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
			List<Member> members = getMembers(householdId);
			return members.stream()
				.map(Member::getId)
				.collect(Collectors.toList());
		} catch (ServiceException e) {
			System.err.println("Error getting member IDs for household " + householdId + ": " + e.getMessage());
			return List.of(); // Return empty list if no members found
		}
	}

	@Override
	public void addHousehold(Household household)
			throws HouseholdAlreadyExistsException, MemberNotFoundException, InvalidHouseholdDataException, HouseholdNotExist, SQLException {
		if (!validateAdd(household)) {
			return;
		}
		householdDAO.add(household);
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
	public int getMemberCount(int id) throws HouseholdNotExist {
		return getHouseholdById(id).getHouseholdSize();
	}

	private boolean validateAdd(Household household)
			throws HouseholdAlreadyExistsException, MemberNotFoundException, HouseholdNotExist, InvalidHouseholdDataException{

		// 1. Check if household number already exists
	    if (isHouseholdNumberExists(household.getHouseholdNumber())) {
	        throw new HouseholdAlreadyExistsException("Hộ khẩu với số '" + household.getHouseholdNumber() + "' đã tồn tại.");
	    }
	    
		// 2. Call business rules validation
		return validateBusinessRules(household);
	}

	private boolean validateUpdate(Household household) throws HouseholdNotExist, HouseholdAlreadyExistsException,
			MemberNotFoundException, InvalidHouseholdDataException {


		// 2. Check if household number is unique (excluding current household)
		Household existingWithSameNumber = householdDAO.findByHouseholdNumber(household.getHouseholdNumber());
		if (existingWithSameNumber != null && existingWithSameNumber.getId() != household.getId()) {
			throw new HouseholdAlreadyExistsException("Số hộ khẩu '" + household.getHouseholdNumber()
					+ "' đã được sử dụng bởi hộ khẩu khác (ID: " + existingWithSameNumber.getId() + ")");
		}

		// 3. Apply the same business logic validations as in validateAdd()
		return validateBusinessRules(household);
	}

	/**
	 * Common business validation logic used by both add and update
	 * @throws HouseholdNotExist 
	 * @throws InvalidHouseholdDataException 
	 */
	private boolean validateBusinessRules(Household household)
			throws MemberNotFoundException, HouseholdNotExist, InvalidHouseholdDataException {
		
		

		// 2. Check if owner exists
		if (!doesMemberExist(household.getOwnerId())) {
			throw new MemberNotFoundException(
					"Chủ hộ với ID '" + household.getOwnerId() + "' không tồn tại trong hệ thống");
		}
		
		// 3. Check if owner already belongs to another household
	    if (isMemberInAnotherHousehold(household.getOwnerId(), household.getHouseholdNumber())) {
	        throw new InvalidHouseholdDataException("Chủ hộ đang thuộc về một hộ khẩu khác. Cần tách hộ trước khi thêm.");
	    }

	 // 4. Validate all member IDs exist and not in another household
		if (household.getMemberIds() != null && !household.getMemberIds().isEmpty()) {
			List<String> memberIds = household.getMemberIds();
			for (String memberId : memberIds) {

				if (!doesMemberExist(memberId)) {
					throw new MemberNotFoundException(
							"Thành viên với ID '" + memberId + "' không tồn tại trong hệ thống");
				}
				
				 // Check member is not already in another household
	            if (isMemberInAnotherHousehold(memberId, household.getHouseholdNumber())) {
	                throw new InvalidHouseholdDataException("Thành viên ID '" + memberId + "' đã thuộc hộ khẩu khác.");
	            }
			}
		}

		// 5. Owner must be in the member list
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

	private boolean isMemberInAnotherHousehold(String memberId, String currentHouseholdNumber) {
	    List<Household> allHouseholds = householdDAO.findAll();
	    for (Household h : allHouseholds) {
	        if (!h.getHouseholdNumber().equals(currentHouseholdNumber) && h.getMemberIds() != null && h.getMemberIds().contains(memberId)) {
	            return true;
	        }
	    }
	    return false;
	} 

	// Helper methods

	/**
	 * Check if household number already exists
	 */
	private boolean isHouseholdNumberExists(String householdNumber) {
		try {
			Household existing = householdDAO.findByHouseholdNumber(householdNumber);
			return existing != null;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Check if member exists by ID
	 */
	private boolean doesMemberExist(String memberId) {
		try {
//        	return true;
			Member member = memberService.getMemberById(memberId);
			return member != null;
		} catch (Exception e) {
			return false;
		}
	}

}
