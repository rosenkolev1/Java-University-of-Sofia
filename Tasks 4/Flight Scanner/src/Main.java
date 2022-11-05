import bg.sofia.uni.fmi.mjt.flightscanner.FlightScanner;
import bg.sofia.uni.fmi.mjt.flightscanner.FlightScannerAPI;
import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.FlightCapacityExceededException;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.Flight;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.RegularFlight;
import bg.sofia.uni.fmi.mjt.flightscanner.passenger.Gender;
import bg.sofia.uni.fmi.mjt.flightscanner.passenger.Passenger;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args)
    throws FlightCapacityExceededException {

        //Nodes of graph
        Airport sofia = new Airport("Sofia");
        Airport varna = new Airport("Varna");
        Airport burgas = new Airport("Burgas");
        Airport stz = new Airport("Stara Zagora");
        Airport plovdiv = new Airport("Plovdiv");
        Airport sliven = new Airport("Sliven");
        Airport ruse = new Airport("Ruse");
        Airport pleven = new Airport("Pleven");
        Airport vidin = new Airport("Vidin");
        Airport bansko = new Airport("Bansko");

        Passenger[] flight1Pas = new Passenger[]{
                new Passenger("0", "Rosen", Gender.MALE),
                new Passenger("1", "Tonito", Gender.MALE),
                new Passenger("2", "Vasko", Gender.MALE),
                new Passenger("3", "Bili", Gender.MALE),
                new Passenger("4", "Pesho", Gender.MALE),
                new Passenger("5", "Ge6a", Gender.MALE),
        };

        Passenger[] flight2Pas = new Passenger[]{
                new Passenger("6", "Beti", Gender.MALE),
                new Passenger("7", "Rado", Gender.MALE),
                new Passenger("8", "Kiro", Gender.MALE),
                new Passenger("9", "Dankata", Gender.MALE),
                new Passenger("10", "Lubo", Gender.MALE),
        };

        Flight flight1 = RegularFlight.of("Fl1", sofia, varna, 8);
        flight1.addPassengers(Arrays.asList(flight1Pas));

        Flight flight2 = RegularFlight.of("Fl2", sofia, burgas, 5);
        flight2.addPassengers(Arrays.asList(flight2Pas));

        Flight flight3 = RegularFlight.of("Fl3", varna, stz, 60);
        Flight flight4 = RegularFlight.of("Fl4", varna, plovdiv, 60);
        Flight flight5 = RegularFlight.of("Fl5", stz, plovdiv, 60);
        Flight flight6 = RegularFlight.of("Fl6", plovdiv, varna, 60);
        Flight flight7 = RegularFlight.of("Fl7", plovdiv, sliven, 60);
        Flight flight8 = RegularFlight.of("Fl8", burgas, ruse, 60);
        Flight flight9 = RegularFlight.of("Fl9", pleven, vidin, 60);
        Flight flight10 = RegularFlight.of("Fl10", vidin, burgas, 60);

        FlightScannerAPI flScanner = new FlightScanner();
        List<Flight> searchRes1 = flScanner.searchFlights(sofia, varna); //--> Empty array

        flScanner.add(flight1);
        flScanner.add(flight2);
        List<Flight> searchRes2 = flScanner.searchFlights(sofia, varna); //--> Sofia --> Varna

        flScanner.add(flight3);
        flScanner.add(flight3);
        flScanner.add(flight4);
        flScanner.add(flight5);
        flScanner.add(flight6);
        flScanner.add(flight7);
        flScanner.addAll(Arrays.asList(new Flight[] {flight8, flight9, flight10}));
        flScanner.add(flight8);
        flScanner.add(flight9);
        flScanner.add(flight10);

        List<Flight> searchRes3 = flScanner.searchFlights(sofia, plovdiv); //--> Sofia --> Varna --> Plovdiv
        List<Flight> searcRes4 = flScanner.searchFlights(sofia, sliven); //--> Sofia --> Varna --> Plovdiv --> Sliven
        List<Flight> searcRes5 = flScanner.searchFlights(pleven, ruse); //--> Pleven --> Vidin --> Burgas --> Ruse
        List<Flight> searcRes6 = flScanner.searchFlights(ruse, pleven); //--> Empty array
        List<Flight> searcRes7 = flScanner.searchFlights(ruse, bansko); //--> Empty array
        List<Flight> searcRes8 = flScanner.searchFlights(bansko, ruse); //--> Empty array

        List<Flight> sortedLexicographicallyByDestination1 = flScanner.getFlightsSortedByDestination(sofia);
        List<Flight> sortedLexicographicallyByDestination4 = flScanner.getFlightsSortedByDestination(plovdiv);
        List<Flight> sortedLexicographicallyByDestination2 = flScanner.getFlightsSortedByDestination(burgas);
        List<Flight> sortedLexicographicallyByDestination3 = flScanner.getFlightsSortedByDestination(bansko);

        List<Flight> sortedByEmptySeats1 = flScanner.getFlightsSortedByFreeSeats(sofia);
        List<Flight> sortedByEmptySeats2 = flScanner.getFlightsSortedByFreeSeats(plovdiv);
        List<Flight> sortedByEmptySeats3 = flScanner.getFlightsSortedByDestination(burgas);
        List<Flight> sortedByEmptySeats4 = flScanner.getFlightsSortedByFreeSeats(bansko);

        flight1.addPassengers(Arrays.asList(new Passenger[]{
                new Passenger("11", "Miro", Gender.MALE),
                new Passenger("12", "Rumkata", Gender.MALE),
                new Passenger("13", "Gagata", Gender.MALE),
        }));

        flight2.addPassenger(new Passenger("14", "Na4kata", Gender.MALE));
    }
}