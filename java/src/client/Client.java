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
            System.out.println("Error: Connection problem from Client side\n");
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
            System.out.println("Error: A problem occurred with closing the socket.\n");

        }
    }

    //--RUN
    // All the messages from the server get handles here
    // This is done on a different thread then the connection thus in clientTUI => (new Thread(client)).start();
    @Override
    public void run() {
        try {
            String line;
            while ( this.activeClient && null != (line = this.inClient.readLine()) ) {
                //System.out.println("Server: " + line);
                String[] commandSplit = line.split("~");
                switch (commandSplit[0]) {
                    case "LOGIN":
                        myClientTUI.playerQueue();
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
                    case "ERROR":
                        myClientTUI.askMove();
                        break;
                    case "MOVE":
                        setMove(commandSplit);
                        break;
                    case "GAMEOVER":
                        gameOverInput(commandSplit);
                        break;
                    default:
                        break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            System.out.println("The player " + commandSplit[2] + " has a Victory\n");
            myClientTUI.continuePlay();

        }
    }

    /**
     * handles move from server on the board that the player sees
     * @param commandSplit
     */
    private void setMove(String[] commandSplit) {
        int move = Integer.parseInt(commandSplit[1]);
        if (this.playerTurn) {
            this.board.setField(move, this.mark);
        } else {
            this.board.setField(move, this.mark.other());
        }
        playerTurn = !playerTurn;

        System.out.println(" Current game situation: \n\n" + board.toString() + "\n");
        if (playerTurn) {
            myClientTUI.askMove();
        }
    }

    /**
     * Sets player name which is send to Server,
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
        System.out.println("Requesting List of waiting players from server\n")  ;
        myClientTUI.playerQueue();
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
            System.out.println("SendMessage CATCH:" +e.getMessage());
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
    public void loginPlayer(String playerName) throws InterruptedException {
        sendMessage("LOGIN~" + playerName);
    }

    /**
     * Following QUIT command connection is terminated //TODO properly close TUI
     */
    public void sendQuit() {
        try {

            System.out.println("Ending connection and game");
            this.sendMessage("QUIT~" + playerName);
            this.close();
            activeClient = false;
            myClientTUI.tuiThread.join();
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
            System.out.println("Player: "+ playerName + " in queue for Newgame, waiting for other queued player\n");
            this.gameStatus = true;
            this.myClientTUI.playerQueue();

        } else {
            System.out.println("Player not in queue for newgame.\n");
            this.myClientTUI.playerQueue();
        }

    }

    /**
     * When a NEWGAME command is received
     *
     * @param commandSplit
     */
    public void newGameStart(String[] commandSplit) {
        System.out.println("Starting new game with player 1: " + commandSplit[1] + " (XX) and player 2: " + commandSplit[2] + " (OO).\n");
        this.gameStatus = true; //get you out of the QUEUE loop
        //Make sure the board is empty from possible previous game.
        board.reset();

        System.out.println(" Current game situation: \n\n" + board.toString() + "\n");

        if (commandSplit[1].equals(this.playerName)) {
            this.mark = Mark.XX;
            System.out.println("You are player 1: " + commandSplit[1] +" (" +this.mark+ ") "+" it is your turn.");
            this.playerTurn = true;
            myClientTUI.askMove();
        } else {
            this.mark = Mark.OO;
            System.out.println("You are player 2: " + this.playerName + " (" +this.mark+ ")  please wait for move from other player");
            this.playerTurn = false;
        }

    }

}

