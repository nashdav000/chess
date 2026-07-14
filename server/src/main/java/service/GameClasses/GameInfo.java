package service.GameClasses;

import chess.ChessGame;

public record GameInfo(String gameID, String white, String black, String gameName, ChessGame game) {
}
