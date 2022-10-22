package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class Hotel extends BaseBookable{
    private static int instancesCount = 0;

    public Hotel(Location location, double pricePerNight){
        super(location, pricePerNight);
        Hotel.instancesCount++;
        this.id = "HOT-" + Hotel.instancesCount;
    }

    public Hotel(Hotel other){
        super(other);
    }

    @Override
    public Hotel clone(){
        return new Hotel(this);
    }
}
