package se.bjorjoh.ErrorHandling;

public class JwtFormatException extends Exception {

    public JwtFormatException() {
        super();
    }
    public JwtFormatException(String message, Throwable cause) {
        super(message, cause);
    }
    public JwtFormatException(String message) {
        super(message);
    }
    public JwtFormatException(Throwable cause) {
        super(cause);
    }

}
