package client;

import chess.*;

import java.util.*;

import static chess.ChessGame.TeamColor.WHITE;
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

    public void run(){
        System.out.println();
        drawBoard(null);

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")){
            promptUser();
            String line = scanner.nextLine();

            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);

                if (line.contains("leave") || line.contains("resign")){
                    result = "quit";
                }
            }
            catch(Exception e){
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR + "\n");
            }
        }
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

    private String highlight(String... params) throws ClientError {
        if (params.length >= 1){
            String move = params[0];

            if (!move.matches("^[a-h][1-8]$")){
                throw new ClientError("Error: %s is not a valid chess position".formatted(move));
            }

            int row  = move.charAt(1) - '0';
            int col = convertColToInt(move.charAt(0));

            Collection<ChessMove> moves = game.validMoves(new ChessPosition(row, col));

            ArrayList<ChessPosition> positions = new ArrayList<>();
            positions.add(new ChessPosition(row, col));

            if (moves != null){
                for (ChessMove m : moves){
                    positions.add(m.getEndPosition());
                }
            }

            drawBoard(positions);
            return "";
        }
        else {
            throw new ClientError("Error: Expected <position>");
        }

    }

    private int convertColToInt(char letter){
        return switch(letter) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> -1;
        };
    }

    private String move(String... params){

        String movement = "";
        if (params.length >= 2 && params[1].matches("[a-h][1-8]")){
            movement += params[1];
        }
        return "";
    }

    private String redraw(){
        drawBoard(null);
        return "";
    }

    private String resign(){

        return "Resigning from chess game\n";
    }

    private String leave(){

        return "Leaving chess game\n";
    }

    private void drawBoard(ArrayList<ChessPosition> positions){
        if (Objects.equals(color, "black")){
            drawBlackBoard(positions);
        }
        else{
            drawWhiteBoard(positions);
        }
    }

    private void drawBlackBoard(ArrayList<ChessPosition> positions){

        ChessBoard board = game.getBoard();
        String printedBoard =  SET_TEXT_COLOR_BLACK + SET_BG_COLOR_BLUE  +
                "    h   g  f   e   d  c   b   a    " + RESET_BG_COLOR + "\n";
        for (int i = 1; i < 9; i++){

            printedBoard += SET_BG_COLOR_BLUE + " " + (i) + " ";

            for (int j = 1; j < 9; j++){
                if (positions != null && Objects.equals(positions.getFirst(), new ChessPosition(i, 9 - j))){
                    printedBoard += SET_BG_COLOR_MAGENTA;
                }
                else if (positions != null && positions.contains(new ChessPosition(i, 9 - j))){
                    if ((i + j) % 2 == 0){
                        printedBoard += SET_BG_COLOR_GREEN;
                    }
                    else{
                        printedBoard += SET_BG_COLOR_DARK_GREEN;
                    }
                }
                else if ((i + j) % 2 == 0){
                    printedBoard += SET_BG_COLOR_LIGHT_GREY;
                }
                else{
                    printedBoard += SET_BG_COLOR_DARK_GREY;
                }

                printedBoard += getPiece(board.getPiece(new ChessPosition(i, 9 - j)));
            }

            printedBoard += SET_BG_COLOR_BLUE + " " + (i) + " " + RESET_BG_COLOR + "\n";
        }
        printedBoard += SET_BG_COLOR_BLUE +
                "    h   g  f   e   d  c   b   a    " + RESET_BG_COLOR + "\n" + RESET_TEXT_COLOR;

        System.out.println(printedBoard);
    }

    private void drawWhiteBoard(ArrayList<ChessPosition> positions){

        ChessBoard board = game.getBoard();
        String printedBoard =  SET_TEXT_COLOR_BLACK + SET_BG_COLOR_BLUE  +
                "    a   b  c   d   e  f   g  h     " + RESET_BG_COLOR + "\n";
        for (int i = 1; i < 9; i++){

            printedBoard += SET_BG_COLOR_BLUE + " " + (9 - i) + " ";

            for (int j = 1; j < 9; j++){

                if (positions != null && Objects.equals(positions.getFirst(), new ChessPosition(9 - i, j))){
                    printedBoard += SET_BG_COLOR_MAGENTA;
                }
                else if (positions != null && positions.contains(new ChessPosition(9 - i, j))){
                    if ((i + j) % 2 == 0){
                        printedBoard += SET_BG_COLOR_GREEN;
                    }
                    else{
                        printedBoard += SET_BG_COLOR_DARK_GREEN;
                    }
                }
                else if ((i + j) % 2 == 0){
                    printedBoard += SET_BG_COLOR_LIGHT_GREY;
                }
                else{
                    printedBoard += SET_BG_COLOR_DARK_GREY;
                }

                printedBoard += getPiece(board.getPiece(new ChessPosition(9 - i, j)));
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
