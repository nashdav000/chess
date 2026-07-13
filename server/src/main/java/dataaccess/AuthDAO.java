package dataaccess;

import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    AuthData createAuth(String username);
}
