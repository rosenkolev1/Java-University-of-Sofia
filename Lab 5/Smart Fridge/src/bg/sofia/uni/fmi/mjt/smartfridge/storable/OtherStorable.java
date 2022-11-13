package bg.sofia.uni.fmi.mjt.smartfridge.storable;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.type.StorableType;

import java.time.LocalDate;

public class OtherStorable extends StorableItem {

    public OtherStorable(String name, LocalDate expirationDate) {
        super(StorableType.OTHER, name, expirationDate);
    }

}
