package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeStatement;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws DataAccessException {
        String[] createUserStatements = {
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
        configureDatabase(createUserStatements);
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

}
