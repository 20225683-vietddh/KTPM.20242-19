package dao.room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exception.ServiceException;
import models.Room;
import utils.DatabaseConnection;
import utils.Utils;

public class RoomDAO {
	private final Connection conn;

    public RoomDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY room_number";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	Room room = Utils.mapResultSetToRoom(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error in findAll(): " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    public Room findById(int id) throws ServiceException {
        String sql = "SELECT * FROM room WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Utils.mapResultSetToRoom(rs);
            } else {
                throw new ServiceException("Room with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in findById(): " + e.getMessage());
            throw new ServiceException("Database error when finding room: " + id);
        }
    }
    
    public Room findByHouseholdId(int hhid) throws ServiceException {
        String sql = "SELECT * FROM room WHERE household_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hhid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Utils.mapResultSetToRoom(rs);
            } else {
                throw new ServiceException("Room with Household ID " + hhid + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in findByHouseholdId(): " + e.getMessage());
            throw new ServiceException("Database error when finding room: " + hhid);
        }
    }

    public Room findByRoomNumber(String roomNumber) throws ServiceException {
        String sql = "SELECT * FROM room WHERE room_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Utils.mapResultSetToRoom(rs);
            } else {
                throw new ServiceException("Room with number " + roomNumber + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in findByRoomNumber(): " + e.getMessage());
            throw new ServiceException("Database error when finding room with number: " + roomNumber);
        }
    }

    public boolean isRoomAvailable(String roomNumber) throws ServiceException {
        Room room = findByRoomNumber(roomNumber);
        return !room.isOccupied();
    }

    public void occupyRoom(String roomNumber, int householdId) throws ServiceException {
        String sql = "UPDATE room SET is_occupied = true, household_id = ? WHERE room_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            stmt.setString(2, roomNumber);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ServiceException("Room with number " + roomNumber + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in occupyRoom(): " + e.getMessage());
            throw new ServiceException("Database error when occupying room: " + roomNumber);
        }
    }

    public void vacateRoom(String roomNumber) throws ServiceException {
        String sql = "UPDATE room SET is_occupied = false, household_id = NULL WHERE room_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ServiceException("Room with number " + roomNumber + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in vacateRoom(): " + e.getMessage());
            throw new ServiceException("Database error when vacating room: " + roomNumber);
        }
    }

    public Float getAreaByRoomNumber(String roomNumber) throws ServiceException {
        Room room = findByRoomNumber(roomNumber);
        return room.getArea();
    }

    public void add(Room room) throws ServiceException {
        String sql = "INSERT INTO room (room_number, area, is_occupied, household_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Utils.setRoomData(stmt, room);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in add(): " + e.getMessage());
            throw new ServiceException("Error adding room: " + e.getMessage());
        }
    }

    public void update(Room room) throws ServiceException {
        String sql = "UPDATE room SET room_number = ?, area = ?, is_occupied = ?, household_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Utils.setRoomData(stmt, room);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ServiceException("Room with ID " + room.getId() + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error in update(): " + e.getMessage());
            throw new ServiceException("Error updating room: " + e.getMessage());
        }
    }
} 