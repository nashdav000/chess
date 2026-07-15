package service;

import dataaccess.DataAccessException;
import dataaccess.*;
import org.junit.jupiter.api.*;
import service.user.classes.LoginRequest;
import service.user.classes.LogoutRequest;
import service.user.classes.RegisterRequest;

public class LoginTests {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private final UserService service = new UserService(userDAO, authDAO);
    private String username;
    private String password;


    @BeforeEach
    public void init() throws DataAccessException {
        service.clearUsers();
        service.clearAuths();

        username = "Broski";
        password = "password";
        String email = "test@email.com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        String authToken = service.register(request).authToken();
        service.logout(new LogoutRequest(authToken));
    }

    @Test
    @DisplayName("Login: Success")
    public void loginExistingUser(){
        Assertions.assertDoesNotThrow(() -> {
            LoginRequest request = new LoginRequest(username, password);
            service.login(request);
        });
    }

    @Test
    @DisplayName("Login: Bad Request")
    public void loginBadRequest(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            LoginRequest request = new LoginRequest(username, null);
            service.login(request);
        });

        Assertions.assertThrows(DataAccessException.class, () -> {
            LoginRequest request = new LoginRequest(null, password);
            service.login(request);
        });
    }

    @Test
    @DisplayName("Login: Unauthorized")
    public void loginUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            LoginRequest request = new LoginRequest(username, "hacker123");
            service.login(request);
        });
    }
}
