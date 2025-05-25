package models;

public class Household {
	private int householdId;
	private String houseNumber;
	private String district;
	private String ward;
	private String street;
	private int head_resident_id;
	
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

	public int getHead_resident_id() {
		return head_resident_id;
	}

	public void setHead_resident_id(int head_resident_id) {
		this.head_resident_id = head_resident_id;
	}
}
