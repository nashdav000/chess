package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final String serverUrl;
    private static final HttpClient client = HttpClient.newHttpClient();

    public ServerFacade(String url){
        this.serverUrl = url;
    }

    public UserData register(UserData user) throws DataAccessException {
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);



        return null;
    }

    public AuthData login(UserData user) throws DataAccessException {
        var request = buildRequest("POST", "/session", user);
        var response = sendRequest(request);

        return null;
    }

    public void logout() throws DataAccessException{
        var request = buildRequest("DELETE", "/session", null);
        var response = sendRequest(request);
    }

    public GameData createGame(String name) throws DataAccessException{
        var path = "/game/%s".formatted(name);
        var request = buildRequest("POST", path, null);
        var response = sendRequest(request);

        return null;
    }

    public void listGames() throws DataAccessException{
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
    }

    public void joinGame(String color, int id) throws DataAccessException{
        var path = "/game/%d".formatted(id);
        var request = buildRequest("PUT", path, null);
        var response = sendRequest(request);
    }

    public void clear() throws DataAccessException {
        var request = buildRequest("DELETE", "/db", null);
        var response = sendRequest(request);
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try{
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e){
            throw new DataAccessException;
        }
    }
}
