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
	private String houseNumber;
	private String street;
	private String district;
	private String ward;
	private Float area;
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
	public Household(int id, String houseNumber, String district, String ward, String street, Float area,
			LocalDate creationDate, int householdSize, int ownerId, String phone, String email, String ownerName,
			List<Resident> residents) {
		this.id = id;
		this.houseNumber = houseNumber;
		this.district = district;
		this.ward = ward;
		this.street = street;
		this.area = area;
		this.creationDate = creationDate;
		this.householdSize = householdSize;
		this.ownerId = ownerId;
		this.phone = phone;
		this.email = email;
		this.ownerName = ownerName;
		this.residents = residents;
	}
	
	//auto gen id
	public Household(String houseNumber, String district, String ward, String street, Float area,
			LocalDate creationDate, int householdSize, int ownerId, String phone, String email, String ownerName,
			List<Resident> residents) throws ServiceException {
		this.houseNumber = houseNumber;
		this.district = district;
		this.ward = ward;
		this.street = street;
		this.area = area;
		this.creationDate = creationDate;
		this.householdSize = householdSize;
		this.ownerId = ownerId;
		this.ownerName = ownerName = residentService.getResidentById(ownerId).getFullName();
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


	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Float getArea() {
		return area;
	}

	public void setArea(Float area) {
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
		// Update household size based on resident count
		this.householdSize = this.residents.size();
	}
	
	public void setResidentIds(List<Integer> residentIds) throws ServiceException {
		residents.clear();
		addResidentIds(residentIds);
	}

	public void addResident(Resident resident) {
		if (resident != null && !residents.contains(resident)) {
			residents.add(resident);
			this.householdSize = residents.size();
		}
	}
	
	public void addResidentId(int residentId) throws ServiceException {
		Resident r= residentService.getResidentById(residentId);
		addResident(r);
	}
	
	public void addResidentIds(List<Integer> residentIds) throws ServiceException {
		for (int residentId: residentIds) {
			addResidentId(residentId);
		}
	}

	public void removeResident(int residentId) {
		residents.removeIf(resident -> resident.getId() == residentId);
		this.householdSize = residents.size();
	}

	public boolean hasResident(int residentId) {
		return residents.stream().map(Resident::getId).collect(Collectors.toList()).contains(residentId);
	}

	@Override
	public String toString() {
		return "Household{" +
				"id=" + id +
				", houseNumber='" + houseNumber + '\'' +
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