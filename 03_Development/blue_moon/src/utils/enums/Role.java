package utils.enums;

public enum Role {
	ADMIN("Administrator"),
    ACCOUNTANT("Ke toan"),
    LEADER("To truong/To pho");
	
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
