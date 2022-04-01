package game;

/** Create basic board (DIMxDIM = 3x3) for the Pentago game
 * //TODO the 4 quadrants and rotation will be added later
 * @author bart.fischer
 */

public class Board {
    //Basic details for BOARD: //TODO adjust when 9x9 board is used
    private static final String DELIM = "     ";
    public static final int DIM = 3;
    private static final String[] NUMBERING = {
            " 0 | 1 | 2 ",
            "---+---+---",
            " 3 | 4 | 5 ",
            "---+---+---",
            " 6 | 7 | 8 "};
    private static final String LINE = NUMBERING[1];
    //This is the line take from the NUMBERING array

    public Mark[] fields;

    //--Constructors
    public Board() {
        fields = new Mark[DIM * DIM]; //Create the fields on the board and..
        for (int i = 0; i < DIM * DIM; i++) { // for all fields...
            fields[i] = Mark.EMPTY; // set the Mark to EMPTY

        }
    }

    //--Methods placing a MARK

    /**
     * Calculates the index in the linear array of fields from a (row, col)
     * pair.
     * @return the index belonging to the (row,col)-field
     */
    public int index(int row, int col) {
         return DIM * row + col;
    }

    /**
     * Returns true if index is a valid index of a field on the board.
     *
     * @return true if 0 <= index < DIM*DIM
     */
    public boolean isField(int index) {
        return index >= 0 && index < DIM * DIM;
    }

    /**
     * Returns the content of the field i.
     *
     * @param i the number of the field (see NUMBERING)
     * @return the mark on the field
     */
    public Mark getField(int i) {
        return fields[i];
    }

    /**
     * Returns the content of the field referred to by the (row,col) pair.
     * @param row the row of the field
     * @param col the column of the field
     * @return the mark on the field
     */
    public Mark getField(int row, int col) {
        int index = index(row, col);
        return fields[index];
    }

    /**
     * Returns true if the field i is empty.
     *
     * @param i the index of the field (see NUMBERING)
     * @return true if the field is empty
     */
    public boolean isEmptyField(int i) {
        return getField(i) == Mark.EMPTY;
    }



    /**
     * Sets the content of field i to the mark m.
     *
     * @param i the field number (see NUMBERING)
     * @param m the mark to be placed
     */
    public void setField(int i, Mark m) {
        fields[i] = m;
    }

    /**
     * Creates a deepCopy of the board (for randomAI)
     * @return
     */
    public Board deepCopy() {
        Board copyBoard = new Board(); //Create the copy variable which is a new Board
        copyBoard.fields = this.fields.clone(); // Clone all fields to the fields of the copyBoard
        return copyBoard; //return the clone if deepCopy is called
    }

    /**
     * Empties all fields of this board (i.e., let them refer to the value
     * Mark.EMPTY).
     */
    public void reset() {
        for (int i = 0; i < DIM * DIM; i++) fields[i] = Mark.EMPTY;
    }

    /**
     * Returns a String representation of this board. In addition to the current
     * situation, the String also shows the numbering of the fields.
     *
     * @return the game situation as String
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < DIM; i++) {
            String row = "";
            for (int j = 0; j < DIM; j++) {
                row = row + " " + getField(i, j).toString().substring(0, 1).replace("E", " ") + " ";
                if (j < DIM - 1) {
                    row = row + "|";
                }
            }
            s = s + row + DELIM + NUMBERING[i * 2];
            if (i < DIM - 1) {
                s = s + "\n" + LINE + DELIM + NUMBERING[i * 2 + 1] + "\n";
            }
        }
        return s;
    }

    //--Methods for Testing the state of board

    /**
     * Tests if the board is full
     * @return true if board is full (no fields = Mark.EMPTY)
     */
    public boolean isFull() {
        for (int i = 0; i < DIM * DIM; i++) {
            if (isEmptyField(i)) {
                return false;
            }
        }
        return true;

    }
    /**
     * Returns true if the game has a winner. This is the case when one of the
     * marks controls at least one row, column or diagonal.
     * @return true if the student has a winner.
     */
    public boolean hasWinner() {
        return isWinner(Mark.XX) || isWinner(Mark.OO);
    }

    /**
     * Checks if the mark m has won. A mark wins if it controls at
     * least one row, column or diagonal.
     * @param m the mark of interest
     * @return true if the mark has won
     */
    public boolean isWinner(Mark m) {
        return hasRow(m) || hasColumn(m) || hasDiagonal(m);
    }
//TODO when going to full board adjust the parameters correctly

    /**
     * Checks whether there is a row which is full and only contains the mark m.
     * @param m the Mark of interest
     * @return true if there is a row controlled by m
     */
    public boolean hasRow(Mark m) {
        for (int j = 0; j < DIM; j++) {
            int countRow = 0;
            for (int i = 0; i < DIM; i++) {
                if (m == getField(j, i)) {
                    countRow++;
                    if (countRow == DIM) {
                        return true;}
                }
            }
        }
        return false;
    }

    /**
     * Checks whether there is a column which is full and only contains the mark
     * m.
     * @param m the Mark of interest
     * @return true if there is a column controlled by m
     */
    public boolean hasColumn(Mark m) {
        for (int c = 0; c < DIM; c++) {
            int sumOfCol = 0;
            for (int r = 0; r < DIM; r++) {
                if (m == getField(r,c)) {
                    sumOfCol++;
                }
            }
            if (sumOfCol == 3) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks whether there is a diagonal which is full and only contains the
     * mark m.
     * @param m the Mark of interest
     * @return true if there is a diagonal controlled by m
     */
    public boolean hasDiagonal(Mark m) {
        int sumDiagonalLeft = 0;
        int sumDiagonalRight = 0;

        for (int r = 0; r < DIM; r++) {
            for (int c = 0; c < DIM; c++) {
                if (r == c && getField(r,c) == m) {
                    sumDiagonalLeft++;
                }
                if (r+c == DIM-1 && getField(r,c) == m) {
                    sumDiagonalRight++;
                }
            }
        }

        return sumDiagonalLeft == DIM || sumDiagonalRight == DIM;
    }
    /**
     * Tests if the game is over when the board is full or if there's a winner
     * @return true is board is full or if there's a winner
     */

    public boolean gameOver(){
        return isFull() || hasWinner();
    }



}
