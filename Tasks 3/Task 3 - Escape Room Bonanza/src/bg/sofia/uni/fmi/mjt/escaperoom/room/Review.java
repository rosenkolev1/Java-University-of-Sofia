package bg.sofia.uni.fmi.mjt.escaperoom.room;

public record Review(int rating, String reviewText) {

    public Review{
        if(rating < 0 || rating > 10) throw new IllegalArgumentException("The rating is not between 0-10 inclusive");
        if(reviewText == null || reviewText.length() > 200) throw new IllegalArgumentException("The reviewText length is more than 200 or the review text is null");
    }
}
