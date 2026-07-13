package service;

import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class UserService {

    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest){
        UserData user = new UserData(registerRequest.username(),
                                     registerRequest.password(),
                                     registerRequest.email());

        try{dataAccess.createUser(user);}
        catch(Exception e){System.out.println(e);}

        String authToken = UUID.randomUUID().toString();
        return new RegisterResult(user.username(), new AuthData(authToken, user.username()));
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
