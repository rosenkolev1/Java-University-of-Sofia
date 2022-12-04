package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class AccountNotFoundException extends Exception{

    public AccountNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public AccountNotFoundException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
