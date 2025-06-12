package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javafx.scene.image.ImageView;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import models.Household;
import models.Resident;
import services.household.HouseholdService;
import services.household.HouseholdServiceImpl;
import services.resident.ResidentService;
import services.resident.ResidentServiceImpl;
import utils.enums.Gender;
import utils.enums.RelationshipType;
import javafx.animation.Interpolator;
import java.time.LocalDate;
import java.util.List;
import java.time.DateTimeException;
import exception.InvalidDateRangeException;
import exception.ServiceException;

public class Utils {
	private final static HouseholdServiceImpl householdService = new HouseholdServiceImpl();
	private final static ResidentServiceImpl memberService = new ResidentServiceImpl();
	
	
	public static String toSHA256(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                hexString.append(String.format("%02x", b)); 
            }

            return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			 throw new RuntimeException(e);
		}
	}
	
	public static void rotateLogo(ImageView imgView) {
    	RotateTransition rotate = new RotateTransition(Duration.seconds(5), imgView);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();
    }
	
	public static LocalDate parseDateSafely(String dayStr, String monthStr, String yearStr) {
	    try {
	        int day = Integer.parseInt(dayStr);
	        int month = Integer.parseInt(monthStr);
	        int year = Integer.parseInt(yearStr);
	        return LocalDate.of(year, month, day);
	    } catch (NumberFormatException | DateTimeException e) {
	        return null;
	    }
	}
	
	public static void validateStartAndDueDate(String sDay, String sMonth, String sYear, String dDay, String dMonth, String dYear) throws InvalidDateRangeException {
		LocalDate startDate = parseDateSafely(sDay, sMonth, sYear);
		LocalDate dueDate = parseDateSafely(dDay, dMonth, dYear);
		
		if (!startDate.isBefore(dueDate)) {
			throw new InvalidDateRangeException("NgÃ y báº¯t Ä‘áº§u pháº£i nhá»� hÆ¡n ngÃ y káº¿t thÃºc!");
		}
	}
	
	public static void validateDate(String day, String month, String year) throws DateTimeException {
		int d = Integer.parseInt(day);
		int m = Integer.parseInt(month);
		int y = Integer.parseInt(year);
		
		LocalDate.of(y, m, d);
	}
	
	public static String formatCurrency(int amount) {
	    return String.format("%,d", amount).replace(',', '.');
	}
	
	public static int parseCurrency(String input) throws NumberFormatException {
		if (input == null || input.isEmpty()) return 0;

		String plainNumber = input.replace(".", "").trim();
		return Integer.parseInt(plainNumber);
	   
	}
	
	public static void printAllHousehold() throws ServiceException {
		System.out.println("-------------------------------------------");
		List<Household> households = householdService.getAllHouseholds();
		for (Household h : households) System.out.println(h.toString());
	}
	
	public static void printAllMember() throws ServiceException {
		System.out.println("-------------------------------------------");
		List<Resident> members = memberService.getAll();
		for (Resident m : members) System.out.println(m.toString());
	}
	
	public static Household mapResultSetToHousehold(ResultSet rs) throws SQLException {
	    Household household = new Household();
	    household.setId(rs.getInt("id"));
	    household.setHouseNumber(rs.getString("house_number"));
	    household.setStreet(rs.getString("street"));
	    household.setDistrict(rs.getString("district"));
	    household.setWard(rs.getString("ward"));
	    
	    float area = rs.getFloat("area");
	    if (rs.wasNull()) {
	        household.setArea(null);
	    } else {
	        household.setArea(area);
	    }

	    household.setHouseholdSize(rs.getInt("household_size"));
	    household.setOwnerId(rs.getInt("owner_id"));
	    household.setOwnerName(rs.getString("owner_name"));
	    household.setPhone(rs.getString("phone"));
	    household.setEmail(rs.getString("email"));
	    
	    Date creationDate = rs.getDate("creation_date");
	    if (creationDate != null) {
	        household.setCreationDate(creationDate.toLocalDate());
	    } else {
	        household.setCreationDate(null);
	    }

	    return household;
	}

	public static void setHouseholdData(PreparedStatement stmt, Household household) throws SQLException {
	    stmt.setString(1, household.getHouseNumber());
	    stmt.setString(2, household.getStreet());
	    stmt.setString(3, household.getDistrict());
	    stmt.setString(4, household.getWard());
	    if (household.getArea() != null) {
	        stmt.setFloat(5, household.getArea());
	    } else {
	        stmt.setNull(5, Types.FLOAT);
	    }
	    stmt.setInt(6, household.getHouseholdSize());
	    stmt.setInt(7, household.getOwnerId());
	    stmt.setString(8, household.getOwnerName());
	    stmt.setString(9, household.getPhone());
	    stmt.setString(10, household.getEmail());
	    stmt.setDate(11, Date.valueOf(household.getCreationDate()));
	}
	
	public static Resident mapResultSetToResident(ResultSet rs) throws SQLException {
        return new Resident(
        	rs.getInt("id"),
            rs.getString("full_name"),
            rs.getDate("date_of_birth").toLocalDate(),
            Gender.valueOf(rs.getString("gender").toUpperCase()),
            rs.getString("ethnicity"),
            
            
            
            
            
            
            
            rs.getString("religion"),
            rs.getString("citizen_id"),
            rs.getDate("date_of_issue").toLocalDate(),
            rs.getString("place_of_issue"),
            RelationshipType.valueOf(rs.getString("relationship").toUpperCase()),
            rs.getString("occupation"),
            rs.getDate("added_date").toLocalDate(),
            rs.getInt("household_id"),
            rs.getBoolean("is_household_head")
        );
    }

    public static void setResidentData(PreparedStatement stmt, Resident resident) throws SQLException {
    stmt.setString(2, resident.getFullName());
    stmt.setDate(3, Date.valueOf(resident.getDateOfBirth()));
    stmt.setString(4, resident.getGender().toString());
    stmt.setString(5, resident.getEthnicity());
    stmt.setString(6, resident.getReligion());
    stmt.setString(7, resident.getCitizenId());
    stmt.setDate(8, Date.valueOf(resident.getDateOfIssue()));
    stmt.setString(9, resident.getPlaceOfIssue());
    stmt.setString(10, resident.getRelationship().toString());
    stmt.setString(11, resident.getOccupation());
    stmt.setDate(12, Date.valueOf(resident.getAddedDate()));
    stmt.setInt(13, resident.getHouseholdId());
    stmt.setBoolean(14, resident.isHouseholdHead());
}
	
	
}
