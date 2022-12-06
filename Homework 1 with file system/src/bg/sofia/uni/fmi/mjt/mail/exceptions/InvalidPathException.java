package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class InvalidPathException extends RuntimeException{

    public InvalidPathException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidPathException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
