package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class BaseBookable implements Bookable{

    protected String id = null;
    protected boolean booked = false;
    protected LocalDateTime checkIn = null;
    protected LocalDateTime checkOut = null;
    protected Location location;
    protected double pricePerNight;

    protected BaseBookable(Location location, double pricePerNight){
        this.location = new Location(location);
        this.pricePerNight = pricePerNight;
    }

    protected BaseBookable(BaseBookable other){
        this.location = new Location(other.getLocation());
        this.pricePerNight = other.getPricePerNight();
        this.id = other.getId();
        this.booked = other.isBooked();
        this.checkIn = other.checkIn;
        this.checkOut = other.checkOut;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean isBooked() {
        return this.booked;
    }

    @Override
    public boolean book(LocalDateTime checkIn, LocalDateTime checkOut) {
        if(this.isBooked() || checkIn.isBefore(LocalDateTime.now()) || checkIn.isAfter(checkOut) )
            return false;

        this.booked = true;
        this.checkIn = checkIn;
        this.checkOut = checkOut;

        return true;
    }

    @Override
    public double getTotalPriceOfStay() {
        return this.isBooked() ? (ChronoUnit.DAYS.between(this.checkIn.toLocalDate(), this.checkOut.toLocalDate()) * this.getPricePerNight()) : 0;
    }

    @Override
    public double getPricePerNight() {
        return this.pricePerNight;
    }

    @Override
    public Bookable copySelf(){
        return new BaseBookable(this){};
    }
}
