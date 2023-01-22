import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsCountry;
import bg.sofia.uni.fmi.mjt.newsfeed.NewsFeed;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RequestException;

public class Main {
    public static void main(String[] args) throws RequestException {
        //    public static final String API_KEY = "2fc30dc9238b4c22badf4228ab764b3d";
        //    public static final String API_KEY = "cc9457678dcd48259b7638aa367f4e0f";
        //    public static final String API_KEY = "a5c9b187c64340dcb1b9c768041ef20b"
        var newsApi = NewsFeed.getInstance("a5c9b187c64340dcb1b9c768041ef20b");

        var articlesRequest = NewsRequest.builder()
            .addKeywords("The")
//            .addCategory(NewsCategory.HEALTH)
            .addCountry(NewsCountry.US)
            .addPageSize(6)
            .addPage(3)
            .build();

        var allArticles = newsApi.getArticlesFromRequest(articlesRequest, 2);

        System.out.println(allArticles);
    }
}