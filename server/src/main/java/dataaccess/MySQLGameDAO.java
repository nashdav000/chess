package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public String createGame(String gameName) {
        return "";
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(String gameID) {
        return null;
    }

    @Override
    public void setGame(String gameID, GameData game) {

    }

    @Override
    public void clearGames() {

    }
}
