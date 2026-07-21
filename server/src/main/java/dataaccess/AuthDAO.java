package dataaccess;

public interface AuthDAO {

    String createAuth(String username) throws DataAccessException;

    String getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clearAuth() throws DataAccessException;
}
