package dao.tempresidenceabsence;

import dto.TemporaryResidenceAbsenceDTO;
import models.TemporaryResidenceAbsence;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemporaryResidenceAbsenceDAO {
    
    private static final String INSERT_REQUEST = 
        "INSERT INTO temporary_residence_absence (member_id, member_name, type, reason, start_date, end_date, address, status, created_date) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_ALL = 
        "SELECT * FROM temporary_residence_absence ORDER BY created_date DESC";
    
    private static final String SELECT_BY_ID = 
        "SELECT * FROM temporary_residence_absence WHERE id = ?";
    
    private static final String UPDATE_REQUEST = 
        "UPDATE temporary_residence_absence SET member_id = ?, member_name = ?, type = ?, reason = ?, " +
        "start_date = ?, end_date = ?, address = ? WHERE id = ?";
    
    private static final String UPDATE_STATUS = 
        "UPDATE temporary_residence_absence SET status = ? WHERE id = ?";
    
    private static final String DELETE_REQUEST = 
        "DELETE FROM temporary_residence_absence WHERE id = ?";
    
    private static final String SEARCH_BY_MEMBER_NAME = 
        "SELECT * FROM temporary_residence_absence WHERE UPPER(member_name) LIKE UPPER(?) ORDER BY created_date DESC";
    
    private static final String SELECT_BY_TYPE = 
        "SELECT * FROM temporary_residence_absence WHERE type = ? ORDER BY created_date DESC";
    
    private static final String SELECT_BY_STATUS = 
        "SELECT * FROM temporary_residence_absence WHERE status = ? ORDER BY created_date DESC";
    
    private static final String SELECT_BY_DATE_RANGE = 
        "SELECT * FROM temporary_residence_absence WHERE start_date >= ? AND end_date <= ? ORDER BY start_date";
    
    private static final String SELECT_BY_MEMBER_ID = 
        "SELECT * FROM temporary_residence_absence WHERE member_id = ? ORDER BY created_date DESC";
    
    private static final String COUNT_TOTAL = 
        "SELECT COUNT(*) FROM temporary_residence_absence";
    
    private static final String COUNT_BY_STATUS = 
        "SELECT COUNT(*) FROM temporary_residence_absence WHERE status = ?";
    
    private static final String COUNT_BY_TYPE = 
        "SELECT COUNT(*) FROM temporary_residence_absence WHERE type = ?";
    
    private static final String SELECT_ACTIVE_BY_MEMBER_ID = 
        "SELECT * FROM temporary_residence_absence WHERE member_id = ? AND status = 'APPROVED' " +
        "AND start_date <= ? AND end_date >= ? ORDER BY start_date";
    
    private static final String CHECK_DATE_OVERLAP = 
        "SELECT COUNT(*) FROM temporary_residence_absence WHERE member_id = ? " +
        "AND status IN ('PENDING', 'APPROVED') " +
        "AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?) " +
        "OR (start_date >= ? AND end_date <= ?)) " +
        "AND (? IS NULL OR id != ?)";

    // ==================== CRUD Operations ====================
    
    public TemporaryResidenceAbsence create(TemporaryResidenceAbsenceDTO dto) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_REQUEST, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, dto.getMemberId());
            stmt.setString(2, dto.getMemberName());
            stmt.setString(3, dto.getType());
            stmt.setString(4, dto.getReason());
            stmt.setDate(5, Date.valueOf(dto.getStartDate()));
            stmt.setDate(6, Date.valueOf(dto.getEndDate()));
            stmt.setString(7, dto.getAddress());
            stmt.setString(8, "PENDING"); // Default status
            stmt.setDate(9, Date.valueOf(LocalDate.now()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating request failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return findById(id).orElse(null);
                } else {
                    throw new SQLException("Creating request failed, no ID obtained.");
                }
            }
        }
    }
    
    public List<TemporaryResidenceAbsence> findAll() throws SQLException {
        List<TemporaryResidenceAbsence> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                requests.add(mapResultSetToEntity(rs));
            }
        }
        
        return requests;
    }
    
    public Optional<TemporaryResidenceAbsence> findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    public TemporaryResidenceAbsence update(int id, TemporaryResidenceAbsenceDTO dto) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_REQUEST)) {
            
            stmt.setInt(1, dto.getMemberId());
            stmt.setString(2, dto.getMemberName());
            stmt.setString(3, dto.getType());
            stmt.setString(4, dto.getReason());
            stmt.setDate(5, Date.valueOf(dto.getStartDate()));
            stmt.setDate(6, Date.valueOf(dto.getEndDate()));
            stmt.setString(7, dto.getAddress());
            stmt.setInt(8, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating request failed, no rows affected.");
            }
            
            return findById(id).orElse(null);
        }
    }
    
    public TemporaryResidenceAbsence updateStatus(int id, String status) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating request status failed, no rows affected.");
            }
            
            return findById(id).orElse(null);
        }
    }
    
    public boolean delete(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_REQUEST)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // ==================== Search and Filter Operations ====================
    
    public List<TemporaryResidenceAbsence> findByMemberName(String memberName) throws SQLException {
        List<TemporaryResidenceAbsence> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_MEMBER_NAME)) {
            
            stmt.setString(1, "%" + memberName + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return requests;
    }
    
    public List<TemporaryResidenceAbsence> findByType(String type) throws SQLException {
        List<TemporaryResidenceAbsence> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TYPE)) {
            
            stmt.setString(1, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return requests;
    }
    
    public List<TemporaryResidenceAbsence> findByStatus(String status) throws SQLException {
        List<TemporaryResidenceAbsence> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STATUS)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return requests;
    }
    
    public List<TemporaryResidenceAbsence> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<TemporaryResidenceAbsence> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATE_RANGE)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return requests;
    }
    
    public List<TemporaryResidenceAbsence> findByMemberId(int memberId) throws SQLException {
        List<TemporaryResidenceAbsence> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MEMBER_ID)) {
            
            stmt.setInt(1, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return requests;
    }
    
    // ==================== Statistics ====================
    
    public int getTotalCount() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_TOTAL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public int getCountByStatus(String status) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_BY_STATUS)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
    
    public int getCountByType(String type) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_BY_TYPE)) {
            
            stmt.setString(1, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
    
    // ==================== Business Logic Support ====================
    
    public List<TemporaryResidenceAbsence> findActiveByMemberId(int memberId) throws SQLException {
        List<TemporaryResidenceAbsence> requests = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVE_BY_MEMBER_ID)) {
            
            stmt.setInt(1, memberId);
            stmt.setDate(2, Date.valueOf(today));
            stmt.setDate(3, Date.valueOf(today));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToEntity(rs));
                }
            }
        }
        
        return requests;
    }
    
    public boolean hasDateOverlap(int memberId, LocalDate startDate, LocalDate endDate, Integer excludeRequestId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_DATE_OVERLAP)) {
            
            stmt.setInt(1, memberId);
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setDate(5, Date.valueOf(startDate));
            stmt.setDate(6, Date.valueOf(startDate));
            stmt.setDate(7, Date.valueOf(endDate));
            
            if (excludeRequestId != null) {
                stmt.setInt(8, excludeRequestId);
                stmt.setInt(9, excludeRequestId);
            } else {
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    // ==================== Helper Methods ====================
    
    private TemporaryResidenceAbsence mapResultSetToEntity(ResultSet rs) throws SQLException {
        TemporaryResidenceAbsence request = new TemporaryResidenceAbsence();
        
        request.setId(rs.getInt("id"));
        request.setMemberId(rs.getInt("member_id"));
        request.setMemberName(rs.getString("member_name"));
        request.setType(rs.getString("type"));
        request.setReason(rs.getString("reason"));
        request.setStartDate(rs.getDate("start_date").toLocalDate());
        request.setEndDate(rs.getDate("end_date").toLocalDate());
        request.setAddress(rs.getString("address"));
        request.setStatus(rs.getString("status"));
        request.setCreatedDate(rs.getDate("created_date").toLocalDate());
        
        return request;
    }

	public TemporaryResidenceAbsence save(TemporaryResidenceAbsence request) {
		// TODO Auto-generated method stub
		return null;
	}
}