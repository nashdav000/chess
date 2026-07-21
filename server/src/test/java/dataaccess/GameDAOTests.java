package dataaccess;

import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.List;

public class GameDAOTests {

    public static GameDAO gameDAO;

    @BeforeAll
    public static void init() throws Exception {
        gameDAO = new MySQLGameDAO();
    }

    @BeforeEach
    public void clear() throws Exception {
        gameDAO.clearGames();
    }


    @Test
    @DisplayName("Create Game: Success")
    public void createGameSuccess() throws Exception {
        String id = gameDAO.createGame("game");
        Assertions.assertEquals("game", gameDAO.getGame(id).gameName());
    }

    @Test
    @DisplayName("Create Game: Fail")
    public void createGameFail(){
        Assertions.assertThrows(DataAccessException.class, ()->{
           gameDAO.createGame(null);
        });
    }

    @Test
    @DisplayName("Get Game: Success")
    public void getGameSuccess() throws Exception {
        String id = gameDAO.createGame("game");
        Assertions.assertEquals("game", gameDAO.getGame(id).gameName());
    }

    @Test
    @DisplayName("Get Game: Fail")
    public void getGameFail(){
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertNull(gameDAO.getGame("5"));
        });
    }

    @Test
    @DisplayName("List Game: Success")
    public void listGameSuccess() throws Exception {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        List<GameData> games = (List<GameData>) gameDAO.listGames();
        Assertions.assertNotNull(games);

        Assertions.assertEquals("game1", games.get(0).gameName());
        Assertions.assertEquals("game2", games.get(1).gameName());
    }

    @Test
    @DisplayName("List Game: Fail")
    public void listGameFail() throws Exception {
        Assertions.assertDoesNotThrow(() -> {
            gameDAO.listGames();
        });

        gameDAO.createGame("game");
        gameDAO.clearGames();

        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertNotNull(games);
    }

    @Test
    @DisplayName("Set Game: Success")
    public void setGameSuccess() throws Exception {
        String id = gameDAO.createGame("game");
        GameData game = gameDAO.getGame(id);
        GameData updatedGame = new GameData(game.gameID(),
                "UserJoined!", game.blackUsername(), game.gameName(), game.chessGame());

        Assertions.assertNotEquals(game, updatedGame);
        Assertions.assertEquals(game.gameID(), updatedGame.gameID());

        gameDAO.setGame(id, updatedGame);
        GameData retrievedGame = gameDAO.getGame(id);

        Assertions.assertEquals(updatedGame, retrievedGame);
        Assertions.assertEquals(updatedGame.gameID(), retrievedGame.gameID());

    }

    @Test
    @DisplayName("Set Game: Fail")
    public void setGameFail() throws Exception {
        String gameID = gameDAO.createGame("game");
        Assertions.assertThrows(Exception.class, () -> {
            gameDAO.setGame(null, null);
        });
    }

    @Test
    @DisplayName("Clear Game: Success")
    public void clearGameSuccess() throws Exception{
        String id = gameDAO.createGame("game");
        Assertions.assertNotNull(gameDAO.getGame(id));
        gameDAO.clearGames();
        Assertions.assertNull(gameDAO.getGame(id));
    }


}
