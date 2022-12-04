package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class AccountAlreadyExistsException extends RuntimeException{

    public AccountAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    public AccountAlreadyExistsException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
