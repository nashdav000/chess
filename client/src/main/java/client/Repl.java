package client;

import client.helperClasses.Status;
import model.UserData;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {

    private Status status = Status.SIGNEDOUT;
    private ServerFacade facade;


    public Repl(String url) {facade = new ServerFacade(url);}

    public void run(){
        System.out.print("Welcome to 240 Chess. Type 'help' to get started.");


        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")){
            promptUser();
            String line = scanner.nextLine();

            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);
            }
            catch(Exception e){
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
            }
        }
    }

    private String eval(String input) throws ClientError {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd){
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private void promptUser(){System.out.print("\n" + ERASE_SCREEN + ">>> " + SET_TEXT_COLOR_GREEN);}

    private String help(){
        return SET_TEXT_COLOR_YELLOW + "register <USERNAME> <PASSWORD> <EMAIL> " +
                RESET_TEXT_COLOR + "- register new user\n" +
                SET_TEXT_COLOR_YELLOW + "login <USERNAME> <PASSWORD> " +
                RESET_TEXT_COLOR + "- to play chess\n" +
                SET_TEXT_COLOR_YELLOW + "quit " +
                RESET_TEXT_COLOR + "- playing chess\n" +
                SET_TEXT_COLOR_YELLOW + "help " +
                RESET_TEXT_COLOR + "- with possible commands";
    }

    private String login(String ... params) throws ClientError {
        if (params.length >= 2){
            String username = params[0];
            String password = params[1];
            facade.login(username, password);
            status = Status.SIGNEDIN;
            return "Logged in as %s".formatted(username);
        }
        else{
            throw new ClientError("Error: Expected <USERNAME> <PASSWORD>");
        }

    }

    private String register(String ... params) throws ClientError {
        if (params.length >= 3){
            String username = params[0];
            String password = params[1];
            String email = params[2];
            facade.register(new UserData(username, password, email));
            status = Status.SIGNEDIN;
            return "Successfully registered as %s".formatted(username);
        }
        else{
            throw new ClientError("Error: Expected <USERNAME> <PASSWORD> <EMAIL>");
        }
    }
}
