package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import service.UserClasses.RegisterRequest;
import service.UserClasses.RegisterResult;

public class RegisterTests {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private final UserService service = new UserService(userDAO, authDAO);
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
        service.clearUsers();
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = service.register(request);
        Assertions.assertEquals(username, result.username());
    }

    @Test
    @DisplayName("Register: Bad Request")
    public void registerUserBadRequest(){
        RegisterRequest r1 = new RegisterRequest(username, password, null);
        Assertions.assertThrows(DataAccessException.class, () -> {service.register(r1);});

        RegisterRequest r2 = new RegisterRequest(username, null, email);
        Assertions.assertThrows(DataAccessException.class, () -> {service.register(r2);});

        RegisterRequest r3 = new RegisterRequest(null, password, email);
        Assertions.assertThrows(DataAccessException.class, () -> {service.register(r3);});
    }

    @Test
    @DisplayName("Register: Existing User")
    public void registerExistingUser(){


        Assertions.assertThrows(DataAccessException.class, () -> {
            RegisterRequest request = new RegisterRequest(username, password, email);
            RegisterResult result = service.register(request);

            RegisterRequest newUser = new RegisterRequest(username, password, email);
            RegisterResult newResult = service.register(newUser);
        });
    }

}
