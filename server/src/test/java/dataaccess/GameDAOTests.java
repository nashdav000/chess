package dataaccess;

import org.junit.jupiter.api.*;

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
    public void createAuthSuccess() throws Exception {
        String id = gameDAO.createGame("game");
        Assertions.assertEquals("game", gameDAO.getGame(id).gameName());
    }

    @Test
    @DisplayName("Create Game: Fail")
    public void createAuthFail(){
        Assertions.assertThrows(DataAccessException.class, ()->{
           gameDAO.createGame(null);
        });
    }

    @Test
    @DisplayName("Get Game: Success")
    public void getAuthSuccess(){

    }

    @Test
    @DisplayName("Get Game: Fail")
    public void getAuthFail(){

    }

    @Test
    @DisplayName("List Game: Success")
    public void listGameSuccess(){

    }

    @Test
    @DisplayName("List Game: Fail")
    public void listGameFail(){

    }

    @Test
    @DisplayName("Set Game: Success")
    public void setGameSuccess(){

    }

    @Test
    @DisplayName("Set Game: Fail")
    public void setGameFail(){

    }

    @Test
    @DisplayName("Clear Game: Success")
    public void clearGameSuccess(){

    }


}
