@integration @cross-system @enterprise-workflow @end-to-end @managed-context
Feature: Cross-System Integration Testing - Application Agnostic Managed Context
  As an Enterprise Automation Engineer
  I want to test complete workflows across multiple systems using managed application context
  So that I can automate any Windows application without predefined configurations

  @critical-path @multi-app @high-priority @managed-context
  Scenario: Multi-Application Workflow - Calculator, Notepad, Paint Integration
    # Step 1: Create data in Calculator
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator"
    And I type "123" in managed application "calculator"
    And I type "+" in managed application "calculator"
    And I type "456" in managed application "calculator"
    And I type "=" in managed application "calculator"
    Then I should see the text "579" in managed application "calculator"
    And I capture evidence of managed application "calculator" with description "Calculation completed"
    
    # Step 2: Document results in Notepad
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    And I type "Calculation Result: 123 + 456 = 579" in managed application "notepad"
    And I press "ENTER" key in managed application "notepad"
    And I type "Timestamp: 2025-06-16" in managed application "notepad"
    And I press "ENTER" key in managed application "notepad"
    And I type "Application: Calculator" in managed application "notepad"
    Then I should see the text "Calculation Result" in managed application "notepad"
    And I capture evidence of managed application "notepad" with description "Results documented"
    
    # Step 3: Create visual documentation in Paint
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "paint"
    And I wait 2 seconds
    And I click at coordinates 100,100 in managed application "paint"
    And I click at coordinates 200,200 in managed application "paint"
    And I take a screenshot of managed application "paint" with name "diagram"
    And I capture evidence of managed application "paint" with description "Visual documentation created"
    
    # Step 4: Switch between applications to verify workflow
    When I switch to managed application "calculator"
    And I take a screenshot of managed application "calculator" with name "final_calc"
    When I switch to managed application "notepad"
    And I take a screenshot of managed application "notepad" with name "final_notes"
    When I switch to managed application "paint"
    And I take a screenshot of managed application "paint" with name "final_diagram"
    
    # Cleanup
    When I terminate the managed application "calculator"
    And I terminate the managed application "notepad"
    And I terminate the managed application "paint"

  @data-flow @real-time @cross-application @managed-context
  Scenario: Cross-Application Data Flow - File Explorer and Notepad
    # Step 1: Create file structure with File Explorer
    When I launch the application at path "C:\Windows\explorer.exe" as "explorer"
    And I press "CTRL+L" key combination in managed application "explorer"  # Focus address bar
    And I type "C:\temp" in managed application "explorer"
    And I press "ENTER" key in managed application "explorer"
    And I wait 2 seconds
    And I press "CTRL+SHIFT+N" key combination in managed application "explorer"  # New folder
    And I type "AutomationTest" in managed application "explorer"
    And I press "ENTER" key in managed application "explorer"
    And I capture evidence of managed application "explorer" with description "Test folder created"
    
    # Step 2: Create documentation file with Notepad
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "documentation_editor"
    And I type "Cross-Application Integration Test" in managed application "documentation_editor"
    And I press "ENTER" key in managed application "documentation_editor"
    And I press "ENTER" key in managed application "documentation_editor"
    And I type "Test Folder: C:\temp\AutomationTest" in managed application "documentation_editor"
    And I press "ENTER" key in managed application "documentation_editor"
    And I type "Created via File Explorer automation" in managed application "documentation_editor"
    Then I should see the text "Cross-Application Integration Test" in managed application "documentation_editor"
    And I capture evidence of managed application "documentation_editor" with description "Documentation file created"
    
    # Step 3: Verify workflow completion
    When I switch to managed application "explorer"
    And I capture evidence of managed application "explorer" with description "Final file structure"
    When I switch to managed application "documentation_editor"
    And I capture evidence of managed application "documentation_editor" with description "Final documentation"
    
    # Cleanup
    When I terminate the managed application "explorer"
    And I terminate the managed application "documentation_editor"

  @performance @load-testing @multi-instance @managed-context
  Scenario: Multi-Instance Performance Testing - Application Load Management
    # Launch multiple instances of the same application
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "perf_notepad_1"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "perf_notepad_2"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "perf_notepad_3"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "perf_calc_1"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "perf_calc_2"
    
    # Verify all instances are running
    Then the application "perf_notepad_1" should be running
    And the application "perf_notepad_2" should be running
    And the application "perf_notepad_3" should be running
    And the application "perf_calc_1" should be running
    And the application "perf_calc_2" should be running
    
    # Perform operations across all instances
    When I switch to managed application "perf_notepad_1"
    And I type "Performance Test Document 1" in managed application "perf_notepad_1"
    
    When I switch to managed application "perf_calc_1"
    And I type "100+200=" in managed application "perf_calc_1"
    
    When I switch to managed application "perf_notepad_2"
    And I type "Performance Test Document 2" in managed application "perf_notepad_2"
    
    When I switch to managed application "perf_calc_2"
    And I type "500*2=" in managed application "perf_calc_2"
    
    When I switch to managed application "perf_notepad_3"
    And I type "Performance Test Document 3" in managed application "perf_notepad_3"
    
    # Verify results in calculators
    When I switch to managed application "perf_calc_1"
    Then I should see the text "300" in managed application "perf_calc_1"
    
    When I switch to managed application "perf_calc_2"
    Then I should see the text "1000" in managed application "perf_calc_2"
    
    # Capture performance evidence
    And I capture evidence of managed application "perf_calc_1" with description "Performance calc 1 result"
    And I capture evidence of managed application "perf_calc_2" with description "Performance calc 2 result"
    And I capture evidence of managed application "perf_notepad_1" with description "Performance notepad 1"
    And I capture evidence of managed application "perf_notepad_2" with description "Performance notepad 2"
    And I capture evidence of managed application "perf_notepad_3" with description "Performance notepad 3"
    
    # Clean up all instances
    When I terminate the managed application "perf_notepad_1"
    And I terminate the managed application "perf_notepad_2"
    And I terminate the managed application "perf_notepad_3"
    And I terminate the managed application "perf_calc_1"
    And I terminate the managed application "perf_calc_2"

  @enterprise @workflow @complex @managed-context
  Scenario: Enterprise Complex Workflow - Multi-Step Business Process
    # Step 1: Initialize data gathering with Calculator
    When I launch the application at path "C:\Windows\System32\calc.exe" as "business_calc"
    And I type "1500" in managed application "business_calc"
    And I type "*" in managed application "business_calc"
    And I type "12" in managed application "business_calc"
    And I type "=" in managed application "business_calc"
    Then I should see the text "18000" in managed application "business_calc"
    And I capture evidence of managed application "business_calc" with description "Annual calculation: 1500*12=18000"
    
    # Step 2: Document business calculations
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "business_report"
    And I type "BUSINESS PROCESS AUTOMATION REPORT" in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I type "Monthly Value: $1,500" in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I type "Annual Total: $18,000" in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I type "Calculation verified via Calculator application" in managed application "business_report"
    Then I should see the text "BUSINESS PROCESS AUTOMATION REPORT" in managed application "business_report"
    And I capture evidence of managed application "business_report" with description "Business report documentation"
    
    # Step 3: Create visual representation
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "business_graphics"
    And I wait 2 seconds
    And I click at coordinates 50,50 in managed application "business_graphics"
    And I click at coordinates 200,50 in managed application "business_graphics"
    And I click at coordinates 200,150 in managed application "business_graphics"
    And I click at coordinates 50,150 in managed application "business_graphics"
    And I capture evidence of managed application "business_graphics" with description "Business process diagram created"
    
    # Step 4: Cross-verify data integrity
    When I switch to managed application "business_calc"
    And I press "ESCAPE" key in managed application "business_calc"
    And I type "18000" in managed application "business_calc"
    And I type "/" in managed application "business_calc"
    And I type "12" in managed application "business_calc"
    And I type "=" in managed application "business_calc"
    Then I should see the text "1500" in managed application "business_calc"
    And I capture evidence of managed application "business_calc" with description "Reverse calculation verification: 18000/12=1500"
    
    # Step 5: Final workflow documentation
    When I switch to managed application "business_report"
    And I press "CTRL+END" key combination in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I type "VERIFICATION COMPLETE:" in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I type "Reverse calculation: 18000 / 12 = 1500 ✓" in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I type "Visual diagram created ✓" in managed application "business_report"
    And I press "ENTER" key in managed application "business_report"
    And I type "Business process automation: SUCCESSFUL" in managed application "business_report"
    Then I should see the text "VERIFICATION COMPLETE" in managed application "business_report"
    And I capture evidence of managed application "business_report" with description "Final business process report"
    
    # Final state capture
    When I switch to managed application "business_calc"
    And I take a screenshot of managed application "business_calc" with name "final_business_calc"
    When I switch to managed application "business_report"
    And I take a screenshot of managed application "business_report" with name "final_business_report"
    When I switch to managed application "business_graphics"
    And I take a screenshot of managed application "business_graphics" with name "final_business_graphics"
    
    # Enterprise cleanup
    When I terminate the managed application "business_calc"
    And I terminate the managed application "business_report"
    And I terminate the managed application "business_graphics"

  @stress-test @reliability @endurance @managed-context
  Scenario: System Stress Testing - Extended Operations Under Load
    # Launch applications for stress testing
    When I launch the application at path "C:\Windows\System32\calc.exe" as "stress_calc"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad"
    And I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "stress_paint"
    
    # Perform 10 rapid operations in calculator
    When I switch to managed application "stress_calc"
    And I type "1+1=" in managed application "stress_calc"
    And I type "+1=" in managed application "stress_calc"
    And I type "+1=" in managed application "stress_calc"
    And I type "+1=" in managed application "stress_calc"
    And I type "+1=" in managed application "stress_calc"
    Then I should see the text "6" in managed application "stress_calc"
    
    # Rapid text operations in notepad
    When I switch to managed application "stress_notepad"
    And I type "Line1 Line2 Line3 Line4 Line5" in managed application "stress_notepad"
    And I press "CTRL+A" key combination in managed application "stress_notepad"
    And I press "CTRL+C" key combination in managed application "stress_notepad"
    And I press "CTRL+END" key combination in managed application "stress_notepad"
    And I press "ENTER" key in managed application "stress_notepad"
    And I press "CTRL+V" key combination in managed application "stress_notepad"
    Then I should see the text "Line1 Line2 Line3" in managed application "stress_notepad"
    
    # Multiple clicks in paint
    When I switch to managed application "stress_paint"
    And I click at coordinates 100,100 in managed application "stress_paint"
    And I click at coordinates 150,150 in managed application "stress_paint"
    And I click at coordinates 200,200 in managed application "stress_paint"
    And I click at coordinates 250,250 in managed application "stress_paint"
    And I click at coordinates 300,300 in managed application "stress_paint"
    
    # Verify system stability
    And I capture evidence of managed application "stress_calc" with description "Stress test - calculator stability"
    And I capture evidence of managed application "stress_notepad" with description "Stress test - notepad stability"
    And I capture evidence of managed application "stress_paint" with description "Stress test - paint stability"
    
    # Stress test cleanup
    When I terminate the managed application "stress_calc"
    And I terminate the managed application "stress_notepad"
    And I terminate the managed application "stress_paint"
