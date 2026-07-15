package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import service.user.classes.RegisterRequest;
import service.user.classes.RegisterResult;

public class RegisterTests {
    private final static UserDAO USER_DAO = new MemoryUserDAO();
    private final static AuthDAO AUTH_DAO = new MemoryAuthDAO();

    private final static UserService SERVICE = new UserService(USER_DAO, AUTH_DAO);
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
        SERVICE.clearUsers();
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = SERVICE.register(request);
        Assertions.assertEquals(username, result.username());
    }

    @Test
    @DisplayName("Register: Bad Request")
    public void registerUserBadRequest(){
        RegisterRequest r1 = new RegisterRequest(username, password, null);
        Assertions.assertThrows(DataAccessException.class, () -> SERVICE.register(r1));

        RegisterRequest r2 = new RegisterRequest(username, null, email);
        Assertions.assertThrows(DataAccessException.class, () -> SERVICE.register(r2));

        RegisterRequest r3 = new RegisterRequest(null, password, email);
        Assertions.assertThrows(DataAccessException.class, () -> SERVICE.register(r3));
    }

    @Test
    @DisplayName("Register: Existing User")
    public void registerExistingUser(){


        Assertions.assertThrows(DataAccessException.class, () -> {
            RegisterRequest request = new RegisterRequest(username, password, email);
            SERVICE.register(request);

            RegisterRequest newUser = new RegisterRequest(username, password, email);
            SERVICE.register(newUser);
        });
    }

}
