package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler,WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();


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
                case CONNECT -> connect(command.getGameID().toString(), ctx.session);
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

    private void connect(String gameID, Session session) throws IOException {
        connections.add(gameID, session);
        var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameID);
        connections.broadcast(session, notification);
    }

    private void makeMove(){

    }

    private void leave(Session session){

    }

    private void resign(){

    }
}
