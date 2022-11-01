package bg.sofia.uni.fmi.mjt.escaperoom.room;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

public class EscapeRoom implements Ratable {

    private String name;
    private Theme theme;
    private Difficulty difficulty;
    private int maxTimeToEscape;
    private double priceToPlay;
    private  int maxReviewsCount;

    private Review[] reviews;
    private int reviewPointer;
    private int totalReviewCount;
    private double rating;

    public EscapeRoom(String name, Theme theme, Difficulty difficulty, int maxTimeToEscape, double priceToPlay,
                      int maxReviewsCount){
        this.name = name;
        this.theme = theme;
        this.difficulty = difficulty;
        this.maxTimeToEscape = maxTimeToEscape;
        this.priceToPlay = priceToPlay;
        this.maxReviewsCount = maxReviewsCount;
        this.reviews = new Review[this.maxReviewsCount];
        for (int i = 0; i < this.maxReviewsCount; i++) {
            this.reviews[i] = null;
        }
        this.reviewPointer = 0;
        this.totalReviewCount = 0;
        this.rating = 0;
    }

    /**
     * Returns the name of the escape room.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the difficulty of the escape room.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the maximum time to escape the room.
     */
    public int getMaxTimeToEscape() {
        return maxTimeToEscape;
    }

    /**
     * Returns all user reviews stored for this escape room, in the order they have been added.
     */
    public Review[] getReviews() {
        // TODO: add implementation here
        Review[] sortedReviews = new Review[this.getReviewsCountCurrent()];

        int sortedCounter = 0;
        for (int i = this.reviewPointer; i < this.getReviewsCountCurrent(); i++) {
            sortedReviews[sortedCounter++] = this.reviews[i];
        }
        for (int i = 0; i < this.reviewPointer; i++){
            sortedReviews[sortedCounter++] = this.reviews[i];
        }

        return sortedReviews;
    }

    public int getReviewsCountCurrent(){
        if(this.totalReviewCount >= this.maxReviewsCount) return this.maxReviewsCount;

        return this.totalReviewCount;
    }

    public int getReviewsCountTotal(){
        return this.totalReviewCount;
    }

    /**
     * Adds a user review for this escape room.
     * The platform keeps just the latest up to {@code maxReviewsCount} reviews and in case the capacity is full,
     * a newly added review would overwrite the oldest added one, so the platform contains
     * {@code maxReviewsCount} at maximum, at any given time. Note that, despite older reviews may have been
     * overwritten, the rating of the room averages all submitted review ratings, regardless of whether all reviews
     * themselves are still stored in the platform.
     *
     * @param review the user review to add.
     */
    public void addReview(Review review) {
        if(reviewPointer == this.maxReviewsCount) reviewPointer = 0;

        this.reviews[reviewPointer++] = review;

        this.rating = (this.rating * this.totalReviewCount + review.rating()) / (this.totalReviewCount + 1);
        this.totalReviewCount++;
    }

    @Override
    public double getRating() {
        return this.rating;
    }
}
