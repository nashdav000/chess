package service.GameClasses;

import chess.ChessGame;

public record GameInfo(String gameID, String whiteUsername, String blackUsername, String gameName, ChessGame chessGame) {
}
