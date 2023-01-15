package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class InvalidURIException extends RuntimeException {
    public InvalidURIException(String message) {
        super(message);
    }

    public InvalidURIException(String message, Throwable e) {
        super(message, e);
    }
}
