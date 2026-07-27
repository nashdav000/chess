package client;

import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;


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
    @DisplayName("Register: Successful")
    public void registerSuccessful() {
        var response = facade.register(user);
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Register: Preexisting User")
    public void registerPreexisting(){

    }

}
