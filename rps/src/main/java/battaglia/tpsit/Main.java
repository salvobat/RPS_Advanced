package battaglia.tpsit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        
        logger.info("Avvio del Main...");
        try{
            // Inizializza il gioco
            logger.debug("Configurazione caricacata correttamente");
            int x = 10/0;
        } catch (Exception e) {
            logger.error("Errore durante l'inizializzazione del gioco: " + e.getMessage());
            return;
        }

    }
}