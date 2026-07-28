package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        String url = args.length == 1 ? args[0] : "http://localhost:8080";

        try {
            new Repl(url).run();

        } catch (Exception e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }
}
