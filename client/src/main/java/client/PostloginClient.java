package client;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class PostloginClient {
    private final ServerFacade facade;
    private String authToken;

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
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
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
            default -> help();
        };
    }

    private void promptUser(){System.out.print("\n" + ERASE_SCREEN + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);}

    private String help(){
        return SET_TEXT_COLOR_YELLOW + "create <NAME> " +
                RESET_TEXT_COLOR + "- a game\n" +
                SET_TEXT_COLOR_YELLOW + "list " +
                RESET_TEXT_COLOR + "- games\n" +
                SET_TEXT_COLOR_YELLOW + "join <ID> [WHITE|BLACK] " +
                RESET_TEXT_COLOR + "- a game\n" +
                SET_TEXT_COLOR_YELLOW + "logout " +
                RESET_TEXT_COLOR + "- when you are done\n" +
                SET_TEXT_COLOR_YELLOW + "quit " +
                RESET_TEXT_COLOR + "- playing chess\n" +
                SET_TEXT_COLOR_YELLOW + "help " +
                RESET_TEXT_COLOR + "- with possible commands\n";
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

        return "";
    }

    private String join(String... params){

        return "";
    }

    private String observe(String... params){

        return "";
    }

    private String quit(){
        facade.logout(authToken);
        return "quit";
    }
}
