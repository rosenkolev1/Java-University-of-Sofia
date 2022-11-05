package bg.sofia.uni.fmi.mjt.airbnb.filter;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class LocationCriterion implements Criterion{

    private Location currentLocation;
    private double maxDistance;

    public LocationCriterion(Location location, double maxDistance){
        this.currentLocation = location;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean check(Bookable bookable) {
        if(bookable == null) return false;

        Location bookableLoc = bookable.getLocation();
        return bookableLoc.calculateDistance(this.currentLocation) <= this.maxDistance;
    }
}
