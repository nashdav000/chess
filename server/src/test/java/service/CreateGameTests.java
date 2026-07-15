package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import service.game.classes.CreateRequest;
import service.user.classes.RegisterRequest;

public class CreateGameTests {
    private final static AuthDAO AUTH_DAO = new MemoryAuthDAO();
    private final static UserDAO USER_DAO = new MemoryUserDAO();
    private final static GameDAO GAMEDAO = new MemoryGameDAO();

    private final static GameService GAME_SERVICE = new GameService(GAMEDAO, AUTH_DAO);
    private static String authToken;

    @BeforeAll
    public static void init() throws DataAccessException {
        String username = "Broski";
        String password = "password";
        String email = "test@email.com";
        UserService userService = new UserService(USER_DAO, AUTH_DAO);

        RegisterRequest request = new RegisterRequest(username, password, email);
        authToken = userService.register(request).authToken();
    }

    @Test
    @DisplayName("Create: Successful")
    public void createSuccessful() throws Exception{
        CreateRequest request = new CreateRequest(authToken, "game");
        String testID = GAME_SERVICE.createGame(request).gameID();
        Assertions.assertNotNull(testID);
    }

    @Test
    @DisplayName("Create: Bad Request")
    public void createBadRequest(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            CreateRequest request = new CreateRequest(authToken, null);
            GAME_SERVICE.createGame(request);
        });
    }

    @Test
    @DisplayName("Create: Unauthorized")
    public void createUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            CreateRequest request = new CreateRequest("hacker123", "game");
            GAME_SERVICE.createGame(request);
        });
    }
}
