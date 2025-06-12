package models;

import java.time.LocalDate;
import java.util.List;

public class Household {
	private int householdId;
	private String houseNumber;
	private String district;
	private String ward;
	private String street;
	private LocalDate registrationDate;
	private int numberOfResidents;
	private int headResidentId;
	private int areas;
	private List<Resident> residents;
	
	public Household() {}

	public int getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
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

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public int getNumberOfResidents() {
		return numberOfResidents;
	}

	public void setNumberOfResidents(int numberOfResidents) {
		this.numberOfResidents = numberOfResidents;
	}

	public int getHeadResidentId() {
		return headResidentId;
	}

	public void setHeadResidentId(int headResidentId) {
		this.headResidentId = headResidentId;
	}

	public int getAreas() {
		return areas;
	}

	public void setAreas(int areas) {
		this.areas = areas;
	}

	public List<Resident> getResidents() {
		return residents;
	}

	public void setResidents(List<Resident> residents) {
		this.residents = residents;
	}
	
	// Legacy getter for backward compatibility
	public int getHead_resident_id() {
		return headResidentId;
	}

	public void setHead_resident_id(int head_resident_id) {
		this.headResidentId = head_resident_id;
	}
}
