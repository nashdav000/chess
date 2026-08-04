package client;

import chess.*;
import client.websocket.Notification;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketCommunicator;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import java.util.*;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor;
import static ui.EscapeSequences.*;


public class GameplayClient implements NotificationHandler {

    private final WebsocketCommunicator ws;

    private final TeamColor playerColor;
    private ChessGame game;
    private final String gameID;
    private final String authToken;

    public GameplayClient(ServerFacade facade, String color, ChessGame game, String id, String authToken) {
        ws = new WebsocketCommunicator(facade.URL(), this);

        this.playerColor = switch(color.toLowerCase()){
            case "white" -> WHITE;
            case "black" -> BLACK;
            default -> null;
        };
        this.game = game;
        this.gameID = id;
        this.authToken = authToken;
    }

    public void run(){
        System.out.println();
        drawBoard(null);
        ws.connect(authToken, gameID);

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")){
            if (game.isOver()) {
                System.out.println(SET_TEXT_COLOR_RED + "Note: Game is over" + RESET_TEXT_COLOR);
            }

            promptUser();
            String line = scanner.nextLine();

            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);

                if (result.contains("Left")){
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

    //===== User Actions Functions
    private String help(){
        return SET_TEXT_COLOR_YELLOW +
               """
               "Highlight legal moves: 'highlight <position>' (e.g. f5)
               "Make a move: 'move <start> <end> <optional promotion>' (e.g f5 e4 q)
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

    private String move(String... params) throws ClientError {
        // Check if the game is over
        if (!checkIfGameOver().isEmpty()) {
            throw new ClientError ("Error: Game is over");
        }

        // Prevent observers from moving pieces
        if (playerColor == null){
            throw new ClientError("Error: Observers can't make moves");
        }

        if (params.length >= 2){
            // Validate input
            if (!params[0].matches("^[a-h][1-8]$") || !params[1].matches("^[a-h][1-8]$")){
                throw new ClientError("Error: %s %s is not valid position".formatted(params[0], params[1]));
            }

            ChessPosition start = new ChessPosition(params[0].charAt(1) - '0', convertColToInt(params[0].charAt(0)));
            ChessPosition end = new ChessPosition(params[1].charAt(1) - '0', convertColToInt(params[1].charAt(0)));

            ChessPiece.PieceType promo = params.length == 2 ? null : switch(params[2]){
                case "q", "queen" -> QUEEN;
                case "b", "bishop" -> BISHOP;
                case "r", "rook" -> ROOK;
                case "n", "knight", "k" -> KNIGHT;
                default -> null;
            };

            // Prevent opponent's from moving their opponent's piece
            if (playerColor != game.getBoard().getPiece(start).getTeamColor()){
                throw new ClientError("Error: Cannot move opponent's pieces");
            }

            // Move the piece
            ws.makeMove(authToken, gameID, new ChessMove(start, end, promo));
            return checkIfGameOver();
        }
        else{
            throw new ClientError("Error: Expected <start> <end> <optional promotion>");
        }

    }

    private String redraw(){
        System.out.print("\n");
        drawBoard(null);
        return checkIfGameOver();
    }

    private String resign(){
        // Resignation Confirmation
        System.out.print(SET_TEXT_COLOR_BLUE + "Confirm resignation? (Y/N)   " + RESET_TEXT_COLOR);
        String s = new Scanner(System.in).nextLine();
        if (!s.toLowerCase().contains("y")){
            return "";
        }

        ws.resign(authToken, gameID);
        return "Resigned from chess game\n";
    }

    private String leave(){
        // Leave Confirmation
        System.out.print(SET_TEXT_COLOR_BLUE + "Confirm leave? (Y/N)   " + RESET_TEXT_COLOR);
        String leaving = new Scanner(System.in).nextLine();
        if (!leaving.toLowerCase().contains("y")){
            return "";
        }

        ws.leave(authToken, gameID);
        return "Left chess game\n";
    }

    //====== User Actions Helper Functions
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

    private char convertIntToCol(char col){
        return switch(col) {
            case '1' -> 'a';
            case '2' -> 'b';
            case '3' -> 'c';
            case '4' -> 'd';
            case '5' -> 'e';
            case '6' -> 'f';
            case '7' -> 'g';
            case '8' -> 'h';
            default -> '-';
        };
    }

    private String checkIfGameOver(){
        if (game.isInCheckmate(BLACK)) {
            return "Game over: White wins";
        }
        if (game.isInCheckmate(WHITE)) {
            return "Game over: Black wins";
        }
        if (game.isInStalemate(BLACK) || game.isInStalemate(WHITE)) {
            return "Game over: Stalemate";
        }

        return "";
    }

    private void drawBoard(ArrayList<ChessPosition> positions){
        if (Objects.equals(playerColor, BLACK)){
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

                // Highlight Board Drawing
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
                // Normal Board Drawing
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

                // Highlight Board Drawing
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
                // Normal Board Drawing
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

    //===== Websocket Functions
    public void notify(Notification notif) {
        System.out.print(ERASE_LINE + SET_TEXT_COLOR_GREEN + handleMessage(notif) + RESET_TEXT_COLOR);
        promptUser();
    }

    private String handleMessage(Notification notif){
        if (notif.serverMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ChessBoard updatedBoard = new Gson().fromJson(notif.game(), ChessBoard.class);
            if (updatedBoard != null) {
                this.game.setBoard(updatedBoard);
                redraw();
                return "";
            }
        }

        return switch(notif.serverMessageType()){
            case LOAD_GAME -> notif.game();
            case ERROR -> SET_TEXT_COLOR_RED + notif.errorMessage();
            case NOTIFICATION -> handleNotifications(notif.message());
        };
    }

    //===== Websocket Helper Functions

    private String handleNotifications(String msg) {
        if (!msg.contains("moved")) {
            return msg;
        }

        String startPos = msg.substring(msg.length() - 13, msg.length() - 7);
        String endPos = msg.substring(msg.length() - 6);

        String notatedStart = convertIntToCol(startPos.charAt(4)) + "" + startPos.charAt(1);
        String notatedEnd = convertIntToCol(endPos.charAt(4)) + "" + endPos.charAt(1);

        return msg.substring(0, msg.length() - 14) + " %s %s".formatted(notatedStart, notatedEnd);
    }
}
