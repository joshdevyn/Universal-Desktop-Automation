@Word @MicrosoftOffice @DocumentProcessing @Productivity @RealApp @pid-driven
Feature: Microsoft Word Automation - PID-Driven Document Processing
  As a user of the universal automation framework
  I want to automate Microsoft Word with PID management
  So that I can prove the framework works with complex document processing applications

  Background:
    Given the ProcessManager is initialized for Word operations
    And all previous winword processes are terminated

  @Smoke @DocumentCreation @BasicFunctionality @pid-driven
  Scenario: Basic Document Creation and Text Operations with PID Management
    When I launch the application at path "winword.exe" as "Word_DocumentCreation"
    Then the application "Word_DocumentCreation" should be running
    And I capture evidence of managed application "Word_DocumentCreation" with description "Microsoft Word opened"
    
    # Create new blank document
    When I press "CTRL+N" key combination in managed application "Word_DocumentCreation"
    And I wait for 3 seconds
    And I capture evidence of managed application "Word_DocumentCreation" with description "New blank document created"
    
    # Type some text
    When I type "This is a test document created by the automation framework." in managed application "Word_DocumentCreation"
    And I wait for 2 seconds
    And I capture evidence of managed application "Word_DocumentCreation" with description "Text typed in document"
    
    # Apply basic formatting - make text bold
    When I press "CTRL+A" key combination in managed application "Word_DocumentCreation"
    And I wait for 1 seconds
    And I press "CTRL+B" key combination in managed application "Word_DocumentCreation"
    And I wait for 1 seconds
    And I capture evidence of managed application "Word_DocumentCreation" with description "Text made bold"
    
    # Add new line and more text
    When I press "CTRL+END" key combination in managed application "Word_DocumentCreation"
    And I press "ENTER" key in managed application "Word_DocumentCreation"
    And I type "This is the second paragraph with normal formatting." in managed application "Word_DocumentCreation"
    And I wait for 2 seconds
    And I capture evidence of managed application "Word_DocumentCreation" with description "Second paragraph added"
    
    # Apply italic formatting to second paragraph
    When I press "CTRL+SHIFT+LEFT" key combination in managed application "Word_DocumentCreation"
    And I press "CTRL+SHIFT+LEFT" key combination in managed application "Word_DocumentCreation"
    And I press "CTRL+SHIFT+LEFT" key combination in managed application "Word_DocumentCreation"
    And I wait for 1 seconds
    And I press "CTRL+I" key combination in managed application "Word_DocumentCreation"
    And I wait for 1 seconds
    Then I capture evidence of managed application "Word_DocumentCreation" with description "Second paragraph italicized"
    
    # Cleanup
    When I terminate application "Word_DocumentCreation"
    Then application "Word_DocumentCreation" should have exactly 0 instances

  @Formatting @TextStyling @AdvancedFormatting @pid-driven
  Scenario: Advanced Text Formatting and Styling with PID Management
    When I launch the application at path "winword.exe" as "Word_Formatting"
    Then the application "Word_Formatting" should be running
    And I capture evidence of managed application "Word_Formatting" with description "Word ready for advanced formatting"
    
    # Create new document
    When I press "CTRL+N" key combination in managed application "Word_Formatting"
    And I wait for 3 seconds
    
    # Create title
    When I type "AUTOMATION FRAMEWORK DOCUMENT" in managed application "Word_Formatting"
    And I press "ENTER" key in managed application "Word_Formatting"
    And I press "ENTER" key in managed application "Word_Formatting"
    And I capture evidence of managed application "Word_Formatting" with description "Title entered"
    
    # Format title - select and center
    When I press "CTRL+HOME" key combination in managed application "Word_Formatting"
    And I press "CTRL+SHIFT+END" key combination in managed application "Word_Formatting"
    And I press "CTRL+E" key combination in managed application "Word_Formatting"
    And I wait for 1 seconds
    And I capture evidence of managed application "Word_Formatting" with description "Title centered"
    
    # Make title larger
    When I press "CTRL+SHIFT+>" key combination in managed application "Word_Formatting"
    And I press "CTRL+SHIFT+>" key combination in managed application "Word_Formatting"
    And I press "CTRL+SHIFT+>" key combination in managed application "Word_Formatting"
    And I wait for 1 seconds
    And I capture evidence of managed application "Word_Formatting" with description "Title size increased"
    
    # Add body text
    When I press "CTRL+END" key combination in managed application "Word_Formatting"
    And I press "CTRL+L" key combination in managed application "Word_Formatting"
    And I type "This document demonstrates the advanced formatting capabilities of the automation framework when working with Microsoft Word." in managed application "Word_Formatting"
    And I press "ENTER" key in managed application "Word_Formatting"
    And I press "ENTER" key in managed application "Word_Formatting"
    And I capture evidence of managed application "Word_Formatting" with description "Body text added"
    
    # Add bullet points
    When I press "CTRL+SHIFT+L" key combination in managed application "Word_Formatting"
    And I type "First bullet point" in managed application "Word_Formatting"
    And I press "ENTER" key in managed application "Word_Formatting"
    And I type "Second bullet point" in managed application "Word_Formatting"
    And I press "ENTER" key in managed application "Word_Formatting"
    And I type "Third bullet point" in managed application "Word_Formatting"
    And I press "ENTER" key in managed application "Word_Formatting"
    Then I capture evidence of managed application "Word_Formatting" with description "Bullet points created"
    
    # Cleanup
    When I terminate application "Word_Formatting"
    Then application "Word_Formatting" should have exactly 0 instances

  @Tables @DataOrganization @StructuredContent @pid-driven
  Scenario: Table Creation and Data Organization with PID Management
    When I launch the application at path "winword.exe" as "Word_Tables"
    Then the application "Word_Tables" should be running
    And I capture evidence of managed application "Word_Tables" with description "Word ready for table creation"
    
    # Create new document
    When I press "CTRL+N" key combination in managed application "Word_Tables"
    And I wait for 3 seconds
    
    # Insert table using keyboard
    When I press "ALT+N" key combination in managed application "Word_Tables"
    And I wait for 1 seconds
    And I press "T" key in managed application "Word_Tables"
    And I wait for 1 seconds
    And I press "I" key in managed application "Word_Tables"
    And I wait for 2 seconds
    And I capture evidence of managed application "Word_Tables" with description "Table insertion dialog opened"
    
    # Accept default table size and create table
    When I press "ENTER" key in managed application "Word_Tables"
    And I wait for 2 seconds
    And I capture evidence of managed application "Word_Tables" with description "Table created"
    
    # Enter table headers
    When I type "Name" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    And I type "Department" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    And I type "Role" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    And I capture evidence of managed application "Word_Tables" with description "Table headers entered"
    
    # Enter table data
    When I type "John Smith" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    And I type "Engineering" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    And I type "Developer" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    
    When I type "Jane Doe" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    And I type "QA" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    And I type "Tester" in managed application "Word_Tables"
    And I press "TAB" key in managed application "Word_Tables"
    Then I capture evidence of managed application "Word_Tables" with description "Table data entered"
    
    # Cleanup
    When I terminate application "Word_Tables"
    Then application "Word_Tables" should have exactly 0 instances

  @FileOperations @DocumentManagement @SaveLoad @pid-driven
  Scenario: File Operations and Document Management with PID Management
    When I launch the application at path "winword.exe" as "Word_FileOps"
    Then the application "Word_FileOps" should be running
    And I capture evidence of managed application "Word_FileOps" with description "Word ready for file operations"
    
    # Create new document with content
    When I press "CTRL+N" key combination in managed application "Word_FileOps"
    And I wait for 3 seconds
    And I type "Test Document for File Operations" in managed application "Word_FileOps"
    And I press "ENTER" key in managed application "Word_FileOps"
    And I type "This document will be saved and reopened to test file operations." in managed application "Word_FileOps"
    And I press "ENTER" key in managed application "Word_FileOps"
    And I capture evidence of managed application "Word_FileOps" with description "Document content created"
    
    # Save document
    When I press "CTRL+S" key combination in managed application "Word_FileOps"
    And I wait for 2 seconds
    And I type "TestDocument" in managed application "Word_FileOps"
    And I press "ENTER" key in managed application "Word_FileOps"
    And I wait for 3 seconds
    And I capture evidence of managed application "Word_FileOps" with description "Document saved"
    
    # Close document
    When I press "CTRL+W" key combination in managed application "Word_FileOps"
    And I wait for 2 seconds
    And I capture evidence of managed application "Word_FileOps" with description "Document closed"
    
    # Reopen document
    When I press "CTRL+O" key combination in managed application "Word_FileOps"
    And I wait for 2 seconds
    And I type "TestDocument" in managed application "Word_FileOps"
    And I press "ENTER" key in managed application "Word_FileOps"
    And I wait for 3 seconds
    Then I capture evidence of managed application "Word_FileOps" with description "Document reopened successfully"
    
    # Cleanup
    When I terminate application "Word_FileOps"
    Then application "Word_FileOps" should have exactly 0 instances

  @MultiInstance @Word @Performance @pid-driven
  Scenario: Multi-Instance Word Management with PID Control
    # Launch multiple Word instances
    When I launch the application at path "winword.exe" as "Word_Instance_1"
    And I launch the application at path "winword.exe" as "Word_Instance_2"
    
    Then the application "Word_Instance_1" should be running
    And the application "Word_Instance_2" should be running
    
    # Configure first instance
    When I switch to managed application "Word_Instance_1"
    And I press "CTRL+N" key combination in managed application "Word_Instance_1"
    And I wait for 3 seconds
    And I type "Document 1 - Instance 1" in managed application "Word_Instance_1"
    And I press "ENTER" key in managed application "Word_Instance_1"
    And I capture evidence of managed application "Word_Instance_1" with description "Instance 1 configured"
    
    # Configure second instance
    When I switch to managed application "Word_Instance_2"
    And I press "CTRL+N" key combination in managed application "Word_Instance_2"
    And I wait for 3 seconds
    And I type "Document 2 - Instance 2" in managed application "Word_Instance_2"
    And I press "ENTER" key in managed application "Word_Instance_2"
    And I capture evidence of managed application "Word_Instance_2" with description "Instance 2 configured"
    
    # Switch between instances to verify independent operation
    When I switch to managed application "Word_Instance_1"
    And I wait for 2 seconds
    And I switch to managed application "Word_Instance_2"
    And I wait for 2 seconds
    
    # Verify both instances are still running
    Then the application "Word_Instance_1" should be running
    And the application "Word_Instance_2" should be running
    
    And I capture evidence with description "Multi-instance Word management completed"
    
    # Cleanup all instances
    When I terminate application "Word_Instance_1"
    And I terminate application "Word_Instance_2"
    
    Then application "Word_Instance_1" should have exactly 0 instances
    And application "Word_Instance_2" should have exactly 0 instances
