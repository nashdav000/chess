package dataaccess;

import chess.ChessGame;
import service.GameClasses.GameInfo;
import java.util.Collection;

public interface GameDAO {
    String createGame(String gameName);

    Collection<GameInfo> listGames();

    GameInfo getGame(String gameID);

    void setGame(String gameID, GameInfo game);

    void clearGames();
}
