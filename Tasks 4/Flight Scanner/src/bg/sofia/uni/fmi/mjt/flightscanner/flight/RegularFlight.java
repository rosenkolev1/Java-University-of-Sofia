package bg.sofia.uni.fmi.mjt.flightscanner.flight;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.FlightCapacityExceededException;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.InvalidFlightException;
import bg.sofia.uni.fmi.mjt.flightscanner.passenger.Passenger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RegularFlight implements Flight {

    String flightId;
    Airport from;
    Airport to;
    int totalCapacity;

    List<Passenger> passengers;

    private RegularFlight(String flightId, Airport from, Airport to, int totalCapacity) {
        this.flightId = flightId;
        this.from = from;
        this.to = to;
        this.totalCapacity = totalCapacity;
        this.passengers = new ArrayList<Passenger>();
    }

    private boolean exceedsCapacity(int newPassengersCount) {
        return newPassengersCount > this.getFreeSeatsCount();
    }

    public static RegularFlight of(String flightId, Airport from, Airport to, int totalCapacity) {

        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("The flight id is null or empty or blank");
        }

        if (from == null) throw new IllegalArgumentException("The from airport is null");
        if (to == null) throw new IllegalArgumentException("The to airport is null");
        if (totalCapacity < 0) throw new IllegalArgumentException("The capacity for the flight is negative");
        if (from.equals(to)) throw new InvalidFlightException("The from and to airports are the same");

        return new RegularFlight(flightId, from, to, totalCapacity);
    }

    @Override
    public Airport getFrom() {
        return this.from;
    }

    @Override
    public Airport getTo() {
        return this.to;
    }

    @Override
    public void addPassenger(Passenger passenger) throws FlightCapacityExceededException {
        if (exceedsCapacity(1)) {
            throw new FlightCapacityExceededException("The plane is full. Cannot accommodate new passenger");
        }

        this.passengers.add(passenger);
    }

    @Override
    public void addPassengers(Collection<Passenger> passengers) throws FlightCapacityExceededException {
        if (exceedsCapacity(passengers.size())) {
            throw new FlightCapacityExceededException("The plane is full. Cannot accommodate new passenger");
        }

        this.passengers.addAll(passengers);
    }

    @Override
    public Collection<Passenger> getAllPassengers() {
        return Collections.unmodifiableList(this.passengers);
    }

    @Override
    public int getFreeSeatsCount() {
        return this.totalCapacity - this.passengers.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (!(o instanceof RegularFlight)) {
            return false;
        }

        RegularFlight other = (RegularFlight) o;

        return this.flightId.equals(other.flightId);
    }
}
