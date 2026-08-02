package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    String game;

    public LoadGameMessage(ServerMessageType type, String game) {
        super(type);
        this.game = game;
    }
}
