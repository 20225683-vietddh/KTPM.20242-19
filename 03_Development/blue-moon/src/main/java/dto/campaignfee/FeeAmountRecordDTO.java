package dto.campaignfee;

import java.time.LocalDate;

public class FeeAmountRecordDTO {
	private int householdId;
	private int campaignFeeId;
	private int feeId;
	private int expectedAmount;
	private int paidAmount;
	private LocalDate paidDate;
	private int additionalAmount;
	private int areas;
	
	public FeeAmountRecordDTO() {}

	public int getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}

	public int getCampaignFeeId() {
		return campaignFeeId;
	}

	public void setCampaignFeeId(int campaignFeeId) {
		this.campaignFeeId = campaignFeeId;
	}

	public int getFeeId() {
		return feeId;
	}

	public void setFeeId(int feeId) {
		this.feeId = feeId;
	}

	public int getExpectedAmount() {
		return expectedAmount;
	}

	public void setExpectedAmount(int expectedAmount) {
		this.expectedAmount = expectedAmount;
	}

	public int getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(int paidAmount) {
		this.paidAmount = paidAmount;
	}

	public LocalDate getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(LocalDate paidDate) {
		this.paidDate = paidDate;
	}

	public int getAdditionalAmount() {
		return additionalAmount;
	}

	public void setAdditionalAmount(int additionalAmount) {
		this.additionalAmount = additionalAmount;
	}
	
	public boolean isFullyPaid() {
		return expectedAmount <= paidAmount;
	}
	
	public int getAreas() {
		return this.areas;
	}
	
	public void setAreas(int areas) {
		this.areas = areas;
	}
}
