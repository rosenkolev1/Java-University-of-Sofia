package bg.sofia.uni.fmi.mjt.newsfeed.article;

public record Article(ArticleSource source, String author, String title, String description,
                      String url, String urlToImage, String publishedAt, String content) {
}
