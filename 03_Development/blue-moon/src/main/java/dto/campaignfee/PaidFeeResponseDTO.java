package dto.campaignfee;

public class PaidFeeResponseDTO {
	private int feeId;
	private String feeName;
	private boolean isMandatory;
	private int expectedAmount;
	private int paidAmount;
	
	public PaidFeeResponseDTO() {}

	public int getFeeId() {
		return feeId;
	}

	public void setFeeId(int feeId) {
		this.feeId = feeId;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
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
	
	public boolean isEnough() {
		return this.expectedAmount <= this.paidAmount;
	}
	
	public int remainingAmount() {
		return this.expectedAmount - this.paidAmount;
	}
}
