package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.UserClasses.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
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
        LogoutRequest request = new LogoutRequest(ctx.header("Authorization"));

        if (request.authToken() == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized, "Error: Unauthorized");
        }

        LogoutResult result = userService.logout(request);

        String json = new Gson().toJson(result);
        ctx.json(json);
    }

    private void clear(Context ctx){
       ClearRequest request = new ClearRequest();
       userService.clearUsers(request);
    }

    private void exceptionHandler(DataAccessException ex, Context ctx){
        String json = new Gson().toJson(Map.of("message", ex.getMessage()));
        ctx.status(ex.toHTTPResponse());
        ctx.json(json);
    }
}
