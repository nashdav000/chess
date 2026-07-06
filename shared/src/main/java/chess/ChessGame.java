package chess;

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

        // Make the move and see if the king is in check
        for (ChessMove move : moves){
            ChessBoard test = _board;

            // Initialize variables
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece startPiece = test.getPiece(startPos);

            // Move the piece, checking whether it should be promoted or not
            if (move.getPromotionPiece() != null){
                test.addPiece(endPos, new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece()));
            }
            else{
                test.addPiece(endPos, startPiece);
            }

            // Remove the piece from where it started
            test.addPiece(startPos, null);

            if (isInCheck(_turn)){
                moves.remove(move);
            }
        }

        return moves;
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
        for (int x = 1; x<9; x++){
            for (int y = 1; y<9; y++){
                ChessPiece piece = _board.getPiece(new ChessPosition(x, y));

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    king = new ChessPosition(x, y);
                }
            }
        }

        // Check the straight moves
        int[][] pattern = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] move : pattern)
        {
            int row = king.getRow() + move[0];
            int col = king.getColumn() + move[1];

            while (0 < row && row < 9 && 0 < col && col < 9){
                ChessPiece other = _board.getPiece(new ChessPosition(row, col));
                if (other != null){
                    if (other.getTeamColor() != teamColor && (other.getPieceType() == ChessPiece.PieceType.QUEEN || other.getPieceType() == ChessPiece.PieceType.ROOK)){
                        return true;
                    }
                    break;
                }
                row += move[0];
                col += move[1];
            }
        }

        // Check the diagonals
        pattern = new int[][] {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        for (int[] move : pattern)
        {
            int row = king.getRow() + move[0];
            int col = king.getColumn() + move[1];

            while (0 < row && row < 9 && 0 < col && col < 9){
                ChessPiece other = _board.getPiece(new ChessPosition(row, col));
                if (other != null){
                    if (other.getTeamColor() != teamColor && (other.getPieceType() == ChessPiece.PieceType.QUEEN || other.getPieceType() == ChessPiece.PieceType.BISHOP))
                    {
                        return true;
                    }
                    break;
                }
                row += move[0];
                col += move[1];
            }
        }

        // Check for knights
        pattern = new int[][] {{2, 1}, {-2, 1}, {2, -1}, {-2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};
        for (int[] move : pattern)
        {
            int row = king.getRow() + move[0];
            int col = king.getColumn() + move[1];

            if (0 < row && row < 9 && 0 < col && col < 9){
                ChessPiece other = _board.getPiece(new ChessPosition(row, col));
                if (other != null){
                    if (other.getTeamColor() != teamColor && other.getPieceType() == ChessPiece.PieceType.KNIGHT){
                        return true;
                    }
                }
            }
        }

        // Check for pawns
        // Check right
        int color = teamColor == TeamColor.BLACK ? -1 : 1;
        if (king.getColumn() < 8 && king.getRow() != 4 + (4 * color)){
            ChessPiece other = _board.getPiece(new ChessPosition(king.getRow() + color, king.getColumn() + 1));
            if (other != null && other.getPieceType() == ChessPiece.PieceType.PAWN && other.getTeamColor() != teamColor){
                return true;
            }
        }
        // Check left
        if (king.getColumn() < 8 && king.getRow() != 4 + (4 * color)){
            ChessPiece other = _board.getPiece(new ChessPosition(king.getRow() + color, king.getColumn() + 1));
            if (other != null && other.getPieceType() == ChessPiece.PieceType.PAWN && other.getTeamColor() != teamColor){
                return true;
            }
        }

        // Check for the king (because this is after the move)
        pattern = new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, -0}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        for (int[] move : pattern)
        {
            int row = king.getRow() + move[0];
            int col = king.getColumn() + move[1];

            if (0 < row && row < 9 && 0 < col && col < 9){
                ChessPiece other = _board.getPiece(new ChessPosition(row, col));
                if (other != null){
                    if (other.getTeamColor() != teamColor && other.getPieceType() == ChessPiece.PieceType.KING){
                        return true;
                    }
                }
            }
        }

        // If all the checks are good, then we are good
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

                if (piece == null || piece.getTeamColor() != teamColor){break;}

                if (!piece.pieceMoves(_board, new ChessPosition(x, y)).isEmpty()){
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
