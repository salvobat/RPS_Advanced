package battaglia.tpsit;

import java.io.*;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final int PORT = 12345;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server avviato sulla porta {}", PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nuovo client connesso: {}", clientSocket.getInetAddress());
                
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            logger.error("Il server si è spento: ", e);
        }
    }
}

// Gestione del singolo client
class ClientHandler extends Thread {
    private Socket clientSocket;
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Benvenuto nel server RPS Advanced!");
            String message;
            while ((message = in.readLine()) != null) {
                logger.info("Messaggio ricevuto: {}", message);
                out.println("Ricevuto: " + message);
            }
        } catch (IOException e) {
            logger.warn("Il client {} si è disconnesso.", clientSocket.getInetAddress());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error("Errore chiusura socket: ", e);
            }
        }
    }
}
