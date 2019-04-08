package se.bjorjoh.ErrorHandling;

public class UnauthorizedMessageAccessException extends Exception {

    public UnauthorizedMessageAccessException() {
        super();
    }
    public UnauthorizedMessageAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnauthorizedMessageAccessException(String message) {
        super(message);
    }
    public UnauthorizedMessageAccessException(Throwable cause) {
        super(cause);
    }
}
