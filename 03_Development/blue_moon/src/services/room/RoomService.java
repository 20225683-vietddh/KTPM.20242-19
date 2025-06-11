package services.room;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Room;

public interface RoomService {


	Room getRoomById(int id) throws ServiceException, SQLException;

	Room getRoomByNumber(String roomNumber) throws ServiceException, SQLException;

	List<Room> getAllRooms() throws ServiceException, SQLException;

	List<Room> getAvailableRooms();

	Float getAreaByRoomNumber(String roomNumber) throws ServiceException;

	void occupyRoom(String roomNumber, int householdId) throws ServiceException, SQLException;

	void vacateRoom(String roomNumber) throws ServiceException, SQLException;

	void add(Room room) throws ServiceException;

	void update(Room room) throws ServiceException;

	boolean isRoomAvailable(String roomNumber) throws ServiceException, SQLException;

	boolean roomExists(String roomNumber) throws ServiceException;

}
