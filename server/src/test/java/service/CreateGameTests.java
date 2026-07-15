package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import service.gameClasses.CreateRequest;
import service.userClasses.RegisterRequest;

public class CreateGameTests {
    private final static AuthDAO authDAO = new MemoryAuthDAO();
    private final static UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final GameService gameService = new GameService(gameDAO, authDAO);
    private static String authToken;

    @BeforeAll
    public static void init() throws DataAccessException {
        String username = "Broski";
        String password = "password";
        String email = "test@email.com";
        UserService userService = new UserService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest(username, password, email);
        authToken = userService.register(request).authToken();
    }

    @Test
    @DisplayName("Create: Successful")
    public void createSuccessful() throws Exception{
        CreateRequest request = new CreateRequest(authToken, "game");
        String testID = gameService.createGame(request).gameID();
        Assertions.assertNotNull(testID);
    }

    @Test
    @DisplayName("Create: Bad Request")
    public void createBadRequest(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            CreateRequest request = new CreateRequest(authToken, null);
            gameService.createGame(request);
        });
    }

    @Test
    @DisplayName("Create: Unauthorized")
    public void createUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            CreateRequest request = new CreateRequest("hacker123", "game");
            gameService.createGame(request);
        });
    }
}
