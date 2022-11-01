package bg.sofia.uni.fmi.mjt.escaperoom.exception;

public class TeamNotFoundException extends RuntimeException{
    public TeamNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
