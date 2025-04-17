package battaglia.tpsit.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import battaglia.tpsit.common.GameMoves;
import battaglia.tpsit.common.GameResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Rappresenta una sessione di gioco tra due giocatori.
 */
public class GameSession {
    private static final Logger logger = LoggerFactory.getLogger(GameSession.class);
    
    private String sessionId;
    private String player1;
    private String player2;
    private Map<String, GameMoves> moves;
    private boolean gameOver;
    private volatile boolean movesProcessed;
    private Server server;
    private CountDownLatch movesLatch;
    
    /**
     * Costruttore per una nuova sessione di gioco.
     * 
     * @param sessionId ID della sessione
     * @param player1 Nome del primo giocatore
     * @param player2 Nome del secondo giocatore
     * @param server Riferimento al server
     */
    public GameSession(String sessionId, String player1, String player2, Server server) {
        this.sessionId = sessionId;
        this.player1 = player1;
        this.player2 = player2;
        this.moves = new HashMap<>();
        this.gameOver = false;
        this.movesProcessed = false;
        this.server = server;
        this.movesLatch = new CountDownLatch(2); // Attende le mosse di entrambi i giocatori
    }

    private Set<String> readyPlayers = new HashSet<>();

    public synchronized void playerReadyForNextRound(String playerName) {
        readyPlayers.add(playerName);
        
        // Se entrambi i giocatori sono pronti, resetta la sessione
        if (readyPlayers.size() == 2) {
            resetMoves();
            readyPlayers.clear();
            logger.info("Entrambi i giocatori pronti per una nuova manche");
        
            // Notifica entrambi i client dell'inizio della nuova partita
            ServerClientHandler handler1 = server.getConnectedClient(player1);
            ServerClientHandler handler2 = server.getConnectedClient(player2);
        
            if (handler1 != null && handler2 != null) {
                handler1.notifyGameStart(player2);
                handler2.notifyGameStart(player1);
            } else {
                logger.warn("Uno dei client non è più connesso");
            }
        }
        
    }
    
    /**
     * Registra la mossa di un giocatore.
     * 
     * @param playerName Nome del giocatore
     * @param move Mossa effettuata
     */
    public synchronized void registerMove(String playerName, GameMoves move) {
        if (moves.containsKey(playerName)) {
            logger.warn("Il giocatore {} ha già effettuato una mossa", playerName);
            return;
        }
        
        moves.put(playerName, move);
        logger.info("Giocatore {} ha scelto {}", playerName, move);
        movesLatch.countDown();
        
        // Se entrambi i giocatori hanno fatto la loro mossa, processa il risultato
        if (moves.size() == 2 && !movesProcessed) {
            processAndSendResults();
        }
    }
    
    /**
     * Processa il risultato della partita e invia i risultati ai client.
     */
    public synchronized void processAndSendResults() {
        if (moves.size() != 2 || movesProcessed) {
            return;
        }
        
        try {
            // Attendiamo che entrambi i giocatori completino le loro mosse
            movesLatch.await();
            
            // Otteniamo gli handler per entrambi i giocatori
            ServerClientHandler handler1 = server.getConnectedClient(player1);
            ServerClientHandler handler2 = server.getConnectedClient(player2);
            
            if (handler1 != null && handler2 != null) {
                // Invia risultato al giocatore 1
                GameResult result1 = getResultForPlayer(player1);
                handler1.sendGameResult(result1);
                
                // Invia risultato al giocatore 2
                GameResult result2 = getResultForPlayer(player2);
                handler2.sendGameResult(result2);
                
                logger.debug("Risultati inviati a entrambi i giocatori");
            } else {
                logger.error("Impossibile inviare risultati, uno o entrambi gli handler non sono disponibili");
            }
            
            movesProcessed = true;
        } catch (InterruptedException e) {
            logger.error("Interruzione durante l'attesa delle mosse", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Verifica se entrambi i giocatori hanno effettuato la loro mossa e il risultato è stato processato.
     * 
     * @return true se entrambi i giocatori hanno fatto la loro mossa e il risultato è stato processato
     */
    public synchronized boolean bothPlayersMovedAndProcessed() {
        return moves.size() == 2 && movesProcessed;
    }
    
    /**
     * Ottiene il risultato della partita per un giocatore specifico.
     * 
     * @param playerName Nome del giocatore
     * @return Risultato della partita
     */
    public synchronized GameResult getResultForPlayer(String playerName) {
        if (moves.size() != 2) {
            return null;
        }
        
        String opponentName = playerName.equals(player1) ? player2 : player1;
        GameMoves playerMove = moves.get(playerName);
        GameMoves opponentMove = moves.get(opponentName);
        
        if (playerMove.equals(opponentMove)) {
            return new GameResult(playerMove, opponentMove);
        } else if (playerMove.beats(opponentMove)) {
            return new GameResult(playerName, playerMove, opponentMove, playerMove.getWinDescription(opponentMove));
        } else {
            return new GameResult(opponentName, opponentMove, playerMove, opponentMove.getWinDescription(playerMove));
        }
    }
    
    /**
     * Resetta le mosse per una nuova manche.
     */
    public synchronized void resetMoves() {
        moves.clear();
        movesProcessed = false;
        movesLatch = new CountDownLatch(2); // Resetta il latch
        logger.debug("Sessione di gioco resettata per una nuova manche");
    }
    
    /**
     * Termina la sessione di gioco.
     */
    public synchronized void endGame() {
        gameOver = true;
    }
    
    /**
     * Verifica se la sessione di gioco è terminata.
     * 
     * @return true se la sessione è terminata
     */
    public synchronized boolean isGameOver() {
        return gameOver;
    }
    
    /**
     * Verifica se un giocatore è in questa sessione.
     * 
     * @param playerName Nome del giocatore
     * @return true se il giocatore è in questa sessione
     */
    public boolean hasPlayer(String playerName) {
        return player1.equals(playerName) || player2.equals(playerName);
    }
    
    /**
     * Ottiene l'ID della sessione.
     * 
     * @return ID della sessione
     */
    public String getSessionId() {
        return sessionId;
    }
}