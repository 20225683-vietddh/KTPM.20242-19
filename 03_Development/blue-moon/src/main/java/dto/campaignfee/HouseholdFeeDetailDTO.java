package dto.campaignfee;

public class HouseholdFeeDetailDTO {
    private int householdId;
    private String houseNumber;
    private int expectedAmount;
    private int paidAmount;
    private int remainingAmount;
    private boolean isFullyPaid;
    
    public HouseholdFeeDetailDTO() {}
    
    public HouseholdFeeDetailDTO(int householdId, String houseNumber, int expectedAmount, int paidAmount) {
        this.householdId = householdId;
        this.houseNumber = houseNumber;
        this.expectedAmount = expectedAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = expectedAmount - paidAmount;
        this.isFullyPaid = paidAmount >= expectedAmount;
    }
    
    // Getters
    public int getHouseholdId() {
        return householdId;
    }
    
    public String getHouseNumber() {
        return houseNumber;
    }
    
    public int getExpectedAmount() {
        return expectedAmount;
    }
    
    public int getPaidAmount() {
        return paidAmount;
    }
    
    public int getRemainingAmount() {
        return remainingAmount;
    }
    
    public boolean isFullyPaid() {
        return isFullyPaid;
    }
    
    // Setters
    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }
    
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
    
    public void setExpectedAmount(int expectedAmount) {
        this.expectedAmount = expectedAmount;
        this.remainingAmount = expectedAmount - paidAmount;
        this.isFullyPaid = paidAmount >= expectedAmount;
    }
    
    public void setPaidAmount(int paidAmount) {
        this.paidAmount = paidAmount;
        this.remainingAmount = expectedAmount - paidAmount;
        this.isFullyPaid = paidAmount >= expectedAmount;
    }
} 