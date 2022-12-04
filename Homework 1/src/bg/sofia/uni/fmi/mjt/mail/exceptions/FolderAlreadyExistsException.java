package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderAlreadyExistsException extends Exception{

    public FolderAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    public FolderAlreadyExistsException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
