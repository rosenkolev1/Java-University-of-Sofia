package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class BadRequestException extends RequestException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable e) {
        super(message, e);
    }
}
