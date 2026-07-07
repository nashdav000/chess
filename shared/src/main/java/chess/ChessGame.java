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

    private TeamColor _turn;
    private ChessBoard _board;

    public ChessGame() {
        _turn = TeamColor.WHITE;
        _board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return _turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        _turn = team;
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

        ChessPiece piece = _board.getPiece(startPosition);
        if (piece == null)
        {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(_board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>(moves);

        // Make the move and see if the king is in check
        for (ChessMove move : moves){

            // Initialize variables
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece startPiece = _board.getPiece(startPos);
            ChessPiece endPiece = _board.getPiece(endPos);

            // Move the piece, checking whether it should be promoted or not
            if (move.getPromotionPiece() != null){
                _board.addPiece(endPos, new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece()));
            }
            else{
                _board.addPiece(endPos, startPiece);
            }

            // Remove the piece from where it started
            _board.addPiece(startPos, null);

            if (isInCheck(piece.getTeamColor())){
                validMoves.remove(move);
            }

            // Undo the fake move
            _board.addPiece(startPos, startPiece);
            _board.addPiece(endPos, endPiece);
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
        ChessPiece startPiece = _board.getPiece(startPos);

        // Check that the right player is moving
        if (startPiece.getTeamColor() != _turn){
            throw new InvalidMoveException("Not your turn");
        }

        // Move the piece, checking whether it should be promoted or not
        if (move.getPromotionPiece() != null){
            _board.addPiece(endPos, new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece()));
        }
        else{
            _board.addPiece(endPos, startPiece);
        }

        // Remove the piece from where it started
        _board.addPiece(startPos, null);

        _turn = _turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
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
                ChessPiece piece = _board.getPiece(new ChessPosition(x, y));

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    king = new ChessPosition(x, y);
                    break kingsearch;
                }
            }
        }

        for (int x = 1; x<9; x++){
            for (int y = 1; y<9; y++){
                ChessPiece piece = _board.getPiece(new ChessPosition(x, y));

                // If there's a piece of the opposite color
                if (piece != null && piece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = piece.pieceMoves(_board, new ChessPosition(x, y));

                    // Check if it can capture the king
                    for (ChessMove move : moves){
                        if (move.getEndPosition().equals(king)){
                            return true;
                        }
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
        boolean canMove = false;

        for (int x = 1; x < 9; x++){
            for (int y = 1; y < 9; y++){
                ChessPiece piece = _board.getPiece(new ChessPosition(x, y));

                if (piece == null || piece.getTeamColor() != teamColor){continue;}

//                Collection<ChessMove> moves = piece.pieceMoves(_board, new ChessPosition(x, y));
//                moves = validMoves(new ChessPosition(x, y));
                if (!validMoves(new ChessPosition(x, y)).isEmpty()){
                    canMove = true;
                }
            }
        }

        return !canMove;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        _board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return _board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return _turn == chessGame._turn && Objects.equals(_board, chessGame._board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_turn, _board);
    }
}
