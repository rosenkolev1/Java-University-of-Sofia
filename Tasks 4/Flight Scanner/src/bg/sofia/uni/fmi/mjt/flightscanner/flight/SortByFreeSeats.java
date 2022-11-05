package bg.sofia.uni.fmi.mjt.flightscanner.flight;

import java.util.Comparator;

public class SortByFreeSeats implements Comparator<Flight> {
    @Override
    public int compare(Flight o1, Flight o2) {
        return (o1.getFreeSeatsCount() - o2.getFreeSeatsCount()) * (-1);
    }
}
