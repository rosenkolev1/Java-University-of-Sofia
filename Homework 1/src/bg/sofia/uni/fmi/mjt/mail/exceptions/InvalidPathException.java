package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class InvalidPathException extends RuntimeException {

    public InvalidPathException(String errorMessage) {
        super(errorMessage);
    }

    //Commenting for code coverage :)
//    public InvalidPathException(String errorMessage, Throwable e) {
//        super(errorMessage, e);
//    }
}
