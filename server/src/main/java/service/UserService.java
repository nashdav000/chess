package service;

import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

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

        return new RegisterResult(user.username(), new AuthData("123", user.username()));
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
