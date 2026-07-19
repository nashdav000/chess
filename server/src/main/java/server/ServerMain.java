package server;

import chess.*;
import dataaccess.*;
import service.GameService;
import service.UserService;

public class ServerMain {
    public static void main(String[] args) {
        GameService gameService = new GameService(new MemoryGameDAO(), new MemoryAuthDAO());
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        var server = new Server(gameService, userService).run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
