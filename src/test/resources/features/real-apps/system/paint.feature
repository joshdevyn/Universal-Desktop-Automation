@system @paint @real-app @pid-driven
Feature: Windows Paint Graphics Application Automation - PID-Driven Excellence  
  As a quality assurance engineer
  I want to automate Windows Paint using PID-driven process management
  So that I can verify the framework works reliably with graphics applications

  Background:
    Given the ProcessManager is initialized for Paint operations
    And all Paint instances are terminated for clean testing

  @smoke @basic-drawing @tools @pid-driven
  Scenario: Basic Drawing Tools and Canvas Operations with PID Management
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "paint_drawing"
    Then the application "paint_drawing" should be running
    And I capture evidence with description "Paint application launched successfully"
    
    # Basic drawing operations
    When I wait for 3 seconds
    # Draw horizontal line
    And I click at coordinates 100,150 in managed application "paint_drawing"
    And I drag from coordinates 100,150 to 200,150
    And I wait for 1 seconds
    And I capture evidence with description "Horizontal line drawn"
    
    # Draw diagonal line
    When I click at coordinates 100,180 in managed application "paint_drawing"
    And I drag from coordinates 100,180 to 200,220
    And I wait for 1 seconds
    And I capture evidence with description "Diagonal line drawn"
    
    # Draw rectangle shape
    When I click at coordinates 250,150 in managed application "paint_drawing"
    And I drag from coordinates 250,150 to 350,200
    And I wait for 1 seconds
    And I capture evidence with description "Rectangle drawn on canvas"
    
    # Draw circular shape
    When I click at coordinates 250,220 in managed application "paint_drawing"
    And I drag from coordinates 250,220 to 350,270
    And I wait for 1 seconds
    And I capture evidence with description "Multiple shapes drawn successfully"
    
    # Cleanup
    When I terminate the managed application "paint_drawing"
    Then application "paint_drawing" should have exactly 0 instances

  @regression @file-operations @save-load @pid-driven
  Scenario: Complete File Operations with PID Management
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "paint_file_ops"
    Then the application "paint_file_ops" should be running
    And I capture evidence with description "Paint ready for file operations"
    
    # Create simple artwork
    When I wait for 3 seconds
    And I click at coordinates 100,100 in managed application "paint_file_ops"
    And I drag from coordinates 100,100 to 200,150
    And I click at coordinates 250,100 in managed application "paint_file_ops"
    And I drag from coordinates 250,100 to 350,200
    And I capture evidence with description "Artwork created for file operations"
    
    # Test Save operations
    When I press "CTRL+S" key combination in managed application "paint_file_ops"
    And I wait for 2 seconds
    And I type "paint_automation_test.png" in managed application "paint_file_ops"
    And I press "ENTER" key in managed application "paint_file_ops"
    And I wait for 2 seconds
    
    And I capture evidence with description "File save operation completed"
    
    # Cleanup
    When I terminate the managed application "paint_file_ops"
    Then application "paint_file_ops" should have exactly 0 instances