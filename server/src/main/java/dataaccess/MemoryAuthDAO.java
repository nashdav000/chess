package dataaccess;

import model.AuthData;

import java.util.UUID;

public class MemoryAuthDAO {
    AuthData createAuth(String username){
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, username);
    }
}
