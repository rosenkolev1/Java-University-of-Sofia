package bg.sofia.uni.fmi.mjt.escaperoom.exception;

public class RoomAlreadyExistsException extends RuntimeException {

    public RoomAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

}
