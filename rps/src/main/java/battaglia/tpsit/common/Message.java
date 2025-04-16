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
    
    // Costruttore predefinito per Jackson
    public Message() {
    }
    
    /**
     * Costruttore per messaggi di tipo HELLO.
     */
    public static Message createHello(String username) {
        Message message = new Message();
        message.setType(MessageType.HELLO);
        message.setUsername(username);
        return message;
    }
    
    /**
     * Costruttore per messaggi di tipo PUBLIC_KEY.
     */
    public static Message createPublicKey(String publicKeyBase64) {
        Message message = new Message();
        message.setType(MessageType.PUBLIC_KEY);
        message.setKey(publicKeyBase64);
        return message;
    }
    
    /**
     * Costruttore per messaggi di tipo AES_KEY.
     */
    public static Message createAesKey(String encryptedKeyBase64) {
        Message message = new Message();
        message.setType(MessageType.AES_KEY);
        message.setKey(encryptedKeyBase64);
        return message;
    }
    
    /**
     * Costruttore per messaggi di tipo MOVE.
     */
    public static Message createMove(String encryptedMoveBase64) {
        Message message = new Message();
        message.setType(MessageType.MOVE);
        message.setData(encryptedMoveBase64);
        return message;
    }
    
    /**
     * Costruttore per messaggi di tipo RESULT.
     */
    public static Message createResult(String encryptedResultBase64) {
        Message message = new Message();
        message.setType(MessageType.RESULT);
        message.setData(encryptedResultBase64);
        return message;
    }
    
    /**
     * Costruttore per messaggi di tipo ERROR.
     */
    public static Message createError(String errorMessage) {
        Message message = new Message();
        message.setType(MessageType.ERROR);
        message.setData(errorMessage);
        return message;
    }
    
    /**
     * Costruttore per messaggi di tipo GAME_START.
     */
    public static Message createGameStart(String opponentName) {
        Message message = new Message();
        message.setType(MessageType.GAME_START);
        message.setData(opponentName);
        return message;
    }
    
    /**
     * Costruttore per messaggi di tipo WAIT_OPPONENT.
     */
    public static Message createWaitOpponent() {
        Message message = new Message();
        message.setType(MessageType.WAIT_OPPONENT);
        return message;
    }

    public static Message createReady() {
        Message message = new Message();
        message.setType(MessageType.READY);
        return message;
    }
    
    // Getters e Setters
    
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}