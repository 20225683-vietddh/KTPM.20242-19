package services.room;

import java.sql.SQLException;
import java.util.List;

import dao.room.RoomDAO;
import exception.ServiceException;
import models.Room;

public class RoomServiceImpl implements RoomService {
	private final RoomDAO roomDAO = new RoomDAO();

	@Override
	public Room getRoomById(int id) throws ServiceException, SQLException {
		return roomDAO.findById(id);
	}

	@Override
	public Room getRoomByNumber(String roomNumber) throws ServiceException, SQLException {
		return roomDAO.findByRoomNumber(roomNumber);
	}
	
	public String getRoomNumberByHouseholdId(int householdId) throws ServiceException {
	    try {
	        List<Room> allRooms = getAllRooms(); 
	        
	        for (Room room : allRooms) {
	            if (room.getHouseholdId() != null && room.getHouseholdId() == householdId) {
	                return room.getRoomNumber();
	            }
	        }
	        
	        return null; // Không tìm thấy phòng nào
	        
	    } catch (Exception e) {
	        throw new ServiceException("Lỗi khi lấy thông tin số phòng: " + e.getMessage(), e);
	    }
	}

	@Override
	public List<Room> getAllRooms() throws ServiceException, SQLException {
		return roomDAO.findAll();
	}

	@Override
	public boolean isRoomAvailable(String roomNumber) throws ServiceException, SQLException {
		return roomDAO.isRoomAvailable(roomNumber);
	}

	@Override
	public void occupyRoom(String roomNumber, int householdId) throws ServiceException, SQLException {
		roomDAO.occupyRoom(roomNumber, householdId);
	}

	@Override
	public void vacateRoom(String roomNumber) throws ServiceException, SQLException {
		roomDAO.vacateRoom(roomNumber);
	}
}
