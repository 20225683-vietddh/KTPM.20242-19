package dao.resident;
import dao.PostgreSQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Resident;

public class ResidentDAO {
    private final Connection conn;

    public ResidentDAO() throws SQLException {
        this.conn = PostgreSQLConnection.getInstance().getConnection();
    }

    // Lấy danh sách nhân khẩu
    public List<Resident> getAllResidents() {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                residents.add(mapResultSetToResident(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return residents;
    }

    // Thêm nhân khẩu mới
    public void addResident(Resident resident) throws SQLException {
        String sql = "INSERT INTO residents (full_name, date_of_birth, gender, ethnicity, religion, citizen_id, date_of_issue, place_of_issue, occupation, notes, added_date, relationship_with_head, household_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setResidentData(stmt, resident);
            stmt.executeUpdate();
        }
    }

    // Cập nhật thông tin nhân khẩu
    public void updateResident(Resident resident) throws SQLException {
        String sql = "UPDATE residents SET full_name = ?, date_of_birth = ?, gender = ?, ethnicity = ?, religion = ?, citizen_id = ?, date_of_issue = ?, place_of_issue = ?, occupation = ?, notes = ?, added_date = ?, relationship_with_head = ?, household_id = ? " +
                     "WHERE resident_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setResidentData(stmt, resident);
            stmt.setInt(14, resident.getId()); // Thêm ID vào vị trí cuối
            stmt.executeUpdate();
        }
    }

    // Xóa nhân khẩu
    public void deleteResident(int residentId) throws SQLException {
        String sql = "DELETE FROM residents WHERE resident_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            stmt.executeUpdate();
        }
    }

    // Lấy thông tin của một nhân khẩu theo ID
    public Resident getResidentById(int residentId) {
        String sql = "SELECT * FROM residents WHERE resident_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToResident(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Phương thức hỗ trợ: Ánh xạ dữ liệu từ ResultSet
    private Resident mapResultSetToResident(ResultSet rs) throws SQLException {
        return new Resident(
            rs.getInt("resident_id"),
            rs.getString("full_name"),
            rs.getDate("date_of_birth").toLocalDate(),
            rs.getString("gender"),
            rs.getString("ethnicity"),
            rs.getString("religion"),
            rs.getString("citizen_id"),
            rs.getDate("date_of_issue").toLocalDate(),
            rs.getString("place_of_issue"),
            rs.getString("occupation"),
            rs.getString("notes"),
            rs.getDate("added_date").toLocalDate(),
            rs.getString("relationship_with_head"),
            rs.getInt("household_id")
        );
    }

    // Phương thức hỗ trợ: Gán dữ liệu từ Resident vào PreparedStatement
    private void setResidentData(PreparedStatement stmt, Resident resident) throws SQLException {
        stmt.setString(1, resident.getFullName());
        stmt.setObject(2, resident.getDateOfBirth());
        stmt.setString(3, resident.getGender());
        stmt.setString(4, resident.getEthnicity());
        stmt.setString(5, resident.getReligion());
        stmt.setString(6, resident.getCitizenId());
        stmt.setObject(7, resident.getDateOfIssue());
        stmt.setString(8, resident.getPlaceOfIssue());
        stmt.setString(9, resident.getOccupation());
        stmt.setString(10, resident.getNotes());
        stmt.setObject(11, resident.getAddedDate());
        stmt.setString(12, resident.getRelationshipWithHead());
        stmt.setInt(13, resident.getHouseholdId());
    }
}