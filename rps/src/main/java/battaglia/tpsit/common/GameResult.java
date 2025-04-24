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

    /**
     * Costruttore predefinito per Jackson.
     * Necessario per la serializzazione/deserializzazione.
     */
    public GameResult() {
    }

    /**
     * Costruttore per un risultato con vincitore.
     *
     * @param winnerUsername  Il nome utente del vincitore
     * @param playerMove      La mossa effettuata dal giocatore
     * @param opponentMove    La mossa effettuata dall'avversario
     * @param winDescription  La descrizione della vittoria
     */
    public GameResult(String winnerUsername, GameMoves playerMove, GameMoves opponentMove, String winDescription) {
        this.winnerUsername = winnerUsername;
        this.playerMove = playerMove;
        this.opponentMove = opponentMove;
        this.winDescription = winDescription;
        this.draw = false;
    }

    /**
     * Costruttore per un risultato di pareggio.
     *
     * @param playerMove   La mossa effettuata dal giocatore
     * @param opponentMove La mossa effettuata dall'avversario
     */
    public GameResult(GameMoves playerMove, GameMoves opponentMove) {
        this.playerMove = playerMove;
        this.opponentMove = opponentMove;
        this.draw = true;
        this.winDescription = "Pareggio - Entrambi i giocatori hanno scelto " + playerMove;
    }

    /**
     * Restituisce il nome utente del vincitore.
     *
     * @return Il nome utente del vincitore
     */
    public String getWinnerUsername() {
        return winnerUsername;
    }

    /**
     * Imposta il nome utente del vincitore.
     *
     * @param winnerUsername Il nome utente del vincitore
     */
    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    /**
     * Restituisce la mossa effettuata dal giocatore.
     *
     * @return La mossa del giocatore
     */
    public GameMoves getPlayerMove() {
        return playerMove;
    }

    /**
     * Imposta la mossa effettuata dal giocatore.
     *
     * @param playerMove La mossa del giocatore
     */
    public void setPlayerMove(GameMoves playerMove) {
        this.playerMove = playerMove;
    }

    /**
     * Restituisce la mossa effettuata dall'avversario.
     *
     * @return La mossa dell'avversario
     */
    public GameMoves getOpponentMove() {
        return opponentMove;
    }

    /**
     * Imposta la mossa effettuata dall'avversario.
     *
     * @param opponentMove La mossa dell'avversario
     */
    public void setOpponentMove(GameMoves opponentMove) {
        this.opponentMove = opponentMove;
    }

    /**
     * Restituisce la descrizione della vittoria.
     *
     * @return La descrizione della vittoria
     */
    public String getWinDescription() {
        return winDescription;
    }

    /**
     * Imposta la descrizione della vittoria.
     *
     * @param winDescription La descrizione della vittoria
     */
    public void setWinDescription(String winDescription) {
        this.winDescription = winDescription;
    }

    /**
     * Verifica se il risultato è un pareggio.
     *
     * @return {@code true} se il risultato è un pareggio, {@code false} altrimenti
     */
    public boolean isDraw() {
        return draw;
    }

    /**
     * Imposta se il risultato è un pareggio.
     *
     * @param draw {@code true} se il risultato è un pareggio, {@code false} altrimenti
     */
    public void setDraw(boolean draw) {
        this.draw = draw;
    }
}