package exception;

public class InvalidHouseholdDataException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidHouseholdDataException(String message) {
        super(message);
    }
}