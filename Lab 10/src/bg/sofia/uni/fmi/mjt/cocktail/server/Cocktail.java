package bg.sofia.uni.fmi.mjt.cocktail.server;

import java.util.Set;

public record Cocktail(String name, Set<Ingredient> ingredients) {

    @Override
    public boolean equals(Object other) {

        if (other == this) {
            return true;
        }

        if (!(other instanceof Cocktail)) {
            return false;
        }

        Cocktail castOther = (Cocktail) other;

        return castOther.name.equals(this.name);
    }
}
