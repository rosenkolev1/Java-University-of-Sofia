package bg.sofia.uni.fmi.mjt.airbnb;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.filter.Criterion;

public class Airbnb implements AirbnbAPI{

    private Bookable[] accommodations;

    public Airbnb(Bookable[] accommodations){
//        this.accommodations = new Bookable[accommodations.length];
//        for (int i = 0; i < accommodations.length; i++) {
//            this.accommodations[i] = accommodations[i]; //.copySelf();
//        }
        this.accommodations = accommodations;
    }

    @Override
    public Bookable findAccommodationById(String id) {

        if(id == null) return null;

        for(Bookable accommodation: this.accommodations){
            if(accommodation.getId().equals(id.toUpperCase())) return accommodation;
        }

        return null;
    }

    @Override
    public double estimateTotalRevenue() {
        double totalRevenue = 0;

        for (Bookable accommodation: this.accommodations) {
            totalRevenue += accommodation.isBooked() ? accommodation.getTotalPriceOfStay() : 0;
        }

        return totalRevenue;
    }

    @Override
    public long countBookings() {
        long bookedCount = 0;

        for (Bookable accommodation: this.accommodations) {
            bookedCount += accommodation.isBooked() ? 1 : 0;
        }

        return bookedCount;
    }

    @Override
    public Bookable[] filterAccommodations(Criterion... criteria) {
        var filteredBookables = new Bookable[this.accommodations.length];
        int filteredBookablesCount = 0;

        for (Bookable accommodation: this.accommodations) {
            boolean meetsAllCriteria = true;

            //Check for all criteria
            for(Criterion singleCrit: criteria){
                if(!singleCrit.check(accommodation)) {
                    meetsAllCriteria = false;
                    break;
                }
            }

            if(meetsAllCriteria) filteredBookables[filteredBookablesCount++] = accommodation;
        }

        //Return a filteredBookables with the correct count of elements
        var filteredBookablesWithCorrectSize = new Bookable[filteredBookablesCount];
        for (int i = 0; i < filteredBookablesCount; i++) {
            filteredBookablesWithCorrectSize[i] = filteredBookables[i];
        }

        return filteredBookablesWithCorrectSize;
    }
}
