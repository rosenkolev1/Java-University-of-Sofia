package bg.sofia.uni.fmi.mjt.sentiment;

public class WordInfo {

    private String word;
    public int totalSentiment;
    //The number of reviews in which the word is matched, not the number of occurrences of the word.
    public int reviewsCount;
    //The total number of times the word is matched in all the reviews, including repeats in the same review
    public int matchesCount;

    public WordInfo(String word, int totalSentiment, int reviewsCount, int matchesCount) {
        this.word = word;
        this.totalSentiment = totalSentiment;
        this.reviewsCount = reviewsCount;
        this.matchesCount = matchesCount;
    }

    public WordInfo(WordInfo other) {
        this.word = other.word;
        this.totalSentiment = other.totalSentiment;
        this.reviewsCount = other.reviewsCount;
        this.matchesCount = other.matchesCount;
    }

    public double getSentimentScore() {
        return (double) this.totalSentiment / this.reviewsCount;
    }

    public String getWord() {
        return this.word;
    }
}
