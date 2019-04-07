package se.bjorjoh.ErrorHandling;

public class AuthorizationException extends RuntimeException{


    public AuthorizationException() {
        super();
    }
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
    public AuthorizationException(String message) {
        super(message);
    }
    public AuthorizationException(Throwable cause) {
        super(cause);
    }

}
