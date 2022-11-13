package bg.sofia.uni.fmi.mjt.flightscanner.exception;

public class InvalidFlightException extends RuntimeException {

    public InvalidFlightException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidFlightException(String message) {
        super(message);
    }
}
