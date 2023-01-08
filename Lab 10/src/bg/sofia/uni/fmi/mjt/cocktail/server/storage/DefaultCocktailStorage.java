package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultCocktailStorage implements CocktailStorage {

    private List<Cocktail> cocktails;

    public DefaultCocktailStorage() {
        this.cocktails = new ArrayList<>();
    }

    @Override
    public void createCocktail(Cocktail cocktail) throws CocktailAlreadyExistsException {

        if (this.cocktails.contains(cocktail)) {
            throw new CocktailAlreadyExistsException("This cocktail already exists!");
        }

        this.cocktails.add(cocktail);
    }

    @Override
    public Collection<Cocktail> getCocktails() {
        return this.cocktails;
    }

    @Override
    public Collection<Cocktail> getCocktailsWithIngredient(String ingredientName) {
        return this.cocktails.stream().filter(x -> x.ingredients().stream()
            .anyMatch(y -> y.name().equalsIgnoreCase(ingredientName))).toList();
    }

    @Override
    public Cocktail getCocktail(String name) throws CocktailNotFoundException {

        if (!this.cocktails.stream().anyMatch(x -> x.name().equalsIgnoreCase(name))) {
            throw new CocktailNotFoundException("The cocktail doesn't exist");
        }

        return this.cocktails.stream().filter(x -> x.name().equalsIgnoreCase(name)).findFirst().get();
    }
}
