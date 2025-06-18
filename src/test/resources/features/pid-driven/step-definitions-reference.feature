Feature: PID-Driven Step Definitions Reference - The Complete Arsenal
  As an automation engineer learning the PID-driven paradigm
  I want to see all available step definitions in action
  So that I can master the most powerful automation framework ever created

  # =====================================================================================
  # REFERENCE GUIDE: All PID-Driven Step Definitions
  # =====================================================================================
  # 
  # 🚀 APPLICATION LAUNCH STEPS:
  # - When I launch the application at path "path/to/executable" as "unique_name"
  # - When I launch application "application_name" (legacy, uses ProcessManager lookup)
  # - When I launch "application_name" (shorthand)
  # 
  # ✅ VERIFICATION STEPS:
  # - Then the application "unique_name" should be running
  # - Then the last launched process should have PID greater than {int}
  # - Then application "unique_name" should have exactly {int} instances
  # 
  # 🎯 INTERACTION STEPS:
  # - When I switch to managed application "unique_name"
  # - When I type "text"
  # - When I press key "KEY_NAME"
  # - When I click at coordinates {int},{int}
  # - When I drag from coordinates {int},{int} to {int},{int}
  # 
  # 🛑 TERMINATION STEPS:
  # - When I terminate application "unique_name"
  # - When I cleanly close the current application
  # 
  # 📸 EVIDENCE STEPS:
  # - And I capture evidence with description "description"
  # - Then I should see the text "expected_text" using OCR
  # 
  # ⏱️ TIMING STEPS:
  # - When I wait for {int} seconds
  # 
  # =====================================================================================

  Background:
    Given the ProcessManager is initialized for reference demonstration
    And all applications are terminated for clean reference testing

  @reference @pid-driven @launch-steps @demo
  Scenario: Launch Step Definitions Reference
    # 🚀 PRIMARY LAUNCH METHOD - Direct executable path with unique name
    When I launch the application at path "C:\Windows\System32\calc.exe" as "reference_calculator"
    Then the application "reference_calculator" should be running
    And the last launched process should have PID greater than 0
    
    # 🚀 JAR APPLICATION LAUNCH
    When I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "reference_as400"
    Then the application "reference_as400" should be running
    
    # 🚀 ALTERNATIVE LAUNCH METHODS (legacy compatibility)
    When I launch application "notepad"
    Then the application "notepad" should be running
    
    When I launch "mspaint"
    Then the application "mspaint" should be running
    
    # ✅ INSTANCE COUNTING
    Then application "reference_calculator" should have exactly 1 instances
    Then application "reference_as400" should have exactly 1 instances
    Then application "notepad" should have exactly 1 instances
    Then application "mspaint" should have exactly 1 instances
    
    # 📸 EVIDENCE COLLECTION
    And I capture evidence with description "Launch step definitions reference"
    
    # 🛑 CLEANUP
    When I terminate application "reference_calculator"
    And I terminate application "reference_as400" 
    And I terminate application "notepad"
    And I terminate application "mspaint"
    
    Then application "reference_calculator" should have exactly 0 instances
    And application "reference_as400" should have exactly 0 instances
    And application "notepad" should have exactly 0 instances
    And application "mspaint" should have exactly 0 instances

  @reference @pid-driven @interaction-steps @demo
  Scenario: Interaction Step Definitions Reference
    # 🚀 SETUP APPLICATIONS FOR INTERACTION
    When I launch the application at path "C:\Windows\System32\calc.exe" as "interaction_calc"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "interaction_notepad"
    
    # 🎯 APPLICATION SWITCHING
    When I switch to managed application "interaction_calc"
    And I wait for 2 seconds
    
    # 🎯 KEYBOARD INPUT STEPS
    When I type "2"
    And I type "+"
    And I type "2"
    And I type "="
    
    # 🎯 KEY PRESS STEPS
    When I press key "ESCAPE"
    And I wait for 1 seconds
    
    # 🎯 SWITCH TO DIFFERENT APPLICATION
    When I switch to managed application "interaction_notepad"
    And I wait for 2 seconds
    
    # 🎯 TEXT ENTRY
    When I type "Interaction Step Definitions Demo"
    And I press key "ENTER"
    And I type "Keys tested: Type, Press Key, Switch Applications"
    And I press key "ENTER"
    
    # 🎯 SPECIAL KEY COMBINATIONS
    When I press key "CTRL+A"
    And I wait for 1 seconds
    When I press key "CTRL+C"
    And I wait for 1 seconds
    When I press key "END"
    And I press key "ENTER"
    When I press key "CTRL+V"
    
    # 📸 EVIDENCE OF INTERACTIONS
    And I capture evidence with description "Interaction step definitions reference"
    
    # 🛑 CLEANUP
    When I terminate application "interaction_calc"
    And I terminate application "interaction_notepad"

  @reference @pid-driven @verification-steps @demo
  Scenario: Verification Step Definitions Reference
    # 🚀 SETUP FOR VERIFICATION DEMOS
    When I launch the application at path "C:\Windows\System32\calc.exe" as "verify_calc"
    Then the application "verify_calc" should be running
    And the last launched process should have PID greater than 100
    
    # 🎯 PERFORM CALCULATION FOR OCR VERIFICATION
    When I type "50*2="
    And I wait for 2 seconds
    
    # ✅ OCR TEXT VERIFICATION
    Then I should see the text "100" using OCR
    
    # ✅ INSTANCE VERIFICATION
    Then application "verify_calc" should have exactly 1 instances
    
    # 🚀 LAUNCH ANOTHER INSTANCE FOR COUNTING TEST
    When I launch the application at path "C:\Windows\System32\calc.exe" as "verify_calc_2"
    Then application "verify_calc_2" should have exactly 1 instances
    
    # ✅ VERIFY BOTH APPLICATIONS SEPARATELY
    Then the application "verify_calc" should be running
    And the application "verify_calc_2" should be running
    
    # 📸 VERIFICATION EVIDENCE
    And I capture evidence with description "Verification step definitions reference"
    
    # 🛑 SELECTIVE TERMINATION TEST
    When I terminate application "verify_calc"
    Then application "verify_calc" should have exactly 0 instances
    And the application "verify_calc_2" should be running
    
    # 🛑 FINAL CLEANUP
    When I terminate application "verify_calc_2"
    Then application "verify_calc_2" should have exactly 0 instances

  @reference @pid-driven @timing-steps @demo
  Scenario: Timing and Wait Step Definitions Reference
    # 🚀 APPLICATION LAUNCH WITH TIMING
    When I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "timing_as400"
    And I wait for 3 seconds
    Then the application "timing_as400" should be running
    
    # 🎯 INTERACTIONS WITH TIMING
    When I type "TESTUSER"
    And I wait for 1 seconds
    And I press key "ENTER"
    And I wait for 2 seconds
    
    # 🎯 MENU NAVIGATION WITH TIMING
    When I type "1"
    And I wait for 1 seconds
    And I press key "ENTER"
    And I wait for 2 seconds
    
    # 🎯 CUSTOMER INQUIRY WITH TIMING
    When I type "CUST001"
    And I wait for 1 seconds
    And I press key "ENTER"
    And I wait for 3 seconds
    
    # ✅ VERIFY TIMING-DEPENDENT OPERATION
    Then I should see the text "CUSTOMER" using OCR
    
    # 📸 TIMING EVIDENCE
    And I capture evidence with description "Timing step definitions reference"
    
    # 🛑 CLEANUP WITH TIMING
    When I terminate application "timing_as400"
    And I wait for 1 seconds
    Then application "timing_as400" should have exactly 0 instances

  @reference @pid-driven @mouse-steps @demo
  Scenario: Mouse Interaction Step Definitions Reference
    # 🚀 LAUNCH PAINT FOR MOUSE INTERACTIONS
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "mouse_paint"
    And I wait for 3 seconds
    Then the application "mouse_paint" should be running
    
    # 🎯 MOUSE CLICK OPERATIONS
    When I click at coordinates 100,100
    And I wait for 1 seconds
    
    # 🎯 MOUSE DRAG OPERATIONS
    When I drag from coordinates 100,100 to 200,150
    And I wait for 1 seconds
    When I drag from coordinates 200,150 to 300,200
    And I wait for 1 seconds
    
    # 🎯 MULTIPLE DRAWING OPERATIONS
    When I click at coordinates 150,250
    When I drag from coordinates 150,250 to 250,300
    When I click at coordinates 300,100
    When I drag from coordinates 300,100 to 400,200
    
    # 📸 MOUSE INTERACTION EVIDENCE
    And I capture evidence with description "Mouse interaction step definitions reference"
    
    # 🛑 CLEANUP
    When I terminate application "mouse_paint"
    Then application "mouse_paint" should have exactly 0 instances

  @reference @pid-driven @comprehensive @all-steps
  Scenario: Comprehensive Step Definitions Demo - The Full Arsenal
    # 🚀 MASSIVE MULTI-APP LAUNCH
    When I launch the application at path "C:\Windows\System32\calc.exe" as "comprehensive_calc"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "comprehensive_notepad"
    And I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "comprehensive_paint"
    And I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "comprehensive_as400"
    
    # ✅ COMPREHENSIVE VERIFICATION
    Then the application "comprehensive_calc" should be running
    And the application "comprehensive_notepad" should be running
    And the application "comprehensive_paint" should be running
    And the application "comprehensive_as400" should be running
    And the last launched process should have PID greater than 0
    
    # 🎯 ORCHESTRATED INTERACTIONS ACROSS ALL APPS
    
    # Calculator operations
    When I switch to managed application "comprehensive_calc"
    And I wait for 2 seconds
    And I type "123*456="
    And I wait for 2 seconds
    Then I should see the text "56088" using OCR
    
    # Notepad documentation
    When I switch to managed application "comprehensive_notepad"
    And I wait for 2 seconds
    And I type "=== COMPREHENSIVE STEP DEFINITIONS TEST ==="
    And I press key "ENTER"
    And I type "Calculator: 123*456=56088 ✓"
    And I press key "ENTER"
    And I type "Notepad: Text entry ✓"
    And I press key "ENTER"
    And I type "Paint: Drawing operations ✓"
    And I press key "ENTER"
    And I type "AS400: Enterprise operations ✓"
    
    # Paint artistic operations
    When I switch to managed application "comprehensive_paint"
    And I wait for 2 seconds
    And I click at coordinates 50,50
    And I drag from coordinates 50,50 to 150,100
    And I click at coordinates 200,50
    And I drag from coordinates 200,50 to 300,150
    And I click at coordinates 100,200
    And I drag from coordinates 100,200 to 250,250
    
    # AS400 enterprise operations
    When I switch to managed application "comprehensive_as400"
    And I wait for 3 seconds
    And I type "COMPUSER"
    And I press key "ENTER"
    And I wait for 2 seconds
    And I type "1"
    And I press key "ENTER"
    And I wait for 2 seconds
    And I type "COMP001"
    And I press key "ENTER"
    And I wait for 3 seconds
    
    # ✅ FINAL COMPREHENSIVE VERIFICATION
    Then application "comprehensive_calc" should have exactly 1 instances
    And application "comprehensive_notepad" should have exactly 1 instances
    And application "comprehensive_paint" should have exactly 1 instances
    And application "comprehensive_as400" should have exactly 1 instances
    
    # 📸 COMPREHENSIVE EVIDENCE
    And I capture evidence with description "COMPREHENSIVE DEMO: All step definitions working in perfect harmony"
    
    # 🛑 COMPREHENSIVE CLEANUP
    When I terminate application "comprehensive_calc"
    And I terminate application "comprehensive_notepad"
    And I terminate application "comprehensive_paint"
    And I terminate application "comprehensive_as400"
    
    # ✅ PERFECT CLEANUP VERIFICATION
    Then application "comprehensive_calc" should have exactly 0 instances
    And application "comprehensive_notepad" should have exactly 0 instances
    And application "comprehensive_paint" should have exactly 0 instances
    And application "comprehensive_as400" should have exactly 0 instances
    
    And I capture evidence with description "REFERENCE COMPLETE: All step definitions demonstrated successfully"
