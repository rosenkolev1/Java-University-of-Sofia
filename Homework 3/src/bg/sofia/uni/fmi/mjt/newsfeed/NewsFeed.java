package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.article.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.*;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsCategory;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsCountry;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NewsFeed implements NewsAPI {
    public static final String REQUEST_PROTOCOL = "https://";
    public static final String REQUEST_HOST = "newsapi.org";
    public static final String REQUEST_ENDPOINT = "/v2/top-headlines";

    public static final String QUERY_PARAMS_START = "?";
    public static final String QUERY_APIKEY = "apiKey=";
    public static final String QUERY_PAGESIZE = "pageSize=";
    public static final String QUERY_PAGE = "page=";
    public static final String QUERY_PARAMS_SEPERATOR = "&";
    public static final String QUERY_KEYWORDS = "q=";
    public static final String QUERY_COUNTRY = "country=";
    public static final String QUERY_CATEGORY = "category=";

    public static final String API_KEY = "2fc30dc9238b4c22badf4228ab764b3d";

    public static final String BASE_URL = REQUEST_PROTOCOL + REQUEST_HOST + REQUEST_ENDPOINT
        + QUERY_PARAMS_START + QUERY_APIKEY + API_KEY;

    public static final int BASE_MAX_ARTICLES = 120;

    private static NewsFeed instance = null;

    private HttpClient client;

    public final int maxArticles;

    private NewsFeed(int maxArticles) {
        this.maxArticles = maxArticles;
        this.client = HttpClient.newBuilder().build();
    }

    public static NewsFeed getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return getInstance(BASE_MAX_ARTICLES);
        }
    }

    public static NewsFeed getInstance(int maxArticles) {
        if (instance != null) {
            return instance;
        } else {
            instance = new NewsFeed(maxArticles);
            return instance;
        }
    }

    private void validateResponse(HttpResponse<String> response) throws RequestException {

        switch (response.statusCode()) {
            case 400:
                throw new BadRequestException(response.body());
            case 401:
                throw new UnauthorizedException(response.body());
            case 429:
                throw new TooManyRequestsException(response.body());
            case 500:
                throw new UnexpectedErrorException(response.body());
        }
    }

    private void removeTrailingQuerySymbols(StringBuilder urlBuilder) {
        while (true) {
            if (urlBuilder.toString().endsWith(QUERY_PARAMS_SEPERATOR)) {
                urlBuilder.delete(
                    urlBuilder.length() - QUERY_PARAMS_SEPERATOR.length(),
                    urlBuilder.length()
                );
            } else if (urlBuilder.toString().endsWith(QUERY_PARAMS_START)) {
                urlBuilder.delete(
                    urlBuilder.length() - QUERY_PARAMS_START.length(),
                    urlBuilder.length()
                );
            } else break;
        }
    }

    private void appendPageSizeToQuery(StringBuilder urlBuilder, int pageSize) {
        if (pageSize > 0) {
            urlBuilder.append(QUERY_PAGESIZE);
            urlBuilder.append(pageSize);

            urlBuilder.append(QUERY_PARAMS_SEPERATOR);
        }
    }

    private void appendPageToQuery(StringBuilder urlBuilder, int page) {
        if (page > 1) {
            urlBuilder.append(QUERY_PAGE);
            urlBuilder.append(page);

            urlBuilder.append(QUERY_PARAMS_SEPERATOR);
        }
    }

    private void appendCategoryToQuery(StringBuilder urlBuilder, NewsCategory category) {
        if (category != NewsCategory.NULL) {
            urlBuilder.append(QUERY_CATEGORY);
            urlBuilder.append(category);

            urlBuilder.append(QUERY_PARAMS_SEPERATOR);
        }
    }

    private void appendCountryToQuery(StringBuilder urlBuilder, NewsCountry country) {
        if (country != NewsCountry.NULL) {
            urlBuilder.append(QUERY_COUNTRY);
            urlBuilder.append(country);

            urlBuilder.append(QUERY_PARAMS_SEPERATOR);
        }
    }

    private void appendKeywordsToQuery(StringBuilder urlBuilder, List<String> keywords) {
        if (!keywords.isEmpty()) {
            urlBuilder.append(QUERY_KEYWORDS);

            StringBuilder keywordArgs = new StringBuilder(String.join("", keywords.stream().map(
                x -> "+" + x
            ).toList()));

            urlBuilder.append(keywordArgs.toString());

            urlBuilder.append(QUERY_PARAMS_SEPERATOR);
        }
    }

    private HttpRequest createHttpRequestFromURL(String url) {
        try {
            return HttpRequest.newBuilder()
                .uri(new URI(url))
                .build();
        } catch (URISyntaxException e) {
            throw new InvalidURIException(
                "The given URI is invalid. Check the query parameters for invalid syntax in keywords!"
            );
        }
    }

    public HttpRequest createHttpRequest(NewsRequest request) {
        var urlBuilder = new StringBuilder(BASE_URL);

        urlBuilder.append(QUERY_PARAMS_SEPERATOR);

        //Append the keywords to the query
        appendKeywordsToQuery(urlBuilder, request.keywords());
        appendCountryToQuery(urlBuilder, request.country());
        appendCategoryToQuery(urlBuilder, request.category());
        appendPageToQuery(urlBuilder, request.page());
        appendPageSizeToQuery(urlBuilder, request.pageSize());

        //Remove trailing query symbols in query
        removeTrailingQuerySymbols(urlBuilder);

        HttpRequest httpRequest = this.createHttpRequestFromURL(urlBuilder.toString());

        return httpRequest;
    }

    @Override
    public Collection<Article> getAllArticlesFromRequest(NewsRequest request)
        throws RequestException {

        Collection<Article> allArticles = new ArrayList<>();

        int curPage = 1;

        do {
            NewsRequest pageRequest = NewsRequest.builder(request)
                .addPage(curPage)
                .build();

            Collection<Article> articles = getArticlesFromRequest(pageRequest);

            if (articles.isEmpty()) {
                break;
            }

            allArticles.addAll(articles);

            curPage++;
        } while(true);

        return allArticles;
    }

    public Collection<Article> getArticlesFromRequest(NewsRequest request, int pages, int startingPage) throws RequestException {

        if (pages < 0) {
            throw new IllegalArgumentException("The pages should be non-negative");
        }

        if (startingPage <= 0) {
            throw new IllegalArgumentException("The starting page should be positive");
        }

        Collection<Article> allArticles = new ArrayList<>();

        for (int i = startingPage; i < startingPage + pages; i++) {
            NewsRequest pageRequest = NewsRequest.builder(request)
                .addPage(i)
                .build();

            Collection<Article> articles = getArticlesFromRequest(pageRequest);

            if (articles.isEmpty()) {
                break;
            }

            allArticles.addAll(articles);
        }

        return allArticles;
    }

    public Collection<Article> getArticlesFromRequest(NewsRequest request, int pages) throws RequestException {
        return getArticlesFromRequest(request, pages, 1);
    }

    public Collection<Article> getArticlesFromRequest(NewsRequest request) throws RequestException {
        if (request == null || !request.containsKeywords()) {
            throw new IllegalArgumentException("The given request was null or it does not contain any keywords!");
        }

        var httpRequest = createHttpRequest(request);

        var responseFuture = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(x -> {
                return x;
            });

        Gson gson = new Gson();
        var response = responseFuture.join();

        validateResponse(response);

        String responseBody = response.body();

        Type listOfArticlesType = new TypeToken<ArrayList<Article>>() {}.getType();

        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        var articlesJsonObject = jsonObject.get("articles"); // returns a JsonElement for that name

        List<Article> articleResponse = gson.fromJson(articlesJsonObject, listOfArticlesType);

        return articleResponse != null ? articleResponse.stream().limit(this.maxArticles).toList() : List.of();
    }
}
