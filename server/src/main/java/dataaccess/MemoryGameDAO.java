package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private HashMap<String, GameData> gameList = new HashMap<>();

    public String createGame(String gameName){
        ChessGame chessGame = new ChessGame();

        // Assign it an untaken ID
        int ID = 1;
        while (gameList.containsKey(String.valueOf(ID))){
            ID++;
        }

        // Store all the information of the new game
        GameData newGame = new GameData(String.valueOf(ID), null, null, gameName, chessGame);

        // Store it in memory
        gameList.put(String.valueOf(ID), newGame);

        // Return the ID
        return String.valueOf(ID);
    }

    public Collection<GameData> listGames(){
        return gameList.values();
    }

    public GameData getGame(String gameID){
        return gameList.get(gameID);
    }

    public void setGame(String gameID, GameData game){
        gameList.put(gameID, game);
    }

    public void clearGames(){
        gameList = new HashMap<>();
    }
}
