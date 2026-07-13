package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class UserService {

    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException{
        // Create the user
        UserData user = new UserData(request.username(),
                                     request.password(),
                                     request.email());

        // Check if the user exists
        if (dataAccess.getUser(user) != null){
            throw new DataAccessException("Username already taken");
        }

        // Create the user and return a new authentification token
        dataAccess.createUser(user);
        String authToken = UUID.randomUUID().toString();
        return new RegisterResult(user.username(), new AuthData(authToken, user.username()));
    }

    public void clearUsers(ClearRequest request){
        dataAccess.clearUsers();
    }
//
//    public LoginResult login(LoginRequest loginRequest) {
//
//    }
//
//    public void logout(LogoutRequest logoutRequest){
//
//    }
}
