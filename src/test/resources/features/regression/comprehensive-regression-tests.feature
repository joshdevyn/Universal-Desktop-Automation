@Regression @Comprehensive @SystemValidation @Stability
Feature: Comprehensive Regression Testing - PID-Driven Multi-Application Validation
  As a QA engineer
  I want to ensure that existing functionality continues to work after changes
  So that I can prevent regressions and maintain system stability across all applications

  Background:
    Given the ProcessManager is initialized for comprehensive testing
    And all previous application processes are terminated

  @Regression @OracleForms @FunctionKeys @Comprehensive @pid-driven
  Scenario: Oracle Forms Complete Function Key Regression with PID Management
    When I launch the application at path "target/mock-apps/OracleFormsMock-oracle-forms-mock.jar" as "OracleForms_Regression"
    Then the application "OracleForms_Regression" should be running
    And I capture evidence of managed application "OracleForms_Regression" with description "Starting Oracle Forms function key regression"
    
    # Forms Login Sequence
    When I wait for 3 seconds
    And I type "testuser" in managed application "OracleForms_Regression"
    And I press "TAB" key in managed application "OracleForms_Regression"
    And I type "testpass" in managed application "OracleForms_Regression"
    And I press "ENTER" key in managed application "OracleForms_Regression"
    And I wait for 3 seconds
    
    Then I should see the text "Welcome" using OCR
    And I capture evidence of managed application "OracleForms_Regression" with description "Oracle Forms login successful"
    
    # Test F6 - New Record
    When I press "F6" key in managed application "OracleForms_Regression"
    And I wait for 2 seconds
    Then I should see the text "New Record" using OCR
    And I capture evidence of managed application "OracleForms_Regression" with description "F6 - New Record function working"
    
    # Test F7 - Enter Query
    When I press "F7" key in managed application "OracleForms_Regression"
    And I wait for 2 seconds
    Then I should see the text "Enter Query" using OCR
    And I capture evidence of managed application "OracleForms_Regression" with description "F7 - Enter Query function working"
    
    # Test F8 - Execute Query
    When I press "F8" key in managed application "OracleForms_Regression"
    And I wait for 3 seconds
    Then I should see the text "Query" using OCR
    And I capture evidence of managed application "OracleForms_Regression" with description "F8 - Execute Query function working"
    
    # Test F10 - Save
    When I press "F6" key in managed application "OracleForms_Regression"
    And I wait for 1 seconds
    And I type "TEST_DATA" in managed application "OracleForms_Regression"
    And I press "F10" key in managed application "OracleForms_Regression"
    And I wait for 3 seconds
    Then I should see the text "Saved" using OCR
    And I capture evidence of managed application "OracleForms_Regression" with description "F10 - Save function working"
    
    # Cleanup
    When I terminate application "OracleForms_Regression"
    Then application "OracleForms_Regression" should have exactly 0 instances

  @Regression @SAPGUI @TransactionCodes @Comprehensive @pid-driven
  Scenario: SAP GUI Transaction Code Regression Testing with PID Management
    When I launch the application at path "target/mock-apps/SAPGUIMock-sap-mock.jar" as "SAPGUI_Regression"
    Then the application "SAPGUI_Regression" should be running
    And I capture evidence of managed application "SAPGUI_Regression" with description "Starting SAP GUI transaction regression"
    
    # SAP Login Sequence
    When I wait for 3 seconds
    And I type "100" in managed application "SAPGUI_Regression"
    And I press "TAB" key in managed application "SAPGUI_Regression"
    And I type "testuser" in managed application "SAPGUI_Regression"
    And I press "TAB" key in managed application "SAPGUI_Regression"
    And I type "testpass" in managed application "SAPGUI_Regression"
    And I press "ENTER" key in managed application "SAPGUI_Regression"
    And I wait for 3 seconds
    
    Then I should see the text "SAP Easy Access" using OCR
    And I capture evidence of managed application "SAPGUI_Regression" with description "SAP GUI login successful"
    
    # Test VA01 - Create Sales Order
    When I type "/nVA01" in managed application "SAPGUI_Regression"
    And I press "ENTER" key in managed application "SAPGUI_Regression"
    And I wait for 3 seconds
    Then I should see the text "Create Sales Order" using OCR
    And I capture evidence of managed application "SAPGUI_Regression" with description "VA01 transaction working"
    
    When I press "F3" key in managed application "SAPGUI_Regression"
    And I wait for 2 seconds
    Then I should see the text "SAP Easy Access" using OCR
    
    # Test VA03 - Display Sales Order
    When I type "/nVA03" in managed application "SAPGUI_Regression"
    And I press "ENTER" key in managed application "SAPGUI_Regression"
    And I wait for 3 seconds
    Then I should see the text "Display Sales Order" using OCR
    And I capture evidence of managed application "SAPGUI_Regression" with description "VA03 transaction working"
    
    When I press "F3" key in managed application "SAPGUI_Regression"
    And I wait for 2 seconds
    Then I should see the text "SAP Easy Access" using OCR
    
    # Test MM03 - Display Material
    When I type "/nMM03" in managed application "SAPGUI_Regression"
    And I press "ENTER" key in managed application "SAPGUI_Regression"
    And I wait for 3 seconds
    Then I should see the text "Display Material" using OCR
    And I capture evidence of managed application "SAPGUI_Regression" with description "MM03 transaction working"
    
    When I press "F3" key in managed application "SAPGUI_Regression"
    And I wait for 2 seconds
    Then I should see the text "SAP Easy Access" using OCR
    And I capture evidence of managed application "SAPGUI_Regression" with description "All SAP transactions tested"
    
    # Cleanup
    When I terminate application "SAPGUI_Regression"
    Then application "SAPGUI_Regression" should have exactly 0 instances

  @Regression @AS400 @Navigation @Comprehensive @pid-driven
  Scenario: AS400 Terminal Navigation Regression with PID Management
    When I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "AS400_Regression"
    Then the application "AS400_Regression" should be running
    And I capture evidence of managed application "AS400_Regression" with description "Starting AS400 navigation regression"
    
    # AS400 Login Sequence
    When I wait for 3 seconds
    And I type "testuser" in managed application "AS400_Regression"
    And I press "ENTER" key in managed application "AS400_Regression"
    And I wait for 3 seconds
    
    Then I should see the text "MAIN MENU" using OCR
    And I capture evidence of managed application "AS400_Regression" with description "AS400 login successful"
    
    # Test Option 1 - Customer Inquiry
    When I type "1" in managed application "AS400_Regression"
    And I press "ENTER" key in managed application "AS400_Regression"
    And I wait for 2 seconds
    Then I should see the text "CUSTOMER INQUIRY" using OCR
    And I capture evidence of managed application "AS400_Regression" with description "Option 1 navigation working"
    
    When I press "F12" key in managed application "AS400_Regression"
    And I wait for 2 seconds
    Then I should see the text "MAIN MENU" using OCR
    
    # Test Option 2 - Inventory Inquiry
    When I type "2" in managed application "AS400_Regression"
    And I press "ENTER" key in managed application "AS400_Regression"
    And I wait for 2 seconds
    Then I should see the text "INVENTORY INQUIRY" using OCR
    And I capture evidence of managed application "AS400_Regression" with description "Option 2 navigation working"
    
    When I press "F12" key in managed application "AS400_Regression"
    And I wait for 2 seconds
    Then I should see the text "MAIN MENU" using OCR
    
    # Test Option 3 - Reports
    When I type "3" in managed application "AS400_Regression"
    And I press "ENTER" key in managed application "AS400_Regression"
    And I wait for 2 seconds
    Then I should see the text "REPORTS" using OCR
    And I capture evidence of managed application "AS400_Regression" with description "Option 3 navigation working"
    
    When I press "F12" key in managed application "AS400_Regression"
    And I wait for 2 seconds
    Then I should see the text "MAIN MENU" using OCR
    And I capture evidence of managed application "AS400_Regression" with description "All AS400 navigation tested"
    
    # Cleanup
    When I terminate application "AS400_Regression"
    Then application "AS400_Regression" should have exactly 0 instances

  @Regression @MultiApplication @CrossSystem @Comprehensive @pid-driven
  Scenario: Multi-Application Cross-System Regression with PID Management
    # Launch all enterprise applications for comprehensive testing
    When I launch the application at path "target/mock-apps/OracleFormsMock-oracle-forms-mock.jar" as "Forms_Multi"
    And I launch the application at path "target/mock-apps/SAPGUIMock-sap-mock.jar" as "SAP_Multi"
    And I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "AS400_Multi"
    
    Then the application "Forms_Multi" should be running
    And the application "SAP_Multi" should be running
    And the application "AS400_Multi" should be running
    
    # Test switching between applications
    When I switch to managed application "Forms_Multi"
    And I wait for 2 seconds
    And I type "Forms_Data" in managed application "Forms_Multi"
    And I capture evidence of managed application "Forms_Multi" with description "Forms multi-app test"
    
    When I switch to managed application "SAP_Multi"
    And I wait for 2 seconds
    And I type "SAP_Data" in managed application "SAP_Multi"
    And I capture evidence of managed application "SAP_Multi" with description "SAP multi-app test"
    
    When I switch to managed application "AS400_Multi"
    And I wait for 2 seconds
    And I type "AS400_Data" in managed application "AS400_Multi"
    And I capture evidence of managed application "AS400_Multi" with description "AS400 multi-app test"
    
    # Verify all applications still running
    Then the application "Forms_Multi" should be running
    And the application "SAP_Multi" should be running
    And the application "AS400_Multi" should be running
    
    And I capture evidence with description "Multi-application regression test completed"
    
    # Cleanup all applications
    When I terminate application "Forms_Multi"
    And I terminate application "SAP_Multi"
    And I terminate application "AS400_Multi"
    
    Then application "Forms_Multi" should have exactly 0 instances
    And application "SAP_Multi" should have exactly 0 instances
    And application "AS400_Multi" should have exactly 0 instances
