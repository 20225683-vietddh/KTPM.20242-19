package dao.residents;

import models.Resident;
import dao.PostgreSQLConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResidentDAOPostgreSQL implements ResidentDAO {
    private Connection conn;

    public ResidentDAOPostgreSQL() throws SQLException {
        this.conn = PostgreSQLConnection.getInstance().getConnection();
    }
    
    @Override
    public List<Resident> getAllResidents() throws Exception {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents ORDER BY added_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                residents.add(mapResultSetToResident(rs));
            }
        }
        
        return residents;
    }
    
    @Override
    public List<Resident> getResidentsByHouseholdId(int householdId) throws Exception {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE household_id = ? ORDER BY added_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, householdId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    residents.add(mapResultSetToResident(rs));
                }
            }
        }
        
        return residents;
    }
    
    @Override
    public List<Resident> searchResidents(String searchTerm) throws Exception {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE " +
                    "LOWER(full_name) LIKE LOWER(?) OR " +
                    "citizen_id LIKE ? OR " +
                    "LOWER(occupation) LIKE LOWER(?) " +
                    "ORDER BY added_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    residents.add(mapResultSetToResident(rs));
                }
            }
        }
        
        return residents;
    }
    
    @Override
    public Resident getResidentById(int residentId) throws Exception {
        String sql = "SELECT * FROM residents WHERE resident_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToResident(rs);
                }
            }
        }
        
        return null;
    }
    
    @Override
    public boolean addResident(Resident resident) throws Exception {
        String sql = "INSERT INTO residents (full_name, date_of_birth, gender, ethnicity, religion, " +
                    "citizen_id, date_of_issue, place_of_issue, occupation, notes, " +
                    "relationship_with_head, household_id, added_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resident.getFullName());
            stmt.setDate(2, Date.valueOf(resident.getDateOfBirth()));
            stmt.setString(3, resident.getGender());
            stmt.setString(4, resident.getEthnicity());
            stmt.setString(5, resident.getReligion());
            stmt.setString(6, resident.getCitizenId());
            stmt.setDate(7, resident.getDateOfIssue() != null ? Date.valueOf(resident.getDateOfIssue()) : null);
            stmt.setString(8, resident.getPlaceOfIssue());
            stmt.setString(9, resident.getOccupation());
            stmt.setString(10, resident.getNotes());
            stmt.setString(11, resident.getRelationshipWithHead());
            stmt.setInt(12, resident.getHouseholdId());
            stmt.setDate(13, Date.valueOf(LocalDate.now()));
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean updateResident(Resident resident) throws Exception {
        String sql = "UPDATE residents SET full_name = ?, date_of_birth = ?, gender = ?, " +
                    "ethnicity = ?, religion = ?, citizen_id = ?, date_of_issue = ?, " +
                    "place_of_issue = ?, occupation = ?, notes = ?, " +
                    "relationship_with_head = ?, household_id = ? " +
                    "WHERE resident_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resident.getFullName());
            stmt.setDate(2, Date.valueOf(resident.getDateOfBirth()));
            stmt.setString(3, resident.getGender());
            stmt.setString(4, resident.getEthnicity());
            stmt.setString(5, resident.getReligion());
            stmt.setString(6, resident.getCitizenId());
            stmt.setDate(7, resident.getDateOfIssue() != null ? Date.valueOf(resident.getDateOfIssue()) : null);
            stmt.setString(8, resident.getPlaceOfIssue());
            stmt.setString(9, resident.getOccupation());
            stmt.setString(10, resident.getNotes());
            stmt.setString(11, resident.getRelationshipWithHead());
            stmt.setInt(12, resident.getHouseholdId());
            stmt.setInt(13, resident.getResidentId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean deleteResident(int residentId) throws Exception {
        String sql = "DELETE FROM residents WHERE resident_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public int getResidentCountByHouseholdId(int householdId) throws Exception {
        String sql = "SELECT COUNT(*) FROM residents WHERE household_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    private Resident mapResultSetToResident(ResultSet rs) throws SQLException {
        Resident resident = new Resident();
        resident.setResidentId(rs.getInt("resident_id"));
        resident.setFullName(rs.getString("full_name"));
        
        Date dateOfBirth = rs.getDate("date_of_birth");
        if (dateOfBirth != null) {
            resident.setDateOfBirth(dateOfBirth.toLocalDate());
        }
        
        resident.setGender(rs.getString("gender"));
        resident.setEthnicity(rs.getString("ethnicity"));
        resident.setReligion(rs.getString("religion"));
        resident.setCitizenId(rs.getString("citizen_id"));
        
        Date dateOfIssue = rs.getDate("date_of_issue");
        if (dateOfIssue != null) {
            resident.setDateOfIssue(dateOfIssue.toLocalDate());
        }
        
        resident.setPlaceOfIssue(rs.getString("place_of_issue"));
        resident.setOccupation(rs.getString("occupation"));
        resident.setNotes(rs.getString("notes"));
        resident.setRelationshipWithHead(rs.getString("relationship_with_head"));
        resident.setHouseholdId(rs.getInt("household_id"));
        
        Date addedDate = rs.getDate("added_date");
        if (addedDate != null) {
            resident.setAddedDate(addedDate.toLocalDate());
        }
        
        return resident;
    }
} 