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
        // Check if any of the fields are null
        if (request.username() == null || request.password() == null || request.email() == null){
            throw new DataAccessException(DataAccessException.Type.BadRequest,
                    "Error: One or more fields were left blank");
        }

        // Create the user
        UserData user = new UserData(request.username(),
                                     request.password(),
                                     request.email());

        // Check if the user exists
        if (userAccess.getUser(user.username()) != null){
            throw new DataAccessException(DataAccessException.Type.UsernameTaken, "Error: Username taken");
        }

        // Create the user and return a new authentification token
        userAccess.createUser(user);
        String authToken = authAccess.createAuth(user.username());
        return new RegisterResult(user.username(), authToken);
    }

    public void clearUsers(ClearRequest request){
        userAccess.clearUsers();
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        // One or more fields are null
        if (request.username() == null || request.password() == null){
            throw new DataAccessException(DataAccessException.Type.BadRequest,
                    "Error: One or more fields were left blank.");
        }

        UserData attemptedLogin = userAccess.getUser(request.username());

        // No user with that name exists
        if (attemptedLogin == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized,
                    "Error: No user with that username exists");
        }

        // Password doesn't match
        if (!Objects.equals(attemptedLogin.password(), request.password())){
            throw new DataAccessException(DataAccessException.Type.Unauthorized, "Error: Unauthorized");
        }

        return new LoginResult(request.username(), authAccess.createAuth(attemptedLogin.username()));
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        if (authorized(request.authToken())){
            authAccess.deleteAuth(request.authToken());
        }

        return new LogoutResult();
    }

    private boolean authorized(String authToken) throws DataAccessException{
        if (authAccess.getAuth(authToken) == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized, "Error: Unauthorized");
        }
        return true;
    }
}
