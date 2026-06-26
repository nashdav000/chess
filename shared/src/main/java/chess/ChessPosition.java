package chess;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return _row == that._row && _col == that._col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_row, _col);
    }
}
