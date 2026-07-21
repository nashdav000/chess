package dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.UUID;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeStatement;
import static java.sql.Types.NULL;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws DataAccessException {
        String[] createAuthStatements = {
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
        configureDatabase(createAuthStatements);
    }

    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO authTokens (authToken, username) VALUES (?, ?);";
        executeStatement(statement, authToken, username);
        return authToken;
    }

    public String getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM authTokens WHERE authToken = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            if (authToken instanceof String p){preparedStatement.setString(1, p);}
            else {preparedStatement.setNull(1, NULL);}

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()){
                return rs.getString("username");
            }

            return null;
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Error: Could not get authToken");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authTokens WHERE authToken = ?;";
        executeStatement(statement, authToken);
    }

    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE TABLE authTokens;";
        executeStatement(statement);
    }

}
