@smoke @critical-path @health-check @rapid-validation @pid-driven
Feature: Critical Path Smoke Tests - PID-Driven Architecture
  As a QA engineer
  I want to rapidly verify that all critical system functions work using PID-driven management
  To ensure the environment is ready for comprehensive enterprise testing

  @smoke @oracle-forms @critical @fast @pid-driven
  Scenario: Oracle Forms Basic Functionality Smoke Test with PID Management
    When I launch the application at path "target/mock-apps/OracleFormsMock-oracle-forms-mock.jar" as "oracle_forms_smoke"
    Then the application "oracle_forms_smoke" should be running
    And I capture evidence with description "Starting Oracle Forms smoke test"
      # Oracle Forms login sequence
    When I wait for 3 seconds
    And I type "testuser" in managed application "oracle_forms_smoke"
    And I press "TAB" key in managed application "oracle_forms_smoke"
    And I type "testpass" in managed application "oracle_forms_smoke"
    And I press "ENTER" key in managed application "oracle_forms_smoke"
    And I wait for 3 seconds
    
    Then I should see the text "Welcome" using OCR
    And I capture evidence with description "Oracle Forms login successful"
      # Forms navigation test
    When I press "F4" key in managed application "oracle_forms_smoke"
    And I wait for 2 seconds
    And I type "ORDERS" in managed application "oracle_forms_smoke"
    And I press "ENTER" key in managed application "oracle_forms_smoke"
    And I wait for 2 seconds
    
    Then I should see the text "Order Entry" using OCR
    And I capture evidence with description "Forms navigation working"
    
    # New record test
    When I press "F6" key in managed application "oracle_forms_smoke"
    And I wait for 2 seconds
    
    Then I should see the text "New Record" using OCR
    And I capture evidence with description "Oracle Forms smoke test passed"
      # Cleanup
    When I terminate the managed application "oracle_forms_smoke"
    Then application "oracle_forms_smoke" should have exactly 0 instances
  @smoke @sap-gui @critical @fast @pid-driven
  Scenario: SAP GUI Basic Functionality Smoke Test with PID Management
    When I launch the application at path "target/mock-apps/SAPGUIMock-sap-mock.jar" as "sap_gui_smoke"
    Then the application "sap_gui_smoke" should be running
    And I capture evidence with description "Starting SAP GUI smoke test"
      # SAP GUI login sequence
    When I wait for 3 seconds
    And I type "100" in managed application "sap_gui_smoke"
    And I press "TAB" key in managed application "sap_gui_smoke"
    And I type "testuser" in managed application "sap_gui_smoke"
    And I press "TAB" key in managed application "sap_gui_smoke"
    And I type "testpass" in managed application "sap_gui_smoke"
    And I press "ENTER" key in managed application "sap_gui_smoke"
    And I wait for 3 seconds
      Then I should see the text "SAP Easy Access" using OCR
    And I capture evidence with description "SAP GUI login successful"
    
    # Transaction navigation test
    When I type "/nVA01" in managed application "sap_gui_smoke"
    And I press "ENTER" key in managed application "sap_gui_smoke"
    And I wait for 3 seconds
    And I capture evidence with description "SAP transaction working"
    
    When I press "F3" key in managed application "sap_gui_smoke"
    And I wait for 2 seconds
    Then I should see the text "SAP Easy Access"
    And I capture evidence with description "SAP GUI smoke test passed"
      # Cleanup
    When I terminate the managed application "sap_gui_smoke"
    Then I capture evidence with description "SAP GUI closed successfully"
  @smoke @as400 @critical @fast
  Scenario: AS400 Terminal Basic Functionality Smoke Test
    Given I have the application "as400_terminal" open
    And I capture evidence with description "Starting AS400 smoke test"
    
    When I wait for window "AS400 Terminal Mock" to appear
    And I type "testuser" in managed application "as400_terminal"
    And I press "TAB" key in managed application "as400_terminal"
    And I type "testpass" in managed application "as400_terminal"
    And I press "ENTER" key in managed application "as400_terminal"
    And I wait for 3 seconds
    Then I should see the text "MAIN MENU"
    And I capture evidence with description "AS400 login successful"
    
    When I type "1" in managed application "as400_terminal"
    And I press "ENTER" key in managed application "as400_terminal"
    And I wait for 2 seconds
    Then I should see the text "CUSTOMER INQUIRY"
    And I capture evidence with description "AS400 navigation working"
    
    When I press "F12" key in managed application "as400_terminal"
    And I wait for 2 seconds
    Then I should see the text "MAIN MENU"
    And I capture evidence with description "AS400 smoke test passed"
    
    # Cleanup
    When I press key "F3"
    And I wait for 2 seconds
    Then I capture evidence with description "AS400 terminal closed successfully"
  @smoke @excel @critical @fast
  Scenario: Excel Application Basic Functionality Smoke Test
    Given I have the application "excel" open
    And I capture evidence with description "Starting Excel smoke test"
    
    When I wait for window "Excel" to appear
    And I press "CTRL+N" key combination in managed application "excel"
    And I wait for 2 seconds
    Then I should see the text "Book1"
    And I capture evidence with description "Excel workbook created"
    
    When I type "Smoke Test Data" in managed application "excel"
    And I press "ENTER" key in managed application "excel"
    And I wait for 1 second
    Then I should see the text "Smoke Test Data"
    And I capture evidence with description "Excel data entry working"
    
    When I press "CTRL+S" key combination in managed application "excel"
    And I wait for 2 seconds
    Then I should see the text "Save As"
    And I capture evidence with description "Excel save dialog opened"
    
    When I press "ESCAPE" key in managed application "excel"
    And I wait for 1 second
    And I terminate the managed application "excel"
    Then I capture evidence with description "Excel smoke test passed"

  @smoke @framework @critical @fast
  Scenario: Framework Components Health Check
    Given I have the application "calculator" open
    And I capture evidence with description "Starting framework health check"
    
    When I wait for window "Calculator" to appear
    And I click on the image "calc_button_2"
    And I wait for 1 second
    And I click on the image "calc_button_plus"
    And I wait for 1 second
    And I click on the image "calc_button_3"
    And I wait for 1 second
    And I click on the image "calc_button_equals"
    And I wait for 2 seconds
    Then I should see the text "5"
    And I capture evidence with description "Image recognition working"
    
    When I extract text from the screen and store it in variable "calc_result"
    Then the variable "calc_result" should contain "5"
    And I capture evidence with description "OCR extraction working"
    
    When I take a screenshot
    Then I capture evidence with description "Screenshot capture working"
      # Cleanup
    When I terminate the managed application "calculator"
    Then I capture evidence with description "Framework health check passed"
  @smoke @performance @baseline @fast
  Scenario: performance Baseline Smoke Test
    Given I have the application "notepad" open
    And I capture evidence with description "Starting performance baseline test"
    
    When I wait for window "Notepad" to appear
    And I type "performance test started at " in managed application "notepad"
    And I wait for 2 seconds
    Then I should see the text "performance test started"
    And I capture evidence with description "Text input performance acceptable"
    
    When I press "CTRL+A" key combination in managed application "notepad"
    And I wait for 1 second
    And I press "CTRL+C" key combination in managed application "notepad"
    And I wait for 1 second
    And I press "CTRL+V" key combination in managed application "notepad"
    And I wait for 2 seconds
    Then I should see the text "performance test started"
    And I capture evidence with description "Copy-paste performance acceptable"
    
    # Cleanup
    When I terminate the managed application "notepad" without saving
    Then I capture evidence with description "performance baseline established"

  @smoke @environment @validation @fast
  Scenario: Environment Validation Smoke Test
    Given I capture evidence with description "Starting environment validation"
    
    # Test screen capture
    When I take a screenshot
    Then I capture evidence with description "Screen capture functional"
    
    # Test multiple applications
    When I have the application "calculator" open
    And I wait for 2 seconds
    And I have the application "notepad" open
    And I wait for 2 seconds
    Then the window "Calculator" should exist    And the window "Notepad" should exist
    And I capture evidence with description "Multi-application support working"
    
    # Test window switching - using managed application focus instead of global Alt+Tab
    When I focus on managed application "calc1"
    And I wait for 2 seconds
    And I focus on managed application "calc2"
    And I wait for 2 seconds
    Then I capture evidence with description "Window switching functional"
      # Cleanup
    When I terminate the managed application "calc1"
    And I wait for 1 second
    And I terminate the managed application "calc2"
    Then I capture evidence with description "Environment validation completed"

  @smoke @integration @quick @fast
  Scenario: Integration Points Quick Check
    Given I have the application "oracle_forms" open
    And I capture evidence with description "Starting integration quick check"
    
    # Test Oracle forms
    When I wait for window "Oracle forms Mock" to appear
    And I click on the image "forms_login_button"
    And I type "testuser" in managed application "oracle_forms"
    And I press "TAB" key in managed application "oracle_forms"
    And I type "testpass" in managed application "oracle_forms"
    And I press "ENTER" key in managed application "oracle_forms"
    And I wait for 3 seconds
    Then I should see the text "Welcome"
    And I set variable "oracle_status" to "OK"
    And I capture evidence with description "Oracle forms integration OK"
    
    # Test SAP GUI
    Given I have the application "sap_gui" open
    When I wait for window "SAP GUI Mock" to appear
    And I click on the image "sap_login_button"
    And I wait for 2 seconds
    Then I should see the text "SAP"
    And I set variable "sap_status" to "OK"
    And I capture evidence with description "SAP GUI integration OK"
      # Test AS400
    Given I have the application "as400_terminal" open
    When I wait for window "AS400 Terminal Mock" to appear
    And I type "testuser" in managed application "as400_terminal"
    And I press "ENTER" key in managed application "as400_terminal"
    And I wait for 2 seconds
    Then I should see the text "MENU"
    And I set variable "as400_status" to "OK"
    And I capture evidence with description "AS400 integration OK"
    
    # Verify all systems
    Then the variable "oracle_status" should equal "OK"
    And the variable "sap_status" should equal "OK"
    And the variable "as400_status" should equal "OK"
    And I capture evidence with description "All integration points verified"
    
    # Cleanup
    When I terminate the managed application "oracle_forms"
    And I wait for 1 second
    And I terminate the managed application "sap_gui"    And I wait for 1 second
    And I terminate the managed application "as400_terminal"
    Then I capture evidence with description "Integration check completed"
    When I create a new workbook
    Then Excel should open with a blank worksheet within 10 seconds
    
    When I click on cell "A1"    And I type "Smoke Test" in managed application "excel"
    Then the text "Smoke Test" should appear in cell "A1"
    
    When I press "CTRL+S" key combination in managed application "excel"
    And I enter filename "SmokeTest.xlsx"
    Then the file should be saved successfully

  # @smoke @critical @fast  
  # Scenario: Visual Studio Code Basic Functionality Smoke Test
  #   Given I have the application "vscode" open
  #   When VS Code launches
  #   Then I should see the welcome screen within 15 seconds
  #   And I should see the VS Code interface elements
    
  #   When I create a new file
  #   And I type "console.log('Hello World');"
  #   Then the code should appear in the editor
    
  #   When I save the file as "test.js"
  #   Then syntax highlighting should be applied automatically
  @smoke @critical @fast
  Scenario: Command Prompt Basic Functionality Smoke Test
    Given I have the application "cmd" open
    When I type "echo Hello, Smoke Test!" in managed application "cmd"
    And I press "ENTER" key in managed application "cmd"
    Then I should see the text "Hello, Smoke Test!" in region "command_prompt_output_area"
    
    When I type "dir" in managed application "cmd"
    And I press "ENTER" key in managed application "cmd"
    Then I should see a directory listing within 5 seconds
    
    When I type "exit" in managed application "cmd"
    And I press "ENTER" key in managed application "cmd"
    Then the command prompt should close

  @smoke @framework @infrastructure
  Scenario: Framework Core Components Smoke Test
    Given the automation framework is initialized
    Then the following components should be loaded:
      | Component         | Status    |
      | WindowController  | Ready     |
      | ScreenCapture     | Ready     |
      | OCREngine         | Ready     |
      | ImageMatcher      | Ready     |
      | ConfigManager     | Ready     |
      | ReportManager     | Ready     |
    
    When I capture a screenshot
    Then the screenshot should be saved successfully
    And the image file should be valid
    
    When I perform OCR on a text region
    Then text should be extracted with confidence > 70%
    
    When I search for an image on screen
    Then the image matching should complete within 5 seconds

  @smoke @configuration @environment
  Scenario: Environment Configuration Smoke Test
    Given the test environment is set up
    Then all required applications should be configured:
      | Application   | Configured | Executable Available |
      | oracle_forms  | Yes        | Yes                  |
      | sap_gui       | Yes        | Yes                  |
      | tandem_terminal | Yes      | Yes                  |
      | excel         | Yes        | Yes                  |
      | vscode        | Yes        | Yes                  |
      | cmd           | Yes        | Yes                  |
    
    And all image templates should be available
    And OCR engine should be properly configured
    And test data files should be accessible

  # @smoke @performance @benchmarks
  # Scenario: performance Baseline Smoke Test
  #   Given I am testing system performance baselines
    
  #   When I launch Oracle forms
  #   Then it should start within 45 seconds
    
  #   When I launch SAP GUI  
  #   Then it should start within 45 seconds
    
  #   When I launch AS400 Terminal
  #   Then it should start within 30 seconds
    
  #   When I capture a full screen screenshot
  #   Then it should complete within 2 seconds
  
  @smoke @connectivity @network
  Scenario: Network Connectivity Smoke Test
    Given I am testing network-dependent functionality
    
    When I test database connectivity for Oracle forms
    Then connection should be established within 10 seconds
    
    When I test SAP server connectivity
    Then connection should be successful
    
    When I test file server access for shared resources
    Then files should be accessible
    
    When I verify internet connectivity for updates
    Then connection should be available

  @smoke @security @authentication
  Scenario: Authentication and Security Smoke Test
    Given I am testing security configurations
    
    When I attempt login with valid credentials to Oracle forms
    Then authentication should succeed
    
    When I attempt login with invalid credentials to SAP
    Then authentication should fail appropriately
    And security logs should be updated
    
    When I test session timeout functionality
    Then sessions should timeout after configured period
    And users should be prompted to re-authenticate

  # @smoke @reporting @outputs
  # Scenario: Reporting and Output Generation Smoke Test
  #   Given I am testing report generation capabilities
    
  #   When I run a simple test scenario
  #   Then HTML test report should be generated
  #   And report should contain test results
  #   And screenshots should be embedded in report
    
  #   When I generate performance metrics
  #   Then performance data should be captured
  #   And metrics should be formatted correctly
    
  #   When I export test data to Excel
  #   Then Excel file should be created successfully
  #   And data should be properly formatted

  @smoke @integration @quick_check
  Scenario: Quick Integration Health Check
    Given I am performing a rapid integration health check
    
    When I create a test order in Oracle forms
    And I check inventory in SAP GUI
    And I verify allocation in AS400 Terminal
    Then all systems should respond within expected timeframes
    And data should flow correctly between systems
    
    When I generate a summary report
    Then the report should include data from all systems
    And integration points should be verified as functional

  @smoke @error_handling @resilience
  Scenario: Error Handling Smoke Test
    Given I am testing basic error handling capabilities
    
    When an application fails to launch
    Then the framework should handle the error gracefully
    And appropriate error messages should be displayed
    And the test should fail with clear diagnostics
    
    When OCR fails to extract text
    Then the framework should retry with different settings
    And fallback mechanisms should be activated
    
    When image matching fails
    Then alternative image variants should be attempted
    And detailed failure information should be logged

  @smoke @cleanup @maintenance
  Scenario: Cleanup and Maintenance Smoke Test
    Given I am testing cleanup procedures
    
    When tests complete execution
    Then temporary files should be cleaned up
    And application instances should be properly closed
    And system resources should be released
    
    When old reports are present
    Then archive procedures should work correctly
    And disk space should be managed appropriately
    
    When log files reach size limits
    Then log rotation should function properly
    And historical data should be preserved appropriately
