package bg.sofia.uni.fmi.mjt.newsfeed.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class NewsRequestTest {

    @Test
    void testNewRequestAddKeywords() {
        var request = NewsRequest.builder()
            .addKeywords("First", "Second", "Third")
            .addKeywords("Fourth")
            .build();

        var keywords = request.keywords();

        var expectedKeywords = List.of("First", "Second", "Third", "Fourth");

        Assertions.assertIterableEquals(expectedKeywords, keywords, "The expected keywords do not match the actual!");

        Assertions.assertThrows(UnsupportedOperationException.class,
            () -> keywords.clear(),
            "The collection should be unmodifiable");

        var firstElem = keywords.get(0);
        firstElem += "MODIFIED!!!";

        Assertions.assertNotEquals(keywords.get(0), firstElem, "The items of the list should be unmodifiable!");
    }

    @Test
    void testNewRequestAddCountry() {
        var nullRequest = NewsRequest.builder()
            .build();

        var nullCountry = nullRequest.country();

        Assertions.assertEquals(NewsCountry.NULL, nullCountry,  "The expected country should be null!");

        var request = NewsRequest.builder()
            .addCountry(NewsCountry.BR)
            .addCountry(NewsCountry.BG)
            .build();

        var country = request.country();

        var expectedCountry = NewsCountry.BG;

        Assertions.assertEquals(expectedCountry, country, "The expected country does not match the actual!");

        country = NewsCountry.US;

        Assertions.assertNotEquals(expectedCountry, country, "The country should be unmodifiable!");
    }

    @Test
    void testNewRequestAddCategory() {
        var nullRequest = NewsRequest.builder()
            .build();

        var nullCategory = nullRequest.category();

        Assertions.assertEquals(NewsCategory.NULL, nullCategory,  "The expected category should be null!");

        var request = NewsRequest.builder()
            .addCategory(NewsCategory.ENTERTAINMENT)
            .addCategory(NewsCategory.SPORTS)
            .build();

        var category = request.category();

        var expectedCategory = NewsCategory.SPORTS;

        Assertions.assertEquals(expectedCategory, category, "The expected category does not match the actual!");

        category = NewsCategory.HEALTH;

        Assertions.assertNotEquals(expectedCategory, category, "The category should be unmodifiable!");
    }

    @Test
    void testNewRequestAddPage() {
        var defaultRequest = NewsRequest.builder()
            .build();

        var defaultPage = defaultRequest.page();

        Assertions.assertEquals(NewsRequest.BASE_PAGE, defaultPage, "The expected BASE page does not match the actual!");

        var request = NewsRequest.builder()
            .addPage(14)
            .addPage(5)
            .build();

        var page = request.page();

        var expectedPage = 5;

        Assertions.assertEquals(expectedPage, page, "The expected page does not match the actual!");
    }

    @Test
    void testNewRequestAddPageSize() {
        var defaultRequest = NewsRequest.builder()
            .build();

        var defaultPageSize = defaultRequest.pageSize();

        Assertions.assertEquals(NewsRequest.BASE_PAGE_SIZE, defaultPageSize, "The expected BASE pageSize does not match the actual!");

        var request = NewsRequest.builder()
            .addPage(30)
            .addPageSize(40)
            .build();

        var pageSize = request.pageSize();

        var expectedPageSize = 40;

        Assertions.assertEquals(expectedPageSize, pageSize, "The expected pageSize does not match the actual!");
    }

    @Test
    void testContainsKeywords() {
        var defaultRequest = NewsRequest.builder()
            .build();

        Assertions.assertFalse(defaultRequest.containsKeywords(), "The default request should not contain keywords!");

        var request = NewsRequest.builder()
            .addKeywords("")
            .build();

        Assertions.assertTrue(request.containsKeywords(), "Expected keywords, but there were none!");
    }

    @Test
    void testNewsRequestBuilderCopy() {
        var request = NewsRequest.builder()
            .addKeywords("The", "Hey")
            .addCategory(NewsCategory.HEALTH)
            .addCountry(NewsCountry.US)
            .addPageSize(15)
            .addPage(4)
            .addKeywords("Another", "One")
            .build();

        var requestCopy = NewsRequest.builder(request)
            .addKeywords("Many", "KUURR!")
            .addCategory(NewsCategory.BUSINESS)
            .addPage(7)
            .build();

        var expectedKeywords = List.of("The", "Hey", "Another", "One", "Many", "KUURR!");
        var expectedCategory = NewsCategory.BUSINESS;
        var expectedPage = 7;

        Assertions.assertEquals(expectedKeywords, requestCopy.keywords());
        Assertions.assertEquals(expectedCategory, requestCopy.category());
        Assertions.assertEquals(expectedPage, requestCopy.page());

        Assertions.assertEquals(request.pageSize(), requestCopy.pageSize());
        Assertions.assertEquals(request.country(), requestCopy.country());
    }
}
