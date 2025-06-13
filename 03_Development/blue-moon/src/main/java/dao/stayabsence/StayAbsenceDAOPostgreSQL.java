package dao.stayabsence;

import dao.PostgreSQLConnection;
import models.StayAbsenceRecord;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StayAbsenceDAOPostgreSQL implements StayAbsenceDAO {
    private Connection conn;
    
    public StayAbsenceDAOPostgreSQL() throws SQLException {
        this.conn = PostgreSQLConnection.getInstance().getConnection();
    }
    
    @Override
    public boolean addStayAbsenceRecord(StayAbsenceRecord record) throws Exception {
        String sql = "INSERT INTO stay_absence_records (" +
                    "record_type, created_date, temp_address, period, request_desc, " +
                    "resident_id, household_id, temp_resident_name, temp_resident_cccd, " +
                    "temp_resident_birth_date, temp_resident_gender, temp_resident_phone, " +
                    "temp_resident_hometown, start_date, end_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING record_id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, record.getRecordType());
            stmt.setDate(2, Date.valueOf(record.getCreatedDate()));
            stmt.setString(3, record.getTempAddress());
            stmt.setString(4, record.getPeriod());
            stmt.setString(5, record.getRequestDesc());
            
            // Resident ID (for temporary absence)
            if (record.getResidentId() != null) {
                stmt.setInt(6, record.getResidentId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            // Household ID
            if (record.getHouseholdId() != null) {
                stmt.setInt(7, record.getHouseholdId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            // Temporary resident info (for temporary stay)
            stmt.setString(8, record.getTempResidentName());
            stmt.setString(9, record.getTempResidentCccd());
            
            if (record.getTempResidentBirthDate() != null) {
                stmt.setDate(10, Date.valueOf(record.getTempResidentBirthDate()));
            } else {
                stmt.setNull(10, Types.DATE);
            }
            
            stmt.setString(11, record.getTempResidentGender());
            stmt.setString(12, record.getTempResidentPhone());
            stmt.setString(13, record.getTempResidentHometown());
            
            // Validity period
            if (record.getStartDate() != null) {
                stmt.setDate(14, Date.valueOf(record.getStartDate()));
            } else {
                stmt.setNull(14, Types.DATE);
            }
            
            if (record.getEndDate() != null) {
                stmt.setDate(15, Date.valueOf(record.getEndDate()));
            } else {
                stmt.setNull(15, Types.DATE);
            }
            
            stmt.setString(16, record.getStatus());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    record.setRecordId(rs.getInt("record_id"));
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public boolean updateStayAbsenceRecord(StayAbsenceRecord record) throws Exception {
        String sql = "UPDATE stay_absence_records SET " +
                    "record_type = ?, temp_address = ?, period = ?, request_desc = ?, " +
                    "resident_id = ?, household_id = ?, temp_resident_name = ?, temp_resident_cccd = ?, " +
                    "temp_resident_birth_date = ?, temp_resident_gender = ?, temp_resident_phone = ?, " +
                    "temp_resident_hometown = ?, start_date = ?, end_date = ?, status = ? " +
                    "WHERE record_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, record.getRecordType());
            stmt.setString(2, record.getTempAddress());
            stmt.setString(3, record.getPeriod());
            stmt.setString(4, record.getRequestDesc());
            
            if (record.getResidentId() != null) {
                stmt.setInt(5, record.getResidentId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (record.getHouseholdId() != null) {
                stmt.setInt(6, record.getHouseholdId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setString(7, record.getTempResidentName());
            stmt.setString(8, record.getTempResidentCccd());
            
            if (record.getTempResidentBirthDate() != null) {
                stmt.setDate(9, Date.valueOf(record.getTempResidentBirthDate()));
            } else {
                stmt.setNull(9, Types.DATE);
            }
            
            stmt.setString(10, record.getTempResidentGender());
            stmt.setString(11, record.getTempResidentPhone());
            stmt.setString(12, record.getTempResidentHometown());
            
            if (record.getStartDate() != null) {
                stmt.setDate(13, Date.valueOf(record.getStartDate()));
            } else {
                stmt.setNull(13, Types.DATE);
            }
            
            if (record.getEndDate() != null) {
                stmt.setDate(14, Date.valueOf(record.getEndDate()));
            } else {
                stmt.setNull(14, Types.DATE);
            }
            
            stmt.setString(15, record.getStatus());
            stmt.setInt(16, record.getRecordId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean deleteStayAbsenceRecord(int recordId) throws Exception {
        String sql = "DELETE FROM stay_absence_records WHERE record_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public StayAbsenceRecord getStayAbsenceRecordById(int recordId) throws Exception {
        String sql = "SELECT s.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM stay_absence_records s " +
                    "LEFT JOIN residents r ON s.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON s.household_id = h.household_id " +
                    "WHERE s.record_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRecord(rs);
                }
            }
        }
        
        return null;
    }
    
    @Override
    public List<StayAbsenceRecord> getAllStayAbsenceRecords() throws Exception {
        String sql = "SELECT s.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM stay_absence_records s " +
                    "LEFT JOIN residents r ON s.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON s.household_id = h.household_id " +
                    "ORDER BY s.created_date DESC, s.record_id DESC";
        
        List<StayAbsenceRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                records.add(mapResultSetToRecord(rs));
            }
        }
        
        return records;
    }
    
    @Override
    public List<StayAbsenceRecord> getStayAbsenceRecordsByType(String recordType) throws Exception {
        String sql = "SELECT s.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM stay_absence_records s " +
                    "LEFT JOIN residents r ON s.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON s.household_id = h.household_id " +
                    "WHERE s.record_type = ? " +
                    "ORDER BY s.created_date DESC, s.record_id DESC";
        
        List<StayAbsenceRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recordType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        
        return records;
    }
    
    @Override
    public List<StayAbsenceRecord> getStayAbsenceRecordsByHouseholdId(int householdId) throws Exception {
        String sql = "SELECT s.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM stay_absence_records s " +
                    "LEFT JOIN residents r ON s.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON s.household_id = h.household_id " +
                    "WHERE s.household_id = ? " +
                    "ORDER BY s.created_date DESC, s.record_id DESC";
        
        List<StayAbsenceRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        
        return records;
    }
    
    @Override
    public List<StayAbsenceRecord> getStayAbsenceRecordsByResidentId(int residentId) throws Exception {
        String sql = "SELECT s.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM stay_absence_records s " +
                    "LEFT JOIN residents r ON s.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON s.household_id = h.household_id " +
                    "WHERE s.resident_id = ? " +
                    "ORDER BY s.created_date DESC, s.record_id DESC";
        
        List<StayAbsenceRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        
        return records;
    }
    
    @Override
    public List<StayAbsenceRecord> getActiveStayAbsenceRecords() throws Exception {
        String sql = "SELECT s.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM stay_absence_records s " +
                    "LEFT JOIN residents r ON s.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON s.household_id = h.household_id " +
                    "WHERE s.status = 'ACTIVE' AND (s.end_date IS NULL OR s.end_date >= CURRENT_DATE) " +
                    "ORDER BY s.created_date DESC, s.record_id DESC";
        
        List<StayAbsenceRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                records.add(mapResultSetToRecord(rs));
            }
        }
        
        return records;
    }
    
    @Override
    public List<StayAbsenceRecord> searchStayAbsenceRecords(String keyword) throws Exception {
        String sql = "SELECT s.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM stay_absence_records s " +
                    "LEFT JOIN residents r ON s.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON s.household_id = h.household_id " +
                    "WHERE LOWER(s.temp_resident_name) LIKE LOWER(?) OR " +
                    "LOWER(s.temp_resident_cccd) LIKE LOWER(?) OR " +
                    "LOWER(r.full_name) LIKE LOWER(?) OR " +
                    "LOWER(h.house_number) LIKE LOWER(?) OR " +
                    "LOWER(s.request_desc) LIKE LOWER(?) " +
                    "ORDER BY s.created_date DESC, s.record_id DESC";
        
        List<StayAbsenceRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
        }
        
        return records;
    }
    
    @Override
    public boolean updateRecordStatus(int recordId, String status) throws Exception {
        String sql = "UPDATE stay_absence_records SET status = ? WHERE record_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, recordId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private StayAbsenceRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        StayAbsenceRecord record = new StayAbsenceRecord();
        
        record.setRecordId(rs.getInt("record_id"));
        record.setRecordType(rs.getString("record_type"));
        
        Date createdDate = rs.getDate("created_date");
        if (createdDate != null) {
            record.setCreatedDate(createdDate.toLocalDate());
        }
        
        record.setTempAddress(rs.getString("temp_address"));
        record.setPeriod(rs.getString("period"));
        record.setRequestDesc(rs.getString("request_desc"));
        
        // Resident info
        if (rs.getObject("resident_id") != null) {
            record.setResidentId(rs.getInt("resident_id"));
        }
        record.setResidentName(rs.getString("resident_name"));
        
        // Household info
        if (rs.getObject("household_id") != null) {
            record.setHouseholdId(rs.getInt("household_id"));
        }
        record.setHouseholdName(rs.getString("household_name"));
        
        // Temporary resident info
        record.setTempResidentName(rs.getString("temp_resident_name"));
        record.setTempResidentCccd(rs.getString("temp_resident_cccd"));
        
        Date tempBirthDate = rs.getDate("temp_resident_birth_date");
        if (tempBirthDate != null) {
            record.setTempResidentBirthDate(tempBirthDate.toLocalDate());
        }
        
        record.setTempResidentGender(rs.getString("temp_resident_gender"));
        record.setTempResidentPhone(rs.getString("temp_resident_phone"));
        record.setTempResidentHometown(rs.getString("temp_resident_hometown"));
        
        // Validity period
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            record.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            record.setEndDate(endDate.toLocalDate());
        }
        
        record.setStatus(rs.getString("status"));
        
        return record;
    }
} 