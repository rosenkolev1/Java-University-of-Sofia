package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import net.bytebuddy.NamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultCocktailStorageTest {

    @Test
    void testCreateCocktailAndGetCocktails() throws CocktailAlreadyExistsException {
        CocktailStorage storage = new DefaultCocktailStorage();

        List<Cocktail> expectedStorage = new ArrayList<>();

        Assertions.assertIterableEquals(expectedStorage, storage.getCocktails());

        Ingredient mentaIngredient = new Ingredient("Menta", "200ml");
        Ingredient spriteIngredient = new Ingredient("Sprite", "100ml");
        Cocktail mentaNaPlaja = new Cocktail("Menta na plaja", Set.of(
            mentaIngredient,
            spriteIngredient
        ));

        storage.createCocktail(mentaNaPlaja);
        expectedStorage.add(mentaNaPlaja);

        Assertions.assertIterableEquals(expectedStorage, storage.getCocktails());

        Ingredient baileysIngredient = new Ingredient("Baileys", "200ml");
        Ingredient bananaJuiceIngredient = new Ingredient("Banana juice", "200ml");
        Cocktail golaMaimuna = new Cocktail("Gola maimuna", Set.of(
            baileysIngredient,
            bananaJuiceIngredient
        ));

        storage.createCocktail(golaMaimuna);
        expectedStorage.add(golaMaimuna);

        Assertions.assertIterableEquals(expectedStorage, storage.getCocktails());
    }

    @Test
    void testCreateCocktailThrowsCocktailAlreadyExistsExcception() throws CocktailAlreadyExistsException {
        CocktailStorage storage = new DefaultCocktailStorage();

        Ingredient mentaIngredient = new Ingredient("Menta", "200ml");
        Ingredient spriteIngredient = new Ingredient("Sprite", "100ml");
        Cocktail mentaNaPlaja = new Cocktail("Menta na plaja", Set.of(
            mentaIngredient,
            spriteIngredient
        ));

        storage.createCocktail(mentaNaPlaja);

        Ingredient baileysIngredient = new Ingredient("Baileys", "200ml");
        Ingredient bananaJuiceIngredient = new Ingredient("Banana juice", "200ml");
        Cocktail golaMaimuna = new Cocktail("Gola maimuna", Set.of(
            baileysIngredient,
            bananaJuiceIngredient
        ));

        storage.createCocktail(golaMaimuna);

        Assertions.assertThrows(CocktailAlreadyExistsException.class,
            () -> storage.createCocktail(mentaNaPlaja));
    }

    @Test
    void testGetCocktailsWithIngredient() throws CocktailAlreadyExistsException {
        CocktailStorage storage = new DefaultCocktailStorage();

        Ingredient mentaIngredient = new Ingredient("Menta", "200ml");
        Ingredient spriteIngredient = new Ingredient("Sprite", "100ml");
        Cocktail mentaNaPlaja = new Cocktail("Menta na plaja", Set.of(
            mentaIngredient,
            spriteIngredient
        ));

        storage.createCocktail(mentaNaPlaja);

        Ingredient baileysIngredient = new Ingredient("Baileys", "200ml");
        Ingredient bananaJuiceIngredient = new Ingredient("Banana juice", "200ml");
        Cocktail golaMaimuna = new Cocktail("Gola maimuna", Set.of(
            baileysIngredient,
            bananaJuiceIngredient
        ));
        storage.createCocktail(golaMaimuna);

        Cocktail golaMaimunaWithMenta = new Cocktail("Gola maimuna sus menta", Set.of(
            bananaJuiceIngredient,
            mentaIngredient
        ));

        storage.createCocktail(golaMaimunaWithMenta);

        List<Cocktail> expectedCocktails = List.of(
            mentaNaPlaja,
            golaMaimunaWithMenta
        );
        Assertions.assertIterableEquals(expectedCocktails, storage.getCocktailsWithIngredient("Menta"));

        expectedCocktails = List.of(
            golaMaimuna,
            golaMaimunaWithMenta
        );
        Assertions.assertIterableEquals(expectedCocktails, storage.getCocktailsWithIngredient("baNAna JuIce"));

        expectedCocktails = List.of(
            mentaNaPlaja
        );
        Assertions.assertIterableEquals(expectedCocktails, storage.getCocktailsWithIngredient("Sprite"));

        expectedCocktails = List.of();
        Assertions.assertIterableEquals(expectedCocktails, storage.getCocktailsWithIngredient("sprit4e"));
    }

    @Test
    void testGetCocktailWithName() throws CocktailAlreadyExistsException, CocktailNotFoundException {
        CocktailStorage storage = new DefaultCocktailStorage();

        Ingredient mentaIngredient = new Ingredient("Menta", "200ml");
        Ingredient spriteIngredient = new Ingredient("Sprite", "100ml");
        Cocktail mentaNaPlaja = new Cocktail("Menta na plaja", Set.of(
            mentaIngredient,
            spriteIngredient
        ));

        storage.createCocktail(mentaNaPlaja);

        Ingredient baileysIngredient = new Ingredient("Baileys", "200ml");
        Ingredient bananaJuiceIngredient = new Ingredient("Banana juice", "200ml");
        Cocktail golaMaimuna = new Cocktail("Gola maimuna", Set.of(
            baileysIngredient,
            bananaJuiceIngredient
        ));
        storage.createCocktail(golaMaimuna);

        Cocktail golaMaimunaWithMenta = new Cocktail("Gola maimuna sus menta", Set.of(
            bananaJuiceIngredient,
            mentaIngredient
        ));

        storage.createCocktail(golaMaimunaWithMenta);

        Assertions.assertEquals(golaMaimuna, storage.getCocktail("Gola maimuna"));
        Assertions.assertEquals(golaMaimuna, storage.getCocktail("gOLa Maimuna"));
    }

    @Test
    void testGetCocktailWithNameThrowsCocktailNotFoundException() throws CocktailAlreadyExistsException, CocktailNotFoundException {
        CocktailStorage storage = new DefaultCocktailStorage();

        Ingredient mentaIngredient = new Ingredient("Menta", "200ml");
        Ingredient spriteIngredient = new Ingredient("Sprite", "100ml");
        Cocktail mentaNaPlaja = new Cocktail("Menta na plaja", Set.of(
            mentaIngredient,
            spriteIngredient
        ));

        storage.createCocktail(mentaNaPlaja);

        Ingredient baileysIngredient = new Ingredient("Baileys", "200ml");
        Ingredient bananaJuiceIngredient = new Ingredient("Banana juice", "200ml");
        Cocktail golaMaimuna = new Cocktail("Gola maimuna", Set.of(
            baileysIngredient,
            bananaJuiceIngredient
        ));
        storage.createCocktail(golaMaimuna);

        Cocktail golaMaimunaWithMenta = new Cocktail("Gola maimuna sus menta", Set.of(
            bananaJuiceIngredient,
            mentaIngredient
        ));

        storage.createCocktail(golaMaimunaWithMenta);

        Assertions.assertThrows(CocktailNotFoundException.class,
            () -> storage.getCocktail("Golata maimuna"));
    }
}
