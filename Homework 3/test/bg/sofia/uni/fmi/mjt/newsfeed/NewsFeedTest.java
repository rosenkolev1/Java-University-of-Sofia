package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.InvalidURIException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.TooManyRequestsException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.UnauthorizedException;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsCountry;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NewsFeedTest {

    private static NewsFeed newsFeed;

    NewsFeedTest() {

    }
    @BeforeAll
    static void initializeSingletonInstance() {
        newsFeed = NewsFeed.getInstance();
    }

    @Test
    void testCreateHttpRequest() {
        var request = NewsRequest.builder()
            .addKeywords("The", "Hey")
            .addCategory(NewsCategory.HEALTH)
            .addCountry(NewsCountry.US)
            .addPageSize(15)
            .addPage(4)
            .addKeywords("Another", "One")
            .build();

        var url = newsFeed.createHttpRequest(request).uri().toString();

        var expectedUrl = NewsFeed.BASE_URL +
            NewsFeed.QUERY_PARAMS_SEPERATOR + NewsFeed.QUERY_KEYWORDS + "+The+Hey+Another+One" +
            NewsFeed.QUERY_PARAMS_SEPERATOR + NewsFeed.QUERY_COUNTRY + "US" +
            NewsFeed.QUERY_PARAMS_SEPERATOR + NewsFeed.QUERY_CATEGORY + "HEALTH" +
            NewsFeed.QUERY_PARAMS_SEPERATOR + NewsFeed.QUERY_PAGE + "4" +
            NewsFeed.QUERY_PARAMS_SEPERATOR + NewsFeed.QUERY_PAGESIZE + "15";

        Assertions.assertEquals(expectedUrl, url, "The expected request url does not match the actual!");
    }

    @Test
    void testCreateHttpRequestDefaultValues() {
        var request = NewsRequest.builder()
            .build();

        var url = newsFeed.createHttpRequest(request).uri().toString();

        var expectedUrl = NewsFeed.BASE_URL + NewsFeed.QUERY_PARAMS_SEPERATOR + NewsFeed.QUERY_PAGESIZE + NewsRequest.BASE_PAGE_SIZE;

        Assertions.assertEquals(expectedUrl, url, "The expected default request url does not match the actual!");
    }

    @Test
    void testCreateHttpRequestThrowsInvalidURIException() {
        var request = NewsRequest.builder()
            .addKeywords("  ")
            .build();

        Assertions.assertThrows(InvalidURIException.class,
            () -> newsFeed.createHttpRequest(request),
            "Expected InvalidURIException but nothing was thrown");
    }

    @Test
    void testGetArticlesFromRequestThrowsIllegalArgumentException() {
        var request = NewsRequest.builder()
            .addCountry(NewsCountry.BG)
            .build();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> newsFeed.getArticlesFromRequest(null),
            "Expected IllegalArgumentException because of null request, but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> newsFeed.getArticlesFromRequest(request),
            "Expected IllegalArgumentException because of no keywords in request, but nothing was thrown!");
    }

    @Test
    void testGetArticlesFromRequestThrowsIllegalArgumentExceptionBecauseOfInvalidPages() throws URISyntaxException {
        var request = NewsRequest.builder()
            .addKeywords("heyyy")
            .build();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> newsFeed.getArticlesFromRequest(request, -1),
            "Expected IllegalArgumentException, but nothing was thrown!");
    }

    @Test
    void testGetArticlesFromRequestThrowsIllegalArgumentExceptionBecauseOfInvalidStartingPage() throws URISyntaxException {
        var request = NewsRequest.builder()
            .addKeywords("heyyy")
            .build();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> newsFeed.getArticlesFromRequest(request, 3, 0),
            "Expected IllegalArgumentException, but nothing was thrown!");
    }

    @Test
    void testGetArticlesFromRequestThrowsBadRequestException() throws URISyntaxException {

        var newsFeedSpy = Mockito.spy(newsFeed);

        var invalidUrl = NewsFeed.REQUEST_PROTOCOL + NewsFeed.REQUEST_HOST + NewsFeed.REQUEST_ENDPOINT +
            NewsFeed.QUERY_PARAMS_START + NewsFeed.QUERY_APIKEY + NewsFeed.API_KEY + NewsFeed.QUERY_PARAMS_SEPERATOR;

        doReturn(HttpRequest.newBuilder()
            .uri(new URI(invalidUrl))
            .build())
            .when(newsFeedSpy).createHttpRequest(any());

        var request = NewsRequest.builder()
            .addKeywords("heyyy")
            .build();

        Assertions.assertThrows(BadRequestException.class,
            () -> newsFeedSpy.getArticlesFromRequest(request),
            "Expected BadRequestException, but nothing was thrown!");

        verify(newsFeedSpy).createHttpRequest(any());
    }

    @Test
    void testGetArticlesFromRequestThrowsUnauthorizedException() throws URISyntaxException {

        var newsFeedSpy = Mockito.spy(newsFeed);

        var invalidUrl = NewsFeed.REQUEST_PROTOCOL + NewsFeed.REQUEST_HOST + NewsFeed.REQUEST_ENDPOINT +
            NewsFeed.QUERY_PARAMS_START + NewsFeed.QUERY_APIKEY;

        doReturn(HttpRequest.newBuilder()
            .uri(new URI(invalidUrl))
            .build())
            .when(newsFeedSpy).createHttpRequest(any());

        var request = NewsRequest.builder()
            .addKeywords("heyyy")
            .build();

        Assertions.assertThrows(UnauthorizedException.class,
            () -> newsFeedSpy.getArticlesFromRequest(request),
            "Expected UnauthorizedException, but nothing was thrown!");

        verify(newsFeedSpy).createHttpRequest(any());
    }

    @Test
    void testGetArticlesFromRequest() throws URISyntaxException {
        var request = NewsRequest.builder()
            .addKeywords("The", "Hey")
            .addCategory(NewsCategory.HEALTH)
            .addCountry(NewsCountry.US)
            .addPageSize(15)
            .addKeywords("Another", "One")
            .build();

        Assertions.assertDoesNotThrow(() -> newsFeed.getArticlesFromRequest(request),
            "Unexpected exception has occurred!");
    }

    @Test
    void testNewsFeedIsSingleton() {
        var newNewsFeed = NewsFeed.getInstance();

        Assertions.assertSame(newNewsFeed, newsFeed);
    }

//    @Test
//    void testGetArticlesFromRequestThrowsTooManyRequestsException() throws URISyntaxException {
//
//        var request = NewsRequest.builder()
//            .addKeywords("a")
//            .build();
//
//        Assertions.assertThrows(TooManyRequestsException.class,
//            () -> {
//                while(true) {
//                    System.out.println(newsFeed.getArticlesFromRequest(request));
//                }
////                newsFeed.getArticlesFromRequest(request);
//            },
//            "Expected TooManyRequestsException, but nothing was thrown!");
//    }
}
