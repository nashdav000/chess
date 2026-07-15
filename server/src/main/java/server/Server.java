package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.game.classes.*;
import service.user.classes.*;

import java.util.Map;

public class Server {

    // private final boolean memory = true;
    // once database is implemented update all the calls to be tertiary determinants on which to use

    private final Javalin javalin;
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // User Endpoints
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);

        // Game Endpoints
        javalin.post("/game", this::create);
        javalin.get("/game", this::list);
        javalin.put("/game", this::join);

        // Clear Endpoint
        javalin.delete("/db", this::clear);

        javalin.exception(DataAccessException.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);

        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws DataAccessException{
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);

        String json = new Gson().toJson(result);
        ctx.json(json);
    }

    private void login(Context ctx) throws DataAccessException{
        LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult result = userService.login(request);

        String json = new Gson().toJson(result);
        ctx.json(json);
    }

    private void logout(Context ctx) throws DataAccessException{
        LogoutRequest request = new LogoutRequest(ctx.header("authorization"));

        if (request.authToken() == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized, "Error: Unauthorized");
        }

        userService.logout(request);

        ctx.json("");
    }

    private void create(Context ctx) throws DataAccessException {
        // Temp class to read in json object
        record info(String gameName){}
        info body = new Gson().fromJson(ctx.body(), info.class);
        String head = ctx.header("authorization");
        CreateRequest request = new CreateRequest(head, body.gameName);

        // Validate data
        if (request.authToken() == null || request.gameName() == null){
            throw new DataAccessException(DataAccessException.Type.BadRequest,
                    "Error: Bad Request");
        }

        CreateResult result = gameService.createGame(request);
        String json = new Gson().toJson(result);
        ctx.json(json);
    }

    private void list(Context ctx) throws DataAccessException {
        ListRequest request = new ListRequest(ctx.header("authorization"));
        ListResult result = gameService.listGames(request);
        String json = new Gson().toJson(result);
        ctx.json(json);
    }

    private void join(Context ctx) throws DataAccessException {
        record info(String playerColor, String gameID){}
        info body = new Gson().fromJson(ctx.body(), info.class);
        JoinRequest request = new JoinRequest(ctx.header("authorization"), body.playerColor, body.gameID);
        JoinResult result = gameService.joinGame(request);
        String json = new Gson().toJson(result);
        ctx.json(json);
    }

    private void clear(Context ctx){
       userService.clearUsers();
       userService.clearAuths();
       gameService.clearGames();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx){
        String json = new Gson().toJson(Map.of("message", ex.getMessage()));
        ctx.status(ex.toHTTPResponse());
        ctx.json(json);
    }
}
