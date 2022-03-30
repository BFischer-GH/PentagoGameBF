package player;

/**
 * Class for Handling a human player who is connected
 *
 */

import game.Board;
import game.Mark;
import server.ClientHandler;
import utils.TextIO;

public class HumanPlayer extends Player{

    private ClientHandler ch;

//-- Constructor
    public HumanPlayer(ClientHandler ch, Mark mark) {
        super(ch, mark);
    }

    @Override
    public int determineMove(Board board, ClientHandler ch) {
        //ToDo ask the input from the Client

        String prompt = "Hello " + getPlayerName() + " (" + getMark().toString() + ")"
                + ", what is your move? ";
        int choiceInput;
        do{
            ch.sendCommand(prompt);
            choiceInput = TextIO.getInt();
            // If input is valid then here the loop will break otherwise line will be send
            ch.sendCommand("Error: Invalid move please re-enter!");
        } while(!board.isField(choiceInput)&&board.isEmptyField(choiceInput));
        System.out.println(prompt);


        //Get the response back
        return choiceInput;
    }

}
