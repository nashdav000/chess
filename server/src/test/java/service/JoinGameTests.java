package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;
import service.game.classes.CreateRequest;
import service.game.classes.JoinRequest;
import service.user.classes.RegisterRequest;

public class JoinGameTests {
    private final static AuthDAO AUTH_DAO = new MemoryAuthDAO();
    private final static UserDAO USER_DAO = new MemoryUserDAO();
    private final static GameDAO GAME_DAO = new MemoryGameDAO();

    private final static GameService GAME_SERVICE = new GameService(GAME_DAO, AUTH_DAO);
    private final static UserService USER_SERVICE = new UserService(USER_DAO, AUTH_DAO);
    private static String username;
    private static String authToken;
    private static String testID;

    @BeforeAll
    public static void init() throws DataAccessException {
        username = "Broski";
        String password = "password";
        String email = "test@email.com";

        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        authToken = USER_SERVICE.register(registerRequest).authToken();
    }

    @BeforeEach
    public void createGame() throws Exception{
        CreateRequest createRequest = new CreateRequest(authToken, "game");
        testID = GAME_SERVICE.createGame(createRequest).gameID();
    }

    @Test
    @DisplayName("Join: Success")
    public void joinSuccessful() throws Exception{
        GameData game = GAME_DAO.getGame(testID);
        String white = game.whiteUsername();
        Assertions.assertNull(white);

        JoinRequest request = new JoinRequest(authToken, "WHITE", testID);
        GAME_SERVICE.joinGame(request);

        // Game was joined
        GameData joinedGame = GAME_DAO.getGame(testID);
        Assertions.assertEquals(username, joinedGame.whiteUsername());

        // Check everything is the same
        game = new GameData(testID, username, game.blackUsername(),
                game.gameName(), game.chessGame());

        Assertions.assertEquals(game, joinedGame);
    }

    @Test
    @DisplayName("Join: Bad Request")
    public void joinBadRequest(){
        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest request = new JoinRequest(authToken, null, testID);
            GAME_SERVICE.joinGame(request);
        });

        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest request = new JoinRequest(authToken, "WHITE", null);
            GAME_SERVICE.joinGame(request);
        });
    }

    @Test
    @DisplayName("Join: Unauthorized")
    public void joinUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest request = new JoinRequest("hacker123", "WHITE", testID);
            GAME_SERVICE.joinGame(request);
        });
    }

    @Test
    @DisplayName("Join: Invalid Game")
    public void joinInvalidGame(){
        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest request = new JoinRequest(authToken, "WHITE", "123456");
            GAME_SERVICE.joinGame(request);
        });
    }

    @Test
    @DisplayName("Join: Taken Color")
    public void joinTakenColor() throws Exception{
        // Player 1 joins the game as white
        JoinRequest request = new JoinRequest(authToken, "WHITE", testID);
        GAME_SERVICE.joinGame(request);

        // Register a new player
        RegisterRequest registerNew = new RegisterRequest("joe", "pw", "fake");
        String newAuth = USER_SERVICE.register(registerNew).authToken();

        // Player 2 joins as white
        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest newJoin = new JoinRequest(newAuth, "WHITE", testID);
            GAME_SERVICE.joinGame(newJoin);
        });

    }
}
