package bg.sofia.uni.fmi.mjt.netflix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record Content(String id, String title, ContentType type, String description,
                      int releaseYear, int runtime, List<String> genres, int seasons,
                      String imdbId, double imdbScore, double imdbVotes) {

    public static Content createContent(List<String> fields) {
        String id;
        String title;
        ContentType type;
        String description;
        int releaseYear;
        int runtime;
        List<String> genres;
        int seasons;
        String imdbId;
        double imdbScore;
        double imdbVotes;

        int fieldIndex = 0;

//        if (fields.size() != 11) {
//            throw new IllegalArgumentException("Missing fields!");
//        }

        id = fields.get(fieldIndex++);
        title = fields.get(fieldIndex++);

        if (fields.get(fieldIndex++).equals(ContentType.MOVE_STRING)) {
            type = ContentType.MOVIE;
        }
        else {
            type = ContentType.SHOW;
        }

        description = fields.get(fieldIndex++);
        releaseYear = Integer.valueOf(fields.get(fieldIndex++));

        runtime = Integer.valueOf(fields.get(fieldIndex++));

        //Parse the genre strings
        String genreString = fields.get(fieldIndex++);

        genreString = genreString.substring(1, genreString.length() - 1);

        List<String> genreStrings = Arrays.asList(genreString.split("; "));

        genres = genreStrings.stream().map(
            genre -> genre.length() <= 2 ? "" : genre.substring(1, genre.length() - 1)
        ).toList();

        seasons = Integer.valueOf(fields.get(fieldIndex++));

        imdbId = fields.get(fieldIndex++);
        imdbScore = Double.valueOf(fields.get(fieldIndex++));
        imdbVotes = Double.valueOf(fields.get(fieldIndex++));

        return new Content(id, title, type, description,
            releaseYear, runtime, genres, seasons,
            imdbId, imdbScore, imdbVotes);
    }
}
