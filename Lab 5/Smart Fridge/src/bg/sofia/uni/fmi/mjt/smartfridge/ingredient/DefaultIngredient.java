package bg.sofia.uni.fmi.mjt.smartfridge.ingredient;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

public record DefaultIngredient<E extends Storable>(E item, int quantity) implements Ingredient<E> {

    /**
     * Gets the item of the ingredient.
     */
    @Override
    public E item() {
        return this.item;
    }

    /**
     * Gets the quantity of the ingredient.
     */
    @Override
    public int quantity() {
        return this.quantity;
    }

}