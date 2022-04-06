package client;

import game.Board;
import game.Mark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class will handel the input from the Client UI and communication with the server according to the protocol
 *
 * @author bart.fischer
 */

public class Client implements Runnable {

    //--Variable:
    private final ClientTUI myClientTUI;
    private Socket socket = null;
    private BufferedReader inClient;
    private PrintWriter outClient;
    private Thread threadClient;
    private String playerName;
    private Mark mark;
    private Board board;
    private boolean activeClient = true;
    private boolean playerTurn;
    protected boolean queueStatus = false;
    protected boolean gameStatus;

    //--Constructor
    public Client(ClientTUI clientTUI) {
        this.myClientTUI = clientTUI;
        board = new Board();
    }

    //-- Basic Methods
    public boolean connect(InetAddress address, int port) {
        try {
            this.socket = new Socket(address, port);
            System.out.println("Making connection to server and starting new thread.\n");

            this.inClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outClient = new PrintWriter(socket.getOutputStream(), true);
            this.threadClient = new Thread(this);
            this.threadClient.start();
            return true;
        } catch (IOException e) {
            System.out.println("Thread connect: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            this.activeClient = false;
            this.socket.close();
            this.threadClient.join();
            this.myClientTUI.tuiThread.join();
            System.out.println("This socket is closed\n");
        } catch (IOException | InterruptedException e) {
            System.out.println("Thread Close: " + e.getMessage());
        }
    }

    //--RUN
    // All the messages from the server get handles here
    // This is done on a different thread then the connection thus in clientTUI => (new Thread(client)).start();
    @Override
    public void run() {
        try {
            String line;
            while (this.activeClient && null != (line = this.inClient.readLine())) {
                //System.out.println("Server: " + line);//Input checker
                String[] commandSplit = line.split("~");
                switch (commandSplit[0]) {
                    case "LOGIN" -> myClientTUI.playerQueue();
                    case "ALREADYLOGGEDIN" -> {
                        System.out.println("Name already taken, please use a different name then " + this.playerName);
                        myClientTUI.playerLogin();
                    }
                    case "LIST" -> this.showList(commandSplit);
                    case "NEWGAME" -> newGameStart(commandSplit);
                    case "ERROR" -> {
                        System.out.println("That move and/or quadrant rotation wasn't valid!\n");
                        myClientTUI.askMove();
                    }
                    case "MOVE" -> setMove(commandSplit);
                    case "GAMEOVER" -> gameOverInput(commandSplit);
                    default -> {
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Client Inter Switch:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Client IOEx Switch:" + e.getMessage());
        }
    }

    /**
     * When Client receives a GameOver then the full string is now handled
     *
     * @param commandSplit
     * @throws InterruptedException
     */

    private void gameOverInput(String[] commandSplit) throws InterruptedException {

        System.out.println("Game Over - No more moves possible!");
        gameStatus = false;
        if (commandSplit[1].equals("DRAW")) {
            System.out.println("The game ended in a draw");
            myClientTUI.continuePlay();

        } else if (commandSplit[1].equals("VICTORY")) {
            System.out.println("The player " + commandSplit[2] + " has won!\n");
            myClientTUI.continuePlay();

        }
    }

    /**
     * handles move from server on the board that the player sees
     * Keeps track of who's turn it is.
     *
     * @param commandSplit
     */
    private void setMove(String[] commandSplit) {
        int move = Integer.parseInt(commandSplit[1]);
        int quad = Integer.parseInt(commandSplit[2]);
        if (this.playerTurn) {
            this.board.setField(move, this.mark);
            board.setQuad(quad);
        } else {
            this.board.setField(move, this.mark.other());
            board.setQuad(quad);
        }
        playerTurn = !playerTurn;

        System.out.println(" Current game situation: \n\n" + board.toString() + "\n");
        if (playerTurn) {
            myClientTUI.askMove();
        }
    }

    /**
     * Sets player name which is send to Server,
     *
     * @param playerName
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Send List command to server to see which players are connected
     */

    public void getList() throws InterruptedException {
        this.sendMessage("LIST~" + this.playerName);
        System.out.println("Requesting List of waiting players from server\n");
        myClientTUI.playerQueue();
    }

    /**
     * Returns Client specific mark
     */
    public Mark getMark() {
        return this.mark;
    }

    /**
     * Send given Message to SERVER
     *
     * @param message
     */
    public void sendMessage(String message) {
        try {
            this.outClient.println(message);
            //System.out.println("Client: " + message);
        } catch (Exception e) {
            System.out.println("SendMessage CATCH:" + e.getMessage());
        }
    }

    /**
     * Show all players that are currently connected to the server
     *
     * @param command
     */
    public void showList(String[] command) {
        System.out.println("The following player(s) are connected to server:");
        for (int i = 1; i < command.length; i++) {
            System.out.println("Player " + i + " : \t" + command[i]);
        }
    }

    /**
     * After connection is established (client.connect (address, port)==true) the player provides
     * a name to LOGIN which is sends to SERVER
     *
     * @param playerName
     */
    public void loginPlayer(String playerName)  {
        sendMessage("LOGIN~" + playerName);
    }

    /**
     * Following QUIT command connection is terminated
     */
    public void sendQuit() {
        try {

            System.out.println("Ending connection and game");
            this.sendMessage("QUIT~" + playerName);
            this.close();
            activeClient = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Following QUEUE input send to server that players wants to play
     */
    public void sendQueue() throws InterruptedException {
        this.queueStatus = !queueStatus;
        this.sendMessage("QUEUE");
        if (this.queueStatus) {
            System.out.println("Player: " + playerName + " in queue for Newgame, waiting for other queued player\n");
            this.gameStatus = true;
            //Probleem is dat deze altijd input verwacht
            this.myClientTUI.playerQueue();

        } else {
            System.out.println("Player not in queue for newgame.\n");
            //this.gameStatus = false;
            this.myClientTUI.playerQueue();
        }

    }

    /**
     * When a NEWGAME command is received
     *
     * @param commandSplit
     */
    public void newGameStart(String[] commandSplit) {
        this.gameStatus = true; //get you out of the QUEUE loop

        System.out.println("Starting new game with player 1: " + commandSplit[1] + " (XX) and player 2: " + commandSplit[2] + " (OO).\n");

        //Make sure the board is empty from possible previous game.
        board.reset();

        System.out.println(" Current game situation: \n\n" + board.toString() + "\n");

        if (commandSplit[1].equals(this.playerName)) {
            this.mark = Mark.XX;
            System.out.println("You are player 1: " + commandSplit[1] + " (" + this.mark + ") \n" );
            System.out.println("Please hit enter to continue.\n");
            this.playerTurn = true;
            myClientTUI.askMove();
        } else {
            this.mark = Mark.OO;
            System.out.println("Please hit enter to continue.\n");
            System.out.println("You are player 2: " + this.playerName + " (" + this.mark + ")  please wait for move from other player\n");
            this.playerTurn = false;
        }

    }

}

