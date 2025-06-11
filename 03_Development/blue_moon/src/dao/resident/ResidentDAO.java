package dao.resident;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.management.relation.Relation;
import javax.print.DocFlavor.READER;

import utils.DatabaseConnection;
import utils.Utils;
import utils.enums.Gender;
import utils.enums.RelationshipType;
import exception.ServiceException;
import models.Resident;
import services.resident.ResidentServiceImpl;

public class ResidentDAO {
    private final Connection conn;

   
    
    public ResidentDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public List<Resident> findAll() {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents";

        try {
            System.out.println("Starting findAll() method in ResidentDAO");
            
            if (conn == null) {
                System.err.println("Database connection is null!");
                throw new SQLException("Database connection is not available");
            }
            System.out.println("Database connection is valid");

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                System.out.println("Executing query: " + sql);
                ResultSet rs = stmt.executeQuery();
                
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    try {
                        Resident resident = Utils.mapResultSetToResident(rs);
                        residents.add(resident);
                        System.out.println("Loaded resident: " + resident.getId() + " - " + resident.getFullName() + " - " + resident.getCitizenId());
                    } catch (SQLException e) {
                        System.err.println("Error mapping resident from ResultSet: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                
                if (!hasResults) {
                    System.out.println("No residents found in database.");
                } else {
                    System.out.println("Total residents loaded: " + residents.size());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in findAll(): " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Returning " + residents.size() + " residents from findAll()");
        return residents;
    }

    public Resident findById(int id) throws ServiceException {
        String sql = "SELECT * FROM residents WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Resident resident = Utils.mapResultSetToResident(rs);
                
                return resident;
            } else {
                throw new ServiceException("Resident with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in findById(): " + e.getMessage());
            throw new ServiceException("Database error when finding resident: " + id);
        }
    }
    
    
    public Resident findByCitizenId(String citizenId) throws ServiceException {
        String sql = "SELECT * FROM residents WHERE citizen_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, citizenId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Utils.mapResultSetToResident(rs);
            } else {
                throw new ServiceException("Resident with citizen ID " + citizenId + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Database error in findByCitizenId(): " + e.getMessage());
            throw new ServiceException("Database error when finding resident with citizen ID: " + citizenId);
        }
    }

    public List<Resident> findByHouseholdId(int householdId) throws ServiceException {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE household_id = ?";

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
                Resident resident = Utils.mapResultSetToResident(rs);
                residents.add(resident);
            }
            
            if (!hasResults) {
                System.out.println("No residents found for household ID: " + householdId);
            } 
            
            return residents;
        } catch (SQLException e) {
            System.err.println("Database error in findByHouseholdId(): " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("Database error when finding residents for household: " + householdId + " - " + e.getMessage());
        }
    }

    public Resident getHouseholdHead(int householdId) throws ServiceException {
	    String sql = "SELECT * FROM residents WHERE household_id = ? AND is_household_head = true";
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, householdId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            Resident resident = Utils.mapResultSetToResident(rs);
	            
	            return resident;
	        } else {
	            throw new ServiceException("Household head not found for household ID " + householdId);
	        }
	    } catch (SQLException e) {
	        System.err.println("Database error in getHouseholdHead(): " + e.getMessage());
	        throw new ServiceException("Database error when finding household head for household: " + householdId);
	    }
	}
//
//	public void add(Resident resident) throws ServiceException, SQLException {
//        if (residentExists(resident.getId())) {
//            throw new ServiceException("Resident with CCCD " + resident.getCitizenId() + " already exists.");
//        }
//        
//        String sql = "INSERT INTO residents (id, full_name, date_of_birth, gender, ethnicity, religion, citizen_id, date_of_issue, place_of_issue, relationship, occupation, added_date, household_id, is_household_head) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//        	Utils.setResidentData(stmt, resident);
//            stmt.executeUpdate();
//            
//        } catch (SQLException e) {
//            System.err.println("Error in add(): " + e.getMessage());
//            throw e;
//        }
//    }
//	
	public int add(Resident resident) throws SQLException {
	    // First, reset the sequence if needed
	    try (Statement stmt = conn.createStatement()) {
	        // Get current max id
	        ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM residents");
	        if (rs.next()) {
	            int maxId = rs.getInt(1);
	            // Reset sequence to start from max + 1
	            if (maxId > 0) {
	                stmt.execute("ALTER SEQUENCE residents_id_seq RESTART WITH " + (maxId + 1));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Warning: Could not reset sequence: " + e.getMessage());
	        // Continue anyway as this is not critical
	    }
	    
	    String sql = "INSERT INTO residents (full_name, date_of_birth, gender, ethnicity, religion, " +
	                 "citizen_id, date_of_issue, place_of_issue, relationship, occupation, " +
	                 "added_date, household_id, is_household_head) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        Utils.setResidentData(stmt, resident);
	        stmt.executeUpdate();
	        
	        ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()) {
	            return rs.getInt(1);
	        } else {
	            throw new SQLException("Creating resident failed, no ID obtained.");
	        }
	    } catch (SQLException e) {
	        System.err.println("Error in add(): " + e.getMessage());
	        throw e;
	    }
	}

    //TODO : 
    public void update(Resident resident) throws ServiceException, SQLException {
        if (resident == null 
        		|| resident.getCitizenId() == null
        		) {
            throw new IllegalArgumentException("Resident or Resident CCCD must not be null.");
        }
        
        String sql = "UPDATE residents SET full_name = ?, date_of_birth = ?, gender = ?, ethnicity = ?, religion = ?, citizen_id = ?, date_of_issue = ?, place_of_issue = ?, relationship = ?, occupation = ?, added_date = ?, household_id = ?, is_household_head = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
        	Utils.setResidentData(stmt, resident);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ServiceException("Cannot update. Resident with ID " + resident.getId() + " not found.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error in update(): " + e.getMessage());
            throw e;
        }
    }

    public void updateMembers(List<Resident> residents) throws ServiceException, SQLException {
        for (Resident resident : residents) {
            update(resident);
        }
    }

    public void delete(String residentCitizenId) throws ServiceException, SQLException {
        String sql = "DELETE FROM residents WHERE citizen_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, residentCitizenId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ServiceException("No resident deleted with CCCD: " + residentCitizenId);
            }
        } catch (SQLException e) {
            System.err.println("Error in delete(): " + e.getMessage());
            throw e;
        }
    }

    public void setHouseholdOwnerByResidentCitizenId(String ownerCitizenId) throws ServiceException, SQLException {
        // First, get the resident to find their household
        Resident owner = findByCitizenId(ownerCitizenId);
        
        // Reset all residents in this household to not be household head
        String resetSql = "UPDATE residents SET is_household_head = false WHERE household_id = ?";
        try (PreparedStatement resetStmt = conn.prepareStatement(resetSql)) {
            resetStmt.setInt(1, owner.getHouseholdId());
            resetStmt.executeUpdate();
        }
        
        // Set the specified resident as household head
        String setSql = "UPDATE residents SET is_household_head = true WHERE citizen_id = ?";
        try (PreparedStatement setStmt = conn.prepareStatement(setSql)) {
            setStmt.setString(1, ownerCitizenId);
            int rowsAffected = setStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ServiceException("Failed to set household head for resident: " + ownerCitizenId);
            }
        }
    }
    
    public boolean residentExists(int residentId) {
	    String sql = "SELECT COUNT(*) FROM residents WHERE id = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, residentId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        System.err.println("Error checking resident existence: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return false;
	}

	public boolean citizenIdExists(String citizenId) {
        String sql = "SELECT COUNT(*) FROM residents WHERE citizen_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, citizenId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking citizen ID existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

}