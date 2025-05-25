package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.scene.image.ImageView;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import java.time.LocalDate;
import java.time.DateTimeException;
import exception.InvalidDateRangeException;

public class Utils {
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
			throw new InvalidDateRangeException("Ngày bắt đầu phải nhỏ hơn ngày kết thúc!");
		}
	}
	
	public static void validateDate(String day, String month, String year) throws DateTimeException {
		int d = Integer.parseInt(day);
		int m = Integer.parseInt(month);
		int y = Integer.parseInt(year);
		
		LocalDate.of(y, m, d);
	}
}
