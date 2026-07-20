package dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO authTokens VALUES ('%s', '%s');".formatted(authToken, username);
        executeStatement(statement);
        return authToken;
    }

    public String getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM authTokens WHERE authToken = '%s';".formatted(authToken);

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()){
                return rs.getString("username");
            }

            return null;
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Error: Database");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authTokens WHERE authToken = '%s';".formatted(authToken);
        executeStatement(statement);
    }

    public void clearAuth() throws DataAccessException {
        var statement = "DELETE FROM authTokens;";
        executeStatement(statement);
    }

    private void executeStatement(String statement) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Unable to execute statement");
        }
    }

    private final String[] createAuthStatements = {
            """
            CREATE TABLE IF NOT EXISTS authTokens (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createAuthStatements) {
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
