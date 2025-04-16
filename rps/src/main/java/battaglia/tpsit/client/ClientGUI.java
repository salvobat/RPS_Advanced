package battaglia.tpsit.client;

import battaglia.tpsit.common.GameMoves;
import battaglia.tpsit.common.GameResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;

/**
 * Interfaccia grafica per il client.
 */
public class ClientGUI {
    private static final Logger logger = LoggerFactory.getLogger(ClientGUI.class);
    
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel gamePanel;
    private JPanel waitingPanel;
    private JPanel resultPanel;
    
    private JTextField usernameField;
    private JButton connectButton;
    private JLabel statusLabel;
    private JLabel opponentLabel;
    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;
    private JButton lizardButton;
    private JButton spockButton;
    private JTextArea resultArea;
    private JButton playAgainButton;
    
    private Client client;
    private GameMoves lastMove;
    
    /**
     * Costruttore per l'interfaccia grafica.
     */
    public ClientGUI() {
        initializeGUI();
    }
    
    /**
     * Inizializza l'interfaccia grafica.
     */
    private void initializeGUI() {
        // Creazione del frame principale
        frame = new JFrame("RPS Advanced");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Centra la finestra
        
        // Pannello principale con CardLayout per gestire le diverse schermate
        mainPanel = new JPanel(new CardLayout());
        
        // Inizializzazione dei pannelli
        initLoginPanel();
        initWaitingPanel();
        initGamePanel();
        initResultPanel();
        
        // Aggiunta dei pannelli al mainPanel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(waitingPanel, "waiting");
        mainPanel.add(gamePanel, "game");
        mainPanel.add(resultPanel, "result");
        
        // Mostra il pannello di login all'avvio
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, "login");
        
        // Aggiunta del pannello principale al frame
        frame.add(mainPanel);
        
        // Gestione della chiusura della finestra
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.close();
                }
            }
        });
    }
    
    /**
     * Inizializza il pannello di login.
     */
    private void initLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        connectButton = new JButton("Connect");
        
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(connectButton);
        
        statusLabel = new JLabel("Inserisci il tuo username e premi Connect");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginPanel.add(Box.createVerticalGlue());
        loginPanel.add(inputPanel);
        loginPanel.add(buttonPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(statusLabel);
        loginPanel.add(Box.createVerticalGlue());
        
        // Azione del pulsante Connect
        connectButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                statusLabel.setText("Inserisci un username valido");
                return;
            }
            
            connectButton.setEnabled(false);
            statusLabel.setText("Connessione in corso...");
            
            // Creazione e connessione del client
            client = new Client(username);
            client.connect().thenRun(() -> {
                // Passaggio al pannello di attesa
                SwingUtilities.invokeLater(() -> {
                    showPanel("waiting");
                    waitForGameStart();
                });
            }).exceptionally(ex -> {
                // Gestione errore di connessione
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Errore di connessione: " + ex.getMessage());
                    connectButton.setEnabled(true);
                });
                return null;
            });
        });
    }
    
    /**
     * Inizializza il pannello di attesa.
     */
    private void initWaitingPanel() {
        waitingPanel = new JPanel(new BorderLayout());
        JLabel waitLabel = new JLabel("In attesa di un avversario...", SwingConstants.CENTER);
        waitLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Versione senza immagine
        JPanel animationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel loadingLabel = new JLabel("Caricamento...");
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        animationPanel.add(loadingLabel);
        
        waitingPanel.add(waitLabel, BorderLayout.NORTH);
        waitingPanel.add(animationPanel, BorderLayout.CENTER);
    }
    
    /**
     * Inizializza il pannello di gioco.
     */
    private void initGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        opponentLabel = new JLabel("Giocando contro: ");
        infoPanel.add(opponentLabel);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        rockButton = createMoveButton("Rock", GameMoves.ROCK);
        paperButton = createMoveButton("Paper", GameMoves.PAPER);
        scissorsButton = createMoveButton("Scissors", GameMoves.SCISSORS);
        lizardButton = createMoveButton("Lizard", GameMoves.LIZARD);
        spockButton = createMoveButton("Spock", GameMoves.SPOCK);
        
        buttonsPanel.add(rockButton);
        buttonsPanel.add(paperButton);
        buttonsPanel.add(scissorsButton);
        buttonsPanel.add(lizardButton);
        buttonsPanel.add(spockButton);
        
        JLabel instructionLabel = new JLabel("Scegli la tua mossa:", SwingConstants.CENTER);
        
        gamePanel.add(infoPanel, BorderLayout.NORTH);
        gamePanel.add(instructionLabel, BorderLayout.CENTER);
        gamePanel.add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea un pulsante per una mossa.
     * 
     * @param text Il testo del pulsante
     * @param move La mossa associata al pulsante
     * @return Il pulsante creato
     */
    private JButton createMoveButton(String text, GameMoves move) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            // Disabilita tutti i pulsanti durante l'elaborazione
            setMoveButtonsEnabled(false);
            
            // Invia la mossa al server
            lastMove = move;
            try {
                client.sendMove(move).thenAccept(result -> {
                    // Mostra il risultato nella schermata apposita
                    SwingUtilities.invokeLater(() -> showResult(result));
                }).exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(frame, 
                                "Errore durante l'invio della mossa: " + ex.getMessage(), 
                                "Errore", JOptionPane.ERROR_MESSAGE);
                        setMoveButtonsEnabled(true);
                    });
                    return null;
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, 
                        "Errore durante l'invio della mossa: " + ex.getMessage(), 
                        "Errore", JOptionPane.ERROR_MESSAGE);
                setMoveButtonsEnabled(true);
            }
        });
        return button;
    }
    
    /**
     * Inizializza il pannello dei risultati.
     */
    private void initResultPanel() {
        resultPanel = new JPanel(new BorderLayout());
        
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        playAgainButton = new JButton("Gioca ancora");
        playAgainButton.addActionListener(e -> {
            // Torna alla schermata di gioco per una nuova partita
            setMoveButtonsEnabled(true);
            showPanel("game");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(playAgainButton);
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Attende l'inizio di una partita.
     */
    private void waitForGameStart() {
        client.waitForGameStart().thenAccept(opponentName -> {
            SwingUtilities.invokeLater(() -> {
                // Aggiorna l'etichetta con il nome dell'avversario
                opponentLabel.setText("Giocando contro: " + opponentName);
                
                // Passa alla schermata di gioco
                showPanel("game");
            });
        });
    }
    
    /**
     * Mostra il risultato di una partita.
     * 
     * @param result Il risultato della partita
     */
    private void showResult(GameResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Risultato della partita:\n\n");
        sb.append("La tua mossa: ").append(lastMove).append("\n");
        sb.append("Mossa dell'avversario: ").append(result.getOpponentMove()).append("\n\n");
        
        if (result.isDraw()) {
            sb.append("È un pareggio!\n");
        } else {
            String winner = result.getWinnerUsername();
            if (winner.equals(client.getUsername())) {
                sb.append("Hai vinto!\n");
            } else {
                sb.append("Hai perso!\n");
            }
            sb.append(result.getWinDescription());
        }
        
        resultArea.setText(sb.toString());
        showPanel("result");
    }
    
    /**
     * Mostra un pannello specifico.
     * 
     * @param panelName Il nome del pannello da mostrare
     */
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, panelName);
    }
    
    /**
     * Abilita o disabilita i pulsanti delle mosse.
     * 
     * @param enabled true per abilitare, false per disabilitare
     */
    private void setMoveButtonsEnabled(boolean enabled) {
        rockButton.setEnabled(enabled);
        paperButton.setEnabled(enabled);
        scissorsButton.setEnabled(enabled);
        lizardButton.setEnabled(enabled);
        spockButton.setEnabled(enabled);
    }
    
    /**
     * Mostra l'interfaccia grafica.
     */
    public void show() {
        frame.setVisible(true);
    }
    
    /**
     * Punto di ingresso principale.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.show();
        });
    }
}