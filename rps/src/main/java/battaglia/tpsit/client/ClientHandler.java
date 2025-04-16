package battaglia.tpsit.client;

import battaglia.tpsit.common.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

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
                    break; // Server disconnected
                }
                
                // Parse the message
                Message message = objectMapper.readValue(messageStr, Message.class);
                
                // Let the client handle the message
                client.handleMessage(message);
            }
        } catch (IOException e) {
            if (running) {
                logger.error("Errore durante la lettura dei messaggi dal server", e);
            }
        } catch (Exception e) {
            logger.error("Errore imprevisto nel client handler", e);
        } finally {
            stop();
        }
    }
    
    /**
     * Ferma l'handler.
     */
    public void stop() {
        running = false;
    }
}