package client;

import model.AuthData;
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
        AuthData response = facade.register(user);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.authToken().length() > 10);
        Assertions.assertEquals(user.username(), response.username());
    }

    @Test
    @DisplayName("Register: Preexisting User")
    public void registerPreexisting(){
        facade.register(user);
        Assertions.assertThrows(ServerException.class, () -> facade.register(user));
    }

}
