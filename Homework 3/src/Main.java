import bg.sofia.uni.fmi.mjt.newsfeed.NewsCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.NewsCountry;
import bg.sofia.uni.fmi.mjt.newsfeed.NewsFeed;
import bg.sofia.uni.fmi.mjt.newsfeed.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RequestException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws RequestException {
        var newsApi = NewsFeed.getInstance();

        var articlesRequest = NewsRequest.builder()
            .addKeywords("The")
//            .addCategory(NewsCategory.HEALTH)
            .addCountry(NewsCountry.US)
            .addPageSize(10)
            .addPage(1)
            .build();

        var allArticles = newsApi.getArticlesFromRequest(articlesRequest, 3, 1);

        System.out.println(allArticles);
    }
}