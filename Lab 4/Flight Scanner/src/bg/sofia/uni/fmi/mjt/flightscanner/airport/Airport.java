package bg.sofia.uni.fmi.mjt.flightscanner.airport;

import bg.sofia.uni.fmi.mjt.flightscanner.flight.RegularFlight;

public record Airport(String id) {

    public Airport {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("The id is null, empty or blank");
        }
    }
}
