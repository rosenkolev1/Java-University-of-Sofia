package bg.sofia.uni.fmi.mjt.airbnb.filter;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;

public class PriceCriterion implements Criterion{
    private double priceComparator;

    public PriceCriterion(double price){
        this.priceComparator = price;
    }

    @Override
    public boolean check(Bookable bookable) {
        return bookable.getTotalPriceOfStay() < this.priceComparator;
    }
}
