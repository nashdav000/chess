package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    ChessMove move;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {return move;}
}
