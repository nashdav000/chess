package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;


public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess; this.authAccess = authAccess;
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

        // Password doesn't match
        if (attemptedLogin.password() != loginRequest.password()){
            throw new DataAccessException("Username and password don't match");
        }

        return new LoginResult(loginRequest.username(), authAccess.createAuth(attemptedLogin.username()));
    }
//
//    public void logout(LogoutRequest logoutRequest){
//
//    }
}
