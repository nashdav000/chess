package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        switch(piece.getPieceType()){
            case PieceType.BISHOP:
                return diagonalMove(myPosition.getRow(), myPosition.getColumn(), true, board);
            case PieceType.ROOK:
                return horizontalMove(myPosition.getRow(), myPosition.getColumn(), true, board);
            case PieceType.QUEEN:
                Collection<ChessMove> list = diagonalMove(myPosition.getRow(), myPosition.getColumn(), true, board);
                list.addAll(horizontalMove(myPosition.getRow(), myPosition.getColumn(), true, board));
                return list;
            case PieceType.KNIGHT:
                return knightMove(myPosition.getRow(), myPosition.getColumn(), board);
            case PieceType.KING:
                Collection<ChessMove> lst = diagonalMove(myPosition.getRow(), myPosition.getColumn(), false, board);
                lst.addAll(horizontalMove(myPosition.getRow(), myPosition.getColumn(), false, board));
                return lst;
            case PieceType.PAWN:
                return pawnMove(myPosition.getRow(), myPosition.getColumn(), board);
        }
        return List.of();
    }

    public Collection<ChessMove> diagonalMove(int startRow, int startCol, boolean multi, ChessBoard board)
    {
        int[][] pattern = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        return movesGenerator(startRow, startCol, pattern, multi, board);
    }

    public Collection<ChessMove> horizontalMove(int startRow, int startCol, boolean multi, ChessBoard board)
    {
        int[][] pattern = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        return movesGenerator(startRow, startCol, pattern, multi, board);
    }

    public Collection<ChessMove> knightMove(int startRow, int startCol, ChessBoard board){
        int[][] pattern = {{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2}};
        return movesGenerator(startRow, startCol, pattern, false, board);
    }

    public Collection<ChessMove> pawnMove(int startRow, int startCol, ChessBoard board){
        Collection<ChessMove> moves = new ArrayList<>();

        if (pieceColor == ChessGame.TeamColor.BLACK)
        {
            // Forward Movement
            if (board.getPiece(new ChessPosition(startRow - 1, startCol)) == null){
                moves.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(startRow - 1, startCol), null));
            }

            // Double Movement if at starting position
            if (startRow == 7 && board.getPiece(new ChessPosition(startRow - 2, startCol)) == null){
                moves.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(startRow - 2, startCol), null));
            }

            // Capture a piece

            // Promotion
        }

        return moves;
    }

    private Collection<ChessMove> movesGenerator(int startRow, int startCol, int[][] pattern, boolean multiMoves, ChessBoard board){
        Collection<ChessMove> moves = new ArrayList<>();

        for (int i = 0; i < pattern.length; i++){
            int endRow = startRow + pattern[i][0];
            int endCol = startCol + pattern[i][1];

            while(endRow < 9 && endRow > 0 && endCol < 9 && endCol > 0){

                // Check if the space it is looking at is occupied
                ChessPiece other = board.getPiece(new ChessPosition(endRow, endCol));
                if (other != null)
                {
                    // If it is, is it on our color, or the other teams color?
                    if (pieceColor == other.pieceColor){
                        break;
                    }

                    moves.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol),null));
                    break;
                }

                moves.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol),null));

                if (!multiMoves) {break;}
                endRow += pattern[i][0];
                endCol += pattern[i][1];
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
