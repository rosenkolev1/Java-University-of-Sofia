package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class Villa extends BaseBookable{
    private static int instancesCount = 0;

    public Villa(Location location, double pricePerNight){
        super(location, pricePerNight);

        this.id = "VIL-" + Villa.instancesCount;
        Villa.instancesCount++;
    }

    public Villa(Villa other){
        super(other);
    }

    @Override
    public Bookable copySelf(){
        return new Villa(this);
    }
}
