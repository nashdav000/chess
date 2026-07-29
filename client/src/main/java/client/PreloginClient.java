package client;

import model.UserData;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class PreloginClient {
    private final ServerFacade facade;
    private String authToken;

    public PreloginClient(String url) {facade = new ServerFacade(url);}

    public void run(){
        System.out.print("Welcome to 240 Chess. Type 'help' to get started.\n");


        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")){
            promptUser();
            String line = scanner.nextLine();

            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);

                if (line.contains("login") || line.contains("register")){
                    result = switchToLoggedIn();
                }
            }
            catch(Exception e){
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR + "\n");
            }
        }
    }

    private String eval(String input) throws ClientError {
        String[] tokens = input.split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd.toLowerCase()){
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            case "help" -> help();
            default -> throw new ClientError("Unrecognized command: " + cmd);
        };
    }

    private void promptUser(){System.out.print("\n" + ERASE_SCREEN + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);}

    private String help(){
        return SET_TEXT_COLOR_YELLOW +
                "Register a new user: 'register <USERNAME> <PASSWORD> <EMAIL>'\n" +
                "Login an existing user: 'login <USERNAME> <PASSWORD>'\n" +
                "Quit the program: 'quit'\n" +
                "Display the help menu: 'help'\n";
    }

    private String login(String... params) throws ClientError {
        if (params.length >= 2){
            String username = params[0];
            String password = params[1];
            authToken = facade.login(username, password).authToken();
            return "Logged in as %s".formatted(username);
        }
        else{
            throw new ClientError("Error: Expected <USERNAME> <PASSWORD>");
        }

    }

    private String register(String... params) throws ClientError {
        if (params.length >= 3){
            String username = params[0];
            String password = params[1];
            String email = params[2];
            authToken = facade.register(new UserData(username, password, email)).authToken();
            return "Successfully registered as %s".formatted(username);
        }
        else{
            throw new ClientError("Error: Expected <USERNAME> <PASSWORD> <EMAIL>");
        }
    }

    private String switchToLoggedIn(){
        try {
            System.out.print("\nType 'help' to display the help menu.");
            return new PostloginClient(facade, authToken).run();

        } catch (Exception e) {
            System.out.print("Error: Unable to log in. Try again later");
            return "";
        }
    }
}
