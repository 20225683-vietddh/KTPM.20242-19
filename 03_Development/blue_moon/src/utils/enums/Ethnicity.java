package utils.enums;

public enum Ethnicity {
    KINH("Kinh"),
    TAY("Tày"),
    THAI("Thái"),
    HMONG("H'Mông"),
    MUONG("Mường"),
    KHMER("Khmer"),
    HOA("Hoa"),
    NUNG("Nùng"),
    DAO("Dao"),
	OTHER("Khác");

    private final String displayName;

    Ethnicity(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 