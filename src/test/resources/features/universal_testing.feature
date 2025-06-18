Feature: Universal Windows Application Testing - Application Agnostic
  As an automation tester
  I want to test any Windows application using managed application context and OCR
  So that I can validate functionality across different applications without predefined configurations

  @smoke @universal @managed-context
  Scenario: Basic Application Interaction - Managed Context
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "universal_test_app"
    And I take a screenshot of managed application "universal_test_app" with name "initial_state"
    Then the application "universal_test_app" should be running
    When I terminate the managed application "universal_test_app"

  @universal @text_validation @managed-context
  Scenario: Text Detection and Validation - Multi-App Management
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    Then the application "calculator" should be running
    And the application "notepad" should be running
    When I switch to managed application "notepad"
    And I type "Testing Windows Microsoft File operations" in managed application "notepad"
    And I take a screenshot of managed application "notepad" with name "text_detection_start"
    Then I should see the text "Windows" in managed application "notepad"
    And I should see the text "Microsoft" in managed application "notepad"
    And I should see the text "File" in managed application "notepad"
    When I terminate the managed application "calculator"
    And I terminate the managed application "notepad"

  @universal @cross_application @managed-context
  Scenario: Cross-Application Data Validation - Managed Context Workflow
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calc_app"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "text_app"
    
    # Perform calculation
    When I switch to managed application "calc_app"
    And I type "25" in managed application "calc_app"
    And I type "+" in managed application "calc_app"
    And I type "75" in managed application "calc_app"
    And I type "=" in managed application "calc_app"
    Then I should see the text "100" in managed application "calc_app"
    And I capture evidence of managed application "calc_app" with description "Calculation result 25+75=100"
    
    # Document result in notepad
    When I switch to managed application "text_app"
    And I type "Calculation Result: 25 + 75 = 100" in managed application "text_app"
    And I press "ENTER" key in managed application "text_app"
    And I type "Cross-application validation successful" in managed application "text_app"
    Then I should see the text "25 + 75 = 100" in managed application "text_app"
    And I should see the text "validation successful" in managed application "text_app"
    And I capture evidence of managed application "text_app" with description "Cross-application data documented"
    
    # Clean up
    When I terminate the managed application "calc_app"
    And I terminate the managed application "text_app"

  @universal @window_management @managed-context
  Scenario: Window State Management - Universal Application Control
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "graphics_app"
    Then the application "graphics_app" should be running
    
    # Test window operations
    When I maximize managed application "graphics_app"
    And I take a screenshot of managed application "graphics_app" with name "maximized_paint"
    When I restore managed application "graphics_app"
    And I take a screenshot of managed application "graphics_app" with name "restored_paint"
    When I minimize managed application "graphics_app"
    When I restore managed application "graphics_app"
    And I bring managed application "graphics_app" to front
    
    # Test basic interaction
    When I click at coordinates 400, 300 in managed application "graphics_app"
    And I capture evidence of managed application "graphics_app" with description "Graphics application interaction test"
    
    # Clean up
    When I terminate the managed application "graphics_app"

  @universal @performance @managed-context
  Scenario: Performance and Stress Testing - Rapid Application Management
    # Launch multiple applications rapidly
    When I launch the application at path "C:\Windows\System32\calc.exe" as "perf_calc"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "perf_notepad"
    And I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "perf_paint"
    
    # Verify all launched successfully
    Then the application "perf_calc" should be running
    And the application "perf_notepad" should be running  
    And the application "perf_paint" should be running
    
    # Perform operations in each
    When I switch to managed application "perf_calc"
    And I type "999" in managed application "perf_calc"
    When I switch to managed application "perf_notepad"
    And I type "Performance test document" in managed application "perf_notepad"
    When I switch to managed application "perf_paint"
    And I click at coordinates 100, 100 in managed application "perf_paint"
    
    # Capture evidence from all
    And I capture evidence of managed application "perf_calc" with description "Performance test - calculator"
    And I capture evidence of managed application "perf_notepad" with description "Performance test - notepad"
    And I capture evidence of managed application "perf_paint" with description "Performance test - paint"
    
    # Clean up all applications
    When I terminate the managed application "perf_calc"
    And I terminate the managed application "perf_notepad"
    And I terminate the managed application "perf_paint"
