package battaglia.tpsit;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 6;
    private static final AtomicInteger clientCount = new AtomicInteger(0);
    private static final String HANDSHAKE_REQUEST = "HANDSHAKE_REQUEST";
    private static final String HANDSHAKE_ACCEPTED = "HANDSHAKE_ACCEPTED";
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server avviato sulla porta {}", PORT);
            while (true) {
                if (clientCount.get() >= MAX_CLIENTS) {
                    logger.warn("Numero massimo di client raggiunto. ("+MAX_CLIENTS+").");
                    Thread.sleep(5000);
                    continue;
                }
                Socket clientSocket = serverSocket.accept();
                clientCount.incrementAndGet();
                logger.info("Nuovo client connesso. Client attivi: {}", clientCount.get());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Errore nel server: ", e);
        }
    }

    public static void decrementClientCount() {
        clientCount.decrementAndGet();
    }
    public static int getClientCount() {
        return clientCount.get();
    }
}

class ClientHandler extends Thread {
    private final Socket clientSocket;
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static final String HANDSHAKE_REQUEST = "HANDSHAKE_REQUEST";
    private static final String HANDSHAKE_ACCEPTED = "HANDSHAKE_ACCEPTED";

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Ricezione del messaggio di handshake
            String handshakeMessage = in.readLine();
            if (HANDSHAKE_REQUEST.equals(handshakeMessage)) {
                out.println(HANDSHAKE_ACCEPTED);
                logger.info("Handshake con il client completato con successo.");
            } else {
                logger.error("Handshake fallito. Chiusura connessione.");
                return;
            }

            // Comunicazione normale
            String message;
            while ((message = in.readLine()) != null) {
                logger.info("Messaggio ricevuto: {}", message);
                out.println("ACK: " + message);
            }
        } catch (IOException e) {
            logger.warn("Client disconnesso in modo anomalo.");
        } finally {
            try {
                clientSocket.close();
                Server.decrementClientCount();
                logger.info("Client disconnesso. Client attivi: {}", Server.getClientCount());
            } catch (IOException e) {
                logger.error("Errore chiusura socket: ", e);
            }
        }
    }
}
