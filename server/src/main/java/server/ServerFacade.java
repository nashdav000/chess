package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;

public class ServerFacade {

    private final String serverUrl;


    public ServerFacade(String url){
        this.serverUrl = url;
    }

    public UserData register(UserData user) throws DataAccessException {
        var request = buildRequest("POST", "/user", user);




        return null;
    }

    public AuthData login(UserData user) throws DataAccessException {



        return null;
    }

    public void logout() throws DataAccessException{

    }

    public GameData createGame() throws DataAccessException{


        return null;
    }

    public void listGames() throws DataAccessException{

    }

    public void joinGame() throws DataAccessException{

    }

    public void clear() throws DataAccessException {
        var request = buildRequest("DELETE", "/db", null);
    }

    private HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null){
            request.setHeader("Content-Type", "application/json");
        }

        return request.build();
    }

    private BodyPublisher makeRequestBody (Object body){
        if (body != null){
            return BodyPublishers.ofString(new Gson().toJson(body));
        }
        else{
            return BodyPublishers.noBody();
        }
    }
}
