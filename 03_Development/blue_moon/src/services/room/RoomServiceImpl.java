package services.room;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dao.room.RoomDAO;
import exception.ServiceException;
import models.Room;

public class RoomServiceImpl implements RoomService {
	private final RoomDAO roomDAO = new RoomDAO();

	@Override
	public List<Room> getAllRooms() throws ServiceException, SQLException {
		return roomDAO.findAll();
	}

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
	    	Room room = roomDAO.findByHouseholdId(householdId);
	        return room.getRoomNumber();
	    } catch (Exception e) {
	        throw new ServiceException("Lỗi khi lấy thông tin số phòng: " + e.getMessage(), e);
	    }
	}

	@Override
	public List<Room> getAvailableRooms() {
		try {
			List<Room> allRooms = getAllRooms();
			return allRooms.stream()
				.filter(room -> !room.isOccupied())
				.collect(Collectors.toList());
		} catch (Exception e) {
			System.err.println("Error getting available rooms: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	@Override
	public void occupyRoom(String roomNumber, int householdId) throws ServiceException, SQLException {
		roomDAO.occupyRoom(roomNumber, householdId);
	}

	@Override
	public void vacateRoom(String roomNumber) throws ServiceException, SQLException {
		roomDAO.vacateRoom(roomNumber);
	}

	@Override
	public boolean isRoomAvailable(String roomNumber) throws ServiceException, SQLException {
		return roomDAO.isRoomAvailable(roomNumber);
	}

	@Override
	public Float getAreaByRoomNumber(String roomNumber) throws ServiceException {
		return roomDAO.getAreaByRoomNumber(roomNumber);
	}

	@Override
	public void add(Room room) throws ServiceException {
		roomDAO.add(room);
	}

	@Override
	public void update(Room room) throws ServiceException {
		roomDAO.update(room);
	}

	public boolean roomExists(String roomNumber) throws ServiceException {
		try {
			Room room = roomDAO.findByRoomNumber(roomNumber);
			return room != null;
		} catch (ServiceException e) {
			// If room is not found, return false
			return false;
		}
	}
}
