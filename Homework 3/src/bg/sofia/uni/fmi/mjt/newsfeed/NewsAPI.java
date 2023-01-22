package bg.sofia.uni.fmi.mjt.newsfeed;

//The API key: 2fc30dc9238b4c22badf4228ab764b3d

import bg.sofia.uni.fmi.mjt.newsfeed.article.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RequestException;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;

import java.util.Collection;

public interface NewsAPI {

    /**
     * Returns all the articles which match the given request criteria.
     * Returns only the articles from the requests' page property.
     * @param request by which to filter the articles.
     * @return the articles which contain all the keywords
     */
    Collection<Article> getArticlesFromRequest(NewsRequest request) throws RequestException;

    /**
     * Returns all the articles which match the given request criteria, starting from <code>request.page()</code> and taking the next <code>pagesCount</code> pages.
     * If <code>pagesCount</code> is more than the possible pages to get, then this method returns all possible pages instead.
     * @param request by which to filter the articles.
     *        pagesCount by which to determine how many pages of articles to return.
     * @return the articles which contain all the keywords
     */
    Collection<Article> getArticlesFromRequest(NewsRequest request, int pagesCount) throws RequestException;

    /**
     * Returns all the articles which match the given request criteria, starting from <code>request.page()</code> and taking all pages after it.
     * @param request by which to filter the articles.
     * @return the articles which contain all the keywords
     */
    Collection<Article> getAllArticlesFromRequest(NewsRequest request) throws RequestException;
}
