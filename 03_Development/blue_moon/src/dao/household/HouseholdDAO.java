package dao.household;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import exception.HouseholdNotExist;
import exception.ServiceException;
import models.Household;
import models.Resident;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import utils.DatabaseConnection;
import utils.Utils;

public class HouseholdDAO {
	private final Connection conn;

    public HouseholdDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public List<Household> findAll() throws ServiceException {
        List<Household> households = new ArrayList<>();
        String sql = "SELECT * FROM households ORDER BY id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Household household = Utils.mapResultSetToHousehold(rs);
                households.add(household);
            }
        } catch (SQLException e) {
            System.err.println("Error in findAll(): " + e.getMessage());
            throw new ServiceException("Error getting all households: " + e.getMessage());
        }

        return households;
    }

    public Household findById(int id) throws HouseholdNotExist, ServiceException {
        String sql = "SELECT * FROM households WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Household h = Utils.mapResultSetToHousehold(rs);
                return h;
            } else {
                throw new HouseholdNotExist("Cannot find household with id: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Database error in findById(): " + e.getMessage());
            throw new HouseholdNotExist("Database error when finding household: " + id);
        }
    }

      
    public Household findByPhone(String phone) throws ServiceException {
	    String sql = "SELECT * FROM households WHERE phone = ?";
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, phone);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return Utils.mapResultSetToHousehold(rs);
	        }
	        return null; // Return null if no household found with this phone number
	    } catch (SQLException e) {
	        System.err.println("Error in findByPhone(): " + e.getMessage());
	        throw new ServiceException("Error finding household by phone: " + e.getMessage());
	    }
	}
    

    public Household findByEmail(String email) throws ServiceException {
	    String sql = "SELECT * FROM households WHERE email = ?";
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, email);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return Utils.mapResultSetToHousehold(rs);
	        }
	        return null;
	    } catch (SQLException e) {
	        System.err.println("Error in findByEmail(): " + e.getMessage());
	        throw new ServiceException("Error finding household by email: " + e.getMessage());
	    }
	}


	public int add(Household household) throws SQLException {
        // First, reset the sequence if needed
        try (Statement stmt = conn.createStatement()) {
            // Get current max id
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM households");
            if (rs.next()) {
                int maxId = rs.getInt(1);
                // Reset sequence to start from max + 1
                if (maxId > 0) {
                    stmt.execute("ALTER SEQUENCE households_id_seq RESTART WITH " + (maxId + 1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Warning: Could not reset sequence: " + e.getMessage());
            // Continue anyway as this is not critical
        }
        
        String sql = "INSERT INTO households (house_number, street, ward, district, household_size, owner_id, phone, email, creation_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Utils.setHouseholdData(stmt, household);
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating household failed, no ID obtained.");
            }
        } catch (SQLException e) {
            System.err.println("Error in add(): " + e.getMessage());
            throw e;
        }
    }
    

    public void update(Household household) throws SQLException {
        String sql = "UPDATE households SET house_number = ?, street = ?, ward = ?, district = ?, " +
                    "household_size = ?, owner_id = ?, phone = ?, email = ?, creation_date = ? " +
                    "WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Utils.setHouseholdData(stmt, household);
            stmt.setInt(10, household.getId());  // Set ID for WHERE clause
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating household failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Error in update(): " + e.getMessage());
            throw e;
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM households WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No household deleted with id: " + id);
            }
            System.out.println("Deleted " + rowsAffected + " household(s)");
        } catch (SQLException e) {
            System.err.println("Error in delete(): " + e.getMessage());
            throw e;
        }
    }


    // Method to get household with residents
    public Household findByIdWithResidents(int id) throws HouseholdNotExist, ServiceException {
        Household household = findById(id);
        
        // Get residents for this household
        String sql = "SELECT * FROM residents WHERE household_id = ?";
        List<Resident> residents = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Resident resident = Utils.mapResultSetToResident(rs);
                residents.add(resident);
            }
            household.setResidents(residents);
        } catch (SQLException e) {
            System.err.println("Error loading residents: " + e.getMessage());
            e.printStackTrace();
        }
        
        return household;
    }

	public void removeResident(Household household, Resident resident) throws SQLException {
		household.getResidents().remove(resident);
		update(household);
		
	}

	public void addResidentToHousehold(Household household, Resident resident) throws SQLException {
		household.getResidents().add(resident);
		update(household);
	}
	
	
	public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM households WHERE email = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            throw e;
        }
        
        return false;
    }
}
