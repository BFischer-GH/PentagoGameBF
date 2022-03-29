package server;

import game.Game;
import game.Mark;
import player.HumanPlayer;

public class GameHandler {

    private ClientHandler[] players;
    private String player1Name;
    private String player2Name;

    public GameHandler(ClientHandler client1, ClientHandler client2) {
        /**
         * The player details taken from the ClientHandlers
         */
        players = new ClientHandler[2];
        this.players[0] = client1;
        this.players[1] = client2;

        //Todo handle computer player here
        this.player1Name = players[0].getPlayerName();
        this.player2Name = players[1].getPlayerName();

        System.out.println("GameHandler start new game on thread with player 1: "+ player1Name +" and player 2: " + player2Name +"\n"  );

        HumanPlayer player1 = new HumanPlayer(players[0], Mark.XX);
        HumanPlayer player2 = new HumanPlayer(players[1], Mark.OO);

        Game game = new Game(player1, player2, client1,client2);
        game.start();

    }
}
