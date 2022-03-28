# CRYPT extension

The CRYPT extension ensures (amongst others) confidentiality of messages exchanged between client and server. Keys are exchanged during the initialization sequence described here, after which all remaining messages will be encrypted. 

Support for this extension is indicated by adding `CRYPT` to the `HELLO` messages during initialization.

The key agreement algorithm to create a shared secret key is Diffie-Hellman (XDH) using X25519 private/public keys. Given that Java 11 is used, no additional libraries are necessary for the implementation, as Java 11 supports X25519 keys and XDH key agreement. Both the client and the server should generate a new private/public keypair for every new connection to ensure forward secrecy. 

# Adapted initialization sequence
After both `HELLO` messages have been exchanged and both parties support the CRYPT extension, the client can initiate encryption by sending the `ENCRYPT` message and awaiting the `ENCRYPT` reply from the server before continuing with `LOGIN`.

## ENCRYPT (client)
Sent by the client to indicate that it wants to use encryption for the remainder of the connection lifetime. The client provides its public key to the server in order to establish a shared secret. 

*Syntax*: `ENCRYPT~<public key>`

The public key is Base64-encoded. 

## Encrypt (server)
Sent as a reply to an encryption request by a client. The server replies with its own public key, allowing both parties to deduce the shared secret. 

*Syntax*: `ENCRYPT~<public key>`

The public key is Base64-encoded. 

# Messages after key exchange
After the above sequence, both parties now compute the shared secret using their own private key and the received public key. All further messages (*including* the `LOGIN` request, even if the `AUTH` is extension used) must now be encrypted using AES-256 and the shared secret (the exact Java cipher is `AES/GCM/NoPadding`, using 12-byte initial vectors, and 128-bit tag length). Every message is encrypted using a randomly generated 12-byte initialization vector, which is included in each message. 

*Syntax*: `<initialization vector>~<ciphertext>`

The initialization vector and ciphertext are Base64-encoded.

The decrypted version of each message should result in a valid (plaintext) command as defined in other places of the protocol. 

# Example
*Start of initialization sequence*
- Client -> server: `HELLO~Crispy Clean Client by Charlie~AUTH~RANK~CRYPT`
- Server -> client: `HELLO~Sanne's Secret Server~AUTH~CRYPT`
- Client -> server: `ENCRYPT~MCowBQYDK2VuAyEA5ABvY1ZtMT68fdCDuMtll5Uf+Xyu0kj7omPOJLck5Wc=`
- Server -> client: `ENCRYPT~MCowBQYDK2VuAyEALOjdaWJolLGRe7YCRweR4qAJSF1HB7E/PovZTENA3XY=`
- Client -> server: `YV2sz2FztJuOJi5I~4JQMxEiEbmcwf4bQ08msLZ/dOwQJZCNumIRVL24Bo3a2WkyaSx5HdJ8Do0mp7ZYNhTlqelzCOpgDf21uaL3ZpwLQahrCbVNwR6grYU2HjGogAG44K2IAN7yW`
- Server -> client: `fIsI4BSpOQ6weMdO~Rvf7aQTIK3nXvvauPMiQji2b0B2dqgGAYMwAtNaOrslPKbqnhoaG6TbTwjpENIxWACau3v+IyFqGbS7MNA0MXQ+QWmSmcCQDUjZQk3Kn9WTffQ==`
- Client -> server: `Oy7LUlMDPtALfiMu~4khOafWrAkLQgR9AY6e6xZQxyd4cfBzBzoR0OALKh7NEBjjZAhZgSBJA834i4BBwPdbMRxcVHraFo4IqX5rHSXPeDs1kVYoZOUE48iLygDcUa0ePm82CdWfGZiT44d36+C1Im6UCp/yY8dhpvJ47y7Bo`
- Server -> client: `jXM36nQgGVMSaYKm~dTaolp5e2NdIrN/kTcLoMD2ZBbRR`

Here, Charlie's client generated the keypair  
- private: `MFECAQEwBQYDK2VuBCIEIGg5hejDK6p9hlVWELYkX1IAqXAZpovAwsfD6jOiBpZwgSEA5ABvY1ZtMT68fdCDuMtll5Uf+Xyu0kj7omPOJLck5Wc=`  
- public: `MCowBQYDK2VuAyEA5ABvY1ZtMT68fdCDuMtll5Uf+Xyu0kj7omPOJLck5Wc=`.

Sanne's server generated the keypair  
- private: `MFECAQEwBQYDK2VuBCIEICCw1Y6Y6cyJka44/4IzUlpVQUwl39E895CEOjCS2GFugSEALOjdaWJolLGRe7YCRweR4qAJSF1HB7E/PovZTENA3XY=`  
- public: `MCowBQYDK2VuAyEALOjdaWJolLGRe7YCRweR4qAJSF1HB7E/PovZTENA3XY=`

The encrypted messages after the exchange are: `LOGIN`, `CHALLENGE`, `CHALLENGE`, `LOGIN` where Charlie logs in using the `AUTH` extension.
