package game;

/**
 * Create basic board (DIMxDIM = 3x3) for the Pentago game
 * //TODO the 4 quadrants and rotation will be added later
 *
 * @author bart.fischer
 */

public class Board {

    private static final String DELIM = "      ";
    public static final int DIM = 6;
    private static final String[] NUMBERING = {
            " 00 | 01 | 02 || 03 | 04 | 05 ",
            "----+----+----||----+----+----",
            " 06 | 07 | 08 || 09 | 10 | 11 ",
            "----+----+----||----+----+----",
            " 12 | 13 | 14 || 15 | 16 | 17 ",
            "==============================",
            " 18 | 19 | 20 || 21 | 22 | 23 ",
            "----+----+----||----+----+----",
            " 24 | 25 | 26 || 27 | 28 | 29 ",
            "----+----+----||----+----+----",
            " 30 | 31 | 32 || 33 | 34 | 35 "
    };
    private static final String LINE = NUMBERING[1];
    private static final String LINEBIG = NUMBERING[5];

    public Mark[] fields;
    public Mark[] quartFields;

    public Board() {
        fields = new Mark[DIM * DIM]; //Create the fields on the board now a 36 in total
        for (int i = 0; i < DIM * DIM; i++) { // for all fields...
            fields[i] = Mark.EMPTY; // set the Mark to EMPTY
        }
    }

    //--Methods placing a MARK

    /**
     * Calculates the index in the linear array of fields from a (row, col)
     * pair.
     *
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
     *
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

    public void setField(int row, int col, Mark m) {
        int index = index(row, col);
        fields[index] = m;
    }

    /** //TODO verde uitwerken van toepassing in spel
     * @param quad
     */
    public String setQuad(int quad) {
        if (quad % 2 == 0) {
            return "A quadrant should go COUNTER clockwise";
        } else return ("A quadrant should go clockwise");
    }

    /**
     * Rotate a quadrant clockwise, quadrant is selected on row/column section
     * @param rowSection Needs to be 0 or 3 for upper or lower section
     * @param columnSection Needs to be 0 or 3 for left or right section
     *                      //TODO this works but can probably be much easier/simpler...
     */
    public void quadClockWise(int rowSection, int columnSection) {
        Board quartBoard = new Board(); // Selected quadrant is moved to upper left corner of this placeholder
        Board transBoard = new Board(); // Board to transpose matrix
        Board columSwitchBoard = new Board(); //Column switch to get clockwise rotated quadrant

        int rowStart=rowSection;
        int rowEnd= rowStart+3;
        int colStart =columnSection;
        int colEnd =colStart+3;

        // Set selected quadrant in upperleft corner of an empty board
        for (int row = rowStart; row < rowEnd ; row++) {
            for (int col = colStart; col < colEnd; col++) {
                quartBoard.setField(row-rowStart,col-colStart,getField(row,col));
            }
        }
        // Transpose Quadrant
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                transBoard.setField(column, row, quartBoard.getField(row, column));
            }
        }
        //Swap Columns
        for (int rows = 0; rows < 3; rows++) {
            for (int columns =0; columns < 3; columns++) {
                columSwitchBoard.setField(rows, 3 - columns - 1, transBoard.getField(rows, columns));
            }
        }
        //Set back in board
        for (int row = rowStart; row < rowEnd ; row++) {
            for (int col = colStart; col < colEnd; col++) {
                fields[index(row,col)] =columSwitchBoard.getField(index(row-rowStart,col-colStart));
            }
        }
    }

    /**
     * Rotate a quadrant COUNTERclockwise, quadrant is selected on row/column section
     * @param rowSection Needs to be 0 or 3 for upper or lower section
     * @param columnSection Needs to be 0 or 3 for left or right section
     *
     */
    public void quadCOUNTERClockWise(int rowSection, int columnSection) {
        Board quartBoard = new Board(); // Selected quadrant is moved to upper left corner of this placeholder
        Board transBoard = new Board(); // Board to transpose matrix
        Board columSwitchBoard = new Board(); //Column switch to get clockwise rotated quadrant

        int rowStart=rowSection;
        int rowEnd= rowStart+3;
        int colStart =columnSection;
        int colEnd =colStart+3;

        // Set selected quadrant in upperleft corner of an empty board
        for (int row = rowStart; row < rowEnd ; row++) {
            for (int col = colStart; col < colEnd; col++) {
                quartBoard.setField(row-rowStart,col-colStart,getField(row,col));
            }
        }
        // Transpose Quadrant
        for (int row = 0, kRow = 2; row < 3; row++, kRow--) {
            for (int column = 0, kCol = 2; column < 3; column++, kCol--) {
                transBoard.setField(kCol, kRow, quartBoard.getField(row, column));
            }
        }

        //Swap Columns
        for (int rows = 0; rows < 3; rows++) {
            for (int columns =0; columns < 3; columns++) {
                columSwitchBoard.setField(rows, 3 - columns - 1, transBoard.getField(rows, columns));
            }
        }
        //Set back in board
        for (int row = rowStart; row < rowEnd ; row++) {
            for (int col = colStart; col < colEnd; col++) {
                fields[index(row,col)] =columSwitchBoard.getField(index(row-rowStart,col-colStart));
            }
        }
    }

    /**
     * Creates a deepCopy of the board (for randomAI)
     *
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
        String boardLayout = "";
        for (int rows = 0; rows < DIM; rows++) {
            String row = "";
            for (int colums = 0; colums < DIM; colums++) {
                row = row + " " + getField(rows, colums).toString().substring(0, 2).replace("EM", "  ") + " ";
                if (colums == 2) {
                    row = row + "||";
                } else if (colums < DIM - 1) {
                    row = row + "|";
                }
            }
            boardLayout = boardLayout + row + DELIM + NUMBERING[rows * 2];
            if (rows == 2) {
                boardLayout = boardLayout + "\n" + LINEBIG + DELIM + NUMBERING[5] + "\n";
            } else if (rows < DIM - 1) {
                boardLayout = boardLayout + "\n" + LINE + DELIM + NUMBERING[rows * 2 + 1] + "\n";
            }
        }
        return boardLayout;
    }


    //--Methods for Testing the state of board

    /**
     * Tests if the board is full
     *
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
     *
     * @return true if the student has a winner.
     */
    public boolean hasWinner() {
        return isWinner(Mark.XX) || isWinner(Mark.OO);
    }

    /**
     * Checks if the mark m has won. A mark wins if it controls at
     * least one row, column or diagonal.
     *
     * @param m the mark of interest
     * @return true if the mark has won
     */
    public boolean isWinner(Mark m) {
        return hasRow(m) || hasColumn(m) || hasDiagonal(m);
    }

    /**
     * Checks whether there is a row which is full and only contains the mark m.
     * Has been fixed that it has to be 5 in a ROW consecutive!
     *
     * @param m the Mark of interest
     * @return true if there is a row controlled by m
     */
    public boolean hasRow(Mark m) {
        for (int row = 0; row < DIM; row++) {
            int countRow = 0;
            for (int column = 0; column < DIM; column++) {
                if (!(getField(row, column) == m)) {
                    countRow = 0;
                } else countRow++;
                if (countRow >= 5) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether there is a column which is full and only contains the mark
     * m.
     *
     * @param m the Mark of interest
     * @return true if there is a column controlled by m
     */
    public boolean hasColumn(Mark m) {
        for (int c = 0; c < DIM; c++) {
            int sumOfCol = 0;
            for (int r = 0; r < DIM; r++) {
                if (!(m == getField(r, c))) {
                    sumOfCol = 0;
                } else sumOfCol++;
                if (sumOfCol >= 5) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks whether there is a diagonal which is full and only contains the
     * mark m.
     *
     * @param m the Mark of interest
     * @return true if there is a diagonal controlled by m
     */
    public boolean hasDiagonal(Mark m) {
        int sumDiagonalLeft = 0;
        int sumDiagonalRight = 0;

        for (int r = 0; r < DIM; r++) {
            for (int c = 0; c < DIM; c++) {
                if (r == c && getField(r, c) == m) {
                    sumDiagonalLeft++;
                } else if (r == c && getField(r, c) != m) {
                    sumDiagonalLeft = 0;
                }

                if (r + c == DIM - 1 && getField(r, c) == m) {
                    sumDiagonalRight++;
                } else if (r + c == DIM - 1 && getField(r, c) != m) {
                    sumDiagonalRight = 0;
                }
            }
        }

        return sumDiagonalLeft >= 5 || sumDiagonalRight >= 5;
    }

    /**
     * Tests if the game is over when the board is full or if there's a winner
     *
     * @return true is board is full or if there's a winner
     */

    public boolean gameOver() {
        return isFull() || hasWinner();
    }


}
