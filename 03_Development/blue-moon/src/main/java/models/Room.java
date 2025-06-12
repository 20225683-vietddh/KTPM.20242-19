package models;

public class Room {
    private String roomNumber;
    private boolean isEmpty;
    private Household household; // Hộ khẩu đang sinh sống trong phòng (nếu có)
    
    public Room() {}
    
    public Room(String roomNumber, boolean isEmpty) {
        this.roomNumber = roomNumber;
        this.isEmpty = isEmpty;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }
    
    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }
    
    public Household getHousehold() {
        return household;
    }
    
    public void setHousehold(Household household) {
        this.household = household;
        // Tự động cập nhật trạng thái phòng
        this.isEmpty = (household == null);
    }
    
    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", isEmpty=" + isEmpty +
                '}';
    }
} 