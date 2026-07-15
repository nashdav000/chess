package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import service.UserClasses.LogoutRequest;
import service.UserClasses.RegisterRequest;

public class LogoutTests {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private final UserService service = new UserService(userDAO, authDAO);
    private String authToken;

    @BeforeEach
    public void init() throws DataAccessException {
        service.clearUsers();
        service.clearAuths();

        String username = "Broski";
        String password = "password";
        String email = "test@email.com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        authToken = service.register(request).authToken();
    }

    @Test
    @DisplayName("Logout: Success")
    public void logoutExistingUser(){
        Assertions.assertDoesNotThrow(()->{
            LogoutRequest request = new LogoutRequest(authToken);
            service.logout(request);
        });
    }

    @Test
    @DisplayName("Logout: Unauthorized")
    public void logoutUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, ()->{
            LogoutRequest request = new LogoutRequest("badAuthToken");
            service.logout(request);
        });
    }
}
