package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import service.ClearRequest;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService(new MemoryUserDAO());

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
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

    private void clear(Context ctx){
       ClearRequest request = new ClearRequest();
       userService.clearUsers(request);
    }

    private void exceptionHandler(DataAccessException ex, Context ctx){
        ctx.status(400);
    }
}
