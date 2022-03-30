package client;

/**
 * This is the TUI that a Pentago Player needs to run (thus the main in here), the handling of all commands is done in the CLIENT class
 *
 * @author bart.fischer
 */

import utils.TextIO;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class ClientTUI {
    //-- Variables:
    Lock lock1 = new ReentrantLock();
    private InetAddress address;
    private int port;
    private Client client = new Client(this);
    private String playerName;



    public static void main(String[] args) throws IOException, InterruptedException{
        new ClientTUI().run();

    } //--End of Main
    public void run() throws IOException, InterruptedException {

        //-- Initial Input:
        connectToServer();
        // Connection is now established and the player should enter a unique name:
        playerLogin();

//        // Player has provided unique name and should Queue to start a game:
//        playerQueue();

        // 2 queued players have matched and a new game has started;`
        startGame();
    }
    /**
     * Asks for address and port to connect to server
     */
    private void connectToServer() {
        do {
            System.out.println("Welcome new Pentago Player!");
            System.out.println("Please enter the following details to connect to server:\n");

            try {
                // Get Address input
                System.out.println("Give server address (Type \"localhost\" when playing on this device):");
                //String addressInput = TextIO.getlnString(); //TODO return address input to normal
                //address = InetAddress.getByName(addressInput);
                address = InetAddress.getByName("localhost");
                System.out.println("\t " + address + " localhost for now selected");
            } catch (UnknownHostException e) {
                System.out.println("This is not a valid address");
            }

            // Get Port input
            try {
                System.out.println("Please enter a valid port number ");
//                int portInput = TextIO.getlnInt(); //todo return addres input to normal
//                if(correctPort(portInput)){
//                   port = portInput;
//                }
                port = 8080;
                System.out.println("\t Port is set at " + port);
            } catch (Exception e) {
                System.out.println("This is an invalid port number");
                continue;
            }
        } while (!client.connect(address, port));
    }

    /**
     * After connection is made, player provides player name which is checked if it is unique on server.
     */
    public void playerLogin() {
        String playerName;
            // Get player name
            try {
                System.out.println("Please enter your name:");
                playerName = getPlayerName();
                client.setPlayername(playerName);
                client.loginPlayer(playerName);

                //System.out.println("LoginSuccesTUI: "+client.loginSuccess);
                //client.login();

            } catch (Exception e) {
                System.out.println("ERROR: invalid player name.");
            }
    }

    /**
     * After playername is correct the player can queue for a game and if another is found a new game is started.
     * @throws InterruptedException
     */
    public void playerQueue() throws InterruptedException {
        String messageTUI = "";
        while (!messageTUI.equals("QUIT") || !client.gameStatus)  {
            try {
                System.out.println("You can use the following commands (CLIENTTUI)\n" +
                        "LIST: " + "\t" + "This shows all connected players.\n" +
                        "QUEUE: " + "\t" + "Indicates if you want to play or not.\n" +
                        "QUIT: " + "\t" + "Disconnect from server. \n");

                (new Thread(client)).start();
                messageTUI = TextIO.getlnString();
                switch (toUpperCase(messageTUI)) {
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
                        System.out.println("ERROR: Invalid input");
                        break;
                }
            } catch (Exception e) {
                System.out.println("ERROR: LOGIN");
            }
            //Thread.sleep(300); //Added timer to make sure that it comes after the CH input
        }


    }

    /**
     * After new game is started, the first player becomes the active player and can send the first move.
     */
    private void startGame() {

        while (client.gameStatus){
            try {
                String messageMove = TextIO.getlnString();




            } catch (Exception e) {
                System.out.println("ERROR: GAME ACTIVE");
            }
        }
    }
//----------------------------------------------------------------------------------------------------------------------
    //--Get/Set

    /**
     * Check if name is valid (cannot be NULL or contain ~)
     *
     * @return set playername
     */
    public String getPlayerName() {
        String playerNameInPut = TextIO.getlnString();
        if (playerNameInPut.contains("~") || playerNameInPut.equals("")) {
            System.out.println("Name cannot be blank or contain \"~\" so please re-enter name:");
            playerNameInPut = TextIO.getlnString();
        }
        this.playerName = playerNameInPut;
        return playerNameInPut;
        }


    //--Checkers

    /** See if the giving port is within the correct range
     *
     * @param port integer given
     * @return true is port is correct, false if incorrect port is given
     */
    public static boolean correctPort(int port) {
        if (port >= 1025 && port <= 65535) {
            return true;
        }
        System.out.println("Error: No correct port detected! Re-enter values stupid!");
        System.out.println(" ");
        return false;
    }

}


