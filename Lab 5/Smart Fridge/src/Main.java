import bg.sofia.uni.fmi.mjt.smartfridge.SmartFridge;
import bg.sofia.uni.fmi.mjt.smartfridge.SmartFridgeAPI;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.FridgeCapacityExceededException;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.InsufficientQuantityException;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.recipe.BaseRecipe;
import bg.sofia.uni.fmi.mjt.smartfridge.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Beverage;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Food;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.OtherStorable;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] arg) throws FridgeCapacityExceededException, InsufficientQuantityException {
        Storable egg = new Food("Egg", LocalDate.of(2022, 11, 20));
        Storable flour = new Food("Flour", LocalDate.of(2022, 12, 20));
        Storable nabuhvatel = new Food("Nabuhvatel", LocalDate.of(2022, 11, 15));
        Storable koriZaBanica = new Food("Kori za banica", LocalDate.of(2022, 11, 18));
        Storable redWine = new Beverage("Red Wine", LocalDate.of(2050, 1, 1));
        Storable hapaNaAlito = new OtherStorable("AliHap", LocalDate.of(2022, 12, 30));
        Storable egg2 = new Food("Egg", LocalDate.of(2022, 10, 20));

        Ingredient<Storable> eggIngredient = new DefaultIngredient<>(egg, 12);
        Ingredient<Storable> flourIngredient = new DefaultIngredient<>(flour, 1);
        Ingredient<Storable> nabuhvatelIngredient = new DefaultIngredient<>(nabuhvatel, 1);
        Ingredient<Storable> koriZaBanicaIngredient = new DefaultIngredient<>(koriZaBanica, 8);
        Ingredient<Storable> redWineIngredient = new DefaultIngredient<>(redWine, 1);
        Ingredient<Storable> hapaNaAlitoIngredient = new DefaultIngredient<>(hapaNaAlito, 2);
        Ingredient<Storable> eggIngredient2 = new DefaultIngredient<>(egg2, 12);

        //Set<Ingredient<Storable>> alkoholnaBanicaIngredients = new HashSet<Ingredient<Storable>>();

        Recipe alkoholnaBanicaRec = new BaseRecipe();
        alkoholnaBanicaRec.addIngredient(eggIngredient);
        alkoholnaBanicaRec.addIngredient(flourIngredient);
        alkoholnaBanicaRec.addIngredient(nabuhvatelIngredient);
        alkoholnaBanicaRec.addIngredient(koriZaBanicaIngredient);
        alkoholnaBanicaRec.addIngredient(redWineIngredient);
        alkoholnaBanicaRec.addIngredient(eggIngredient);
        alkoholnaBanicaRec.addIngredient(eggIngredient2);

        alkoholnaBanicaRec.removeIngredient("Egg");
        alkoholnaBanicaRec.addIngredient(eggIngredient);

        SmartFridgeAPI sf = new SmartFridge(20);

        sf.store(egg, 6);
        sf.store(egg2, 6);
        sf.store(koriZaBanica, 8);

//        int countOfEggs = sf.getQuantityOfItem("Egg");
//        int countOfHapa = sf.getQuantityOfItem("AliHap");
//        List<? extends Storable> getEggs = sf.retrieve("Egg", 10);
//        List<? extends Storable> getEggs2 = sf.retrieve("Egg", 2);
//        List<? extends Storable> getEggs3 = sf.retrieve("Egg");

        //sf.store(egg, 12);
        Recipe easyRec = new BaseRecipe();
        easyRec.addIngredient(new DefaultIngredient<Storable>(egg, 6));

        sf.getMissingIngredientsFromRecipe(alkoholnaBanicaRec);
        sf.getMissingIngredientsFromRecipe(easyRec);

//        List<? extends Storable> getExpired = sf.removeExpired();
        //List<? extends Storable> getExpired2 = sf.removeExpired();
    }
}