package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderAlreadyExistsException extends RuntimeException {

    public FolderAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    //Commenting for code coverage :)
//    public FolderAlreadyExistsException(String errorMessage, Throwable e) {
//        super(errorMessage, e);
//    }
}
