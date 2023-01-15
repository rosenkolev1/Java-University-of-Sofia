package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class RequestException extends Exception {
    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, Throwable e) {
        super(message, e);
    }
}
