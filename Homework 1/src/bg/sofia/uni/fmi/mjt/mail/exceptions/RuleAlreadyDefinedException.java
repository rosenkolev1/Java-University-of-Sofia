package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class RuleAlreadyDefinedException extends Exception{

    public RuleAlreadyDefinedException(String errorMessage) {
        super(errorMessage);
    }

    public RuleAlreadyDefinedException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
