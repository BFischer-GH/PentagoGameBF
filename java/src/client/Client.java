package client;

import game.Board;
import utils.TextIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class will handel the input from the Client UI and communication with the server according to the protocol
 *
 *
 * @author bart.fischer
 */

public class Client implements Runnable{

    //--Variable:
    private final ClientTUI myClientTUI;
    private Socket socket = null;
    private BufferedReader inClient;
    private PrintWriter outClient;
    private Thread threadClient;
    private boolean run = true;
    private String playerName;

    protected boolean loginSuccess;
    protected boolean queueStatus;
    protected boolean gameStatus;
    public boolean loginMessageReceived;
    private Board board;

    //--Constructor
    public Client(ClientTUI clientTUI) {
        this.myClientTUI = clientTUI;
        board = new Board();
    }

    //private Player player;

    //-- Basic Methods
    public boolean connect(InetAddress address, int port){
        try{

            this.socket = new Socket(address, port);
            System.out.println("Making connection to server and starting new thread. (CLIENT)");

            this.inClient= new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outClient = new PrintWriter(socket.getOutputStream(), true);
            this.threadClient = new Thread(this);
            this.threadClient.start();
            return true;
        } catch (IOException e) {
            System.out.println("Error: Connection problem from Client side");
            return false;
        }
    }

    public void close(){
        try{
            this.socket.close();
            this.threadClient.join();
            this.run = false;
            System.out.println("This socket is closed");
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: A problem occurred with closing the socket.");

        }
    }


 //--RUN
    // all the messages from the server get handles here
    @Override
    public void run() {
        while (this.run){
            try{
                String line;
                while ((line = this.inClient.readLine()) != null){
                    //System.out.println("Server: "+line);
                    String[] commandSplit = line.split("~");
                    switch (commandSplit[0]){
                        case "LOGIN":

                            System.out.println("Succesfull login as " + playerName);
                            this.loginSuccess = true;
                            myClientTUI.playerQueue();

                            // doorgeven dat mainthread nu doorkan
                            break;
                        case "ALREADYLOGGEDIN":
                            System.out.println("Name already taken, please use a different name then " + this.playerName);
                            myClientTUI.playerLogin();
                            break;
                        case "LIST":
                           this.showList(commandSplit);
                           break;
                        case "NEWGAME":
                            newGameStart(commandSplit);
                            break;
                        case "MOVE":
                            setMove();
                            break;
                        default:
                            break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Catch from Client RUN");;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //-- Set/Get
    public void setPlayername(String playerName) {this.playerName = playerName;}

    public void getList(){
        this.sendMessage("LIST~"+this.playerName);
        System.out.println("Requesting List of waiting players from server\n");
    }

     //--New Methods

    /**
     * Send given Message to SERVER
     * @param message
     */
    public void sendMessage(String message){
        try{
            this.outClient.println(message);
        } catch (Exception e){
            System.out.println("Error with message from Client to Server");
        }
    }

    /**
     *
     * @param command
     */
    public void showList(String[] command) {
        System.out.println("The following player(s) are connected to server:");
        for (int i = 1; i < command.length ; i++) {
            System.out.println("Player "+ i + " : \t" + command[i]);
        }
    }

    /**
     * After connection is established (client.connect (address, port)==true) the player provides
     * a name to LOGIN which is send to SERVER
     * @param playerName
     */
    public void loginPlayer(String playerName) throws InterruptedException {
               sendMessage("LOGIN~" + playerName);
//               Thread.sleep(300);

    }

    /**
     * Following QUIT command connection is terminated //TODO properly close TUI
     */
    public void sendQuit() {
        System.out.println("Ending connection and game");
        this.sendMessage("QUIT~"+playerName);
        this.close();
    }

    /**
     * Following QUEUE input send to server that players wants to play
     */
    public void sendQueue() {
        this.queueStatus = !queueStatus;
        System.out.println("Sending QUEUE command to server with status " + this.queueStatus);
        this.sendMessage("QUEUE");
        if(this.queueStatus){
            System.out.println("Player in queue for newgame, waiting for second player\n" +
                    "If you want to go out of queue enter: \t \"QUEUE\" \n");
            } else {
            System.out.println("Player not in queue for newgame.");
        }
    }

    /**
     *
     * @param commandSplit
     */
    private void newGameStart(String[] commandSplit) {
        System.out.println("Starting new game with player 1: " + commandSplit[1] + " and player 2: " + commandSplit[2]+"\n");
        this.gameStatus = true; //get you out of the QUEUE loop

        System.out.println("  Curent game situation: \n\n"+ board.toString() +"\n");

}

        // todo board opstarten
        // todo if playername = commandSplit[1] => you're player 1 otherwise player2
        // todo player2 = commandSplit[2]
        // todo move()


    public void setMove() {
            System.out.println(" It's now your turn, please enter a move value");
            String messageMove = TextIO.getlnString();
            sendMessage("Move~"+messageMove);
            //this.playerTurn = !playerTurn; // Todo this happen when Move is received back from Server
        }

        //todo if it is my turn ask for move
        //todo if move is given send move to server
        //todo if not my turn wait for MOVE from server
    }

