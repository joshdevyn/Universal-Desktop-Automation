# Multi-Window Process Automation Test with Managed Application Context
# Demonstrates advanced window management for processes with multiple windows
# Use Case: File Explorer with multiple windows, any multi-window application

Feature: Enhanced Multi-Window Process Management - Application Agnostic
  As an automation engineer
  I want to manage applications with multiple windows using managed context
  So that I can interact with specific windows within the same process reliably

  @multi-window @explorer @priority-high @managed-context
  Scenario: File Explorer Multi-Window Management with Managed Context
    When I launch the application at path "C:\Windows\explorer.exe" as "file_explorer"
    Then the application "file_explorer" should be running
    
    # Open a new File Explorer window using managed context
    When I press "CTRL+N" key combination in managed application "file_explorer"
    And I wait 2 seconds
    
    # Switch between windows using managed context
    When I switch to managed application "file_explorer"
    And I take a screenshot of managed application "file_explorer" with name "first_explorer_window"
    
    # Test window management operations
    When I maximize managed application "file_explorer"
    And I take a screenshot of managed application "file_explorer" with name "maximized_explorer"
    When I restore managed application "file_explorer"
    And I take a screenshot of managed application "file_explorer" with name "restored_explorer"
    
    # Capture evidence and close
    And I capture evidence of managed application "file_explorer" with description "Multi-window File Explorer management completed"
    When I terminate the managed application "file_explorer"

  @multi-window @calculator @managed-context @advanced
  Scenario: Multiple Calculator Instances - Multi-Application Management
    # Launch multiple calculator instances with unique names
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calc_instance_1"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "calc_instance_2"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "calc_instance_3"
    
    # Verify all instances are running
    Then the application "calc_instance_1" should be running
    And the application "calc_instance_2" should be running
    And the application "calc_instance_3" should be running
    
    # Perform different calculations in each instance
    When I switch to managed application "calc_instance_1"
    And I type "10+20=" in managed application "calc_instance_1"
    Then I should see the text "30" in managed application "calc_instance_1"
    And I capture evidence of managed application "calc_instance_1" with description "Calculator 1: 10+20=30"
    
    When I switch to managed application "calc_instance_2"
    And I type "50*2=" in managed application "calc_instance_2"
    Then I should see the text "100" in managed application "calc_instance_2"
    And I capture evidence of managed application "calc_instance_2" with description "Calculator 2: 50*2=100"
    
    When I switch to managed application "calc_instance_3"
    And I type "1000/10=" in managed application "calc_instance_3"
    Then I should see the text "100" in managed application "calc_instance_3"
    And I capture evidence of managed application "calc_instance_3" with description "Calculator 3: 1000/10=100"
    
    # Test window arrangement - arrange all windows
    When I move managed application "calc_instance_1" to position 100,100
    And I move managed application "calc_instance_2" to position 400,100
    And I move managed application "calc_instance_3" to position 700,100
    
    # Take screenshot of arrangement
    When I switch to managed application "calc_instance_1"
    And I take a screenshot of managed application "calc_instance_1" with name "calc_arrangement_1"
    When I switch to managed application "calc_instance_2"
    And I take a screenshot of managed application "calc_instance_2" with name "calc_arrangement_2"
    When I switch to managed application "calc_instance_3"
    And I take a screenshot of managed application "calc_instance_3" with name "calc_arrangement_3"
    
    # Clean up all instances
    When I terminate the managed application "calc_instance_1"
    And I terminate the managed application "calc_instance_2"
    And I terminate the managed application "calc_instance_3"

  @multi-window @mixed-apps @integration @managed-context
  Scenario: Mixed Application Multi-Window Integration - Complex Workflow
    # Launch different applications for complex workflow
    When I launch the application at path "C:\Windows\System32\calc.exe" as "workflow_calc"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "workflow_notepad"
    And I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "workflow_paint"
    
    # Verify all applications launched
    Then the application "workflow_calc" should be running
    And the application "workflow_notepad" should be running
    And the application "workflow_paint" should be running
    
    # Create a workflow between applications
    # Step 1: Calculate values
    When I switch to managed application "workflow_calc"
    And I type "150+75=" in managed application "workflow_calc"
    Then I should see the text "225" in managed application "workflow_calc"
    And I capture evidence of managed application "workflow_calc" with description "Workflow calculation: 150+75=225"
    
    # Step 2: Document the calculation
    When I switch to managed application "workflow_notepad"
    And I type "Workflow Documentation" in managed application "workflow_notepad"
    And I press "ENTER" key in managed application "workflow_notepad"
    And I press "ENTER" key in managed application "workflow_notepad"
    And I type "Calculator Result: 150 + 75 = 225" in managed application "workflow_notepad"
    And I press "ENTER" key in managed application "workflow_notepad"
    And I type "Graphics work in progress..." in managed application "workflow_notepad"
    Then I should see the text "Calculator Result: 150 + 75 = 225" in managed application "workflow_notepad"
    And I capture evidence of managed application "workflow_notepad" with description "Workflow documentation created"
    
    # Step 3: Create graphics element
    When I switch to managed application "workflow_paint"
    And I click at coordinates 300, 200 in managed application "workflow_paint"
    And I click at coordinates 400, 300 in managed application "workflow_paint"
    And I capture evidence of managed application "workflow_paint" with description "Workflow graphics interaction"
    
    # Step 4: Arrange windows for final review
    When I move managed application "workflow_calc" to position 50,50
    And I resize managed application "workflow_calc" to 400x300
    When I move managed application "workflow_notepad" to position 500,50
    And I resize managed application "workflow_notepad" to 400x300
    When I move managed application "workflow_paint" to position 300,400
    And I resize managed application "workflow_paint" to 600x300
    
    # Final verification - ensure all applications maintain their state
    When I switch to managed application "workflow_calc"
    Then I should see the text "225" in managed application "workflow_calc"
    
    When I switch to managed application "workflow_notepad"
    Then I should see the text "Graphics work in progress" in managed application "workflow_notepad"
    
    # Capture final state
    And I capture evidence of managed application "workflow_calc" with description "Final calc state verification"
    And I capture evidence of managed application "workflow_notepad" with description "Final notepad state verification"
    And I capture evidence of managed application "workflow_paint" with description "Final paint state verification"
    
    # Clean up entire workflow
    When I terminate the managed application "workflow_calc"
    And I terminate the managed application "workflow_notepad"
    And I terminate the managed application "workflow_paint"

  @multi-window @stress-test @performance @managed-context
  Scenario: Multi-Window Stress Testing - High-Load Management
    # Launch multiple notepad instances for stress testing
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad_1"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad_2"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad_3"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad_4"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad_5"
    
    # Rapid switching and operations
    When I switch to managed application "stress_notepad_1"
    And I type "Stress Test Instance 1" in managed application "stress_notepad_1"
    
    When I switch to managed application "stress_notepad_2"
    And I type "Stress Test Instance 2" in managed application "stress_notepad_2"
    
    When I switch to managed application "stress_notepad_3"
    And I type "Stress Test Instance 3" in managed application "stress_notepad_3"
    
    When I switch to managed application "stress_notepad_4"
    And I type "Stress Test Instance 4" in managed application "stress_notepad_4"
    
    When I switch to managed application "stress_notepad_5"
    And I type "Stress Test Instance 5" in managed application "stress_notepad_5"
    
    # Verify content in each instance
    When I switch to managed application "stress_notepad_1"
    Then I should see the text "Instance 1" in managed application "stress_notepad_1"
    
    When I switch to managed application "stress_notepad_3"
    Then I should see the text "Instance 3" in managed application "stress_notepad_3"
    
    When I switch to managed application "stress_notepad_5"
    Then I should see the text "Instance 5" in managed application "stress_notepad_5"
    
    # Capture stress test evidence
    And I capture evidence of managed application "stress_notepad_1" with description "Stress test - Instance 1"
    And I capture evidence of managed application "stress_notepad_3" with description "Stress test - Instance 3"
    And I capture evidence of managed application "stress_notepad_5" with description "Stress test - Instance 5"
    
    # Clean up all stress test instances
    When I terminate the managed application "stress_notepad_1"
    And I terminate the managed application "stress_notepad_2"
    And I terminate the managed application "stress_notepad_3"
    And I terminate the managed application "stress_notepad_4"
    And I terminate the managed application "stress_notepad_5"
