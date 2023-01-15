package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.article.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.article.ArticleSource;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javax.sql.rowset.BaseRowSet;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NewsFeed implements NewsAPI {

    private static NewsFeed instance = null;

    private int maxArticles;

    private HttpClient client;

    private NewsFeed(int maxArticles) {
        this.maxArticles = maxArticles;
        this.client = HttpClient.newBuilder().build();
    }

    public static NewsFeed getInstance() {
        if (instance != null) {
            return instance;
        }
        else {
            return new NewsFeed(120);
        }
    }

//    private String getErrorMessageFromBody(String body) {
//        Gson gson = new Gson();
//
//        var jsonObject = gson.fromJson(body, JsonObject.class);
//        var messageElement = jsonObject.get("message");
//        var message = gson.fromJson(messageElement, String.class);
//
//        return message;
//    }

    private void validateResponse(HttpResponse<String> response) throws RequestException {

        switch (response.statusCode()) {
            case 500:
                throw new UnexpectedErrorException(response.body());
            case 400:
                throw new BadRequestException(response.body());
            case 401:
                throw new UnauthorizedException(response.body());
            case 429:
                throw new TooManyRequestsException(response.body());
        }
    }

    public Collection<Article> getArticlesFromRequest(NewsRequest request) throws RequestException {

        if (!request.containsKeywords()) {
            throw new RequestException("The given request does not contain keyword restrictions!");
        }

        var responseFuture = client.sendAsync(request.getHttpRequest(), HttpResponse.BodyHandlers.ofString())
            .thenApply(x -> {
                return x;
            });

        Gson gson = new Gson();
        var response = responseFuture.join();

        validateResponse(response);

        String responseBody = response.body();
//        System.out.println("\n\n" + responseBody);

        Type listOfArticlesType = new TypeToken<ArrayList<Article>>() {}.getType();

        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        var articlesJsonObject = jsonObject.get("articles"); // returns a JsonElement for that name

        List<Article> articleResponse = gson.fromJson(articlesJsonObject, listOfArticlesType);

//        System.out.println("\n\n" + responseBody);
//        System.out.println("\n\n" + articleResponse);

        return articleResponse.stream().limit(this.maxArticles).toList();
    }
//
//    public Collection<Article> getArticlesByKeyword(String... keywords) throws RequestException {
//        String newURL = this.newsQueryBuilder()
//            .addKeywordsContains(keywords)
//            .build();
//
//        HttpRequest request = this.buildRequestFromURL(newURL);
//
//        return this.getArticlesFromRequest(request);
//    }

//    public
}
