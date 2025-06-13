package dao.households;

import dao.PostgreSQLConnection;
import models.Household;
import models.Resident;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HouseholdDAOPostgreSQL implements HouseholdDAO {
    private Connection conn;

    public HouseholdDAOPostgreSQL() throws SQLException {
        this.conn = PostgreSQLConnection.getInstance().getConnection();
    }

    @Override
    public List<Household> getAllHouseholds() {
        List<Household> households = new ArrayList<>();
        String sql = "SELECT h.*, r.full_name as head_name, " +
                    "COALESCE((SELECT COUNT(*) FROM residents res WHERE res.household_id = h.household_id), 0) as actual_residents_count " +
                    "FROM households h " +
                    "LEFT JOIN residents r ON h.head_resident_id = r.resident_id " +
                    "ORDER BY h.household_id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Household household = mapResultSetToHousehold(rs);
                households.add(household);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return households;
    }

    @Override
    public Household getHouseholdById(int householdId) {
        String sql = "SELECT h.*, r.full_name as head_name, " +
                    "COALESCE((SELECT COUNT(*) FROM residents res WHERE res.household_id = h.household_id), 0) as actual_residents_count " +
                    "FROM households h " +
                    "LEFT JOIN residents r ON h.head_resident_id = r.resident_id " +
                    "WHERE h.household_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Household household = mapResultSetToHousehold(rs);
                // Lấy danh sách cư dân
                List<Resident> residents = getResidentsByHouseholdId(householdId);
                household.setResidents(residents);
                return household;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public List<Resident> getResidentsByHouseholdId(int householdId) {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE household_id = ? ORDER BY resident_id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Resident resident = mapResultSetToResident(rs);
                residents.add(resident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return residents;
    }

    @Override
    public List<Household> searchHouseholds(String keyword) {
        List<Household> households = new ArrayList<>();
        String sql = "SELECT h.*, r.full_name as head_name, " +
                    "COALESCE((SELECT COUNT(*) FROM residents res WHERE res.household_id = h.household_id), 0) as actual_residents_count " +
                    "FROM households h " +
                    "LEFT JOIN residents r ON h.head_resident_id = r.resident_id " +
                    "WHERE LOWER(h.house_number) LIKE LOWER(?) " +
                    "OR LOWER(h.district) LIKE LOWER(?) " +
                    "OR LOWER(h.ward) LIKE LOWER(?) " +
                    "OR LOWER(h.street) LIKE LOWER(?) " +
                    "OR LOWER(r.full_name) LIKE LOWER(?) " +
                    "ORDER BY h.household_id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Household household = mapResultSetToHousehold(rs);
                households.add(household);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return households;
    }

    @Override
    public boolean addHousehold(Household household) {
        String sql = "INSERT INTO households (house_number, district, ward, street, registration_date, head_resident_id, areas) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, household.getHouseNumber());
            stmt.setString(2, household.getDistrict());
            stmt.setString(3, household.getWard());
            stmt.setString(4, household.getStreet());
            stmt.setDate(5, household.getRegistrationDate() != null ? Date.valueOf(household.getRegistrationDate()) : null);
            
            // Xử lý head_resident_id: nếu là 0 hoặc âm thì set NULL
            if (household.getHeadResidentId() > 0) {
                stmt.setInt(6, household.getHeadResidentId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setInt(7, household.getAreas());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean updateHousehold(Household household) {
        String sql = "UPDATE households SET house_number = ?, district = ?, ward = ?, street = ?, " +
                    "registration_date = ?, head_resident_id = ?, areas = ? " +
                    "WHERE household_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, household.getHouseNumber());
            stmt.setString(2, household.getDistrict());
            stmt.setString(3, household.getWard());
            stmt.setString(4, household.getStreet());
            stmt.setDate(5, household.getRegistrationDate() != null ? Date.valueOf(household.getRegistrationDate()) : null);
            
            // Xử lý head_resident_id: nếu là 0 hoặc âm thì set NULL
            if (household.getHeadResidentId() > 0) {
                stmt.setInt(6, household.getHeadResidentId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setInt(7, household.getAreas());
            stmt.setInt(8, household.getHouseholdId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean deleteHousehold(int householdId) {
        String sql = "DELETE FROM households WHERE household_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private Household mapResultSetToHousehold(ResultSet rs) throws SQLException {
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
        
        // Sử dụng số nhân khẩu tính động thay vì lấy từ trường cũ
        int actualResidentsCount = rs.getInt("actual_residents_count");
        household.setNumberOfResidents(actualResidentsCount);
        
        Integer headResidentId = rs.getObject("head_resident_id", Integer.class);
        if (headResidentId != null) {
            household.setHeadResidentId(headResidentId);
        }
        
        // Lấy diện tích từ database
        int areas = rs.getInt("areas");
        household.setAreas(areas);
        
        // Debug log để kiểm tra
        System.out.println("DEBUG: Household ID " + household.getHouseholdId() + " - Areas: " + areas + ", Residents count: " + actualResidentsCount);
        
        return household;
    }

    private Resident mapResultSetToResident(ResultSet rs) throws SQLException {
        Resident resident = new Resident();
        resident.setResidentId(rs.getInt("resident_id"));
        resident.setFullName(rs.getString("full_name"));
        
        Date birthDate = rs.getDate("date_of_birth");
        if (birthDate != null) {
            resident.setDateOfBirth(birthDate.toLocalDate());
        }
        
        resident.setGender(rs.getString("gender"));
        resident.setEthnicity(rs.getString("ethnicity"));
        resident.setReligion(rs.getString("religion"));
        resident.setCitizenId(rs.getString("citizen_id"));
        
        Date issueDate = rs.getDate("date_of_issue");
        if (issueDate != null) {
            resident.setDateOfIssue(issueDate.toLocalDate());
        }
        
        resident.setPlaceOfIssue(rs.getString("place_of_issue"));
        resident.setOccupation(rs.getString("occupation"));
        resident.setNotes(rs.getString("notes"));
        
        Date addedDate = rs.getDate("added_date");
        if (addedDate != null) {
            resident.setAddedDate(addedDate.toLocalDate());
        }
        
        resident.setRelationshipWithHead(rs.getString("relationship_with_head"));
        resident.setHouseholdId(rs.getInt("household_id"));
        
        return resident;
    }
    
    @Override
    public boolean updateResidentsCount(int householdId) {
        String sql = "UPDATE households SET number_of_residents = " +
                    "(SELECT COUNT(*) FROM residents WHERE household_id = ?) " +
                    "WHERE household_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            stmt.setInt(2, householdId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
} 