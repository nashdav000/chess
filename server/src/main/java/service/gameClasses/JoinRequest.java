package service.gameClasses;

public record JoinRequest(String authToken, String playerColor, String gameID) {
}
