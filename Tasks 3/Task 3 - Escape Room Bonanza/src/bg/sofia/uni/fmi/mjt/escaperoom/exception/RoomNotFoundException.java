package bg.sofia.uni.fmi.mjt.escaperoom.exception;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
