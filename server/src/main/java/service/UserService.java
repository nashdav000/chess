package service;


import model.AuthData;
import model.UserData;

public class UserService {


    public RegisterResult register(RegisterRequest registerRequest){
        UserData user = new UserData(registerRequest.username(),
                                     registerRequest.password(),
                                     registerRequest.email());

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
