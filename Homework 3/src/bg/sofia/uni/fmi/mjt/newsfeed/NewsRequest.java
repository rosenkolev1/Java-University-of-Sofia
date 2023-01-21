package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.InvalidURIException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NewsRequest {
    private static final String REQUEST_PROTOCOL = "https://";
    private static final String REQUEST_HOST = "newsapi.org";
    private static final String REQUEST_ENDPOINT = "/v2/top-headlines";
    private static final String QUERY_PARAMS_START = "?";
    private static final String QUERY_APIKEY = "apiKey=";
    private static final String QUERY_PAGESIZE = "pageSize=";
    private static final String QUERY_PAGE = "page=";
    private static final String QUERY_PARAMS_SEPERATOR = "&";
    private static final String QUERY_KEYWORDS = "q=";
    private static final String QUERY_COUNTRY = "country=";
    private static final String QUERY_CATEGORY = "category=";

    private static String API_KEY = "2fc30dc9238b4c22badf4228ab764b3d";
    private static int BASE_PAGE_SIZE = 50;
    private static int BASE_PAGE = 1;

    private static final String BASE_URL = REQUEST_PROTOCOL + REQUEST_HOST + REQUEST_ENDPOINT
        + QUERY_PARAMS_START + QUERY_APIKEY + API_KEY;

    private final String baseUrl;
    private final String url;
    private final List<String> keywords;
    private final NewsCountry country;
    private final NewsCategory category;
    private final HttpRequest request;
    private final int page;
    private final int pageSize;

    private NewsRequest(NewsRequestBuilder builder) {
        this.baseUrl = builder.baseUrl;
        this.url = builder.urlBuilder.toString();
        this.keywords = builder.keywords;
        this.country = builder.country;
        this.category = builder.category;
        this.request = builder.request;
        this.page = builder.page;
        this.pageSize = builder.pageSize;
    }

    public static NewsRequestBuilder builder(String baseUrl) {
        return new NewsRequestBuilder(baseUrl);
    }

    public static NewsRequestBuilder builder() {
        return new NewsRequestBuilder();
    }

    public static NewsRequestBuilder builder(NewsRequest request) {
        return new NewsRequestBuilder(request);
    }

    public HttpRequest httpRequest() {
        return this.request;
    }

    public String baseUrl() {
        return this.baseUrl;
    }
    public String url() {
        return this.url;
    }
    public List<String> keywords() {
        return Collections.unmodifiableList(this.keywords);
    }
    public NewsCountry country() {
        return this.country;
    }
    public NewsCategory category() {
        return this.category;
    }
    public int page() {
        return this.page;
    }
    public int pageSize() {
        return this.pageSize;
    }

    public boolean containsKeywords() {
        return this.keywords.size() + this.keywords.size() > 0;
    }

    //Request Builder class
    public static class NewsRequestBuilder {
        private StringBuilder urlBuilder;
        private String baseUrl;
        private List<String> keywords;
        private NewsCountry country;
        private NewsCategory category;
        private HttpRequest request;
        private int page;
        private int pageSize;

        private boolean containsQueryStart;

        private NewsRequestBuilder() {
            this(BASE_URL);
        }

        private NewsRequestBuilder(String baseUrl) {
            this.baseUrl = baseUrl;
            this.urlBuilder = new StringBuilder(baseUrl);
            this.containsQueryStart = baseUrl.contains(QUERY_PARAMS_START);
            this.keywords = new ArrayList<>();
            this.country = NewsCountry.NULL;
            this.category = NewsCategory.NULL;
            this.page = BASE_PAGE;
            this.pageSize = BASE_PAGE_SIZE;
        }

        private NewsRequestBuilder(NewsRequest request) {
            this.baseUrl = request.baseUrl;
            this.urlBuilder = new StringBuilder(request.baseUrl);;
            this.containsQueryStart = request.baseUrl.contains(QUERY_PARAMS_START);
            this.keywords = new ArrayList<>(request.keywords);
            this.country = request.country;
            this.category = request.category;
            this.page = request.page;
            this.pageSize = request.pageSize;
        }

        public NewsRequestBuilder addKeywords(String... keywords) {
            this.keywords.addAll(Arrays.stream(keywords).toList());
            return this;
        }

        public NewsRequestBuilder addCountry(NewsCountry country) {
            this.country = country;
            return this;
        }

        public NewsRequestBuilder addCategory(NewsCategory category) {
            this.category = category;
            return this;
        }

        public NewsRequestBuilder addPage(int page) {
            this.page = page;
            return this;
        }

        public NewsRequestBuilder addPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public NewsRequest build() {
            if (!this.containsQueryStart) {
                this.urlBuilder.append(QUERY_PARAMS_START);
            }
            else {
                urlBuilder.append(QUERY_PARAMS_SEPERATOR);
            }

            //Append the keywords to the query
            appendKeywordsToQuery();

            appendCountryToQuery();

            appendCategoryToQuery();

            appendPageToQuery();

            appendPageSizeToQuery();

            //Remove trailing query symbols in query
            removeTrailingQuerySymbols();

            this.request = this.buildRequestFromURL(urlBuilder.toString());

            return new NewsRequest(this);
        }

        private void removeTrailingQuerySymbols() {
            while (true){
                if (urlBuilder.toString().endsWith(QUERY_PARAMS_SEPERATOR)) {
                    urlBuilder.delete(
                        urlBuilder.length() - QUERY_PARAMS_SEPERATOR.length(),
                        urlBuilder.length()
                    );
                }
                else if (urlBuilder.toString().endsWith(QUERY_PARAMS_START)) {
                    urlBuilder.delete(
                        urlBuilder.length() - QUERY_PARAMS_START.length(),
                        urlBuilder.length()
                    );
                }
                else break;
            }
        }

        private void appendPageSizeToQuery() {
            if (this.pageSize > 0) {
                urlBuilder.append(QUERY_PAGESIZE);
                urlBuilder.append(this.pageSize);

                urlBuilder.append(QUERY_PARAMS_SEPERATOR);
            }
        }

        private void appendPageToQuery() {
            if (this.page > 1) {
                urlBuilder.append(QUERY_PAGE);
                urlBuilder.append(this.page);

                urlBuilder.append(QUERY_PARAMS_SEPERATOR);
            }
        }

        private void appendCategoryToQuery() {
            if (this.category != NewsCategory.NULL) {
                urlBuilder.append(QUERY_CATEGORY);
                urlBuilder.append(this.category);

                urlBuilder.append(QUERY_PARAMS_SEPERATOR);
            }
        }

        private void appendCountryToQuery() {
            if (this.country != NewsCountry.NULL) {
                urlBuilder.append(QUERY_COUNTRY);
                urlBuilder.append(this.country);

                urlBuilder.append(QUERY_PARAMS_SEPERATOR);
            }
        }

        private void appendKeywordsToQuery() {
            if (!this.keywords.isEmpty() || !this.keywords.isEmpty()) {
                urlBuilder.append(QUERY_KEYWORDS);

                StringBuilder keywordArgs = new StringBuilder(String.join("", this.keywords.stream().map(
                    x -> "+" + x
                ).toList()));

                urlBuilder.append(keywordArgs.toString());

                urlBuilder.append(QUERY_PARAMS_SEPERATOR);
            }
        }

        private HttpRequest buildRequestFromURL(String url) {
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
    }
}
