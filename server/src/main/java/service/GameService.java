package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import service.GameClasses.*;

import java.util.List;


public class GameService {

    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public GameService(GameDAO gameAccess, AuthDAO authAccess) {
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    public CreateResult createGame(CreateRequest request) throws DataAccessException{
        // Make sure the user is authorized
        authorize(request.authToken());

        return new CreateResult(gameAccess.createGame(request.gameName()));
    }

    public void clearGames(){
        gameAccess.clearGames();
    }

//    public GetResult getGame(GetRequest request){
//
//    }
//
    public ListResult listGames(ListRequest request) throws DataAccessException{
        authorize(request.authToken());

        return new ListResult(gameAccess.listGames());
    }
//
//    public UpdateResult updateGame(UpdateRequest request){
//
//    }

    private void authorize(String authToken) throws DataAccessException{
        if (authAccess.getAuth(authToken) == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized,
                                            "Error: Unauthorized");
        }
    }
}
