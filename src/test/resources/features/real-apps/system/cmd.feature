@system @command-prompt @real-app @pid-driven
Feature: Windows Command Prompt Automation - PID-Driven Excellence
  As a quality assurance engineer
  I want to automate Windows Command Prompt using PID-driven process management
  So that I can verify the framework works reliably with command-line applications

  Background:
    Given the ProcessManager is initialized for Command Prompt operations
    And all Command Prompt instances are terminated for clean testing

  @smoke @basic-commands @system-info @pid-driven
  Scenario: Basic Command Execution with PID Management
    When I launch the application at path "C:\Windows\System32\cmd.exe" as "cmd_basic"
    Then the application "cmd_basic" should be running
    And I capture evidence with description "Command Prompt launched successfully"
    
    # Basic system commands
    When I wait for 2 seconds
    And I type "echo Professional automation testing in progress" in managed application "cmd_basic"
    And I press "ENTER" key in managed application "cmd_basic"
    And I wait for 1 seconds
    And I capture evidence with description "Echo command executed"
    
    When I type "dir C:\" in managed application "cmd_basic"
    And I press "ENTER" key in managed application "cmd_basic"
    And I wait for 2 seconds
    And I capture evidence with description "Directory listing completed"
    
    When I type "systeminfo | findstr /C:\"OS Name\"" in managed application "cmd_basic"
    And I press "ENTER" key in managed application "cmd_basic"
    And I wait for 3 seconds
    And I capture evidence with description "System information retrieved"
    
    # Cleanup
    When I terminate the managed application "cmd_basic"
    Then application "cmd_basic" should have exactly 0 instances

  @regression @file-operations @batch-commands @pid-driven
  Scenario: File Operations and Batch Commands with PID Management
    When I launch the application at path "C:\Windows\System32\cmd.exe" as "cmd_file_ops"
    Then the application "cmd_file_ops" should be running
    And I capture evidence with description "Command Prompt ready for file operations"
    
    # Create test directory and file
    When I wait for 2 seconds
    And I type "mkdir automation_test_dir" in managed application "cmd_file_ops"
    And I press "ENTER" key in managed application "cmd_file_ops"
    And I wait for 1 seconds
    
    When I type "cd automation_test_dir" in managed application "cmd_file_ops"
    And I press "ENTER" key in managed application "cmd_file_ops"
    And I wait for 1 seconds
    
    When I type "echo Professional testing content > test_file.txt" in managed application "cmd_file_ops"
    And I press "ENTER" key in managed application "cmd_file_ops"
    And I wait for 1 seconds
    
    When I type "type test_file.txt" in managed application "cmd_file_ops"
    And I press "ENTER" key in managed application "cmd_file_ops"
    And I wait for 2 seconds
    And I capture evidence with description "File operations completed successfully"
    
    # Cleanup test files
    When I type "cd .." in managed application "cmd_file_ops"
    And I press "ENTER" key in managed application "cmd_file_ops"
    And I wait for 1 seconds
    
    When I type "rmdir /s /q automation_test_dir" in managed application "cmd_file_ops"
    And I press "ENTER" key in managed application "cmd_file_ops"
    And I wait for 1 seconds
    And I capture evidence with description "Test directory cleanup completed"
    
    # Cleanup
    When I terminate the managed application "cmd_file_ops"
    Then application "cmd_file_ops" should have exactly 0 instances