package server;

/**
 * Server that allows Players to connect for a game, must be executed to create a new server (thus main)
 *
 * @author bart.fischer
 */

//-- Imports

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import utils.TextIO;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class GameServer implements Runnable {
    //-- Variables
    private int port;
    private InetAddress address;
    private ServerSocket serverSocket;
    private Thread thread;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    protected List<String> clientNames = new ArrayList<>();
    private BufferedReader inServer;
    private PrintWriter outServer;

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
            System.out.println("Give some details to get it all started:");
            System.out.println(" ");

            try {
                // Get Address input
                System.out.println("Please enter server address (Type \"localhost\" when playing on this device):");
//                String addressInput = TextIO.getlnString();
//                address = InetAddress.getByName(addressInput);
                address = InetAddress.getByName("localhost");
                System.out.println("\t Localhost selected for now");
            } catch (UnknownHostException e) {
                System.out.println("This is not a valid address");
               continue;
            }

            // Get Port input
            try {
                System.out.println("Please enter a valid port number ");
//                int portInput = TextIO.getlnInt();
//                port = portInput; //TODO reset this!
                port = 8080;
                System.out.println("\t For now port is set to " + port);
            } catch (Exception e) {
                System.out.println("This is an invalid port number");
                continue;
            }
            GameServer gameServer = new GameServer(address,port);
            gameServer.start();
            run = true;

        } while (!checkPort(port));

        while(run){
            System.out.println("To quit server enter: QUIT");
            String input = TextIO.getlnString();
            if(toUpperCase(input).equals("QUIT")) {
                System.out.println("This should en the server"); //TODO correct implementation of QUIT command in server
                run = false;

                }
            else {
                System.out.println("ERROR: Incorrect input");
            }
        }
    }

    //-- Server basic methods
    public void start() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("New server started at port: " + this.serverSocket.getLocalPort());
            //Start this server on a new thread
            this.thread = new Thread(this);
            this.thread.start();
        } catch (IOException e) {
            //System.out.println("ERROR: No connection at port " +this.serverSocket.getLocalPort());
            e.printStackTrace();
        }
    }

    public void stop(){
        try{
            serverSocket.close();
            this.thread.join();

        } catch (InterruptedException e) {
            System.out.println("InterruptedEx");
        } catch (IOException e){
            System.out.println("IOException");
        }
    }

    @Override
    public void run() {
        boolean run = true;
        while(run) {
            try{
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
    public void addClient(ClientHandler addCH) { clients.add(addCH); }

    /**
     * Removes Clienthandler from client array
     *
     * @param removeCH
     */
    public void removeClient(ClientHandler removeCH) {clients.remove(removeCH); }
//--Checkers

    /** See if the giving port is within the correct range
     *
     * @param port integer given
     * @return true is port is correct, false if incorrect port is given
     */
    public static boolean checkPort(int port){
        if(port >= 1025 && port <= 65535 ){
            return true;
        }
        System.out.println("Error: No correct port detected! Re-enter values stupid!");
        System.out.println(" ");
        return false;


    }

    /**
     * When a Client is QUEUEd for a game this method checks if there's anybody else QUEUEd
     * If so it start a new game and the Clients are removed from the list, CH remains
     * @param client1
     */
    public void checkQueues(ClientHandler client1) {

        try {
            for (ClientHandler client2 : clients) { //Run through all clients
                if (!client2.getPlayerName().equals(client1.getPlayerName()) && client2.getQueue() && client1.getQueue()) {
                    startNewGame(client1, client2);
                    removeClient(client1);
                    removeClient(client2);
                }
            }
        } catch ( ConcurrentModificationException ignored) {
            System.out.println("ConcurrentMod ignored"); //TODO check for details
        }
    }

    /**
     * Initiates the NEWGAME from Server to 2 players who where both QUEUED for active game
     * @param client1
     * @param client2
     */
    private void startNewGame(ClientHandler client1, ClientHandler client2) {
        System.out.println("Game will start with player 1:"+ client1.getPlayerName() +" and player 2: "+ client2.getPlayerName());
        String userName1 = client1.getPlayerName();
        String userName2 = client2.getPlayerName();

        System.out.println("This message is an indicator that a match game can start");

        String startNewGame = "NEWGAME~" + userName1 + "~" + userName2;
        client1.sendCommand(startNewGame);
        client2.sendCommand(startNewGame);

        GameHandler game = new GameHandler(client1, client2);


    }

//-- Get.Set
    /**
     * Get the Arraylist LIST of all waiting clients/players
     *
     * @return
     */
    public ArrayList<ClientHandler> getList() {
        return clients;
    }

    /**
     *
     * @return Return the given port
     */
    public int getPort() { return this.serverSocket.getLocalPort();}



}
