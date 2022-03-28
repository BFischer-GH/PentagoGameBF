# CHAT extension
The chat extension allows client to communicate both during and outside games via text messages. Support for this extension is indicated by adding `CHAT` to the `HELLO` commands during initialization.

**Important**: All chat messages are allowed to contain the delimiter (`~`). Given that the message is always the last argument, no confusion will arise as to what the sender intended. The message should be presented as one message to the user. 

# New commands

## CHAT (client)
Sent by the client to request delivery of a chat message to all clients. The server will then deliver this message to all clients *except for the client who sent the message*. 

*Syntax*: `CHAT~<message>`

### Examples
- To send a message to all clients: `CHAT~Hello from Enschede!`
- To send a global chat message containing multiple tildes: `CHAT~Obviously everyone has seen a tilde before! It's not as if ~ is that special.In fact, some people have already seen ~= during the Haskell pearl.` 
- To send a global chat message containing a single tilde: `CHAT~~`

## CHAT (server)
Sent by the server to notify the client that it has received a message that was sent to the global chat. Sent to all clients who support chat functionality, *except for the client who sent the message*. 

*Syntax*: `CHAT~<sender>~<message>`

The first argument is the username of the sender of the message. The second argument contains the actual message. 

### Examples
- To report a message sent by Charlie to the global chat: `CHAT~Charlie~Is it raining for everyone else as well?`
- To report a message consisting of only two tildes: `CHAT~Chuck~~~`

## WHISPER (client)
Sent by the client to request delivery of a private chat message to a particular client (possibly itself). 
Note that if the client is unknown in the server or does not support chat, the server replies with `CANNOTWHISPER~<recipient>`.  

*Syntax*: `WHISPER~<recipient>~<message>`

### Examples
- Alice sending a private message to Bob: `WHISPER~Bob~I have a feeling that Chuck will cheat...`
- Chuck sending a private message to Johnny Flodder: `WHISPER~Johnny Flodder~Alice is winning. Execute plan C within ~10 seconds.`

## WHISPER (server)
Sent by the server to notify a client that it has received a private message. 

*Syntax*: `WHISPER~<sender>~<message>`

### Examples
- Bob receiving a private message from Alice: `WHISPER~Alice~I have a feeling that Chuck will cheat...`
- Johnny Flodder receiving a private message from Chuck: `WHISPER~Chuck~Alice is winning. Execute plan C within ~10 seconds.`

## CANNOTWHISPER (server)
Sent by the server to notify the client that the intended recipient of a message is unable to receive the message.

*Syntax*: `CANNOTWHISPER~<recipient>`