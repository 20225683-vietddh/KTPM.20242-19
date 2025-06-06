package models;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import exception.ServiceException;
import services.resident.ResidentServiceImpl;

public class Household {
	private final ResidentServiceImpl residentService = new ResidentServiceImpl();

	//giong
	private int id;
	private String houseNumber;//room number
	private String street;
	private String ward;
	private String district;
	private int householdSize;
	private int ownerId;
	private String ownerName;
	private String phone;
	private String email;	
	private LocalDate creationDate;
	private List<Resident> residents;

	
	public Household() {
		this.residents = new ArrayList<>();
	}
	

	//full construtor
	public Household(int id, String houseNumber, String street, String ward, String district,
			int householdSize, int ownerId, String phone, String email, LocalDate creationDate) {
		this.id = id;
		this.houseNumber = houseNumber;
		this.street = street;
		this.ward = ward;
		this.district = district;
		this.householdSize = householdSize;
		this.ownerId = ownerId;
		this.phone = phone;
		this.email = email;
		this.creationDate = creationDate;
	}
	
	//auto gen id
	public Household(String houseNumber, String district, String ward, String street,
			LocalDate creationDate, int householdSize, int ownerId, String phone, String email, String ownerName,
			List<Resident> residents) throws ServiceException {
		this.houseNumber = houseNumber;
		this.district = district;
		this.ward = ward;
		this.street = street;
		this.creationDate = creationDate;
		this.householdSize = householdSize;
		this.ownerId = ownerId;
		this.ownerName = residentService.getResidentById(ownerId).getFullName();
		this.phone = phone;
		this.email = email;
		this.residents = residents;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ResidentServiceImpl getMemberService() {
		return residentService;
	}

	
	public String getHouseNumber() {
		return houseNumber;
	}
	
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	
	public String getDistrict() {
		return district;
	}
	
	public void setDistrict(String district) {
		this.district = district;
	}
	
	public String getWard() {
		return ward;
	}
	
	public void setWard(String ward) {
		this.ward = ward;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public int getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}


	public String getOwnerName() throws ServiceException {
		
		return residentService.getResidentById(this.ownerId).getFullName();
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public List<Resident> getResidents() {
		return residents;
	}
	
	public List<Integer> getResidentIds(){
		return residents.stream().map(Resident::getId).collect(Collectors.toList());
	}

	public void setResidents(List<Resident> residents) {
		this.residents = residents != null ? residents : new ArrayList<>();
		this.householdSize = this.residents.size();
	}
	
	public void setResidentCitizenIds(List<String> residentCitizenIds) throws ServiceException {
		residents.clear();
		addResidentCitizenIds(residentCitizenIds);
	}

	public void addResident(Resident resident) {
		if (resident != null && !residents.contains(resident)) {
			residents.add(resident);
			this.householdSize = residents.size();
		}
	}
	
	public void addResidents(List<Resident> residents) {
		if (residents != null ) {
			for (Resident r : residents) residents.add(r);
			this.householdSize = residents.size();
		}
		
	}
	
	public void addResidentCitizenId(String residentCitizenId) throws ServiceException {
		Resident r= residentService.getResidentByCitizenId(residentCitizenId);
		addResident(r);
	}
	
	public void addResidentCitizenIds(List<String> residentCitizenIds) throws ServiceException {
		for (String residentCitizenId: residentCitizenIds) {
			addResidentCitizenId(residentCitizenId);
		}
	}

	public void removeResident(String residentCitizenId) {
		residents.removeIf(resident -> resident.getCitizenId() == residentCitizenId);
		this.householdSize = residents.size();
	}

	public boolean hasResident(String residentCitizenId) {
		return residents.stream().map(Resident::getCitizenId).collect(Collectors.toList()).contains(residentCitizenId);
	}

	@Override
	public String toString() {
		return "Household{" +
				"id=" + id +
				", houseNumber='" + houseNumber + '\'' +
				", street='" + street + '\'' +
				", ward='" + ward + '\'' +
				", district='" + district + '\'' +
				", householdSize=" + householdSize +
				", ownerId=" + ownerId +
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
	
	public void setRelationships(List<utils.enums.RelationshipType> relationships) throws ServiceException, SQLException {
		if (relationships == null || relationships.isEmpty()) {
			return;
		}

		// Đảm bảo số lượng quan hệ khớp với số lượng thành viên
		if (relationships.size() != residents.size()) {
			throw new ServiceException("Số lượng quan hệ không khớp với số lượng thành viên!");
		}

		// Cập nhật quan hệ cho từng thành viên
		for (int i = 0; i < residents.size(); i++) {
			Resident resident = residents.get(i);
			utils.enums.RelationshipType relationship = relationships.get(i);

			// Cập nhật quan hệ và trạng thái chủ hộ
			resident.setRelationship(relationship);
			resident.setHouseholdHead(resident.getId() == ownerId);
		}
	}
}