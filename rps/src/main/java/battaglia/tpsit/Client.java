package battaglia.tpsit;

import java.io.*;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // IP locale
    private static final int PORT = 12345;
    private static final int RETRY_INTERVAL = 3000; // 3 secondi
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        while (true) {
            try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

                logger.info("Connesso al server {} sulla porta {}", SERVER_ADDRESS, PORT);
                System.out.println(in.readLine()); // Messaggio di benvenuto

                String message;
                while (true) {
                    System.out.print("Inserisci un messaggio: ");
                    message = consoleInput.readLine();
                    if ("exit".equalsIgnoreCase(message)) {
                        logger.info("Client chiuso dall'utente.");
                        return;
                    }
                    out.println(message); // Invia al server
                    String response = in.readLine(); // Attende risposta
                    if (response == null) { 
                        logger.warn("Connessione persa con il server.");
                        break; 
                    }
                    System.out.println("Server: " + response);
                }
            } catch (IOException e) {
                logger.error("Connessione al server fallita. Riprovo in {} ms...", RETRY_INTERVAL);
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
