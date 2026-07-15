package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.*;

public class UserServiceTests {

    private static UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    private static



    @Test
    @DisplayName("Register: Success")
    public void registerUserSuccess(){
        service.register()
    }

    @Test
    @DisplayName("Register: Bad Request")
    public void registerUserBadRequest(){

    }

    @Test
    @DisplayName("Register: Existing User")
    public void registerExistingUser(){

    }
}
