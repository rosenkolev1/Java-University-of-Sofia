package bg.sofia.uni.fmi.mjt.netflix;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NetflixRecommender {

    List<Content> contents;

    private static final String NEWLINE = System.lineSeparator();

    /**
     * Loads the dataset from the given {@code reader}.
     *
     * @param reader Reader from which the dataset can be read.
     */
    public NetflixRecommender(Reader reader) {

        this.contents = new ArrayList<>();

        try {
            //Remove the first line of the file which has the column definitions
            while (true) {
                int charInt = reader.read();

                if (charInt == -1) {
                    //File is empty in this case
                    return;
                }

                if (isNewline((char) charInt, reader)) {
                    break;
                }
            }

            String currentField = "";
            List<String> currentFields = new ArrayList<>();

            //Start reading the entries fields
            while (true) {
                int charInt = reader.read();

                if (charInt == -1) {

                    if (!currentField.isEmpty()) {
                        currentFields.add(currentField);

                        var newContent = Content.createContent(currentFields);
                        this.contents.add(newContent);

                        //Reset the fields
                        currentField = "";
                        currentFields = new ArrayList<>();
                    }

                    break;
                }

                char newChar = (char) charInt;

                if (isNewline(newChar, reader)) {
                    currentFields.add(currentField);

                    var newContent = Content.createContent(currentFields);
                    this.contents.add(newContent);

                    //Reset the fields
                    currentField = "";
                    currentFields = new ArrayList<>();
                } else if (newChar == ',') {
                    currentFields.add(currentField);
                    currentField = "";
                } else {
                    currentField += newChar;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNewline(char symbol, Reader reader) {
        final int readAheadLimit = 100;

        try {
            if (NEWLINE.startsWith(String.valueOf(symbol))) {
                if (NEWLINE.equals(String.valueOf(symbol))) {
                    //Newline found, working on Linux based system where newline is likely '\n'
                    return true;
                } else {
                    //I assume that no operating system has a newline that is longer than 100 bytes
                    reader.mark(readAheadLimit);

                    //Probably working on Windows where newline is '\r\n'
                    for (int i = 1; i < NEWLINE.length(); i++) {
                        if ((char) reader.read() != NEWLINE.charAt(i)) {
                            //Return the reader to it's starting point
                            reader.reset();

                            return false;
                        }
                    }

                    return true;
                }
            }

            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all movies and shows from the dataset in undefined order as an unmodifiable List.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all movies and shows.
     */
    public List<Content> getAllContent() {
        return Collections.unmodifiableList(this.contents);
    }

    /**
     * Returns a list of all unique genres of movies and shows in the dataset in undefined order.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all genres
     */
    public List<String> getAllGenres() {

        var uniqueGenres = this.contents.stream().flatMap(
            content -> content.genres().stream()
        ).distinct().toList();

        return uniqueGenres;
    }

    /**
     * Returns the movie with the longest duration / run time. If there are two or more movies
     * with equal maximum run time, returns any of them. Shows in the dataset are not considered by this method.
     *
     * @return the movie with the longest run time
     * @throws NoSuchElementException in case there are no movies in the dataset.
     */
    public Content getTheLongestMovie() {

        if (this.contents.isEmpty()) {
            throw new NoSuchElementException("There are no contents in the dataset!");
        }

        var longestMovieOptional = this.contents.stream()
            .filter((content) -> content.type() == ContentType.MOVIE)
            .max((first, second) -> first.runtime() > second.runtime() ? 1 : -1);

        var longestMovie = longestMovieOptional.get();

        return longestMovie;
    }

    /**
     * Returns a breakdown of content by type (movie or show).
     *
     * @return a Map with key: a ContentType and value: the set of movies or shows on the dataset, in undefined order.
     */
    public Map<ContentType, Set<Content>> groupContentByType() {

        var groups = this.contents.stream().collect(
            Collectors.groupingBy(Content::type, Collectors.toSet())
        );

        return groups;
    }

    /**
     * Returns the top N movies and shows sorted by weighed IMDB rating in descending order.
     * If there are fewer movies and shows than {@code n} in the dataset, return all of them.
     * If {@code n} is zero, returns an empty list.
     * <p>
     * The weighed rating is calculated by the following formula:
     * Weighted Rating (WR) = (v ÷ (v + m)) × R + (m ÷ (v + m)) × C
     * where
     * R is the content's own average rating across all votes. If it has no votes, its R is 0.
     * C is the average rating of content across the dataset
     * v is the number of votes for a content
     * m is a tunable parameter: sensitivity threshold. In our algorithm, it's a constant equal to 10_000.
     * <p>
     * Check https://stackoverflow.com/questions/1411199/what-is-a-better-way-to-sort-by-a-5-star-rating for details.
     *
     * @param n the number of the top-rated movies and shows to return
     * @return the list of the top-rated movies and shows
     * @throws IllegalArgumentException if {@code n} is negative.
     */
    public List<Content> getTopNRatedContent(int n) {
        final int m = 10_000;

        if (n < 0) {
            throw new IllegalArgumentException("The n is negative!");
        }

        if (this.contents.isEmpty()) return new ArrayList<>();

        final double averageRatingDataset = this.contents.stream()
            .mapToDouble(x -> x.imdbScore()).average().getAsDouble();

        Function<Content, Double> formula = (Content x) -> {
            return (x.imdbVotes() / (x.imdbVotes() + m)) * (x.imdbVotes() > 0 ? x.imdbScore() : 0) +
                (m / (x.imdbVotes() + m)) * averageRatingDataset;
        };

        var topRatedContent = this.contents.stream()
            .sorted((x, y) -> {
                if (formula.apply(x) > formula.apply(y)) return -1;
                else if (formula.apply(x) < formula.apply(y)) return 1;
                return 0;
            }).limit(n).toList();

        return topRatedContent;
    }

    /**
     * Returns a list of content similar to the specified one sorted by similarity is descending order.
     * Two contents are considered similar, only if they are of the same type (movie or show).
     * The used measure of similarity is the number of genres two contents share.
     * If two contents have equal number of common genres with the specified one, their mutual oder
     * in the result is undefined.
     *
     * @param content the specified movie or show.
     * @return the sorted list of content similar to the specified one.
     */

    private long sharedGenres(List<String> genresFirst, List<String> genresSecond) {
        return genresFirst.stream().filter(x -> genresSecond.contains(x)).count();
    }

    public List<Content> getSimilarContent(Content content) {

        var similarContent = this.contents.stream()
            .filter(x -> x.type() == content.type())
            .sorted((x, y) -> {
                if (sharedGenres(x.genres(), content.genres()) > sharedGenres(y.genres(), content.genres())) {
                    return -1;
                } else if (sharedGenres(x.genres(), content.genres()) < sharedGenres(y.genres(), content.genres())) {
                    return 1;
                }
                return 0;
            }).toList();

        return similarContent;
    }

    /**
     * Searches content by keywords in the description (case-insensitive).
     *
     * @param keywords the keywords to search for
     * @return an unmodifiable set of movies and shows whose description contains all specified keywords.
     */
    public Set<Content> getContentByKeywords(String... keywords) {
        var keywordsLowerCase = Arrays.stream(keywords).map(x -> x.toLowerCase()).toList();

        var filteredContent = this.contents.stream()
            .filter(x -> Arrays.stream(x.description().toLowerCase()
                    .split("[\\p{IsPunctuation}\\s]+")).toList()
                .containsAll(keywordsLowerCase)).collect(Collectors.toUnmodifiableSet());

        return filteredContent;
    }

}