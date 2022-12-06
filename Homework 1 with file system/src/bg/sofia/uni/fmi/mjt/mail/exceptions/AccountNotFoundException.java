package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public AccountNotFoundException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
