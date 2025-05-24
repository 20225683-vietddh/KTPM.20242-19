package models;

import java.time.LocalDate;

public class Citizen {
    private int id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String identityNumber;

    public Citizen(int id, String fullName, LocalDate dateOfBirth, String gender, String address, String identityNumber) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.identityNumber = identityNumber;
    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    @Override
    public String toString() {
        return "Citizen{" + "id=" + id + ", fullName='" + fullName + '\'' + ", dateOfBirth=" + dateOfBirth +
               ", gender='" + gender + '\'' + ", address='" + address + '\'' + ", identityNumber='" + identityNumber + '\'' + '}';
    }
}