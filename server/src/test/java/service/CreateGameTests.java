package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;
import service.GameClasses.CreateRequest;
import service.UserClasses.RegisterRequest;

public class CreateGameTests {
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

    }
}
