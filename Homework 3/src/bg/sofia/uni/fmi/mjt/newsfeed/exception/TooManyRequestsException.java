package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class TooManyRequestsException extends RequestException {
    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, Throwable e) {
        super(message, e);
    }
}
