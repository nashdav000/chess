package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

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
            switch (command.getCommandType()){
                case CONNECT -> connect(command.getGameID().toString(), command.getAuthToken(), ctx.session);
                case MAKE_MOVE -> makeMove();
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
        if (authAccess.getAuth(authToken) == null) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid authToken");
            connections.broadcastSelf(session, error);
            return;
        }

        if (gameAccess.getGame(gameID) == null) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid gameID");
            connections.broadcastSelf(session, error);
            return;
        }

        connections.add(gameID, session);

        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
        connections.broadcastSelf(session, loadGame);

        String username;
        try {
            username = authAccess.getAuth(authToken);
        }
        catch (Exception e){
            throw new IOException(e.getMessage());
        }

        String message = "%s joined the game";
        var notifyOthers = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username);
        connections.broadcastOthers(session, notifyOthers);
    }



    private void makeMove(){

    }

    private void leave(Session session){

    }

    private void resign(){

    }
}
