package test;

import game.Board;
import game.Mark;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.ClientHandler;
import server.GameHandler;
import org.junit.jupiter.api.BeforeEach;
import server.GameServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.net.InetAddress.*;
import static org.junit.Assert.assertTrue;

public class GameHandlerTest {
    private GameServer server;
    private GameHandler gameHandler;
    private Board board;
    private ClientHandler[] players = new ClientHandler[2];

    @BeforeEach
    public void setUp() throws IOException {

        InetAddress address = getByName("localhost");
        this.server = new GameServer(address, 9000);


    }
    @AfterEach
    public void close() {
        this.server.stop();
    }

    @Test
    public void testMove() throws IOException {
        this.server.start();
        Socket socket = new Socket(InetAddress.getByName("localhost"), 9000);

        ClientHandler ch1 = new ClientHandler(socket, server);
        ClientHandler ch2 = new ClientHandler(socket, server);

        gameHandler = new GameHandler(ch1, ch2);
        //System.out.println(board.toString());
        assertTrue(this.gameHandler.checkMove("5", "5"));

        this.gameHandler.doMove("5","5",Mark.XX);

        //this.gameHandler.doMove(String.valueOf(6),Mark.OO);

    }
}
