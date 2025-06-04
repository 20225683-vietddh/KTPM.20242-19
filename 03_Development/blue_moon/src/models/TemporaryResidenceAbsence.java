package models;

import java.time.LocalDate;

public class TemporaryResidenceAbsence {
    private int id;
    private int memberId;
    private String memberName;
    private String type; // "RESIDENCE" hoặc "ABSENCE"
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private String address;
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private LocalDate createdAt;
    private LocalDate updatedAt;
    
    public LocalDate getCreateAt() {
		return createdAt;
	}

	public void setCreateAt(LocalDate createAt) {
		this.createdAt = createAt;
	}

	public LocalDate getUpdateAt() {
		return updatedAt;
	}

	public void setUpdateAt(LocalDate updateAt) {
		this.updatedAt = updateAt;
	}

	public TemporaryResidenceAbsence() {}
    
    public TemporaryResidenceAbsence(int id, int memberId, String memberName, String type, 
                                   String reason, LocalDate startDate, LocalDate endDate, 
                                   String address, String status) {
        this.id = id;
        this.memberId = memberId;
        this.memberName = memberName;
        this.type = type;
        this.reason = reason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "TemporaryResidenceAbsence{" +
                "id=" + id +
                ", memberName='" + memberName + '\'' +
                ", type='" + type + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
}