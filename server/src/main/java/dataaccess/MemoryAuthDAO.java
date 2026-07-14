package dataaccess;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private HashMap<String, String> activeUsers = new HashMap<>();

    public String createAuth(String username){
        String authToken = UUID.randomUUID().toString();
        activeUsers.put(authToken, username);
        return authToken;
    }

    public String getAuth(String authToken) {
        return activeUsers.get(authToken);
    }

    public void deleteAuth(String authToken) {
        activeUsers.remove(authToken);
    }

    public void clearAuth(){
        activeUsers = new HashMap<>();
    }

}
