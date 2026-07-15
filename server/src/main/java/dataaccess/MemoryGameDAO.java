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
        int id = 1;
        while (gameList.containsKey(String.valueOf(id))){
            id++;
        }

        // Store all the information of the new game
        GameData newGame = new GameData(String.valueOf(id), null, null, gameName, chessGame);

        // Store it in memory
        gameList.put(String.valueOf(id), newGame);

        // Return the ID
        return String.valueOf(id);
    }

    public Collection<GameData> listGames(){
        return gameList.values();
    }

    public GameData getGame(String gameid){
        return gameList.get(gameid);
    }

    public void setGame(String gameid, GameData game){
        gameList.put(gameid, game);
    }

    public void clearGames(){
        gameList = new HashMap<>();
    }
}
