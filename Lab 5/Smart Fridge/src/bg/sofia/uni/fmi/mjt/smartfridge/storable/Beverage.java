package bg.sofia.uni.fmi.mjt.smartfridge.storable;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.type.StorableType;

import java.time.LocalDate;

public class Beverage extends StorableItem {

    public Beverage(String name, LocalDate expirationDate) {
        super(StorableType.BEVERAGE, name, expirationDate);
    }

}
