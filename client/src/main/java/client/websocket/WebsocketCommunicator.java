package client.websocket;

import chess.ChessMove;
import client.ServerException;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebsocketCommunicator(String url, NotificationHandler notificationHandler) throws ServerException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        }
        catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void connect(String authToken, String gameID) throws ServerException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, Integer.parseInt(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (Exception e){
            throw new ServerException(e.getMessage());
        }
    }

    public void makeMove(String authToken, String gameID, ChessMove move) {
        try {
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.CONNECT, authToken, Integer.parseInt(gameID), move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (Exception e){
            throw new ServerException(e.getMessage());
        }
    }

    public void leave(String authToken, String gameID){
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, Integer.parseInt(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }
        catch (Exception e){
            throw new ServerException(e.getMessage());
        }
    }

    public void resign(){

    }
}
