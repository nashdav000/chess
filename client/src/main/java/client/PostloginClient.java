package client;

import java.util.Arrays;
import java.util.List;
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
                "Create a new game: 'create <NAME>'\n" +
                "List all games: 'list'\n" +
                "Join a game: 'join <ID> [WHITE|BLACK]'\n" +
                "Logout: 'logout'\n" +
                "Quit the program: 'quit'\n" +
                "Display the help menu: 'help'\n";
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
            response += "Game #" + g.gameID() + "\t" + g.gameName() +
                    "\n\tWhite: " + (g.whiteUsername() != null ? g.whiteUsername() : "") +
                    "\n\tBlack: " + (g.blackUsername() != null ? g.blackUsername() : "") +
                    "\n\n";
        }

        return response.substring(0, response.length() - 1);
    }

    private String join(String... params){
        if (params.length >= 2){
            String id = params[0];
            String color = params[1];
            facade.joinGame(color, id, authToken);
            return "Joined game #%s".formatted(id);
        }
        else{
            throw new ClientError("Error: Expected <ID> [WHITE|BLACK]");
        }
    }

    private String observe(String... params){

        return "";
    }

    private String quit(){
        facade.logout(authToken);
        return "quit";
    }
}
