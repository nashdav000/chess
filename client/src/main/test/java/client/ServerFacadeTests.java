package client;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static UserData user;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        var url = "http://localhost:" + server.port();
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(url);
        user = new UserData("Joe", "password", "email");
    }

    @BeforeEach
    public void clear(){
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("Register: Successful")
    public void registerSuccessful() {
        AuthData response = facade.register(user);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.authToken().length() > 10);
        Assertions.assertEquals(user.username(), response.username());
    }

    @Test
    @Order(1)
    @DisplayName("Register: Preexisting User")
    public void registerPreexisting(){
        facade.register(user);
        Assertions.assertThrows(ServerException.class, () -> facade.register(user));
    }

    @Test
    @Order(1)
    @DisplayName("Register: Bad Request")
    public void registerBadRequest(){
        Assertions.assertThrows(ServerException.class, () -> facade.register(new UserData(null, null, null)));
    }

    @Test
    @Order(2)
    @DisplayName("Login: Successful")
    public void loginSuccessful(){
        facade.register(user);
        restartServer();
        var result = facade.login(user.username(), user.password());
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(2)
    @DisplayName("Login: Nonexistent User")
    public void loginNonexistent(){
        Assertions.assertThrows(ServerException.class, ()-> facade.login("Cody", "password"));
    }

    @Test
    @Order(2)
    @DisplayName("Login: Bad Request")
    public void loginBadRequest(){
        Assertions.assertThrows(ServerException.class, ()-> facade.login(null, null));
    }

    @Test
    @Order(2)
    @DisplayName("Login: Wrong Password")
    public void loginWrongPassword(){
        facade.register(user);
        restartServer();

        Assertions.assertThrows(ServerException.class, ()-> facade.login(user.username(), "om-nom"));
    }

    @Test
    @Order(3)
    @DisplayName("Logout: Successful")
    public void logoutSuccessful(){
        AuthData authToken = facade.register(user);
        facade.logout(authToken.authToken());
    }

    @Test
    @Order(3)
    @DisplayName("Logout: Unauthorized")
    public void logoutUnauthorized(){
        Assertions.assertThrows(ServerException. class, () -> facade.logout("fake"));
    }

    @Test
    @Order(3)
    @DisplayName("Logout: Not logged in")
    public void logoutNotLoggedIn(){
        AuthData auth = facade.register(user);
        facade.logout(auth.authToken());
        Assertions.assertThrows(ServerException.class, () -> facade.logout(auth.authToken()));
    }

    @Test
    @Order(4)
    @DisplayName("Create Game: Successful")
    public void createGameSuccessful(){
        AuthData auth = facade.register(user);
        String id = facade.createGame("Name", auth.authToken());
        Assertions.assertNotNull(id);
    }

    @Test
    @Order(4)
    @DisplayName("Create Game: Unauthorized")
    public void createGameUnauthorized(){
        Assertions.assertThrows(ServerException.class, () -> facade.createGame("", null));
    }

    @Test
    @Order(5)
    @DisplayName("List Games: Successful")
    public void listGamesSuccessful(){
        String authToken = facade.register(user).authToken();

        List<GameData> games = facade.listGames(authToken);
        Assertions.assertEquals(0, games.size());

        facade.createGame("game", authToken);
        games = facade.listGames(authToken);
        Assertions.assertEquals(1, games.size());

        facade.createGame("game", authToken);
        games = facade.listGames(authToken);
        Assertions.assertEquals(2, games.size());
    }

    @Test
    @Order(5)
    @DisplayName("List Games: Joined Game Displays Correct Information")
    public void listGamesUpdated(){
        String authToken = facade.register(user).authToken();
        String id = facade.createGame("game", authToken);
        facade.joinGame("WHITE", id, authToken);

        List<GameData> games = facade.listGames(authToken);
        Assertions.assertEquals(1, games.size());

        Assertions.assertEquals(user.username(), games.getFirst().whiteUsername());
    }

    @Test
    @Order(5)
    @DisplayName("List Games: Unauthorized")
    public void listGamesUnauthorized(){
        Assertions.assertThrows(ServerException.class, () -> facade.listGames("fake"));
    }

    @Test
    @Order(6)
    @DisplayName("Join Game: Successful")
    public void joinGameSuccessful(){
        String a = facade.register(user).authToken();
        String id = facade.createGame("game", a);

        Assertions.assertDoesNotThrow(() -> facade.joinGame("WHITE", id, a));

        List<GameData> games = facade.listGames(a);
        Assertions.assertEquals(1, games.size());

        GameData game = games.getFirst();

        Assertions.assertEquals(id, game.gameID());
        Assertions.assertNotNull(game.whiteUsername());
    }

    @Test
    @Order(6)
    @DisplayName("Join Game: Bad Request")
    public void joinGameBadRequest(){
        String a = facade.register(user).authToken();
        String id = facade.createGame("game", a);
        Assertions.assertThrows(ServerException.class, () -> facade.joinGame("Pineapple", id, a));
    }

    @Test
    @Order(6)
    @DisplayName("Join Game: Nonexistent")
    public void joinGameNonexistent(){
        String a = facade.register(user).authToken();
        facade.createGame("game", a);
        Assertions.assertThrows(ServerException.class, () -> facade.joinGame("WHITE", "1252145", a));
    }

    @Test
    @Order(6)
    @DisplayName("Join Game: Unauthorized")
    public void joinGameUnauthorized(){
        String a = facade.register(user).authToken();
        String id = facade.createGame("game", a);
        Assertions.assertThrows(ServerException.class, () -> facade.joinGame("Pineapple", id, null));
    }

    @Test
    @Order(7)
    @DisplayName("Clear: Successful")
    public void clearSuccessful(){
        String a = facade.register(user).authToken();
        Assertions.assertEquals(0, facade.listGames(a).size());
        facade.createGame("game", a);
        Assertions.assertEquals(1, facade.listGames(a).size());

        facade.clear();
        Assertions.assertDoesNotThrow(() -> {
            String auth = facade.register(user).authToken();
            Assertions.assertEquals(0, facade.listGames(auth).size());
        });

    }

    private void restartServer(){
        server.stop();
        server = new Server();
        server.run(0);
        var url = "http://localhost:" + server.port();
        facade = new ServerFacade(url);
    }
}
