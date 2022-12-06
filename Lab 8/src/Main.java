import bg.sofia.uni.fmi.mjt.netflix.NetflixRecommender;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var datasetPath = Path.of("dataset.csv");

        try (var reader = Files.newBufferedReader(datasetPath)){

            var recommender = new NetflixRecommender(reader);
            var contents = recommender.getAllContent();

            var uniqueGenres = recommender.getAllGenres();

            var longestMovie = recommender.getTheLongestMovie();

            var groupedContent = recommender.groupContentByType();

            var similarContent = recommender.getSimilarContent(longestMovie);

            var searchByKeywords = recommender.getContentByKeywords("SuCh", "mOnty");

            var topRatedContent = recommender.getTopNRatedContent(20);

            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}