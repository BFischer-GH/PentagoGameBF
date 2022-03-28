# Example game 
In this document, the networking messages that are exchanged between a particular client and server during the execution of a game are shown. Note that, while both client and server support extensions, none of them are used since none of them are in common. 

The messages are shown below. The sentences in between (in *italics*) indicate the phase of the connection or a different event happening which is not in the log.   
The client is called Alice, her opponent in the game will be Bob. The server was made by Charlie. Only messages between Alice and the server are shown. 

*Start of initialization sequence*

- Client -> server: `HELLO~Masterpiece of Alice~RANK~CRYPT`
- Server -> client: `HELLO~Charlie's domain~AUTH`
- Client -> server: `LOGIN~Alice`
- Server -> client: `LOGIN`
- Client -> server: `QUEUE`

*After some time, Bob also joins and queues for a game*

- Server -> client: `NEWGAME~Bob~Alice`

*After some time, Bob has sent a move to the server*

- Server -> client: `MOVE~2~3` *(the move by Bob)*
- Client -> server: `MOVE~25~1` *(the move by Alice, after some time)*
- Server -> client: `MOVE~25~1` *(the server confirming the previous move, almost immediately)*
- Server -> client: `MOVE~10~4` *(the next move by Bob, after some time)*

*The pattern above continues for some time, until Alice does the last possible move*

- Server -> client: `GAMEOVER~VICTORY~Bob` *(Bob had five-in-a-row and won)*
- Client -> server: `QUEUE` *(Alice wants to play a next game)*
- *Nothing happens because Bob didn't want to play again*