package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

public class UserDAOTests {

    static UserDAO userDAO;
    static UserData user;

    @BeforeAll
    public static void init() throws Exception {
        userDAO = new MySQLUserDAO();
        user = new UserData("Joe", "password", "test@gmail.com");
    }

    @BeforeEach
    public void clear() throws Exception {
        userDAO.clearUsers();
    }

    @Test
    @DisplayName("Create User: Success")
    public void createUserSuccess() throws Exception {
        userDAO.createUser(user);
        Assertions.assertNotNull(userDAO.getUser(user.username()));
    }

    @Test
    @DisplayName("Create User: Fail")
    public void createUserFail(){
        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData(null, null, null));
        });
    }

    @Test
    @DisplayName("Get User: Success")
    public void getUserSuccess() throws DataAccessException {
        userDAO.createUser(user);
        Assertions.assertNotNull(userDAO.getUser(user.username()));
    }

    @Test
    @DisplayName("Get User: Fail")
    public void getUserFail() throws DataAccessException {
        userDAO.createUser(user);
        Assertions.assertNull(userDAO.getUser("Faker"));
    }

    @Test
    @DisplayName("Clear User: Success")
    public void clearUserSuccess() throws DataAccessException {
        userDAO.createUser(user);
        Assertions.assertNotNull(userDAO.getUser(user.username()));
        userDAO.clearUsers();
        Assertions.assertNull(userDAO.getUser("Joe"));
    }


}
