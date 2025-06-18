Feature: Enterprise Mock Applications - PID-Driven Architecture
  As an enterprise automation specialist
  I want to test mock applications using direct executable paths
  So that I can achieve reliable testing in enterprise environments

  Background:
    Given the ProcessManager is initialized for enterprise operations
    And all mock application instances are cleaned up

  @pid-driven @enterprise @as400 @critical
  Scenario: AS400 Terminal Mock - Complete Enterprise Workflow
    # LAUNCH AS400 MOCK BY JAR PATH
    When I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "as400_session_1"
    Then the application "as400_session_1" should be running
    And the last launched process should have PID greater than 0
    
    # REGISTER AS MANAGED APPLICATION FOR SAFETY
    When I register the newest running process "java" as managed application "AS400Terminal"
    
    # AS400 LOGIN SEQUENCE
    When I wait for 3 seconds
    And I type "TESTUSER123" in managed application "AS400Terminal"
    And I press "ENTER" key in managed application "AS400Terminal"
    And I wait for 2 seconds
    
    # VERIFY LOGIN SUCCESS
    Then I should see the text "MAIN MENU" using OCR
    And I capture evidence with description "AS400 Mock - Login successful"
    
    # NAVIGATE TO CUSTOMER INQUIRY
    When I type "1" in managed application "AS400Terminal"
    And I press "ENTER" key in managed application "AS400Terminal"
    And I wait for 2 seconds
    
    # VERIFY CUSTOMER SCREEN
    Then I should see the text "CUSTOMER INQUIRY" using OCR
    
    # SEARCH FOR CUSTOMER
    When I type "CUST001" in managed application "AS400Terminal"
    And I press "ENTER" key in managed application "AS400Terminal"
    And I wait for 2 seconds
    
    # VERIFY CUSTOMER DATA
    Then I should see the text "CUSTOMER INFORMATION" using OCR
    And I capture evidence with description "AS400 Mock - Customer inquiry completed"
    
    # CLEAN TERMINATION
    When I terminate application "as400_session_1"
    Then application "as400_session_1" should have exactly 0 instances

  @pid-driven @enterprise @sap @critical
  Scenario: SAP GUI Mock - Enterprise Business Process
    # LAUNCH SAP MOCK BY JAR PATH
    When I launch the application at path "target/mock-apps/SAPGUIMock-sap-mock.jar" as "sap_gui_session"
    Then the application "sap_gui_session" should be running
    
    # REGISTER AS MANAGED APPLICATION FOR SAFETY
    When I register the newest running process "java" as managed application "SAPGUIApp"
    
    # SAP LOGIN SEQUENCE
    When I wait for 3 seconds
    And I type "SAPUSER" in managed application "SAPGUIApp"
    And I press "TAB" key in managed application "SAPGUIApp"
    And I type "PASSWORD123" in managed application "SAPGUIApp"
    And I press "ENTER" key in managed application "SAPGUIApp"
    And I wait for 3 seconds
    
    # VERIFY SAP MAIN SCREEN
    Then I should see the text "SAP Easy Access" using OCR
    And I capture evidence with description "SAP Mock - Login and main screen"
    
    # EXECUTE TRANSACTION
    When I type "/nMM01" in managed application "SAPGUIApp"
    And I press "ENTER" key in managed application "SAPGUIApp"
    And I wait for 2 seconds
    
    # VERIFY TRANSACTION SCREEN
    Then I should see the text "Create Material" using OCR
    And I capture evidence with description "SAP Mock - Transaction MM01 executed"
    
    # ENTERPRISE CLEANUP
    When I terminate application "sap_gui_session"
    Then application "sap_gui_session" should have exactly 0 instances

  @pid-driven @enterprise @oracle @critical
  Scenario: Oracle Forms Mock - Database Application Testing
    # LAUNCH ORACLE FORMS MOCK
    When I launch the application at path "target/mock-apps/OracleFormsMock-oracle-forms-mock.jar" as "oracle_forms_session"
    Then the application "oracle_forms_session" should be running
    
    # REGISTER AS MANAGED APPLICATION FOR SAFETY
    When I register the newest running process "java" as managed application "OracleFormsApp"
    
    # ORACLE FORMS LOGIN
    When I wait for 3 seconds
    And I type "ORACLE_USER" in managed application "OracleFormsApp"
    And I press "TAB" key in managed application "OracleFormsApp"
    And I type "ORACLE_PASS" in managed application "OracleFormsApp"
    And I press "ENTER" key in managed application "OracleFormsApp"
    And I wait for 3 seconds
    
    # VERIFY FORMS ENVIRONMENT
    Then I should see the text "Oracle Forms" using OCR
    And I capture evidence with description "Oracle Forms Mock - Forms environment loaded"
    
    # NAVIGATE TO CUSTOMER FORM
    When I press "F4" key in managed application "OracleFormsApp"
    And I wait for 1 seconds
    And I type "CUSTOMERS" in managed application "OracleFormsApp"
    And I press "ENTER" key in managed application "OracleFormsApp"
    And I wait for 2 seconds
    
    # VERIFY CUSTOMER FORM
    Then I should see the text "Customer Master" using OCR
    
    # QUERY CUSTOMER DATA
    When I type "123456" in managed application "OracleFormsApp"
    And I press "F8" key in managed application "OracleFormsApp"
    And I wait for 2 seconds
    
    # VERIFY QUERY RESULTS
    Then I should see the text "Customer Details" using OCR
    And I capture evidence with description "Oracle Forms Mock - Customer query successful"
    
    # FORMS CLEANUP
    When I terminate application "oracle_forms_session"
    Then application "oracle_forms_session" should have exactly 0 instances

  @pid-driven @enterprise @excel @critical
  Scenario: Excel Mock - Office Application Automation
    # LAUNCH EXCEL MOCK
    When I launch the application at path "target/mock-apps/ExcelMock-excel-mock.jar" as "excel_session"
    Then the application "excel_session" should be running
    
    # REGISTER AS MANAGED APPLICATION FOR SAFETY
    When I register the newest running process "java" as managed application "ExcelApp"
    
    # EXCEL OPERATIONS
    When I wait for 3 seconds
    And I type "Sales Data" in managed application "ExcelApp"
    And I press "ENTER" key in managed application "ExcelApp"
    And I type "100" in managed application "ExcelApp"
    And I press "TAB" key in managed application "ExcelApp"
    And I type "200" in managed application "ExcelApp"
    And I press "TAB" key in managed application "ExcelApp"
    And I type "300" in managed application "ExcelApp"
    
    # VERIFY EXCEL DATA
    Then I should see the text "Sales Data" using OCR
    And I capture evidence with description "Excel Mock - Data entry completed"
    
    # CREATE FORMULA
    When I press "ENTER" key in managed application "ExcelApp"
    And I type "=SUM(B1:D1)" in managed application "ExcelApp"
    And I press "ENTER" key in managed application "ExcelApp"
    
    # VERIFY CALCULATION
    Then I should see the text "600" using OCR
    And I capture evidence with description "Excel Mock - Formula calculation"
    
    # OFFICE CLEANUP
    When I terminate application "excel_session"
    Then application "excel_session" should have exactly 0 instances

  @pid-driven @enterprise @multi-system @advanced
  Scenario: Multi-System Enterprise Integration Test
    # LAUNCH MULTIPLE ENTERPRISE SYSTEMS
    When I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "as400_integration"
    And I launch the application at path "target/mock-apps/SAPGUIMock-sap-mock.jar" as "sap_integration"
    And I launch the application at path "target/mock-apps/ExcelMock-excel-mock.jar" as "excel_integration"
    
    # VERIFY ALL SYSTEMS RUNNING
    Then the application "as400_integration" should be running
    And the application "sap_integration" should be running
    And the application "excel_integration" should be running
    
    # REGISTER MANAGED APPLICATIONS FOR SAFETY
    When I register the newest running process "java" as managed application "AS400Integration"
    And I register the process matching "java.*SAPGUIMock" as managed application "SAPIntegration"  
    And I register the process matching "java.*ExcelMock" as managed application "ExcelIntegration"
    
    # CROSS-SYSTEM DATA FLOW SIMULATION
    # Step 1: Extract data from AS400
    When I switch to managed application "AS400Integration"
    And I wait for 3 seconds
    And I type "ADMIN" in managed application "AS400Integration"
    And I press "ENTER" key in managed application "AS400Integration"
    And I wait for 2 seconds
    And I type "2" in managed application "AS400Integration"
    And I press "ENTER" key in managed application "AS400Integration"
    And I wait for 2 seconds
    
    # Step 2: Process in SAP
    When I switch to managed application "SAPIntegration"
    And I wait for 3 seconds
    And I type "SAPUSER" in managed application "SAPIntegration"
    And I press "TAB" key in managed application "SAPIntegration"
    And I type "PASS123" in managed application "SAPIntegration"
    And I press "ENTER" key in managed application "SAPIntegration"
    And I wait for 3 seconds
    
    # Step 3: Report in Excel
    When I switch to managed application "ExcelIntegration"
    And I wait for 3 seconds
    And I type "Integration Test Results" in managed application "ExcelIntegration"
    And I press "ENTER" key in managed application "ExcelIntegration"
    And I type "AS400 Data: Extracted" in managed application "ExcelIntegration"
    And I press "ENTER" key in managed application "ExcelIntegration"
    And I type "SAP Processing: Complete" in managed application "ExcelIntegration"
    
    # VERIFY INTEGRATION SUCCESS
    Then I should see the text "Integration Test Results" using OCR
    And I capture evidence with description "Multi-system enterprise integration completed"
    
    # CLEAN ENTERPRISE SHUTDOWN
    When I terminate application "as400_integration"
    And I terminate application "sap_integration"
    And I terminate application "excel_integration"
    
    Then application "as400_integration" should have exactly 0 instances
    And application "sap_integration" should have exactly 0 instances
    And application "excel_integration" should have exactly 0 instances

  @pid-driven @enterprise @load-test @stress
  Scenario: Enterprise Load Testing - Multiple Mock Instances
    # LAUNCH MULTIPLE INSTANCES OF ENTERPRISE APPS
    When I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "as400_load_1"
    And I launch the application at path "target/mock-apps/AS400TerminalMock-as400-mock.jar" as "as400_load_2"
    And I launch the application at path "target/mock-apps/SAPGUIMock-sap-mock.jar" as "sap_load_1"
    And I launch the application at path "target/mock-apps/SAPGUIMock-sap-mock.jar" as "sap_load_2"
    
    # VERIFY LOAD TEST SETUP
    Then the application "as400_load_1" should be running
    And the application "as400_load_2" should be running
    And the application "sap_load_1" should be running
    And the application "sap_load_2" should be running
    
    # REGISTER MANAGED APPLICATIONS FOR LOAD TEST
    When I register the process matching "java.*AS400.*as400_load_1" as managed application "AS400Load1"
    And I register the process matching "java.*AS400.*as400_load_2" as managed application "AS400Load2"
    And I register the process matching "java.*SAP.*sap_load_1" as managed application "SAPLoad1"
    And I register the process matching "java.*SAP.*sap_load_2" as managed application "SAPLoad2"
    
    # SIMULATE CONCURRENT USER ACTIVITY
    When I switch to managed application "AS400Load1"
    And I type "USER1" in managed application "AS400Load1"
    When I switch to managed application "AS400Load2"
    And I type "USER2" in managed application "AS400Load2"
    When I switch to managed application "SAPLoad1"
    And I type "SAPUSER1" in managed application "SAPLoad1"
    When I switch to managed application "SAPLoad2"
    And I type "SAPUSER2" in managed application "SAPLoad2"
    
    # VERIFY CONCURRENT OPERATIONS
    And I capture evidence with description "Enterprise load test - concurrent operations"
    
    # LOAD TEST CLEANUP
    When I terminate application "as400_load_1"
    And I terminate application "as400_load_2"
    And I terminate application "sap_load_1"
    And I terminate application "sap_load_2"
    
    Then application "as400_load_1" should have exactly 0 instances
    And application "as400_load_2" should have exactly 0 instances
    And application "sap_load_1" should have exactly 0 instances
    And application "sap_load_2" should have exactly 0 instances
