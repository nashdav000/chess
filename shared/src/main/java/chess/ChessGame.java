package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard gameBoard;

    public ChessGame() {
        turn = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = gameBoard.getPiece(startPosition);
        if (piece == null)
        {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>(moves);

        // Make the move and see if the king is in check
        for (ChessMove move : moves){

            // Initialize variables
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece startPiece = gameBoard.getPiece(startPos);
            ChessPiece endPiece = gameBoard.getPiece(endPos);

            // Move the piece, checking whether it should be promoted or not
            if (move.getPromotionPiece() != null){
                gameBoard.addPiece(endPos, new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece()));
            }
            else{
                gameBoard.addPiece(endPos, startPiece);
            }

            // Remove the piece from where it started
            gameBoard.addPiece(startPos, null);

            if (isInCheck(piece.getTeamColor())){
                validMoves.remove(move);
            }

            // Undo the fake move
            gameBoard.addPiece(startPos, startPiece);
            gameBoard.addPiece(endPos, endPiece);
        }

        return validMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> valid = validMoves(move.getStartPosition());

        // Check that the move is valid
        if (valid == null || !valid.contains(move)) {
            throw new InvalidMoveException("Invalid Move");
        }

        // Initialize variables
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece startPiece = gameBoard.getPiece(startPos);

        // Check that the right player is moving
        if (startPiece.getTeamColor() != turn){
            throw new InvalidMoveException("Not your turn");
        }

        // Move the piece, checking whether it should be promoted or not
        if (move.getPromotionPiece() != null){
            gameBoard.addPiece(endPos, new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece()));
        }
        else{
// Edit this to make a new piece and then I can use memory hacks for castling
// ie testing if the king == the piece, because making a new one deletes memory
            gameBoard.addPiece(endPos, startPiece);
        }

        // Remove the piece from where it started
        gameBoard.addPiece(startPos, null);

        turn = turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = new ChessPosition(1, 1);

        // Find the king
        kingsearch:
        for (int x = 1; x<9; x++){
            for (int y = 1; y<9; y++){
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(x, y));

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    king = new ChessPosition(x, y);
                    break kingsearch;
                }
            }
        }

        for (int x = 1; x<9; x++){
            for (int y = 1; y<9; y++){
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(x, y));

                Collection<ChessMove> moves;
                // If there's a piece of the opposite color
                if (piece != null && piece.getTeamColor() != teamColor) {
                    moves = piece.pieceMoves(gameBoard, new ChessPosition(x, y));
                }
                else{continue;}

                // Check if it can capture the king
                for (ChessMove move : moves){
                    if (move.getEndPosition().equals(king)){
                        return true;
                    }
                }
            }
        }

        // If we reach this, it means there are no moves that can capture the king
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean stuck = canMove(teamColor);

        if (isInCheck(teamColor)){
            return !stuck;
        }
        else{
            return false; // Stalemate
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean stuck = canMove(teamColor);

        if (!isInCheck(teamColor)){
            return !stuck;
        }
        else{
            return false; // Checkmate
        }
    }

    private boolean canMove(TeamColor teamColor){
        boolean stuck = false;

        for (int x = 1; x < 9; x++){
            for (int y = 1; y < 9; y++){
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(x, y));

                // Checkmate is defined as there are no valid moves. If we can move, there is not a checkmate
                if (piece == null || piece.getTeamColor() != teamColor){continue;}

                if (!validMoves(new ChessPosition(x, y)).isEmpty()){
                    stuck = true;
                }
            }
        }

        return stuck;
    }
    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, gameBoard);
    }
}
