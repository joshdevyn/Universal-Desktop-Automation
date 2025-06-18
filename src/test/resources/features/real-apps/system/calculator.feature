Feature: Windows Calculator - Application Agnostic Testing  
  As a QA engineer
  I want to test Windows Calculator using managed application context
  So that I can validate calculator functionality without predefined configurations
  
  @smoke @calculator @real-app @managed-context
  Scenario: Basic Arithmetic Operations with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "123" in managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "number_input_123"
    And I type "+" in managed application "calculator"
    And I type "456" in managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "before_calculation"
    And I type "=" in managed application "calculator"
    Then I should see the text "579" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Addition result 123+456=579"
    
    When I press "ESCAPE" key in managed application "calculator"
    And I type "999" in managed application "calculator"
    And I type "*" in managed application "calculator"
    And I type "8" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "7992" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Multiplication result 999*8=7992"
    
    When I terminate the managed application "calculator"

  @regression @calculator @real-app @managed-context
  Scenario: Complex Mathematical Operations with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "15" in managed application "calculator"
    And I type "/" in managed application "calculator"
    And I type "3" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "5" in managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "division_result"
    
    When I press "ESCAPE" key in managed application "calculator"
    And I type "2.5" in managed application "calculator"
    And I type "*" in managed application "calculator"
    And I type "4.8" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "12" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Decimal multiplication result"
    
    When I terminate the managed application "calculator"

  @edge-cases @calculator @real-app @managed-context
  Scenario: Error Handling and Edge Cases with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "10" in managed application "calculator"
    And I type "/" in managed application "calculator"
    And I type "0" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "Cannot divide by zero" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Division by zero error handling"
    
    When I press "ESCAPE" key in managed application "calculator"
    And I type "999999999" in managed application "calculator"
    And I type "*" in managed application "calculator"
    And I type "999999999" in managed application "calculator"
    And I type "=" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Large number multiplication"
    
    When I terminate the managed application "calculator"

  @memory @calculator @real-app @managed-context
  Scenario: Memory Functions with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "25" in managed application "calculator"
    And I press "CTRL+M" key combination in managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "memory_store"
    And I press "ESCAPE" key in managed application "calculator"
    And I type "15" in managed application "calculator"
    And I press "CTRL+P" key combination in managed application "calculator"
    And I press "ESCAPE" key in managed application "calculator"
    And I press "CTRL+R" key combination in managed application "calculator"
    Then I should see the text "40" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Memory functions: 25 + 15 = 40"
    
    When I terminate the managed application "calculator"

  @accessibility @calculator @real-app @managed-context
  Scenario: Keyboard Navigation and Accessibility with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "1" in managed application "calculator"
    And I type "+" in managed application "calculator"
    And I type "2" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "3" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Keyboard-only operation"
    
    When I terminate the managed application "calculator"

  @visual-validation @calculator @real-app @managed-context
  Scenario: Visual State Validation with Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "0" in managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "zero_state"
    And I type "1" in managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "one_state"
    And I capture evidence of managed application "calculator" with description "Visual state validation complete"
    
    When I terminate the managed application "calculator"