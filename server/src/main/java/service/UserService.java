package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import service.user.classes.*;

import java.util.Objects;


public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException{
        // Check if any of the fields are null
        if (request.username() == null || request.password() == null || request.email() == null){
            throw new DataAccessException(DataAccessException.Type.BadRequest,
                    "Error: One or more fields were left blank");
        }

        // Check if the user exists
        if (userAccess.getUser(request.username()) != null){
            throw new DataAccessException(DataAccessException.Type.AlreadyTaken, "Error: Username taken");
        }

        // Create the user
        UserData user = new UserData(request.username(),
                request.password(),
                request.email());
        userAccess.createUser(user);

        // Return a new authentification token
        String authToken = authAccess.createAuth(user.username());
        return new RegisterResult(user.username(), authToken);
    }

    public void clearUsers() throws DataAccessException {
        userAccess.clearUsers();
    }

    public void clearAuths() throws DataAccessException {authAccess.clearAuth();}

    public LoginResult login(LoginRequest request) throws DataAccessException {
        // One or more fields are null
        if (request.username() == null || request.password() == null){
            throw new DataAccessException(DataAccessException.Type.BadRequest,
                    "Error: One or more fields were left blank.");
        }

        // No user with that name exists
        if (userAccess.getUser(request.username()) == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized,
                    "Error: No user with that username exists");
        }

        // Password doesn't match
        if (!Objects.equals(userAccess.getUser(request.username()).password(), request.password())){
            throw new DataAccessException(DataAccessException.Type.Unauthorized, "Error: Unauthorized");
        }

        return new LoginResult(request.username(),
                authAccess.createAuth(request.username()));
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        if (authAccess.getAuth(request.authToken()) == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized, "Error: Unauthorized");
        }

        authAccess.deleteAuth(request.authToken());
    }
}
