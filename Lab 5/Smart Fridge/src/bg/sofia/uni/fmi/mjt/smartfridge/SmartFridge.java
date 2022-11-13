package bg.sofia.uni.fmi.mjt.smartfridge;

import bg.sofia.uni.fmi.mjt.smartfridge.exception.FridgeCapacityExceededException;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.InsufficientQuantityException;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.StorableItem;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.comparators.SortByExpirationDate;

import java.lang.reflect.Array;
import java.util.*;

public class SmartFridge implements SmartFridgeAPI {

    private int totalCapacity;
    private List<Storable> items;

    public SmartFridge(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        this.items = new ArrayList<Storable>(totalCapacity);
    }

    @Override
    public <E extends Storable> void store(E item, int quantity) throws FridgeCapacityExceededException {
        if (item == null) throw new IllegalArgumentException("The item is null!");
        if (quantity <= 0) throw new IllegalArgumentException("The quantity is not positive!");
        if (this.items.size() + quantity > this.totalCapacity) {
            throw new FridgeCapacityExceededException("The fridge lacks the capacity for the items!");
        }

        for (int i = 0; i < quantity; i++) {
            this.items.add(item);
        }
    }

    @Override
    public List<? extends Storable> retrieve(String itemName) {

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("The item name is null, empty or blank!");
        }

        List<Storable> filteredList = new ArrayList<>();

        for (int i = 0; i < this.items.size(); i++) {
            Storable item = this.items.get(i);

            if (item.getName().equals(itemName)) {
                filteredList.add(item);
                this.items.remove(i);
                i--;
            }
        }

        filteredList.sort(new SortByExpirationDate());

        return filteredList;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName, int quantity) throws InsufficientQuantityException {

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("The item name is null, empty or blank!");
        }
        if (quantity <= 0) throw new IllegalArgumentException("The quantity is not positive!");

        List<Storable> filteredList = new ArrayList<>();

        for (int i = 0; i < this.items.size(); i++) {
            Storable item = this.items.get(i);

            if (item.getName().equals(itemName)) {
                filteredList.add(item);
            }
        }

        if (filteredList.size() < quantity)
            throw new InsufficientQuantityException("The quantity of the item is insufficient");

        filteredList.sort(new SortByExpirationDate());

        //Remove the filtered items from the filtered list
        for (int i = filteredList.size() - 1; i >= quantity; i--) {
            filteredList.remove(i);
        }

        //Remove the filtered items from the fridge
        for (Storable curItem : filteredList) {
            for (int i = 0; i < this.items.size(); i++) {
                Storable curFridgeItem = this.items.get(i);

                if (curFridgeItem.getName().equals(curItem.getName()) &&
                    curFridgeItem.getExpiration().isEqual(curItem.getExpiration())) {
                    this.items.remove(i);
                    break;
                }
            }
//            this.items.remove(curItem);
        }

        return filteredList;
    }

    @Override
    public int getQuantityOfItem(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("The item name is null, empty or blank!");
        }

        int count = 0;
        for (Storable curItem : this.items) {
            if (curItem.getName().equals(itemName)) count++;
        }

        return count;
    }

    @Override
    public Iterator<Ingredient<? extends Storable>> getMissingIngredientsFromRecipe(Recipe recipe) {

        String ingredientLol = "Fridge Contents:\n";

        if (recipe == null) throw new IllegalArgumentException("The recipe is null!");

        List<Storable> nonExpiredItems = new ArrayList<>();

        for (Storable curItem : this.items) {
            ingredientLol += "Name: " + curItem.getName() +
                " | Expiration Date: " + curItem.getExpiration() + "\n";
            if (!curItem.isExpired()) nonExpiredItems.add(curItem);
        }
        ingredientLol += "\nRecipe contents:\n";

        Set<Ingredient<? extends Storable>> insufficientIngredients = new HashSet<>();

        for (Ingredient<? extends Storable> curIngredient : recipe.getIngredients()) {
            int quantityOfItemAvailable = 0;
            for (Storable curItem : nonExpiredItems) {
                if (curItem.getName().equals(curIngredient.item().getName())) quantityOfItemAvailable++;
            }

            if (quantityOfItemAvailable < curIngredient.quantity()) {
                int differenceBetweenQuality = curIngredient.quantity() - quantityOfItemAvailable;
                Ingredient<? extends Storable> newInsufficientIngredient
                    = new DefaultIngredient<>(curIngredient.item(), differenceBetweenQuality);
                insufficientIngredients.add(newInsufficientIngredient);
            }
        }

        //DEBUG SHIT
        Set<Ingredient<? extends Storable>> recipeIngredients = recipe.getIngredients();

        for (Ingredient<? extends Storable> recIngredient : recipeIngredients) {
            ingredientLol += "SomeInfo: " + recIngredient + "Name: " + recIngredient.item().getName() +
                " | Expiration Date: " + recIngredient.item().getExpiration()
                + " | " + recIngredient.quantity() + "\n";
        }
        ingredientLol += "My Ingredients:\n";

        for (Ingredient<? extends Storable> recIngredient : insufficientIngredients) {
            ingredientLol += "SomeInfo: " + recIngredient + "Name: " + recIngredient.item().getName() +
                " | Expiration Date: " + recIngredient.item().getExpiration()
                + " | " + recIngredient.quantity() + "\n";
        }

        //if (true) throw new IllegalArgumentException(ingredientLol);

        return insufficientIngredients.iterator();
    }

    @Override
    public List<? extends Storable> removeExpired() {
        List<Storable> expiredItems = new ArrayList<>();

        for (int i = 0; i < this.items.size(); i++) {
            Storable curItem = this.items.get(i);

            if (curItem.isExpired()) {
                this.items.remove(i);
                i--;
                expiredItems.add(curItem);
            }
        }

        return expiredItems;
    }
}
