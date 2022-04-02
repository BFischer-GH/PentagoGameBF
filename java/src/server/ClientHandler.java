package server;

/**
 * This clienthandler for 1  client on a new thread (thread is started by server)
 *
 * @author bart.fischer
 */

import game.Mark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class ClientHandler implements Runnable {
    private final BufferedReader chIn;
    private final PrintWriter chOut;
    private final Socket socket;
    private String playerName;
    private boolean queueStatus;
    private GameServer server;
    private GameHandler gameHandler;
    private Mark mark;
    boolean run = true;

    //-- Constructors
    public ClientHandler(Socket socket, GameServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.chIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.chOut = new PrintWriter(socket.getOutputStream(), true);
    }

    //-- Getter/Setters
    public String getPlayerName() {
        return playerName;
    }

    public boolean getQueue() {
        return queueStatus;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setGameHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    //-- Run (containing the Switch)
    @Override
    public void run() {
        while (run) {
            try {
                String message;
                while ((message = chIn.readLine()) != null) {
                    //System.out.println("Client: " + message);
                    String[] messageSplit = message.split("~");
                    switch (toUpperCase(messageSplit[0])) {
                        case "LOGIN":
                            playerLogin(messageSplit[1]);
                            break;
                        case "LIST":
                            sendList();
                            break;
                        case "QUEUE":
                            updateQueue();
                            break;
                        case "MOVE":
                            moveCH(messageSplit);
                            break;
                        case "QUIT":
                            this.chOut.println(this.playerName + " is quiting from server");
                            this.close();
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error at CH: " + e.getMessage());
            }

        }
    }


//-- Methods

    /**
     * Check move input and set it on gameHandler
     *
     * @param messageSplit
     */
    private void moveCH(String[] messageSplit) {
        if (this.gameHandler.checkMove(messageSplit[1])) {
            this.gameHandler.doMove(messageSplit[1], this.mark);
        } else sendCommand("ERROR");
        return;
    }

    /**
     * Method to send message to client
     *
     * @param command the message to be send
     */
    public void sendCommand(String command) {
        this.chOut.println(command);
    }

    /**
     * Close the connection by closing the socket from this ClientHandler
     *
     * @throws IOException
     */
    public void close() throws IOException {
        this.run = false;
        server.removeClient(this);
        socket.close();
    }

    /**
     * Following LIST request from CLIENT returns a string with connected player names
     *
     * @return String to CLIENT containing all names of player currently connected.
     */
    public void sendList() {
        List<ClientHandler> list = server.getList();
        String command = "LIST";
        for (ClientHandler ch : list) {
            command += "~" + ch.getPlayerName();
        }
        sendCommand(command);
    }

    /**
     * Checker if provided player name is already present in server
     *
     * @param playerName given bij player
     * @return True is player is already present in
     */
    public boolean checkName(String playerName) {
        List<ClientHandler> list = server.getList();
        for (ClientHandler ch : list) {
            if (ch.getPlayerName().equals(playerName))
                return true;
        }
        return false;
    }

    /**
     * Creates and checks if player Login is ok
     *
     * @param playerName input from CLIENT
     */

    private void playerLogin(String playerName) {
        setPlayerName(playerName);
        try {
            if (server.clients.isEmpty()) {
                System.out.println("First player joined");
                sendCommand("LOGIN");
                server.addClient(this);
            } else if (checkName(playerName)) {
                //setPlayerName("default");
                sendCommand("ALREADYLOGGEDIN");
                //server.addClient(this);
                System.out.println("Name already in server");
            } else {
                server.addClient(this);
                System.out.println("Additional player joined");
                sendCommand("LOGIN");
            }
        } catch (Exception e) {
            System.out.println("Error in name");
        }
    }

    /**
     * Switches the queue status following TUI input
     */
    public void updateQueue() throws IOException {
        flipQueueState();
        server.checkQueues(this);
    }

    /**
     * Changes the QUEUE state
     * Required for updateQueue() and when starting new game
     */
    public void flipQueueState() {
        this.queueStatus = !queueStatus;
    }

}

