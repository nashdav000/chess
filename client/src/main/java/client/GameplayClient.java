package client;

import model.GameData;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class GameplayClient {

    private final ServerFacade facade;

    public GameplayClient(ServerFacade facade) {
        this.facade = facade;
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
            case "highlight" -> highlight(params);
            case "move" -> move(params);
            case "redraw" -> redraw();
            case "resign" -> resign();
            case "leave" -> leave();
            case "help" -> help();
            default -> throw new ClientError("Unrecognized command: " + cmd);
        };
    }

    private void promptUser(){System.out.print("\n" + ERASE_SCREEN + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);}

    private String help(){
        return SET_TEXT_COLOR_YELLOW +
               """
               "Highlight legal moves: 'highlight <position>' (e.g. f5)
               "Make a move: 'move <source> <destination> <optional promotion>' (e.g f5 e4 q)
               "Redraw chess board: 'redraw'
               "Resign from game: 'resign'
               "Leave game: 'leave'
               "Display the help menu: 'help'
               """;

    }

    private String highlight(String... params){

        return "";
    }

    private String move(String... params){

        return "";
    }

    private String redraw(){

        return "";
    }

    private String resign(){

        return "";
    }

    private String leave(){

        return "";
    }
}
