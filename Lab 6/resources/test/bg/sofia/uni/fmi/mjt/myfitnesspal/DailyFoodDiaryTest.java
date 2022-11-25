package bg.sofia.uni.fmi.mjt.myfitnesspal;


import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.DailyFoodDiary;
import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.FoodEntry;
import bg.sofia.uni.fmi.mjt.myfitnesspal.diary.Meal;
import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfoAPI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyFoodDiaryTest {

    @Mock
    NutritionInfoAPI nutritionInfoApi;

    @InjectMocks
    DailyFoodDiary fd;

    @Test
    void testAddFoodThrowsMealIsNullException() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> fd.addFood(null, "Something", 10),
            "Expected IllegalArgumentException because of null meal!");

    }

    @Test
    void testAddFoodThrowsNameIsNullOrBlankException() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> fd.addFood(Meal.BREAKFAST, null, 10),
            "Expected IllegalArgumentException because of null foodName!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> fd.addFood(Meal.BREAKFAST, "", 10),
            "Expected IllegalArgumentException because of empty foodName!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> fd.addFood(Meal.BREAKFAST, "     ", 10),
            "Expected IllegalArgumentException because of blank foodName!");
    }

    @Test
    void testAddFoodThrowsServingSizeIsNegativeException() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> fd.addFood(Meal.BREAKFAST, "Something", -1),
            "Expected IllegalArgumentException because of negative serving size!");
    }

    @Test
    void testAddFoodWorksProperly() throws UnknownFoodException{
        String foodName = "Kartof";
        double servingSize = 10;
        NutritionInfo foodNI = new NutritionInfo(50, 30, 20);

        when(nutritionInfoApi.getNutritionInfo(foodName))
            .thenReturn(foodNI);

        FoodEntry expectedOutput = new FoodEntry(foodName, servingSize, foodNI);

        Assertions.assertEquals(fd.addFood(Meal.BREAKFAST, foodName, servingSize), (expectedOutput),
            "Expected objects to be the same");

        verify(nutritionInfoApi).getNutritionInfo(foodName);
    }

    @Test
    void testGetALlFoodEntriesEmpty() {

        Assertions.assertIterableEquals(fd.getAllFoodEntries(), new ArrayList<FoodEntry>());

    }

    @Test
    void testGetAllFoodEntriesWorksProperly() throws UnknownFoodException{
        String foodNamePotato = "Potato";
        double servingSizePotato = 3;
        NutritionInfo foodNIPotato = new NutritionInfo(50, 30, 20);

        when(nutritionInfoApi.getNutritionInfo(foodNamePotato))
            .thenReturn(foodNIPotato);

        String foodNameFish = "Fish";
        double servingSizeFish = 5;
        NutritionInfo foodNIFish = new NutritionInfo(25, 25, 50);

        when(nutritionInfoApi.getNutritionInfo(foodNameFish))
            .thenReturn(foodNIFish);

        String foodNameBread = "Bread";
        double servingSizeBread = 2;
        NutritionInfo foodNIBread = new NutritionInfo(70, 17, 13);

        when(nutritionInfoApi.getNutritionInfo(foodNameBread))
            .thenReturn(foodNIBread);

        String foodNameBroccoli = "Broccoli";
        double servingSizeBroccoli = 1;
        NutritionInfo foodNIBroccoli = new NutritionInfo(38, 22, 40);

        when(nutritionInfoApi.getNutritionInfo(foodNameBroccoli))
            .thenReturn(foodNIBroccoli);

        fd.addFood(Meal.BREAKFAST, foodNamePotato, servingSizePotato);
        fd.addFood(Meal.BREAKFAST, foodNameFish, servingSizeFish);
        fd.addFood(Meal.DINNER, foodNameBread, servingSizeBread);
        fd.addFood(Meal.SNACKS, foodNameBroccoli, servingSizeBroccoli);

        FoodEntry foodEntryPotato = new FoodEntry(foodNamePotato, servingSizePotato, foodNIPotato);
        FoodEntry foodEntryFish = new FoodEntry(foodNameFish, servingSizeFish, foodNIFish);
        FoodEntry foodEntryBread = new FoodEntry(foodNameBread, servingSizeBread, foodNIBread);
        FoodEntry foodEntryBroccoli = new FoodEntry(foodNameBroccoli, servingSizeBroccoli, foodNIBroccoli);

        Collection<FoodEntry> expectedOutput = new ArrayList<>(Arrays.asList(
            foodEntryFish,
            foodEntryBread,
            foodEntryBroccoli,
            foodEntryPotato
        ));

        Collection<FoodEntry> actualOutput = fd.getAllFoodEntries();

        Assertions.assertTrue(actualOutput.containsAll(expectedOutput),
            "The foods should all be contained in the list but they are not!");

        Assertions.assertTrue(actualOutput.size() == expectedOutput.size(),
            "The actual and the expected outputs differ in length");

        Assertions.assertThrows(UnsupportedOperationException.class,
            () -> actualOutput.add(foodEntryFish),
            "The collection should be unmodifiable!");

        verify(nutritionInfoApi).getNutritionInfo(foodNamePotato);
        verify(nutritionInfoApi).getNutritionInfo(foodNameFish);
        verify(nutritionInfoApi).getNutritionInfo(foodNameBread);
        verify(nutritionInfoApi).getNutritionInfo(foodNameBroccoli);
    }

    @Test
    void testGetAllFoodEntriesByProteinContentWorksProperly() throws UnknownFoodException{

        String foodNamePotato = "Potato";
        double servingSizePotato = 3;
        NutritionInfo foodNIPotato = new NutritionInfo(50, 30, 20);
        //60 protein

        String foodNameFish = "Fish";
        double servingSizeFish = 5;
        NutritionInfo foodNIFish = new NutritionInfo(25, 25, 50);
        //250 protein

        String foodNameBread = "Bread";
        double servingSizeBread = 2;
        NutritionInfo foodNIBread = new NutritionInfo(70, 17, 13);
        //26 protein

        String foodNameBroccoli = "Broccoli";
        double servingSizeBroccoli = 1;
        NutritionInfo foodNIBroccoli = new NutritionInfo(38, 22, 40);
        //40 protein

        FoodEntry foodEntryPotato = new FoodEntry(foodNamePotato, servingSizePotato, foodNIPotato);
        FoodEntry foodEntryFish = new FoodEntry(foodNameFish, servingSizeFish, foodNIFish);
        FoodEntry foodEntryBread = new FoodEntry(foodNameBread, servingSizeBread, foodNIBread);
        FoodEntry foodEntryBroccoli = new FoodEntry(foodNameBroccoli, servingSizeBroccoli, foodNIBroccoli);

        Collection<FoodEntry> allFoodEntries = new ArrayList<>(Arrays.asList(
            foodEntryFish,
            foodEntryBread,
            foodEntryBroccoli,
            foodEntryPotato
        ));

        //Mock the tested class
        DailyFoodDiary fdMock =  spy(fd);
        when(fdMock.getAllFoodEntries())
            .thenReturn(allFoodEntries);

        Collection<FoodEntry> expectedOutput = new ArrayList<>(Arrays.asList(
            foodEntryBread,
            foodEntryBroccoli,
            foodEntryPotato,
            foodEntryFish
        ));

        Collection<FoodEntry> actualOutput = fdMock.getAllFoodEntriesByProteinContent();

        Assertions.assertIterableEquals(actualOutput, expectedOutput,
            "The food entries were not sorted in the proper order!");

        Assertions.assertTrue(actualOutput.size() == expectedOutput.size(),
            "The actual and the expected outputs differ in length");

        Assertions.assertThrows(UnsupportedOperationException.class,
            () -> actualOutput.add(foodEntryFish),
            "The collection should be unmodifiable!");

        verify(fdMock).getAllFoodEntries();
    }

    @Test
    void testGetAllFoodEntriesByProteinContentEmpty() throws UnknownFoodException{

        Collection<FoodEntry> allFoodEntries = new ArrayList<>();

        //Mock the tested class
        DailyFoodDiary fdMock =  Mockito.spy(fd);
        when(fdMock.getAllFoodEntries())
            .thenReturn(allFoodEntries);

        Collection<FoodEntry> expectedOutput = new ArrayList<>();
        Collection<FoodEntry> actualOutput = fdMock.getAllFoodEntriesByProteinContent();

        Assertions.assertIterableEquals(actualOutput, expectedOutput,
            "The food entries were not sorted in the proper order!");

        Assertions.assertThrows(UnsupportedOperationException.class,
            () -> actualOutput.clear(),
            "The collection should be unmodifiable!");

        verify(fdMock).getAllFoodEntries();
    }

    @Test
    void testGetDailyCaloriesIntakePerMealIsNull() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> fd.getDailyCaloriesIntakePerMeal(null),
            "The method should throw an illegalArgumentException because of null meal");

    }

    @Test
    void testGetDailyCaloriesIntakePerMealWorksEmptyMeal() throws UnknownFoodException {

        double expectedOutput = 0;

        Assertions.assertEquals(fd.getDailyCaloriesIntakePerMeal(Meal.BREAKFAST), expectedOutput,
            "The actual daily calories for the meal should be 0 because the meal is empty");
    }

    @Test
    void testGetDailyCaloriesIntakePerMealWorksProperly() throws UnknownFoodException {

        String foodNamePotato = "Potato";
        double servingSizePotato = 3;
        NutritionInfo foodNIPotato = new NutritionInfo(50, 30, 20);
        //150 * 4 + 90 * 9 + 60 * 4 = 1650

        when(nutritionInfoApi.getNutritionInfo(foodNamePotato))
            .thenReturn(foodNIPotato);

        String foodNameFish = "Fish";
        double servingSizeFish = 5;
        NutritionInfo foodNIFish = new NutritionInfo(25, 25, 50);
        //125 * 4 + 125 * 9 + 250 * 4 = 2625

        when(nutritionInfoApi.getNutritionInfo(foodNameFish))
            .thenReturn(foodNIFish);

        fd.addFood(Meal.BREAKFAST, foodNamePotato, servingSizePotato);
        fd.addFood(Meal.BREAKFAST, foodNameFish, servingSizeFish);

        double expectedOutput = 4275;

        Assertions.assertEquals(fd.getDailyCaloriesIntakePerMeal(Meal.BREAKFAST), expectedOutput,
            "The actual daily calories for the meal differ from the expected");

        verify(nutritionInfoApi).getNutritionInfo(foodNamePotato);
        verify(nutritionInfoApi).getNutritionInfo(foodNameFish);
    }

    @Test
    void testGetDailyCaloriesIntakeEmpty() {

        double expectedOutput = 0;

        Assertions.assertEquals(fd.getDailyCaloriesIntake(), expectedOutput,
            "The total daily calories should be 0 because all the meals are empty");

    }

    @Test
    void testGetDailyCaloriesIntakeWorksProperly() throws UnknownFoodException{

        String foodNamePotato = "Potato";
        double servingSizePotato = 3;
        NutritionInfo foodNIPotato = new NutritionInfo(50, 30, 20);
        //150 * 4 + 90 * 9 + 60 * 4 = 1650

        when(nutritionInfoApi.getNutritionInfo(foodNamePotato))
            .thenReturn(foodNIPotato);

        String foodNameFish = "Fish";
        double servingSizeFish = 5;
        NutritionInfo foodNIFish = new NutritionInfo(25, 25, 50);
        //125 * 4 + 125 * 9 + 250 * 4 = 2625

        when(nutritionInfoApi.getNutritionInfo(foodNameFish))
            .thenReturn(foodNIFish);

        String foodNameBread = "Bread";
        double servingSizeBread = 2;
        NutritionInfo foodNIBread = new NutritionInfo(70, 17, 13);
        //140 * 4 + 34 * 9 + 26 * 4 = 970

        when(nutritionInfoApi.getNutritionInfo(foodNameBread))
            .thenReturn(foodNIBread);

        String foodNameBroccoli = "Broccoli";
        double servingSizeBroccoli = 1;
        NutritionInfo foodNIBroccoli = new NutritionInfo(38, 22, 40);
        //38 * 4 + 22 * 9 + 40 * 4 = 510

        when(nutritionInfoApi.getNutritionInfo(foodNameBroccoli))
            .thenReturn(foodNIBroccoli);

        fd.addFood(Meal.BREAKFAST, foodNamePotato, servingSizePotato);
        fd.addFood(Meal.BREAKFAST, foodNameFish, servingSizeFish);
        fd.addFood(Meal.DINNER, foodNameBread, servingSizeBread);
        fd.addFood(Meal.SNACKS, foodNameBroccoli, servingSizeBroccoli);

        FoodEntry foodEntryPotato = new FoodEntry(foodNamePotato, servingSizePotato, foodNIPotato);
        FoodEntry foodEntryFish = new FoodEntry(foodNameFish, servingSizeFish, foodNIFish);
        FoodEntry foodEntryBread = new FoodEntry(foodNameBread, servingSizeBread, foodNIBread);
        FoodEntry foodEntryBroccoli = new FoodEntry(foodNameBroccoli, servingSizeBroccoli, foodNIBroccoli);

        double expectedOutput = 1650 + 2625 + 970 + 510; //5755 THAT'S A LOT OF CALORIES SON

        Assertions.assertEquals(fd.getDailyCaloriesIntake(), expectedOutput,
            "The actual total daily calories is not equal to the expected");

        verify(nutritionInfoApi).getNutritionInfo(foodNamePotato);
        verify(nutritionInfoApi).getNutritionInfo(foodNameFish);
        verify(nutritionInfoApi).getNutritionInfo(foodNameBread);
        verify(nutritionInfoApi).getNutritionInfo(foodNameBroccoli);
    }
}
