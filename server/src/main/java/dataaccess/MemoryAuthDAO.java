package dataaccess;


import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private final HashMap<String, String> activeUsers = new HashMap<>();

    public String createAuth(String username){
        activeUsers.put(username, UUID.randomUUID().toString());
        return activeUsers.get(username);
    }

    public String getAuth(String username) {
        return activeUsers.get(username);
    }

    public void deleteAuth(String username) {
        activeUsers.remove(username);
    }


}
