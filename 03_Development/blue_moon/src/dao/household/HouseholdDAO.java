package dao.household;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import exception.HouseholdNotExist;
import exception.ServiceException;
import models.Household;
import models.Resident;
import services.MemberService;
import services.MemberServiceImpl;
import utils.DatabaseConnection;

public class HouseholdDAO {
//    private Connection connection;
//    
//    public HouseholdDAO() {
//        this.connection = DatabaseConnection.getConnection();
//    }
//    
//    public List<Household> findAll() {
//    	return HouseholdDB.HOUSEHOLD_SAMPLE;
//    }
//    
//    public Household findById(int id) throws HouseholdNotExist {
//        for (Household household :  HouseholdDB.HOUSEHOLD_SAMPLE) {
//        	if (household.getId() == id) {
//        		 return household;
//        	}
//        }
//        
//        throw new HouseholdNotExist("Cannot find household from id"+id);
//       
//    }
//    
//    public Household findByHouseholdNumber(String householdNumber) {
//    	for (Household household :  HouseholdDB.HOUSEHOLD_SAMPLE) {
//        	if (household.getHouseholdNumber().equals(householdNumber)) {
//        		 return household;
//        	}
//        }
//    	
//    	return null;
//	}
//
//	public void add(Household saveHousehold) {
//		//try catch 
//    	HouseholdDB.HOUSEHOLD_SAMPLE.add(saveHousehold);
//        return ;
//    }
//
//	public void update(Household updatedHousehold) throws HouseholdNotExist {
//		System.out.println(updatedHousehold.getEmail());
//		Household household = findByHouseholdNumber(updatedHousehold.getHouseholdNumber());
//		System.out.println(household.getEmail());
//		HouseholdDB.HOUSEHOLD_SAMPLE.remove(household);
//		HouseholdDB.HOUSEHOLD_SAMPLE.add(updatedHousehold);
//
//	}
//    
//    public void delete(int id) throws HouseholdNotExist {
//    	Household household = findById(id);
//    		HouseholdDB.HOUSEHOLD_SAMPLE.remove(household);
//    }
//
//    public void addMemberToHousehold(Household h, String memberId) throws HouseholdNotExist {
//        if (!h.getMemberIds().contains(memberId)) {
//            h.getMemberIds().add(memberId);
//            update(h);
//        } else {
//            System.out.println("Member already exists in household.");
//        }
//    }
//
//    public void removeMember(Household h, String memberId) throws HouseholdNotExist {
//        if (h.getMemberIds().contains(memberId)) {
//            h.getMemberIds().remove(memberId);
//            update(h);
//        } else {
//            System.out.println("Member not found in household.");
//        }
//    }
	
	
	
	
	
	
	
	
	
	private final Connection conn;

    public HouseholdDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public List<Household> findAll() throws ServiceException {
        MemberServiceImpl memberService = new MemberServiceImpl();  
        
        List<Household> households = new ArrayList<>();
        String sql = "SELECT * FROM households";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Household h = new Household();
                h.setId(rs.getInt("id"));
                h.setEmail(rs.getString("email"));
                h.setPhone(rs.getString("phone"));
                h.setAddress(rs.getString("address"));
                h.setOwnerName(rs.getString("owner_name"));
                h.setArea(rs.getString("area"));
                h.setHouseholdSize(rs.getInt("household_size"));
                h.setOwnerId(rs.getString("owner_id"));
                h.setCreationDate(rs.getDate("creation_date"));
                
                // Get members for this household
                try {
                    List<Resident> members = memberService.getMembersByHouseholdId(h.getId());
                    h.setMembers(members);
                } catch (ServiceException e) {
                    System.err.println("No members for household ID " + h.getId());
                    h.setMembers(new ArrayList<>());
                }
                
                households.add(h);
            }
        } catch (SQLException e) {
            System.err.println("Error in findAll(): " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("Database error when finding all households: " + e.getMessage());
        }

        return households;
    }

    public Household findById(int id) throws HouseholdNotExist, ServiceException {
        String sql = "SELECT * FROM households WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Household h = new Household();
                h.setId(rs.getInt("id"));
                h.setEmail(rs.getString("email"));
                h.setPhone(rs.getString("phone"));
                h.setAddress(rs.getString("address"));
                h.setOwnerName(rs.getString("owner_name"));
                h.setArea(rs.getString("area"));
                h.setHouseholdSize(rs.getInt("household_size"));
                h.setOwnerId(rs.getString("owner_id"));
                h.setCreationDate(rs.getDate("creation_date"));
                return h;
            } else {
                throw new HouseholdNotExist("Cannot find household with id: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Database error in findById(): " + e.getMessage());
            throw new HouseholdNotExist("Database error when finding household: " + id);
        }
    }

    

    public int add(Household household) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert with auto-generated ID from sequence
            String sql = "INSERT INTO households (email, phone, address, owner_name, area, household_size, owner_id, creation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, household.getEmail());
            stmt.setString(2, household.getPhone());
            stmt.setString(3, household.getAddress());
            stmt.setString(4, household.getOwnerName());
            stmt.setString(5, household.getArea());
            stmt.setInt(6, household.getHouseholdSize());
            stmt.setString(7, household.getOwnerId());
            
            // Convert String to Date for database
            if (household.getCreationDate() != null) {
                stmt.setDate(8, household.getCreationDate());
            } else {
                stmt.setDate(8, new java.sql.Date(System.currentTimeMillis()));
            }
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt("id");
                household.setId(generatedId);
                conn.commit();
                return generatedId;
            }
            
            throw new SQLException("Failed to get generated ID for household");
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Error rolling back transaction", ex);
                }
            }
            throw new SQLException("Error adding household: " + e.getMessage(), e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignored */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignored */ }
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignored */ }
        }
    }

    public void update(Household household) throws SQLException {
        String sql = "UPDATE households SET email = ?, phone = ?, address = ?, owner_name = ?, area = ?, household_size = ?, owner_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, household.getEmail());
            stmt.setString(2, household.getPhone());
            stmt.setString(3, household.getAddress());
            stmt.setString(4, household.getOwnerName());
            stmt.setString(5, household.getArea());
            stmt.setInt(6, household.getHouseholdSize());
            stmt.setString(7, household.getOwnerId());
            stmt.setInt(8, household.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No household updated with ID: " + household.getId());
            }
            System.out.println("Updated " + rowsAffected + " household(s)");
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

    // Helper method to test database connection
    public boolean testConnection() {
        try {
            String sql = "SELECT COUNT(*) FROM households";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Total households in database: " + count);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Method to get household with members
    public Household findByIdWithMembers(int id) throws HouseholdNotExist, ServiceException {
        Household household = findById(id);
        
        // Get members for this household
        String sql = "SELECT * FROM member WHERE household_id = ?";
        List<Resident> members = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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
            household.setMembers(members);
        } catch (SQLException e) {
            System.err.println("Error loading members: " + e.getMessage());
            e.printStackTrace();
        }
        
        return household;
    }

	public void removeMember(Household household, Resident member) throws SQLException {
		household.getMembers().remove(member);
		update(household);
		
	}

	public void addMemberToHousehold(Household household, Resident member) throws SQLException {
		household.getMembers().add(member);
		update(household);
	}
}