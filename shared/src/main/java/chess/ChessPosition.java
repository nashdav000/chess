package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int _row;
    private int _col;

    public ChessPosition(int row, int col)
    {
        this._row = row;
        this._col = col;
    };

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return _row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return _col;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", _row, _col);
    }
}
