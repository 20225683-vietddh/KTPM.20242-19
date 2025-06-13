package dao.rooms;

import dao.PostgreSQLConnection;
import models.Room;
import models.Household;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomDAOPostgreSQL implements RoomDAO {
    private Connection conn;

    public RoomDAOPostgreSQL() throws SQLException {
        this.conn = PostgreSQLConnection.getInstance().getConnection();
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_number, r.is_empty, " +
                    "h.household_id, h.house_number, h.district, h.ward, h.street, " +
                    "h.registration_date, h.head_resident_id, h.areas, " +
                    "res.full_name as head_name, " +
                    "COALESCE((SELECT COUNT(*) FROM residents res2 WHERE res2.household_id = h.household_id), 0) as residents_count " +
                    "FROM rooms r " +
                    "LEFT JOIN households h ON r.room_number = h.house_number " +
                    "LEFT JOIN residents res ON h.head_resident_id = res.resident_id " +
                    "ORDER BY r.room_number";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }

    @Override
    public List<Room> searchRooms(String keyword) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_number, r.is_empty, " +
                    "h.household_id, h.house_number, h.district, h.ward, h.street, " +
                    "h.registration_date, h.head_resident_id, h.areas, " +
                    "res.full_name as head_name, " +
                    "COALESCE((SELECT COUNT(*) FROM residents res2 WHERE res2.household_id = h.household_id), 0) as residents_count " +
                    "FROM rooms r " +
                    "LEFT JOIN households h ON r.room_number = h.house_number " +
                    "LEFT JOIN residents res ON h.head_resident_id = res.resident_id " +
                    "WHERE LOWER(r.room_number) LIKE LOWER(?) " +
                    "OR LOWER(h.district) LIKE LOWER(?) " +
                    "OR LOWER(h.ward) LIKE LOWER(?) " +
                    "OR LOWER(res.full_name) LIKE LOWER(?) " +
                    "ORDER BY r.room_number";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }

    @Override
    public Room getRoomByNumber(String roomNumber) {
        String sql = "SELECT r.room_number, r.is_empty, " +
                    "h.household_id, h.house_number, h.district, h.ward, h.street, " +
                    "h.registration_date, h.head_resident_id, h.areas, " +
                    "res.full_name as head_name, " +
                    "COALESCE((SELECT COUNT(*) FROM residents res2 WHERE res2.household_id = h.household_id), 0) as residents_count " +
                    "FROM rooms r " +
                    "LEFT JOIN households h ON r.room_number = h.house_number " +
                    "LEFT JOIN residents res ON h.head_resident_id = res.resident_id " +
                    "WHERE r.room_number = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public List<Room> getEmptyRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_number, is_empty FROM rooms WHERE is_empty = TRUE ORDER BY room_number";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Room room = new Room();
                room.setRoomNumber(rs.getString("room_number"));
                room.setEmpty(rs.getBoolean("is_empty"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }

    @Override
    public List<Room> getOccupiedRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_number, r.is_empty, " +
                    "h.household_id, h.house_number, h.district, h.ward, h.street, " +
                    "h.registration_date, h.head_resident_id, h.areas, " +
                    "res.full_name as head_name, " +
                    "COALESCE((SELECT COUNT(*) FROM residents res2 WHERE res2.household_id = h.household_id), 0) as residents_count " +
                    "FROM rooms r " +
                    "INNER JOIN households h ON r.room_number = h.house_number " +
                    "LEFT JOIN residents res ON h.head_resident_id = res.resident_id " +
                    "WHERE r.is_empty = FALSE " +
                    "ORDER BY r.room_number";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rooms;
    }

    @Override
    public boolean updateRoomStatus(String roomNumber, boolean isEmpty) {
        String sql = "UPDATE rooms SET is_empty = ? WHERE room_number = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isEmpty);
            stmt.setString(2, roomNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, is_empty) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setBoolean(2, room.isEmpty());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean deleteRoom(String roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomNumber(rs.getString("room_number"));
        room.setEmpty(rs.getBoolean("is_empty"));
        
        // Nếu phòng có hộ khẩu sinh sống
        if (!room.isEmpty() && rs.getObject("household_id") != null) {
            Household household = new Household();
            household.setHouseholdId(rs.getInt("household_id"));
            household.setHouseNumber(rs.getString("house_number"));
            household.setDistrict(rs.getString("district"));
            household.setWard(rs.getString("ward"));
            household.setStreet(rs.getString("street"));
            
            Date regDate = rs.getDate("registration_date");
            if (regDate != null) {
                household.setRegistrationDate(regDate.toLocalDate());
            }
            
            Integer headResidentId = rs.getObject("head_resident_id", Integer.class);
            if (headResidentId != null) {
                household.setHeadResidentId(headResidentId);
            }
            
            household.setAreas(rs.getInt("areas"));
            household.setNumberOfResidents(rs.getInt("residents_count"));
            
            room.setHousehold(household);
        }
        
        return room;
    }
} 