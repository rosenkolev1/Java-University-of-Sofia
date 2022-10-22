package bg.sofia.uni.fmi.mjt.airbnb.filter;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class LocationCriterion implements Criterion{

    private Location locationComparator;

    public LocationCriterion(Location location){
        this.locationComparator = new Location(location);
    }

    @Override
    public boolean check(Bookable bookable) {
        if(bookable == null) return false;

        return bookable.getLocation().equalLocations(this.locationComparator);
    }
}
