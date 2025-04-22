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
 * Gestore dei messaggi in arrivo dal server.
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
     * @param reader Il BufferedReader per leggere i messaggi dal server
     */
    public ClientHandler(Client client, BufferedReader reader) {
        this.client = client;
        this.reader = reader;
        this.objectMapper = new ObjectMapper();
        this.running = true;
    }
    
    @Override
    public void run() {
        try {
            // Keep reading messages until stopped
            while (running) {
                String messageStr = reader.readLine();
                if (messageStr == null) {
                    // Server disconnected
                    logger.warn("Il server si è disconnesso (EOF)");
                    handleDisconnection("Il server si è disconnesso");
                    break;
                }
                
                // Parse the message
                Message message = objectMapper.readValue(messageStr, Message.class);
                
                // Let the client handle the message
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
     * 
     * @param message Il messaggio di errore da mostrare
     */
    private void handleDisconnection(String message) {
        if (client.isConnected()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, 
                        message, 
                        "Errore di connessione", 
                        JOptionPane.ERROR_MESSAGE);
            });
            client.close();
        }
    }
    
    /**
     * Ferma l'handler.
     */
    public void stop() {
        running = false;
    }
}