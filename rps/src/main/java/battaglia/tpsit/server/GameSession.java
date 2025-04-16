package battaglia.tpsit.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import battaglia.tpsit.common.GameMoves;
import battaglia.tpsit.common.GameResult;

import java.util.HashMap;
import java.util.Map;

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
    private boolean movesProcessed;
    
    /**
     * Costruttore per una nuova sessione di gioco.
     * 
     * @param sessionId ID della sessione
     * @param player1 Nome del primo giocatore
     * @param player2 Nome del secondo giocatore
     */
    public GameSession(String sessionId, String player1, String player2) {
        this.sessionId = sessionId;
        this.player1 = player1;
        this.player2 = player2;
        this.moves = new HashMap<>();
        this.gameOver = false;
        this.movesProcessed = false;
    }
    
    /**
     * Registra la mossa di un giocatore.
     * 
     * @param playerName Nome del giocatore
     * @param move Mossa effettuata
     */
    public synchronized void registerMove(String playerName, GameMoves move) {
        moves.put(playerName, move);
        logger.info("Giocatore {} ha scelto {}", playerName, move);
        
        // Se entrambi i giocatori hanno fatto la loro mossa, processa il risultato
        if (moves.size() == 2 && !movesProcessed) {
            processResult();
            movesProcessed = true;
        }
    }
    
    /**
     * Processa il risultato della partita dopo che entrambi i giocatori hanno effettuato le loro mosse.
     */
    private void processResult() {
        GameMoves move1 = moves.get(player1);
        GameMoves move2 = moves.get(player2);
        
        logger.info("Elaborazione risultato: {} ({}) vs {} ({})", player1, move1, player2, move2);
        
        // Il risultato è già disponibile attraverso getResultForPlayer
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

    public synchronized void processAndSendResults() {
        if (moves.size() != 2 || movesProcessed) {
            return;
        }
        
        // Get handlers for both players
        Server serverInstance = new Server();
        ServerClientHandler handler1 = serverInstance.getConnectedClient(player1);
        ServerClientHandler handler2 = serverInstance.getConnectedClient(player2);
        
        if (handler1 != null && handler2 != null) {
            // Send result to player 1
            GameResult result1 = getResultForPlayer(player1);
            handler1.sendGameResult(result1);
            
            // Send result to player 2
            GameResult result2 = getResultForPlayer(player2);
            handler2.sendGameResult(result2);
        }
        
        movesProcessed = true;
    }
    
    /**
     * Resetta le mosse per una nuova manche.
     */
    public synchronized void resetMoves() {
        moves.clear();
        movesProcessed = false;
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