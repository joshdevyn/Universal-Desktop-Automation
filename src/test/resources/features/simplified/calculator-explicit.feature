Feature: Simplified Calculator Testing - Application Agnostic
  As a QA engineer
  I want to test calculator functionality using managed application context
  So that I have full control over application lifecycle without predefined configurations

  @simplified @calculator @smoke @managed-context
  Scenario: Basic Calculator Operations with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I take a screenshot of managed application "calculator" with name "launched"
    When I type "2" in managed application "calculator"
    And I type "+" in managed application "calculator"
    And I type "3" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "5" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Simple addition 2+3=5"
    When I terminate the managed application "calculator"

  @simplified @calculator @demo @managed-context
  Scenario: Calculator with Direct Application Launch and Focus
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "10" in managed application "calculator"
    And I type "*" in managed application "calculator"
    And I type "5" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "50" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Multiplication operation 10*5=50"
    When I terminate the managed application "calculator"

  @simplified @calculator @data-driven @managed-context
  Scenario Outline: Data-Driven Calculator Testing with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "<input1>" in managed application "calculator"
    And I type "<operation>" in managed application "calculator"
    And I type "<input2>" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "<expected>" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Data-driven test: <input1><operation><input2>=<expected>"
    When I terminate the managed application "calculator"

    Examples:
      | input1 | operation | input2 | expected |
      | 15     | +         | 25     | 40       |
      | 100    | -         | 30     | 70       |
      | 8      | *         | 7      | 56       |
      | 144    | /         | 12     | 12       |

  @simplified @calculator @keyboard @managed-context
  Scenario: Calculator Keyboard Shortcuts with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "999" in managed application "calculator"
    And I press "ESCAPE" key in managed application "calculator"
    Then I should see the text "0" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Clear operation with ESCAPE key"
    
    When I type "123" in managed application "calculator"
    And I press "BACKSPACE" key in managed application "calculator"
    Then I should see the text "12" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Backspace operation"
    When I terminate the managed application "calculator"
