package server;

/**
 * Server that allows Players to connect for a game, must be executed to create a new server (thus main)
 *
 * @author bart.fischer
 */

//-- Imports

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import game.Mark;
import utils.TextIO;


import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class GameServer implements Runnable {
    //-- Variables
    private int port;
    private InetAddress address;
    private ServerSocket serverSocket;
    private Thread thread;
    protected List<ClientHandler> clients = new ArrayList<>();

    //-- Constructor
    public GameServer(InetAddress address, int port) {
        this.port = port;
        this.address = address;
    }

    //-- MAIN
    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = null;
        int port = 0;

        boolean run = false;
        do {
            System.out.println("Welcome, so you want to start a Pentago Server?");
            System.out.println("Give some details to get it all started:\n");

            try {
                // Get Address input
                System.out.println("Please enter server address (Type \"localhost\" when playing on this device):\n");
                String addressInput = TextIO.getlnString();
                address = InetAddress.getByName(addressInput);
                //address = InetAddress.getByName("localhost");
                //System.out.println("\t Localhost selected for now");
            } catch (UnknownHostException e) {
                System.out.println("This is not a valid address");
                continue;
            }

            // Get Port input
            try {
                System.out.println("Please enter a valid port number ");
                int portInput = TextIO.getlnInt();
                port = portInput;
//                port = 8080;
//                System.out.println("\t For now port is set to " + port);
            } catch (Exception e) {
                System.out.println("This is an invalid port number");
                continue;
            }
            GameServer gameServer = new GameServer(address, port);
            gameServer.start();
            run = true;

        } while (!checkPort(port));

        while (run) {
            System.out.println("To quit server enter: QUIT");
            String input = TextIO.getlnString();
            if (toUpperCase(input).equals("QUIT")) {
                System.out.println("Please perform manual server reset\n");
            } else {
                System.out.println("ERROR: Incorrect input");
            }
        }
    }

    //-- Server basic methods
    public void start() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("New server started at port: " + this.serverSocket.getLocalPort() + "\n");
            //Start this server on a new thread
            this.thread = new Thread(this);
            this.thread.start();
        } catch (IOException e) {
            //System.out.println("ERROR: No connection at port " +this.serverSocket.getLocalPort());
            e.printStackTrace();
        }
    }

    /**
     * Stop is used in Testing the server
     */
    public void stop() {
        try {
            serverSocket.close();
            this.thread.join();

        } catch (InterruptedException e) {
            System.out.println("InterruptedEx");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    @Override
    public void run() {
        boolean run = true;
        while (run) {
            try {
                Socket sock = serverSocket.accept();
                ClientHandler ch = new ClientHandler(sock, this);
                new Thread(ch).start();
            } catch (IOException e) {
                System.out.println("Server terminated at port " + this.serverSocket.getLocalPort());
                //e.printStackTrace();
                run = false;
            }
        }
    }

    //-- Add/Remove clients from clients arraylist

    /**
     * Add a Clienthandler to client array
     *
     * @param addCH
     */
    public void addClient(ClientHandler addCH) {
        clients.add(addCH);
    }

    /**
     * Removes Clienthandler from client array and Name from namelist
     *
     * @param removeCH
     */
    public void removeClient(ClientHandler removeCH) {
        clients.remove(removeCH);
    }
//--Checkers

    /**
     * See if the giving port is within the correct range
     *
     * @param port integer given
     * @return true is port is correct, false if incorrect port is given
     */
    public static boolean checkPort(int port) {
        if (port >= 1025 && port <= 65535) {
            return true;
        }
        System.out.println("Error: No correct port detected! Re-enter values stupid!");
        System.out.println(" ");
        return false;


    }

    /**
     * When a Client is QUEUEd for a game this method checks if there's anybody else QUEUEd
     * If so it start a new game and the Clients are removed from the list, CH remains
     *
     * @param client1
     */
    public void checkQueues(ClientHandler client1) {
        for (ClientHandler client2 : clients) { //Run through all clients
            if (!client2.getPlayerName().equals(client1.getPlayerName()) && client2.getQueue() && client1.getQueue()) {
                startNewGame(client1, client2);
                //To make sure players cannot be found whilst playing a game
                client1.flipQueueState();
                client2.flipQueueState();
            }
        }
    }

    /**
     * Initiates the NEWGAME from Server to 2 players who where both QUEUED for active game
     *
     * @param ch1
     * @param ch2
     */
    private void startNewGame(ClientHandler ch1, ClientHandler ch2) {
        String userName1 = ch1.getPlayerName();
        String userName2 = ch2.getPlayerName();

        String startNewGame = "NEWGAME~" + userName1 + "~" + userName2;
        ch1.sendCommand(startNewGame);
        ch2.sendCommand(startNewGame);

        ch1.setMark(Mark.XX);
        ch2.setMark(Mark.OO);
        System.out.println("Game will start with player 1: " +
                ch1.getPlayerName() + " (" + Mark.XX + ") " +
                " and player 2: " + ch2.getPlayerName() + " (" + Mark.OO + ") \n");

        new GameHandler(ch1, ch2);
    }

    /**
     * Get the Arraylist LIST of all waiting clients/players
     *
     * @return
     */
    public List<ClientHandler> getList() {
        return clients;
    }

}
