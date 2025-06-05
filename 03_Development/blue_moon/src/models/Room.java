package models;

public class Room {
    private int id;
    private String roomNumber;
    private boolean isOccupied;
    private Integer householdId;  // Can be null if room is not occupied

    public Room() {
    }

    public Room(int id, String roomNumber, boolean isOccupied, Integer householdId) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.isOccupied = isOccupied;
        this.householdId = householdId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public Integer getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Integer householdId) {
        this.householdId = householdId;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", isOccupied=" + isOccupied +
                ", householdId=" + householdId +
                '}';
    }
} 