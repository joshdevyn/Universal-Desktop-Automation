@Excel @Spreadsheet @Productivity @RealApp @pid-driven
Feature: Microsoft Excel Automation - PID-Driven Spreadsheet Management
  As a user of the universal automation framework
  I want to automate Microsoft Excel with PID management
  So that I can prove the framework works with spreadsheet applications and data manipulation

  Background:
    Given the ProcessManager is initialized for Excel operations
    And all previous excel processes are terminated

  @Smoke @BasicFunctionality @DataEntry @pid-driven
  Scenario: Basic Excel Data Entry and Formatting with PID Management
    When I launch the application at path "excel.exe" as "Excel_DataEntry"
    Then the application "Excel_DataEntry" should be running
    And I capture evidence of managed application "Excel_DataEntry" with description "Excel application opened"
    
    # Create new workbook
    When I press "CTRL+N" key combination in managed application "Excel_DataEntry"
    And I wait for 3 seconds
    And I capture evidence of managed application "Excel_DataEntry" with description "New workbook created"
    
    # Enter headers
    When I press "CTRL+HOME" key combination in managed application "Excel_DataEntry"
    And I type "Name" in managed application "Excel_DataEntry"
    And I press "TAB" key in managed application "Excel_DataEntry"
    And I type "Age" in managed application "Excel_DataEntry"
    And I press "TAB" key in managed application "Excel_DataEntry"
    And I type "City" in managed application "Excel_DataEntry"
    And I press "ENTER" key in managed application "Excel_DataEntry"
    And I capture evidence of managed application "Excel_DataEntry" with description "Headers entered"
    
    # Enter first data row
    When I type "John Doe" in managed application "Excel_DataEntry"
    And I press "TAB" key in managed application "Excel_DataEntry"
    And I type "25" in managed application "Excel_DataEntry"
    And I press "TAB" key in managed application "Excel_DataEntry"
    And I type "New York" in managed application "Excel_DataEntry"
    And I press "ENTER" key in managed application "Excel_DataEntry"
    And I capture evidence of managed application "Excel_DataEntry" with description "First data row entered"
    
    # Enter second data row
    When I type "Jane Smith" in managed application "Excel_DataEntry"
    And I press "TAB" key in managed application "Excel_DataEntry"
    And I type "30" in managed application "Excel_DataEntry"
    And I press "TAB" key in managed application "Excel_DataEntry"
    And I type "Los Angeles" in managed application "Excel_DataEntry"
    And I press "ENTER" key in managed application "Excel_DataEntry"
    Then I capture evidence of managed application "Excel_DataEntry" with description "Second data row entered"
    
    # Cleanup
    When I terminate application "Excel_DataEntry"
    Then application "Excel_DataEntry" should have exactly 0 instances

  @Formulas @Calculations @Functions @pid-driven
  Scenario: Formula Creation and Mathematical Operations with PID Management
    When I launch the application at path "excel.exe" as "Excel_Formulas"
    Then the application "Excel_Formulas" should be running
    And I capture evidence of managed application "Excel_Formulas" with description "Excel ready for formulas"
    
    # Create new workbook and set up data
    When I press "CTRL+N" key combination in managed application "Excel_Formulas"
    And I wait for 3 seconds
    And I press "CTRL+HOME" key combination in managed application "Excel_Formulas"
    
    # Enter numbers for calculations
    When I type "10" in managed application "Excel_Formulas"
    And I press "ENTER" key in managed application "Excel_Formulas"
    And I type "20" in managed application "Excel_Formulas"
    And I press "ENTER" key in managed application "Excel_Formulas"
    And I type "30" in managed application "Excel_Formulas"
    And I press "ENTER" key in managed application "Excel_Formulas"
    And I capture evidence of managed application "Excel_Formulas" with description "Numbers entered for formulas"
    
    # Create SUM formula
    When I type "=SUM(A1:A3)" in managed application "Excel_Formulas"
    And I press "ENTER" key in managed application "Excel_Formulas"
    And I capture evidence of managed application "Excel_Formulas" with description "SUM formula created"
    
    # Create AVERAGE formula
    When I type "=AVERAGE(A1:A3)" in managed application "Excel_Formulas"
    And I press "ENTER" key in managed application "Excel_Formulas"
    And I capture evidence of managed application "Excel_Formulas" with description "AVERAGE formula created"
    
    # Create MAX formula
    When I type "=MAX(A1:A3)" in managed application "Excel_Formulas"
    And I press "ENTER" key in managed application "Excel_Formulas"
    Then I capture evidence of managed application "Excel_Formulas" with description "Mathematical formulas completed"
    
    # Cleanup
    When I terminate application "Excel_Formulas"
    Then application "Excel_Formulas" should have exactly 0 instances

  @Charts @DataVisualization @Graphs @pid-driven
  Scenario: Chart Creation and Data Visualization with PID Management
    When I launch the application at path "excel.exe" as "Excel_Charts"
    Then the application "Excel_Charts" should be running
    And I capture evidence of managed application "Excel_Charts" with description "Excel ready for chart creation"
    
    # Create new workbook and enter chart data
    When I press "CTRL+N" key combination in managed application "Excel_Charts"
    And I wait for 3 seconds
    And I press "CTRL+HOME" key combination in managed application "Excel_Charts"
    
    # Enter data for chart
    When I type "Quarter" in managed application "Excel_Charts"
    And I press "TAB" key in managed application "Excel_Charts"
    And I type "Sales" in managed application "Excel_Charts"
    And I press "ENTER" key in managed application "Excel_Charts"
    
    When I type "Q1" in managed application "Excel_Charts"
    And I press "TAB" key in managed application "Excel_Charts"
    And I type "100" in managed application "Excel_Charts"
    And I press "ENTER" key in managed application "Excel_Charts"
    
    When I type "Q2" in managed application "Excel_Charts"
    And I press "TAB" key in managed application "Excel_Charts"
    And I type "150" in managed application "Excel_Charts"
    And I press "ENTER" key in managed application "Excel_Charts"
    
    When I type "Q3" in managed application "Excel_Charts"
    And I press "TAB" key in managed application "Excel_Charts"
    And I type "200" in managed application "Excel_Charts"
    And I press "ENTER" key in managed application "Excel_Charts"
    
    When I type "Q4" in managed application "Excel_Charts"
    And I press "TAB" key in managed application "Excel_Charts"
    And I type "180" in managed application "Excel_Charts"
    And I press "ENTER" key in managed application "Excel_Charts"
    And I capture evidence of managed application "Excel_Charts" with description "Chart data entered"
    
    # Select data range for chart
    When I press "CTRL+HOME" key combination in managed application "Excel_Charts"
    And I press "CTRL+SHIFT+END" key combination in managed application "Excel_Charts"
    And I capture evidence of managed application "Excel_Charts" with description "Data range selected"
    
    # Insert chart using keyboard shortcut
    When I press "ALT+F1" key combination in managed application "Excel_Charts"
    And I wait for 3 seconds
    Then I capture evidence of managed application "Excel_Charts" with description "Chart created successfully"
    
    # Cleanup
    When I terminate application "Excel_Charts"
    Then application "Excel_Charts" should have exactly 0 instances

  @FileOperations @SaveLoad @WorkbookManagement @pid-driven
  Scenario: File Operations and Workbook Management with PID Management
    When I launch the application at path "excel.exe" as "Excel_FileOps"
    Then the application "Excel_FileOps" should be running
    And I capture evidence of managed application "Excel_FileOps" with description "Excel ready for file operations"
    
    # Create new workbook with data
    When I press "CTRL+N" key combination in managed application "Excel_FileOps"
    And I wait for 3 seconds
    And I press "CTRL+HOME" key combination in managed application "Excel_FileOps"
    And I type "Test Data" in managed application "Excel_FileOps"
    And I press "ENTER" key in managed application "Excel_FileOps"
    And I type "Automation Framework" in managed application "Excel_FileOps"
    And I press "ENTER" key in managed application "Excel_FileOps"
    And I capture evidence of managed application "Excel_FileOps" with description "Test data entered"
    
    # Save workbook
    When I press "CTRL+S" key combination in managed application "Excel_FileOps"
    And I wait for 2 seconds
    And I type "TestWorkbook" in managed application "Excel_FileOps"
    And I press "ENTER" key in managed application "Excel_FileOps"
    And I wait for 3 seconds
    And I capture evidence of managed application "Excel_FileOps" with description "Workbook saved"
    
    # Close and reopen
    When I press "CTRL+W" key combination in managed application "Excel_FileOps"
    And I wait for 2 seconds
    And I press "CTRL+O" key combination in managed application "Excel_FileOps"
    And I wait for 2 seconds
    And I type "TestWorkbook" in managed application "Excel_FileOps"
    And I press "ENTER" key in managed application "Excel_FileOps"
    And I wait for 3 seconds
    Then I capture evidence of managed application "Excel_FileOps" with description "Workbook reopened successfully"
    
    # Cleanup
    When I terminate application "Excel_FileOps"
    Then application "Excel_FileOps" should have exactly 0 instances

  @MultiInstance @Excel @Performance @pid-driven
  Scenario: Multi-Instance Excel Management with PID Control
    # Launch multiple Excel instances
    When I launch the application at path "excel.exe" as "Excel_Instance_1"
    And I launch the application at path "excel.exe" as "Excel_Instance_2"
    And I launch the application at path "excel.exe" as "Excel_Instance_3"
    
    Then the application "Excel_Instance_1" should be running
    And the application "Excel_Instance_2" should be running
    And the application "Excel_Instance_3" should be running
    
    # Configure each instance with different data
    When I switch to managed application "Excel_Instance_1"
    And I press "CTRL+N" key combination in managed application "Excel_Instance_1"
    And I wait for 3 seconds
    And I type "Instance 1 Data" in managed application "Excel_Instance_1"
    And I press "ENTER" key in managed application "Excel_Instance_1"
    
    When I switch to managed application "Excel_Instance_2"
    And I press "CTRL+N" key combination in managed application "Excel_Instance_2"
    And I wait for 3 seconds
    And I type "Instance 2 Data" in managed application "Excel_Instance_2"
    And I press "ENTER" key in managed application "Excel_Instance_2"
    
    When I switch to managed application "Excel_Instance_3"
    And I press "CTRL+N" key combination in managed application "Excel_Instance_3"
    And I wait for 3 seconds
    And I type "Instance 3 Data" in managed application "Excel_Instance_3"
    And I press "ENTER" key in managed application "Excel_Instance_3"
    
    # Verify all instances are still running
    Then the application "Excel_Instance_1" should be running
    And the application "Excel_Instance_2" should be running
    And the application "Excel_Instance_3" should be running
    
    And I capture evidence with description "Multi-instance Excel management completed"
    
    # Cleanup all instances
    When I terminate application "Excel_Instance_1"
    And I terminate application "Excel_Instance_2"
    And I terminate application "Excel_Instance_3"
    
    Then application "Excel_Instance_1" should have exactly 0 instances
    And application "Excel_Instance_2" should have exactly 0 instances
    And application "Excel_Instance_3" should have exactly 0 instances
