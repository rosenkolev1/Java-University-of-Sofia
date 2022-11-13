package bg.sofia.uni.fmi.mjt.smartfridge.recipe;

import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.util.HashSet;
import java.util.Set;

public class BaseRecipe implements Recipe {

    private Set<Ingredient<? extends Storable>> ingredients;

    public BaseRecipe() {
        this(new HashSet<Ingredient<? extends Storable>>());
    }

    public BaseRecipe(Set<Ingredient<? extends Storable>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public Set<Ingredient<? extends Storable>> getIngredients() {
        return this.ingredients;
    }

    @Override
    public void addIngredient(Ingredient<? extends Storable> ingredient) {
        if (ingredient == null) throw new IllegalArgumentException("The ingredient is null!");

        boolean ingredientExists = false;

        for (Ingredient<? extends Storable> curIngredient : this.ingredients) {
            if (curIngredient.item().getName().equals(ingredient.item().getName())) {
                this.ingredients.remove(curIngredient);
                curIngredient =
                    new DefaultIngredient(curIngredient.item(), curIngredient.quantity() + ingredient.quantity());
                this.ingredients.add(curIngredient);
                ingredientExists = true;
                break;
            }
        }

        if (!ingredientExists) this.ingredients.add(ingredient);
    }

    @Override
    public void removeIngredient(String itemName) {
        if (itemName == null || itemName.isBlank())
            throw new IllegalArgumentException("Item name is null, empty or blank");

        for (Ingredient<? extends Storable> curIngredient : this.ingredients) {
            if (curIngredient.item().getName().equals(itemName)) {
                this.ingredients.remove(curIngredient);
                break;
            }
        }
    }
}
