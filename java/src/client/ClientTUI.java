package client;

/**
 * This is the TUI that a Pentago Player needs to run (thus the main in here),
 * the handling of all commands is done in the CLIENT class
 *
 * @author bart.fischer
 */


import com.sun.tools.javac.util.StringUtils;
import utils.TextIO;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientTUI {
    /* -- Variables: */

    private InetAddress address;
    private int port;
    private Client client = new Client(this);
    private String playerName;
    private boolean gameIsStarted = false;
    public Thread tuiThread;

    public static void main(String[] args) {
        new ClientTUI().initiateTUI();
    } //--End of Main

    public void initiateTUI() {

        //-- Initial Input:
        connectToServer();
        // New thread is required for handling reading input from the sever
        this.tuiThread = (new Thread(client));
        this.tuiThread.start();
        // Connection is now established and the player should enter a unique name:
        playerLogin();

    }

    /**
     * Asks for address and port to connect to server
     */
    private void connectToServer() {
        do {
            System.out.println("Welcome new Pentago Player!");
            System.out.println("Please enter the following details to connect to server:\n");

            try {
                // Get Address input //TODO check input
                System.out.println("Give server address (Type \"localhost\" when playing on this device):\n");
//                String addressInput = TextIO.getlnString();
//                address = InetAddress.getByName(addressInput);
                address = InetAddress.getByName("localhost");
                System.out.println("\t " + address + " localhost for now selected\n");
            } catch (UnknownHostException e) {
                System.out.println("This is not a valid address\n");
            }

            // Get Port input //TODO check input
            try {
                System.out.println("Please enter a valid port number \n");
//                int portInput = TextIO.getlnInt();
//                if(correctPort(portInput)){
//                   port = portInput;
//                }
                port = 8080;
                System.out.println("\t Port is set at " + port);
            } catch (Exception e) {
                System.out.println("This is an invalid port number\n");
                continue;
            }
        } while (!client.connect(address, port));
    }

    /**
     * After connection is made, player provides player name which is checked if it is unique on server.
     */
    public void playerLogin() {
        String playerName = "";
        // Get player name
        try {
            System.out.println("Please enter your name:\n");
            playerName = getPlayerName();
            client.setPlayerName(playerName);
            client.loginPlayer(playerName);

        } catch (Exception e) {
            System.out.println("ERROR: invalid player name.\n");
        }
    }

    /**
     * After playername is correct the player can queue for a game and if another is found a new game is started.
     *
     * @throws InterruptedException
     */
    public void playerQueue() throws InterruptedException {
        String messageTUI;
        try {
            printHelpMenu();
            while ((!getGameStatus())) {
                messageTUI = TextIO.getln();
                switch (StringUtils.toUpperCase(messageTUI)) {
                    case "LIST":
                        client.getList();
                        break;
                    case "QUEUE":
                        client.sendQueue();
                        break;
                    case "QUIT":
                        client.sendQuit();
                        break;
                    default:
                       break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Get the boolean value if a new game has started.
     *
     * @return true is game has started
     */
    public boolean getGameStatus() {
        return this.client.gameStatus;
    }

    /**
     * Check if name is valid (cannot be NULL or contain ~)
     *
     * @return set playername
     */
    public String getPlayerName() {
        String playerNameInPut = TextIO.getlnString();
        if (playerNameInPut.contains("~") || playerNameInPut.equals("")) {
            System.out.println("Name cannot be blank or contain \"~\" so please re-enter name!'\n");
            playerNameInPut = TextIO.getlnString();
        }
        this.playerName = playerNameInPut;
        return playerNameInPut;
    }

    /**
     * See if the giving port is within the correct range
     *
     * @param port integer given
     * @return true is port is correct, false if incorrect port is given
     */
    public static boolean correctPort(int port) {
        if (port >= 1025 && port <= 65535) {
            return true;
        }
        System.out.println("Error: No correct port detected! Re-enter values stupid!\n");
        System.out.println(" ");
        return false;
    }

    /**
     * To keep the playerQueue readable the menu is placed here.
     */
    public void printHelpMenu() {
        String menu = ("You can use the following commands: \n" +
                "LIST: " + "\t" + "This shows all connected players.\n" +
                "QUEUE: " + "\t" + "Indicates if you want to play or not.\n" +
                "QUIT: " + "\t" + "Disconnect from server. \n");
        System.out.println(menu);
    }

    /**
     * When a valid move is received the board is updated
     * Uses getGameStatus to prevent last move request
     */
    public void askMove() {
        if (getGameStatus()){
        System.out.println("Please enter valid move\n");
        String command = "MOVE~";
        int input = TextIO.getlnInt();
        command += input;
        client.sendMessage(command);
        }

    }

    /**
     * After a gameover the player is asked if they want to continue
     */
    public void continuePlay() throws InterruptedException {
        System.out.println("Do you want to play another game?\n");
        boolean input;
        input = TextIO.getBoolean();
        if (input) {
            gameIsStarted = false;
            this.client.gameStatus = false;
            this.client.queueStatus = false;
            playerQueue();
        } else client.sendQuit();
    }


}



