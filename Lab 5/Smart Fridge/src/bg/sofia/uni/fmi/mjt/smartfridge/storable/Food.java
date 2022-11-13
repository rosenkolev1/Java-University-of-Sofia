package bg.sofia.uni.fmi.mjt.smartfridge.storable;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.type.StorableType;

import java.time.LocalDate;

public class Food extends StorableItem {

    public Food(String name, LocalDate expirationDate) {
        super(StorableType.FOOD, name, expirationDate);
    }

}
