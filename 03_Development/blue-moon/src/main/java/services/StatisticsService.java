package services;

import models.Resident;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import java.time.Period;
import dao.PostgreSQLConnection;

public class StatisticsService {
    private Connection conn;
    
    public StatisticsService() throws SQLException {
        this.conn = PostgreSQLConnection.getInstance().getConnection();
    }
    
    // Thống kê theo độ tuổi
    public Map<String, Integer> getAgeStatistics() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("0-18", 0);
        stats.put("19-60", 0);
        stats.put(">60", 0);
        
        String sql = "SELECT date_of_birth FROM residents";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                java.sql.Date dob = rs.getDate("date_of_birth");
                if (dob != null) {
                    int age = Period.between(dob.toLocalDate(), LocalDate.now()).getYears();
                    if (age <= 18) stats.put("0-18", stats.get("0-18") + 1);
                    else if (age <= 60) stats.put("19-60", stats.get("19-60") + 1);
                    else stats.put(">60", stats.get(">60") + 1);
                }
            }
        }
        return stats;
    }
    
    // Thống kê theo giới tính
    public Map<String, Integer> getGenderStatistics() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Nam", 0);
        stats.put("Nữ", 0);
        
        String sql = "SELECT gender FROM residents";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String gender = rs.getString("gender");
                if (gender != null) {
                    stats.put(gender, stats.get(gender) + 1);
                }
            }
        }
        return stats;
    }
    
    // Thống kê theo tình trạng cư trú
    public Map<String, Integer> getResidenceStatusStatistics() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Thường trú", 0);
        stats.put("Tạm trú", 0);
        stats.put("Tạm vắng", 0);
        
        String sql = "SELECT r.resident_id, " +
                    "CASE " +
                    "  WHEN sar.record_id IS NOT NULL AND sar.status = 'ACTIVE' AND sar.record_type = 'TEMPORARY_ABSENCE' THEN 'Tạm vắng' " +
                    "  WHEN sar.record_id IS NOT NULL AND sar.status = 'ACTIVE' AND sar.record_type = 'TEMPORARY_STAY' THEN 'Tạm trú' " +
                    "  ELSE 'Thường trú' " +
                    "END as residence_status " +
                    "FROM residents r " +
                    "LEFT JOIN stay_absence_records sar ON r.resident_id = sar.resident_id";
                    
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String status = rs.getString("residence_status");
                stats.put(status, stats.get(status) + 1);
            }
        }
        return stats;
    }
    
    // Thống kê theo năm sinh
    public Map<String, Integer> getBirthYearStatistics() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        // Khởi tạo các thập kỷ từ 1940 đến 2010
        for (int year = 1940; year <= 2010; year += 10) {
            stats.put(year + "-" + (year + 9), 0);
        }
        
        String sql = "SELECT date_of_birth FROM residents";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                java.sql.Date dob = rs.getDate("date_of_birth");
                if (dob != null) {
                    int birthYear = dob.toLocalDate().getYear();
                    int decade = (birthYear / 10) * 10;
                    String key = decade + "-" + (decade + 9);
                    if (stats.containsKey(key)) {
                        stats.put(key, stats.get(key) + 1);
                    }
                }
            }
        }
        return stats;
    }
} 