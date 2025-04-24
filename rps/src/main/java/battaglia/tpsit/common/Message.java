package battaglia.tpsit.common;

import java.io.Serializable;

/**
 * Rappresenta un messaggio nel protocollo di comunicazione.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private String username;
    private String key;
    private String data;

    /**
     * Costruttore predefinito per Jackson.
     * Necessario per la serializzazione/deserializzazione.
     */
    public Message() {
    }

    /**
     * Crea un messaggio di tipo HELLO.
     *
     * @param username Il nome utente del client
     * @return Un messaggio di tipo HELLO
     */
    public static Message createHello(String username) {
        Message message = new Message();
        message.setType(MessageType.HELLO);
        message.setUsername(username);
        return message;
    }

    /**
     * Crea un messaggio di tipo PUBLIC_KEY.
     *
     * @param publicKeyBase64 La chiave pubblica in formato Base64
     * @return Un messaggio di tipo PUBLIC_KEY
     */
    public static Message createPublicKey(String publicKeyBase64) {
        Message message = new Message();
        message.setType(MessageType.PUBLIC_KEY);
        message.setKey(publicKeyBase64);
        return message;
    }

    /**
     * Crea un messaggio di tipo AES_KEY.
     *
     * @param encryptedKeyBase64 La chiave AES criptata in formato Base64
     * @return Un messaggio di tipo AES_KEY
     */
    public static Message createAesKey(String encryptedKeyBase64) {
        Message message = new Message();
        message.setType(MessageType.AES_KEY);
        message.setKey(encryptedKeyBase64);
        return message;
    }

    /**
     * Crea un messaggio di tipo MOVE.
     *
     * @param encryptedMoveBase64 La mossa criptata in formato Base64
     * @return Un messaggio di tipo MOVE
     */
    public static Message createMove(String encryptedMoveBase64) {
        Message message = new Message();
        message.setType(MessageType.MOVE);
        message.setData(encryptedMoveBase64);
        return message;
    }

    /**
     * Crea un messaggio di tipo RESULT.
     *
     * @param encryptedResultBase64 Il risultato criptato in formato Base64
     * @return Un messaggio di tipo RESULT
     */
    public static Message createResult(String encryptedResultBase64) {
        Message message = new Message();
        message.setType(MessageType.RESULT);
        message.setData(encryptedResultBase64);
        return message;
    }

    /**
     * Crea un messaggio di tipo ERROR.
     *
     * @param errorMessage Il messaggio di errore
     * @return Un messaggio di tipo ERROR
     */
    public static Message createError(String errorMessage) {
        Message message = new Message();
        message.setType(MessageType.ERROR);
        message.setData(errorMessage);
        return message;
    }

    /**
     * Crea un messaggio di tipo GAME_START.
     *
     * @param opponentName Il nome dell'avversario
     * @return Un messaggio di tipo GAME_START
     */
    public static Message createGameStart(String opponentName) {
        Message message = new Message();
        message.setType(MessageType.GAME_START);
        message.setData(opponentName);
        return message;
    }

    /**
     * Crea un messaggio di tipo WAIT_OPPONENT.
     *
     * @return Un messaggio di tipo WAIT_OPPONENT
     */
    public static Message createWaitOpponent() {
        Message message = new Message();
        message.setType(MessageType.WAIT_OPPONENT);
        return message;
    }

    /**
     * Crea un messaggio di tipo READY.
     *
     * @return Un messaggio di tipo READY
     */
    public static Message createReady() {
        Message message = new Message();
        message.setType(MessageType.READY);
        return message;
    }

    /**
     * Restituisce il tipo del messaggio.
     *
     * @return Il tipo del messaggio
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Imposta il tipo del messaggio.
     *
     * @param type Il tipo del messaggio
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * Restituisce il nome utente associato al messaggio.
     *
     * @return Il nome utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta il nome utente associato al messaggio.
     *
     * @param username Il nome utente
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la chiave associata al messaggio.
     *
     * @return La chiave
     */
    public String getKey() {
        return key;
    }

    /**
     * Imposta la chiave associata al messaggio.
     *
     * @param key La chiave
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Restituisce i dati associati al messaggio.
     *
     * @return I dati
     */
    public String getData() {
        return data;
    }

    /**
     * Imposta i dati associati al messaggio.
     *
     * @param data I dati
     */
    public void setData(String data) {
        this.data = data;
    }
}