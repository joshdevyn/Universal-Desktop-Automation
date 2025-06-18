package com.automation.mock;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock SAP GUI Application for testing
 * Simulates a real SAP GUI interface for automation testing
 */
public class SAPGUIMock extends JFrame {
    
    private static final String WINDOW_TITLE = "SAP Easy Access - Mock System";
    private static final Color SAP_BLUE = new Color(0, 51, 153);
    private static final Color SAP_LIGHT_BLUE = new Color(230, 240, 255);
    private static final Color SAP_GRAY = new Color(240, 240, 240);
    
    // UI Components
    private JTextField clientField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JTextField languageField;
    private JButton loginButton;
    private JTextField transactionField;
    private JTextArea messageArea;
    private JPanel loginPanel;
    private JPanel mainPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JMenuBar menuBar;
    
    // Application state
    private boolean loggedIn = false;
    private String currentUser = "";
    private String currentClient = "";
    private String currentTransaction = "";
    
    // Mock data
    private Map<String, String> users = new HashMap<>();
    private Map<String, String> transactions = new HashMap<>();
    
    public SAPGUIMock() {
        initializeMockData();
        initializeUI();
        setupEventHandlers();
        showLoginScreen();
    }
    
    private void initializeMockData() {
        // Mock users (client:user:password)
        users.put("100:SAPUSER:password123", "SAP Test User");
        users.put("100:ADMIN:admin123", "SAP Administrator");
        users.put("200:TESTUSER:test123", "Test User");
        
        // Mock transaction codes
        transactions.put("VA01", "Create Sales Order");
        transactions.put("VA02", "Change Sales Order");
        transactions.put("VA03", "Display Sales Order");
        transactions.put("MM01", "Create Material");
        transactions.put("MM02", "Change Material");
        transactions.put("MM03", "Display Material");
        transactions.put("FB01", "Post Document");
        transactions.put("FB02", "Change Document");
        transactions.put("FB03", "Display Document");
        transactions.put("SE11", "ABAP Dictionary");
        transactions.put("SE80", "Object Navigator");
        transactions.put("SM30", "Table Maintenance");
        transactions.put("SPRO", "Customizing");
        transactions.put("SU01", "User Maintenance");
    }
    
    private void initializeUI() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create main layout
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();
        
        // Create login panel
        createLoginPanel();
        
        // Create main panel (hidden initially)
        createMainPanel();
        
        // Create status panel
        createStatusPanel();
        
        // Add panels to frame
        add(loginPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Set SAP look and feel
        getContentPane().setBackground(SAP_GRAY);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(SAP_BLUE);
        
        // System menu
        JMenu systemMenu = new JMenu("System");
        systemMenu.setForeground(Color.WHITE);
        systemMenu.add(new JMenuItem("User Profile"));
        systemMenu.add(new JMenuItem("Services"));
        systemMenu.addSeparator();
        systemMenu.add(new JMenuItem("Log off"));
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.add(new JMenuItem("SAP Library"));
        helpMenu.add(new JMenuItem("Release Notes"));
        
        menuBar.add(systemMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(SAP_LIGHT_BLUE);
        loginPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createBevelBorder(BevelBorder.RAISED),
            "SAP System Logon",
            0, 0, new Font("Arial", Font.BOLD, 14), SAP_BLUE));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // SAP Logo/Title
        JLabel titleLabel = new JLabel("SAP R/3 System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(SAP_BLUE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Client field
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1;
        clientField = new JTextField(10);
        clientField.setText("100");
        loginPanel.add(clientField, gbc);
        
        // User field
        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(new JLabel("User:"), gbc);
        gbc.gridx = 1;
        userField = new JTextField(10);
        loginPanel.add(userField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 3;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(10);
        loginPanel.add(passwordField, gbc);
        
        // Language field
        gbc.gridx = 0; gbc.gridy = 4;
        loginPanel.add(new JLabel("Language:"), gbc);
        gbc.gridx = 1;
        languageField = new JTextField(10);
        languageField.setText("EN");
        loginPanel.add(languageField, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton = new JButton("Log On");
        loginButton.setBackground(SAP_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginPanel.add(loginButton, gbc);
        
        // System info
        gbc.gridy = 6;
        JLabel systemInfo = new JLabel("<html><center>System: PRD<br>Instance: 00<br>Client: 100</center></html>");
        systemInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        systemInfo.setForeground(Color.GRAY);
        loginPanel.add(systemInfo, gbc);
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Top panel with transaction field
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(SAP_LIGHT_BLUE);
        topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        topPanel.add(new JLabel("Transaction:"));
        transactionField = new JTextField(10);
        transactionField.setFont(new Font("Courier", Font.PLAIN, 12));
        topPanel.add(transactionField);
        
        JButton executeButton = new JButton("Execute");
        executeButton.setBackground(SAP_BLUE);
        executeButton.setForeground(Color.WHITE);
        topPanel.add(executeButton);
        
        // Center panel with SAP tree/favorites
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("SAP Easy Access"));
        
        // Create tree structure
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("SAP Easy Access");
        DefaultMutableTreeNode favorites = new DefaultMutableTreeNode("Favorites");
        DefaultMutableTreeNode sapMenu = new DefaultMutableTreeNode("SAP Menu");
        
        // Add some common menu items
        DefaultMutableTreeNode logistics = new DefaultMutableTreeNode("Logistics");
        logistics.add(new DefaultMutableTreeNode("Sales and Distribution"));
        logistics.add(new DefaultMutableTreeNode("Materials Management"));
        logistics.add(new DefaultMutableTreeNode("Production Planning"));
        
        DefaultMutableTreeNode accounting = new DefaultMutableTreeNode("Accounting");
        accounting.add(new DefaultMutableTreeNode("Financial Accounting"));
        accounting.add(new DefaultMutableTreeNode("Controlling"));
        accounting.add(new DefaultMutableTreeNode("Asset Accounting"));
        
        DefaultMutableTreeNode tools = new DefaultMutableTreeNode("Tools");
        tools.add(new DefaultMutableTreeNode("ABAP Workbench"));
        tools.add(new DefaultMutableTreeNode("Administration"));
        tools.add(new DefaultMutableTreeNode("Utilities"));
        
        sapMenu.add(logistics);
        sapMenu.add(accounting);
        sapMenu.add(tools);
        
        root.add(favorites);
        root.add(sapMenu);
        
        JTree tree = new JTree(root);
        tree.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(300, 400));
        
        centerPanel.add(treeScrollPane, BorderLayout.WEST);
        
        // Message area
        messageArea = new JTextArea(15, 40);
        messageArea.setFont(new Font("Courier", Font.PLAIN, 11));
        messageArea.setEditable(false);
        messageArea.setBackground(Color.WHITE);
        messageArea.setBorder(BorderFactory.createTitledBorder("Messages"));
        
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        centerPanel.add(messageScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Setup transaction execution
        executeButton.addActionListener(e -> executeTransaction());
        
        transactionField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executeTransaction();
                }
            }
        });
    }
    
    private void createStatusPanel() {
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(SAP_BLUE);
        statusPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        statusLabel = new JLabel(" Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel timeLabel = new JLabel("System Time: " + java.time.LocalTime.now().toString().substring(0, 8) + " ");
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());
        
        // Enter key on password field triggers login
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        // Setup menu actions
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                setupMenuActions(menu);
            }
        }
    }
    
    private void setupMenuActions(JMenu menu) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if (item != null) {
                item.addActionListener(e -> {
                    if ("Log off".equals(item.getText())) {
                        performLogoff();
                    } else {
                        showMessage("Menu item '" + item.getText() + "' selected");
                    }
                });
            }
        }
    }
    
    private void performLogin() {
        String client = clientField.getText().trim();
        String user = userField.getText().trim();
        String password = new String(passwordField.getPassword());
        String language = languageField.getText().trim();
        
        if (client.isEmpty() || user.isEmpty() || password.isEmpty()) {
            showLoginError("Please fill in all required fields");
            return;
        }
        
        String userKey = client + ":" + user + ":" + password;
        
        if (users.containsKey(userKey)) {
            loggedIn = true;
            currentUser = user;
            currentClient = client;
            
            showMainScreen();
            updateStatus("User " + currentUser + " logged on to client " + currentClient);
            showMessage("Welcome to SAP R/3 System\n" +
                       "User: " + users.get(userKey) + "\n" +
                       "Client: " + client + "\n" +
                       "Language: " + language + "\n" +
                       "System: PRD Instance: 00\n\n" +
                       "Please select a transaction or use the menu.");
        } else {
            showLoginError("Invalid credentials. Try:\nClient: 100, User: SAPUSER, Password: password123");
        }
    }
    
    private void performLogoff() {
        if (!loggedIn) {
            updateStatus("Not currently logged in.");
            return;
        }
        loggedIn = false;
        currentUser = "";
        currentClient = "";
        currentTransaction = "";
        
        // Clear password field
        passwordField.setText("");
        
        showLoginScreen();
        updateStatus("Logged off");
    }
    
    private void executeTransaction() {
        String tCode = transactionField.getText().trim().toUpperCase();
        
        if (tCode.isEmpty()) {
            showMessage("Please enter a transaction code");
            return;
        }
        
        currentTransaction = tCode;
        
        if (transactions.containsKey(tCode)) {
            String transactionName = transactions.get(tCode);
            showMessage("Executing transaction " + tCode + ": " + transactionName + "\n\n" +
                       "This is a mock implementation.\n" +
                       "In a real SAP system, this would open the " + transactionName + " screen.\n\n" +
                       "Available mock transactions:\n" +
                       "VA01 - Create Sales Order\n" +
                       "MM01 - Create Material\n" +
                       "FB01 - Post Document\n" +
                       "SE11 - ABAP Dictionary\n" +
                       "SU01 - User Maintenance");
            
            updateStatus("Transaction " + tCode + " executed");
            
            // Simulate transaction-specific screens
            simulateTransactionScreen(transactionName);
        } else {
            showMessage("Transaction " + tCode + " does not exist or is not available.\n\n" +
                       "Available transactions: " + String.join(", ", transactions.keySet()));
            updateStatus("Invalid transaction: " + tCode);
        }
        
        transactionField.setText("");
    }
    
    private void simulateTransactionScreen(String transactionName) {
        // Create a simple dialog to simulate transaction screen
        JDialog transactionDialog = new JDialog(this, transactionName + " (" + this.currentTransaction + ")", true);
        transactionDialog.setSize(600, 400);
        transactionDialog.setLocationRelativeTo(this);
        
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel(transactionName + " - Mock Screen");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextArea mockContent = new JTextArea();
        mockContent.setText("Transaction: " + this.currentTransaction + "\n" +
                           "Description: " + transactionName + "\n\n" +
                           "This is a mock SAP transaction screen.\n" +
                           "In a real SAP system, this would display\n" +
                           "the actual transaction interface with\n" +
                           "input fields, tables, and buttons.\n\n" +
                           "Mock data and functionality would be\n" +
                           "implemented here for testing purposes.");
        mockContent.setFont(new Font("Courier", Font.PLAIN, 12));
        mockContent.setEditable(false);
        mockContent.setBackground(SAP_LIGHT_BLUE);
        
        JButton closeButton = new JButton("Close (F3)");
        closeButton.addActionListener(e -> transactionDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        
        dialogPanel.add(titleLabel, BorderLayout.NORTH);
        dialogPanel.add(new JScrollPane(mockContent), BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        transactionDialog.add(dialogPanel);
        transactionDialog.setVisible(true);
    }
    
    private void showLoginScreen() {
        if (mainPanel != null) {
            remove(mainPanel);
        }
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        userField.requestFocusInWindow();
    }
    
    private void showMainScreen() {
        remove(loginPanel);
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        transactionField.requestFocusInWindow();
    }
    
    private void showLoginError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
        userField.selectAll();
        userField.requestFocusInWindow();
    }
    
    private void showMessage(String message) {
        messageArea.setText(message);
        messageArea.setCaretPosition(0);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(" " + message);
    }    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SAPGUIMock().setVisible(true);
        });
    }
}
