package dataaccess;

import org.junit.jupiter.api.*;

public class AuthDAOTests {

    public static AuthDAO authDAO;

    @BeforeAll
    public static void init() throws Exception {
        authDAO = new MySQLAuthDAO();
    }

    @BeforeEach
    public void reset() throws Exception {
        authDAO.clearAuth();
    }

    @Test
    @DisplayName("Create Auth: Success")
    public void createAuthSuccess() throws Exception {
        String authToken = authDAO.createAuth("joe");
        Assertions.assertNotNull(authToken);
        Assertions.assertEquals("joe", authDAO.getAuth(authToken));
    }

    @Test
    @DisplayName("Create Auth: Fail")
    public void createAuthFail() {
        Assertions.assertThrows(DataAccessException.class, ()->{
            authDAO.createAuth(null);
        });
    }

    @Test
    @DisplayName("Get Auth: Success")
    public void getAuthSuccess() throws Exception {
        String authToken = authDAO.createAuth("joe");
        Assertions.assertNotNull(authToken);
        Assertions.assertEquals("joe", authDAO.getAuth(authToken));
    }

    @Test
    @DisplayName("Get Auth: Fail")
    public void getAuthFail() throws Exception {
        authDAO.createAuth("joe");
        Assertions.assertNull(authDAO.getAuth(null));
    }

    @Test
    @DisplayName("Delete Auth: Success")
    public void deleteAuthSuccess() throws Exception {
        String authToken = authDAO.createAuth("joe");
        Assertions.assertNotNull(authToken);
        authDAO.deleteAuth(authToken);
        Assertions.assertNull(authDAO.getAuth(authToken));
    }

    @Test
    @DisplayName("Delete Auth: Fail")
    public void deleteAuthFail() throws Exception {
        String authToken = authDAO.createAuth("joe");
        authDAO.deleteAuth("joe");
        Assertions.assertNotNull(authDAO.getAuth(authToken));
    }

    @Test
    @DisplayName("Clear Auth: Success")
    public void clearAuthSuccess() throws Exception {
        String authToken = authDAO.createAuth("joe");
        authDAO.clearAuth();
        Assertions.assertNull(authDAO.getAuth(authToken));
    }


}
