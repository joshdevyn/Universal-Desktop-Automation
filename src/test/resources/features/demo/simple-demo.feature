@live-demo-2025 @recording-session @framework-showcase @no-ocr-simple
Feature: Live Recording Demo - Explorer Based Automation
  As a demo presenter
  I want to showcase core automation capabilities via Windows Explorer
  So that stakeholders can see the framework's practical applications

  @demo-calc @wild-west-calculator @raw-key-sending @live-recording-2025
  Scenario: Wild West Calculator - Raw Key Sending Without Management
    When I launch the application at path "C:\Windows\System32\calc.exe" as "CalculatorLauncher"
    And I wait 3 seconds
    Then the process "CalculatorApp.exe" should be running
    And I take a screenshot with name "wild_west_calc_opened"
    
    # Perform basic calculation using raw key sending (whatever window has focus)
    When I type text "123"
    And I take a screenshot with name "wild_west_number_123"
    And I type text "+"
    And I type text "456"
    And I take a screenshot with name "wild_west_before_calculation"
    And I type text "="
    And I wait 2 seconds
    And I take a screenshot with name "wild_west_addition_result"
    
    # Clear and perform another operation using raw keys
    When I press key "ESCAPE"
    And I type text "999"
    And I type text "*"
    And I type text "8"
    And I type text "="
    And I wait 2 seconds
    And I take a screenshot with name "wild_west_multiplication_result"
    
    # Close calculator using raw key sending
    When I press key combination "ALT+F4"
    And I wait 1 seconds

  @demo-cmd @explorer-to-command-prompt @live-recording-2025
  Scenario: Launch CMD via Windows Explorer and Execute Commands
    When I launch the application at path "C:\WINDOWS\explorer.exe" as "DemoExplorer"
    And I wait 2 seconds
    And I take a screenshot of managed application "DemoExplorer" with name "demo_explorer_cmd_opened"
    And I press "ALT+D" key combination in managed application "DemoExplorer"
    And I wait 1 seconds
    And I type "cmd" in managed application "DemoExplorer"
    And I take a screenshot of managed application "DemoExplorer" with name "demo_cmd_typed"
    And I press "ENTER" key in managed application "DemoExplorer"
    And I wait 3 seconds
    Then the process "cmd.exe" should be running
    When I register the newest running process "cmd.exe" as managed application "DemoCommandPrompt"
    And I take a screenshot of managed application "DemoCommandPrompt" with name "demo_cmd_opened"
    
    # Execute basic commands
    And I type "echo Desktop Automation Framework Demo" in managed application "DemoCommandPrompt"
    And I press "ENTER" key in managed application "DemoCommandPrompt"
    And I wait 2 seconds
    And I take a screenshot of managed application "DemoCommandPrompt" with name "demo_cmd_echo_result"
    
    And I type "cd %USERPROFILE%" in managed application "DemoCommandPrompt"
    And I press "ENTER" key in managed application "DemoCommandPrompt"
    And I wait 2 seconds
    And I take a screenshot of managed application "DemoCommandPrompt" with name "demo_cmd_cd_result"
    
    And I type "dir | findstr Desktop" in managed application "DemoCommandPrompt"
    And I press "ENTER" key in managed application "DemoCommandPrompt"
    And I wait 3 seconds
    And I capture evidence of managed application "DemoCommandPrompt" with description "CMD directory listing with findstr"
    
    Then the application "DemoCommandPrompt" should be running
    When I terminate the managed application "DemoCommandPrompt"
    And I close the window for managed application "DemoExplorer"
