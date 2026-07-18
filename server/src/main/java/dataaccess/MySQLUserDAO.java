package dataaccess;

import model.UserData;

public class MySQLUserDAO implements UserDAO {


    public void createUser(UserData user){

    }

    public UserData getUser(String username){

        return null;
    }

    public void clearUsers() {

    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
