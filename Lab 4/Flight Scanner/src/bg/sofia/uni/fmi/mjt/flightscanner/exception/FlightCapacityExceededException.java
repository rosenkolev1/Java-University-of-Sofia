package bg.sofia.uni.fmi.mjt.flightscanner.exception;

public class FlightCapacityExceededException extends Exception {

    public FlightCapacityExceededException(String message, Throwable e) {
        super(message, e);
    }

    public FlightCapacityExceededException(String message) {
        super(message);
    }

}
