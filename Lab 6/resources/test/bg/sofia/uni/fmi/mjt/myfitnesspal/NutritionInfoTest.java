package bg.sofia.uni.fmi.mjt.myfitnesspal;

import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NutritionInfoTest {

    @Test
    void testCreateNutritionInfoNegativeMacronutrient() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new NutritionInfo(-1, 10, 10),
            "Creating nutritionInfo with negative carbohydrates should throw an illegalArgumentException!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new NutritionInfo(10, -1, 10),
            "Creating nutritionInfo with negative fats should throw an illegalArgumentException!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new NutritionInfo(10, 10, -1),
            "Creating nutritionInfo with negative protein should throw an illegalArgumentException!");
    }

    @Test
    void testCreateNutritionInfoMacrosNotEqualTo100() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new NutritionInfo(33, 47, 19),
            "Creating nutritionInfo with total macros not equal to 100 should throw an illegalArgumentException!");
    }

    @Test
    void testCreateNutritionInfoWithSomeMacrosAtZeroShouldNotThrowError() {
        Assertions.assertDoesNotThrow(() -> new NutritionInfo(100, 0, 0),
            "Creating nutritionInfo with 100 carbs and no fats or protein should not throw any exception");

        Assertions.assertDoesNotThrow(() -> new NutritionInfo(0, 100, 0),
            "Creating nutritionInfo with 100 fats and no carbs or protein should not throw any exception");

        Assertions.assertDoesNotThrow(() -> new NutritionInfo(0, 0, 100),
            "Creating nutritionInfo with 100 protein and no carbs or fats should not throw any exception");
    }

}
