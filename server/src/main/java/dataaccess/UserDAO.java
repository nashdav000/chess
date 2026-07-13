package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {
    void createUser(UserData user);

    UserData getUser(String username);

    void clearUsers();
}
