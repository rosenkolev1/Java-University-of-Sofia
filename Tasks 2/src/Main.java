import bg.sofia.uni.fmi.mjt.airbnb.Airbnb;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Apartment;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Hotel;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Villa;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;
import bg.sofia.uni.fmi.mjt.airbnb.filter.Criterion;
import bg.sofia.uni.fmi.mjt.airbnb.filter.LocationCriterion;
import bg.sofia.uni.fmi.mjt.airbnb.filter.PriceCriterion;

import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {

        Location hotelHemus = new Location(10, 30);
        Location apartmentVazov = new Location(55, 7);
        Location villaJivot = new Location(23, 18);
        Hotel hotelHemusAccommodation = new Hotel(hotelHemus, 40);
        Apartment apartmentVazovAccommodation = new Apartment(apartmentVazov, 100);
        Villa villaJivotAccommodation = new Villa(villaJivot, 18);

        Bookable[] accommodations = new Bookable[]{
                hotelHemusAccommodation,
                apartmentVazovAccommodation,
                villaJivotAccommodation
        };

        Airbnb shitCompany = new Airbnb(accommodations);

        System.out.println("Get the hotelHemus accommodation by Id " +
                shitCompany.findAccommodationById("HOT-1").getLocation());
        System.out.println("Get the apartmentVazov accommodation by Id " +
                shitCompany.findAccommodationById("APA-1").getLocation());
        System.out.println("Get the villaJivot accommodation by Id " +
                shitCompany.findAccommodationById("VIL-1").getLocation());

        System.out.println("Fail get by ID because id does not exist " +
                (shitCompany.findAccommodationById("HOT-2") == null ? "NULL" : "NOT NULL"));
        System.out.println("Fail get by ID because id does not exist " +
                (shitCompany.findAccommodationById(null) == null ? "NULL" : "NOT NULL"));
        System.out.println("Fail get by ID because id does not exist " +
                (shitCompany.findAccommodationById("") == null ? "NULL" : "NOT NULL"));

        System.out.println();

        //Try changing Bookables outside of Airbnb.Bookables
        hotelHemusAccommodation.book(
                LocalDateTime.of(2023, Month.MARCH, 25, 14, 0, 0),
                LocalDateTime.of(2023, Month.MARCH, 31, 12, 0, 0));
        System.out.println("Changing Bookables should not change the Airbnb.Bookables. Expected FALSE --> " +
                (shitCompany.findAccommodationById("HOT-1").getTotalPriceOfStay() ==
                        hotelHemusAccommodation.getTotalPriceOfStay() ? "TRUE" : "FALSE"));

        System.out.println();

        //Try the estimateTotalRevenue
        shitCompany.findAccommodationById("VIL-1").book(
                LocalDateTime.of(2023, Month.MARCH, 25, 14, 0, 0),
                LocalDateTime.of(2023, Month.MARCH, 27, 12, 0, 0)
        );
        System.out.println("The estimated total revenue should be equal to 2*18=36: Expected TRUE --> " +
                (shitCompany.estimateTotalRevenue() == 36 ? "TRUE" : "FALSE"));
        System.out.println("The estimated bookings should be equal to 1: Expected TRUE --> " +
                (shitCompany.countBookings() == 1 ? "TRUE" : "FALSE"));

        System.out.println();
        System.out.println("{NOW BOOKING THE APARTMENT OF VAZOV FOR 4 DAYS}");
        System.out.println();

        shitCompany.findAccommodationById("APA-1").book(
                LocalDateTime.of(2022, Month.NOVEMBER, 1, 14, 0, 0),
                LocalDateTime.of(2022, Month.NOVEMBER, 5, 12, 0, 0)
        );
        System.out.println("The estimated total revenue should be equal to 2*18 + 4*100 = 436: Expected TRUE --> " +
                (shitCompany.estimateTotalRevenue() == 436 ? "TRUE" : "FALSE"));
        System.out.println("The estimated bookings should be equal to 2: Expected TRUE --> " +
                (shitCompany.countBookings() == 2 ? "TRUE" : "FALSE"));

        System.out.println();

        //Filter the Bookables by Location
        //TEST CASE 1
        Criterion locCritHemus = new LocationCriterion(hotelHemus);
        Bookable[] filteredBookables1 = shitCompany.filterAccommodations(locCritHemus);
        System.out.println("The filteredBookables1 should be of length 1 and have just the Hemus hotel accommodation: Expected TRUE, TRUE --> " +
                (filteredBookables1.length == 1 ? "TRUE" : "FALSE") + ", " +
                (filteredBookables1[0].getId().equals("HOT-1") ? "TRUE" : "FALSE"));

        //TEST CASE 2
        Criterion priceCrit = new PriceCriterion(50);
        Bookable[] filteredBookables2 = shitCompany.filterAccommodations(locCritHemus, priceCrit);
        System.out.println("The filteredBookables2 should be of length 1 and have just the Hemus hotel accommodation: Expected TRUE, TRUE --> " +
                (filteredBookables2.length == 1 ? "TRUE" : "FALSE") + ", " +
                (filteredBookables2[0].getId().equals("HOT-1") ? "TRUE" : "FALSE"));

        //TEST CASE 3
        Criterion locCritVazov = new LocationCriterion(apartmentVazov);
        Bookable[] filteredBookables3 = shitCompany.filterAccommodations(locCritVazov, priceCrit);
        System.out.println("The filteredBookables3 should be of length 0 and have no accommodations: Expected TRUE | " +
                (filteredBookables3.length == 0 ? "TRUE" : "FALSE"));

        //TEST CASE 4
        Criterion locCritJivot = new LocationCriterion(villaJivot);
        Bookable[] filteredBookables4 = shitCompany.filterAccommodations(locCritJivot, priceCrit);
        System.out.println("The filteredBookables4 should be of length 1 and have just the villa accommodation: Expected TRUE, TRUE --> " +
                (filteredBookables4.length == 1 ? "TRUE" : "FALSE") + ", " +
                (filteredBookables4[0].getId().equals("VIL-1") ? "TRUE" : "FALSE"));

        //TEST CASE 5
        Bookable[] filteredBookables5 = shitCompany.filterAccommodations(locCritVazov);
        System.out.println("The filteredBookables5 should be of length 1 and have just the apartment accommodation: Expected TRUE, TRUE --> " +
                (filteredBookables5.length == 1 ? "TRUE" : "FALSE") + ", " +
                (filteredBookables5[0].getId().equals("APA-1") ? "TRUE" : "FALSE"));

        //TEST CASE 6
        Bookable[] filteredBookables6 = shitCompany.filterAccommodations(priceCrit);
        System.out.println("The filteredBookables6 should be of length 2 and have the villa and hotel accommodations: Expected TRUE, TRUE, TRUE --> " +
                (filteredBookables6.length == 2 ? "TRUE" : "FALSE") + ", " +
                (filteredBookables6[0].getId().equals("HOT-1") ? "TRUE" : "FALSE")  + ", " +
                (filteredBookables6[1].getId().equals("VIL-1") ? "TRUE" : "FALSE"));

        //TEST CASE 7
        Bookable[] filteredBookables7 = shitCompany.filterAccommodations();
        System.out.println("The filteredBookables7 should be of length 3 and have all the accommodations: Expected TRUE, TRUE, TRUE, TRUE --> " +
                (filteredBookables7.length == 3 ? "TRUE" : "FALSE") + ", " +
                (filteredBookables7[0].getId().equals("HOT-1") ? "TRUE" : "FALSE")  + ", " +
                (filteredBookables7[1].getId().equals("APA-1") ? "TRUE" : "FALSE")  + ", " +
                (filteredBookables7[2].getId().equals("VIL-1") ? "TRUE" : "FALSE"));

        System.out.println();

        //Now lastly we test the locations
        System.out.println("Trying to book Hotel Hemus again should return false and not do anything: Expected FALSE --> " +
                (hotelHemusAccommodation.book(
                        LocalDateTime.of(2022, Month.NOVEMBER, 1, 14, 0, 0),
                        LocalDateTime.of(2022, Month.NOVEMBER, 5, 12, 0, 0)
                ) ? "TRUE" : "FALSE")); //Should not do anything, I.E return false

        System.out.println("Trying to book apartment Vazov should return false and not do anything because " +
                "the time of checking in is in the past: Expected FALSE --> " +
                (apartmentVazovAccommodation.book(
                        LocalDateTime.of(2022, Month.OCTOBER, 1, 14, 0, 0),
                        LocalDateTime.of(2022, Month.NOVEMBER, 5, 12, 0, 0)
                ) ? "TRUE" : "FALSE")); //Should not do anything, I.E return false

        System.out.println("Trying to book apartment Vazov should return false and not do anything because " +
                "the time of checking in is after the time of checking out: Expected FALSE --> " +
                (apartmentVazovAccommodation.book(
                        LocalDateTime.of(2022, Month.NOVEMBER, 1, 14, 0, 0),
                        LocalDateTime.of(2022, Month.NOVEMBER, 1, 12, 0, 0)
                ) ? "TRUE" : "FALSE")); //Should not do anything, I.E return false
    }
}