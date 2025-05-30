package utils;

public enum Role {
	ADMIN("Administrator"),
    ACCOUNTANT("Kế toán"),
    LEADER("Tổ trưởng/Tổ phó");
	
	private String displayName;
	
	Role(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	@Override 
	public String toString() {
		return this.displayName;
	}
}
