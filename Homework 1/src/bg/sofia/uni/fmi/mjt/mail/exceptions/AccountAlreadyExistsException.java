package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    //Commenting for code coverage :)
//    public AccountAlreadyExistsException(String errorMessage, Throwable e) {
//        super(errorMessage, e);
//    }
}
