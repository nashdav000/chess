package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public String createGame(String gameName) throws DataAccessException {

        int gameID;

        // Find the id of the last game
        String statement = "SELECT MAX(id) AS id FROM games;";
        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();

            gameID = rs.getInt("id") + 1;
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Unable to find ID");
        }

        ChessGame game = new ChessGame();
        GameData gameData = new GameData(String.valueOf(gameID),null, null, gameName, game);
        String json = new Gson().toJson(gameData);

        statement = "INSERT INTO games (gameName, json) VALUES (?, ?);";
        executeStatement(statement, gameName, json);

        return String.valueOf(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var statement = "SELECT * FROM games;";
        Collection<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                var json = rs.getString("json");
                games.add(new Gson().fromJson(json, GameData.class));
            }

            return games;
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Unable to list games");
        }
    }

    public GameData getGame(String gameID) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE id = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setInt(1, Integer.parseInt(gameID));
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()){
                var json = rs.getString("json");
                return new Gson().fromJson(json, GameData.class);
            }

            return null;
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Unable to get games");
        }
    }

    public void setGame(String gameID, GameData game) throws DataAccessException {
        String json = new Gson().toJson(game);
        var statement = "UPDATE games SET json = ? WHERE id = ?;";
        executeStatement(statement, json, Integer.parseInt(gameID));
    }

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE TABLE games;";
        executeStatement(statement);
    }

    private void executeStatement(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            for (int i = 0; i < params.length; i++){
                Object param = params[i];

                if (param instanceof String p) {ps.setString(i + 1, p);}
                else if (param instanceof Integer p) {ps.setInt(i + 1, p);}
                else {ps.setNull(i + 1, NULL);}
            }

            ps.executeUpdate();
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL,
                    "Error: Unable to execute statement %s".formatted(statement));
        }
    }

    private final String[] createGameStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `id` int AUTO_INCREMENT,
              `gameName` varchar(256) NOT NULL,
              `json` TEXT NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createGameStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(DataAccessException.Type.SQL,
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
