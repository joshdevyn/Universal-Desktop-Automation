Feature: Simplified Notepad Testing - Application Agnostic
  As a QA engineer
  I want to test notepad functionality using managed application context
  So that I can validate text editing without predefined configurations

  @simplified @notepad @smoke @managed-context
  Scenario: Basic Notepad Text Entry with Managed Context
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    And I take a screenshot of managed application "notepad" with name "launched"
    When I type "Hello World!" in managed application "notepad"
    And I press "ENTER" key in managed application "notepad"
    When I type "This is a test of the application agnostic framework." in managed application "notepad"
    Then I should see the text "Hello World!" in managed application "notepad"
    And I should see the text "application agnostic framework" in managed application "notepad"
    And I capture evidence of managed application "notepad" with description "Text entry successful"
    When I terminate the managed application "notepad"

  @simplified @notepad @demo @managed-context
  Scenario: Notepad File Operations - Managed Context
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    And I type "Test Document" in managed application "notepad"
    And I press "ENTER" key in managed application "notepad"
    When I type "Line 2 of content" in managed application "notepad"
    And I take a screenshot of managed application "notepad" with name "content"
    # Test Ctrl+A (Select All)
    When I press "CTRL+A" key combination in managed application "notepad"
    And I wait 1 seconds
    # Test Ctrl+C (Copy)
    When I press "CTRL+C" key combination in managed application "notepad"
    And I wait 1 seconds
    # Test Delete
    When I press "DELETE" key in managed application "notepad"
    And I wait 1 seconds
    # Test Ctrl+V (Paste)
    When I press "CTRL+V" key combination in managed application "notepad"
    Then I should see the text "Test Document" in managed application "notepad"
    And I capture evidence of managed application "notepad" with description "Copy-paste operations successful"
    When I terminate the managed application "notepad"

  @simplified @notepad @critical @managed-context
  Scenario: Notepad Window State Management - Managed Context
    When I launch the application at path "C:\Windows\System32\notepad.exe" as "notepad"
    And I type "Testing window operations" in managed application "notepad"
    And I take a screenshot of managed application "notepad" with name "normal"
    When I maximize managed application "notepad"
    And I take a screenshot of managed application "notepad" with name "maximized"
    When I restore managed application "notepad"
    And I wait 1 seconds
    When I minimize managed application "notepad"
    When I restore managed application "notepad"
    And I bring managed application "notepad" to front
    And I capture evidence of managed application "notepad" with description "Window state operations completed"
    When I terminate the managed application "notepad"
