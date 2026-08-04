package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler,WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;

    public WebSocketHandler(AuthDAO auth, GameDAO game) {
        this.authAccess = auth;
        this.gameAccess = game;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            MakeMoveCommand moveCommand = null;
            if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
            }

            switch (command.getCommandType()){
                case CONNECT -> connect(command.getGameID().toString(), command.getAuthToken(), ctx.session);
                case MAKE_MOVE -> makeMove(command.getGameID().toString(), command.getAuthToken(), moveCommand.getMove(), ctx.session);
                case LEAVE -> leave(command.getGameID().toString(), command.getAuthToken(), ctx.session);
                case RESIGN -> resign(command.getGameID().toString(), command.getAuthToken(), ctx.session);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String gameID, String authToken, Session session) throws Exception {

        // Data Validation
        if (!validInput(gameID, authToken, session)){
            return;
        }

        // Add the root client
        connections.add(gameID, session);

        // Notify the root client they joined the game
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, "Joined game %s".formatted(gameID));
        connections.broadcastSelf(session, loadGame);


        // Notify all other clients in the game root client joined
        String username = authAccess.getAuth(authToken);
        String message = "%s joined the game as".formatted(username);
        if (Objects.equals(gameAccess.getGame(gameID).blackUsername(), username)) {
            message += " black";
        }
        else if (Objects.equals(gameAccess.getGame(gameID).whiteUsername(), username)) {
            message += " white";
        }
        else {
            message += " an observer";
        }

        var notifyOthers = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastOthers(session, notifyOthers);
    }

    private void makeMove(String gameID, String authToken, ChessMove move, Session session) throws Exception {

        // Validate input
        if (!validInput(gameID, authToken, session)) {
            return;
        }

        // Get the game
        GameData game = gameAccess.getGame(gameID);

        // Check if the game is over
        if (game.chessGame().isOver()) {
            String message = "Error: Game is over";
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastSelf(session, error);
            return;
        }

        // Validate the player can make the move
        ChessGame.TeamColor pieceColor = game.chessGame().getBoard().getPiece(move.getStartPosition()).getTeamColor();
        String username = authAccess.getAuth(authToken);

        if (pieceColor == ChessGame.TeamColor.WHITE && !Objects.equals(username, game.whiteUsername())) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Not your color");
            connections.broadcastSelf(session, error);
            return;
        }

        if (pieceColor == ChessGame.TeamColor.BLACK && !Objects.equals(username, game.blackUsername())) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Not your color");
            connections.broadcastSelf(session, error);
            return;
        }

        // Make the move
        try {
            game.chessGame().makeMove(move);
        }
        catch (InvalidMoveException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid move");
            connections.broadcastSelf(session, error);
            return;
        }

        isInCheckCheckmateStalemate(game);
        gameAccess.setGame(gameID, game);
        game = gameAccess.getGame(gameID);

        // Broadcast to everyone that a move was made and the new board
        String json = new Gson().toJson(game.chessGame().getBoard());
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, json);
        connections.broadcastAll(session, loadGame);

        // Broadcast move
        String message = "%s moved %s %s".formatted(username, move.getStartPosition().toString(), move.getEndPosition().toString());
        var notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastOthers(session, notif);

        // Check if check, checkmate, or stalemate
        message = isInCheckCheckmateStalemate(game);
        if (!message.isEmpty()) {
            var gameUpdate = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcastAll(session, gameUpdate);
        }
    }

    private void leave(String gameID, String authToken, Session session) throws Exception {
        // Data Validation
        if (!validInput(gameID, authToken, session)){
            return;
        }

        // Get the data
        GameData game = gameAccess.getGame(gameID);
        String username = authAccess.getAuth(authToken);
        String message = "%s has left the game".formatted(username);

        // Remove the player from the game
        if (Objects.equals(username, game.whiteUsername())) {
            game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.chessGame());
        }
        else if (Objects.equals(username, game.blackUsername())) {
            game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.chessGame());
        }
        else {
            message = "%s has stopped observing the game".formatted(username);
        }
        gameAccess.setGame(gameID, game);

        // Broadcast to other players that someone left
        var notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastOthers(session, notif);
        connections.remove(session);
    }

    private void resign(String gameID, String authToken, Session session) throws Exception {
        // Data Validation
        if (!validInput(gameID, authToken, session)){
            return;
        }

        // Get the data
        GameData game = gameAccess.getGame(gameID);
        String username = authAccess.getAuth(authToken);
        String message = "%s has resigned".formatted(username);

        // Check if the game is over
        if (game.chessGame().isOver()) {
            message = "Error: Game is over";
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastSelf(session, error);
            return;
        }

        // Check if they are an actual player
        if (!Objects.equals(username, game.whiteUsername()) && !Objects.equals(username, game.blackUsername())) {
            message = "Error: Observers cannot resign";
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
            connections.broadcastSelf(session, error);
            return;
        }

        // Mark the game as done
        game.chessGame().markGameAsOver();
        gameAccess.setGame(gameID, game);

        // Broadcast to other players that someone resigned
        var notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastAll(session, notif);
        connections.remove(session);
    }

    //===== Helper Functions
    private boolean validInput(String gameID, String authToken, Session session) throws Exception {
        try {
            String username = authAccess.getAuth(authToken);
            if (username == null) {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid authToken");
                connections.broadcastSelf(session, error);
                return false;
            }

            if (gameAccess.getGame(gameID) == null) {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid gameID");
                connections.broadcastSelf(session, error);
                return false;
            }

            return true;
        }
        catch (Exception e) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unable to connect to server");
            connections.broadcastSelf(session, error);
            return false;
        }
    }

    private String isInCheckCheckmateStalemate(GameData game) {
        if (game.chessGame().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return "%s has been checkmated. %s wins".formatted(game.whiteUsername(), game.blackUsername());
        }
        else if (game.chessGame().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return "%s has been checkmated. %s wins".formatted(game.blackUsername(), game.whiteUsername());
        }
        else if (game.chessGame().isInStalemate(ChessGame.TeamColor.WHITE)) {
            return "%s has been stalemated. The game is a draw".formatted(game.whiteUsername());
        }
        else if (game.chessGame().isInStalemate(ChessGame.TeamColor.BLACK)) {
            return "%s is in stalemate. The game is a draw".formatted(game.blackUsername());
        }
        else if (game.chessGame().isInCheck(ChessGame.TeamColor.WHITE)) {
            return "%s is in check".formatted(game.whiteUsername());
        }
        else if (game.chessGame().isInCheck(ChessGame.TeamColor.BLACK)) {
            return "%s is in check".formatted(game.blackUsername());
        }

        return "";
    }
}
