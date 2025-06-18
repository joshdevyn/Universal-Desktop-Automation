Feature: Managed Application Context - Application Agnostic Testing
  As a Quality Assurance Engineer
  I want to test applications using managed application context
  So that I have reliable, deterministic control over application lifecycle management

  @managed-context @explicit @calculator @smoke
  Scenario: Calculator with Managed Context - Application Agnostic Management
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    Then the application "calculator" should be running
    
    # Execute mathematical operation using keyboard input
    When I type "123" in managed application "calculator"
    And I type "+" in managed application "calculator"
    And I type "456" in managed application "calculator"
    And I type "=" in managed application "calculator"
    
    # Verify calculation result using OCR text recognition
    Then I should see the text "579" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Managed context calculator addition result"
    
    # Close application using managed context
    When I terminate the managed application "calculator"

  @managed-context @explicit @notepad @smoke  
  Scenario: Notepad with Managed Context - Application Agnostic Text Editor Testing
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    Then the application "notepad" should be running
    
    # Type test content
    When I type "Hello from managed context!" in managed application "notepad"
    And I press "ENTER" key in managed application "notepad"
    And I type "Testing application agnostic framework." in managed application "notepad"
    
    # Verify text content using OCR
    Then I should see the text "Hello from managed context!" in managed application "notepad"
    And I should see the text "application agnostic framework" in managed application "notepad"
    And I capture evidence of managed application "notepad" with description "Managed context text input verification"
    
    # Test keyboard shortcuts
    When I press "CTRL+A" key combination in managed application "notepad"
    And I press "CTRL+C" key combination in managed application "notepad"
    And I press "CTRL+END" key combination in managed application "notepad"
    And I press "ENTER" key in managed application "notepad"
    And I press "CTRL+V" key combination in managed application "notepad"
    
    # Verify copied content
    And I capture evidence of managed application "notepad" with description "Copy-paste operations completed"
    
    # Close application
    When I terminate the managed application "notepad"

  @managed-context @explicit @paint @visual
  Scenario: Paint Application with Managed Context - Application Agnostic Graphics Testing
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "paint"
    Then the application "paint" should be running
    
    # Maximize window for better testing
    When I maximize managed application "paint"
    And I take a screenshot of managed application "paint" with name "paint_maximized"
    
    # Test clicking in drawing area (center coordinates)
    When I click at coordinates 400, 300 in managed application "paint"
    And I capture evidence of managed application "paint" with description "Paint application interaction"
    
    # Close application
    When I terminate the managed application "paint"

  @managed-context @explicit @explorer @file-system
  Scenario: File Explorer with Managed Context - Application Agnostic File Management
    When I launch the application at path "C:\Windows\explorer.exe" as "explorer"
    Then the application "explorer" should be running
    
    # Take screenshot of file explorer
    When I take a screenshot of managed application "explorer" with name "file_explorer_opened"
    And I capture evidence of managed application "explorer" with description "File Explorer managed context testing"
    
    # Test window management
    When I maximize managed application "explorer"
    And I wait 2 seconds
    When I restore managed application "explorer"
    And I wait 2 seconds
    
    # Close application
    When I terminate the managed application "explorer"

  @managed-context @explicit @multi-app @integration
  Scenario: Multi-Application Workflow - Application Agnostic Integration Testing
    # Launch multiple applications in managed context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    Then the application "calculator" should be running
    And the application "notepad" should be running
    
    # Test switching between applications
    When I switch to managed application "calculator"
    And I type "42" in managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "calc_with_42"
    
    When I switch to managed application "notepad"
    And I type "Calculator shows: 42" in managed application "notepad"
    And I take a screenshot of managed application "notepad" with name "notepad_with_text"
    
    # Verify both applications maintained their state
    When I switch to managed application "calculator"
    Then I should see the text "42" in managed application "calculator"
    
    When I switch to managed application "notepad"
    Then I should see the text "Calculator shows: 42" in managed application "notepad"
    
    # Capture integration evidence
    And I capture evidence of managed application "notepad" with description "Multi-application state preservation verified"
    
    # Clean up - close both applications
    When I terminate the managed application "calculator"
    And I terminate the managed application "notepad"

  @managed-context @explicit @process-registration @advanced
  Scenario: External Process Registration - Enterprise Process Management
    # Register a manually launched process as managed application
    When I launch the application at path "C:\Windows\System32\calc.exe" as "temp_calc"
    And I wait 3 seconds
    When I register the running process "Calculator.exe" as managed application "registered_calc"
    Then the application "registered_calc" should be running
    
    # Test the registered application
    When I switch to managed application "registered_calc"
    And I type "99" in managed application "registered_calc"
    And I type "+" in managed application "registered_calc"
    And I type "1" in managed application "registered_calc"
    And I type "=" in managed application "registered_calc"
    Then I should see the text "100" in managed application "registered_calc"
    And I capture evidence of managed application "registered_calc" with description "Registered process calculation result"
    
    # Clean up
    When I terminate the managed application "registered_calc"
