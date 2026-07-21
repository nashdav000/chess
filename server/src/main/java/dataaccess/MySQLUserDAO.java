package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData user) throws DataAccessException {
        String json = new Gson().toJson(user);
        var statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?);";
        executeStatement(statement, user.username(), user.password(), user.email(), json);
    }

    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT * FROM users WHERE username='%s';".formatted(username);

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {


            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()){
                String json = rs.getString("json");
                return new Gson().fromJson(json, UserData.class);
            }

            return null;
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL, "Error: Unable to get user");
        }
    }

    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE TABLE users;";
        executeStatement(statement);
    }

    private void executeStatement(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement)) {

            for (int i = 0; i < params.length; i++){
                Object param = params[i];

                if (param instanceof String p){ps.setString(i + 1, p);}
                else {ps.setNull(i + 1, NULL);}
            }

            ps.executeUpdate();
        }
        catch(Exception e){
            throw new DataAccessException(DataAccessException.Type.SQL,
                    "Error: Unable to execute statement %s".formatted(statement));
        }
    }


    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
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
