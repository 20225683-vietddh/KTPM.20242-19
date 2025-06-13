package dao.rooms;

import models.Room;
import java.util.List;

public interface RoomDAO {
    /**
     * Lấy danh sách tất cả các phòng
     */
    List<Room> getAllRooms();
    
    /**
     * Tìm kiếm phòng theo số phòng
     */
    List<Room> searchRooms(String keyword);
    
    /**
     * Lấy thông tin phòng theo số phòng
     */
    Room getRoomByNumber(String roomNumber);
    
    /**
     * Lấy danh sách phòng trống
     */
    List<Room> getEmptyRooms();
    
    /**
     * Lấy danh sách phòng đã có người ở
     */
    List<Room> getOccupiedRooms();
    
    /**
     * Cập nhật trạng thái phòng
     */
    boolean updateRoomStatus(String roomNumber, boolean isEmpty);
    
    /**
     * Thêm phòng mới
     */
    boolean addRoom(Room room);
    
    /**
     * Xóa phòng
     */
    boolean deleteRoom(String roomNumber);
} 