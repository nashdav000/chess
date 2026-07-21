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
    public void getAuthSuccess(){

    }

    @Test
    @DisplayName("Get Auth: Fail")
    public void getAuthFail(){

    }

    @Test
    @DisplayName("Delete Auth: Success")
    public void deleteAuthSuccess(){

    }

    @Test
    @DisplayName("Delete Auth: Fail")
    public void deleteAuthFail(){

    }

    @Test
    @DisplayName("Clear Auth: Success")
    public void clearAuthSuccess(){

    }


}
