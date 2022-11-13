package bg.sofia.uni.fmi.mjt.escaperoom.exception;

public class RoomNotFoundException extends Exception{
    public RoomNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
