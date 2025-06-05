package dao.room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.DAO;
import models.Room;
import utils.DatabaseConnection;

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
                rooms.add(extractRoomFromResultSet(rs));
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
                return extractRoomFromResultSet(rs);
            } else {
                throw new ServiceException("Room with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in findById(): " + e.getMessage());
            throw new ServiceException("Database error when finding room: " + id);
        }
    }

    public Room findByRoomNumber(String roomNumber) throws ServiceException {
        String sql = "SELECT * FROM room WHERE room_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractRoomFromResultSet(rs);
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

    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setOccupied(rs.getBoolean("is_occupied"));
        
        int householdId = rs.getInt("household_id");
        if (!rs.wasNull()) {
            room.setHouseholdId(householdId);
        }
        
        return room;
    }
} 