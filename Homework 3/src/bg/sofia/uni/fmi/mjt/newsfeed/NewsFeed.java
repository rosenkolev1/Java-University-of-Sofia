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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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

    public static final int BASE_MAX_ARTICLES = 120;

    private static final Gson GSON = new Gson();

    private static NewsFeed instance = null;

    private HttpClient client;

    private final String apiKey;

    public final int maxArticles;

    private NewsFeed(String apiKey, int maxArticles) {
        this.apiKey = apiKey;
        this.maxArticles = maxArticles;
        this.client = HttpClient.newBuilder().build();
    }

    public static NewsFeed getInstance(String apiKey) {
        if (instance != null) {
            return instance;
        } else {
            return getInstance(apiKey, BASE_MAX_ARTICLES);
        }
    }

    public static NewsFeed getInstance(String apiKey, int maxArticles) {
        if (instance != null) {
            return instance;
        } else {
            instance = new NewsFeed(apiKey, maxArticles);
            return instance;
        }
    }

    public String getBaseUrl() {
        return REQUEST_PROTOCOL + REQUEST_HOST + REQUEST_ENDPOINT
            + QUERY_PARAMS_START + QUERY_APIKEY + apiKey;
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

    private CompletableFuture<HttpResponse<String>> createCompletableFutureForRequest(NewsRequest request) {
        var httpRequest = createHttpRequest(request);

        var responseFuture = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(x -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return x;
            });

        return responseFuture;
    }

    private JsonObject createJsonObjectFromResponse(HttpResponse<String> response) throws RequestException {
        String responseBody = response.body();

        JsonObject jsonObject = GSON.fromJson(responseBody, JsonObject.class);

        return jsonObject;
    }

    private List<Article> getArticlesFromJsonResponse(JsonObject jsonObject) {

        var articlesJsonObject = jsonObject.get("articles"); // returns a JsonElement for that name

        Type listOfArticlesType = new TypeToken<ArrayList<Article>>() {}.getType();
        List<Article> articleResponse = GSON.fromJson(articlesJsonObject, listOfArticlesType);

        return articleResponse != null ? articleResponse.stream().limit(this.maxArticles).toList() : List.of();
    }

    private void validateRequest(NewsRequest request) {
        if (request == null || !request.containsKeywords()) {
            throw new IllegalArgumentException("The given request was null or it does not contain any keywords!");
        }

        if (request.pageSize() <= 0) {
            throw new IllegalArgumentException("The pageSize of the request should be positive!");
        }

        if (request.page() <= 0) {
            throw new IllegalArgumentException("The page of the request should be positive!");
        }
    }

    public HttpRequest createHttpRequest(NewsRequest request) {
        var urlBuilder = new StringBuilder(this.getBaseUrl());

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

        validateRequest(request);

        Collection<Article> allArticles = new ArrayList<>();

        var intialResponse = createCompletableFutureForRequest(request).join();
        validateResponse(intialResponse);

        var initialResponseJson = createJsonObjectFromResponse(intialResponse);

        int skippedResults = request.pageSize() * (request.page() - 1);

        int totalPossibleResults = GSON.fromJson(initialResponseJson.get("totalResults"), int.class) - skippedResults;

        var requestsNeeded = (int)Math.ceil((double)totalPossibleResults / request.pageSize());

        return getArticlesFromRequest(request, requestsNeeded);
    }

    public Collection<Article> getArticlesFromRequest(NewsRequest request, int pagesCount) throws RequestException {

        validateRequest(request);

        if (pagesCount < 0) {
            throw new IllegalArgumentException("The pagesCount should be non-negative");
        }

        Collection<Article> allArticles = new ArrayList<>();

        Collection<CompletableFuture<HttpResponse<String>>> responseFutures = new ArrayList<>();

        for (int i = request.page(); i < request.page() + pagesCount; i++) {
            NewsRequest pageRequest = NewsRequest.builder(request)
                .addPage(i)
                .build();

            var responseFuture = createCompletableFutureForRequest(pageRequest);

            responseFutures.add(responseFuture);
        }

        var allResponses = CompletableFuture.allOf(responseFutures.toArray(new CompletableFuture[responseFutures.size()]))
            .thenApply(x -> responseFutures.stream().map(y -> y.join()).toList())
            .join();

        for (var response : allResponses) {
            validateResponse(response);

            var jsonObject = createJsonObjectFromResponse(response);
            var articles = getArticlesFromJsonResponse(jsonObject);

            allArticles.addAll(articles);

            if (allArticles.size() >= maxArticles) {
                return allArticles;
            }
        }

        return allArticles;
    }

    public Collection<Article> getArticlesFromRequest(NewsRequest request) throws RequestException {

        validateRequest(request);

        var response = createCompletableFutureForRequest(request).join();

        validateResponse(response);

        var jsonObject = createJsonObjectFromResponse(response);

        List<Article> articles = getArticlesFromJsonResponse(jsonObject);

        return articles;
    }
}
