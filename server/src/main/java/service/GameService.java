package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import service.GameClasses.*;


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

    public JoinResult joinGame(JoinRequest request) throws DataAccessException {
        authorize(request.authToken());

        // Validate Data
        if (request.gameID() == null | request.playerColor() == null){
            throw new DataAccessException(DataAccessException.Type.BadRequest,
                                            "Error: One or more fields were left blank");
        }

        if (!request.playerColor().equals("BLACK") && !request.playerColor().equals("WHITE")){
            throw new DataAccessException(DataAccessException.Type.BadRequest,
                                        "Error: One or more fields were left blank");
        }

        // Get the game
        GameData game = gameAccess.getGame(request.gameID());
        GameData updatedGame = game;

//        System.out.println("authToken");
//        System.out.println(request.authToken());
//        System.out.println("username");
//        System.out.println(authAccess.getAuth(request.authToken()));

        switch (request.playerColor()){
            case "BLACK":
                // Validate color
                if (game.blackUsername() != null){
                    throw new DataAccessException(DataAccessException.Type.AlreadyTaken,
                            "Error: already taken");
                }

                // Add player
                updatedGame = new GameData(game.gameID(), game.whiteUsername(),
                                authAccess.getAuth(request.authToken()), game.gameName(), game.chessGame());

                break;

            case "WHITE":

                // Validate color
                if (game.whiteUsername() != null){
                    throw new DataAccessException(DataAccessException.Type.AlreadyTaken,
                            "Error: already taken");
                }

                // Add player
                updatedGame = new GameData(game.gameID(),
                        authAccess.getAuth(request.authToken()),
                        game.blackUsername(), game.gameName(), game.chessGame());
                break;
        }

        gameAccess.setGame(request.gameID(), updatedGame);
        return new JoinResult();
    }

    public ListResult listGames(ListRequest request) throws DataAccessException{
        authorize(request.authToken());

        return new ListResult(gameAccess.listGames());
    }

    private void authorize(String authToken) throws DataAccessException{
        if (authAccess.getAuth(authToken) == null){
            throw new DataAccessException(DataAccessException.Type.Unauthorized,
                                            "Error: Unauthorized");
        }
    }
}
