package dataaccess;

public interface AuthDAO {

    String createAuth(String username);

    String getAuth(String username);

    void deleteAuth(String username);
}
