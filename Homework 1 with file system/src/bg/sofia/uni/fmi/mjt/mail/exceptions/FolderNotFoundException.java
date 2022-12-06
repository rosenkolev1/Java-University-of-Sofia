package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class FolderNotFoundException extends RuntimeException{

    public FolderNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public FolderNotFoundException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
