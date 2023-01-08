package bg.sofia.uni.fmi.mjt.cocktail.server;

public record Ingredient(String name, String amount) {

    @Override
    public boolean equals(Object other) {

        if (other == this) {
            return true;
        }

        if (!(other instanceof Ingredient)) {
            return false;
        }

        Ingredient castOther = (Ingredient) other;

        return castOther.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
