package bg.sofia.uni.fmi.mjt.sentiment;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {

    public static final String SENTIMENT_VALUE_UNKNOWN = "unknown";
    public static final String SENTIMENT_VALUE_NEGATIVE = "negative";
    public static final String SENTIMENT_VALUE_SNEGATIVE = "somewhat negative";
    public static final String SENTIMENT_VALUE_NEUTRAL = "neutral";
    public static final String SENTIMENT_VALUE_SPOSITIVE = "somewhat positive";
    public static final String SENTIMENT_VALUE_POSITIVE = "positive";
    public static final Map<Integer, String> SENTIMENT_VALUES = Stream.of(new Object[][] {
        {-1, SENTIMENT_VALUE_UNKNOWN},
        {0, SENTIMENT_VALUE_NEGATIVE},
        {1, SENTIMENT_VALUE_SNEGATIVE},
        {2, SENTIMENT_VALUE_NEUTRAL},
        {3, SENTIMENT_VALUE_SPOSITIVE},
        {4, SENTIMENT_VALUE_POSITIVE},
    }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (String) data[1]));
    private static final String WORD_REGEX_STRING = "([a-zA-Z0-9']{2,})";
    private static final Pattern WORD_REGEX_PATTERN = Pattern.compile(WORD_REGEX_STRING);

    private Writer reviewsWriter;
    private Reader reviewsReader;
    private Reader stopwordsReader;
    private Map<String, WordInfo> wordsSentiments;
    private Set<String> stopwords;

    private MovieReviewSentimentAnalyzer(MovieReviewSentimentAnalyzer other) {
        this.copy(other);
    }

    public MovieReviewSentimentAnalyzer(Reader stopwordsIn, Reader reviewsIn, Writer reviewsOut) {
        this.wordsSentiments = new HashMap<>();
        this.stopwords = new HashSet<>();

        this.reviewsWriter = reviewsOut;
        this.reviewsReader = reviewsIn;
        this.stopwordsReader = stopwordsIn;

        try (var stopwordsFileReader = new BufferedReader(stopwordsIn)) {
            while (true) {
                String stopword = stopwordsFileReader.readLine();

                if (stopword == null) {
                    break;
                }

                this.stopwords.add(stopword);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var reviewsFileReader = new BufferedReader(reviewsIn)) {
            while (true) {
                String review = reviewsFileReader.readLine();

                if (review == null) {
                    break;
                }

                updateWordInfo(review);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateWordInfo(String review) {
        int reviewSentiment = Integer.valueOf(review.substring(0, 1));

        updateWordInfo(review, reviewSentiment);
    }

    private void updateWordInfo(String review, int sentiment) {
        Matcher wordsMatcher = WORD_REGEX_PATTERN.matcher(review);

        List<String> allMatches = wordsMatcher.results().map(x -> review.substring(x.start(), x.end()).toLowerCase())
            .filter(x -> !this.isStopWord(x)).toList();
        Set<String> uniqueMatches = new HashSet<>();

        for (var match : allMatches) {
            if (this.isStopWord(match)) continue;

            if (this.wordsSentiments.containsKey(match)) {
                var wordInfo = this.wordsSentiments.get(match);
                wordInfo.matchesCount += 1;

                if (!uniqueMatches.contains(match)) {
                    wordInfo.reviewsCount += 1;
                    wordInfo.totalSentiment += sentiment;
                    uniqueMatches.add(match);
                }
            } else {
                var wordInfo = new WordInfo(match, sentiment, 1, 1);
                this.wordsSentiments.put(match, wordInfo);
                uniqueMatches.add(match);
            }
        }
    }

    private void copy(MovieReviewSentimentAnalyzer other) {
        //Can't copy the streams so just share them. It doesn't matter in the context of this program anyway
        this.reviewsWriter = other.reviewsWriter;
        this.reviewsReader = other.reviewsReader;
        this.stopwordsReader = other.stopwordsReader;

        this.wordsSentiments = new HashMap<>();
        for (var key : other.wordsSentiments.keySet()) {
            var wordInfo = other.wordsSentiments.get(key);
            var wordInfoCopy = new WordInfo(wordInfo);
            this.wordsSentiments.put(key, wordInfoCopy);
        }

        this.stopwords = new HashSet<>(other.stopwords);
    }

    @Override
    public double getReviewSentiment(String review) {
        double reviewSentiment = 0;
        int matchesCount = 0;

        Matcher wordsMatcher = WORD_REGEX_PATTERN.matcher(review);
        List<String> matches =
            wordsMatcher.results().map(x -> review.substring(x.start(), x.end()).toLowerCase()).toList();

        for (String match : matches) {
            if (this.wordsSentiments.containsKey(match)) {
                reviewSentiment += this.wordsSentiments.get(match).getSentimentScore();
                matchesCount++;
            }
        }

        if (matchesCount == 0) {
            return -1;
        }

        return reviewSentiment / matchesCount;
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        return SENTIMENT_VALUES.get((int) Math.round(this.getReviewSentiment(review)));
    }

    @Override
    public double getWordSentiment(String word) {
        word = word.toLowerCase();

        if (this.wordsSentiments.containsKey(word)) {
            return this.wordsSentiments.get(word).getSentimentScore();
        }

        return -1;
    }

    @Override
    public int getWordFrequency(String word) {
        word = word.toLowerCase();

        if (this.isStopWord(word)) {
            throw new IllegalArgumentException("The word is a stopword!");
        }

        if (!this.wordsSentiments.containsKey(word)) return 0;

        return this.wordsSentiments.get(word).matchesCount;
    }

    @Override
    public List<String> getMostFrequentWords(int n) {
        if (n < 0) throw new IllegalArgumentException("N cannot be negative");

        return this.wordsSentiments.values().stream().sorted((x, y) -> y.matchesCount - x.matchesCount)
            .limit(n).map(x -> x.getWord()).toList();
    }

    @Override
    public List<String> getMostPositiveWords(int n) {
        if (n < 0) throw new IllegalArgumentException("N cannot be negative");

        return this.wordsSentiments.values().stream()
            .sorted(Comparator.comparingDouble(WordInfo::getSentimentScore).reversed())
            .limit(n).map(x -> x.getWord()).toList();
    }

    @Override
    public List<String> getMostNegativeWords(int n) {
        if (n < 0) throw new IllegalArgumentException("N cannot be negative");

        return this.wordsSentiments.values().stream().sorted(Comparator.comparingDouble(WordInfo::getSentimentScore))
            .limit(n).map(x -> x.getWord()).toList();
    }

    @Override
    public boolean appendReview(String review, int sentiment) {
        if (review == null || review.isBlank()) {
            throw new IllegalArgumentException("The review is null or empty or blank!");
        }
        if (sentiment < 0 || sentiment > 4) {
            throw new IllegalArgumentException("The sentiment is not in the range [0-4]");
        }

        //In case of unexpected error, restore previous state and return false
        var backupObject = new MovieReviewSentimentAnalyzer(this);

        try {
            updateWordInfo(review, sentiment);
        } catch (Exception e) {
            this.copy(backupObject);
            return false;
        }

        String newReviewLine = sentiment + " " + review + System.lineSeparator();

        //Replace the old dataset with the new
        try {
            var buffWriter = new BufferedWriter(this.reviewsWriter);
            buffWriter.write(newReviewLine);
            buffWriter.flush();
        } catch (IOException e) {
            this.copy(backupObject);
            return false;
        }

        return true;
    }

    @Override
    public int getSentimentDictionarySize() {
        return this.wordsSentiments.size();
    }

    @Override
    public boolean isStopWord(String word) {
        return this.stopwords.contains(word.toLowerCase());
    }
}
