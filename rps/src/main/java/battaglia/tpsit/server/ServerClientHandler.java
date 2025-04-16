package battaglia.tpsit.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import battaglia.tpsit.common.CryptoUtils;
import battaglia.tpsit.common.GameMoves;
import battaglia.tpsit.common.GameResult;
import battaglia.tpsit.common.Message;
import battaglia.tpsit.common.MessageType;

/**
 * Gestore della connessione con un singolo client sul lato server.
 */
public class ServerClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerClientHandler.class);
    
    private Server server;
    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    private PrivateKey privateKey;
    private SecretKey aesKey;
    private GameSession currentGameSession;
    private ObjectMapper objectMapper;
    private boolean running;
    
    /**
     * Costruttore per il gestore della connessione con un client.
     * 
     * @param server Il server principale
     * @param clientSocket Il socket del client
     * @param serverKeyPair La coppia di chiavi RSA del server
     */
    public ServerClientHandler(Server server, Socket clientSocket, KeyPair serverKeyPair) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.privateKey = serverKeyPair.getPrivate();
        this.objectMapper = new ObjectMapper();
        this.running = true;
        
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            logger.error("Errore durante l'inizializzazione degli stream", e);
            throw new RuntimeException("Impossibile inizializzare gli stream");
        }
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                String messageStr = reader.readLine();
                if (messageStr == null) {
                    break; // Client disconnesso
                }
                
                Message message = objectMapper.readValue(messageStr, Message.class);
                handleMessage(message);
            }
        } catch (IOException e) {
            logger.error("Errore durante la comunicazione con il client", e);
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Gestisce un messaggio ricevuto dal client.
     * 
     * @param message Il messaggio ricevuto
     */
    private void handleMessage(Message message) {
        try {
            switch (message.getType()) {
                case HELLO:
                    handleHello(message);
                    break;
                case AES_KEY:
                    handleAesKey(message);
                    break;
                case MOVE:
                    handleMove(message);
                    break;
                default:
                    logger.warn("Tipo di messaggio non gestito: {}", message.getType());
            }
        } catch (Exception e) {
            logger.error("Errore durante la gestione del messaggio", e);
            sendErrorMessage("Errore interno del server");
        }
    }
    
    /**
     * Gestisce un messaggio di tipo HELLO.
     * 
     * @param message Il messaggio HELLO
     * @throws Exception Se si verifica un errore durante la gestione
     */
    private void handleHello(Message message) throws Exception {
        this.username = message.getUsername();
        logger.info("Richiesta di connessione da: {}", username);
        
        // Invia la chiave pubblica RSA al client
        String publicKeyBase64 = CryptoUtils.encodePublicKey(server.getServerKeyPair().getPublic());
        Message response = Message.createPublicKey(publicKeyBase64);
        sendMessage(response);
        
        // Registra il client sul server
        server.registerClient(username, this);
    }
    
    /**
     * Gestisce un messaggio di tipo AES_KEY.
     * 
     * @param message Il messaggio AES_KEY
     * @throws Exception Se si verifica un errore durante la gestione
     */
    private void handleAesKey(Message message) throws Exception {
        String encryptedKeyBase64 = message.getKey();
        this.aesKey = CryptoUtils.decryptAESKeyWithRSA(encryptedKeyBase64, privateKey);
        logger.info("Chiave AES ricevuta e decifrata per l'utente: {}", username);
        
        // Notifica al client di aspettare un avversario
        sendMessage(Message.createWaitOpponent());
    }
    
    /**
     * Gestisce un messaggio di tipo MOVE.
     * 
     * @param message Il messaggio MOVE
     * @throws Exception Se si verifica un errore durante la gestione
     */
    private void handleMove(Message message) throws Exception {
        if (currentGameSession == null) {
            sendErrorMessage("Nessuna partita in corso");
            return;
        }
        
        // Decrittografa la mossa
        String encryptedMoveBase64 = message.getData();
        String moveStr = CryptoUtils.decryptWithAES(encryptedMoveBase64, aesKey);
        GameMoves move = GameMoves.valueOf(moveStr);
        
        // Registra la mossa nella sessione di gioco
        currentGameSession.registerMove(username, move);
        
        // Controlla se entrambi i giocatori hanno effettuato la loro mossa
        if (currentGameSession.bothPlayersMovedAndProcessed()) {
            // Ottieni il risultato direttamente dalla sessione
            GameResult result = currentGameSession.getResultForPlayer(username);
            String resultJson = objectMapper.writeValueAsString(result);
            String encryptedResultBase64 = CryptoUtils.encryptWithAES(resultJson, aesKey);
            
            Message resultMessage = Message.createResult(encryptedResultBase64);
            sendMessage(resultMessage);
        }
        
        // Non resettare qui la sessione - verrebbe fatto in un'altra fase del gioco
    }

    public void readyForNextRound() {
        if (currentGameSession != null && currentGameSession.bothPlayersMovedAndProcessed()) {
            currentGameSession.resetMoves();
        }
    }
    
    /**
     * Invia un messaggio al client.
     * 
     * @param message Il messaggio da inviare
     */
    public void sendMessage(Message message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            writer.write(messageJson);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            logger.error("Errore durante l'invio del messaggio", e);
            closeConnection();
        }
    }
    
    /**
     * Invia un messaggio di errore al client.
     * 
     * @param errorMessage Il messaggio di errore
     */
    private void sendErrorMessage(String errorMessage) {
        Message errorMsg = Message.createError(errorMessage);
        sendMessage(errorMsg);
    }
    
    /**
     * Notifica al client l'inizio di una partita.
     * 
     * @param opponentName Nome dell'avversario
     */
    public void notifyGameStart(String opponentName) {
        Message gameStartMsg = Message.createGameStart(opponentName);
        sendMessage(gameStartMsg);
    }
    
    /**
     * Imposta la sessione di gioco corrente.
     * 
     * @param gameSession La sessione di gioco
     */
    public void setCurrentGameSession(GameSession gameSession) {
        this.currentGameSession = gameSession;
    }
    
    /**
     * Chiude la connessione con il client.
     */
    public void closeConnection() {
        if (running) {
            running = false;
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (clientSocket != null) clientSocket.close();
                
                if (username != null) {
                    server.removeClient(username);
                }
                
                logger.info("Connessione chiusa con: {}", username != null ? username : "client sconosciuto");
            } catch (IOException e) {
                logger.error("Errore durante la chiusura della connessione", e);
            }
        }
    }

    public void sendGameResult(GameResult result) {
        try {
            String resultJson = objectMapper.writeValueAsString(result);
            String encryptedResultBase64 = CryptoUtils.encryptWithAES(resultJson, aesKey);
            
            Message resultMessage = Message.createResult(encryptedResultBase64);
            sendMessage(resultMessage);
        } catch (Exception e) {
            logger.error("Errore durante l'invio del risultato di gioco", e);
        }
    }


}