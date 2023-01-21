package bg.sofia.uni.fmi.mjt.newsfeed.request;

import java.util.*;

public class NewsRequest {
    public static final int BASE_PAGE_SIZE = 50;
    public static final int BASE_PAGE = 1;

    private final List<String> keywords;
    private final NewsCountry country;
    private final NewsCategory category;
    private final int page;
    private final int pageSize;

    private NewsRequest(NewsRequestBuilder builder) {
        this.keywords = builder.keywords;
        this.country = builder.country;
        this.category = builder.category;
        this.page = builder.page;
        this.pageSize = builder.pageSize;
    }

    public static NewsRequestBuilder builder() {
        return new NewsRequestBuilder();
    }

    public static NewsRequestBuilder builder(NewsRequest request) {
        return new NewsRequestBuilder(request);
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
        private List<String> keywords;
        private NewsCountry country;
        private NewsCategory category;
        private int page;
        private int pageSize;

        private NewsRequestBuilder() {
            this.keywords = new ArrayList<>();
            this.country = NewsCountry.NULL;
            this.category = NewsCategory.NULL;
            this.page = BASE_PAGE;
            this.pageSize = BASE_PAGE_SIZE;
        }

        private NewsRequestBuilder(NewsRequest request) {
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
            return new NewsRequest(this);
        }
    }
}
