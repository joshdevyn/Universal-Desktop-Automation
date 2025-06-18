@system @notepad @real-app @pid-driven
Feature: Windows Notepad Text Editor Automation - PID-Driven Revolution
  As a user of the universal automation framework
  I want to automate Windows Notepad using PID-driven process management
  So that I can prove the framework works reliably with any text editor application
  
  Background:
    Given the ProcessManager is initialized for Notepad operations
    And all Notepad instances are terminated for clean testing

  @smoke @text-input @basic-functionality @pid-driven
  Scenario: Basic Text Input and File Operations with PID Management
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad_basic"
    Then the application "notepad_basic" should be running
    And I capture evidence with description "Notepad application launched successfully"
    
    # Text input operations
    When I wait for 3 seconds
    And I type "This is a test document created by the PID-Driven Automation Framework."    And I press "ENTER" key in managed application "notepad_basic"
    And I type "The framework can automate ANY Windows application with enterprise reliability."
    And I press "ENTER" key in managed application "notepad_basic"
    And I type "Date: $(Get-Date)"
    And I press "ENTER" key in managed application "notepad_basic"
    And I type "Process Management: REVOLUTIONARY!"
    
    Then I should see the text "PID-Driven" using OCR
    And I capture evidence with description "Text entered successfully"
      # Save file operations
    When I press "CTRL+S" key combination in managed application "notepad_basic"
    And I wait for 2 seconds
    And I type "automation_test_document.txt"
    And I press "ENTER" key in managed application "notepad_basic"
    And I wait for 2 seconds
    
    Then I should see the text "automation_test_document.txt" using OCR
    And I capture evidence with description "File saved successfully"
    
    # Cleanup
    When I terminate application "notepad_basic"
    Then application "notepad_basic" should have exactly 0 instances

  @regression @file-operations @menu-navigation @pid-driven
  Scenario: Complete File Menu Operations with PID Management
    When I launch the application at path "notepad.exe" as "notepad_file_ops"
    Then the application "notepad_file_ops" should be running
    And I capture evidence with description "Notepad ready for file operations testing"
    
    # Initial content
    When I wait for 3 seconds
    And I type "Testing File Menu Operations with PID-Driven Framework"
    And I capture evidence with description "Initial text entered"
    
    # Test File operations using keyboard shortcuts
    When I press "CTRL+S" key in managed application "notepad_file_ops"
    And I wait for 2 seconds
    And I type "file_operations_test.txt"
    And I press "ENTER" key in managed application "notepad_file_ops"
    And I wait for 2 seconds
    
    # New document test
    When I press "CTRL+N" key in managed application "notepad_file_ops"
    And I wait for 2 seconds
    And I type "New document content for testing PID-driven operations"
    And I capture evidence with description "New document created and content added"
    
    # Cleanup
    When I terminate application "notepad_file_ops"
    Then application "notepad_file_ops" should have exactly 0 instances
    And I capture screenshot with description "New content entered"
    
    # Test File → Save As
    When I press "Ctrl+S" key combination
    And I wait for window with title containing "Save As" for 5 seconds
    And I capture screenshot with description "Save As dialog for new file"
    And I type "new_test_file.txt"
    And I press "Enter" key
    And I wait for 2 seconds
    Then I capture screenshot with description "New file saved"
    
    # Test File → Open
    When I press "Ctrl+O" key combination
    And I wait for window with title containing "Open" for 5 seconds
    And I capture screenshot with description "Open dialog displayed"
    And I type "automation_test_document.txt"
    And I press "Enter" key
    And I wait for 2 seconds
    Then I should see text "Universal Automation Framework" on screen within 5 seconds
    And I capture screenshot with description "Previously saved file opened"

  @edit-menu @text-manipulation @advanced
  Scenario: Edit Menu and Text Manipulation Features
    Given I type "Line 1: This is the first line of text"
    And I press "Enter" key
    And I type "Line 2: This is the second line of text"
    And I press "Enter" key
    And I type "Line 3: This is the third line of text"
    And I capture screenshot with description "Multiple lines entered"
    
    # Test Select All

    And I wait for 1 second
    And I capture screenshot with description "Edit menu opened"

    And I wait for 1 second
    Then I capture screenshot with description "All text selected"
    
    # Test Copy


    And I wait for 1 second
    Then I capture screenshot with description "Text copied to clipboard"
    
    # Test Paste
    When I press "Ctrl+End" key combination
    And I press "Enter" key
    And I type "Pasted content: "


    And I wait for 1 second
    Then I capture screenshot with description "Text pasted successfully"
    
    # Test Find functionality
    When I press "Ctrl+F" key combination
    And I wait for window with title containing "Find" for 5 seconds
    And I capture screenshot with description "Find dialog opened"
    And I type "second line"
    And I press "Enter" key
    And I wait for 1 second
    Then I capture screenshot with description "Text found and highlighted"
    
    # Close find dialog
    When I press "Escape" key
    And I wait for 1 second
    Then I capture screenshot with description "Find dialog closed"

  @format-menu @word-wrap @font-settings
  Scenario: format Menu and Display Options
    Given I type "This is a very long line of text that should demonstrate word wrap functionality when enabled and disabled in Notepad application testing"
    And I capture screenshot with description "Long text entered"
    
    # Test Word Wrap toggle

    And I wait for 1 second
    And I capture screenshot with description "format menu opened"

    And I wait for 1 second
    Then I capture screenshot with description "Word wrap toggled"
    
    # Toggle back


    And I wait for 1 second
    Then I capture screenshot with description "Word wrap toggled back"
    
    # Test Font settings


    And I wait for window with title containing "Font" for 5 seconds
    And I capture screenshot with description "Font dialog opened"
    And I press "Tab" key 3 times
    And I type "14"
    And I press "Enter" key
    And I wait for 2 seconds
    Then I capture screenshot with description "Font size changed"

  @view-menu @status-bar @zoom
  Scenario: View Menu and Display Settings
    Given I type "Testing View menu options and display settings"
    And I capture screenshot with description "Sample text for view testing"
    
    # Test Status Bar toggle

    And I wait for 1 second
    And I capture screenshot with description "View menu opened"

    And I wait for 1 second
    Then I capture screenshot with description "Status bar toggled"
    
    # Toggle back


    And I wait for 1 second
    Then I capture screenshot with description "Status bar toggled back"
    
    # Test Zoom functionality


    And I wait for 1 second
    And I capture screenshot with description "Zoom submenu opened"

    And I wait for 1 second
    Then I capture screenshot with description "Zoomed in"
    
    # Zoom out



    And I wait for 1 second
    Then I capture screenshot with description "Zoomed out"
    
    # Restore default zoom



    And I wait for 1 second
    Then I capture screenshot with description "Default zoom restored"

  @keyboard-shortcuts @power-user @efficiency
  Scenario: Comprehensive Keyboard Shortcuts Testing
    Given I capture screenshot with description "Testing keyboard shortcuts"
    
    # Text entry and basic shortcuts
    When I type "Testing comprehensive keyboard shortcuts in Notepad"
    And I press "Enter" key
    And I type "This line will be selected and manipulated"
    And I capture screenshot with description "Initial text for shortcuts testing"
    
    # Select current line
    When I press "Home" key
    And I press "Shift+End" key combination
    And I capture screenshot with description "Line selected with keyboard"
    
    # Cut and paste
    When I press "Ctrl+X" key combination
    And I wait for 1 second
    And I press "End" key
    And I press "Enter" key
    And I press "Ctrl+V" key combination
    And I wait for 1 second
    Then I capture screenshot with description "Cut and paste completed"
    
    # Undo and Redo
    When I press "Ctrl+Z" key combination
    And I wait for 1 second
    And I capture screenshot with description "Undo perfor med"
    And I press "Ctrl+Y" key combination
    And I wait for 1 second
    Then I capture screenshot with description "Redo perfor med"
    
    # Go to specific position
    When I press "Ctrl+G" key combination
    And I wait for window with title containing "Go To Line" for 5 seconds
    And I capture screenshot with description "Go to line dialog"
    And I type "1"
    And I press "Enter" key
    And I wait for 1 second
    Then I capture screenshot with description "Cursor moved to line 1"

  @data-validation @content-verification @ocr
  Scenario: Text Content Validation and OCR Testing
    Given I type "VALIDATION TEST DOCUMENT"
    And I press "Enter" key
    And I type "Line 1: Testing OCR capabilities"
    And I press "Enter" key
    And I type "Line 2: Mixed CASE text and Numbers 12345"
    And I press "Enter" key
    And I type "Line 3: Special characters !@#$%^&*()"
    And I press "Enter" key
    And I type "Line 4: Date format test 2024-01-15"
    And I capture screenshot with description "Validation content entered"
    
    # Validate specific text exists
    Then I should see text "VALIDATION TEST DOCUMENT" on screen within 5 seconds
    And I should see text "Testing OCR capabilities" on screen within 5 seconds
    And I should see text "12345" on screen within 5 seconds
    And I should see text "!@#$%^&*()" on screen within 5 seconds
    And I should see text "2024-01-15" on screen within 5 seconds
    
    # Validate text in specific regions (if we define text area region)
    When I store current window region as "text_area"
    Then I should see text "Mixed CASE text" in region "text_area" within 5 seconds
    And I should see text "Special characters" in region "text_area" within 5 seconds

  @error-handling @recovery @robustness
  Scenario: Error Handling and Recovery Testing
    Given I type "Testing error handling scenarios"
    And I capture screenshot with description "Base content for error testing"
    
    # Test invalid file operations
    When I press "Ctrl+O" key combination
    And I wait for window with title containing "Open" for 5 seconds
    And I type "nonexistent_file_12345.txt"
    And I press "Enter" key
    And I wait for 3 seconds
    Then I capture screenshot with description "Attempting to open nonexistent file"
    
    # Handle error dialog if it appears
    When I wait for window with title containing "Notepad" for 3 seconds
    And I capture screenshot with description "Error handling result"
    And I press "Escape" key
    And I wait for 1 second
    
    # Verify we can still work with the application
    When I press "Ctrl+A" key combination
    And I type "Recovery test: Application still functional after error"
    And I capture screenshot with description "Application recovered successfully"
    Then I should see text "Recovery test" on screen within 5 seconds

  @performance @large-content @stress
  Scenario: performance and Large Content Handling
    Given I type "performance testing with large content"
    And I press "Enter" key
    And I capture screenshot with description "Starting performance test"
    
    # Generate large content using copy-paste
    And I press "Ctrl+C" key combination
    And I press "End" key
    And I press "Enter" key
    # Paste multiple times to create large content
    And I press "Ctrl+V" key combination
    And I press "Ctrl+V" key combination
    And I press "Ctrl+V" key combination
    And I press "Ctrl+V" key combination
    And I press "Ctrl+V" key combination
    And I capture screenshot with description "Large content generated"
    
    # Test scrolling performance
    When I press "Ctrl+Home" key combination
    And I capture screenshot with description "At beginning of large document"
    And I press "Ctrl+End" key combination
    And I capture screenshot with description "At end of large document"
    
    # Test find in large content
    When I press "Ctrl+F" key combination
    And I wait for window with title containing "Find" for 5 seconds
    And I type "performance testing"
    And I press "Enter" key
    And I wait for 2 seconds
    Then I capture screenshot with description "Found text in large document"
    
    # Close find dialog
    When I press "Escape" key
    And I wait for 1 second
    Then I capture screenshot with description "Large content test completed"

  @accessibility @windows-integration @system-interaction
  Scenario: Accessibility and Windows Integration Testing
    Given I type "Testing Windows accessibility and integration features"
    And I capture screenshot with description "Accessibility test content"
    
    # Test right-click context menu
    And I right-click at current cursor position
    And I wait for 2 seconds
    And I capture screenshot with description "Context menu displayed"
    And I press "Escape" key
    And I wait for 1 second
    
    # Test Alt+Tab integration
    When I press "Alt+Tab" key combination
    And I wait for 2 seconds
    And I capture screenshot with description "Alt+Tab window switching"
    And I press "Alt+Tab" key combination
    And I wait for 1 second
    
    # Test window minimize/restore
    When I press "Alt+Space" key combination
    And I wait for 1 second
    And I capture screenshot with description "System menu opened"
    And I press "Escape" key
    And I wait for 1 second
    
    # Test application title bar

    And I capture screenshot with description "Accessibility testing completed"

  @cleanup @session-end
  Scenario: Application Cleanup and Session Management
    Given I type "Final cleanup test - this document will be closed without saving"
    And I capture screenshot with description "Cleanup test content"
    
    # Test close without save
    When I press "Ctrl+W" key combination
    And I wait for 3 seconds
    And I capture screenshot with description "Close confirmation dialog"
    
    # Don't save changes
    When I press "Tab" key
    And I press "Enter" key
    And I wait for 2 seconds
    Then I capture screenshot with description "Application closed without saving"
    
    # Verify application is closed
    When I wait for 2 seconds
    Then I capture screenshot with description "Final cleanup completed"
    And I set session variable "cleanup_completed" to "true"
