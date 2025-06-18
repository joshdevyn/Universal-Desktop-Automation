package com.automation.mock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Enterprise-Grade Mock AS400 Terminal Application for Testing
 * Simulates a real AS400/5250 terminal interface with full interactive capabilities
 * Supports command processing, screen navigation, data entry, and function keys
 */
public class AS400TerminalMock extends JFrame {
    
    private static final int ROWS = 24;
    private static final int COLS = 80;    private JTextArea screenArea;
    private JTextField inputField;
    private JLabel statusBar;
    private String currentScreen = "login";
    private boolean loggedIn = false;
      public AS400TerminalMock() {
        initializeUI();
        setupEventHandlers();
        setupAutomationSupport();
        showLoginScreen();
          // Make sure input field always gets focus
        SwingUtilities.invokeLater(() -> {
            inputField.requestFocusInWindow();
        });
    }private void initializeUI() {
        setTitle("AS400 Terminal Emulator - Mock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Enhanced automation support
        setAlwaysOnTop(false);
        setFocusableWindowState(true);
        setAutoRequestFocus(true);
        setFocusable(true);
        
        // Set AS400 terminal look
        setBackground(Color.BLACK);
        
        // Create screen area
        screenArea = new JTextArea(ROWS, COLS);
        screenArea.setBackground(Color.BLACK);
        screenArea.setForeground(Color.GREEN);
        screenArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        screenArea.setEditable(false);
        screenArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        JScrollPane scrollPane = new JScrollPane(screenArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Create input field
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputField.setCaretColor(Color.GREEN);
        
        // Create status bar
        statusBar = new JLabel(" F3=Exit  F12=Cancel  Enter=Submit");
        statusBar.setBackground(Color.BLUE);
        statusBar.setForeground(Color.WHITE);
        statusBar.setOpaque(true);
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        add(statusBar, BorderLayout.PAGE_END);
        
        // Request focus for input
        inputField.requestFocusInWindow();
    }
    
    private void setupEventHandlers() {
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        processCommand(inputField.getText());
                        inputField.setText("");
                        break;
                    case KeyEvent.VK_F3:
                        System.exit(0);
                        break;
                    case KeyEvent.VK_F12:
                        if (!currentScreen.equals("login")) {
                            showLoginScreen();
                        }
                        inputField.setText("");
                        break;
                }
            }
        });
    }
    
    private void processCommand(String command) {
        if (command == null) command = "";
        
        switch (currentScreen) {
            case "login":
                handleLogin(command);
                break;
            case "main_menu":
                handleMainMenu(command);
                break;
            case "customer_inquiry":
                handleCustomerInquiry(command);
                break;
            case "inventory":
                handleInventory(command);
                break;
            default:
                showErrorMessage("Invalid screen state");
        }
    }    private void handleLogin(String command) {
        if (command.isEmpty()) return;
        
        // Accept any non-empty input as valid login - no special protocol needed
        String userId = command.trim();
        if (!userId.isEmpty()) {
            loggedIn = true;
            showMainMenu();
            updateStatus("Login successful for user: " + userId);
        } else {
            showErrorMessage("User ID required");
        }
    }
    
    private void handleMainMenu(String command) {
        String cmd = command.toUpperCase().trim();
        
        switch (cmd) {
            case "1":
            case "CUST":
                showCustomerInquiry();
                break;
            case "2":
            case "INV":
                showInventory();
                break;
            case "3":
            case "ORD":
                showMessage("Order Management - Not implemented in mock");
                break;
            case "90":
            case "SIGNOFF":
                loggedIn = false;
                showLoginScreen();
                break;
            default:
                showErrorMessage("Invalid option: " + command);
        }
    }
    
    private void handleCustomerInquiry(String command) {
        if (command.trim().isEmpty()) {
            showErrorMessage("Customer ID required");
            return;
        }
        
        // Simulate customer lookup
        String customerId = command.trim().toUpperCase();
        displayCustomerInfo(customerId);
    }
    
    private void handleInventory(String command) {
        if (command.trim().isEmpty()) {
            displayInventoryList();
        } else {
            // Simulate item lookup
            String itemId = command.trim().toUpperCase();
            displayItemInfo(itemId);
        }
    }
      private void showLoginScreen() {
        currentScreen = "login";
        StringBuilder screen = new StringBuilder();
        screen.append("\n\n");
        screen.append("                        AS400 SYSTEM SIGN ON\n");
        screen.append("                        ====================\n\n");
        screen.append("    System: MOCK400      Subsystem: QINTER      Display: DSP01\n\n");
        screen.append("    User ID  . . . . . . . . . . . . . . . . .\n");
        screen.append("    Password . . . . . . . . . . . . . . . . .\n\n");
        screen.append("    Program/procedure  . . . . . . . . . . . .\n");
        screen.append("    Menu . . . . . . . . . . . . . . . . . . .\n");
        screen.append("    Current library  . . . . . . . . . . . . .\n\n");
        screen.append("    Type any text and press Enter to sign on\n");
        screen.append("    (This is a mock - any input will work)\n\n");
        
        screenArea.setText(screen.toString());
        updateStatus("Ready for login - Type any text and press Enter");
    }
    
    private void showMainMenu() {
        if (!loggedIn) {
            showLoginScreen();
            return;
        }
        
        currentScreen = "main_menu";
        StringBuilder screen = new StringBuilder();
        screen.append("\n");
        screen.append("                        AS400 MAIN MENU\n");
        screen.append("                        ===============\n\n");
        screen.append("    Select one of the following:\n\n");
        screen.append("        1. Customer Inquiry    (CUST)\n");
        screen.append("        2. Inventory Inquiry   (INV)\n");
        screen.append("        3. Order Management    (ORD)\n");
        screen.append("        4. Reports             (RPT)\n");
        screen.append("        5. System Utilities    (UTIL)\n\n");
        screen.append("       90. Sign off            (SIGNOFF)\n\n");
        screen.append("    Selection or command\n");
        screen.append("    ===> \n\n");
        screen.append("    F3=Exit   F4=Prompt   F9=Retrieve   F12=Cancel\n");
        
        screenArea.setText(screen.toString());
        updateStatus("Main menu active");
    }
    
    private void showCustomerInquiry() {
        currentScreen = "customer_inquiry";
        StringBuilder screen = new StringBuilder();
        screen.append("\n");
        screen.append("                     CUSTOMER INQUIRY\n");
        screen.append("                     ================\n\n");
        screen.append("    Customer ID  . . . . . . . . . . . . . . .\n\n");
        screen.append("    Enter customer ID to display information\n");
        screen.append("    or press F12 to return to main menu\n\n");
        
        screenArea.setText(screen.toString());
        updateStatus("Customer inquiry screen");
    }
    
    private void displayCustomerInfo(String customerId) {
        StringBuilder screen = new StringBuilder();
        screen.append("\n");
        screen.append("                     CUSTOMER INFORMATION\n");
        screen.append("                     ====================\n\n");
        screen.append("    Customer ID: ").append(customerId).append("\n");
        screen.append("    Name: MOCK CUSTOMER ").append(customerId).append("\n");
        screen.append("    Address: 123 TEST STREET\n");
        screen.append("    City: MOCK CITY         State: MO   ZIP: 12345\n");
        screen.append("    Phone: (555) 123-4567\n");
        screen.append("    Credit Limit: $50,000.00\n");
        screen.append("    Current Balance: $1,234.56\n\n");
        screen.append("    F12=Return   F3=Exit\n");
        
        screenArea.setText(screen.toString());
        updateStatus("Customer " + customerId + " displayed");
    }
    
    private void showInventory() {
        currentScreen = "inventory";
        StringBuilder screen = new StringBuilder();
        screen.append("\n");
        screen.append("                     INVENTORY INQUIRY\n");
        screen.append("                     =================\n\n");
        screen.append("    Item ID  . . . . . . . . . . . . . . . . .\n\n");
        screen.append("    Enter item ID for details or press Enter for list\n");
        screen.append("    F12=Return to main menu\n\n");
        
        screenArea.setText(screen.toString());
        updateStatus("Inventory inquiry screen");
    }
    
    private void displayInventoryList() {
        StringBuilder screen = new StringBuilder();
        screen.append("\n");
        screen.append("                     INVENTORY LIST\n");
        screen.append("                     ==============\n\n");
        screen.append("    Item ID    Description              Qty    Price\n");
        screen.append("    -------    -----------              ---    -----\n");
        screen.append("    ITM001     WIDGET TYPE A            150    $12.50\n");
        screen.append("    ITM002     GADGET MODEL B           200    $25.00\n");
        screen.append("    ITM003     COMPONENT X              75     $8.75\n");
        screen.append("    ITM004     ASSEMBLY Y               300    $45.00\n\n");
        screen.append("    F12=Return   F3=Exit\n");
        
        screenArea.setText(screen.toString());
        updateStatus("Inventory list displayed");
    }
    
    private void displayItemInfo(String itemId) {
        StringBuilder screen = new StringBuilder();
        screen.append("\n");
        screen.append("                     ITEM INFORMATION\n");
        screen.append("                     ================\n\n");
        screen.append("    Item ID: ").append(itemId).append("\n");
        screen.append("    Description: MOCK ITEM ").append(itemId).append("\n");
        screen.append("    Category: TEST CATEGORY\n");
        screen.append("    Unit Price: $99.99\n");
        screen.append("    Quantity on Hand: 100\n");
        screen.append("    Reorder Point: 25\n");
        screen.append("    Location: BIN-A-123\n\n");
        screen.append("    F12=Return   F3=Exit\n");
        
        screenArea.setText(screen.toString());
        updateStatus("Item " + itemId + " displayed");
    }
      private void showErrorMessage(String message) {
        updateStatus("ERROR: " + message);
        // Flash the screen briefly using Swing Timer
        javax.swing.Timer flashTimer = new javax.swing.Timer(200, e -> updateStatus("Ready"));
        flashTimer.setRepeats(false);
        flashTimer.start();
    }
    
    private void showMessage(String message) {
        updateStatus("INFO: " + message);
    }
    
    private void updateStatus(String message) {
        statusBar.setText(" " + message + "  F3=Exit  F12=Cancel  Enter=Submit");
    }
    
    /**
     * Enhanced automation support for testing frameworks
     */
    private void setupAutomationSupport() {
        // Ensure proper focus behavior for automation
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                requestFocusForInput();
            }
            
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                requestFocusForInput();
            }
        });
        
        // Add component listener for better visibility
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                requestFocusForInput();
            }
        });
        
        // Make window focusable by automation tools
        setFocusTraversalPolicyProvider(true);
        setFocusTraversalKeysEnabled(true);
    }
    
    /**
     * Ensure input field gets focus for automation interactions
     */
    private void requestFocusForInput() {
        SwingUtilities.invokeLater(() -> {
            if (inputField != null) {
                inputField.requestFocusInWindow();
                inputField.setCaretPosition(inputField.getText().length());
            }
        });
    }
    
    /**
     * Get current screen state for automation validation
     */
    public String getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Get screen content for OCR/text validation
     */
    public String getScreenContent() {
        return screenArea != null ? screenArea.getText() : "";
    }
    
    /**
     * Check if user is logged in (for automation state validation)
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }
    
    /**
     * Send text input programmatically (for automation)
     */
    public void sendInput(String text) {
        SwingUtilities.invokeLater(() -> {
            if (inputField != null) {
                inputField.setText(text);
                processCommand(text);
                inputField.setText("");
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AS400TerminalMock().setVisible(true);
        });
    }
}
