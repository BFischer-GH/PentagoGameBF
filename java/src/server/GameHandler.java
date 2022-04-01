package server;

/**
 * GameHandler is started when 2 players with queue active are matched
 * Communication goes through the ClientHandlers
 *
 */

import game.Board;
import game.Mark;

public class GameHandler {
    //-- Variables
    private ClientHandler[] players;
    private Board board;

    public GameHandler(ClientHandler ch1, ClientHandler ch2) {
    //-- Constructor
        ch1.setGameHandler(this);
        ch2.setGameHandler(this);
        this.board = new Board();

        System.out.println(" Current game situation: \n");
        System.out.println(board+"\n");

        players = new ClientHandler[2];
        this.players[0] = ch1;
        this.players[1] = ch2;

    }

    //-- Move Methods

    /**
     * Checks if provided move is valid
     * TODO update for new input 6x6 grid
     *
     * @param input move provided by player
     * @return true if valid move
     */
    public boolean checkMove(String input) {
        int move = Integer.parseInt(input);
        return board.isField(move) && board.isEmptyField(move);
    }

    /**
     * If valid move is provided place it on the board if game over isn't valid
     * See board for rules
     *
     * @param input
     * @param mark
     */
    public void doMove(String input, Mark mark) {
        int move = Integer.parseInt(input);

        board.setField(move, mark);
        System.out.println(" Current game situation: \n\n" + board.toString() + "\n");

        if (this.board.gameOver()) {
           // This will be the final move


            if (this.board.isFull()) {
                this.players[0].sendCommand("GAMEOVER~DRAW");
                this.players[1].sendCommand("GAMEOVER~DRAW");
                //Send last move
                this.players[0].sendCommand("MOVE~" + move);
                this.players[1].sendCommand("MOVE~" + move);
            } else if (this.board.hasWinner()) {
                if (board.isWinner(game.Mark.XX)) {
                    this.players[0].sendCommand("GAMEOVER~VICTORY~" + players[0].getPlayerName());
                    this.players[1].sendCommand("GAMEOVER~VICTORY~" + players[0].getPlayerName());
                    //Send last move
                    this.players[0].sendCommand("MOVE~" + move);
                    this.players[1].sendCommand("MOVE~" + move);
                } else {
                    this.players[0].sendCommand("GAMEOVER~VICTORY~" + players[1].getPlayerName());
                    this.players[1].sendCommand("GAMEOVER~VICTORY~" + players[1].getPlayerName());
                    // Send last move
                    this.players[0].sendCommand("MOVE~" + move);
                    this.players[1].sendCommand("MOVE~" + move);
                }
            }
        } else {
            //Update move
            this.players[0].sendCommand("MOVE~" + move);
            this.players[1].sendCommand("MOVE~" + move);
        }

    }


}
