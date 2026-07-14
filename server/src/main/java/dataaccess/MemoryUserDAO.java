package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> userStorage = new HashMap<>();

    public void createUser(UserData user) {
        userStorage.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return userStorage.get(username);
    }

    public void clearUsers(){
        userStorage = new HashMap<>();
    }
}
