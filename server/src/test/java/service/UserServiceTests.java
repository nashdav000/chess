package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.*;
import service.TestClasses.TestUser;
import service.UserClasses.*;

public class UserServiceTests {

    private static UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    private static String username;
    private static String password;
    private static String email;

    @BeforeAll
    public static void createUser(){
        username = "Broski";
        password = "123456789";
        email = "fake@email.com";
    }

    @Test
    @DisplayName("Register: Success")
    public void registerUserSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = service.register(request);
        Assertions.assertEquals(username, result.username());
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
