package server;

import client.Client;
import server.ClientHandler;

public class GameHandler {

    private ClientHandler[] players;
    private String player1Name;
    private String player2Name;



    public GameHandler(ClientHandler client1, ClientHandler client2) {
        //board = new Board();
        players = new ClientHandler[2];
        this.players[0] = client1;
        this.players[1] = client2;

        this.player1Name = players[0].getPlayerName();
        this.player2Name = players[1].getPlayerName();

        players[0].askMove();

        // todo start method askMove()
        // todo start board

    }
}
