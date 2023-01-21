import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsCountry;
import bg.sofia.uni.fmi.mjt.newsfeed.NewsFeed;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RequestException;

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