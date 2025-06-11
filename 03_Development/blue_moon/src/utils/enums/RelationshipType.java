package utils.enums;

public enum RelationshipType {
	FATHER("Bố"),
	MOTHER("Mẹ"),
	SON("Con trai"),
	DAUGHTER("Con gái"),
	HUSBAND("Chồng"),
	WIFE("Vợ"),
	BROTHER("Anh/Em trai"),
	SISTER("Chị/Em gái"),
	GRAND_FATHER("Ông"),
	GRAND_MOTHER("Bà"),
	GRAND_SON("Cháu trai"),
	GRAND_DAUGHTER("Cháu gái"),
	UNCLE("Chú/Bác"),
	AUNT("Cô/Dì"),
	NEPHEW("Cháu trai"),
	NIECE("Cháu gái"),
	COUSIN("Anh/Chị/Em họ"),
	FATHER_IN_LAW("Bố chồng/vợ"),
	MOTHER_IN_LAW("Mẹ chồng/vợ"),
	SON_IN_LAW("Con rể"),
	DAUGHTER_IN_LAW("Con dâu"),
	BROTHER_IN_LAW("Anh/Em rể"),
	SISTER_IN_LAW("Chị/Em dâu"),
	OTHER("Khác"),
	UNKNOWN("Không rõ");

	private final String displayName;

	RelationshipType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
