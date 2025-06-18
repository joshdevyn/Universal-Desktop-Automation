Feature: Real System Applications - PID-Driven System Integration
  As a system integration tester
  I want to test real Windows applications using direct paths
  So that I can validate the framework works with actual production software

  Background:
    Given the ProcessManager is initialized for system testing
    And all system application instances are cleaned up

  @pid-driven @system @windows-apps @critical
  Scenario: Windows Built-in Applications Suite
    # 🚀 TEST CORE WINDOWS APPLICATIONS
    When I launch the application at path "C:\Windows\System32\calc.exe" as "windows_calculator"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "windows_notepad"
    And I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "windows_paint"
    
    # ✅ VERIFY ALL SYSTEM APPS RUNNING
    Then the application "windows_calculator" should be running
    And the application "windows_notepad" should be running
    And the application "windows_paint" should be running
    
    # 🎯 CALCULATOR OPERATIONS
    When I switch to managed application "windows_calculator"
    And I type "25*4="
    Then I should see the text "100" using OCR
    
    # 🎯 NOTEPAD TEXT OPERATIONS
    When I switch to managed application "windows_notepad"
    And I type "System Integration Test - Windows Applications"
    And I press key "ENTER"
    And I type "Calculator: 25*4=100 ✓"
    And I press key "ENTER"
    And I type "Notepad: Text entry ✓"
    And I press key "ENTER"
    And I type "Paint: Next test ✓"
    
    # 🎯 PAINT BASIC OPERATIONS
    When I switch to managed application "windows_paint"
    And I click at coordinates 100,100
    And I drag from coordinates 100,100 to 200,150
    
    # ✅ EVIDENCE COLLECTION
    And I capture evidence with description "Windows built-in applications test"
    
    # 🛑 SYSTEM CLEANUP
    When I terminate application "windows_calculator"
    And I terminate application "windows_notepad"
    And I terminate application "windows_paint"
    
    Then application "windows_calculator" should have exactly 0 instances
    And application "windows_notepad" should have exactly 0 instances
    And application "windows_paint" should have exactly 0 instances

  @pid-driven @system @command-line @critical
  Scenario: Command Line Applications Testing
    # 🚀 LAUNCH COMMAND LINE APPLICATIONS
    When I launch the application at path "C:\Windows\System32\cmd.exe" as "command_prompt_1"
    And I launch the application at path "powershell.exe" as "powershell_1"
    
    # ✅ VERIFY COMMAND LINE APPS
    Then the application "command_prompt_1" should be running
    And the application "powershell_1" should be running
    
    # 🎯 CMD OPERATIONS
    When I switch to managed application "command_prompt_1"
    And I type "echo PID-Driven Framework Test"
    And I press key "ENTER"
    And I wait for 2 seconds
    And I type "dir /w"
    And I press key "ENTER"
    And I wait for 3 seconds
    
    # 🎯 POWERSHELL OPERATIONS
    When I switch to managed application "powershell_1"
    And I type "Write-Host 'ProcessManager rocks!'"
    And I press key "ENTER"
    And I wait for 2 seconds
    And I type "Get-Process | Where-Object {$_.Name -like '*calc*'}"
    And I press key "ENTER"
    And I wait for 3 seconds
    
    # ✅ COMMAND LINE EVIDENCE
    And I capture evidence with description "Command line applications automation"
    
    # 🛑 COMMAND LINE CLEANUP
    When I terminate application "command_prompt_1"
    And I terminate application "powershell_1"
    
    Then application "command_prompt_1" should have exactly 0 instances
    And application "powershell_1" should have exactly 0 instances

  @pid-driven @system @office-suite @advanced
  Scenario: Microsoft Office Applications (if available)
    # 🚀 ATTEMPT OFFICE APPLICATIONS LAUNCH
    When I launch the application at path "WINWORD.EXE" as "ms_word"
    Then the application "ms_word" should be running
    
    # 🎯 WORD OPERATIONS
    When I wait for 5 seconds
    And I type "This document was created using PID-driven automation!"
    And I press key "ENTER"
    And I press key "ENTER"
    And I type "Features tested:"
    And I press key "ENTER"
    And I type "- Direct executable path launching"
    And I press key "ENTER"
    And I type "- ProcessManager PID tracking"
    And I press key "ENTER"
    And I type "- Reliable application management"
    
    # ✅ OFFICE EVIDENCE
    And I capture evidence with description "Microsoft Word automation via PID"
    
    # 🛑 OFFICE CLEANUP (without saving)
    When I press key "ALT+F4"
    And I wait for 2 seconds
    And I press key "N"
    And I wait for 2 seconds
    
    Then application "ms_word" should have exactly 0 instances

  @pid-driven @system @web-browsers @critical
  Scenario: Web Browser Applications Testing
    # 🚀 LAUNCH BROWSERS (if available)
    When I launch the application at path "msedge.exe" as "edge_browser"
    
    # ✅ VERIFY BROWSER LAUNCH
    Then the application "edge_browser" should be running
    And the last launched process should have PID greater than 0
    
    # 🎯 BROWSER OPERATIONS
    When I wait for 5 seconds
    And I press key "CTRL+L"
    And I wait for 1 seconds
    And I type "https://www.google.com"
    And I press key "ENTER"
    And I wait for 10 seconds
    
    # ✅ BROWSER EVIDENCE
    And I capture evidence with description "Web browser automation via PID"
    
    # 🛑 BROWSER CLEANUP
    When I terminate application "edge_browser"
    Then application "edge_browser" should have exactly 0 instances

  @pid-driven @system @system-utilities @critical
  Scenario: Windows System Utilities Testing
    # 🚀 LAUNCH SYSTEM UTILITIES
    When I launch the application at path "C:\Windows\System32\taskmgr.exe" as "task_manager"
    And I launch the application at path "regedit.exe" as "registry_editor"
    
    # ✅ VERIFY UTILITIES LAUNCH
    Then the application "task_manager" should be running
    And the application "registry_editor" should be running
    
    # 🎯 TASK MANAGER INTERACTION
    When I switch to managed application "task_manager"
    And I wait for 3 seconds
    And I press key "TAB"
    And I press key "DOWN"
    And I press key "DOWN"
    
    # 🎯 REGISTRY EDITOR INTERACTION (READ-ONLY)
    When I switch to managed application "registry_editor"
    And I wait for 3 seconds
    And I press key "F5"
    And I wait for 2 seconds
    
    # ✅ UTILITIES EVIDENCE
    And I capture evidence with description "System utilities automation"
    
    # 🛑 UTILITIES CLEANUP
    When I terminate application "task_manager"
    And I terminate application "registry_editor"
    
    Then application "task_manager" should have exactly 0 instances
    And application "registry_editor" should have exactly 0 instances

  @pid-driven @system @multimedia @advanced
  Scenario: Multimedia Applications Testing
    # 🚀 LAUNCH MULTIMEDIA APPS
    When I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "paint_advanced"
    
    # ✅ VERIFY MULTIMEDIA LAUNCH
    Then the application "paint_advanced" should be running
    
    # 🎯 ADVANCED PAINT OPERATIONS
    When I wait for 3 seconds
    # Select brush tool
    And I click at coordinates 50,50
    # Draw a rectangle
    And I click at coordinates 100,100
    And I drag from coordinates 100,100 to 300,200
    # Draw a circle
    And I click at coordinates 350,350
    And I drag from coordinates 350,350 to 450,450
    # Add text
    And I click at coordinates 150,250
    And I type "PID-DRIVEN"
    
    # ✅ MULTIMEDIA EVIDENCE
    And I capture evidence with description "Advanced multimedia automation"
    
    # 🛑 MULTIMEDIA CLEANUP
    When I terminate application "paint_advanced"
    Then application "paint_advanced" should have exactly 0 instances

  @pid-driven @system @stress-test @performance
  Scenario: System Performance Stress Test
    # 🚀 RAPID SYSTEM APPLICATION LAUNCHES
    When I launch the application at path "C:\Windows\System32\calc.exe" as "stress_calc_1"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "stress_calc_2"
    And I launch the application at path "C:\Windows\System32\calc.exe" as "stress_calc_3"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad_1"
    And I launch the application at path "C:\Windows\System32\notepad.exe" as "stress_notepad_2"
    And I launch the application at path "C:\Program Files\WindowsApps\Microsoft.Paint_11.2503.381.0_x64__8wekyb3d8bbwe\mspaint.exe" as "stress_paint_1"
    
    # ✅ VERIFY STRESS TEST DEPLOYMENT
    Then the application "stress_calc_1" should be running
    And the application "stress_calc_2" should be running
    And the application "stress_calc_3" should be running
    And the application "stress_notepad_1" should be running
    And the application "stress_notepad_2" should be running
    And the application "stress_paint_1" should be running
    
    # 🎯 CONCURRENT OPERATIONS
    When I switch to managed application "stress_calc_1"
    And I type "123+456="
    When I switch to managed application "stress_calc_2"
    And I type "789*2="
    When I switch to managed application "stress_calc_3"
    And I type "100/5="
    When I switch to managed application "stress_notepad_1"
    And I type "Stress Test Instance 1"
    When I switch to managed application "stress_notepad_2"
    And I type "Stress Test Instance 2"
    
    # ✅ STRESS TEST EVIDENCE
    And I capture evidence with description "System stress test - multiple concurrent applications"
    
    # 🛑 RAPID CLEANUP SEQUENCE
    When I terminate application "stress_calc_1"
    And I terminate application "stress_calc_2"
    And I terminate application "stress_calc_3"
    And I terminate application "stress_notepad_1"
    And I terminate application "stress_notepad_2"
    And I terminate application "stress_paint_1"
    
    # ✅ VERIFY CLEAN STRESS TEST COMPLETION
    Then application "stress_calc_1" should have exactly 0 instances
    And application "stress_calc_2" should have exactly 0 instances
    And application "stress_calc_3" should have exactly 0 instances
    And application "stress_notepad_1" should have exactly 0 instances
    And application "stress_notepad_2" should have exactly 0 instances
    And application "stress_paint_1" should have exactly 0 instances
