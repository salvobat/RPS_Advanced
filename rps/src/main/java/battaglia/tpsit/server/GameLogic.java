package battaglia.tpsit.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import battaglia.tpsit.common.GameMoves;
import battaglia.tpsit.common.GameResult;

/**
 * Classe che gestisce la logica di gioco.
 */
public class GameLogic {
    private static final Logger logger = LoggerFactory.getLogger(GameLogic.class);
    
    /**
     * Determina il risultato della partita tra due giocatori.
     * 
     * @param player1 Nome del primo giocatore
     * @param move1   Mossa del primo giocatore
     * @param player2 Nome del secondo giocatore
     * @param move2   Mossa del secondo giocatore
     * @return Un oggetto {@link GameResult} che rappresenta il risultato della partita.
     *         Se le mosse sono uguali, il risultato sarà un pareggio.
     *         Altrimenti, il vincitore sarà determinato in base alle regole del gioco.
     */
    public static GameResult determineResult(String player1, GameMoves move1, String player2, GameMoves move2) {
        if (move1 == move2) {
            logger.info("Pareggio tra {} e {}: entrambi hanno scelto {}", player1, player2, move1);
            return new GameResult(move1, move2);
        } else if (move1.beats(move2)) {
            String description = move1.getWinDescription(move2);
            logger.info("{} batte {}: {} - {}", player1, player2, description, move1);
            return new GameResult(player1, move1, move2, description);
        } else {
            String description = move2.getWinDescription(move1);
            logger.info("{} batte {}: {} - {}", player2, player1, description, move2);
            return new GameResult(player2, move2, move1, description);
        }
    }
}