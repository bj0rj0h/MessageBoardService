package se.bjorjoh.ErrorHandling;

public class MissingMessageException extends Exception {
    public MissingMessageException() {
        super();
    }
    public MissingMessageException(String message, Throwable cause) {
        super(message, cause);
    }
    public MissingMessageException(String message) {
        super(message);
    }
    public MissingMessageException(Throwable cause) {
        super(cause);
    }
}
