package battaglia.tpsit.common;

/**
 * Enumerazione che definisce i tipi di messaggio per il protocollo di comunicazione.
 */
public enum MessageType {
    HELLO,        // Client -> Server: Richiesta di connessione
    PUBLIC_KEY,   // Server -> Client: Invio chiave pubblica RSA
    AES_KEY,      // Client -> Server: Invio chiave AES crittografata con RSA
    MOVE,         // Client -> Server: Invio mossa crittografata con AES
    RESULT,       // Server -> Client: Invio risultato crittografato con AES
    ERROR,        // Entrambi: Segnalazione errori
    GAME_START,   // Server -> Client: Notifica inizio partita
    WAIT_OPPONENT // Server -> Client: In attesa dell'avversario
}