package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.scene.image.ImageView;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import models.Household;
import models.Resident;
import services.HouseholdService;
import services.HouseholdServiceImpl;
import services.MemberService;
import services.MemberServiceImpl;
import javafx.animation.Interpolator;
import java.time.LocalDate;
import java.util.List;
import java.time.DateTimeException;
import exception.InvalidDateRangeException;
import exception.ServiceException;

public class Utils {
	private final static HouseholdServiceImpl householdService = new HouseholdServiceImpl();
	private final static MemberServiceImpl memberService = new MemberServiceImpl();
	
	
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
	
	
}
