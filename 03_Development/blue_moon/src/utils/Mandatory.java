package utils;

public enum Mandatory {
	MANDATORY("Bắt buộc"),
    OPTIONAL("Không bắt buộc");
	
	private String displayName;
	
	Mandatory(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public static Mandatory fromDisplayName(String displayName) {
		for (Mandatory mandatory : Mandatory.values()) {
			if (mandatory.getDisplayName().equals(displayName)) {
				return mandatory;
			}
		}
		throw new IllegalArgumentException("Không tìm thấy loại bắt buộc: " + displayName);
	}
	
	public static Mandatory fromBoolean(boolean isMandatory) {
		return isMandatory ? MANDATORY : OPTIONAL;
	}
	
	public boolean toBoolean() {
		return this == MANDATORY;
	}
	
	@Override 
	public String toString() {
		return this.displayName;
	}
}
