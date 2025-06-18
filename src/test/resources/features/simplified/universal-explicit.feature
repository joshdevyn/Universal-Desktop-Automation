Feature: Universal Application Testing - Application Agnostic Framework
  As a framework architect
  I want to demonstrate the framework works with any Windows application using managed context
  So that users understand the universal capabilities without predefined configurations

  @screenshot
  Scenario: Universal Application Pattern - Managed Context
    When I launch the application at path "C:\Windows\System32\calc.exe" as "universal_demo_app"
    And I take a screenshot of managed application "universal_demo_app" with name "initial_app_state"
    When I bring managed application "universal_demo_app" to front
    And I switch to managed application "universal_demo_app"
    When I maximize managed application "universal_demo_app"
    And I take a screenshot of managed application "universal_demo_app" with name "maximized_state"
    When I restore managed application "universal_demo_app"
    And I capture evidence of managed application "universal_demo_app" with description "Universal window operations completed"
    When I terminate the managed application "universal_demo_app"

  @critical
  Scenario: Cross-Application Window Management - Multi-Application Management
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I take a screenshot of managed application "calculator" with name "calculator_open"
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    And I take a screenshot of managed application "notepad" with name "both_apps_open"
    When I switch to managed application "calculator"
    And I type "789" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Calculator state after focus"
    
    When I switch to managed application "notepad"
    And I type "Testing cross-application management" in managed application "notepad"
    And I capture evidence of managed application "notepad" with description "Notepad state after focus switch"
    
    # Test window operations on both applications
    When I maximize managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "calculator_maximized"
    When I maximize managed application "notepad"
    And I take a screenshot of managed application "notepad" with name "notepad_maximized"
    
    # Restore both windows
    When I restore managed application "calculator"
    And I restore managed application "notepad"
    
    # Clean up
    When I terminate the managed application "calculator"
    And I terminate the managed application "notepad"

  @performance
  Scenario: Performance Testing Pattern - Rapid Application Lifecycle
    # Test rapid launch and termination
    When I launch the application at path "C:\Windows\System32\calc.exe" as "perf_calc_1"
    And I wait 1 seconds
    When I terminate the managed application "perf_calc_1"
    
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "perf_notepad_1"
    And I wait 1 seconds
    When I terminate the managed application "perf_notepad_1"
    
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "perf_paint_1"
    And I wait 1 seconds
    When I terminate the managed application "perf_paint_1"
    
    # Test concurrent application management
    When I launch the application at path "C:\Windows\System32\calc.exe" as "concurrent_calc"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "concurrent_notepad"
    And I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "concurrent_paint"
    
    # Verify all are running
    Then the application "concurrent_calc" should be running
    And the application "concurrent_notepad" should be running
    And the application "concurrent_paint" should be running
    
    # Perform operations on each
    When I switch to managed application "concurrent_calc"
    And I type "111+222=" in managed application "concurrent_calc"
    
    When I switch to managed application "concurrent_notepad"
    And I type "Performance test running..." in managed application "concurrent_notepad"
    
    When I switch to managed application "concurrent_paint"
    And I click at coordinates 400, 300 in managed application "concurrent_paint"
    
    # Capture evidence
    And I capture evidence of managed application "concurrent_calc" with description "Concurrent calc operation"
    And I capture evidence of managed application "concurrent_notepad" with description "Concurrent notepad operation"
    And I capture evidence of managed application "concurrent_paint" with description "Concurrent paint operation"
    
    # Terminate all
    When I terminate the managed application "concurrent_calc"
    And I terminate the managed application "concurrent_notepad"
    And I terminate the managed application "concurrent_paint"

  @edge-cases
  Scenario: Edge Case Handling - Application Error Recovery
    # Test launching non-existent application path
    When I launch the application at path "C:\Windows\System32\calc.exe" as "edge_test_calc"
    Then the application "edge_test_calc" should be running
    
    # Test multiple operations rapidly
    When I type "1" in managed application "edge_test_calc"
    And I type "+" in managed application "edge_test_calc"
    And I type "1" in managed application "edge_test_calc"
    And I type "=" in managed application "edge_test_calc"
    And I type "+" in managed application "edge_test_calc"
    And I type "1" in managed application "edge_test_calc"
    And I type "=" in managed application "edge_test_calc"
    
    # Verify final result
    Then I should see the text "3" in managed application "edge_test_calc"
    And I capture evidence of managed application "edge_test_calc" with description "Rapid operation sequence result"
    
    # Clean up
    When I terminate the managed application "edge_test_calc"
