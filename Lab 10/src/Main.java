import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cocktail.server.socket.Server;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.DefaultCocktailStorage;

public class Main {
    public static void main(String[] args) {
        CocktailStorage storage = new DefaultCocktailStorage();
        var commandExecutor = new CommandExecutor(storage);
        Server server = new Server(7777, commandExecutor);

        server.start();
    }
}