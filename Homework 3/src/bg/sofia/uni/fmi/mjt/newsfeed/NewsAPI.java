package bg.sofia.uni.fmi.mjt.newsfeed;

//The API key: 2fc30dc9238b4c22badf4228ab764b3d

import bg.sofia.uni.fmi.mjt.newsfeed.article.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RequestException;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;

import java.util.Collection;

public interface NewsAPI {

    /**
     * @param keywords by which to filter the articles
     * @return the articles which contain all the keywords
     */
    Collection<Article> getArticlesFromRequest(NewsRequest request) throws RequestException;
    Collection<Article> getArticlesFromRequest(NewsRequest request, int pages) throws RequestException;
    Collection<Article> getArticlesFromRequest(NewsRequest request, int pages, int startingPage) throws RequestException;
}
