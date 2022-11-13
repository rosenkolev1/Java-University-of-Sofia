package bg.sofia.uni.fmi.mjt.smartfridge.storable.comparators;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.util.Comparator;

public class SortByExpirationDate implements Comparator<Storable> {

    @Override
    public int compare(Storable o1, Storable o2) {
        if (o1.getExpiration().isBefore(o2.getExpiration())) return -1;
        else if (o1.getExpiration().isEqual(o2.getExpiration())) return 0;

        return 1;
    }
}