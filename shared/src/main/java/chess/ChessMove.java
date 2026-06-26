package chess;

import java.util.List;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        return String.format("%s%s", startPosition, endPosition);
    }

    public List diagonalMove()
    {
        int[][] diags = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};

        List moves = List.of();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();

        for (int i = 0; i < 4; i++){
            int endRow = startRow + diags[i][0];
            int endCol = startCol + diags[i][1];

            while(endRow < 8 && endRow > 0 && endCol < 8 && endCol > 0){
                moves.add(new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol),null));
                endRow += diags[i][0];
                endCol += diags[i][1];
            }
        }

        return moves;
    }

}
