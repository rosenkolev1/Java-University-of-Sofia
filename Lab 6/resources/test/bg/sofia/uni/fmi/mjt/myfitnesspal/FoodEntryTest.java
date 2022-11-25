package bg.sofia.uni.fmi.mjt.myfitnesspal;

import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntry;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//@ExtendWith(MockitoExtension.class)
public class FoodEntryTest {

    NutritionInfo nutritionInfo = new NutritionInfo(30, 40, 30);

    @Test
    void testCreateFoodEntryNullOrBlankFoodName() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new FoodEntry(null, 4, nutritionInfo),
            "Creating a food entry with a null name should throw an illegalArgumentException!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new FoodEntry("", 4, nutritionInfo),
            "Creating a food entry with an empty name should throw an illegalArgumentException!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new FoodEntry("    ", 4, nutritionInfo),
            "Creating a food entry with a blank name should throw an illegalArgumentException!");
    }

    @Test
    void testCreateFoodEntryNegativeServingSize() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new FoodEntry("Potato", -1, nutritionInfo),
            "Creating a food entry with a negative serving size should throw an illegalArgumentException!");
    }

    @Test
    void testCreateFoodEntryNullNutrientInfo() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new FoodEntry("Potato", 3, null),
            "Creating a food entry with a null nutrient info should throw an illegalArgumentException!");
    }

    @Test
    void testCreateFoodEntryWithZeroServingSizeShouldNotThrowError() {
        Assertions.assertDoesNotThrow(() -> new FoodEntry("Potato", 0, nutritionInfo),
            "Creating a food entry with a serving size of 0 should not throw any exception");
    }
}
