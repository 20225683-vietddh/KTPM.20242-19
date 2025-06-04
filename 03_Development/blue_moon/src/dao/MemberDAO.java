package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import utils.DatabaseConnection;
import exception.ServiceException;
import models.Resident;
import services.MemberServiceImpl;

public class MemberDAO {
    private final Connection conn;

   
    
    public MemberDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public List<Resident> findAll() {
    	
    	
        List<Resident> members = new ArrayList<>();
        String sql = "SELECT * FROM member";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Resident member = new Resident();
                member.setId(rs.getString("id"));
                member.setHouseholdId(rs.getInt("household_id"));
                member.setFullName(rs.getString("full_name"));
                member.setGender(rs.getString("gender"));
                
                member.setDateOfBirth(rs.getDate("date_of_birth"));
                
                member.setIdCard(rs.getString("id_card"));
                member.setRelationship(rs.getString("relationship"));
                member.setOccupation(rs.getString("occupation"));
                member.setHouseholdHead(rs.getBoolean("is_household_head"));
                
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error in findAll(): " + e.getMessage());
            e.printStackTrace();
        }

        return members;
    }

    public Resident findById(String id) throws ServiceException {
        String sql = "SELECT * FROM member WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Resident member = new Resident();
                member.setId(rs.getString("id"));
                member.setHouseholdId(rs.getInt("household_id"));
                member.setFullName(rs.getString("full_name"));
                member.setGender(rs.getString("gender"));
                
                // Convert Date to String for your model
                member.setDateOfBirth(rs.getDate("date_of_birth"));
                
                member.setIdCard(rs.getString("id_card"));
                member.setRelationship(rs.getString("relationship"));
                member.setOccupation(rs.getString("occupation"));
                member.setHouseholdHead(rs.getBoolean("is_household_head"));
                
                return member;
            } else {
                throw new ServiceException("Member with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in findById(): " + e.getMessage());
            throw new ServiceException("Database error when finding member: " + id);
        }
    }

    public List<Resident> findByHouseholdId(int householdId) throws ServiceException {
        List<Resident> members = new ArrayList<>();
        String sql = "SELECT * FROM member WHERE household_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Database connection is null!");
                throw new ServiceException("Database connection is not available");
            }

            stmt.setInt(1, householdId);
            ResultSet rs = stmt.executeQuery();
            
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                Resident member = new Resident();
                member.setId(rs.getString("id"));
                member.setHouseholdId(rs.getInt("household_id"));
                member.setFullName(rs.getString("full_name"));
                member.setGender(rs.getString("gender"));
                member.setDateOfBirth(rs.getDate("date_of_birth"));
                member.setIdCard(rs.getString("id_card"));
                member.setRelationship(rs.getString("relationship"));
                member.setOccupation(rs.getString("occupation"));
                member.setHouseholdHead(rs.getBoolean("is_household_head"));
                members.add(member);
            }
            
            if (!hasResults) {
                System.out.println("No members found for household ID: " + householdId);
            } 
            
            return members;
        } catch (SQLException e) {
            System.err.println("Database error in findByHouseholdId(): " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("Database error when finding members for household: " + householdId + " - " + e.getMessage());
        }
    }

    public boolean memberExists(String memberId) {
        String sql = "SELECT COUNT(*) FROM member WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking member existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public void add(Resident member) throws ServiceException, SQLException {
        if (memberExists(member.getId())) {
            throw new ServiceException("Member with ID " + member.getId() + " already exists.");
        }
        
        String sql = "INSERT INTO member (id, household_id, full_name, gender, date_of_birth, id_card, relationship, occupation, is_household_head) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getId());
            stmt.setInt(2, member.getHouseholdId());
            stmt.setString(3, member.getFullName());
            stmt.setString(4, member.getGender().toString());
            
            // Convert String to Date for database
                stmt.setDate(5,member.getDateOfBirth());
            
            stmt.setString(6, member.getIdCard());
            stmt.setString(7, member.getRelationship().toString());
            stmt.setString(8, member.getOccupation());
            stmt.setBoolean(9, member.isHouseholdHead());
            
            int rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in add(): " + e.getMessage());
            throw e;
        }
    }

    public void update(Resident member) throws ServiceException, SQLException {
        if (member == null || member.getId() == null) {
            throw new IllegalArgumentException("Member or Member ID must not be null.");
        }
        
        String sql = "UPDATE member SET household_id = ?, full_name = ?, gender = ?, date_of_birth = ?, id_card = ?, relationship = ?, occupation = ?, is_household_head = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, member.getHouseholdId());
            stmt.setString(2, member.getFullName());
            stmt.setString(3, member.getGender().toString());
            
                stmt.setDate(4, member.getDateOfBirth());
           
            
            stmt.setString(5, member.getIdCard());
            stmt.setString(6, member.getRelationship().toString());
            stmt.setString(7, member.getOccupation());
            stmt.setBoolean(8, member.isHouseholdHead());
            stmt.setString(9, member.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ServiceException("Cannot update. Member with ID " + member.getId() + " not found.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error in update(): " + e.getMessage());
            throw e;
        }
    }

    public void updateMembers(List<Resident> members) throws ServiceException, SQLException {
        for (Resident member : members) {
            update(member);
        }
    }

    public void delete(String memberId) throws ServiceException, SQLException {
        String sql = "DELETE FROM member WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ServiceException("No member deleted with id: " + memberId);
            }
        } catch (SQLException e) {
            System.err.println("Error in delete(): " + e.getMessage());
            throw e;
        }
    }

    public Resident getHouseholdHead(int householdId) throws ServiceException {
        String sql = "SELECT * FROM member WHERE household_id = ? AND is_household_head = true";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, householdId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Resident member = new Resident();
                member.setId(rs.getString("id"));
                member.setHouseholdId(rs.getInt("household_id"));
                member.setFullName(rs.getString("full_name"));
                member.setGender(rs.getString("gender"));
                
                member.setDateOfBirth(rs.getDate("date_of_birth"));
                
                member.setIdCard(rs.getString("id_card"));
                member.setRelationship(rs.getString("relationship"));
                member.setOccupation(rs.getString("occupation"));
                member.setHouseholdHead(rs.getBoolean("is_household_head"));
                
                return member;
            } else {
                throw new ServiceException("Household head not found for household ID " + householdId);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getHouseholdHead(): " + e.getMessage());
            throw new ServiceException("Database error when finding household head for household: " + householdId);
        }
    }

    public void setHouseholdOwnerByMemberId(String ownerId) throws ServiceException, SQLException {
        // First, get the member to find their household
        Resident owner = findById(ownerId);
        
        // Reset all members in this household to not be household head
        String resetSql = "UPDATE member SET is_household_head = false WHERE household_id = ?";
        try (PreparedStatement resetStmt = conn.prepareStatement(resetSql)) {
            resetStmt.setInt(1, owner.getHouseholdId());
            resetStmt.executeUpdate();
        }
        
        // Set the specified member as household head
        String setSql = "UPDATE member SET is_household_head = true WHERE id = ?";
        try (PreparedStatement setStmt = conn.prepareStatement(setSql)) {
            setStmt.setString(1, ownerId);
            int rowsAffected = setStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ServiceException("Failed to set household head for member: " + ownerId);
            }
        }
    }

}