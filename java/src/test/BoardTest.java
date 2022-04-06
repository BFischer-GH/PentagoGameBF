package test;

import game.Board;
import game.Mark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    /**
     * Easy check to see if PentagoBoard layout is correct
     */
    @Test
    public void testBoardPrint() {
        System.out.println("\t\t This should show a nicely lined out PentaGoBoard: \n\n" + board.toString() + "\n");
    }

    /**
     * Check if board now accepts field input > then old small input
     */
    @Test
    public void testIsFieldIndex() {
        assertFalse(board.isField(-1));
        assertTrue(board.isField(0));
        assertTrue(board.isField(30));
        assertFalse(board.isField(45));
        assertTrue(board.isField(Board.DIM * Board.DIM - 1));
        assertFalse(board.isField(Board.DIM * Board.DIM));
    }

    @Test
    public void testSetAndGetFieldIndex() {
        board.setField(24, Mark.XX);
        assertEquals(Mark.XX, board.getField(24));
        assertEquals(Mark.EMPTY, board.getField(30));
    }

    @Test
    public void testSetup() {
        for (int i = 0; i < Board.DIM * Board.DIM; i++) {
            assertEquals(Mark.EMPTY, board.getField(i));
        }
    }

    @Test
    public void testReset() {
        board.setField(0, Mark.XX);
        board.setField(1, Mark.XX);
        board.setField(Board.DIM * Board.DIM - 1, Mark.OO);
        board.reset();
        assertEquals(Mark.EMPTY, board.getField(0));
        assertEquals(Mark.EMPTY, board.getField(1));
        assertEquals(Mark.EMPTY, board.getField(Board.DIM * Board.DIM - 1));
    }

    @Test
    public void testIsFull() {
        for (int i = 0; i < Board.DIM * Board.DIM - 1; i++) {
            board.setField(i, Mark.XX);
        }
        assertFalse(board.isFull());

        board.setField(Board.DIM * Board.DIM - 1, Mark.XX);
        assertTrue(board.isFull());
    }

    @Test
    public void testHasRow() {
        board.setField(0, Mark.XX);
        board.setField(1, Mark.XX);
        board.setField(4, Mark.XX);
        board.setField(3, Mark.XX);
        board.setField(5, Mark.XX);
        System.out.println("\t\t This should show 5 in a row for XX but not valid: \n\n" + board.toString() + "\n");
        assertFalse(board.hasRow(Mark.XX));
        System.out.println(" Indeed above board has not a winning 5 in a row");


        board.setField(2, Mark.XX);
        System.out.println("\t\t This should show 6 in a row for XX: \n\n" + board.toString() + "\n");
        assertTrue(board.hasRow(Mark.XX));
        assertFalse(board.hasRow(Mark.OO));

        board.reset();

        board.setField(19, Mark.OO);
        board.setField(20, Mark.OO);
        board.setField(22, Mark.OO);
        board.setField(23, Mark.OO);
        assertFalse(board.hasRow(Mark.OO));
        assertFalse(board.hasRow(Mark.XX));
        board.setField(21, Mark.OO);
        assertTrue(board.hasRow(Mark.OO));
        assertFalse(board.hasRow(Mark.XX));
        System.out.println("\t\t This should show 5 in a row for OO: \n\n" + board.toString() + "\n");
    }

    @Test
    public void testHasColumn() {
        board.setField(0, Mark.XX);
        board.setField(6, Mark.XX);
        board.setField(18, Mark.XX);
        board.setField(24, Mark.XX);
        assertFalse(board.hasRow(Mark.XX));

        board.setField(30, Mark.XX);
        System.out.println("\t\t This should not show 5 consecutive in a column for XX: \n\n" + board.toString() + "\n");
        assertFalse(board.hasColumn(Mark.XX));
        board.setField(12, Mark.XX);
        System.out.println("\t\t This should show 6 in a column for XX: \n\n" + board.toString() + "\n");
        assertTrue(board.hasColumn(Mark.XX));
        assertFalse(board.hasColumn(Mark.OO));

        board.reset();

        board.setField(9, Mark.OO);
        board.setField(15, Mark.OO);
        board.setField(27, Mark.OO);
        board.setField(21, Mark.OO);
        assertFalse(board.hasColumn(Mark.OO));
        assertFalse(board.hasColumn(Mark.XX));
        board.setField(33, Mark.OO);
        System.out.println("\t\t This should show 5 in a column for OO: \n\n" + board.toString() + "\n");
        assertTrue(board.hasColumn(Mark.OO));
        assertFalse(board.hasColumn(Mark.XX));

    }

    @Test
    public void testHasDiagonaLeft() {
        //Check downwards - Center Diagonal line
        //board.setField(00, Mark.XX);
        board.setField(7, Mark.XX);
        board.setField(14, Mark.XX);
        board.setField(28, Mark.XX);
        assertFalse(board.hasDiagonal(Mark.XX));
        board.setField(21, Mark.XX);
        board.setField(35, Mark.XX);
        //System.out.println("\t\t This should show 5 XX diagonally but not consecutive downwards: \n\n" + board.toString() + "\n");
        assertFalse(board.hasDiagonal(Mark.OO));
        assertTrue(board.hasDiagonal(Mark.XX));

        System.out.println("\t\t This should show 5 XX diagonally  consecutive downwards: \n\n" + board.toString() + "\n");
        board.reset();

        // Check above the diagonal line
        board.setField(1, Mark.OO);
        board.setField(8, Mark.OO);
        board.setField(15, Mark.OO);
        board.setField(22, Mark.OO);
        assertFalse(board.hasDiagonal(Mark.OO));
        board.setField(29, Mark.OO);
        System.out.println("\t\t This should show 5 consecutive diagonally downwards for OO: \n\n" + board.toString() + "\n");
        assertTrue(board.hasDiagonalAbove(Mark.OO));

        board.reset();
        // This will check if there's 5 in a row below the
        board.setField(6, Mark.OO);
        board.setField(13, Mark.OO);
        board.setField(20, Mark.OO);
        board.setField(27, Mark.OO);
        assertFalse(board.hasDiagonal(Mark.OO));
        board.setField(34, Mark.OO);
        System.out.println("\t\t This should show 5 consecutive diagonally downwards for OO: \n\n" + board.toString() + "\n");
        assertTrue(board.hasDiagonalBelow(Mark.OO));

    }

    /**
     *
     */
    @Test
    public void testHasDiagonalRight() {
        // Check upwards -Center Diagonal line
        //board.setField(05, Mark.XX);
        board.setField(30, Mark.XX);
        board.setField(25, Mark.XX);
        board.setField(20, Mark.XX);
        assertFalse(board.hasDiagonal(Mark.XX));
        board.setField(10, Mark.XX);
        board.setField(15, Mark.XX);
        System.out.println("\t\t This should show 5 XX diagonally but consecutive upwards: \n\n" + board.toString() + "\n");
        assertFalse(board.hasDiagonal(Mark.OO));
        assertTrue(board.hasDiagonal(Mark.XX));

        board.reset();
        // Check upwards- Above Diagonal Line

        board.setField(24, Mark.XX);
        board.setField(19, Mark.XX);
        board.setField(14, Mark.XX);
        board.setField(04, Mark.XX);
        assertFalse(board.hasDiagonalAbove(Mark.XX));
        board.setField(9, Mark.XX);
        assertTrue(board.hasDiagonalAbove(Mark.XX));
        System.out.println("\t\t This should show 5 consecutive diagonally upwards for XX: \n\n" + board.toString() + "\n");

        board.reset();
        board.setField(31, Mark.XX);
        board.setField(26, Mark.XX);
        board.setField(11, Mark.XX);
        board.setField(16, Mark.XX);
        assertFalse(board.hasDiagonalBelow(Mark.XX));
        board.setField(21, Mark.XX);
        assertTrue(board.hasDiagonalBelow(Mark.XX));
        System.out.println("\t\t This should show 5 consecutive diagonally upwards for XX: \n\n" + board.toString() + "\n");


    }

    @Test
    public void testHasTwoWinnersDraw(){
        board.setField(0, Mark.XX);
        board.setField(7, Mark.XX);
        board.setField(14, Mark.XX);
        board.setField(28, Mark.XX);
        board.setField(23,Mark.XX);

        board.setField(5, Mark.OO);
        board.setField(11, Mark.OO);
        board.setField(17, Mark.OO);
        board.setField(35, Mark.OO);

        System.out.println("\t\t 1. Setup, this should show a board with 2player" +
                ": \n\n" + board.toString() + "\n");
        board.setField(34,Mark.OO);
        board.setQuad(6);
        assertTrue(board.hasDrawMove());
        System.out.println("\t\t 2. This should show a board with 2player DRAW" +
                ": \n\n" + board.toString() + "\n");
    }
}