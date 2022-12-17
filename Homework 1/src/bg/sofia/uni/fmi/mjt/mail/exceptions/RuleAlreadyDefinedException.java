package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class RuleAlreadyDefinedException extends RuntimeException {

    public RuleAlreadyDefinedException(String errorMessage) {
        super(errorMessage);
    }

    //Commenting for code coverage :)
//    public RuleAlreadyDefinedException(String errorMessage, Throwable e) {
//        super(errorMessage, e);
//    }
}
