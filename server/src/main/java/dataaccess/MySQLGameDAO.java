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

    private final String[] createGameStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              'gameName' varchar(256) NOT NULL,
              'chessGame' TEXT NOT NULL
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
