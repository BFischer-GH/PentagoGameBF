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
    public void testHasDiagonal() {
        //Check downwards
        board.setField(00, Mark.XX);
        board.setField(07, Mark.XX);
        board.setField(14, Mark.XX);
        board.setField(28, Mark.XX);
        assertFalse(board.hasDiagonal(Mark.XX));
        board.setField(35, Mark.XX);
        System.out.println("\t\t This should show 5 XX diagonally but not consecutive downwards: \n\n" + board.toString() + "\n");
        assertFalse(board.hasDiagonal(Mark.XX));
        assertFalse(board.hasDiagonal(Mark.OO));
        board.setField(21, Mark.XX);
        assertTrue(board.hasDiagonal(Mark.XX));
        System.out.println("\t\t This should show 6 XX diagonally  consecutive downwards: \n\n" + board.toString() + "\n");
        board.reset();
        // Check upwards
        board.setField(30, Mark.OO);
        board.setField(25, Mark.OO);
        board.setField(20, Mark.OO);
        board.setField(10, Mark.OO);
        board.setField(05, Mark.OO);
        assertFalse(board.hasDiagonal(Mark.OO));
        System.out.println("\t\t This should show 5 non-consecutive diagonally upwards for OO: \n\n" + board.toString() + "\n");
        board.setField(15,Mark.OO);
        assertTrue(board.hasDiagonal(Mark.OO));
        assertFalse(board.hasDiagonal(Mark.XX));
        System.out.println("\t\t This should show 6 marks consecutive diagonally upwards for OO: \n\n" + board.toString() + "\n");

        System.out.println("If 4 boards show then the Diagonal Test was succesfull!");
    }
}
