package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.InvalidURIException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsRequest {
    private static final String REQUEST_PROTOCOL = "https://";
    private static final String REQUEST_HOST = "newsapi.org";
    private static final String REQUEST_ENDPOINT = "/v2/top-headlines";
    private static final String QUERY_PARAMS_START = "?";
    private static final String QUERY_APIKEY = "apiKey=";
    //TODO -> User builder pattern to be able to set the key from the builder and then create an instance object
    //Instead of using a singleton object
    private static String API_KEY = "2fc30dc9238b4c22badf4228ab764b3d";
    private static final String QUERY_PARAMS_SEPERATOR = "&";
    private static final String BASE_URL = REQUEST_PROTOCOL + REQUEST_HOST + REQUEST_ENDPOINT
        + QUERY_PARAMS_START + QUERY_APIKEY + API_KEY;
    private static final String QUERY_KEYWORDS = "q=";
    private static final String QUERY_COUNTRY = "country=";
    private static final String QUERY_CATEGORY = "category=";
    
    private String url;
    private List<String> keywordsIncluded;
    private List<String> keywordsExcluded;
    private NewsCountry country;
    private NewsCategory category;
    private HttpRequest request;

    private NewsRequest(NewsRequestBuilder builder) {
        this.url = builder.urlBuilder.toString();
        this.keywordsIncluded = builder.keywordsIncluded;
        this.keywordsExcluded = builder.keywordsExcluded;
        this.country = builder.country;
        this.category = builder.category;
        this.request = builder.request;
    }

    public static NewsRequestBuilder builder(String url) {
        return new NewsRequestBuilder(url);
    }

    public static NewsRequestBuilder builder() {
        return new NewsRequestBuilder();
    }

    public HttpRequest getHttpRequest() {
        return this.request;
    }

    public boolean containsKeywords() {
        return this.keywordsIncluded.size() + this.keywordsExcluded.size() > 0;
    }

    //Request Builder class
    public static class NewsRequestBuilder {
        private StringBuilder urlBuilder;
        private List<String> keywordsIncluded;
        private List<String> keywordsExcluded;
        private NewsCountry country;
        private NewsCategory category;
        private HttpRequest request;

        private boolean containsQueryStart;

        private NewsRequestBuilder() {
            this(BASE_URL);
        }

        private NewsRequestBuilder(String url) {
            this.urlBuilder = new StringBuilder(url);
            this.containsQueryStart = url.contains(QUERY_PARAMS_START);
            this.keywordsIncluded = new ArrayList<>();
            this.keywordsExcluded = new ArrayList<>();
            this.country = NewsCountry.NULL;
            this.category = NewsCategory.NULL;
        }

        public NewsRequestBuilder addKeywordsIncluded(String... keywords) {
            this.keywordsIncluded.addAll(Arrays.stream(keywords).toList());
            return this;
        }

        /**
         * This method doesn't work because the API doesn't support advanced keyword search
         * */
        public NewsRequestBuilder addKeywordsExcluded(String... keywords) {
            this.keywordsExcluded.addAll(Arrays.stream(keywords).toList());
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

        public NewsRequest build() {
            if (!this.containsQueryStart) {
                this.urlBuilder.append(QUERY_PARAMS_START);
            }
            else {
                urlBuilder.append(QUERY_PARAMS_SEPERATOR);
            }

            //Append the keywords to the query
            appendKeywordsToQuery();
            urlBuilder.append(QUERY_PARAMS_SEPERATOR);

            appendCountryToQuery();
            urlBuilder.append(QUERY_PARAMS_SEPERATOR);

            appendCategoryToQuery();
            urlBuilder.append(QUERY_PARAMS_SEPERATOR);

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

        private void appendCategoryToQuery() {
            if (this.category != NewsCategory.NULL) {
                urlBuilder.append(QUERY_CATEGORY);
                urlBuilder.append(this.category);
            }
        }

        private void appendCountryToQuery() {
            if (this.country != NewsCountry.NULL) {
                urlBuilder.append(QUERY_COUNTRY);
                urlBuilder.append(this.country);
            }
        }

        private void appendKeywordsToQuery() {
            if (!this.keywordsIncluded.isEmpty() || !this.keywordsExcluded.isEmpty()) {
                urlBuilder.append(QUERY_KEYWORDS);

                StringBuilder keywordArgs = new StringBuilder(String.join("", this.keywordsIncluded.stream().map(
                    x -> "+" + x
                ).toList()));

                if (!this.keywordsExcluded.isEmpty()) {
                    keywordArgs.append(" NOT ");
                }

                keywordArgs.append(String.join(" NOT ", this.keywordsExcluded.stream().map(
                    x -> "-" + x
                ).toList()));

//                String encodedKeywordArgs = URLEncoder.encode(keywordArgs.toString(), StandardCharsets.UTF_8);
                String encodedKeywordArgs = keywordArgs.toString();

                urlBuilder.append(encodedKeywordArgs);
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
