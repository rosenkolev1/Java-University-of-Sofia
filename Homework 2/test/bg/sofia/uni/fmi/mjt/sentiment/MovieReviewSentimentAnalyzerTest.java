package bg.sofia.uni.fmi.mjt.sentiment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

public class MovieReviewSentimentAnalyzerTest {

    SentimentAnalyzer createAnalyzer(String stopwordsData, String movieReviewsData) {
        var stopwordsReader = new StringReader(stopwordsData);
        var movieReviewsReader = new StringReader(movieReviewsData);
        var movieReviewsWriter = new StringWriter();

        return new MovieReviewSentimentAnalyzer(stopwordsReader, movieReviewsReader, movieReviewsWriter);
    }

    SentimentAnalyzer createAnalyzer(Reader stopwordsReader, String movieReviewsData) {
        var movieReviewsReader = new StringReader(movieReviewsData);
        var movieReviewsWriter = new StringWriter();

        return new MovieReviewSentimentAnalyzer(stopwordsReader, movieReviewsReader, movieReviewsWriter);
    }

    SentimentAnalyzer createAnalyzer(Reader stopwordsReader, String movieReviewsData, Writer writer) {
        var movieReviewsReader = new StringReader(movieReviewsData);

        return new MovieReviewSentimentAnalyzer(stopwordsReader, movieReviewsReader, writer);
    }

    SentimentAnalyzer createAnalyzer() throws FileNotFoundException {
        var stopwordsReader = new FileReader("stopwords.txt");

        var movieReviewsData =
            """
            4 This was the greatest film of my entire life baby ! Greatest characters, greatest story, greatest action scenes. Loved it . So great !
            0 To be influenced chiefly by humanity's greatest shame , reality shows -- reality shows for God's sake !
            1 Spain's greatest star wattage doesn't overcome the tumult of maudlin tragedy .
            4 Gay or straight , Kissing Jessica Stein is one of the greatest date movies in years .
            2 There are a few modest laughs , but certainly no thrills .
            3 Jackie Chan movies are a guilty pleasure - he's easy to like and always leaves us laughing .
            """;

        return createAnalyzer(stopwordsReader, movieReviewsData);
    }

    SentimentAnalyzer createAnalyzer(Writer writer) throws FileNotFoundException {
        var stopwordsReader = new FileReader("stopwords.txt");

        var movieReviewsData =
            """
            4 This was the greatest film of my entire life baby ! Greatest characters, greatest story, greatest action scenes. Loved it . So great !
            0 To be influenced chiefly by humanity's greatest shame , reality shows -- reality shows for God's sake !
            1 Spain's greatest star wattage doesn't overcome the tumult of maudlin tragedy .
            4 Gay or straight , Kissing Jessica Stein is one of the greatest date movies in years .
            2 There are a few modest laughs , but certainly no thrills .
            3 Jackie Chan movies are a guilty pleasure - he's easy to like and always leaves us laughing .
            """;

        return createAnalyzer(stopwordsReader, movieReviewsData, writer);
    }

    @Test
    void testIsStopword() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertTrue(analyzer.isStopWord("for"),
            "Should be a stopword");
        Assertions.assertTrue(analyzer.isStopWord("FoR"),
            "Should be a stopword, despite the different cases of the letters.");
        Assertions.assertFalse(analyzer.isStopWord("like"),
            "Should not be a stopword");
    }

    @Test
    void testGetReviewSentiment() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        String review = "This was one of the greatest movies ever . Other shows do not compare to this absolute pleasure. Greatest everything";

        //(2.25 + 3.5 + 0 + 3 + 2.25) / 5 = 2.5
        double reviewSentiment = analyzer.getReviewSentiment(review);

        Assertions.assertEquals(2.5, reviewSentiment, 0.001,
            "The review sentiment doesn't match the expected!");
    }

    @Test
    void testGetReviewSentimentWhenReviewContainsOnlyUnknownWords() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        String review = "UNKNOWN ERR41fo0ro0s ; , mvmkjksRors";

        //-1
        double reviewSentiment = analyzer.getReviewSentiment(review);

        Assertions.assertEquals(-1, reviewSentiment, 0.001,
            "The review sentiment doesn't match the expected!");
    }

    @Test
    void testGetReviewSentimentWhenReviewContainsNoWords() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        String review = ".%&$(@)*&%^*@*(!#&*%^^%";
        double reviewSentiment = analyzer.getReviewSentiment(review);

        Assertions.assertEquals(-1, reviewSentiment, 0.001,
            "The review sentiment doesn't match the expected!");

        review = "";
        reviewSentiment = analyzer.getReviewSentiment(review);

        Assertions.assertEquals(-1, reviewSentiment, 0.001,
            "The review sentiment doesn't match the expected!");
    }

    @Test
    void testGetReviewSentimentAsName() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        String review = "This was one of the greatest movies ever . Other shows do not compare to this absolute pleasure. Greatest everything";

        //(2.25 + 3.5 + 0 + 3 + 2.25) / 5 = 2.5
        String reviewSentimentName = analyzer.getReviewSentimentAsName(review);

        Assertions.assertEquals(MovieReviewSentimentAnalyzer.SENTIMENT_VALUE_SPOSITIVE, reviewSentimentName,
            "The review sentiment name doesn't match the expected!");
    }

    @Test
    void testGetReviewSentimentAsNameWhenReviewContainsOnlyUnknownWords() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        String review = "UNKNOWN ERR41fo0ro0s ; , mvmkjksRors";

        String reviewSentimentName = analyzer.getReviewSentimentAsName(review);

        Assertions.assertEquals(MovieReviewSentimentAnalyzer.SENTIMENT_VALUE_UNKNOWN, reviewSentimentName,
            "The review sentiment name doesn't match the expected!");
    }

    @Test
    void testGetReviewSentimentAsNameWhenReviewContainsNoWords() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        String review = ".%&$(@)*&%^*@*(!#&*%^^%";
        String reviewSentimentName = analyzer.getReviewSentimentAsName(review);

        Assertions.assertEquals(MovieReviewSentimentAnalyzer.SENTIMENT_VALUE_UNKNOWN, reviewSentimentName,
            "The review sentiment name doesn't match the expected!");

        review = "";
        reviewSentimentName = analyzer.getReviewSentimentAsName(review);

        Assertions.assertEquals(MovieReviewSentimentAnalyzer.SENTIMENT_VALUE_UNKNOWN, reviewSentimentName,
            "The review sentiment name doesn't match the expected!");
    }

    @Test
    void testGetWordSentiment() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        double wordSentiment = analyzer.getReviewSentiment("GrEaTesT");
        Assertions.assertEquals(2.25, wordSentiment, 0.001,
            "The word sentiment doesn't match the expected!");

        wordSentiment = analyzer.getReviewSentiment("movies");
        Assertions.assertEquals(3.5, wordSentiment, 0.001,
            "The word sentiment doesn't match the expected!");

        wordSentiment = analyzer.getReviewSentiment("shows");
        Assertions.assertEquals(0, wordSentiment, 0.001,
            "The word sentiment doesn't match the expected!");
    }

    @Test
    void testGetWordSentimentUnknownWord() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        double wordSentiment = analyzer.getReviewSentiment("unknown");
        Assertions.assertEquals(-1, wordSentiment, 0.001,
            "The word sentiment doesn't match the expected!");
    }

    @Test
    void testGetWordFrequency() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        int wordFrequency = analyzer.getWordFrequency("GrEaTesT");
        Assertions.assertEquals(7, wordFrequency,
            "The word frequency doesn't match the expected!");

        wordFrequency = analyzer.getWordFrequency("movies");
        Assertions.assertEquals(2, wordFrequency,
            "The word frequency doesn't match the expected!");

        wordFrequency = analyzer.getWordFrequency("SHOWS");
        Assertions.assertEquals(2, wordFrequency,
            "The word frequency doesn't match the expected!");

        wordFrequency = analyzer.getWordFrequency("unknown");
        Assertions.assertEquals(0, wordFrequency,
            "The word frequency doesn't match the expected!");
    }

    @Test
    void testGetWordFrequencyThrowIllegalArgumentExceptionBecauseOfStopword() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.getWordFrequency("FoR"),
            "Expected IllegalArgumentException because the word is a stopword");
    }

    @Test
    void testGetMostFrequentWords() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        var result = analyzer.getMostFrequentWords(3);
        List<String> expectedResult = List.of(
            "greatest",
            "movies",
            "shows"
        );

        Assertions.assertIterableEquals(expectedResult, result,
            "The most frequent words are incorrect");

        result = analyzer.getMostFrequentWords(50);

        Assertions.assertEquals(49, result.size(),
            "The list should contain all 49 unique words.");

        result = analyzer.getMostFrequentWords(0);

        Assertions.assertEquals(0, result.size(),
            "The list should be empty");
    }

    @Test
    void testGetMostFrequentWordsThrowsIllegalArgumentExceptionBecauseOfNegativeN() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.getMostFrequentWords(-1),
            "Expected IllegalArgumentException because of negative N");
    }

    @Test
    void testGetMostPositiveWords() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        var result = analyzer.getMostPositiveWords(3);
        List<String> expectedResult = List.of(
            "date",
            "entire",
            "years"
        );

        Assertions.assertIterableEquals(expectedResult, result,
            "The most positive words are incorrect");

        result = analyzer.getMostPositiveWords(50);

        Assertions.assertEquals(49, result.size(),
            "The list should contain all 49 unique words.");

        result = analyzer.getMostPositiveWords(0);

        Assertions.assertEquals(0, result.size(),
            "The list should be empty");
    }

    @Test
    void testGetMostPositiveWordsThrowsIllegalArgumentExceptionBecauseOfNegativeN() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.getMostPositiveWords(-1),
            "Expected IllegalArgumentException because of negative N");
    }

    @Test
    void testGetMostNegativeWords() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        var result = analyzer.getMostNegativeWords(3);
        List<String> expectedResult = List.of(
            "shows",
            "sake",
            "humanity's"
        );

        Assertions.assertIterableEquals(expectedResult, result,
            "The most negative words are incorrect");

        result = analyzer.getMostPositiveWords(50);

        Assertions.assertEquals(49, result.size(),
            "The list should contain all 49 unique words.");

        result = analyzer.getMostPositiveWords(0);

        Assertions.assertEquals(0, result.size(),
            "The list should be empty");
    }

    @Test
    void testGetMostNegativeWordsThrowsIllegalArgumentExceptionBecauseOfNegativeN() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.getMostNegativeWords(-1),
            "Expected IllegalArgumentException because of negative N");
    }

    @Test
    void testGetSentimentDictionarySize() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertEquals(49, analyzer.getSentimentDictionarySize(),
            "The expected dictionary size doesn't match the actual size");

        var stopwordsReader2 = new FileReader("stopwords.txt");
        SentimentAnalyzer analyzer2 = createAnalyzer(stopwordsReader2, "");
        Assertions.assertEquals(0, analyzer2.getSentimentDictionarySize(),
            "Expected dictionary to be empty, but it wasn't");
    }

    @Test
    void testAppendReview() throws IOException {
        var outputTestPath = Path.of("outputTest.txt");

        var buffWriter = Files.newBufferedWriter(outputTestPath, StandardOpenOption.CREATE_NEW);

        SentimentAnalyzer analyzer = createAnalyzer(buffWriter);

        String review = "This was one of the greatest movies ever . Other shows do not compare to this absolute pleasure. Greatest everything";
        int sentiment = 4;

        Assertions.assertTrue(analyzer.appendReview(review, sentiment),
            "Expected the appending to be successful");
        Assertions.assertEquals(2.6, analyzer.getWordSentiment("greatest"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(3.666, analyzer.getWordSentiment("movies"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(4, analyzer.getWordSentiment("ever"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(0, analyzer.getWordSentiment("influenced"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(53, analyzer.getSentimentDictionarySize(),
            "Expected dictionary size differs from actual");

        review = "Influenced newWord";
        sentiment = 4;

        Assertions.assertTrue(analyzer.appendReview(review, sentiment),
            "Expected the appending to be successful");
        Assertions.assertEquals(2.6, analyzer.getWordSentiment("greatest"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(3.666, analyzer.getWordSentiment("movies"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(4, analyzer.getWordSentiment("ever"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(2, analyzer.getWordSentiment("influenced"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(4, analyzer.getWordSentiment("newWoRd"), 0.001,
            "Expected word sentiment differs from actual!");
        Assertions.assertEquals(54, analyzer.getSentimentDictionarySize(),
            "Expected dictionary size differs from actual");

        //Test if the actual file has been written to correctly
        buffWriter.close();

        List<String> outputFileLines = Files.readAllLines(outputTestPath);
        List<String> expectedOutputFileLines = List.of(
            "4 This was one of the greatest movies ever . Other shows do not compare to this absolute pleasure. Greatest everything",
            "4 Influenced newWord"
        );

        Files.delete(outputTestPath);

        Assertions.assertIterableEquals(expectedOutputFileLines, outputFileLines,
            "Expected file content differs from the actual file content");
    }

    @Test
    void testAppendReviewThrowsIllegalArgumentExceptionBecauseReviewIsNullEmptyOrBlank() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.appendReview(null, 2),
            "Expected IllegalArgumentException because of null name");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.appendReview("", 2),
            "Expected IllegalArgumentException because of empty name");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.appendReview(" ", 2),
            "Expected IllegalArgumentException because of blank name");
    }

    @Test
    void testAppendReviewThrowsIllegalArgumentExceptionBecauseSentimentIsInvalid() throws FileNotFoundException {
        SentimentAnalyzer analyzer = createAnalyzer();

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.appendReview("Some Review", -1),
            "Expected IllegalArgumentException because sentiment is negative");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> analyzer.appendReview("Some Review", 5),
            "Expected IllegalArgumentException because sentiment is more than 4");
    }

    @Test
    void testAppendReviewReturnsFalseAndRestoresStateFromBackupBecauseOfIOException() throws IOException {
        var stopwordsReader = new FileReader("stopwords.txt");

        var movieReviewsData =
            """
            4 This was the greatest film of my entire life baby ! Greatest characters, greatest story, greatest action scenes. Loved it . So great !
            0 To be influenced chiefly by humanity's greatest shame , reality shows -- reality shows for God's sake !
            1 Spain's greatest star wattage doesn't overcome the tumult of maudlin tragedy .
            4 Gay or straight , Kissing Jessica Stein is one of the greatest date movies in years .
            2 There are a few modest laughs , but certainly no thrills .
            3 Jackie Chan movies are a guilty pleasure - he's easy to like and always leaves us laughing .
            """;

        var movieReviewsReader = new StringReader(movieReviewsData);
        var movieReviewsWriter = new FileWriter("somefile.txt");
        movieReviewsWriter.close();
        Files.delete(Path.of("somefile.txt"));

        SentimentAnalyzer analyzer = new MovieReviewSentimentAnalyzer(stopwordsReader, movieReviewsReader, movieReviewsWriter);

        String review = "This was one of the greatest movies ever . Other shows do not compare to this absolute pleasure. Greatest everything";
        int sentiment = 4;

        Assertions.assertFalse(analyzer.appendReview(review, sentiment),
            """
                    This should return false because an IOException occurred when trying to write the new review through the writer.
                    This is because the writer has already been closed.
                    """);
        Assertions.assertEquals(2.25, analyzer.getWordSentiment("greatest"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(3.5, analyzer.getWordSentiment("movies"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(-1, analyzer.getWordSentiment("ever"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(0, analyzer.getWordSentiment("influenced"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(49, analyzer.getSentimentDictionarySize(),
            "The backup restore was unsuccessful");
    }

    @Test
    void testAppendReviewReturnsFalseAndRestoresStateFromBackupBecauseOfUnexpectedException() throws IOException {
        SentimentAnalyzer analyzer = createAnalyzer();

        SentimentAnalyzer analyzerSpy = Mockito.spy(analyzer);
        Mockito.when(analyzerSpy.isStopWord(anyString())).thenThrow(new RuntimeException("A mocked exception has occured"));

        String review = "This was one of the greatest movies ever . Other shows do not compare to this absolute pleasure. Greatest everything";
        int sentiment = 4;

        Assertions.assertFalse(analyzerSpy.appendReview(review, sentiment),
            """
                    This should return false because a mocked runtime exception is thrown when calling the isStopWord method.
                    """);
        Assertions.assertEquals(2.25, analyzerSpy.getWordSentiment("greatest"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(3.5, analyzerSpy.getWordSentiment("movies"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(-1, analyzerSpy.getWordSentiment("ever"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(0, analyzerSpy.getWordSentiment("influenced"), 0.001,
            "The backup restore was unsuccessful");
        Assertions.assertEquals(49, analyzerSpy.getSentimentDictionarySize(),
            "The backup restore was unsuccessful");

        Mockito.verify(analyzerSpy).isStopWord(anyString());
    }
}
