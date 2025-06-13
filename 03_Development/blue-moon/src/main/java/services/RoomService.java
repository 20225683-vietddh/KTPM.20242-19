package services;

import dao.rooms.RoomDAO;
import dao.rooms.RoomDAOPostgreSQL;
import models.Room;
import java.sql.SQLException;
import java.util.List;

public class RoomService {
    private RoomDAO roomDAO;
    
    public RoomService() {
        try {
            this.roomDAO = new RoomDAOPostgreSQL();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database trong RoomService: " + e.getMessage());
            throw new RuntimeException("Không thể khởi tạo RoomService", e);
        }
    }
    
    /**
     * Lấy danh sách tất cả các phòng
     */
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }
    
    /**
     * Tìm kiếm phòng theo từ khóa
     */
    public List<Room> searchRooms(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRooms();
        }
        return roomDAO.searchRooms(keyword.trim());
    }
    
    /**
     * Lấy thông tin phòng theo số phòng
     */
    public Room getRoomByNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return null;
        }
        return roomDAO.getRoomByNumber(roomNumber.trim());
    }
    
    /**
     * Lấy danh sách phòng trống
     */
    public List<Room> getEmptyRooms() {
        return roomDAO.getEmptyRooms();
    }
    
    /**
     * Lấy danh sách phòng đã có người ở
     */
    public List<Room> getOccupiedRooms() {
        return roomDAO.getOccupiedRooms();
    }
    
    /**
     * Lọc phòng theo trạng thái
     */
    public List<Room> filterRoomsByStatus(String status) {
        switch (status.toLowerCase()) {
            case "trống":
            case "empty":
                return getEmptyRooms();
            case "có người":
            case "occupied":
                return getOccupiedRooms();
            case "tất cả":
            case "all":
            default:
                return getAllRooms();
        }
    }
    
    /**
     * Cập nhật trạng thái phòng
     */
    public boolean updateRoomStatus(String roomNumber, boolean isEmpty) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return false;
        }
        return roomDAO.updateRoomStatus(roomNumber.trim(), isEmpty);
    }
    
    /**
     * Thêm phòng mới
     */
    public boolean addRoom(Room room) {
        if (room == null || room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra format số phòng
        if (!isValidRoomNumber(room.getRoomNumber())) {
            throw new IllegalArgumentException("Số phòng không đúng định dạng. Phải theo format: Nhà_[số]/Tầng_[số]/BlueMoon");
        }
        
        // Kiểm tra phòng đã tồn tại chưa
        Room existingRoom = roomDAO.getRoomByNumber(room.getRoomNumber());
        if (existingRoom != null) {
            throw new IllegalArgumentException("Phòng " + room.getRoomNumber() + " đã tồn tại!");
        }
        
        return roomDAO.addRoom(room);
    }
    
    /**
     * Xóa phòng
     */
    public boolean deleteRoom(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra phòng có tồn tại không
        Room room = roomDAO.getRoomByNumber(roomNumber.trim());
        if (room == null) {
            throw new IllegalArgumentException("Phòng " + roomNumber + " không tồn tại!");
        }
        
        // Kiểm tra phòng có đang có người ở không
        if (!room.isEmpty()) {
            throw new IllegalArgumentException("Không thể xóa phòng " + roomNumber + " vì đang có hộ khẩu sinh sống!");
        }
        
        return roomDAO.deleteRoom(roomNumber.trim());
    }
    
    /**
     * Kiểm tra format số phòng có hợp lệ không
     */
    private boolean isValidRoomNumber(String roomNumber) {
        if (roomNumber == null) return false;
        return roomNumber.matches("^Nhà_[0-9]+/Tầng_[0-9]+/BlueMoon$");
    }
    
    /**
     * Đếm tổng số phòng
     */
    public int getTotalRoomsCount() {
        return getAllRooms().size();
    }
    
    /**
     * Đếm số phòng trống
     */
    public int getEmptyRoomsCount() {
        return getEmptyRooms().size();
    }
    
    /**
     * Đếm số phòng đã có người ở
     */
    public int getOccupiedRoomsCount() {
        return getOccupiedRooms().size();
    }
    
    /**
     * Tính tỷ lệ lấp đầy (%)
     */
    public double getOccupancyRate() {
        int total = getTotalRoomsCount();
        if (total == 0) return 0.0;
        
        int occupied = getOccupiedRoomsCount();
        return (double) occupied / total * 100;
    }
} 