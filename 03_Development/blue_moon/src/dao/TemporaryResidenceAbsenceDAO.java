package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.TemporaryResidenceAbsence;
import utils.DatabaseConnection;

public class TemporaryResidenceAbsenceDAO {
    private Connection connection;
    
    public TemporaryResidenceAbsenceDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public List<TemporaryResidenceAbsence> getAll() {
        List<TemporaryResidenceAbsence> tempResidenceAbsences = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM temporary_residence_absence ORDER BY id";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                TemporaryResidenceAbsence tempResidenceAbsence = mapResultSet(resultSet);
                tempResidenceAbsences.add(tempResidenceAbsence);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tempResidenceAbsences;
    }
    
    public List<TemporaryResidenceAbsence> getByMemberId(int memberId) {
        List<TemporaryResidenceAbsence> tempResidenceAbsences = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM temporary_residence_absence WHERE member_id = ? ORDER BY id";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, memberId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                TemporaryResidenceAbsence tempResidenceAbsence = mapResultSet(resultSet);
                tempResidenceAbsences.add(tempResidenceAbsence);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tempResidenceAbsences;
    }
    
    public boolean save(TemporaryResidenceAbsence tempResidenceAbsence) {
        try {
            String query = "INSERT INTO temporary_residence_absence (member_id, member_name, type, reason, start_date, end_date, address, status) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, tempResidenceAbsence.getMemberId());
            statement.setString(2, tempResidenceAbsence.getMemberName());
            statement.setString(3, tempResidenceAbsence.getType());
            statement.setString(4, tempResidenceAbsence.getReason());
            statement.setDate(5, Date.valueOf(tempResidenceAbsence.getStartDate()));
            statement.setDate(6, Date.valueOf(tempResidenceAbsence.getEndDate()));
            statement.setString(7, tempResidenceAbsence.getAddress());
            statement.setString(8, tempResidenceAbsence.getStatus());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tempResidenceAbsence.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean update(TemporaryResidenceAbsence tempResidenceAbsence) {
        try {
            String query = "UPDATE temporary_residence_absence SET member_id = ?, member_name = ?, type = ?, " +
                           "reason = ?, start_date = ?, end_date = ?, address = ?, status = ? " +
                           "WHERE id = ?";
            
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, tempResidenceAbsence.getMemberId());
            statement.setString(2, tempResidenceAbsence.getMemberName());
            statement.setString(3, tempResidenceAbsence.getType());
            statement.setString(4, tempResidenceAbsence.getReason());
            statement.setDate(5, Date.valueOf(tempResidenceAbsence.getStartDate()));
            statement.setDate(6, Date.valueOf(tempResidenceAbsence.getEndDate()));
            statement.setString(7, tempResidenceAbsence.getAddress());
            statement.setString(8, tempResidenceAbsence.getStatus());
            statement.setInt(9, tempResidenceAbsence.getId());
            
            int rowsAffected = statement.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean delete(int id) {
        try {
            String query = "DELETE FROM temporary_residence_absence WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private TemporaryResidenceAbsence mapResultSet(ResultSet resultSet) throws SQLException {
        TemporaryResidenceAbsence tempResidenceAbsence = new TemporaryResidenceAbsence();
        tempResidenceAbsence.setId(resultSet.getInt("id"));
        tempResidenceAbsence.setMemberId(resultSet.getInt("member_id"));
        tempResidenceAbsence.setMemberName(resultSet.getString("member_name"));
        tempResidenceAbsence.setType(resultSet.getString("type"));
        tempResidenceAbsence.setReason(resultSet.getString("reason"));
        tempResidenceAbsence.setStartDate(resultSet.getDate("start_date").toLocalDate());
        tempResidenceAbsence.setEndDate(resultSet.getDate("end_date").toLocalDate());
        tempResidenceAbsence.setAddress(resultSet.getString("address"));
        tempResidenceAbsence.setStatus(resultSet.getString("status"));
        return tempResidenceAbsence;
    }
}