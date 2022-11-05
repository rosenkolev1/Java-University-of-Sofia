package bg.sofia.uni.fmi.mjt.flightscanner.passenger;

public record Passenger(String id, String name, Gender gender) {

    public Passenger {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("The id of the passenger is null or empty or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name of the passenger is null or empty or blank");
        }
    }

}
