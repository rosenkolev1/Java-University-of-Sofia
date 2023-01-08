package bg.sofia.uni.fmi.mjt.cocktail.server.command;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import com.google.gson.Gson;

import java.util.*;

public class CommandExecutor {
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
        "Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"";

    private static final String CREATE = "create";
    private static final String GET = "get";
    private static final String GET_ALL = "get all";
    private static final String GET_BY_NAME = "get by-name";
    private static final String GET_BY_INGREDIENT = "get by-ingredient";
//    private static final String DISCONNECT = "list";

    private CocktailStorage storage;

    public CommandExecutor(CocktailStorage storage) {
        this.storage = storage;
    }

    public String execute(Command cmd) {
        String cmdString = cmd.command();

        if (cmdString.equals(CREATE)) {
            return this.createCocktail(cmd.arguments());
        }
        else if (cmdString.equals(GET)) {
            cmdString += " " + (cmd.arguments().length > 0 ? cmd.arguments()[0] : "");
            var args = Arrays.stream(cmd.arguments()).skip(1).toArray(String[]::new);

            if (cmdString.equals(GET_ALL)) {
                return this.getAll(args);
            }
            else if (cmdString.equals(GET_BY_NAME)) {
                return this.getByName(args);
            }
            else if (cmdString.equals(GET_BY_INGREDIENT)) {
                return this.getByIngredient(args);
            }
        }

        return "Unknown command";
    }

    private String createCocktail(String[] args) {
        if (args.length < 2) {
            return String.format("Invalid count of arguments: \"%s\" expects %d or more arguments. Example: \"%s\""
                , CREATE, 2, CREATE + " <cocktail_name> [<ingredient_name>=<ingredient_amount> ...]");
        }

        String cocktailName = args[0];
        Set<Ingredient> ingredients = new HashSet<>();

        for (String arg : Arrays.stream(args).skip(1).toList()) {
            String[] argSplit = arg.split("=");

            String ingredientName = argSplit[0];
            String ingredientAmount = argSplit[1];

            ingredients.add(new Ingredient(ingredientName, ingredientAmount));
        }

        try {
            this.storage.createCocktail(new Cocktail(cocktailName, ingredients));
        } catch (CocktailAlreadyExistsException e) {
            return "Could not add the new cocktail!\nError: " + e.getMessage();
        }

        return "The new cocktail has been successfully created!";
    }

    private String getAll(String[] args) {
        if (args.length != 0) {
            return String.format("Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\""
                , GET_ALL, 0, GET_ALL);
        }
        var gson = new Gson();

        String jsonString = gson.toJson(this.storage.getCocktails());

        return jsonString;
    }

    private String getByName(String[] args) {
        if (args.length != 1) {
            return String.format("Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\""
                , GET_BY_NAME, 1, GET_BY_NAME + " <cocktail_name>");
        }
        Cocktail cocktail = null;
        try {
            cocktail = this.storage.getCocktail(args[0]);
        } catch (CocktailNotFoundException e) {
            return e.getMessage();
        }
        var gson = new Gson();

        String jsonString = gson.toJson(cocktail);

        return jsonString;
    }

    private String getByIngredient(String[] args) {
        if (args.length != 1) {
            return String.format("Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\""
                , GET_BY_INGREDIENT, 1, GET_BY_INGREDIENT + " <ingredient_name>");
        }

        Collection<Cocktail> cocktails = this.storage.getCocktailsWithIngredient(args[0]);

        var gson = new Gson();

        String jsonString = gson.toJson(cocktails);

        return jsonString;
    }

//    private String addToDo(String[] args) {
//        if (args.length != 2) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, ADD, 2, ADD + " <username> <todo_item>");
//        }
//
//        String user = args[0];
//        String todo = args[1];
//
//        int todoID = storage.add(user, todo);
//        return String.format("Added new To Do with ID %s for user %s", todoID, user);
//    }
//
//    private String complete(String[] args) {
//        if (args.length != 2) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, COMPLETE, 2,
//                COMPLETE + " <username> <todo_item_id>");
//        }
//
//        String user = args[0];
//        int todoID;
//        try {
//            todoID = Integer.parseInt(args[1]);
//        } catch (NumberFormatException e) {
//            return "Invalid ID provided for command \"complete-todo\": only integer values are allowed";
//        }
//
//        storage.remove(user, todoID);
//        return String.format("Completed To Do with ID %s for user %s", todoID, user);
//    }
//
//    private String list(String[] args) {
//        if (args.length != 1) {
//            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, LIST, 1, LIST + " <username>");
//        }
//        String user = args[0];
//        var todos = storage.list(user);
//        if (todos.isEmpty()) {
//            return "No To-Do items found for user with name " + user;
//        }
//
//        StringBuilder response = new StringBuilder(String.format("To-Do list of user %s:%n", user));
//        todos.forEach((k, v) -> response.append(String.format("[%d] %s%n", k, v)));
//
//        return response.toString();
//    }
}
