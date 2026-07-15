package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import service.game.classes.CreateRequest;
import service.game.classes.ListRequest;
import service.user.classes.RegisterRequest;

import java.util.Collection;

public class ListGameTests {
    private final static AuthDAO AUTH_DAO = new MemoryAuthDAO();
    private final static UserDAO USER_DAO = new MemoryUserDAO();
    private final static GameDAO GAME_DAO = new MemoryGameDAO();

    private final static GameService GAME_SERVICE = new GameService(GAME_DAO, AUTH_DAO);
    private static String authToken;
    private static String testID;

    @BeforeAll
    public static void init() throws DataAccessException {
        String username = "Broski";
        String password = "password";
        String email = "test@email.com";
        UserService userService = new UserService(USER_DAO, AUTH_DAO);

        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        authToken = userService.register(registerRequest).authToken();

        CreateRequest createRequest = new CreateRequest(authToken, "game");
        testID = GAME_SERVICE.createGame(createRequest).gameID();
    }

    @Test
    @DisplayName("List: Successful")
    public void listSuccessful() throws Exception{
        ListRequest request = new ListRequest(authToken);
        Collection<GameData> games = GAME_SERVICE.listGames(request).games();

        Assertions.assertNotNull(games);
        GameData game = GAME_DAO.getGame(testID);

        Assertions.assertEquals(games.toArray()[0], game);
    }

    @Test
    @DisplayName("List: Unauthorized")
    public void listUnauthorized(){
        Assertions.assertThrows(DataAccessException.class, () ->{
            ListRequest request = new ListRequest("hacker123");
            GAME_SERVICE.listGames(request);
        });
    }
}
