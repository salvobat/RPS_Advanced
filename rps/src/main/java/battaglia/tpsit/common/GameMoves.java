package battaglia.tpsit.common;

/**
 * Enumerazione delle possibili mosse nel gioco "Rock Paper Scissors Lizard Spock".
 */
public enum GameMoves {
    ROCK, PAPER, SCISSORS, LIZARD, SPOCK;
    
    /**
     * Determina se questa mossa batte la mossa dell'avversario.
     * 
     * @param opponentMove La mossa dell'avversario
     * @return true se questa mossa batte quella dell'avversario, false altrimenti
     */
    public boolean beats(GameMoves opponentMove) {
        switch (this) {
            case ROCK:
                return opponentMove == SCISSORS || opponentMove == LIZARD;
            case PAPER:
                return opponentMove == ROCK || opponentMove == SPOCK;
            case SCISSORS:
                return opponentMove == PAPER || opponentMove == LIZARD;
            case LIZARD:
                return opponentMove == PAPER || opponentMove == SPOCK;
            case SPOCK:
                return opponentMove == ROCK || opponentMove == SCISSORS;
            default:
                return false;
        }
    }
    
    /**
     * Restituisce una descrizione della regola di vittoria.
     * 
     * @param opponent La mossa dell'avversario
     * @return Una stringa che descrive come questa mossa batte quella dell'avversario
     */
    public String getWinDescription(GameMoves opponent) {
        switch (this) {
            case ROCK:
                if (opponent == SCISSORS) return "Rock crushes Scissors";
                if (opponent == LIZARD) return "Rock crushes Lizard";
                break;
            case PAPER:
                if (opponent == ROCK) return "Paper covers Rock";
                if (opponent == SPOCK) return "Paper disproves Spock";
                break;
            case SCISSORS:
                if (opponent == PAPER) return "Scissors cuts Paper";
                if (opponent == LIZARD) return "Scissors decapitates Lizard";
                break;
            case LIZARD:
                if (opponent == PAPER) return "Lizard eats Paper";
                if (opponent == SPOCK) return "Lizard poisons Spock";
                break;
            case SPOCK:
                if (opponent == ROCK) return "Spock vaporizes Rock";
                if (opponent == SCISSORS) return "Spock smashes Scissors";
                break;
        }
        return "";
    }
    
    /**
     * Restituisce il nome dell'enumerazione in formato leggibile (prima lettera maiuscola)
     */
    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}