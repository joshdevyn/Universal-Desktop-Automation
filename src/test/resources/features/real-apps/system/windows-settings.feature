@WindowsSettings @SystemSettings @Configuration @RealApp @pid-driven
Feature: Windows Settings Automation - PID-Driven System Configuration
  As a user of the universal automation framework
  I want to automate Windows Settings application with PID management
  So that I can prove the framework works with modern system configuration interfaces

  Background:
    Given the ProcessManager is initialized for Windows Settings operations
    And all previous SystemSettings processes are terminated

  @Smoke @Navigation @BasicFunctionality @pid-driven
  Scenario: Basic Settings Navigation and System Information with PID Management
    When I launch the application at path "ms-settings:" as "WindowsSettings_Navigation"
    Then the application "WindowsSettings_Navigation" should be running
    And I capture evidence of managed application "WindowsSettings_Navigation" with description "Windows Settings opened"
    
    # Navigate through main categories
    When I press "TAB" key in managed application "WindowsSettings_Navigation"
    And I press "TAB" key in managed application "WindowsSettings_Navigation"
    And I press "ENTER" key in managed application "WindowsSettings_Navigation"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Navigation" with description "First settings category accessed"
    
    # Navigate to System settings
    When I press "ALT+LEFT" key combination in managed application "WindowsSettings_Navigation"
    And I wait for 2 seconds
    And I press "DOWN" key in managed application "WindowsSettings_Navigation"
    And I press "ENTER" key in managed application "WindowsSettings_Navigation"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Navigation" with description "System settings accessed"
    
    # Navigate to About section
    When I press "DOWN" key in managed application "WindowsSettings_Navigation"
    And I press "DOWN" key in managed application "WindowsSettings_Navigation"
    And I press "DOWN" key in managed application "WindowsSettings_Navigation"
    And I press "ENTER" key in managed application "WindowsSettings_Navigation"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Navigation" with description "About system information accessed"
    
    # Return to main settings
    When I press "ALT+LEFT" key combination in managed application "WindowsSettings_Navigation"
    And I wait for 2 seconds
    And I press "ALT+LEFT" key combination in managed application "WindowsSettings_Navigation"
    And I wait for 2 seconds
    Then I capture evidence of managed application "WindowsSettings_Navigation" with description "Back to main Settings"
    
    # Cleanup
    When I terminate application "WindowsSettings_Navigation"
    Then application "WindowsSettings_Navigation" should have exactly 0 instances

  @DisplaySettings @MonitorConfiguration @VisualSettings @pid-driven
  Scenario: Display Settings and Monitor Configuration with PID Management
    When I launch the application at path "ms-settings:display" as "WindowsSettings_Display"
    Then the application "WindowsSettings_Display" should be running
    And I capture evidence of managed application "WindowsSettings_Display" with description "Display settings opened directly"
    
    # Navigate through display options
    When I press "TAB" key in managed application "WindowsSettings_Display"
    And I wait for 2 seconds
    And I capture evidence of managed application "WindowsSettings_Display" with description "Display options navigation"
    
    # Access brightness and color settings
    When I press "TAB" key in managed application "WindowsSettings_Display"
    And I press "TAB" key in managed application "WindowsSettings_Display"
    And I press "TAB" key in managed application "WindowsSettings_Display"
    And I wait for 2 seconds
    And I capture evidence of managed application "WindowsSettings_Display" with description "Brightness and color section"
    
    # Navigate through scale and layout
    When I press "TAB" key in managed application "WindowsSettings_Display"
    And I press "TAB" key in managed application "WindowsSettings_Display"
    And I wait for 2 seconds
    Then I capture evidence of managed application "WindowsSettings_Display" with description "Scale and layout section accessed"
    
    # Cleanup
    When I terminate application "WindowsSettings_Display"
    Then application "WindowsSettings_Display" should have exactly 0 instances

  @PersonalizationSettings @Themes @Appearance @pid-driven
  Scenario: Personalization Settings and Theme Management with PID Management
    When I launch the application at path "ms-settings:personalization" as "WindowsSettings_Personalization"
    Then the application "WindowsSettings_Personalization" should be running
    And I capture evidence of managed application "WindowsSettings_Personalization" with description "Personalization settings opened"
    
    # Navigate to Background settings
    When I press "TAB" key in managed application "WindowsSettings_Personalization"
    And I press "ENTER" key in managed application "WindowsSettings_Personalization"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Personalization" with description "Background settings accessed"
    
    # Navigate to Colors
    When I press "ALT+LEFT" key combination in managed application "WindowsSettings_Personalization"
    And I wait for 2 seconds
    And I press "DOWN" key in managed application "WindowsSettings_Personalization"
    And I press "ENTER" key in managed application "WindowsSettings_Personalization"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Personalization" with description "Colors settings accessed"
    
    # Navigate to Themes
    When I press "ALT+LEFT" key combination in managed application "WindowsSettings_Personalization"
    And I wait for 2 seconds
    And I press "DOWN" key in managed application "WindowsSettings_Personalization"
    And I press "DOWN" key in managed application "WindowsSettings_Personalization"
    And I press "ENTER" key in managed application "WindowsSettings_Personalization"
    And I wait for 3 seconds
    Then I capture evidence of managed application "WindowsSettings_Personalization" with description "Themes settings accessed"
    
    # Cleanup
    When I terminate application "WindowsSettings_Personalization"
    Then application "WindowsSettings_Personalization" should have exactly 0 instances

  @NetworkSettings @WiFi @Connectivity @pid-driven
  Scenario: Network Settings and WiFi Management with PID Management
    When I launch the application at path "ms-settings:network" as "WindowsSettings_Network"
    Then the application "WindowsSettings_Network" should be running
    And I capture evidence of managed application "WindowsSettings_Network" with description "Network settings opened"
    
    # Navigate to WiFi settings
    When I press "TAB" key in managed application "WindowsSettings_Network"
    And I press "ENTER" key in managed application "WindowsSettings_Network"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Network" with description "WiFi settings accessed"
    
    # Navigate to Ethernet settings
    When I press "ALT+LEFT" key combination in managed application "WindowsSettings_Network"
    And I wait for 2 seconds
    And I press "DOWN" key in managed application "WindowsSettings_Network"
    And I press "ENTER" key in managed application "WindowsSettings_Network"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Network" with description "Ethernet settings accessed"
    
    # Navigate to Advanced network settings
    When I press "ALT+LEFT" key combination in managed application "WindowsSettings_Network"
    And I wait for 2 seconds
    And I press "DOWN" key in managed application "WindowsSettings_Network"
    And I press "DOWN" key in managed application "WindowsSettings_Network"
    And I press "ENTER" key in managed application "WindowsSettings_Network"
    And I wait for 3 seconds
    Then I capture evidence of managed application "WindowsSettings_Network" with description "Advanced network settings accessed"
    
    # Cleanup
    When I terminate application "WindowsSettings_Network"
    Then application "WindowsSettings_Network" should have exactly 0 instances

  @UpdateSettings @Security @Privacy @pid-driven
  Scenario: Update and Security Settings with PID Management
    When I launch the application at path "ms-settings:windowsupdate" as "WindowsSettings_Update"
    Then the application "WindowsSettings_Update" should be running
    And I capture evidence of managed application "WindowsSettings_Update" with description "Windows Update settings opened"
    
    # Navigate through update options
    When I press "TAB" key in managed application "WindowsSettings_Update"
    And I wait for 2 seconds
    And I capture evidence of managed application "WindowsSettings_Update" with description "Update options navigation"
    
    # Navigate to Security settings
    When I press "CTRL+HOME" key combination in managed application "WindowsSettings_Update"
    And I wait for 2 seconds
    And I press "TAB" key in managed application "WindowsSettings_Update"
    And I press "DOWN" key in managed application "WindowsSettings_Update"
    And I press "ENTER" key in managed application "WindowsSettings_Update"
    And I wait for 3 seconds
    And I capture evidence of managed application "WindowsSettings_Update" with description "Security settings accessed"
    
    # Navigate to Privacy settings
    When I press "ALT+LEFT" key combination in managed application "WindowsSettings_Update"
    And I wait for 2 seconds
    And I press "DOWN" key in managed application "WindowsSettings_Update"
    And I press "ENTER" key in managed application "WindowsSettings_Update"
    And I wait for 3 seconds
    Then I capture evidence of managed application "WindowsSettings_Update" with description "Privacy settings accessed"
    
    # Cleanup
    When I terminate application "WindowsSettings_Update"
    Then application "WindowsSettings_Update" should have exactly 0 instances

  @MultiInstance @WindowsSettings @SystemConfiguration @pid-driven
  Scenario: Multi-Instance Windows Settings Management with PID Control
    # Launch multiple Settings instances for different categories
    When I launch the application at path "ms-settings:display" as "Settings_Display"
    And I launch the application at path "ms-settings:personalization" as "Settings_Personalization"
    And I launch the application at path "ms-settings:network" as "Settings_Network"
    
    Then the application "Settings_Display" should be running
    And the application "Settings_Personalization" should be running
    And the application "Settings_Network" should be running
    
    # Verify each instance shows different settings category
    When I switch to managed application "Settings_Display"
    And I wait for 2 seconds
    And I capture evidence of managed application "Settings_Display" with description "Display settings instance"
    
    When I switch to managed application "Settings_Personalization"
    And I wait for 2 seconds
    And I capture evidence of managed application "Settings_Personalization" with description "Personalization settings instance"
    
    When I switch to managed application "Settings_Network"
    And I wait for 2 seconds
    And I capture evidence of managed application "Settings_Network" with description "Network settings instance"
    
    # Verify all instances are still functional
    Then the application "Settings_Display" should be running
    And the application "Settings_Personalization" should be running
    And the application "Settings_Network" should be running
    
    And I capture evidence with description "Multi-instance Windows Settings management completed"
    
    # Cleanup all instances
    When I terminate application "Settings_Display"
    And I terminate application "Settings_Personalization"
    And I terminate application "Settings_Network"
    
    Then application "Settings_Display" should have exactly 0 instances
    And application "Settings_Personalization" should have exactly 0 instances
    And application "Settings_Network" should have exactly 0 instances
