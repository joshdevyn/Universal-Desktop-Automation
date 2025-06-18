@FileExplorer @WindowsExplorer @FileManagement @RealApp @Basic @pid-driven
Feature: Windows File Explorer Basic Navigation - PID-Driven File Management
  As a user of the universal automation framework
  I want to automate basic Windows File Explorer navigation with PID management
  So that I can prove the framework works with basic file operations and managed application context

  Background:
    Given the ProcessManager is initialized for file explorer operations
    And all previous explorer processes are terminated

  @Smoke @Navigation @BasicFunctionality @pid-driven
  Scenario: Basic File Explorer Navigation with PID Management
    When I launch the application at path "C:\Windows\explorer.exe" as "FileExplorer_Navigation"
    Then the application "FileExplorer_Navigation" should be running
    And I capture evidence of managed application "FileExplorer_Navigation" with description "File Explorer opened"
    
    # Navigate to Documents
    When I press "CTRL+L" key combination in managed application "FileExplorer_Navigation"
    And I wait for 2 seconds
    And I type "%USERPROFILE%\Documents" in managed application "FileExplorer_Navigation"
    And I press "ENTER" key in managed application "FileExplorer_Navigation"
    And I wait for 3 seconds
    Then I capture evidence of managed application "FileExplorer_Navigation" with description "Documents folder opened"
    
    # Navigate to Desktop
    When I press "CTRL+L" key combination in managed application "FileExplorer_Navigation"
    And I type "%USERPROFILE%\Desktop" in managed application "FileExplorer_Navigation"
    And I press "ENTER" key in managed application "FileExplorer_Navigation"
    And I wait for 3 seconds
    Then I capture evidence of managed application "FileExplorer_Navigation" with description "Desktop folder opened"
    
    # Navigate using Alt+Up
    When I press "ALT+UP" key combination in managed application "FileExplorer_Navigation"
    And I wait for 2 seconds
    Then I capture evidence of managed application "FileExplorer_Navigation" with description "Navigated up one level"
    
    # Test back and forward navigation
    When I press "ALT+LEFT" key combination in managed application "FileExplorer_Navigation"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_Navigation" with description "Navigated back"
    
    When I press "ALT+RIGHT" key combination in managed application "FileExplorer_Navigation"
    And I wait for 2 seconds
    Then I capture evidence of managed application "FileExplorer_Navigation" with description "Navigated forward"
    
    # Cleanup
    When I terminate application "FileExplorer_Navigation"
    Then application "FileExplorer_Navigation" should have exactly 0 instances

  @FolderCreation @FileOperations @DirectoryManagement @pid-driven
  Scenario: Folder Creation and Directory Management with PID Control
    When I launch the application at path "C:\Windows\explorer.exe" as "FileExplorer_FolderOps"
    Then the application "FileExplorer_FolderOps" should be running
    And I capture evidence of managed application "FileExplorer_FolderOps" with description "File Explorer ready for folder operations"
    
    # Navigate to Documents folder
    When I press "CTRL+L" key combination in managed application "FileExplorer_FolderOps"
    And I wait for 2 seconds
    And I type "%USERPROFILE%\Documents" in managed application "FileExplorer_FolderOps"
    And I press "ENTER" key in managed application "FileExplorer_FolderOps"
    And I wait for 3 seconds
    Then I capture evidence of managed application "FileExplorer_FolderOps" with description "In Documents for folder operations"
    
    # Create new folder
    When I press "CTRL+SHIFT+N" key combination in managed application "FileExplorer_FolderOps"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_FolderOps" with description "New folder dialog"
    And I type "AutomationTestFolder" in managed application "FileExplorer_FolderOps"
    And I press "ENTER" key in managed application "FileExplorer_FolderOps"
    And I wait for 2 seconds
    Then I capture evidence of managed application "FileExplorer_FolderOps" with description "Test folder created"
    And I should see the text "AutomationTestFolder" using OCR
    
    # Navigate into the folder
    When I press "ENTER" key in managed application "FileExplorer_FolderOps"
    And I wait for 3 seconds
    Then I capture evidence of managed application "FileExplorer_FolderOps" with description "Inside test folder"
    
    # Create subfolder
    When I press "CTRL+SHIFT+N" key combination in managed application "FileExplorer_FolderOps"
    And I wait for 2 seconds
    And I type "SubFolder" in managed application "FileExplorer_FolderOps"
    And I press "ENTER" key in managed application "FileExplorer_FolderOps"
    And I wait for 2 seconds
    Then I capture evidence of managed application "FileExplorer_FolderOps" with description "Subfolder created"
    
    # Navigate back to parent
    When I press "ALT+UP" key combination in managed application "FileExplorer_FolderOps"
    And I wait for 2 seconds
    Then I capture evidence of managed application "FileExplorer_FolderOps" with description "Back to Documents"
    
    # Cleanup
    When I terminate application "FileExplorer_FolderOps"
    Then application "FileExplorer_FolderOps" should have exactly 0 instances

  @ViewModes @DisplayOptions @LayoutManagement @pid-driven
  Scenario: View Modes and Display Options with PID Management
    When I launch the application at path "C:\Windows\explorer.exe" as "FileExplorer_ViewModes"
    Then the application "FileExplorer_ViewModes" should be running
    And I capture evidence of managed application "FileExplorer_ViewModes" with description "File Explorer ready for view mode testing"
    
    # Navigate to Documents folder
    When I press "CTRL+L" key combination in managed application "FileExplorer_ViewModes"
    And I wait for 2 seconds
    And I type "%USERPROFILE%\Documents" in managed application "FileExplorer_ViewModes"
    And I press "ENTER" key in managed application "FileExplorer_ViewModes"
    And I wait for 3 seconds
    Then I capture evidence of managed application "FileExplorer_ViewModes" with description "Documents for view mode testing"
    
    # Test different view modes
    When I press "CTRL+1" key combination in managed application "FileExplorer_ViewModes"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_ViewModes" with description "Extra large icons view"
    
    When I press "CTRL+2" key combination in managed application "FileExplorer_ViewModes"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_ViewModes" with description "Large icons view"
    
    When I press "CTRL+3" key combination in managed application "FileExplorer_ViewModes"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_ViewModes" with description "Medium icons view"
    
    When I press "CTRL+4" key combination in managed application "FileExplorer_ViewModes"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_ViewModes" with description "Small icons view"
    
    When I press "CTRL+5" key combination in managed application "FileExplorer_ViewModes"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_ViewModes" with description "List view"
    
    When I press "CTRL+6" key combination in managed application "FileExplorer_ViewModes"
    And I wait for 2 seconds
    And I capture evidence of managed application "FileExplorer_ViewModes" with description "Details view"
    
    # Test view options completed
    Then I capture evidence of managed application "FileExplorer_ViewModes" with description "All view modes tested successfully"
    
    # Cleanup
    When I terminate application "FileExplorer_ViewModes"
    Then application "FileExplorer_ViewModes" should have exactly 0 instances

  @MultiInstance @FileExplorer @Performance @pid-driven
  Scenario: Multi-Instance File Explorer Management with PID Control
    # Launch multiple File Explorer instances
    When I launch the application at path "C:\Windows\explorer.exe" as "Explorer_Instance_1"
    And I launch the application at path "C:\Windows\explorer.exe" as "Explorer_Instance_2"
    And I launch the application at path "C:\Windows\explorer.exe" as "Explorer_Instance_3"
    
    Then the application "Explorer_Instance_1" should be running
    And the application "Explorer_Instance_2" should be running
    And the application "Explorer_Instance_3" should be running
    
    # Configure each instance differently
    When I switch to managed application "Explorer_Instance_1"
    And I press "CTRL+L" key combination in managed application "Explorer_Instance_1"
    And I type "C:\" in managed application "Explorer_Instance_1"
    And I press "ENTER" key in managed application "Explorer_Instance_1"
    And I wait for 2 seconds
    
    When I switch to managed application "Explorer_Instance_2"
    And I press "CTRL+L" key combination in managed application "Explorer_Instance_2"
    And I type "%USERPROFILE%\Documents" in managed application "Explorer_Instance_2"
    And I press "ENTER" key in managed application "Explorer_Instance_2"
    And I wait for 2 seconds
    
    When I switch to managed application "Explorer_Instance_3"
    And I press "CTRL+L" key combination in managed application "Explorer_Instance_3"
    And I type "%USERPROFILE%\Downloads" in managed application "Explorer_Instance_3"
    And I press "ENTER" key in managed application "Explorer_Instance_3"
    And I wait for 2 seconds
    
    # Verify all instances are configured correctly
    Then the application "Explorer_Instance_1" should be running
    And the application "Explorer_Instance_2" should be running
    And the application "Explorer_Instance_3" should be running
    
    And I capture evidence with description "Multi-instance File Explorer management completed"
    
    # Cleanup all instances
    When I terminate application "Explorer_Instance_1"
    And I terminate application "Explorer_Instance_2"
    And I terminate application "Explorer_Instance_3"
    
    Then application "Explorer_Instance_1" should have exactly 0 instances
    And application "Explorer_Instance_2" should have exactly 0 instances
    And application "Explorer_Instance_3" should have exactly 0 instances
