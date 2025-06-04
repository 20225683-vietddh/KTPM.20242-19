package models;

import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import exception.ServiceException;
import services.MemberServiceImpl;

public class Household {
	private final MemberServiceImpl memberService = new MemberServiceImpl();

	//giong
	private int id;
	private String houseNumber;
	private String district;
	private String ward;
	private String street;
	
	
	private LocalDate creationDate;
	
	private int householdSize;
	private String ownerId;
	
	//khac
//	private String address;
	private Float area;
	
	
	//them
	private String phone;
	private String email;
	private String ownerName;
	private List<Resident> members;

	public Household() {
		this.members = new ArrayList<>();
	}

	// constructor for init data
	public Household(int id, String address, String area, int householdSize, String ownerId, String ownerName,
			String phone, String email, Date creationDate, List<Resident> members) {
		this.id = id;
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
			String email, Date creationDate, List<Resident> members) throws ServiceException {
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

	public List<Resident> getMembers() {
		return members;
	}
	
	public List<String> getMemberIds(){
		return members.stream().map(Resident::getId).collect(Collectors.toList());
	}

	public void setMembers(List<Resident> members) {
		this.members = members != null ? members : new ArrayList<>();
		// Update household size based on member count
		this.householdSize = this.members.size();
	}
	
	public void setMemberIds(List<String> memberIds) throws ServiceException {
		members.clear();
		addMemberIds(memberIds);
	}

	// Utility methods for member management
	public void addMember(Resident member) {
		if (member != null && !members.contains(member)) {
			members.add(member);
			this.householdSize = members.size();
		}
	}
	
	public void addMemberId(String memberId) throws ServiceException {
		Resident m = memberService.getMemberById(memberId);
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
		return members.stream().map(Resident::getId).collect(Collectors.toList()).contains(memberId);
	}

	@Override
	public String toString() {
		return "Household{" +
				"id=" + id +
				", address='" + address + '\'' +
				", area='" + area + '\'' +
				", householdSize=" + householdSize +
				", ownerId='" + ownerId + '\'' +
				", ownerName='" + ownerName + '\'' +
				", phone='" + phone + '\'' +
				", email='" + email + '\'' +
				", creationDate=" + creationDate +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Household other = (Household) obj;
		return id == other.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}