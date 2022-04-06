package test;

import game.Board;
import game.Mark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardQuadTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testBoardPrint() {
        //Let's just print an empty board
        System.out.println("\t\t This should show a nicely lined out PentaGoBoard: \n\n" + board.toString() + "\n");
        System.out.println("Yup that looks like an empty board to me :-D!");
    }

    @Disabled
    @Test
    //Note old  test for handling Int correctly, needs String output from setQuad
    public void testQuadSelect() {
//        String answerCCW = "A quadrant should go COUNTER clockwise";
//        String answerCW = "A quadrant should go clockwise";
//        assertEquals(board.setQuad(2), answerCCW);
//        assertEquals(board.setQuad(0), answerCCW);
//        assertEquals(board.setQuad(5), answerCW);
//        assertEquals(board.setQuad(7), answerCW);
//        System.out.println("TestQuad successful");
    }

    @Test
    //Work on getting full board rotation up and running
    public void testRotateQuadClockwise() {
        //Placing some markers to play with
        board.setField(1, Mark.XX);
        board.setField(6, Mark.OO);
        board.setField(13, Mark.XX);
        board.setField(27, Mark.XX);
        board.setField(29, Mark.XX);
        board.setField(22, Mark.OO);
        board.setField(4, Mark.OO);
        board.setField(9, Mark.XX);
        board.setField(16, Mark.OO);
        board.setField(24, Mark.OO);
        board.setField(26, Mark.OO);
        board.setField(31, Mark.XX);

        System.out.println("\t\t 1. PentaGoBoard with various markers: \n\n" + board.toString() + "\n");
        //quadClockWise needs 2 values (row/columnSection) which can be 0 or 3 //Todo change into booleans
        board.quadClockWise(03, 00);
        System.out.println("\t 2. PentaGoBoard that should be have a clockwise rotated quad: \n" +
                " \t\t See input in quadClockWise which quad \n" + board.toString() + "\n");

        board.setField(34, Mark.OO);
        System.out.println("\t\t 3. PentaGoBoard with additional marker (#34 OO): \n\n" + board.toString() + "\n");
        board.quadClockWise(3, 3);
        System.out.println("\t\t 4. PentaGoBoard again with quad rotation clockwise, see remark @ nr2: \n\n" + board.toString() + "\n");

         }
    @Test
    public void testRotateQuadCOUNTERCW(){
        //Placing some markers to play with
        board.setField(1, Mark.XX);
        board.setField(6, Mark.OO);
        board.setField(13, Mark.XX);
        board.setField(27, Mark.XX);
        board.setField(29, Mark.XX);
        board.setField(22, Mark.OO);
        board.setField(4, Mark.OO);
        board.setField(9, Mark.XX);
        board.setField(16, Mark.OO);
        board.setField(24, Mark.OO);
        board.setField(26, Mark.OO);
        board.setField(31, Mark.XX);
        System.out.println("\t\t 1. PentaGoBoard with various markers: \n\n" + board.toString() + "\n");
        //quadClockWise needs 2 values (row/columnSection) which can be 0 or 3 //Todo change into booleans
        board.quadCOUNTERClockWise(00, 03);
        System.out.println("\t 2. PentaGoBoard that should be have a COUNTERclockwise rotated quad: \n" +
                " \t\t See input in quadClockWise which quad \n" + board.toString() + "\n");

        board.setField(28, Mark.OO);
        System.out.println("\t\t 3. PentaGoBoard with additional marker (#28 OO): \n\n" + board.toString() + "\n");
        board.quadCOUNTERClockWise(3, 3);
        System.out.println("\t\t 4. PentaGoBoard again with quad rotation COUNTER clockwise, see remark @ nr2: \n\n" + board.toString() + "\n");

    }
}
