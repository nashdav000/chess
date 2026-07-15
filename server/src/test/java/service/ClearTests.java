package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import service.game.classes.CreateRequest;
import service.user.classes.RegisterRequest;

public class ClearTests {
    private final static AuthDAO AUTH_DAO = new MemoryAuthDAO();
    private final static UserDAO USER_DAO = new MemoryUserDAO();
    private final static GameDAO GAME_DAO = new MemoryGameDAO();

    private final static UserService USER_SERVICE = new UserService(USER_DAO, AUTH_DAO);
    private final static GameService GAME_SERVICE = new GameService(GAME_DAO, AUTH_DAO);
    private String username;
    private String authToken;

    @BeforeEach
    public void init() throws DataAccessException {
        USER_SERVICE.clearUsers();
        USER_SERVICE.clearAuths();

        username = "Broski";
        String password = "password";
        String email = "test@email.com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        authToken = USER_SERVICE.register(request).authToken();
    }

    @Test
    @DisplayName("Clear: Users")
    public void clearUsers(){
        UserData test = USER_DAO.getUser(username);
        Assertions.assertNotNull(test);
        USER_SERVICE.clearUsers();
        test = USER_DAO.getUser(username);
        Assertions.assertNull(test);
    }

    @Test
    @DisplayName("Clear: Games")
    public void clearGames() throws Exception {
        CreateRequest request = new CreateRequest(authToken, "game");
        String testID = GAME_SERVICE.createGame(request).gameID();
        Assertions.assertNotNull(testID);

        GameData game = GAME_DAO.getGame(testID);
        Assertions.assertNotNull(game);
        GAME_SERVICE.clearGames();
        game = GAME_DAO.getGame(testID);
        Assertions.assertNull(game);
    }

    @Test
    @DisplayName("Clear: Auth")
    public void clearAuths(){
        String test = AUTH_DAO.getAuth(authToken);
        Assertions.assertNotNull(test);
        USER_SERVICE.clearAuths();
        test = AUTH_DAO.getAuth(authToken);
        Assertions.assertNull(test);
    }
}
