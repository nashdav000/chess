package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    String createGame(String gameName) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData getGame(String gameID) throws DataAccessException;

    void setGame(String gameID, GameData game) throws DataAccessException;

    void clearGames() throws DataAccessException;
}
