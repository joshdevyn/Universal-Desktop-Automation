@powerpoint @presentation @microsoft-office @real-app @pid-driven
Feature: Microsoft PowerPoint Automation - PID-Driven Excellence
  As a quality assurance engineer
  I want to automate Microsoft PowerPoint using PID-driven process management
  So that I can verify the framework works reliably with presentation software

  Background:
    Given the ProcessManager is initialized for PowerPoint operations
    And all PowerPoint instances are terminated for clean testing

  @smoke @basic-functionality @presentation-creation @pid-driven
  Scenario: Basic Presentation Creation and Slide Management with PID Management
    When I launch the application at path "C:\Program Files\Microsoft Office\root\Office16\POWERPNT.EXE" as "powerpoint_basic"
    Then the application "powerpoint_basic" should be running
    And I capture evidence with description "PowerPoint launched successfully"
    
    # Create new presentation
    When I wait for 5 seconds
    And I press "CTRL+N" key combination in managed application "powerpoint_basic"
    And I wait for 3 seconds
    And I capture evidence with description "New presentation created"
    
    # Add title to first slide
    When I click at coordinates 400,200 in managed application "powerpoint_basic"
    And I wait for 2 seconds
    And I type "Professional Automation Framework Demonstration" in managed application "powerpoint_basic"
    And I capture evidence with description "Title added to slide"
    
    # Add subtitle
    When I press "TAB" key in managed application "powerpoint_basic"
    And I wait for 1 seconds
    And I type "PID-Driven Process Management Excellence" in managed application "powerpoint_basic"
    And I capture evidence with description "Subtitle added to slide"
    
    # Add new slide
    When I press "CTRL+M" key combination in managed application "powerpoint_basic"
    And I wait for 2 seconds
    And I capture evidence with description "New slide added to presentation"
    
    # Save presentation
    When I press "CTRL+S" key combination in managed application "powerpoint_basic"
    And I wait for 3 seconds
    And I type "automation_test_presentation.pptx" in managed application "powerpoint_basic"
    And I press "ENTER" key in managed application "powerpoint_basic"
    And I wait for 3 seconds
    And I capture evidence with description "Presentation saved successfully"
    
    # Cleanup
    When I terminate the managed application "powerpoint_basic"
    Then application "powerpoint_basic" should have exactly 0 instances

  @regression @slide-operations @formatting @pid-driven
  Scenario: Advanced Slide Operations and Formatting with PID Management
    When I launch the application at path "C:\Program Files\Microsoft Office\root\Office16\POWERPNT.EXE" as "powerpoint_advanced"
    Then the application "powerpoint_advanced" should be running
    And I capture evidence with description "PowerPoint ready for advanced operations"
    
    # Create presentation with multiple slides
    When I wait for 5 seconds
    And I press "CTRL+N" key combination in managed application "powerpoint_advanced"
    And I wait for 3 seconds
    
    # First slide content
    When I click at coordinates 400,200 in managed application "powerpoint_advanced"
    And I wait for 1 seconds
    And I type "Advanced Testing Scenarios" in managed application "powerpoint_advanced"
    And I press "TAB" key in managed application "powerpoint_advanced"
    And I type "Comprehensive validation of automation capabilities" in managed application "powerpoint_advanced"
    
    # Add bullet point slide
    When I press "CTRL+M" key combination in managed application "powerpoint_advanced"
    And I wait for 2 seconds
    And I click at coordinates 400,200 in managed application "powerpoint_advanced"
    And I type "Key Testing Features" in managed application "powerpoint_advanced"
    And I press "TAB" key in managed application "powerpoint_advanced"
    
    # Add bullet points
    When I type "Process identification and management" in managed application "powerpoint_advanced"
    And I press "ENTER" key in managed application "powerpoint_advanced"
    And I type "Cross-application automation" in managed application "powerpoint_advanced"
    And I press "ENTER" key in managed application "powerpoint_advanced"
    And I type "Enterprise-grade reliability" in managed application "powerpoint_advanced"
    And I capture evidence with description "Multiple slides with content created"
    
    # Start slideshow to test presentation mode
    When I press "F5" key in managed application "powerpoint_advanced"
    And I wait for 3 seconds
    And I capture evidence with description "Slideshow mode activated"
    
    # Navigate through slides
    When I press "RIGHT" key in managed application "powerpoint_advanced"
    And I wait for 2 seconds
    And I capture evidence with description "Advanced to second slide"
    
    # Exit slideshow
    When I press "ESCAPE" key in managed application "powerpoint_advanced"
    And I wait for 2 seconds
    And I capture evidence with description "Returned to edit mode"
    
    # Cleanup
    When I terminate the managed application "powerpoint_advanced"
    Then application "powerpoint_advanced" should have exactly 0 instances
