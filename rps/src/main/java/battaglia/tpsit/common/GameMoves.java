package battaglia.tpsit.common;

/**
 * Enumerazione delle possibili mosse nel gioco "Sasso Carta Forbice Lucertola Spock".
 */
public enum GameMoves {
    SASSO, CARTA, FORBICE, LUCERTOLA, SPOCK;

    /**
     * Determina se questa mossa batte la mossa dell'avversario.
     *
     * @param opponentMove La mossa dell'avversario
     * @return {@code true} se questa mossa batte quella dell'avversario, {@code false} altrimenti
     */
    public boolean beats(GameMoves opponentMove) {
        switch (this) {
            case SASSO:
                return opponentMove == FORBICE || opponentMove == LUCERTOLA;
            case CARTA:
                return opponentMove == SASSO || opponentMove == SPOCK;
            case FORBICE:
                return opponentMove == CARTA || opponentMove == LUCERTOLA;
            case LUCERTOLA:
                return opponentMove == CARTA || opponentMove == SPOCK;
            case SPOCK:
                return opponentMove == SASSO || opponentMove == FORBICE;
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
            case SASSO:
                if (opponent == FORBICE) return "La Roccia rompe le Forbici";
                if (opponent == LUCERTOLA) return "La Roccia schiaccia la Lucertola";
                break;
            case CARTA:
                if (opponent == SASSO) return "La Carta copre la Roccia";
                if (opponent == SPOCK) return "La Carta smentisce Spock";
                break;
            case FORBICE:
                if (opponent == CARTA) return "La Forbice taglia la Carta";
                if (opponent == LUCERTOLA) return "La Forbice decapita la Lucertola";
                break;
            case LUCERTOLA:
                if (opponent == CARTA) return "La Lucertola mangia la Carta";
                if (opponent == SPOCK) return "La Lucertola avvelena Spock";
                break;
            case SPOCK:
                if (opponent == SASSO) return "Spock vaporizza la Roccia";
                if (opponent == FORBICE) return "Spock frantuma le Forbici";
                break;
        }
        return "";
    }

    /**
     * Restituisce il nome dell'enumerazione in formato leggibile (prima lettera maiuscola).
     *
     * @return Il nome della mossa con la prima lettera maiuscola.
     */
    @Override
    public String toString() {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}