package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    String createGame(String gameName);

    Collection<GameData> listGames();

    GameData getGame(String gameID);

    void setGame(String gameID, GameData game);

    void clearGames();
}
