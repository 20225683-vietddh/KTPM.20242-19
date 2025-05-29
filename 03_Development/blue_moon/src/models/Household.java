package models;

import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import exception.ServiceException;
import services.MemberServiceImpl;

public class Household {
	private static int ID = 10;
	private final MemberServiceImpl memberService = new MemberServiceImpl();

	private int id;
	private String householdNumber;
	private String address;
	private String area;
	private int householdSize;
	private String ownerId;
	private String ownerName;
	private String phone;
	private String email;
	private Date creationDate;
//    private List<Member> members;
	// put it here temporarily because usually , it is obj, not primitive type like
	// this
	private List<Member> members;

	public Household() {
		ID = ID + 1;
		this.id = ID;
		this.householdNumber = "HH" + id;
		this.members = new ArrayList<>();
	}

	// constructor for init data
	public Household(int id, String address, String area, int householdSize, String ownerId, String ownerName,
			String phone, String email, Date creationDate, List<Member> members) {
		this.id = id;
		this.householdNumber = "HH" + id;
		this.address = address;
		this.area = area;
		this.householdSize = householdSize;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.phone = phone;
		this.email = email;
		this.creationDate = creationDate;
		this.members = members;
	}

	// constructor for auto gen id
	public Household(String address, String area, int householdSize, String ownerId, String ownerName, String phone,
			String email, Date creationDate, List<Member> members) throws ServiceException {
		ID = ID + 1;
		this.id = ID;
		this.householdNumber = "HH" + id;
		this.address = address;
		this.area = area;
		this.householdSize = householdSize;
		this.ownerId = ownerId;
		// auto set owner name
		this.ownerName = ownerName = memberService.getMemberById(ownerId).getFullName();
		this.phone = phone;
		this.email = email;
		this.creationDate = creationDate;
		this.members = members;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHouseholdNumber() {
		return householdNumber;
	}

	public void setHouseholdNumber(String householdNumber) {
		this.householdNumber = householdNumber;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) throws ServiceException, SQLException {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getHouseholdSize() {
		return householdSize;
	}

	public void setHouseholdSize(int householdSize) {
		this.householdSize = householdSize;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public List<Member> getMembers() {
		return members;
	}
	
	public List<String> getMemberIds(){
		return members.stream().map(Member::getId).collect(Collectors.toList());
	}

	public void setMembers(List<Member> members) {
		this.members = members != null ? members : new ArrayList<>();
		// Update household size based on member count
		this.householdSize = this.members.size();
	}
	
	public void setMemberIds(List<String> memberIds) throws ServiceException {
		members.clear();
		addMemberIds(memberIds);
	}

	// Utility methods for member management
	public void addMember(Member member) {
		if (member != null && !members.contains(member)) {
			members.add(member);
			this.householdSize = members.size();
		}
	}
	
	public void addMemberId(String memberId) throws ServiceException {
		Member m = memberService.getMemberById(memberId);
		members.add(m);
	}
	
	public void addMemberIds(List<String> memberIds) throws ServiceException {
		for (String m: memberIds) {
			addMemberId(m);
		}
	}

	public void removeMember(String memberId) {

		members.removeIf(member -> member.getId().equals(memberId));

		this.householdSize = members.size();
	}

	public boolean hasMember(String memberId) {
		return members.stream().map(Member::getId).collect(Collectors.toList()).contains(memberId);
	}

//	// Validation methods
//    public boolean isValid() {
//        return householdNumber != null && !householdNumber.trim().isEmpty() &&
//               ownerId != null && !ownerId.trim().isEmpty() &&
//               address != null && !address.trim().isEmpty() &&
//               area != null && !area.trim().isEmpty();
//    }
//    
//    public List<String> getValidationErrors() {
//        List<String> errors = new ArrayList<>();
//        
//        if (householdNumber == null || householdNumber.trim().isEmpty()) {
//            errors.add("Số hộ khẩu không được để trống");
//        }
//        
//        if (ownerId == null || ownerId.trim().isEmpty()) {
//            errors.add("ID chủ hộ không được để trống");
//        }
//        
//        if (address == null || address.trim().isEmpty()) {
//            errors.add("Địa chỉ không được để trống");
//        }
//        
//        if (area == null || area.trim().isEmpty()) {
//            errors.add("Khu vực không được để trống");
//        }
//        
//        // Optional: Validate phone format
//        if (phone != null && !phone.trim().isEmpty()) {
//            if (!phone.matches("^[0-9+\\-\\s()]+$")) {
//                errors.add("Số điện thoại không hợp lệ");
//            }
//        }
//        
//        // Optional: Validate email format
//        if (email != null && !email.trim().isEmpty()) {
//            if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
//                errors.add("Email không hợp lệ");
//            }
//        }
//        
//        return errors;
//    }

	@Override
	public String toString() {
		return String.format(
				"Household{id=%d, householdNumber='%s', ownerId='%s', ownerName='%s', "
						+ "address='%s', area='%s', phone='%s', email='%s', householdSize=%d, "
						+ "creationDate='%s', memberCount=%d}",
				id, householdNumber, ownerId, ownerName, address, area, phone, email, householdSize, creationDate,
				members.size());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Household household = (Household) obj;
		return id == household.id && Objects.equals(householdNumber, household.householdNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, householdNumber);
	}
	
}