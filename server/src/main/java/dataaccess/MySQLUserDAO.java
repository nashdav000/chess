package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData user) throws DataAccessException {
        var json = new Gson().toJson(user);
        // String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement = "INSERT INTO user VALUES ('%s', '%s', '%s', '%s');"
                .formatted(user.username(), user.password(), user.email(), json);

        executeStatement(statement);
    }

    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT * FROM user WHERE username='%s';".formatted(username);

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()){
                var json = rs.getString("json");
                return new Gson().fromJson(json, UserData.class);
            }

            return null;
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Unable to execute statement");
        }
    }

    public void clearUsers() throws DataAccessException {
        var statement = "DELETE FROM user;";
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


    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createUserStatements) {
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
