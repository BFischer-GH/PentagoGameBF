package game;

import player.Player;
import server.ClientHandler;

public class Game {

    /**
     * The playing board
     */
    private Board board;

    /**
     * The 2 players of the game (human or AI)
     */
    public Player[] players;
    public ClientHandler[] clients;

    /**
     * Tracking the current player
     */
    private int current;

    //--Constructor

    /**
     * Creates a new Game Object with Players and ClientHandlers for communication
     * @param player1 is the first player
     * @param player2 is the second player
     */

    public Game(Player player1, Player player2, ClientHandler client1, ClientHandler client2) {
        board = new Board();
        players = new Player[2];
        players[0] = player1;
        players[1] = player2;
        clients = new ClientHandler[2];
        clients[0] = client1;
        clients[1] = client2;
        current = 0;

    }
    //--Commands

    /**
     * Starts a new PentagoGame
     */
    public void start() {
            reset();
            play();
//Todo Add continue option
    }

    /**
     * Resets the board for a new game
     */
    private void reset() {
        current = 0;
        board.reset();
    }

    /** Prints the status of game (should be initially empty)
     *
     */
    private String update(){
        String update = "  Curent game situation: \n\n"+ board.toString() +"\n";
        return update;
    }

    /**
     * Plays the game, starts with showing the empty board
     */
    private void play(){
        this.reset();
        int numberPlayer = 0;
        System.out.println("Test line 1tr55r");
        while (!board.gameOver()) {
            System.out.println(update());
            if (numberPlayer % 2 == 0) {

                board.setField(players[0].determineMove(this.board),players[0].getMark());
            } else {
                board.setField(players[1].determineMove(this.board),players[1].getMark());
            }
            numberPlayer++;
        }
        System.out.println(this.update()); //Game display in the server

        //clients[0].sendCommand(this.update()); //TODO niet het hele board doorsturen enkel de move
        //Send first move request from player 1
        //board.setField(players[0].determineMove(this.board), players[0].getMark());
    }
}
