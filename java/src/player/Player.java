package player;

/** Abstract class for player, to be used for Human and AI player class
 *
 * @author bart.fischer
 *
 */

import game.Board;
import game.Mark;
import server.ClientHandler;

public abstract class Player {
    //--Variables
    private ClientHandler clientHandler; //To be taken from Clienthandler through GameHandler
    private Mark mark;

    //--Constructors
    public Player(ClientHandler clientHandler, Mark mark){
        this.clientHandler = clientHandler;
        this.mark = mark;
    }

    //--Get/Set

    /**
     * Returns the name of the player
     * @return playername
     */
    public String getPlayerName() { return clientHandler.getPlayerName(); }

    /**
     * Returns the mark (XX or OO) of the player
     * @return
     */
    public Mark getMark() { return mark; }


    //-- Moving Methods

    /**
     * Determines the field for the next move
     *  See HumanPlayer for details
     * @param board the current board
     * @return the move provided by the player
     */
    public abstract int determineMove(Board board);

    /**
     * Makes the move on the board
     * @param board the current board
     */
    public void makeMove(Board board){
        int choice = determineMove(board);
                board.setField(choice,getMark());
    }
}
