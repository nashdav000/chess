package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.*;
import service.UserClasses.LogoutRequest;
import service.UserClasses.RegisterRequest;

public class LogoutTests {
    private final static UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
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
