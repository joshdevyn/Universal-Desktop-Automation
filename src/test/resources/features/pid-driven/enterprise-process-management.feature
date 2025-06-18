Feature: Enterprise PID-Driven Process Management - Business Architecture
  As an Automation Engineer
  I want to launch applications using executable paths and manage them by PID
  So that I can achieve deterministic reliability without window title dependencies

  Background:
    Given the ProcessManager is initialized for PID-driven operations
    And all previous application instances are terminated

  @pid-driven @enterprise @critical
  Scenario: Launch application by direct path - Calculator System Test
    # Direct executable path launch with unique process identifier
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calculator_main"
    Then the application "calculator_main" should be running
    And the last launched process should have PID greater than 0
    
    # Execute mathematical operation using managed process
    When I type "2"
    And I type "+"
    And I type "3"
    And I type "="
    
    # Verify calculation results using OCR text recognition
    Then I should see the text "5" using OCR
    And I capture evidence with description "PID-driven calculator test - 2+3=5"
    
    # Terminate application using process identifier
    When I terminate application "calculator_main"
    Then application "calculator_main" should have exactly 0 instances
  @pid-driven @enterprise @notepad @critical
  Scenario: Launch Notepad by path and manage with PID tracking
    # Launch Notepad application using direct executable path
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad_instance_1"
    Then the application "notepad_instance_1" should be running
    
    # Execute text editing operations for validation
    When I type "This is a PID-driven test!"
    And I press key "ENTER"
    And I type "ProcessManager provides enterprise-grade process management!"
    
    # Capture test execution evidence
    And I capture evidence with description "PID-driven notepad automation"
    
    # Execute controlled application termination
    When I terminate application "notepad_instance_1"
    Then application "notepad_instance_1" should have exactly 0 instances

  @pid-driven @enterprise @paint @critical
  Scenario: Paint application with direct executable path
    # Launch Paint application using system executable path
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "paint_session"
    Then the application "paint_session" should be running
    And the last launched process should have PID greater than 0
    
    # Execute basic graphics interaction operations
    When I click at coordinates 100,100
    And I drag from coordinates 100,100 to 200,200
    
    # Capture visual test evidence
    And I capture evidence with description "PID-driven paint application test"
      # Execute controlled application termination
    When I terminate application "paint_session"
    Then application "paint_session" should have exactly 0 instances

  @pid-driven @enterprise @multi-instance @advanced
  Scenario: Multi-instance management with different applications
    # Launch multiple application instances with unique managed identifiers
    When I launch the application at path "C:\Windows\System32\calc.exe" as "calc_instance_1"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad_instance_1"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "calc_instance_2"
    
    # Verify all application instances are successfully running
    Then the application "calc_instance_1" should be running
    And the application "notepad_instance_1" should be running
    And the application "calc_instance_2" should be running
    
    # Execute cross-instance workflow operations
    When I switch to managed application "calc_instance_1"
    And I type "10+20="
    Then I should see the text "30" using OCR
    
    When I switch to managed application "notepad_instance_1"
    And I type "Instance switching test executed successfully"
    
    When I switch to managed application "calc_instance_2"
    And I type "50*2="
    Then I should see the text "100" using OCR
    
    # Capture multi-instance management evidence
    And I capture evidence with description "Multi-instance PID management validation"
      # Execute controlled shutdown sequence for all instances
    When I terminate application "calc_instance_1"
    And I terminate application "notepad_instance_1"
    And I terminate application "calc_instance_2"
    
    Then application "calc_instance_1" should have exactly 0 instances
    And application "notepad_instance_1" should have exactly 0 instances
    And application "calc_instance_2" should have exactly 0 instances

  @pid-driven @enterprise @enterprise @mock-apps
  Scenario: Enterprise mock application with JAR path launching
    # Launch enterprise mock application using JAR executable path
    When I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "as400_terminal"
    Then the application "as400_terminal" should be running
    
    # Execute enterprise application interaction workflow
    When I type "TESTUSER"
    And I press key "ENTER"
    And I wait for 2 seconds
    
    # Verify enterprise application functionality
    Then I should see the text "MAIN MENU" using OCR
    And I capture evidence with description "Enterprise AS400 mock - PID-driven validation"
    
    # Execute enterprise application cleanup
    When I terminate application "as400_terminal"
    Then application "as400_terminal" should have exactly 0 instances

  @pid-driven @enterprise @stress-test @advanced
  Scenario: Rapid launch and terminate stress test
    # Execute rapid application launch sequence
    When I launch the application at path "C:\Windows\System32\calc.exe" as "stress_calc_1"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "stress_calc_2"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "stress_calc_3"
    
    # Verify successful rapid deployment
    Then the application "stress_calc_1" should be running
    And the application "stress_calc_2" should be running
    And the application "stress_calc_3" should be running
    
    # Execute rapid termination sequence
    When I terminate application "stress_calc_1"
    And I terminate application "stress_calc_2"
    And I terminate application "stress_calc_3"
    
    # Verify complete termination sequence
    Then application "stress_calc_1" should have exactly 0 instances
    And application "stress_calc_2" should have exactly 0 instances
    And application "stress_calc_3" should have exactly 0 instances
    
    And I capture evidence with description "Stress test - rapid launch/terminate cycle validation"
