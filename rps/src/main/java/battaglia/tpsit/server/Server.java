package battaglia.tpsit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import battaglia.tpsit.common.CryptoUtils;

/**
 * Server principale per il gioco RPS Advanced.
 * Gestisce la connessione dei client e l'avvio delle sessioni di gioco.
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 12345;
    
    private ServerSocket serverSocket;
    private boolean running;
    private ExecutorService executorService;
    private Map<String, ServerClientHandler> connectedClients;
    private Map<String, GameSession> gameSessions;
    private KeyPair serverKeyPair;
    
    /**
     * Costruttore del server.
     */
    public Server() {
        this.connectedClients = new ConcurrentHashMap<>();
        this.gameSessions = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
        
        try {
            this.serverKeyPair = CryptoUtils.generateRSAKeyPair();
            logger.info("Chiavi RSA generate con successo");
        } catch (Exception e) {
            logger.error("Errore durante la generazione delle chiavi RSA", e);
            throw new RuntimeException("Impossibile avviare il server: errore di crittografia");
        }
    }
    
    /**
     * Avvia il server.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            logger.info("Server avviato sulla porta {}", PORT);
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nuova connessione da {}", clientSocket.getInetAddress());
                
                // Crea un nuovo handler per il client
                ServerClientHandler clientHandler = new ServerClientHandler(this, clientSocket, serverKeyPair);
                executorService.submit(clientHandler);
            }
        } catch (IOException e) {
            if (running) {
                logger.error("Errore durante l'accettazione delle connessioni", e);
            }
        } finally {
            stop();
        }
    }
    
    /**
     * Ferma il server.
     */
    public void stop() {
        try {
            running = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executorService.shutdown();
            logger.info("Server arrestato");
        } catch (IOException e) {
            logger.error("Errore durante l'arresto del server", e);
        }
    }
    
    /**
     * Registra un client connesso.
     * 
     * @param username Nome utente del client
     * @param handler Handler del client
     */
    public void registerClient(String username, ServerClientHandler handler) {
        connectedClients.put(username, handler);
        logger.info("Client registrato: {}", username);
        
        // Verifica se è possibile avviare una nuova partita
        checkForMatchmaking();
    }
    
    /**
     * Rimuove un client dalla lista dei client connessi.
     * 
     * @param username Nome utente del client da rimuovere
     */
    public void removeClient(String username) {
        connectedClients.remove(username);
        logger.info("Client rimosso: {}", username);
    }
    
    /**
     * Controlla se ci sono abbastanza client per avviare una partita.
     */
    private void checkForMatchmaking() {
        if (connectedClients.size() >= 2) {
            // Prendi due client che non sono in una sessione di gioco
            String[] availableClients = connectedClients.keySet().stream()
                .filter(username -> !isClientInGame(username))
                .limit(2)
                .toArray(String[]::new);
            
            if (availableClients.length == 2) {
                String player1 = availableClients[0];
                String player2 = availableClients[1];
                
                // Crea una nuova sessione di gioco
                String sessionId = player1 + "-" + player2;
                GameSession gameSession = new GameSession(sessionId, player1, player2);
                gameSessions.put(sessionId, gameSession);
                
                // Notifica i client dell'inizio della partita
                ServerClientHandler handler1 = connectedClients.get(player1);
                ServerClientHandler handler2 = connectedClients.get(player2);
                
                handler1.setCurrentGameSession(gameSession);
                handler2.setCurrentGameSession(gameSession);
                
                handler1.notifyGameStart(player2);
                handler2.notifyGameStart(player1);
                
                logger.info("Nuova sessione di gioco avviata: {} vs {}", player1, player2);
            }
        }
    }
    
    /**
     * Verifica se un client è già in una sessione di gioco.
     * 
     * @param username Nome utente del client
     * @return true se il client è in una sessione di gioco, false altrimenti
     */
    private boolean isClientInGame(String username) {
        return gameSessions.values().stream()
            .anyMatch(session -> session.hasPlayer(username) && !session.isGameOver());
    }
    
    /**
     * Termina una sessione di gioco.
     * 
     * @param sessionId ID della sessione di gioco
     */
    public void endGameSession(String sessionId) {
        gameSessions.remove(sessionId);
        logger.info("Sessione di gioco terminata: {}", sessionId);
        
        // Verifica se è possibile avviare una nuova partita
        checkForMatchmaking();
    }

    /**
     * Restituisce la coppia di chiavi RSA del server.
     * 
     * @return La coppia di chiavi RSA
     */
    public KeyPair getServerKeyPair() {
        return serverKeyPair; // Assumes serverKeyPair is a field in the Server class
    }
    
    /**
     * Punto di ingresso principale.
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}