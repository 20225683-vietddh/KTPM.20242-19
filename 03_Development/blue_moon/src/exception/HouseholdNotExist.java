package exception;

public class HouseholdNotExist extends Exception {
	private static final long serialVersionUID = 1L;

	public HouseholdNotExist(String message) {
		super(message);
	}
}
