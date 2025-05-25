package utils;

public enum Status {
	PAID("Đã thanh toán"),
    UNPAID("Chưa thanh toán");
	
	private String displayName;
	
	Status(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public static Status fromDisplayName(String displayName) {
		for (Status status : Status.values()) {
			if (status.getDisplayName().equals(displayName)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Không tìm thấy trạng thái: " + displayName);
	}
	
	@Override 
	public String toString() {
		return this.displayName;
	}
}
