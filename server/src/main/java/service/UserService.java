package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.Objects;


public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException{
        // Create the user
        UserData user = new UserData(request.username(),
                                     request.password(),
                                     request.email());

        // Check if the user exists
        if (userAccess.getUser(user.username()) != null){
            throw new DataAccessException("Username already taken");
        }

        // Create the user and return a new authentification token
        userAccess.createUser(user);
        String authToken = authAccess.createAuth(user.username());
        return new RegisterResult(user.username(), authToken);
    }

    public void clearUsers(ClearRequest request){
        userAccess.clearUsers();
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData attemptedLogin = userAccess.getUser(loginRequest.username());

        // No user with that name exists
        if (attemptedLogin == null){
            throw new DataAccessException("No user with that username exists");
        }

        // No user with that name logged in
        if (authAccess.getAuth(attemptedLogin.username()) != null){
            throw new DataAccessException("Already logged in");
        }

        // Password doesn't match
        if (!Objects.equals(attemptedLogin.password(), loginRequest.password())){
            throw new DataAccessException("Username and password don't match");
        }

        return new LoginResult(loginRequest.username(), authAccess.createAuth(attemptedLogin.username()));
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        if (authorized(request.username(), request.authToken())){
            authAccess.deleteAuth(request.username());
        }
    }

    private boolean authorized(String username, String authToken) throws DataAccessException{
        if (authAccess.getAuth(username) == null) {return false;}
        if (!Objects.equals(authAccess.getAuth(username), authToken)){
            throw new DataAccessException("Unauthorized");
        }
        return true;
    }
}
