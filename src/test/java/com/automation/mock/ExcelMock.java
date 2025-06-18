package com.automation.mock;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Simple Excel Mock Application for testing
 * Simulates basic Excel functionality for automation testing
 */
public class ExcelMock extends JFrame {
    
    private static final String WINDOW_TITLE = "Microsoft Excel - Mock Workbook";
    
    // UI Components
    private JTable spreadsheetTable;
    private DefaultTableModel tableModel;
    private JTextField formulaBar;
    private JLabel cellLabel;
    
    public ExcelMock() {
        initializeUI();
        setupEventHandlers();
    }
    
    private void initializeUI() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("New"));
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create formula bar
        JPanel formulaPanel = new JPanel(new BorderLayout());
        cellLabel = new JLabel("A1");
        cellLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        formulaBar = new JTextField();
        formulaPanel.add(cellLabel, BorderLayout.WEST);
        formulaPanel.add(formulaBar, BorderLayout.CENTER);
        
        // Create spreadsheet table
        String[] columns = new String[26];
        for (int i = 0; i < 26; i++) {
            columns[i] = String.valueOf((char) ('A' + i));
        }
        
        tableModel = new DefaultTableModel(columns, 20);
        spreadsheetTable = new JTable(tableModel);
        spreadsheetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(spreadsheetTable);
        
        mainPanel.add(formulaPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void setupEventHandlers() {
        // Basic event handling for cell selection
        spreadsheetTable.getSelectionModel().addListSelectionListener(e -> {
            int row = spreadsheetTable.getSelectedRow();
            int col = spreadsheetTable.getSelectedColumn();
            if (row >= 0 && col >= 0) {
                char column = (char) ('A' + col);
                cellLabel.setText(column + String.valueOf(row + 1));
                Object value = tableModel.getValueAt(row, col);
                formulaBar.setText(value != null ? value.toString() : "");
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ExcelMock().setVisible(true);
        });
    }
}
