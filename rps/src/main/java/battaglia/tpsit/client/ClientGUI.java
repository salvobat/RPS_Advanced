package battaglia.tpsit.client;

import battaglia.tpsit.common.GameMoves;
import battaglia.tpsit.common.GameResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
// import javax.swing.border.*; // Non usato
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Interfaccia grafica per il client.
 */
public class ClientGUI {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);


    // Colori personalizzati
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    //private static final Color WARNING_COLOR = new Color(241, 196, 15); // Non usato
    private static final Color INFO_COLOR = new Color(52, 73, 94);
    private static final Color LIGHT_COLOR = new Color(236, 240, 241);
    
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
    private JEditorPane resultArea;
    private JButton playAgainButton;
    
    // Icone per le mosse
    private Map<GameMoves, ImageIcon> moveIcons = new HashMap<>();
    
    private Client client;
    private GameMoves lastMove;
    
    /**
     * Costruttore per l'interfaccia grafica.
     */
    public ClientGUI() {
        loadIcons();
        initializeGUI();
        applyCustomStyle();
    }

    private GameMoves getLastMove() {
        return lastMove;
    }
    
    private void setLastMove(GameMoves lastMove) {
        this.lastMove = lastMove;
    }
    
    /**
     * Carica le icone per le mosse.
     */
    private void loadIcons() {
        // Define the desired icon size
        int iconWidth = 50;
        int iconHeight = 50;

        // Load and resize custom icons from the resources/img directory
        moveIcons.put(GameMoves.ROCK, resizeIcon("/img/rock.png", iconWidth, iconHeight));
        moveIcons.put(GameMoves.PAPER, resizeIcon("/img/paper.png", iconWidth, iconHeight));
        moveIcons.put(GameMoves.SCISSORS, resizeIcon("/img/scissors.png", iconWidth, iconHeight));
        moveIcons.put(GameMoves.LIZARD, resizeIcon("/img/lizard.png", iconWidth, iconHeight));
        moveIcons.put(GameMoves.SPOCK, resizeIcon("/img/spock.png", iconWidth, iconHeight));
    }

    /**
     * Resizes an icon to the specified width and height.
     *
     * @param path  The path to the image resource.
     * @param width The desired width of the icon.
     * @param height The desired height of the icon.
     * @return The resized ImageIcon.
     */
    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
        Image resizedImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    
    /**
     * Inizializza l'interfaccia grafica.
     */
    private void initializeGUI() {
        // Creazione del frame principale
        frame = new JFrame("Rock Paper Scissors Advanced");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null); // Centra la finestra
        Icon icon = UIManager.getIcon("OptionPane.questionIcon");
        if (icon instanceof ImageIcon) {
            frame.setIconImage(((ImageIcon)icon).getImage());
        }
        
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
     * Applica stili personalizzati ai componenti dell'interfaccia.
     */
    private void applyCustomStyle() {
        try {
            // Applica un look and feel moderno
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Inizializza il pannello di login.
     */
    private void initLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        loginPanel.setBackground(LIGHT_COLOR);
        
        // Titolo con logo
        JLabel titleLabel = new JLabel("Rock Paper Scissors Advanced");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Pannello per l'input username
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.setBackground(LIGHT_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(250, 30));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        // Pannello per il pulsante
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(LIGHT_COLOR);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Arial", Font.BOLD, 14));
        connectButton.setBackground(PRIMARY_COLOR);
        //connectButton.setForeground(Color.WHITE);
        connectButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        connectButton.setFocusPainted(false);
        
        // Label di stato
        statusLabel = new JLabel("Inserisci il tuo username e premi Connect");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(INFO_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Aggiunta componenti
        inputPanel.add(usernameLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(usernameField);
        
        buttonPanel.add(connectButton);
        
        loginPanel.add(Box.createVerticalGlue());
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginPanel.add(inputPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(buttonPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        loginPanel.add(statusLabel);
        loginPanel.add(Box.createVerticalGlue());
        
        // Azione del pulsante Connect
        connectButton.addActionListener(e -> {
            logger.trace("Pulsante premuto: {}", e.getActionCommand());
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                statusLabel.setText("Inserisci un username valido");
                statusLabel.setForeground(DANGER_COLOR);
                return;
            }
            
            connectButton.setEnabled(false);
            statusLabel.setText("Connessione in corso...");
            statusLabel.setForeground(INFO_COLOR);
            
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
                    statusLabel.setForeground(DANGER_COLOR);
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
        waitingPanel.setBackground(LIGHT_COLOR);
        waitingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel waitLabel = new JLabel("In attesa di un avversario...", SwingConstants.CENTER);
        waitLabel.setFont(new Font("Arial", Font.BOLD, 20));
        waitLabel.setForeground(PRIMARY_COLOR);
        
        // Animazione di attesa
        JPanel animationPanel = new JPanel(new BorderLayout());
        animationPanel.setBackground(LIGHT_COLOR);
        
        // Progress bar circolare o animazione
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setBackground(LIGHT_COLOR);
        progressBar.setForeground(PRIMARY_COLOR);
        
        // Pannello centrale per l'animazione
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(LIGHT_COLOR);
        centerPanel.add(progressBar);
        
        animationPanel.add(centerPanel, BorderLayout.CENTER);
        
        waitingPanel.add(waitLabel, BorderLayout.NORTH);
        waitingPanel.add(animationPanel, BorderLayout.CENTER);
    }
    
    /**
     * Inizializza il pannello di gioco.
     */
    private void initGamePanel() {
        gamePanel = new JPanel(new BorderLayout(10, 10));
        gamePanel.setBackground(LIGHT_COLOR);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pannello informazioni partita
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(LIGHT_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        opponentLabel = new JLabel("Stai giocando contro: ");
        opponentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        opponentLabel.setForeground(INFO_COLOR);
        infoPanel.add(opponentLabel);

        // Istruzioni
        JPanel instructionPanel = new JPanel();
        instructionPanel.setBackground(LIGHT_COLOR);

        JLabel instructionLabel = new JLabel("Scegli la tua mossa:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        instructionLabel.setForeground(PRIMARY_COLOR);
        instructionPanel.add(instructionLabel);

        // Pannello per i pulsanti delle mosse
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(LIGHT_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add buttons to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonsPanel.add(rockButton = createMoveButton("Rock", GameMoves.ROCK), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonsPanel.add(paperButton = createMoveButton("Paper", GameMoves.PAPER), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonsPanel.add(scissorsButton = createMoveButton("Scissors", GameMoves.SCISSORS), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        buttonsPanel.add(lizardButton = createMoveButton("Lizard", GameMoves.LIZARD), gbc);

        // Center the "Spock" button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        buttonsPanel.add(spockButton = createMoveButton("Spock", GameMoves.SPOCK), gbc);

        gamePanel.add(infoPanel, BorderLayout.NORTH);
        gamePanel.add(instructionPanel, BorderLayout.CENTER);
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
        button.setIcon(moveIcons.get(move));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(LIGHT_COLOR);
        button.setForeground(INFO_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        button.addActionListener(e -> {
            logger.trace("Pulsante premuto: {}", e.getActionCommand());
            // Disabilita tutti i pulsanti durante l'elaborazione
            setMoveButtonsEnabled(false);
            
            // Invia la mossa al server
            this.setLastMove(move);
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
        resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBackground(LIGHT_COLOR);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Area risultati
        resultArea = new JEditorPane();
        resultArea.setContentType("text/html");
        resultArea.setEditable(false);
        resultArea.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        
        // Pulsante gioca ancora
        playAgainButton = new JButton("Gioca ancora");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 14));
        playAgainButton.setBackground(SUCCESS_COLOR);
        //playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        playAgainButton.addActionListener(e -> {
            logger.trace("Pulsante premuto: {}", e.getActionCommand());
            // Invia al server la richiesta di nuovo turno
            try {
                client.readyForNextRound();
                showPanel("waiting");
                waitForGameStart();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Errore: " + ex.getMessage());
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(LIGHT_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
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
                opponentLabel.setText("Stai giocando contro: " + opponentName);
                
                setMoveButtonsEnabled(true);
                
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
        // Definizione dei colori e stili
        String GREEN = "#2E8B57";
        String RED = "#DC143C";
        String BLUE = "#4682B4";
        String BOLD = "<b>";
        String END_BOLD = "</b>";
        
        // Creazione del contenuto HTML
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; padding: 10px;'>");
        html.append("<h2 style='color: #333; text-align: center;'>Risultato della partita</h2>");
        html.append("<div style='border: 1px solid #ccc; border-radius: 10px; padding: 15px; margin: 10px;'>");
        
        // Mosse dei giocatori
        html.append("<table style='width: 100%; margin-bottom: 15px;'>");
        html.append("<tr><td style='font-weight: bold; width: 50%'>La tua mossa:</td>");
        
        GameMoves yourMove;
        GameMoves opponentMove;
        String resultMessage;
        String resultColor;
        
        if (result.isDraw()) {
            yourMove = this.getLastMove();
            opponentMove = this.getLastMove();
            resultMessage = "È un pareggio!";
            resultColor = BLUE;
        } else if (result.getWinnerUsername().equals(client.getUsername())) {
            yourMove = this.getLastMove();
            opponentMove = result.getOpponentMove();
            resultMessage = "Congratulazioni! Hai vinto!";
            resultColor = GREEN;
        } else {
            yourMove = result.getOpponentMove();
            opponentMove = result.getPlayerMove();
            resultMessage = "Mi dispiace, hai perso!";
            resultColor = RED;
        }
        
        html.append("<td>").append(BOLD).append(yourMove.toString()).append(END_BOLD).append("</td></tr>");
        html.append("<tr><td style='font-weight: bold'>Mossa dell'avversario:</td>");
        html.append("<td>").append(BOLD).append(opponentMove.toString()).append(END_BOLD).append("</td></tr>");
        html.append("</table>");
        
        // Risultato
        html.append("<div style='text-align: center; margin: 20px 0;'>");
        html.append("<h3 style='color: ").append(resultColor).append("; font-size: 18px;'>")
            .append(resultMessage).append("</h3>");
        
        // Descrizione della vittoria
        html.append("<p style='font-style: italic; color: #555;'>").append(result.getWinDescription()).append("</p>");
        html.append("</div></div></body></html>");
        
        // Imposta il testo formattato nell'area risultato
        resultArea.setText(html.toString());
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
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ClientGUI gui = new ClientGUI();
            gui.show();
        });
    }
}