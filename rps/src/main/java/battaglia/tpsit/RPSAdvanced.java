package battaglia.tpsit;

import battaglia.tpsit.client.ClientGUI;
import battaglia.tpsit.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Classe principale per l'avvio dell'applicazione "Rock Paper Scissors Advanced".
 * Permette di avviare il server o il client tramite argomenti o una finestra di dialogo.
 */
public class RPSAdvanced {
    private static final Logger logger = LoggerFactory.getLogger(RPSAdvanced.class);
    
    /**
     * Punto di ingresso principale dell'applicazione.
     *
     * @param args Argomenti della riga di comando:
     *             <ul>
     *                 <li>{@code --server}: Avvia il server</li>
     *                 <li>{@code --client}: Avvia il client</li>
     *             </ul>
     *             Se nessun argomento è specificato, viene mostrata una finestra di dialogo per scegliere.
     */
    public static void main(String[] args) {
        // Se viene passato l'argomento "--server", avvia solo il server
        if (args.length > 0 && args[0].equals("--server")) {
            startServer();
            return;
        }
        
        // Se viene passato l'argomento "--client", avvia solo il client
        if (args.length > 0 && args[0].equals("--client")) {
            startClient();
            return;
        }
        
        // Altrimenti mostra una finestra di dialogo per scegliere
        SwingUtilities.invokeLater(() -> createSelectionDialog());
    }
    
    /**
     * Crea una finestra di dialogo per scegliere se avviare il server o il client.
     */
    private static void createSelectionDialog() {
        JFrame frame = new JFrame("RPS Advanced");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        
        JLabel label = new JLabel("Seleziona cosa vuoi avviare:", SwingConstants.CENTER);
        
        JButton serverButton = new JButton("Avvia Server");
        serverButton.addActionListener(e -> {
            logger.trace("Pulsante premuto: {}", e.getActionCommand());
            frame.dispose();
            startServer();
        });
        
        JButton clientButton = new JButton("Avvia Client");
        clientButton.addActionListener(e -> {
            logger.trace("Pulsante premuto: {}", e.getActionCommand());
            frame.dispose();
            startClient();
        });
        
        panel.add(label);
        panel.add(serverButton);
        panel.add(clientButton);
        
        frame.add(panel);
        frame.setVisible(true);
    }
    
    /**
     * Avvia il server.
* Crea una semplice interfaccia grafica per monitorare lo stato del server e fermarlo.
     */
    private static void startServer() {
        logger.info("Avvio del server...");
        Server server = new Server();
        
        // Crea una semplice interfaccia grafica per il server
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("RPS Advanced - Server");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            
            JPanel panel = new JPanel(new BorderLayout());
            
            JLabel statusLabel = new JLabel("Server in esecuzione sulla porta 12345", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            JButton stopButton = new JButton("Arresta Server");
            stopButton.addActionListener(e -> {
                logger.trace("Pulsante premuto: {}", e.getActionCommand());
                server.stop();
                statusLabel.setText("Server arrestato");
                stopButton.setEnabled(false);
            });
            
            panel.add(statusLabel, BorderLayout.CENTER);
            panel.add(stopButton, BorderLayout.SOUTH);
            
            frame.add(panel);
            frame.setVisible(true);
        });
        
        // Avvia il server in un thread separato
        new Thread(() -> server.start()).start();
    }
    
    /**
     * Avvia il client.
* Crea e mostra l'interfaccia grafica del client.
     */
    private static void startClient() {
        logger.info("Avvio del client...");
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.show();
        });
    }
}