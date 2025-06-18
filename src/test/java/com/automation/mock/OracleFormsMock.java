package com.automation.mock;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock Oracle Forms Application for testing
 * Simulates a real Oracle Forms interface for automation testing
 */
public class OracleFormsMock extends JFrame {
    
    private static final String WINDOW_TITLE = "Oracle Forms - Order Entry System";
    private static final Color FORMS_BLUE = new Color(0, 102, 204);
    private static final Color FORMS_GRAY = new Color(240, 240, 240);
    private static final Color FORMS_WHITE = new Color(255, 255, 255);
    
    // UI Components
    private JPanel loginPanel;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextField orderNumberField;
    private JTextField customerField;
    private JTextField customerNameField;
    private JTextField itemCodeField;
    private JTextField itemDescField;    private JTextField quantityField;
    private JTextField lineTotalField;
    private JLabel statusLabel;
    private JLabel recordCounterLabel;
    private JTable orderLinesTable;
    private DefaultTableModel tableModel;
    
    // Application state
    private boolean loggedIn = false;
    private String currentUser = "";
    private String currentMode = "READY";
    private int recordCount = 0;
    
    // Mock data
    private Map<String, String> users = new HashMap<>();
    private Map<String, String> customers = new HashMap<>();
    private Map<String, String> items = new HashMap<>();
    private Map<String, Order> orders = new HashMap<>();
    
    public OracleFormsMock() {
        initializeMockData();
        initializeUI();
        setupEventHandlers();
        showLoginScreen();
    }
    
    private void initializeMockData() {
        // Mock users
        users.put("TESTUSER", "password123");
        users.put("ADMIN_USER", "admin123");
        users.put("READONLY_USER", "readonly123");
        
        // Mock customers
        customers.put("ACME", "ACME Corporation");
        customers.put("BETA", "BETA Corp");
        customers.put("GAMMA", "GAMMA Inc");
        
        // Mock items
        items.put("ITEM-001", "Widget A");
        items.put("ITEM-002", "Widget B");
        items.put("ITEM-003", "Widget C");
    }
    
    private void initializeUI() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 700);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create login panel
        createLoginPanel();
        
        // Create main panel
        createMainPanel();
        
        // Create status panel
        createStatusPanel();
        
        // Initial layout
        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(FORMS_BLUE);
        
        JMenu systemMenu = new JMenu("System");
        systemMenu.setForeground(Color.WHITE);
        systemMenu.add(new JMenuItem("About"));
        systemMenu.add(new JMenuItem("Exit"));
        
        JMenu actionMenu = new JMenu("Action");
        actionMenu.setForeground(Color.WHITE);
        actionMenu.add(new JMenuItem("Save"));
        actionMenu.add(new JMenuItem("Query"));
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.add(new JMenuItem("Oracle Forms Help"));
        
        menuBar.add(systemMenu);
        menuBar.add(actionMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(FORMS_GRAY);
        loginPanel.setBorder(BorderFactory.createTitledBorder("Oracle Forms Login"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Oracle Forms banner
        JLabel bannerLabel = new JLabel("Oracle Forms", SwingConstants.CENTER);
        bannerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bannerLabel.setForeground(FORMS_BLUE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(bannerLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton = new JButton("Login");
        loginButton.setBackground(FORMS_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginPanel.add(loginButton, gbc);
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(FORMS_WHITE);
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(FORMS_GRAY);
        toolbar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        JLabel toolbarLabel = new JLabel("Order Entry Form");
        toolbarLabel.setFont(new Font("Arial", Font.BOLD, 12));
        toolbar.add(toolbarLabel);
        
        // Order header block
        JPanel headerBlock = createOrderHeaderBlock();
        
        // Order lines block
        JPanel linesBlock = createOrderLinesBlock();
        
        // Combine blocks
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(headerBlock, BorderLayout.NORTH);
        formPanel.add(linesBlock, BorderLayout.CENTER);
        
        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
    }
    
    private JPanel createOrderHeaderBlock() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Order Header"));
        headerPanel.setBackground(FORMS_WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Order Number
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("Order Number:"), gbc);
        gbc.gridx = 1;
        orderNumberField = new JTextField(15);
        headerPanel.add(orderNumberField, gbc);
        
        // Customer
        gbc.gridx = 0; gbc.gridy = 1;
        headerPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        customerField = new JTextField(10);
        headerPanel.add(customerField, gbc);
        gbc.gridx = 2;
        customerNameField = new JTextField(20);
        customerNameField.setEditable(false);
        customerNameField.setBackground(FORMS_GRAY);
        headerPanel.add(customerNameField, gbc);
        
        return headerPanel;
    }
    
    private JPanel createOrderLinesBlock() {
        JPanel linesPanel = new JPanel(new BorderLayout());
        linesPanel.setBorder(BorderFactory.createTitledBorder("Order Lines"));
        
        // Create table
        String[] columns = {"Item Code", "Description", "Quantity", "Unit Price", "Line Total"};
        tableModel = new DefaultTableModel(columns, 0);
        orderLinesTable = new JTable(tableModel);
        orderLinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(orderLinesTable);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        
        // Input fields for new lines
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Item:"));
        itemCodeField = new JTextField(10);
        inputPanel.add(itemCodeField);
        
        inputPanel.add(new JLabel("Description:"));
        itemDescField = new JTextField(15);
        itemDescField.setEditable(false);
        itemDescField.setBackground(FORMS_GRAY);
        inputPanel.add(itemDescField);
        
        inputPanel.add(new JLabel("Qty:"));
        quantityField = new JTextField(5);
        inputPanel.add(quantityField);
        
        inputPanel.add(new JLabel("Total:"));
        lineTotalField = new JTextField(8);
        lineTotalField.setEditable(false);
        lineTotalField.setBackground(FORMS_GRAY);
        inputPanel.add(lineTotalField);
        
        linesPanel.add(scrollPane, BorderLayout.CENTER);
        linesPanel.add(inputPanel, BorderLayout.SOUTH);
        
        return linesPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(FORMS_BLUE);
        statusPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        statusLabel = new JLabel(" Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        recordCounterLabel = new JLabel("Record 0 of 0 ");
        recordCounterLabel.setForeground(Color.WHITE);
        recordCounterLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel functionKeysLabel = new JLabel("F6=Create F7=Query F8=Execute F10=Save F11=Delete ");
        functionKeysLabel.setForeground(Color.WHITE);
        functionKeysLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(functionKeysLabel, BorderLayout.CENTER);
        statusPanel.add(recordCounterLabel, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());
        
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        // Setup main form handlers when logged in
        setupFormHandlers();
    }
    
    private void setupFormHandlers() {
        // Customer field validation
        customerField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    validateCustomer();
                }
            }
        });
        
        // Item field validation
        itemCodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    validateItem();
                }
            }
        });
        
        // Quantity calculation
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    calculateLineTotal();
                }
            }
        });
        
        // Function keys
        KeyListener functionKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleFunctionKey(e.getKeyCode());
            }
        };
        
        // Add function key listener to all components
        addFunctionKeyListener(mainPanel, functionKeyListener);
    }
    
    private void addFunctionKeyListener(Container container, KeyListener listener) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField || component instanceof JTable) {
                component.addKeyListener(listener);
            }
            if (component instanceof Container) {
                addFunctionKeyListener((Container) component, listener);
            }
        }
    }
    
    private void handleFunctionKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_F6:
                createNewRecord();
                break;
            case KeyEvent.VK_F7:
                enterQueryMode();
                break;
            case KeyEvent.VK_F8:
                executeQuery();
                break;
            case KeyEvent.VK_F10:
                saveRecord();
                break;
            case KeyEvent.VK_F11:
                deleteRecord();
                break;
        }
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (users.containsKey(username) && users.get(username).equals(password)) {
            loggedIn = true;
            currentUser = username;
            showMainScreen();
            updateStatus("Welcome");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showLoginScreen() {
        remove(mainPanel);
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        usernameField.requestFocusInWindow();
    }
    
    private void showMainScreen() {
        remove(loginPanel);
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        orderNumberField.requestFocusInWindow();
        updateStatus("Enter Query");
        currentMode = "ENTER_QUERY";
    }
    
    private void validateCustomer() {
        String customerCode = customerField.getText().trim().toUpperCase();
        if (customers.containsKey(customerCode)) {
            customerNameField.setText(customers.get(customerCode));
        } else if (!customerCode.isEmpty()) {
            customerNameField.setText("");
            updateStatus("FRM-40202: Invalid customer code");
        }
    }
    
    private void validateItem() {
        String itemCode = itemCodeField.getText().trim().toUpperCase();
        if (items.containsKey(itemCode)) {
            itemDescField.setText(items.get(itemCode));
        } else if (!itemCode.isEmpty()) {
            itemDescField.setText("");
            updateStatus("FRM-40202: Invalid item code");
        }
    }
    
    private void calculateLineTotal() {
        try {
            String qtyText = quantityField.getText().trim();
            if (!qtyText.isEmpty()) {
                int qty = Integer.parseInt(qtyText);
                double unitPrice = 10.00; // Mock unit price
                double total = qty * unitPrice;
                lineTotalField.setText(String.format("%.2f", total));
            }
        } catch (NumberFormatException e) {
            updateStatus("FRM-40202: Invalid quantity");
        }
    }
    
    private void createNewRecord() {
        clearFields();
        recordCount = 1;
        updateRecordCounter();
        updateStatus("Ready");
        currentMode = "INSERT";
    }
    
    private void enterQueryMode() {
        clearFields();
        updateStatus("Enter Query");
        currentMode = "ENTER_QUERY";
    }
    
    private void executeQuery() {
        String orderNumber = orderNumberField.getText().trim();
        if (orderNumber.isEmpty()) {
            updateStatus("FRM-40301: Query caused no records to be retrieved");
            recordCount = 0;
        } else if (orders.containsKey(orderNumber)) {
            // Load existing order
            Order order = orders.get(orderNumber);
            loadOrder(order);
            updateStatus("FRM-40350: Query complete: 1 records retrieved");
            recordCount = 1;
        } else {
            updateStatus("FRM-40301: Query caused no records to be retrieved");
            recordCount = 0;
        }
        updateRecordCounter();
        currentMode = "READY";
    }
    
    private void saveRecord() {
        if (validateRecord()) {
            String orderNumber = orderNumberField.getText().trim();
            if (!orderNumber.isEmpty()) {
                // Save order
                Order order = new Order();
                order.orderNumber = orderNumber;
                order.customer = customerField.getText().trim();
                order.customerName = customerNameField.getText().trim();
                orders.put(orderNumber, order);
                
                updateStatus("FRM-40400: Transaction complete");
                currentMode = "READY";
            }
        }
    }
    
    private void deleteRecord() {
        String orderNumber = orderNumberField.getText().trim();
        if (!orderNumber.isEmpty() && orders.containsKey(orderNumber)) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                orders.remove(orderNumber);
                clearFields();
                updateStatus("Record deleted");
                recordCount = 0;
                updateRecordCounter();
            }
        }
    }
    
    private boolean validateRecord() {
        if (orderNumberField.getText().trim().isEmpty()) {
            updateStatus("FRM-40200: Field must be entered");
            orderNumberField.requestFocusInWindow();
            return false;
        }
        return true;
    }
    
    private void loadOrder(Order order) {
        orderNumberField.setText(order.orderNumber);
        customerField.setText(order.customer);
        customerNameField.setText(order.customerName);
    }
    
    private void clearFields() {
        orderNumberField.setText("");
        customerField.setText("");
        customerNameField.setText("");
        itemCodeField.setText("");
        itemDescField.setText("");
        quantityField.setText("");
        lineTotalField.setText("");
        tableModel.setRowCount(0);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(" " + message);
    }
    
    private void updateRecordCounter() {
        recordCounterLabel.setText(String.format("Record %d of %d ", 
            recordCount > 0 ? 1 : 0, recordCount));
    }
    
    // Getter methods for external access (used by test automation)
    public boolean isLoggedIn() {
        return loggedIn;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public String getCurrentMode() {
        return currentMode;
    }
    
    // Simple Order class for mock data
    private static class Order {
        String orderNumber;
        String customer;
        String customerName;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OracleFormsMock().setVisible(true);
        });
    }
}
