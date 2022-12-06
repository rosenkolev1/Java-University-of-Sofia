package bg.sofia.uni.fmi.mjt.netflix;

import bg.sofia.uni.fmi.mjt.netflix.NetflixRecommender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NetflixRecommenderTest {

    private NetflixRecommender recommender;
    private static final Path DATASET_PATH = Path.of("datasetTest.csv");

    private static final String EMPTY_DATASET =
        "id,title,type,description,release_year,runtime,genres,seasons,imdb_id,imdb_score,imdb_votes";

    @Test
    void testGetAllContentsWorkEmpty() {
        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            var allContents = recommender.getAllContent();
            List<Content> expectedContents = new ArrayList<>();

            Assertions.assertIterableEquals(
                expectedContents,
                allContents,
                "The actual contents are not empty, but they should be!"
            );

        }
    }

    @Test
    void testGetAllContentsWorks() {
        try (var reader = Files.newBufferedReader(DATASET_PATH)){
            recommender = new NetflixRecommender(reader);

            var allContents = recommender.getAllContent();

            var expectedContents = List.of(
                new Content("tm1", "Taxi Driver", ContentType.MOVIE,
                    "Some description lol",
                    1976, 114,
                    List.of("drama", "crime"), -1,
                    "tt1", 8.2, 808582.0),

                new Content("tm2", "Deliverance", ContentType.MOVIE,
                    "Some description lol",
                    1972, 109,
                    List.of("drama", "action", "thriller", "european"), -1,
                    "tt2", 7.7, 107673.0),

                new Content("tm3", "Monty Python and the Holy Grail", ContentType.MOVIE,
                    "Some description lol",
                    1975, 91,
                    List.of("fantasy", "action", "comedy"), -1,
                    "tt3", 8.2, 534486.0),

                new Content("tm4", "The Dirty Dozen", ContentType.MOVIE,
                    "Some description lol",
                    1967, 150,
                    List.of("war", "action"), -1,
                    "tt4", 7.7, 72662.0),

                new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                    "Some description lol",
                    1969, 30,
                    List.of("comedy", "european"), 1,
                    "tt5", 8.8, 73424.0),

                new Content("tm6", "Life of Brian", ContentType.MOVIE,
                    "Some description lol",
                    1979, 94,
                    List.of("comedy"), -1,
                    "tt6", 8, 395024),

                new Content("tm7", "Dirty Harry", ContentType.SHOW,
                    "Some description lol",
                    1971, 170,
                    List.of("thriller", "action", "crime"), 3,
                    "tt7", 7.7, 155051),

                new Content("tm8", "Bonnie and Clyde", ContentType.MOVIE,
                    "Some description lol",
                    1967, 110,
                    List.of("crime", "drama", "action"), -1,
                    "tt8", 7.7, 112048),

                new Content("tm9", "The Blue Lagoon", ContentType.MOVIE,
                    "Some description lol",
                    1980, 104,
                    List.of("romance", "action", "drama"), -1,
                    "tt9", 5.8, 69844),

                new Content("tm10", "The Guns of Navarone", ContentType.MOVIE,
                    "Some description lol",
                    1961, 158,
                    List.of("action", "drama", "war"), -1,
                    "tt10", 7.5, 50748)
            );

            Assertions.assertIterableEquals(
                expectedContents,
                allContents,
                "The contents differ from the expected somewhere!"
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllGenresWorkEmpty() {
        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            var allGenres = recommender.getAllGenres();
            List<Content> expectedContents = new ArrayList<>();

            Assertions.assertIterableEquals(
                expectedContents,
                allGenres,
                "The actual genres are not empty, but they should be!"
            );

        }
    }

    @Test
    void testGetAllGenresWork() {
        try (var reader = Files.newBufferedReader(DATASET_PATH)){
            recommender = new NetflixRecommender(reader);

            var allGenres = recommender.getAllGenres();

            List<String> expectedGenres = List.of(
                "drama", "crime", "action", "thriller", "european", "fantasy", "comedy", "war", "romance"
            );

            Assertions.assertIterableEquals(
                expectedGenres,
                allGenres,
                "The actual genres are incorrect!"
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetLongestMovieThrowsNoSuchElementException() {

        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            Assertions.assertThrows(NoSuchElementException.class,
                () -> recommender.getTheLongestMovie(),
                "Expected NoSuchElementException to be thrown, but nothing was thrown!");
        }

    }

    @Test
    void testGetLongestMovieWorks() {
        try (var reader = Files.newBufferedReader(DATASET_PATH)){
            recommender = new NetflixRecommender(reader);

            var longestMovie = recommender.getTheLongestMovie();
            var expectedLongestMovie = new Content("tm10", "The Guns of Navarone", ContentType.MOVIE,
                "Some description lol",
                1961, 158,
                List.of("action", "drama", "war"), -1,
                "tt10", 7.5, 50748);

            Assertions.assertEquals(
                expectedLongestMovie,
                longestMovie,
                "The actual longest movie differs from the expected!"
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGroupContentByTypeWorksEmpty() {
        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            Map<ContentType, Set<Content>> expectedGroupContent = new HashMap<>();

            Assertions.assertEquals(expectedGroupContent,
                recommender.groupContentByType(),
                "Expected the grouped content to be empty");
        }
    }

    @Test
    void testGroupContentByTypeWorks() {
        try (var reader = Files.newBufferedReader(DATASET_PATH)){
            recommender = new NetflixRecommender(reader);

            var groupContent = recommender.groupContentByType();

            Map<ContentType, Set<Content>> expectedGroupContent = Map.ofEntries(
                Map.entry(ContentType.MOVIE, Set.of(
                    new Content("tm1", "Taxi Driver", ContentType.MOVIE,
                        "Some description lol",
                        1976, 114,
                        List.of("drama", "crime"), -1,
                        "tt1", 8.2, 808582.0),

                    new Content("tm2", "Deliverance", ContentType.MOVIE,
                        "Some description lol",
                        1972, 109,
                        List.of("drama", "action", "thriller", "european"), -1,
                        "tt2", 7.7, 107673.0),

                    new Content("tm3", "Monty Python and the Holy Grail", ContentType.MOVIE,
                        "Some description lol",
                        1975, 91,
                        List.of("fantasy", "action", "comedy"), -1,
                        "tt3", 8.2, 534486.0),

                    new Content("tm4", "The Dirty Dozen", ContentType.MOVIE,
                        "Some description lol",
                        1967, 150,
                        List.of("war", "action"), -1,
                        "tt4", 7.7, 72662.0),

                    new Content("tm6", "Life of Brian", ContentType.MOVIE,
                        "Some description lol",
                        1979, 94,
                        List.of("comedy"), -1,
                        "tt6", 8, 395024),

                    new Content("tm8", "Bonnie and Clyde", ContentType.MOVIE,
                        "Some description lol",
                        1967, 110,
                        List.of("crime", "drama", "action"), -1,
                        "tt8", 7.7, 112048),

                    new Content("tm9", "The Blue Lagoon", ContentType.MOVIE,
                        "Some description lol",
                        1980, 104,
                        List.of("romance", "action", "drama"), -1,
                        "tt9", 5.8, 69844),

                    new Content("tm10", "The Guns of Navarone", ContentType.MOVIE,
                        "Some description lol",
                        1961, 158,
                        List.of("action", "drama", "war"), -1,
                        "tt10", 7.5, 50748)

                )),
                Map.entry(ContentType.SHOW, Set.of(
                    new Content("tm7", "Dirty Harry", ContentType.SHOW,
                        "Some description lol",
                        1971, 170,
                        List.of("thriller", "action", "crime"), 3,
                        "tt7", 7.7, 155051),

                    new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                        "Some description lol",
                        1969, 30,
                        List.of("comedy", "european"), 1,
                        "tt5", 8.8, 73424.0)
                ))
            );

            Assertions.assertEquals(expectedGroupContent, groupContent,
                "The contents of the 2 groups differ somewhere!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetTopNRatedContentThrowsIllegalArgumentExeptionBecauseNegativeN() {
        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            List<Content> expectedContent = new ArrayList<>();

            Assertions.assertThrows(IllegalArgumentException.class,
                () -> recommender.getTopNRatedContent(-1),
                "Expected IllegalArgumentException to be thrown, but nothing was thrown");
        }
    }

    @Test
    void testGetTopNRatedContentWorksEmpty() {
        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            List<Content> expectedContent = new ArrayList<>();

            Assertions.assertEquals(expectedContent,
                recommender.getTopNRatedContent(4),
                "Expected the grouped content to be empty");
        }
    }

    @Test
    void testGetTopNRatedContentWorks() {
        try (var reader = Files.newBufferedReader(DATASET_PATH)){
            recommender = new NetflixRecommender(reader);

            //5,1,3,6,4
            var topContents = recommender.getTopNRatedContent(5);
            var expectedContents = List.of(

                new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                    "Some description lol",
                    1969, 30,
                    List.of("comedy", "european"), 1,
                    "tt5", 8.8, 73424.0),

                new Content("tm1", "Taxi Driver", ContentType.MOVIE,
                    "Some description lol",
                    1976, 114,
                    List.of("drama", "crime"), -1,
                    "tt1", 8.2, 808582.0),

                new Content("tm3", "Monty Python and the Holy Grail", ContentType.MOVIE,
                    "Some description lol",
                    1975, 91,
                    List.of("fantasy", "action", "comedy"), -1,
                    "tt3", 8.2, 534486.0),

                new Content("tm6", "Life of Brian", ContentType.MOVIE,
                    "Some description lol",
                    1979, 94,
                    List.of("comedy"), -1,
                    "tt6", 8, 395024),

                new Content("tm4", "The Dirty Dozen", ContentType.MOVIE,
                    "Some description lol",
                    1967, 150,
                    List.of("war", "action"), -1,
                    "tt4", 7.7, 72662.0)
            );

            Assertions.assertIterableEquals(
                expectedContents,
                topContents,
                "The top contents differ from the expected!"
            );

            //5,1,3,6,4,2,8,7,10,9
            topContents = recommender.getTopNRatedContent(13);
            expectedContents = List.of(
                new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                    "Some description lol",
                    1969, 30,
                    List.of("comedy", "european"), 1,
                    "tt5", 8.8, 73424.0),

                new Content("tm1", "Taxi Driver", ContentType.MOVIE,
                    "Some description lol",
                    1976, 114,
                    List.of("drama", "crime"), -1,
                    "tt1", 8.2, 808582.0),

                new Content("tm3", "Monty Python and the Holy Grail", ContentType.MOVIE,
                    "Some description lol",
                    1975, 91,
                    List.of("fantasy", "action", "comedy"), -1,
                    "tt3", 8.2, 534486.0),

                new Content("tm6", "Life of Brian", ContentType.MOVIE,
                    "Some description lol",
                    1979, 94,
                    List.of("comedy"), -1,
                    "tt6", 8, 395024),

                new Content("tm4", "The Dirty Dozen", ContentType.MOVIE,
                    "Some description lol",
                    1967, 150,
                    List.of("war", "action"), -1,
                    "tt4", 7.7, 72662.0),

                new Content("tm2", "Deliverance", ContentType.MOVIE,
                    "Some description lol",
                    1972, 109,
                    List.of("drama", "action", "thriller", "european"), -1,
                    "tt2", 7.7, 107673.0),

                new Content("tm8", "Bonnie and Clyde", ContentType.MOVIE,
                    "Some description lol",
                    1967, 110,
                    List.of("crime", "drama", "action"), -1,
                    "tt8", 7.7, 112048),

                new Content("tm7", "Dirty Harry", ContentType.SHOW,
                    "Some description lol",
                    1971, 170,
                    List.of("thriller", "action", "crime"), 3,
                    "tt7", 7.7, 155051),

                new Content("tm10", "The Guns of Navarone", ContentType.MOVIE,
                    "Some description lol",
                    1961, 158,
                    List.of("action", "drama", "war"), -1,
                    "tt10", 7.5, 50748),

                new Content("tm9", "The Blue Lagoon", ContentType.MOVIE,
                    "Some description lol",
                    1980, 104,
                    List.of("romance", "action", "drama"), -1,
                    "tt9", 5.8, 69844)
            );

            Assertions.assertIterableEquals(
                expectedContents,
                topContents,
                "The top contents differ from the expected!"
            );

            topContents = recommender.getTopNRatedContent(0);
            expectedContents = new ArrayList<>();

            Assertions.assertIterableEquals(
                expectedContents,
                topContents,
                "The top contents differ from the expected!"
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetSimilarContentWorksEmpty() {
        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            var someContent = new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                "Some description lol",
                1969, 30,
                List.of("comedy", "european"), 1,
                "tt5", 8.8, 73424.0);

            var similarContent = recommender.getSimilarContent(someContent);
            List<Content> expectedContent = List.of(

            );

            Assertions.assertEquals(expectedContent,
                expectedContent,
                "Expected the similar content to be empty");
        }
    }

    @Test
    void testGetSimilarContentWorks() {
        try (var reader = Files.newBufferedReader(DATASET_PATH)){
            recommender = new NetflixRecommender(reader);

            var similarContent1 = new Content("tm3", "Monty Python and the Holy Grail", ContentType.MOVIE,
                "Some description lol",
                1975, 91,
                List.of("fantasy", "action", "comedy"), -1,
                "tt3", 8.2, 534486.0);
            //3,2,4,6,8,9,10,1
            var similarContents = recommender.getSimilarContent(similarContent1);
            var expectedContents = List.of(

                new Content("tm3", "Monty Python and the Holy Grail", ContentType.MOVIE,
                    "Some description lol",
                    1975, 91,
                    List.of("fantasy", "action", "comedy"), -1,
                    "tt3", 8.2, 534486.0),

                new Content("tm2", "Deliverance", ContentType.MOVIE,
                    "Some description lol",
                    1972, 109,
                    List.of("drama", "action", "thriller", "european"), -1,
                    "tt2", 7.7, 107673.0),

                new Content("tm4", "The Dirty Dozen", ContentType.MOVIE,
                    "Some description lol",
                    1967, 150,
                    List.of("war", "action"), -1,
                    "tt4", 7.7, 72662.0),

                new Content("tm6", "Life of Brian", ContentType.MOVIE,
                    "Some description lol",
                    1979, 94,
                    List.of("comedy"), -1,
                    "tt6", 8, 395024),

                new Content("tm8", "Bonnie and Clyde", ContentType.MOVIE,
                    "Some description lol",
                    1967, 110,
                    List.of("crime", "drama", "action"), -1,
                    "tt8", 7.7, 112048),

                new Content("tm9", "The Blue Lagoon", ContentType.MOVIE,
                    "Some description lol",
                    1980, 104,
                    List.of("romance", "action", "drama"), -1,
                    "tt9", 5.8, 69844),

                new Content("tm10", "The Guns of Navarone", ContentType.MOVIE,
                    "Some description lol",
                    1961, 158,
                    List.of("action", "drama", "war"), -1,
                    "tt10", 7.5, 50748),

                new Content("tm1", "Taxi Driver", ContentType.MOVIE,
                    "Some description lol",
                    1976, 114,
                    List.of("drama", "crime"), -1,
                    "tt1", 8.2, 808582.0)
            );

            Assertions.assertIterableEquals(
                expectedContents,
                similarContents,
                "The similar contents are not the same as expected!"
            );

            var similarContent2 = new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                "Some description lol",
                1969, 30,
                List.of("comedy", "european"), 1,
                "tt5", 8.8, 73424.0);

            similarContents = recommender.getSimilarContent(similarContent2);

            expectedContents = List.of(

                new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                    "Some description lol",
                    1969, 30,
                    List.of("comedy", "european"), 1,
                    "tt5", 8.8, 73424.0),

                new Content("tm7", "Dirty Harry", ContentType.SHOW,
                    "Some description lol",
                    1971, 170,
                    List.of("thriller", "action", "crime"), 3,
                    "tt7", 7.7, 155051)
            );

            Assertions.assertIterableEquals(
                expectedContents,
                similarContents,
                "The similar contents are not the same as expected!"
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetContentByKeywordsEmpty() {
        try (var reader = new StringReader(EMPTY_DATASET)){
            recommender = new NetflixRecommender(reader);

            var matchingContent = recommender.getContentByKeywords("ss");
            Set<Content> expectedContent = Set.of();

            Assertions.assertEquals(expectedContent,
                matchingContent,
                "Expected the matching content to be empty");
        }
    }

    @Test
    void testGetContentByKeywordsWorks() {
        try (var reader = Files.newBufferedReader(DATASET_PATH)){
            recommender = new NetflixRecommender(reader);

            var matchingContents = recommender.getContentByKeywords("lol");

            var expectedContents = Set.of(
                new Content("tm1", "Taxi Driver", ContentType.MOVIE,
                    "Some description lol",
                    1976, 114,
                    List.of("drama", "crime"), -1,
                    "tt1", 8.2, 808582.0),

                new Content("tm2", "Deliverance", ContentType.MOVIE,
                    "Some description lol",
                    1972, 109,
                    List.of("drama", "action", "thriller", "european"), -1,
                    "tt2", 7.7, 107673.0),

                new Content("tm3", "Monty Python and the Holy Grail", ContentType.MOVIE,
                    "Some description lol",
                    1975, 91,
                    List.of("fantasy", "action", "comedy"), -1,
                    "tt3", 8.2, 534486.0),

                new Content("tm4", "The Dirty Dozen", ContentType.MOVIE,
                    "Some description lol",
                    1967, 150,
                    List.of("war", "action"), -1,
                    "tt4", 7.7, 72662.0),

                new Content("tm5", "Monty Python's Flying Circus", ContentType.SHOW,
                    "Some description lol",
                    1969, 30,
                    List.of("comedy", "european"), 1,
                    "tt5", 8.8, 73424.0),

                new Content("tm6", "Life of Brian", ContentType.MOVIE,
                    "Some description lol",
                    1979, 94,
                    List.of("comedy"), -1,
                    "tt6", 8, 395024),

                new Content("tm7", "Dirty Harry", ContentType.SHOW,
                    "Some description lol",
                    1971, 170,
                    List.of("thriller", "action", "crime"), 3,
                    "tt7", 7.7, 155051),

                new Content("tm8", "Bonnie and Clyde", ContentType.MOVIE,
                    "Some description lol",
                    1967, 110,
                    List.of("crime", "drama", "action"), -1,
                    "tt8", 7.7, 112048),

                new Content("tm9", "The Blue Lagoon", ContentType.MOVIE,
                    "Some description lol",
                    1980, 104,
                    List.of("romance", "action", "drama"), -1,
                    "tt9", 5.8, 69844),

                new Content("tm10", "The Guns of Navarone", ContentType.MOVIE,
                    "Some description lol",
                    1961, 158,
                    List.of("action", "drama", "war"), -1,
                    "tt10", 7.5, 50748)
            );

            Assertions.assertEquals(expectedContents, matchingContents,
                "The matching contents differ somewhere!");

            Set<Content> finalMatchingContents = matchingContents;
            Assertions.assertThrows(UnsupportedOperationException.class,
                () -> finalMatchingContents.remove(null),
                "Expected the collection to be unmodifiable");

            matchingContents = recommender.getContentByKeywords("loL", "descRipTion", "SoME");

            Assertions.assertEquals(expectedContents, matchingContents,
                "The matching contents differ somewhere!");

            Set<Content> finalMatchingContents1 = matchingContents;
            Assertions.assertThrows(UnsupportedOperationException.class,
                () -> finalMatchingContents1.remove(null),
                "Expected the collection to be unmodifiable");

            matchingContents = recommender.getContentByKeywords("lol", "KUR");

            expectedContents = Set.of();

            Assertions.assertEquals(expectedContents, matchingContents,
                "The matching contents differ somewhere!");

            Set<Content> finalMatchingContents2 = matchingContents;
            Assertions.assertThrows(UnsupportedOperationException.class,
                () -> finalMatchingContents2.remove(null),
                "Expected the collection to be unmodifiable");

            matchingContents = recommender.getContentByKeywords("");

            expectedContents = Set.of();

            Assertions.assertEquals(expectedContents, matchingContents,
                "The matching contents differ somewhere!");

            Set<Content> finalMatchingContents3 = matchingContents;
            Assertions.assertThrows(UnsupportedOperationException.class,
                () -> finalMatchingContents3.remove(null),
                "Expected the collection to be unmodifiable");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
