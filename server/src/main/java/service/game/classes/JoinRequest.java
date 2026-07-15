package service.game.classes;

public record JoinRequest(String authToken, String playerColor, String gameID) {
}
