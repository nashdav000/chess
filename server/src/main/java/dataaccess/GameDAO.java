package dataaccess;

import service.GameClasses.GameInfo;
import java.util.Collection;

public interface GameDAO {
    String createGame(String gameName);

    Collection<GameInfo> listGames();

    void clearGames();
}
