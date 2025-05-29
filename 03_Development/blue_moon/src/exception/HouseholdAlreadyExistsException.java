package exception;

public class HouseholdAlreadyExistsException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HouseholdAlreadyExistsException(String message) {
        super(message);
    }
}