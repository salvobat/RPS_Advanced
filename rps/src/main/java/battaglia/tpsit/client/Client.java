package battaglia.tpsit.client;

import battaglia.tpsit.common.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Classe principale del client che gestisce la connessione al server.
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ObjectMapper objectMapper;
    private String username;
    private String opponentName;
    private PublicKey serverPublicKey;
    private SecretKey aesKey;
    private ClientHandler clientHandler;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private CompletableFuture<Void> connectionFuture = new CompletableFuture<>();
    private CompletableFuture<String> gameStartFuture = new CompletableFuture<>();
    private CompletableFuture<GameResult> resultFuture = new CompletableFuture<>();
    
    /**
     * Costruttore per il client.
     * 
     * @param username Nome utente del client
     */
    public Client(String username) {
        this.username = username;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Connette il client al server.
     * 
     * @return CompletableFuture che completa quando la connessione è stabilita
     */
    public CompletableFuture<Void> connect() {
        try {
            this.socket = new Socket(SERVER_HOST, SERVER_PORT);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            // Avvia il thread per la lettura dei messaggi dal server
            this.clientHandler = new ClientHandler(this, reader);  // Passa il reader direttamente
            Thread handlerThread = new Thread(clientHandler);
            handlerThread.setDaemon(true);
            handlerThread.start();
            
            // Invia il messaggio HELLO per iniziare la procedura di handshake
            Message helloMessage = Message.createHello(username);
            sendMessage(helloMessage);
            
            logger.info("Client avviato e connesso al server");
            connected.set(true);
            
        } catch (Exception e) {
            logger.error("Errore durante la connessione al server", e);
            connectionFuture.completeExceptionally(e);
        }
        
        return connectionFuture;
    }

    
    
    /**
     * Invia una mossa al server.
     * 
     * @param move La mossa da inviare
     * @return CompletableFuture che completa con il risultato della partita
     * @throws Exception Se si verifica un errore durante l'invio della mossa
     */
    public CompletableFuture<GameResult> sendMove(GameMoves move) throws Exception {
        if (!connected.get() || aesKey == null) {
            throw new IllegalStateException("Client non connesso o crittografia non inizializzata");
        }
        
        // Reset del future per il risultato
        resultFuture = new CompletableFuture<>();
        
        // Cripta la mossa con AES
        String moveStr = move.name();
        String encryptedMoveBase64 = CryptoUtils.encryptWithAES(moveStr, aesKey);
        
        // Crea e invia il messaggio
        Message moveMessage = Message.createMove(encryptedMoveBase64);
        sendMessage(moveMessage);
        
        logger.info("Mossa inviata: {}", move);
        return resultFuture;
    }
    
    /**
     * Invia un messaggio al server.
     * 
     * @param message Il messaggio da inviare
     * @throws Exception Se si verifica un errore durante l'invio del messaggio
     */
    public void sendMessage(Message message) throws Exception {
        String messageJson = objectMapper.writeValueAsString(message);
        writer.write(messageJson);
        writer.newLine();
        writer.flush();
    }
    
    /**
     * Gestisce un messaggio ricevuto dal server.
     * 
     * @param message Il messaggio ricevuto
     */
    public void handleMessage(Message message) {
        try {
            switch (message.getType()) {
                case PUBLIC_KEY:
                    handlePublicKey(message);
                    break;
                case WAIT_OPPONENT:
                    logger.info("In attesa di un avversario...");
                    break;
                case GAME_START:
                    handleGameStart(message);
                    break;
                case RESULT:
                    handleResult(message);
                    break;
                case ERROR:
                    logger.error("Errore dal server: {}", message.getData());
                    break;
                default:
                    logger.warn("Tipo di messaggio non gestito: {}", message.getType());
            }
        } catch (Exception e) {
            logger.error("Errore durante la gestione del messaggio", e);
        }
    }
    
    /**
     * Gestisce un messaggio di tipo PUBLIC_KEY.
     * 
     * @param message Il messaggio PUBLIC_KEY
     * @throws Exception Se si verifica un errore durante la gestione
     */
    private void handlePublicKey(Message message) throws Exception {
        String publicKeyBase64 = message.getKey();
        this.serverPublicKey = CryptoUtils.decodePublicKey(publicKeyBase64);
        logger.debug("Chiave pubblica RSA ricevuta dal server");
        
        // Genera una chiave AES casuale
        this.aesKey = CryptoUtils.generateAESKey();
        
        // Cripta la chiave AES con la chiave pubblica del server
        String encryptedKeyBase64 = CryptoUtils.encryptAESKeyWithRSA(aesKey, serverPublicKey);
        
        // Invia la chiave AES crittografata al server
        Message aesKeyMessage = Message.createAesKey(encryptedKeyBase64);
        sendMessage(aesKeyMessage);
        
        logger.debug("Chiave AES generata e inviata al server");
        
        // Completa il future della connessione
        connectionFuture.complete(null);
    }
    
    /**
     * Gestisce un messaggio di tipo GAME_START.
     * 
     * @param message Il messaggio GAME_START
     */
    private void handleGameStart(Message message) {
        this.opponentName = message.getData();
        logger.info("Partita iniziata contro {}", opponentName);
        
        // Completa il future dell'inizio partita
        gameStartFuture.complete(opponentName);
    }
    
    /**
     * Gestisce un messaggio di tipo RESULT.
     * 
     * @param message Il messaggio RESULT
     * @throws Exception Se si verifica un errore durante la gestione
     */
    private void handleResult(Message message) throws Exception {
        String encryptedResultBase64 = message.getData();
        String resultJson = CryptoUtils.decryptWithAES(encryptedResultBase64, aesKey);
        
        // Converte il JSON in oggetto GameResult
        GameResult result = objectMapper.readValue(resultJson, GameResult.class);
        logger.info("Risultato ricevuto: {}", result.getWinDescription());
        
        // Verifica se il future è già completato
        if (!resultFuture.isDone()) {
            // Completa il future del risultato
            resultFuture.complete(result);
        } else {
            logger.warn("Risultato ricevuto ma il future era già completato");
            // Crea un nuovo future e lo completa immediatamente
            resultFuture = CompletableFuture.completedFuture(result);
        }
    }
    
    /**
     * Attende l'inizio di una partita.
     * 
     * @return CompletableFuture che completa con il nome dell'avversario
     */
    public CompletableFuture<String> waitForGameStart() {
        // Reset del future per l'inizio partita
        gameStartFuture = new CompletableFuture<>();
        return gameStartFuture;
    }
    
    /**
     * Chiude la connessione con il server.
     */
    public void close() {
        try {
            connected.set(false);
            
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (clientHandler != null) {
                clientHandler.stop();
            }
            
            logger.info("Connessione chiusa");
        } catch (Exception e) {
            logger.error("Errore durante la chiusura della connessione", e);
        }
    }

    public void readyForNextRound() throws Exception {
        Message readyMessage = Message.createReady();
        sendMessage(readyMessage);
    }
    
    /**
     * Verifica se il client è connesso al server.
     * 
     * @return true se il client è connesso, false altrimenti
     */
    public boolean isConnected() {
        return connected.get();
    }
    
    /**
     * Ottiene il nome utente del client.
     * 
     * @return Nome utente
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Ottiene il nome dell'avversario.
     * 
     * @return Nome dell'avversario
     */
    public String getOpponentName() {
        return opponentName;
    }
}