package client;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;
    private static final HttpClient client = HttpClient.newHttpClient();

    public ServerFacade(String url){
        this.serverUrl = url;
    }

    public AuthData register(UserData user) throws ServerException {
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws ServerException {
        record loginAttempt(String username, String password){}

        var request = buildRequest("POST", "/session", new loginAttempt(username, password), null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ServerException{
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public String createGame(String name, String authToken) throws ServerException{
        var path = "/game/%s".formatted(name);
        var request = buildRequest("POST", path, null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, String.class);
    }

    public List<GameData> listGames(String authToken) throws ServerException{
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, null);
    }

    public void joinGame(String color, String id, String authToken) throws ServerException{

        var path = "/game/%s".formatted(id);
        var request = buildRequest("PUT", path, color, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws ServerException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }




    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null){
            request.setHeader("Content-Type", "application/json");
        }

        if (authToken != null){
            request.setHeader("Authorization", authToken);
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws ServerException {
        try{
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e){
            throw new ServerException("Error: Bad Request");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ServerException {
        if (response.statusCode() / 100 != 2){
            switch (response.statusCode()) {
                case 400 -> throw new ServerException("Error: Bad Request. One more or fields left blank");
                case 401 -> throw new ServerException("Error: Unauthorized");
                case 403 -> throw new ServerException("Error: Username already taken");
                case 500 -> throw new ServerException("Error: Does Not Exist");
            }
        }

        if (responseClass != null){
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }
}
