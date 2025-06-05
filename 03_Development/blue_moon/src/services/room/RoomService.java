package services.room;

import java.sql.SQLException;
import java.util.List;

import exception.ServiceException;
import models.Room;

public interface RoomService {


	boolean isRoomAvailable(String roomNumber) throws ServiceException, SQLException;

	void occupyRoom(String value, int id) throws ServiceException, SQLException;

	Room getRoomById(int id) throws ServiceException, SQLException;

	Room getRoomByNumber(String roomNumber) throws ServiceException, SQLException;

	List<Room> getAllRooms() throws ServiceException, SQLException;

	void vacateRoom(String roomNumber) throws ServiceException, SQLException;

}
