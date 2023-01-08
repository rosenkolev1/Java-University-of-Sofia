package bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions;

public class CocktailAlreadyExistsException extends Exception {
    public CocktailAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    public CocktailAlreadyExistsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
