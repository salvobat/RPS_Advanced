package battaglia.tpsit.common;

import java.io.Serializable;

/**
 * Rappresenta il risultato di una partita.
 */
public class GameResult implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String winnerUsername;
    private GameMoves playerMove;
    private GameMoves opponentMove;
    private String winDescription;
    private boolean draw;
    
    // Costruttore predefinito per Jackson
    public GameResult() {
    }
    
    /**
     * Costruttore per risultato con vincitore.
     */
    public GameResult(String winnerUsername, GameMoves playerMove, GameMoves opponentMove, String winDescription) {
        this.winnerUsername = winnerUsername;
        this.playerMove = playerMove;
        this.opponentMove = opponentMove;
        this.winDescription = winDescription;
        this.draw = false;
    }
    
    /**
     * Costruttore per pareggio.
     */
    public GameResult(GameMoves playerMove, GameMoves opponentMove) {
        this.playerMove = playerMove;
        this.opponentMove = opponentMove;
        this.draw = true;
        this.winDescription = "Pareggio - Entrambi i giocatori hanno scelto " + playerMove;
    }
    
    // Getters e Setters
    
    public String getWinnerUsername() {
        return winnerUsername;
    }

    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public GameMoves getPlayerMove() {
        return playerMove;
    }

    public void setPlayerMove(GameMoves playerMove) {
        this.playerMove = playerMove;
    }

    public GameMoves getOpponentMove() {
        return opponentMove;
    }

    public void setOpponentMove(GameMoves opponentMove) {
        this.opponentMove = opponentMove;
    }

    public String getWinDescription() {
        return winDescription;
    }

    public void setWinDescription(String winDescription) {
        this.winDescription = winDescription;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }
}