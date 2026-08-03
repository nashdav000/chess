package client.websocket;

import websocket.messages.ServerMessage;

public record Notification(ServerMessage.ServerMessageType serverMessageType, String game, String message, String errorMessage) {

}
