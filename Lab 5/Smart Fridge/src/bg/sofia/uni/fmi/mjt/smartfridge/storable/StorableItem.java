package bg.sofia.uni.fmi.mjt.smartfridge.storable;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.type.StorableType;

import java.time.LocalDate;

public abstract class StorableItem implements Storable {

    StorableType type;
    String name;
    LocalDate expirationDate;

    protected StorableItem(StorableType type, String name, LocalDate expirationDate) {
        this.type = type;
        this.name = name;
        this.expirationDate = expirationDate;
    }

    @Override
    public StorableType getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LocalDate getExpiration() {
        return this.expirationDate;
    }

    @Override
    public boolean isExpired() {
        return this.expirationDate.isBefore(LocalDate.now());
    }

//    @Override
//    public boolean equals(Object o) {
//
//        // If the object is compared with itself then return true
//        if (o == this) {
//            return true;
//        }
//
//        /* Check if o is an instance of Complex or not
//          "null instanceof [type]" also returns false */
//        if (!(o instanceof StorableItem)) {
//            return false;
//        }
//
//        // typecast o to Complex so that we can compare data members
//        StorableItem c = (StorableItem) o;
//
//        // Compare the data members and return accordingly
//        return this.name.equals(c.getName());
//    }
//
//    @Override
//    public int hashCode()
//    {
//        return this.name.hashCode();
//    }
}
