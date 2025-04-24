package battaglia.tpsit.client;

import battaglia.tpsit.common.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Classe che gestisce i messaggi in arrivo dal server.
 * Implementa l'interfaccia {@link Runnable} per essere eseguita in un thread separato.
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private Client client;
    private BufferedReader reader;
    private ObjectMapper objectMapper;
    private volatile boolean running;

    /**
     * Costruttore per il gestore dei messaggi.
     *
     * @param client Il client a cui è associato questo handler
     * @param reader Il {@link BufferedReader} per leggere i messaggi dal server
     */
    public ClientHandler(Client client, BufferedReader reader) {
        this.client = client;
        this.reader = reader;
        this.objectMapper = new ObjectMapper();
        this.running = true;
    }

    /**
     * Metodo eseguito quando il thread viene avviato.
     * Legge i messaggi dal server e li passa al client per la gestione.
     */
    @Override
    public void run() {
        try {
            // Continua a leggere i messaggi finché l'handler è in esecuzione
            while (running) {
                String messageStr = reader.readLine();
                if (messageStr == null) {
                    // Il server si è disconnesso
                    logger.warn("Il server si è disconnesso (EOF)");
                    handleDisconnection("Il server si è disconnesso");
                    break;
                }

                // Analizza il messaggio ricevuto
                Message message = objectMapper.readValue(messageStr, Message.class);

                // Passa il messaggio al client per la gestione
                client.handleMessage(message);
            }
        } catch (SocketException e) {
            if (running) {
                logger.error("Connessione al server persa", e);
                handleDisconnection("Connessione al server persa: " + e.getMessage());
            }
        } catch (IOException e) {
            if (running) {
                logger.error("Errore durante la lettura dei messaggi dal server", e);
                handleDisconnection("Errore di comunicazione: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Errore imprevisto nel client handler", e);
            handleDisconnection("Errore imprevisto: " + e.getMessage());
        } finally {
            stop();
        }
    }

    /**
     * Gestisce la disconnessione dal server.
     * Mostra un messaggio di errore all'utente e chiude il client.
     *
     * @param message Il messaggio di errore da mostrare
     */
    private void handleDisconnection(String message) {
        if (client.isConnected()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        message,
                        "Errore di connessione",
                        JOptionPane.ERROR_MESSAGE
                );
            });
            client.close();
        }
    }

    /**
     * Ferma l'esecuzione dell'handler.
     * Imposta il flag {@code running} a {@code false}.
     */
    public void stop() {
        running = false;
    }
}