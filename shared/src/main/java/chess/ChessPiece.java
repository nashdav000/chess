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
                return diagonalMove(myPosition.getRow(), myPosition.getColumn());
            case PieceType.ROOK:
                return horizontalMove(myPosition.getRow(), myPosition.getColumn());
        }
        return List.of();
    }

    public List diagonalMove(int startRow, int startCol)
    {
        int[][] diags = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};

        List<ChessMove> moves = new ArrayList<>();

        for (int i = 0; i < 4; i++){
            int endRow = startRow + diags[i][0];
            int endCol = startCol + diags[i][1];

            // Check if in the move is in bounds of the board
            while(endRow < 9 && endRow > 0 && endCol < 9 && endCol > 0){

                // Check if the space it is looking at is occupied
                /*if (ChessBoard.getPiece(new ChessPosition(endRow, endCol)){
                /*
                    // If it is, is it on our color, or the other teams color?
                    if (pieceColor == other.pieceColor){
                        break;
                    }
                    *
                    * moves.add(new ChessMove(startRow, startCol), new ChessPosition(endRow, endCol),null));
                    * break;
                * }
                *
                */

                moves.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol),null));
                endRow += diags[i][0];
                endCol += diags[i][1];
            }
        }

        return moves;
    }

    public List horizontalMove(int startRow, int startCol)
    {
        int[][] horzs = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};

        List<ChessMove> moves = new ArrayList<>();

        for (int i = 0; i < 4; i++){
            int endRow = startRow + horzs[i][0];
            int endCol = startCol + horzs[i][1];

            while(endRow < 9 && endRow > 0 && endCol < 9 && endCol > 0){
                moves.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol),null));
                endRow += horzs[i][0];
                endCol += horzs[i][1];
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
