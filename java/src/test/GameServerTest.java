package test;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import server.GameServer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameServerTest {
    private GameServer server;

    @BeforeEach
        //Creates a test server before all tess on localhost and port 8080
    void setUp() throws IOException {

        server = new GameServer(InetAddress.getLocalHost(), 8080);
    }

    @Test
    void loginOn1() throws IOException {
        // Start the server
        server.start();
        Socket socket = new Socket(InetAddress.getLocalHost(), 8080);
        String s;

        //  Create input and output with BufferedReader and PrintReader
        try (BufferedReader inRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter outPrint = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            //Write player1 name (Alfa) to server, if succesfull s=LOGIN is expected
            outPrint.println("LOGIN~Alfa");
            s = inRead.readLine();
            assertEquals("LOGIN", s);

            //Add player2 name (Beta);
            outPrint.println("LOGIN~Beta");
            s = inRead.readLine();
            assertEquals("LOGIN", s);

            //Add player3 name which already present (Alfa);
            outPrint.println("LOGIN~Beta");
            s = inRead.readLine();
            assertEquals("ALREADYLOGGEDIN", s);
            outPrint.println("LOGIN~Gamma");
            s = inRead.readLine();
            assertEquals("LOGIN", s);


            //TODO nagaan waarom het Finally aspect voor problemen zorgt
        } finally {
                // Close the connection, stop the server.
                socket.close();
                server.stop();
        }

    }

    @Test
    void listTest() throws IOException {
        server.start();
        Socket socket = new Socket(InetAddress.getLocalHost(), 8080);
        String s;

        //  Create input and output with BufferedReader and PrintReader
        try (BufferedReader inRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter outPrint = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            //Add a few player to the list
            outPrint.println("LOGIN~Rincewind");
            outPrint.println("LOGIN~TwoFlower");
            outPrint.println("LOGIN~Baggage");
            //Now player Alfa request a list of joined player
            outPrint.println("LIST");
            s = inRead.readLine();


        } finally {
        // Close the connection, stop the server.
        socket.close();
        server.stop();
    }

    }
}
