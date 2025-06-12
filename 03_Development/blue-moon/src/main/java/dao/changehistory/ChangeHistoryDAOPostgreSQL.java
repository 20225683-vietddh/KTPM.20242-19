package dao.changehistory;

import dao.PostgreSQLConnection;
import models.ChangeHistoryRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChangeHistoryDAOPostgreSQL implements ChangeHistoryDAO {
    private Connection conn;
    
    public ChangeHistoryDAOPostgreSQL() throws SQLException {
        this.conn = PostgreSQLConnection.getInstance().getConnection();
    }
    
    @Override
    public boolean addChangeRecord(ChangeHistoryRecord record) {
        String sql = "INSERT INTO change_history_records (change_type, change_date, resident_id, household_id) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            System.out.println("🔍 DEBUG: Chuẩn bị thêm lịch sử...");
            System.out.println("   - Change Type: " + record.getChangeType());
            System.out.println("   - Change Date: " + record.getChangeDate());
            System.out.println("   - Resident ID: " + record.getResidentId());
            System.out.println("   - Household ID: " + record.getHouseholdId());
            
            stmt.setString(1, record.getChangeType());
            stmt.setDate(2, Date.valueOf(record.getChangeDate()));
            stmt.setInt(3, record.getResidentId());
            stmt.setInt(4, record.getHouseholdId());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("🔍 DEBUG: Rows affected = " + rowsAffected);
            
            if (rowsAffected > 0) {
                System.out.println("✅ DEBUG: Đã ghi lịch sử thành công!");
                return true;
            } else {
                System.err.println("❌ DEBUG: Không có row nào bị ảnh hưởng!");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ DEBUG: Lỗi khi thêm bản ghi lịch sử: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<ChangeHistoryRecord> getAllChangeRecords() {
        String sql = "SELECT c.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM change_history_records c " +
                    "LEFT JOIN residents r ON c.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON c.household_id = h.household_id " +
                    "ORDER BY c.change_date DESC, c.record_id DESC";
        List<ChangeHistoryRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                records.add(mapResultSetToRecordWithNames(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả bản ghi lịch sử: " + e.getMessage());
            e.printStackTrace();
        }
        
        return records;
    }
    
    @Override
    public List<ChangeHistoryRecord> getChangeRecordsByResidentId(int residentId) {
        String sql = "SELECT * FROM change_history_records WHERE resident_id = ? ORDER BY change_date DESC, record_id DESC";
        List<ChangeHistoryRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, residentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bản ghi lịch sử theo resident ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return records;
    }
    
    @Override
    public List<ChangeHistoryRecord> getChangeRecordsByHouseholdId(int householdId) {
        String sql = "SELECT c.*, r.full_name as resident_name, h.house_number as household_name " +
                    "FROM change_history_records c " +
                    "LEFT JOIN residents r ON c.resident_id = r.resident_id " +
                    "LEFT JOIN households h ON c.household_id = h.household_id " +
                    "WHERE c.household_id = ? " +
                    "ORDER BY c.change_date DESC, c.record_id DESC";
        List<ChangeHistoryRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, householdId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecordWithNames(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bản ghi lịch sử theo household ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return records;
    }
    
    @Override
    public List<ChangeHistoryRecord> getChangeRecordsByType(String changeType) {
        String sql = "SELECT * FROM change_history_records WHERE change_type = ? ORDER BY change_date DESC, record_id DESC";
        List<ChangeHistoryRecord> records = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, changeType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToRecord(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bản ghi lịch sử theo loại thay đổi: " + e.getMessage());
            e.printStackTrace();
        }
        
        return records;
    }
    
    @Override
    public boolean deleteChangeRecord(int recordId) {
        String sql = "DELETE FROM change_history_records WHERE record_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, recordId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bản ghi lịch sử: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteChangeRecordsByResidentId(int residentId) {
        String sql = "DELETE FROM change_history_records WHERE resident_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, residentId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bản ghi lịch sử theo resident ID: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteChangeRecordsByHouseholdId(int householdId) {
        String sql = "DELETE FROM change_history_records WHERE household_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, householdId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bản ghi lịch sử theo household ID: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Map ResultSet to ChangeHistoryRecord object
     */
    private ChangeHistoryRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        return new ChangeHistoryRecord(
            rs.getInt("record_id"),
            rs.getString("change_type"),
            rs.getDate("change_date").toLocalDate(),
            rs.getInt("resident_id"),
            rs.getInt("household_id")
        );
    }
    
    /**
     * Map ResultSet to ChangeHistoryRecord object with resident and household names
     */
    private ChangeHistoryRecord mapResultSetToRecordWithNames(ResultSet rs) throws SQLException {
        return new ChangeHistoryRecord(
            rs.getInt("record_id"),
            rs.getString("change_type"),
            rs.getDate("change_date").toLocalDate(),
            rs.getInt("resident_id"),
            rs.getInt("household_id"),
            rs.getString("resident_name"),
            rs.getString("household_name")
        );
    }
} 