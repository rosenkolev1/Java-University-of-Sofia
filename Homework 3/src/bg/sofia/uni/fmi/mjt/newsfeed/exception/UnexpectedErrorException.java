package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class UnexpectedErrorException extends RequestException {

    public UnexpectedErrorException(String message) {
        super(message);
    }

    public UnexpectedErrorException(String message, Throwable e) {
        super(message, e);
    }
}
