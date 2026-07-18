package dataaccess;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public String createAuth(String username) {
        return "";
    }

    @Override
    public String getAuth(String authToken) {
        return "";
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clearAuth() {

    }

    private final String[] createAuthStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authTokens (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
