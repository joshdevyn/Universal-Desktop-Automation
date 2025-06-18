@TaskManager @SystemMonitoring @ProcessManagement @RealApp @pid-driven
Feature: Windows Task Manager Automation - PID-Driven System Monitoring
  As a user of the universal automation framework
  I want to automate Windows Task Manager with PID management
  So that I can prove the framework works with system monitoring and process management tools

  Background:
    Given the ProcessManager is initialized for task manager operations
    And all previous taskmgr processes are terminated

  @Smoke @Processes @BasicFunctionality @pid-driven
  Scenario: Basic Process Monitoring with PID Management
    When I launch the application at path "C:\Windows\System32\taskmgr.exe" as "TaskManager_Basic"
    Then the application "TaskManager_Basic" should be running
    And I capture evidence of managed application "TaskManager_Basic" with description "Task Manager opened"
    
    # Navigate to Processes tab
    When I press "CTRL+1" key combination in managed application "TaskManager_Basic"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Basic" with description "Processes tab active"
    Then I should see the text "Processes" using OCR
    
    # Navigate through different tabs
    When I press "CTRL+2" key combination in managed application "TaskManager_Basic"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Basic" with description "Performance tab accessed"
    
    When I press "CTRL+3" key combination in managed application "TaskManager_Basic"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Basic" with description "App history tab accessed"
    
    When I press "CTRL+4" key combination in managed application "TaskManager_Basic"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Basic" with description "Startup tab accessed"
    
    # Return to Processes tab
    When I press "CTRL+1" key combination in managed application "TaskManager_Basic"
    And I wait for 2 seconds
    Then I capture evidence of managed application "TaskManager_Basic" with description "Returned to Processes tab"
    
    # Cleanup
    When I terminate application "TaskManager_Basic"
    Then application "TaskManager_Basic" should have exactly 0 instances

  @ProcessDetails @DetailedView @ResourceMonitoring @pid-driven
  Scenario: Detailed Process Information and Resource Monitoring with PID Control
    When I launch the application at path "C:\Windows\System32\taskmgr.exe" as "TaskManager_Details"
    Then the application "TaskManager_Details" should be running
    And I capture evidence of managed application "TaskManager_Details" with description "Task Manager ready for detailed monitoring"
    
    # Navigate to Details tab for more detailed process information
    When I press "CTRL+5" key combination in managed application "TaskManager_Details"
    And I wait for 3 seconds
    And I capture evidence of managed application "TaskManager_Details" with description "Details tab accessed"
    Then I should see the text "PID" using OCR
    
    # Navigate to Performance tab for system metrics
    When I press "CTRL+2" key combination in managed application "TaskManager_Details"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Details" with description "Performance metrics displayed"
    
    # Check CPU performance
    When I press "TAB" key in managed application "TaskManager_Details"
    And I press "ENTER" key in managed application "TaskManager_Details"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Details" with description "CPU performance selected"
    
    # Check Memory performance
    When I press "DOWN" key in managed application "TaskManager_Details"
    And I press "ENTER" key in managed application "TaskManager_Details"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Details" with description "Memory performance selected"
    
    # Check Disk performance
    When I press "DOWN" key in managed application "TaskManager_Details"
    And I press "ENTER" key in managed application "TaskManager_Details"
    And I wait for 2 seconds
    Then I capture evidence of managed application "TaskManager_Details" with description "Disk performance monitoring completed"
    
    # Cleanup
    When I terminate application "TaskManager_Details"
    Then application "TaskManager_Details" should have exactly 0 instances

  @Services @ServiceManagement @SystemServices @pid-driven
  Scenario: Services Management and System Service Monitoring with PID Management
    When I launch the application at path "C:\Windows\System32\taskmgr.exe" as "TaskManager_Services"
    Then the application "TaskManager_Services" should be running
    And I capture evidence of managed application "TaskManager_Services" with description "Task Manager ready for services management"
    
    # Navigate to Services tab
    When I press "CTRL+6" key combination in managed application "TaskManager_Services"
    And I wait for 3 seconds
    And I capture evidence of managed application "TaskManager_Services" with description "Services tab accessed"
    
    # Navigate through services list
    When I press "TAB" key in managed application "TaskManager_Services"
    And I wait for 1 seconds
    And I press "DOWN" key in managed application "TaskManager_Services"
    And I press "DOWN" key in managed application "TaskManager_Services"
    And I press "DOWN" key in managed application "TaskManager_Services"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Services" with description "Navigated through services list"
    
    # Sort services by status
    When I press "TAB" key in managed application "TaskManager_Services"
    And I press "TAB" key in managed application "TaskManager_Services"
    And I press "ENTER" key in managed application "TaskManager_Services"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Services" with description "Services sorted by status"
    
    # Sort services by name
    When I press "SHIFT+TAB" key combination in managed application "TaskManager_Services"
    And I press "ENTER" key in managed application "TaskManager_Services"
    And I wait for 2 seconds
    Then I capture evidence of managed application "TaskManager_Services" with description "Services sorted by name"
    
    # Cleanup
    When I terminate application "TaskManager_Services"
    Then application "TaskManager_Services" should have exactly 0 instances

  @Users @UserSessions @ResourceUsage @pid-driven
  Scenario: User Sessions and Resource Usage Monitoring with PID Management
    When I launch the application at path "C:\Windows\System32\taskmgr.exe" as "TaskManager_Users"
    Then the application "TaskManager_Users" should be running
    And I capture evidence of managed application "TaskManager_Users" with description "Task Manager ready for user monitoring"
    
    # Navigate to Users tab
    When I press "CTRL+7" key combination in managed application "TaskManager_Users"
    And I wait for 3 seconds
    And I capture evidence of managed application "TaskManager_Users" with description "Users tab accessed"
    
    # Check current user session information
    When I press "TAB" key in managed application "TaskManager_Users"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Users" with description "User session information displayed"
    
    # Navigate back to Processes for final verification
    When I press "CTRL+1" key combination in managed application "TaskManager_Users"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskManager_Users" with description "Returned to Processes tab for final check"
    
    # Check overall system resource usage
    When I press "CTRL+2" key combination in managed application "TaskManager_Users"
    And I wait for 2 seconds
    Then I capture evidence of managed application "TaskManager_Users" with description "Final system resource overview"
    
    # Cleanup
    When I terminate application "TaskManager_Users"
    Then application "TaskManager_Users" should have exactly 0 instances

  @MultiInstance @TaskManager @SystemMonitoring @pid-driven
  Scenario: Multi-Instance Task Manager for Advanced System Monitoring with PID Control
    # Launch multiple Task Manager instances for comprehensive monitoring
    When I launch the application at path "C:\Windows\System32\taskmgr.exe" as "TaskMgr_Instance_1"
    And I launch the application at path "C:\Windows\System32\taskmgr.exe" as "TaskMgr_Instance_2"
    
    Then the application "TaskMgr_Instance_1" should be running
    And the application "TaskMgr_Instance_2" should be running
    
    # Configure first instance for Processes monitoring
    When I switch to managed application "TaskMgr_Instance_1"
    And I press "CTRL+1" key combination in managed application "TaskMgr_Instance_1"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskMgr_Instance_1" with description "Instance 1 - Processes monitoring"
    
    # Configure second instance for Performance monitoring
    When I switch to managed application "TaskMgr_Instance_2"
    And I press "CTRL+2" key combination in managed application "TaskMgr_Instance_2"
    And I wait for 2 seconds
    And I capture evidence of managed application "TaskMgr_Instance_2" with description "Instance 2 - Performance monitoring"
    
    # Switch between instances to verify independent operation
    When I switch to managed application "TaskMgr_Instance_1"
    And I wait for 2 seconds
    And I switch to managed application "TaskMgr_Instance_2"
    And I wait for 2 seconds
    
    # Verify both instances are still running and functional
    Then the application "TaskMgr_Instance_1" should be running
    And the application "TaskMgr_Instance_2" should be running
    
    And I capture evidence with description "Multi-instance Task Manager monitoring completed"
    
    # Cleanup all instances
    When I terminate application "TaskMgr_Instance_1"
    And I terminate application "TaskMgr_Instance_2"
    
    Then application "TaskMgr_Instance_1" should have exactly 0 instances
    And application "TaskMgr_Instance_2" should have exactly 0 instances
