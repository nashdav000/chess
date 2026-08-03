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
                case LEAVE -> leave(ctx.session);
                case RESIGN -> resign();
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

        // Make a move
        try {
            game.chessGame().makeMove(move);
        }
        catch (InvalidMoveException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid move");
            connections.broadcastSelf(session, error);
            return;
        }
        gameAccess.setGame(gameID, game);

        // Broadcast to everyone that a move was made
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
        connections.broadcastAll(session, loadGame);

        // Broadcast move
        String username = authAccess.getAuth(authToken);
        String message = "%s moved %s %s".formatted(username, move.getStartPosition().toString(), move.getEndPosition().toString());
        var notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastOthers(session, notif);

        // Check if check, checkmate, or stalemate
        message = isIncheckCheckmateStalemate(game);
        if (!message.isEmpty()) {
            var gameUpdate = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcastAll(session, gameUpdate);
        }
    }

    private void leave(Session session){
        connections.remove(session);
    }

    private void resign(){

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

    private String isIncheckCheckmateStalemate(GameData game) {
        if (game.chessGame().isInCheck(ChessGame.TeamColor.WHITE)) {
            return "%s is in check".formatted(game.whiteUsername());
        }

        if (game.chessGame().isInCheck(ChessGame.TeamColor.BLACK)) {
            return "%s is in check".formatted(game.blackUsername());
        }

        if (game.chessGame().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return "%s is in checkmate".formatted(game.whiteUsername());
        }

        if (game.chessGame().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return "%s is in checkmate".formatted(game.blackUsername());
        }

        if (game.chessGame().isInStalemate(ChessGame.TeamColor.WHITE)) {
            return "%s is in stalemate".formatted(game.whiteUsername());
        }

        if (game.chessGame().isInStalemate(ChessGame.TeamColor.BLACK)) {
            return "%s is in stalemate".formatted(game.blackUsername());
        }

        return "";
    }
}
