@VSCode @IDE @DeveloperTools @RealApp @pid-driven
Feature: Visual Studio Code Automation - PID-Driven IDE Management
  As a user of the universal automation framework
  I want to automate Visual Studio Code with PID management
  So that I can prove the framework works with complex IDEs and development environments

  Background:
    Given the ProcessManager is initialized for VSCode operations
    And all previous Code processes are terminated

  @Smoke @BasicFunctionality @EditorOperations @pid-driven
  Scenario: Basic Editor Operations and File Management with PID Management
    When I launch the application at path "code.exe" as "VSCode_Editor"
    Then the application "VSCode_Editor" should be running
    And I capture evidence of managed application "VSCode_Editor" with description "VS Code opened"
    
    # Create new file
    When I press "CTRL+N" key combination in managed application "VSCode_Editor"
    And I wait for 2 seconds
    And I capture evidence of managed application "VSCode_Editor" with description "New untitled file created"
    
    # Type code content
    When I type "// This is a test file created by automation framework" in managed application "VSCode_Editor"
    And I press "ENTER" key in managed application "VSCode_Editor"
    And I press "ENTER" key in managed application "VSCode_Editor"
    And I type "console.log('Hello from automation!');" in managed application "VSCode_Editor"
    And I capture evidence of managed application "VSCode_Editor" with description "Code typed in editor"
    
    # Save file
    When I press "CTRL+S" key combination in managed application "VSCode_Editor"
    And I wait for 3 seconds
    And I type "automation-test.js" in managed application "VSCode_Editor"
    And I press "ENTER" key in managed application "VSCode_Editor"
    And I wait for 3 seconds
    Then I capture evidence of managed application "VSCode_Editor" with description "File saved as JavaScript"
    
    # Cleanup
    When I terminate application "VSCode_Editor"
    Then application "VSCode_Editor" should have exactly 0 instances

  @Terminal @IntegratedTerminal @CommandExecution @pid-driven
  Scenario: Integrated Terminal Operations with PID Management
    When I launch the application at path "code.exe" as "VSCode_Terminal"
    Then the application "VSCode_Terminal" should be running
    And I capture evidence of managed application "VSCode_Terminal" with description "VS Code ready for terminal operations"
    
    # Create a new file first
    When I press "CTRL+N" key combination in managed application "VSCode_Terminal"
    And I wait for 2 seconds
    And I type "console.log('Terminal test');" in managed application "VSCode_Terminal"
    And I capture evidence of managed application "VSCode_Terminal" with description "File content for terminal test"
    
    # Open integrated terminal
    When I press "CTRL+`" key combination in managed application "VSCode_Terminal"
    And I wait for 3 seconds
    And I capture evidence of managed application "VSCode_Terminal" with description "Integrated terminal opened"
    
    # Execute commands in terminal
    When I type "echo 'Hello from VSCode terminal'" in managed application "VSCode_Terminal"
    And I press "ENTER" key in managed application "VSCode_Terminal"
    And I wait for 2 seconds
    And I capture evidence of managed application "VSCode_Terminal" with description "Command executed in terminal"
    
    # Check directory
    When I type "dir" in managed application "VSCode_Terminal"
    And I press "ENTER" key in managed application "VSCode_Terminal"
    And I wait for 2 seconds
    Then I capture evidence of managed application "VSCode_Terminal" with description "Directory listing in terminal"
    
    # Cleanup
    When I terminate application "VSCode_Terminal"
    Then application "VSCode_Terminal" should have exactly 0 instances

  @Extensions @PluginManagement @IDE @pid-driven
  Scenario: Extension Management and Plugin Operations with PID Management
    When I launch the application at path "code.exe" as "VSCode_Extensions"
    Then the application "VSCode_Extensions" should be running
    And I capture evidence of managed application "VSCode_Extensions" with description "VS Code ready for extension management"
    
    # Open Extensions view
    When I press "CTRL+SHIFT+X" key combination in managed application "VSCode_Extensions"
    And I wait for 3 seconds
    And I capture evidence of managed application "VSCode_Extensions" with description "Extensions view opened"
    
    # Search for extensions
    When I type "prettier" in managed application "VSCode_Extensions"
    And I wait for 3 seconds
    And I capture evidence of managed application "VSCode_Extensions" with description "Extension search performed"
    
    # Go back to Explorer view
    When I press "CTRL+SHIFT+E" key combination in managed application "VSCode_Extensions"
    And I wait for 2 seconds
    And I capture evidence of managed application "VSCode_Extensions" with description "Explorer view restored"
    
    # Open Command Palette
    When I press "CTRL+SHIFT+P" key combination in managed application "VSCode_Extensions"
    And I wait for 2 seconds
    And I type "View: Toggle Terminal" in managed application "VSCode_Extensions"
    And I press "ENTER" key in managed application "VSCode_Extensions"
    And I wait for 2 seconds
    Then I capture evidence of managed application "VSCode_Extensions" with description "Command palette used successfully"
    
    # Cleanup
    When I terminate application "VSCode_Extensions"
    Then application "VSCode_Extensions" should have exactly 0 instances

  @FileOperations @ProjectManagement @WorkspaceManagement @pid-driven
  Scenario: File Operations and Project Management with PID Management
    When I launch the application at path "code.exe" as "VSCode_Projects"
    Then the application "VSCode_Projects" should be running
    And I capture evidence of managed application "VSCode_Projects" with description "VS Code ready for project management"
    
    # Open folder
    When I press "CTRL+K" key combination in managed application "VSCode_Projects"
    And I press "CTRL+O" key combination in managed application "VSCode_Projects"
    And I wait for 3 seconds
    And I type "%USERPROFILE%\Documents" in managed application "VSCode_Projects"
    And I press "ENTER" key in managed application "VSCode_Projects"
    And I wait for 3 seconds
    And I capture evidence of managed application "VSCode_Projects" with description "Folder opened in workspace"
    
    # Create new file in workspace
    When I press "CTRL+N" key combination in managed application "VSCode_Projects"
    And I wait for 2 seconds
    And I type "// Project file created by automation" in managed application "VSCode_Projects"
    And I press "ENTER" key in managed application "VSCode_Projects"
    And I type "function automationTest() {" in managed application "VSCode_Projects"
    And I press "ENTER" key in managed application "VSCode_Projects"
    And I type "    return 'Success';" in managed application "VSCode_Projects"
    And I press "ENTER" key in managed application "VSCode_Projects"
    And I type "}" in managed application "VSCode_Projects"
    And I capture evidence of managed application "VSCode_Projects" with description "Function code created"
    
    # Save the file
    When I press "CTRL+S" key combination in managed application "VSCode_Projects"
    And I wait for 2 seconds
    And I type "automation-function.js" in managed application "VSCode_Projects"
    And I press "ENTER" key in managed application "VSCode_Projects"
    And I wait for 3 seconds
    Then I capture evidence of managed application "VSCode_Projects" with description "Project file saved successfully"
    
    # Cleanup
    When I terminate application "VSCode_Projects"
    Then application "VSCode_Projects" should have exactly 0 instances

  @Debugging @CodeAnalysis @DeveloperFeatures @pid-driven
  Scenario: Debugging and Code Analysis Features with PID Management
    When I launch the application at path "code.exe" as "VSCode_Debug"
    Then the application "VSCode_Debug" should be running
    And I capture evidence of managed application "VSCode_Debug" with description "VS Code ready for debugging features"
    
    # Create a new JavaScript file with code for debugging
    When I press "CTRL+N" key combination in managed application "VSCode_Debug"
    And I wait for 2 seconds
    And I type "function calculateSum(a, b) {" in managed application "VSCode_Debug"
    And I press "ENTER" key in managed application "VSCode_Debug"
    And I type "    const result = a + b;" in managed application "VSCode_Debug"
    And I press "ENTER" key in managed application "VSCode_Debug"
    And I type "    return result;" in managed application "VSCode_Debug"
    And I press "ENTER" key in managed application "VSCode_Debug"
    And I type "}" in managed application "VSCode_Debug"
    And I press "ENTER" key in managed application "VSCode_Debug"
    And I press "ENTER" key in managed application "VSCode_Debug"
    And I type "console.log(calculateSum(5, 3));" in managed application "VSCode_Debug"
    And I capture evidence of managed application "VSCode_Debug" with description "Debug code created"
    
    # Save the file
    When I press "CTRL+S" key combination in managed application "VSCode_Debug"
    And I wait for 2 seconds
    And I type "debug-test.js" in managed application "VSCode_Debug"
    And I press "ENTER" key in managed application "VSCode_Debug"
    And I wait for 3 seconds
    And I capture evidence of managed application "VSCode_Debug" with description "Debug file saved"
    
    # Open Debug view
    When I press "CTRL+SHIFT+D" key combination in managed application "VSCode_Debug"
    And I wait for 3 seconds
    And I capture evidence of managed application "VSCode_Debug" with description "Debug view opened"
    
    # Go back to file explorer
    When I press "CTRL+SHIFT+E" key combination in managed application "VSCode_Debug"
    And I wait for 2 seconds
    Then I capture evidence of managed application "VSCode_Debug" with description "Debug features demonstrated"
    
    # Cleanup
    When I terminate application "VSCode_Debug"
    Then application "VSCode_Debug" should have exactly 0 instances

  @MultiInstance @VSCode @Performance @pid-driven
  Scenario: Multi-Instance VSCode Management with PID Control
    # Launch multiple VSCode instances
    When I launch the application at path "code.exe" as "VSCode_Instance_1"
    And I launch the application at path "code.exe" as "VSCode_Instance_2"
    
    Then the application "VSCode_Instance_1" should be running
    And the application "VSCode_Instance_2" should be running
    
    # Configure first instance
    When I switch to managed application "VSCode_Instance_1"
    And I press "CTRL+N" key combination in managed application "VSCode_Instance_1"
    And I wait for 2 seconds
    And I type "// VSCode Instance 1" in managed application "VSCode_Instance_1"
    And I press "ENTER" key in managed application "VSCode_Instance_1"
    And I type "console.log('Instance 1 active');" in managed application "VSCode_Instance_1"
    And I capture evidence of managed application "VSCode_Instance_1" with description "Instance 1 configured"
    
    # Configure second instance
    When I switch to managed application "VSCode_Instance_2"
    And I press "CTRL+N" key combination in managed application "VSCode_Instance_2"
    And I wait for 2 seconds
    And I type "// VSCode Instance 2" in managed application "VSCode_Instance_2"
    And I press "ENTER" key in managed application "VSCode_Instance_2"
    And I type "console.log('Instance 2 active');" in managed application "VSCode_Instance_2"
    And I capture evidence of managed application "VSCode_Instance_2" with description "Instance 2 configured"
    
    # Switch between instances to verify independent operation
    When I switch to managed application "VSCode_Instance_1"
    And I wait for 2 seconds
    And I switch to managed application "VSCode_Instance_2"
    And I wait for 2 seconds
    
    # Verify both instances are still running
    Then the application "VSCode_Instance_1" should be running
    And the application "VSCode_Instance_2" should be running
    
    And I capture evidence with description "Multi-instance VSCode management completed"
    
    # Cleanup all instances
    When I terminate application "VSCode_Instance_1"
    And I terminate application "VSCode_Instance_2"
    
    Then application "VSCode_Instance_1" should have exactly 0 instances
    And application "VSCode_Instance_2" should have exactly 0 instances
