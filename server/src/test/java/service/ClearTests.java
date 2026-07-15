package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import service.UserClasses.RegisterRequest;

public class ClearTests {
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private String username;
    private String authToken;

    @BeforeEach
    public void init() throws DataAccessException {
        userService.clearUsers();
        userService.clearAuths();

        username = "Broski";
        String password = "password";
        String email = "test@email.com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        authToken = userService.register(request).authToken();
    }

    @Test
    @DisplayName("Clear: Users")
    public void clearUsers(){
        UserData test = userDAO.getUser(username);
        Assertions.assertNotNull(test);
        userService.clearUsers();
        test = userDAO.getUser(username);
        Assertions.assertNull(test);
    }

    @Test
    @DisplayName("Clear: Games")
    public void clearGames(){

    }

    @Test
    @DisplayName("Clear: Auth")
    public void clearAuths(){
        String test = authDAO.getAuth(authToken);
        Assertions.assertNotNull(test);
        userService.clearAuths();
        test = authDAO.getAuth(authToken);
        Assertions.assertNull(test);
    }
}
