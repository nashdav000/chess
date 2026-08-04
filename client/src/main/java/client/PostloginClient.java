package client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import model.*;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class PostloginClient {
    private final ServerFacade facade;
    private final String authToken;

    public PostloginClient(ServerFacade facade, String authToken) {
        this.facade = facade;
        this.authToken = authToken;
    }

    public String run(){
        System.out.println();


        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")){
            promptUser();
            String line = scanner.nextLine();

            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);

                if (line.contains("logout")){
                    result = "quit";
                }

                if (line.contains("quit")){
                    return "quit";
                }
            }
            catch(Exception e){
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR + "\n");
            }
        }

        return "";
    }

    private String eval(String input) throws ClientError {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd){
            case "logout" -> logout();
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "quit" -> quit();
            case "help" -> help();
            default -> throw new ClientError("Unrecognized command: " + cmd);
        };
    }

    private void promptUser(){System.out.print("\n" + ERASE_SCREEN + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);}

    private String help(){
        return SET_TEXT_COLOR_YELLOW +
                """
                Create a new game: 'create <NAME>'
                List all games: 'list'
                Join a game: 'join <ID> [WHITE|BLACK]'
                Observe a game: 'observe <ID>'
                Logout: 'logout'
                Quit the program: 'quit'
                Display the help menu: 'help'
                """;
    }

    private String logout(){
        facade.logout(authToken);
        return "Logged out\n";
    }

    private String create(String... params) throws ClientError {
        if (params.length >= 1){
            String gameName = params[0];
            String id = facade.createGame(gameName, authToken);
            return "Created game #%s: %s\n".formatted(id, gameName);
        }
        else{
            throw new ClientError("Error: Expected <NAME>");
        }
    }

    private String list(){
        List<GameData> games = facade.listGames(authToken);

        String response = "";
        for (GameData g : games) {
            // Hide Finished Games
            if (g.gameName().contains(" — Finished")) {
                continue;
            }

            response += "Game #" + g.gameID() + "\t" + g.gameName() +
                    "\n\tWhite: " + (g.whiteUsername() != null ? g.whiteUsername() : "") +
                    "\n\tBlack: " + (g.blackUsername() != null ? g.blackUsername() : "") +
                    "\n\n";
        }

        return response.isEmpty() ? "No games. Consider creating one!\n" :
                response.substring(0, response.length() - 1);
    }

    private String join(String... params){
        if (params.length >= 2){
            String id = params[0];
            String color = params[1];
            facade.joinGame(color, id, authToken);
            switchtoGameplay(color, id);
            return "";
        }
        else{
            throw new ClientError("Error: Expected <ID> [WHITE|BLACK]");
        }
    }

    private String observe(String... params) throws ClientError {
        if (params.length >= 1){
            String id = params[0];
            switchtoGameplay("", id);
            return "";
        }
        else{
            throw new ClientError("Error: Expected <ID>");
        }
    }

    private String quit(){
        facade.logout(authToken);
        return "quit";
    }

    private void switchtoGameplay(String color, String id) throws ClientError {
        for (GameData g : facade.listGames(authToken)){
            if (Objects.equals(g.gameID(), id)){
                new GameplayClient(facade, color, g.chessGame(), id, authToken).run();
                return;
            }
        }

        throw new ClientError("Error: Game ID does not exist");
    }
}
