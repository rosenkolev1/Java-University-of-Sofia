package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class UnauthorizedException extends RequestException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable e) {
        super(message, e);
    }
}
