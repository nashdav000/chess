package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public void createUser(UserData user) {userStorage.put(user.username(), user);}

    @Override
    public UserData getUser(UserData user) {return userStorage.get(user.username());}

    public void clearUsers(){
        userStorage = new HashMap<>();
    };
}
