package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class Apartment extends BaseBookable{

    private static int instancesCount = 0;

    public Apartment(Location location, double pricePerNight){
        super(location, pricePerNight);
        Apartment.instancesCount++;
        this.id = "APA-" + Apartment.instancesCount;
    }


    public Apartment(Apartment other){
        super(other);
    }

    @Override
    public Apartment clone(){
        return new Apartment(this);
    }
}
