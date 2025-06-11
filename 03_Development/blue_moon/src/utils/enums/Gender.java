package utils.enums;

public enum Gender {
	MALE("Nam"),
	FEMALE("Nữ"),
	OTHER("Khác");

	private final String displayName;

	Gender(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
