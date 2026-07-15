package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import service.user.classes.LogoutRequest;
import service.user.classes.RegisterRequest;

public class LogoutTests {
    private final static UserDAO USER_DAO = new MemoryUserDAO();
    private final static AuthDAO AUTH_DAO = new MemoryAuthDAO();

    private final static UserService SERVICE = new UserService(USER_DAO, AUTH_DAO);
    private String authToken;

    @BeforeEach
    public void init() throws DataAccessException {
        SERVICE.clearUsers();
        SERVICE.clearAuths();

        String username = "Broski";
        String password = "password";
        String email = "test@email.com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        authToken = SERVICE.register(request).authToken();
    }

    @Test
    @DisplayName("Logout: Success")
    public void logoutExistingUser(){
        Assertions.assertDoesNotThrow(()->{
            LogoutRequest request = new LogoutRequest(authToken);
            SERVICE.logout(request);
        });
    }

    @Test
    @DisplayName("Logout: Unauthorized")
    public void logoutUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, ()->{
            LogoutRequest request = new LogoutRequest("badAuthToken");
            SERVICE.logout(request);
        });
    }
}
