package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public void createUser(UserData user) {
        userStorage.put(user.username(), user);

        userStorage.forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + value.password());
        });
    }

    @Override
    public UserData getUser(UserData user) {
        return userStorage.get(user.username());
    }
}
