package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.MySQLAuthDAO;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler,WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authAccess;

    public WebSocketHandler(AuthDAO dao) {
        this.authAccess = dao;
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

    private void connect(String gameID, String authToken, Session session) throws IOException {
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
        var notifyOthers = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "%s");
        connections.broadcastOthers(session, notifyOthers);
    }



    private void makeMove(){

    }

    private void leave(Session session){

    }

    private void resign(){

    }
}
