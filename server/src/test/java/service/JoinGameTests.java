package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;
import service.GameClasses.CreateRequest;
import service.GameClasses.JoinRequest;
import service.UserClasses.RegisterRequest;

public class JoinGameTests {
    private final static AuthDAO authDAO = new MemoryAuthDAO();
    private final static UserDAO userDAO = new MemoryUserDAO();
    private final static GameDAO gameDAO = new MemoryGameDAO();

    private final static GameService gameService = new GameService(gameDAO, authDAO);
    private final static UserService userService = new UserService(userDAO, authDAO);
    private static String username;
    private static String authToken;
    private static String testID;

    @BeforeAll
    public static void init() throws DataAccessException {
        username = "Broski";
        String password = "password";
        String email = "test@email.com";

        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        authToken = userService.register(registerRequest).authToken();
    }

    @BeforeEach
    public void createGame() throws Exception{
        CreateRequest createRequest = new CreateRequest(authToken, "game");
        testID = gameService.createGame(createRequest).gameID();
    }

    @Test
    @DisplayName("Join: Success")
    public void joinSuccessful() throws Exception{
        GameData game = gameDAO.getGame(testID);
        String white = game.whiteUsername();
        Assertions.assertNull(white);

        JoinRequest request = new JoinRequest(authToken, "WHITE", testID);
        gameService.joinGame(request);

        // Game was joined
        GameData joinedGame = gameDAO.getGame(testID);
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
            gameService.joinGame(request);
        });

        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest request = new JoinRequest(authToken, "WHITE", null);
            gameService.joinGame(request);
        });
    }

    @Test
    @DisplayName("Join: Unauthorized")
    public void joinUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest request = new JoinRequest("hacker123", "WHITE", testID);
            gameService.joinGame(request);
        });
    }

    @Test
    @DisplayName("Join: Invalid Game")
    public void joinInvalidGame(){
        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest request = new JoinRequest(authToken, "WHITE", "123456");
            gameService.joinGame(request);
        });
    }

    @Test
    @DisplayName("Join: Taken Color")
    public void joinTakenColor() throws Exception{
        // Player 1 joins the game as white
        JoinRequest request = new JoinRequest(authToken, "WHITE", testID);
        gameService.joinGame(request);

        // Register a new player
        RegisterRequest registerNew = new RegisterRequest("joe", "pw", "fake");
        String newAuth = userService.register(registerNew).authToken();

        // Player 2 joins as white
        Assertions.assertThrows(DataAccessException.class, () ->{
            JoinRequest newJoin = new JoinRequest(newAuth, "WHITE", testID);
            gameService.joinGame(request);
        });

    }
}
