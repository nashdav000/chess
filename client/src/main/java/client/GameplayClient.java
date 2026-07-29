package client;

import chess.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class GameplayClient {

    private final ServerFacade facade;
    private final String color;
    private final ChessGame game;

    public GameplayClient(ServerFacade facade, String color, ChessGame game) {
        this.facade = facade;
        this.color = color.toLowerCase();
        this.game = game;
    }

    public String run(){
        System.out.println();
        drawBoard();

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

    private void promptUser(){System.out.print("\n" + ERASE_SCREEN + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN);}

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

    private void drawBoard(){
        if (Objects.equals(color, "black")){
            drawBlackBoard();
        }
        else{
            drawWhiteBoard();
        }
    }


    private void drawBlackBoard(){

        ChessBoard board = game.getBoard();
        String printedBoard =  SET_TEXT_COLOR_BLACK + SET_BG_COLOR_BLUE  +
                "    h   g  f   e   d  c   b  a     " + RESET_BG_COLOR + "\n";
        for (int i = 1; i < 9; i++){

            printedBoard += SET_BG_COLOR_BLUE + " " + (i) + " ";

            for (int j = 1; j < 9; j++){
                printedBoard += ((i + j) % 2 == 0 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY) +
                    getPiece(board.getPiece(new ChessPosition(i, 9 - j)));
            }

            printedBoard += SET_BG_COLOR_BLUE + " " + (i) + " " + RESET_BG_COLOR + "\n";
        }
        printedBoard += SET_BG_COLOR_BLUE +
                "    h   g  f   e   d  c   b  a     " + RESET_BG_COLOR + "\n" + RESET_TEXT_COLOR;

        System.out.println(printedBoard);
    }

    private void drawWhiteBoard(){

        ChessBoard board = game.getBoard();
        String printedBoard =  SET_TEXT_COLOR_BLACK + SET_BG_COLOR_BLUE  +
                "    a   b  c   d   e  f   g  h     " + RESET_BG_COLOR + "\n";
        for (int i = 1; i < 9; i++){

            printedBoard += SET_BG_COLOR_BLUE + " " + (9 - i) + " ";

            for (int j = 1; j < 9; j++){
                printedBoard += ((i + j) % 2 == 0 ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY) +
                        getPiece(board.getPiece(new ChessPosition(9 - i, j)));
            }

            printedBoard += SET_BG_COLOR_BLUE + " " + (9 - i) + " " + RESET_BG_COLOR + "\n";
        }
        printedBoard += SET_BG_COLOR_BLUE +
                "    a   b  c   d   e  f   g  h     " + RESET_BG_COLOR + "\n" + RESET_TEXT_COLOR;

        System.out.println(printedBoard);
    }

    private String getPiece(ChessPiece piece){
        if (piece == null){
            return EMPTY;
        }

        if (piece.getTeamColor() == WHITE){
            return switch(piece.getPieceType()){
                case PAWN -> SET_TEXT_COLOR_WHITE + WHITE_PAWN + SET_TEXT_COLOR_BLACK;
                case BISHOP -> SET_TEXT_COLOR_WHITE + WHITE_BISHOP + SET_TEXT_COLOR_BLACK;
                case ROOK -> SET_TEXT_COLOR_WHITE + WHITE_ROOK + SET_TEXT_COLOR_BLACK;
                case KING -> SET_TEXT_COLOR_WHITE + WHITE_KING + SET_TEXT_COLOR_BLACK;
                case QUEEN -> SET_TEXT_COLOR_WHITE + WHITE_QUEEN + SET_TEXT_COLOR_BLACK;
                case KNIGHT -> SET_TEXT_COLOR_WHITE + WHITE_KNIGHT + SET_TEXT_COLOR_BLACK;
            };
        }
        else {
            return switch(piece.getPieceType()){
                case PAWN -> BLACK_PAWN;
                case BISHOP -> BLACK_BISHOP;
                case ROOK -> BLACK_ROOK;
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case KNIGHT -> BLACK_KNIGHT;
            };
        }
    }
}
