package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/clear", this::clear);

        javalin.post("/user", this::register);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);

        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx){

    }

    private void register(Context ctx){
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        UserService userService = new UserService();
        RegisterResult result = userService.register(request);

        return result
    }
}
