# AUTH extension

The `AUTH` extension allows the client and the server to verify each other's identities by digitally signing an authentication challenge. The client should use the same private and public key every time it connects to the server, thus providing evidence for their identity. The server should obviously also use the same private and public key every time. Servers must check that the client uses the same public key to authenticate. Clients should also check that the server uses the same public key as before.

Support for this extension is indicated by adding `AUTH` to the `HELLO` messages during initialization.

The authentication is based on digital signatures using [Ed25519 private/public keys](https://en.wikipedia.org/wiki/EdDSA) and proceeds as follows.
1. The client initiates by sending a `LOGIN` message with its username, its own public key, and an authentication challenge for the server.  
The authentication challenge is an array of 40 (securely chosen) random bytes.
2. The server signs the authentication challenge from the client using its own private key.
3. The server replies by sending a `CHALLENGE` message with its own public key, the signature of step 2, and a (different) randomly generated authentication challenge for the client.
4. The client verifies the signature of the server and compares the public key with the known public key (if connected before).
5. The client signs the authentication challenge from the server using its own private key.
6. The client replies by sending a `CHALLENGE` message with the signature of step 5.
7. The server verifies the signature of the client and compares the public key with the known public key (if logged in before). If this fails, the server sends a `WRONGKEY` message.
8. The server checks that the user is not already logged in. If this is the case, then the server sends a `ALREADYLOGGEDIN` message.
9. The server confirms that the user is logged in by sending a `LOGIN` message.

If the client and the server support the `CRYPT` extension and the connection is encrypted, the above process is modified. When encryption is turned on, the server and client sign the concatenation of the authentication challenge and the shared secret key. That is, they both sign a 72-byte array, where the first 40 bytes are the received authentication challenge, and the last 32 bytes are the shared secret key. Similarly verifying the signature. This thwarts man-in-the-middle attacks as an attacker cannot forge the signature without knowing the private keys of the client and the server.

# Adapted initialization sequence
After both `HELLO` messages have been exchanged (and possibly both `ENCRYPT` messages), the client initiates authenticated login by sending an extended `LOGIN` command. 

## LOGIN (client)
*Note: this is an adapted version of the [LOGIN command as defined for the base protocol](../commands.md#login-client). The original version should still be supported for interacting with servers without this extension.* 

Sent by the client to claim a username. The client provides its own public key. If the client has already authenticated to this server before, this public key should be identical to the previously used key. The client also provides an authentication challenge for the server.

*Syntax*: `LOGIN~<username>~<public key>~<challenge>`

The public key is a Base64-encoded Ed25519 public key.  
The challenge is a Base64-encoding of 40 (securely chosen) random bytes.

## CHALLENGE (server)
Sent as a reply to a `LOGIN` request with a public key. The server proves its identity by sending its public key and signing the authentication challenge sent by the client. The server also provides an authentication challenge for the client.

*Syntax*: `CHALLENGE~<public key>~<signature>~<challenge>`

The public key is a Base64-encoded Ed25519 public key (of the server).  
The signature is a Base64-encoded array of bytes.  
The challenge is a Base64-encoding of 40 (securely chosen) random bytes.

## CHALLENGE (client)
Sent as a reply to a `CHALLENGE` command from the server. The client proves its identity by signing the challenge sent by the server. 

*Syntax*: `CHALLENGE~<signature>`

The signature is a Base64-encoded array of bytes.

## LOGIN (server)
*Note: this is identical to the [command defined in the base protocol](../commands.md#login-server)*

A confirmation by the server that the client has passed the challenge. This marks the end of the initialization sequence. 

*Syntax*: `LOGIN`

Note: once a username has been claimed with a public key, the server must reject unauthenticated login attempts with that username.

## WRONGKEY (server)
Sent as a reply to a `CHALLENGE` command from the client. While the client has passed the challenge (otherwise an `ERROR` message is sent), the public key is not the key that is registered in the server for the given username.

*Syntax*: `WRONGKEY`

## ALREADYLOGGEDIN (server)
*Note: this is identical to the [command defined in the base protocol](../commands.md#alreadyloggedin-server)*

Sent as a reply to a `CHALLENGE` command from the client. While the client has passed the challenge and used the correct public key, there is already a client connected to the server using these credentials.

*Syntax*: `ALREADYLOGGEDIN`

# Errors during initialization sequence
During the sequence as defined above, errors can occur at multiple places: Base64-encodings can be malformed and the signature check can fail, among many others. As defined in [the base protocol](../commands#error-handling), the generic `ERROR` message can be sent for any of such errors, and the other party can re-attempt with a valid message. This extension is no exception to this: it is merely stressed due to the plethora of errors that are possible.  

# Implementation details
Unlike the X25519 keypairs for the `CRYPT` extension, Java 11 does not support Ed25519 keys and digital signatures out-of-the-box. This feature is added in Java 15. To use Ed25519 keys in Java 11, we suggest using the [Bouncy Castle library](https://www.bouncycastle.org/java.html). Please study [Provider Installation](https://github.com/bcgit/bc-java/wiki/Provider-Installation). Use the latest version with the JCA provider, for example, bcprov-jdk15on-166.jar. 

Our intention is that, by implementing the `AUTH` extension, you demonstrate the ability to cleanly use an external library. Include the jar file of the library and ensure that your server and client run without missing dependencies (when following the instructions in your README file). For example, you could produce a jar artifact that includes dependency jar files.

Study the [JCA reference guide](https://docs.oracle.com/javase/9/security/java-cryptography-architecture-jca-reference-guide.htm). You can use the JCA's `KeyPairGenerator` to generate the Ed25519 keys. Use JCA's `Signature` class to sign and verify messages. JCA lets you encode keys using the `getEncoded()` method of keys. Decoding involves the `KeyFactory` and either `PKCS8EncodedKeySpec` for private keys, or `X509EncodedKeySpec` for public keys.

# Example

*Start of initialization sequence*
- Client -> server: `HELLO~Crispy Clean Client by Charlie~AUTH~RANK~CRYPT`
- Server -> client: `HELLO~Sanne's Secret Server~AUTH~CRYPT`
- Client -> server: `LOGIN~Charlie~MCowBQYDK2VwAyEAWulAz3uheF7aPxk66CfBg/JiblYHNgk2L2JFFTiYq4U=~+dGj6c87MWiChIjpCuP5bzJw1oYlebQgd6UzTu5QPkE+nakBtMAZBg==`
- Server -> client: `CHALLENGE~MCowBQYDK2VwAyEATCIVgKnkV3tvR8fv1lt2LkBXB+jkgOJ871HpV1qr2yM=~+Ij0ck/rSIPk94FygXTlngtXfUhuSW9bUUGK3Wp5ZPfB6u1DfMHbvZTYCaeSd11H6igO9EiPBxbTT7m1mkT8Cw==~m7WZIFIpCiOnhtGwnAYdkrm+1LROCr17BWyOEjk6Wa7X1BSfpGY2BQ==`
- Client -> server: `CHALLENGE~Vx7kyrHuvZ8u6iXLv7eLByqPGtWdIHTKunHoXB8tWoqTn4zXdMx/ihTSPwC8BZHreCFuqyNq8RExBu1EUd9JBw==`
- Server -> client: `LOGIN`

Here, Charlie used the generated Ed25516 keypair
- private: `MFECAQEwBQYDK2VwBCIEIM+oLulwUky5dulF7IerX74M9I9IfAqtE8zxNRDRWeDxgSEAWulAz3uheF7aPxk66CfBg/JiblYHNgk2L2JFFTiYq4U=`
- public: `MCowBQYDK2VwAyEAWulAz3uheF7aPxk66CfBg/JiblYHNgk2L2JFFTiYq4U=`

The client sent a `LOGIN` message with
- the client's username `Charlie`
- the client's public key `MCowBQYDK2VwAyEAWulAz3uheF7aPxk66CfBg/JiblYHNgk2L2JFFTiYq4U=`
- the challenge to the server `+dGj6c87MWiChIjpCuP5bzJw1oYlebQgd6UzTu5QPkE+nakBtMAZBg==`

The server replied with a `CHALLENGE` message with
- the server's public key `MCowBQYDK2VwAyEATCIVgKnkV3tvR8fv1lt2LkBXB+jkgOJ871HpV1qr2yM=`
- the server's signature `+Ij0ck/rSIPk94FygXTlngtXfUhuSW9bUUGK3Wp5ZPfB6u1DfMHbvZTYCaeSd11H6igO9EiPBxbTT7m1mkT8Cw==` obtained by signing the challenge from the client
- the challenge to the client `m7WZIFIpCiOnhtGwnAYdkrm+1LROCr17BWyOEjk6Wa7X1BSfpGY2BQ==`

The client replied with a `CHALLENGE` message with
- the client's signature `Vx7kyrHuvZ8u6iXLv7eLByqPGtWdIHTKunHoXB8tWoqTn4zXdMx/ihTSPwC8BZHreCFuqyNq8RExBu1EUd9JBw==` obtained by signing the challenge from the server

Finally, the server replied with `LOGIN`.
