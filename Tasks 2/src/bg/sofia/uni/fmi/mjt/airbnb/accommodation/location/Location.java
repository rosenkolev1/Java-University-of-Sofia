package bg.sofia.uni.fmi.mjt.airbnb.accommodation.location;

public class Location {
    public double x;
    public double y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Location(Location other){
        this.x = other.x;
        this.y = other.y;
    }


    public boolean equalLocations(Location other){
        return this.x == other.x && this.y == other.y;
    }
}
