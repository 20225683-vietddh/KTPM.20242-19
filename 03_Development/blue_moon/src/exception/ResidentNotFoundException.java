package exception;

public class ResidentNotFoundException extends Exception {
    public ResidentNotFoundException(String message) {
        super(message);
    }
    
    public ResidentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}