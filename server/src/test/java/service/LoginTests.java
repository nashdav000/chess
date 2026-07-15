package service;

import dataaccess.DataAccessException;
import dataaccess.*;
import org.junit.jupiter.api.*;
import service.user.classes.LoginRequest;
import service.user.classes.LogoutRequest;
import service.user.classes.RegisterRequest;

public class LoginTests {
    private final static UserDAO USER_DAO = new MemoryUserDAO();
    private final static AuthDAO AUTH_DAO = new MemoryAuthDAO();

    private final static UserService SERVICE = new UserService(USER_DAO, AUTH_DAO);
    private String username;
    private String password;


    @BeforeEach
    public void init() throws DataAccessException {
        SERVICE.clearUsers();
        SERVICE.clearAuths();

        username = "Broski";
        password = "password";
        String email = "test@email.com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        String authToken = SERVICE.register(request).authToken();
        SERVICE.logout(new LogoutRequest(authToken));
    }

    @Test
    @DisplayName("Login: Success")
    public void loginExistingUser(){
        Assertions.assertDoesNotThrow(() -> {
            LoginRequest request = new LoginRequest(username, password);
            SERVICE.login(request);
        });
    }

    @Test
    @DisplayName("Login: Bad Request")
    public void loginBadRequest(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            LoginRequest request = new LoginRequest(username, null);
            SERVICE.login(request);
        });

        Assertions.assertThrows(DataAccessException.class, () -> {
            LoginRequest request = new LoginRequest(null, password);
            SERVICE.login(request);
        });
    }

    @Test
    @DisplayName("Login: Unauthorized")
    public void loginUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            LoginRequest request = new LoginRequest(username, "hacker123");
            SERVICE.login(request);
        });
    }
}
