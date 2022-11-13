package bg.sofia.uni.fmi.mjt.flightscanner;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.Flight;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.SortByDestinationId;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.SortByFreeSeats;

import java.util.*;

public class FlightScanner implements FlightScannerAPI {

    List<Flight> flights;

    public FlightScanner() {
        this.flights = new ArrayList<Flight>();
    }

    private List<Flight> filterFlightsByStartAirport(Airport from) {
        if (from == null) throw new IllegalArgumentException("The from airport is null");

        List<Flight> flightsFiltered = new ArrayList<Flight>();

        for (Flight fl : this.flights) {
            //(fl.getFrom().equals(from))
            if  (fl.getFrom().id().equals(from.id())) flightsFiltered.add(fl);
        }

        return flightsFiltered;
    }

    @Override
    public void add(Flight flight) {
        if (flight == null) throw new IllegalArgumentException("The flight is null");

        if (flights.contains(flight)) return;

        flights.add(flight);
    }

    @Override
    public void addAll(Collection<Flight> flights) {
        if (flights == null) throw new IllegalArgumentException("The flight is null");

        for (Flight fl : flights) {
            if (!this.flights.contains(fl)) this.add(fl);
        }
    }

    @Override
    public List<Flight> searchFlights(Airport from, Airport to) {
        if (from == null) throw new IllegalArgumentException("The starting airport is null");
        if (to == null) throw new IllegalArgumentException("The destination airport is null");
        if (from.equals(to)) {
            throw new IllegalArgumentException("The starting and destination airports are the same");
        }

        //Store all of the flights that start from some airport and finish in other airports
        HashMap<Airport, List<Flight>> graph = new HashMap<Airport, List<Flight>>();

        for (Flight fl : this.flights) {
            Airport curFrom = fl.getFrom();
            if (graph.containsKey(curFrom)) graph.get(curFrom).add(fl);
            else {
                List<Flight> curFlights = new ArrayList<Flight>();
                curFlights.add(fl);
                graph.put(curFrom, curFlights);
            }
        }

        //Does BFS of the graph
        Queue<Airport> visitingQueue = new LinkedList<Airport>();
        //Stores the flight path for reaching a node
        Queue<List<Flight>> flightPaths = new LinkedList<List<Flight>>();
        //Stores the already visited nodes
        List<Airport> visitedNodes = new ArrayList<Airport>();

        //Add the starting airport to the visitingQueue
        visitingQueue.add(from);
        visitedNodes.add(from);
        flightPaths.add(new ArrayList<Flight>());

        List<Flight> correctFlightPath = new ArrayList<Flight>();

        //Do the  BFS
        boolean foundPath = false;
        while (!visitingQueue.isEmpty() && !foundPath) {
            List<Flight> flightsFromAp = graph.get(visitingQueue.peek());
            visitingQueue.remove();
            List<Flight> oldFlightPath = flightPaths.peek();

            flightPaths.remove();

            //In this case, there are no flights starting from the current airport,
            // so we just continue into the other nodes
            if (flightsFromAp == null) continue;
            //Add all the new nodes to the queue for going through them
            // and also check and update the path to the node for going in circles and for output later
            for (Flight fl : flightsFromAp) {
                Airport destination = fl.getTo();
                //Check if the new destination is the desired one. If so, we have the path at the head of the queue
                // of the flightPaths
                if (destination.equals(to)) {
                    correctFlightPath = oldFlightPath;
                    correctFlightPath.add(fl);
                    foundPath = true;
                    break;
                }
                //Check if the destination airport has already been visited before
                else if (!visitedNodes.contains(destination)) {
                    List<Flight> oldFlightPathCopy = new ArrayList<Flight>();
                    //Make copy of the old flight path so that the original is not altered
                    for (Flight oldFlight : oldFlightPath) {
                        oldFlightPathCopy.add(oldFlight);
                    }
                    visitingQueue.add(destination);
                    oldFlightPathCopy.add(fl);
                    flightPaths.add(oldFlightPathCopy);
                }
            }
        }

        //If this is true, we have found the correct path
        return correctFlightPath;
    }

    @Override
    public List<Flight> getFlightsSortedByFreeSeats(Airport from) {

        List<Flight> flightsFiltered = this.filterFlightsByStartAirport(from);

        flightsFiltered.sort(new SortByFreeSeats());
        return Collections.unmodifiableList(flightsFiltered);
    }

    @Override
    public List<Flight> getFlightsSortedByDestination(Airport from) {

        List<Flight> flightsFiltered = this.filterFlightsByStartAirport(from);

        Collections.sort(flightsFiltered, new SortByDestinationId());
        return Collections.unmodifiableList(flightsFiltered);
    }
}
