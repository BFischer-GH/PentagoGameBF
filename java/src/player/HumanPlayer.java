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

    private ClientHandler player;

//-- Constructor
    public HumanPlayer(ClientHandler name, Mark mark) {
        super(name, mark);
        this.player = name;

    }

    @Override
    public int determineMove(Board board) {
        //ToDo ask the input from the Client

        String prompt = "Hello " + getPlayerName() + " (" + getMark().toString() + ")"
                + ", what is your move? ";

                //This message should be send to user
        player.sendCommand(prompt);
        //System.out.println(prompt);

        //This should be the input from the user
        int choice = TextIO.getInt();

        boolean valid = board.isField(choice) && board.isEmptyField(choice);
        while (!valid) {
            System.out.println("ERROR: field " + choice
                    + " is no valid choice.");
            System.out.println(prompt);
            choice = TextIO.getInt();
            valid = board.isField(choice) && board.isEmptyField(choice);
        }
        //Get the response back
        return choice;
    }

}
