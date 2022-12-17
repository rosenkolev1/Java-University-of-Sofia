package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderNotFoundException extends RuntimeException {

    public FolderNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    //Commenting for code coverage :)
//    public FolderNotFoundException(String errorMessage, Throwable e) {
//        super(errorMessage, e);
//    }
}
