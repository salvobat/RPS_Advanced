package battaglia.tpsit;
import java.io.*;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 12345;
    private static final String HANDSHAKE_REQUEST = "HANDSHAKE_REQUEST";
    private static final String HANDSHAKE_ACCEPTED = "HANDSHAKE_ACCEPTED";
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(SERVER_ADDRESS, PORT), 5000);
            logger.info("Connesso al server su {}:{}", SERVER_ADDRESS, PORT);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

                // Invio del messaggio di handshake
                out.println(HANDSHAKE_REQUEST);
                String serverResponse = in.readLine();
                if (!HANDSHAKE_ACCEPTED.equals(serverResponse)) {
                    logger.error("Handshake fallito. Chiusura connessione.");
                    return;
                }
                logger.info("Handshake completato con successo.");

                // Comunicazione normale
                String input;
                while (true) {
                    System.out.print("Inserisci messaggio: ");
                    input = userInput.readLine();
                    if (input == null || input.equalsIgnoreCase("exit")) {
                        logger.info("Disconnessione dal server.");
                        break;
                    }
                    out.println(input);
                    String response = in.readLine();
                    logger.info("Risposta dal server: {}", response);
                }
            }
        } catch (SocketTimeoutException e) {
            logger.error("Connessione al server fallita: timeout raggiunto.");
        } catch (ConnectException e) {
            logger.error("Connessione rifiutata: il server potrebbe non essere attivo.");
        } catch (IOException e) {
            logger.error("Errore di comunicazione con il server.", e);
        }
    }
}
