package dataaccess;

import chess.ChessGame;
import service.GameClasses.GameInfo;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private final HashMap<String, GameInfo> gameList = new HashMap<>();

    public String createGame(String gameName){
        ChessGame chessGame = new ChessGame();

        // Assign it an untaken ID
        int ID = 1;
        while (gameList.containsKey(String.valueOf(ID))){
            ID++;
        }

        // Store all the information of the new game
        GameInfo newGame = new GameInfo(String.valueOf(ID), null, null, gameName, chessGame);

        // Store it in memory
        gameList.put(String.valueOf(ID), newGame);

        // Return the ID
        return String.valueOf(ID);
    }
}
