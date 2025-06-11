package utils.enums;

public enum PlaceOfIssue {
    HA_NOI("Hà Nội"),
    HO_CHI_MINH("Hồ Chí Minh"),
    HAI_PHONG("Hải Phòng"),
    DA_NANG("Đà Nẵng"),
    CAN_THO("Cần Thơ"),
    AN_GIANG("An Giang"),
    BA_RIA_VUNG_TAU("Bà Rịa - Vũng Tàu"),
    BAC_GIANG("Bắc Giang"),
    BAC_KAN("Bắc Kạn"),
    BAC_LIEU("Bạc Liêu"),
    BAC_NINH("Bắc Ninh"),
    OTHER("Khác");

    private final String displayName;

    PlaceOfIssue(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 