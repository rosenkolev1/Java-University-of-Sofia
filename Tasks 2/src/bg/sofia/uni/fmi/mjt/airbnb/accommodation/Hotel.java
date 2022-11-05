package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class Hotel extends BaseBookable{
    private static int instancesCount = 0;

    public Hotel(Location location, double pricePerNight){
        super(location, pricePerNight);
        this.id = "HOT-" + Hotel.instancesCount;
        Hotel.instancesCount++;
    }

    public Hotel(Hotel other){
        super(other);
    }

    @Override
    public Bookable copySelf(){
        return new Hotel(this);
    }
}
