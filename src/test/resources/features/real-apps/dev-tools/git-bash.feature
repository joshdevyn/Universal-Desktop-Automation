@git @version-control @developer-tools @real-app @pid-driven
Feature: Git Bash Command Line Tool Automation - PID-Driven Excellence
  As a quality assurance engineer
  I want to automate Git Bash using PID-driven process management
  So that I can verify the framework works reliably with development tools

  Background:
    Given the ProcessManager is initialized for Git Bash operations
    And all Git Bash instances are terminated for clean testing

  @smoke @basic-functionality @git-commands @pid-driven
  Scenario: Git Repository Operations with PID Management
    When I launch the application at path "C:\Program Files\Git\bin\bash.exe" as "git_bash_repo"
    Then the application "git_bash_repo" should be running
    And I capture evidence with description "Git Bash launched successfully"
    
    # Navigate to test directory and create repository
    When I wait for 3 seconds
    And I type "cd ~/Documents" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 2 seconds
    
    When I type "mkdir GitAutomationTest" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 1 seconds
    
    When I type "cd GitAutomationTest" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 1 seconds
    
    # Initialize git repository
    When I type "git init" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 2 seconds
    And I capture evidence with description "Git repository initialized"
    
    # Create test file and commit
    When I type "echo 'Professional automation testing with Git' > test_file.txt" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 1 seconds
    
    When I type "git add test_file.txt" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 1 seconds
    
    When I type "git commit -m \"Initial commit for automation testing\"" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 2 seconds
    And I capture evidence with description "Git commit completed successfully"
    
    # Cleanup test directory
    When I type "cd .." in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 1 seconds
    
    When I type "rm -rf GitAutomationTest" in managed application "git_bash_repo"
    And I press "ENTER" key in managed application "git_bash_repo"
    And I wait for 1 seconds
    And I capture evidence with description "Test directory cleanup completed"
    
    # Cleanup
    When I terminate the managed application "git_bash_repo"
    Then application "git_bash_repo" should have exactly 0 instances

  @regression @advanced-git @branch-operations @pid-driven
  Scenario: Advanced Git Branch Operations with PID Management
    When I launch the application at path "C:\Program Files\Git\bin\bash.exe" as "git_bash_advanced"
    Then the application "git_bash_advanced" should be running
    And I capture evidence with description "Git Bash ready for advanced operations"
    
    # Setup test repository
    When I wait for 3 seconds
    And I type "cd ~/Documents && mkdir GitAdvancedTest && cd GitAdvancedTest" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 2 seconds
    
    When I type "git init" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 1 seconds
    
    # Create initial content and commit
    When I type "echo 'Main branch content' > main.txt" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 1 seconds
    
    When I type "git add main.txt && git commit -m \"Main branch initial commit\"" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 2 seconds
    
    # Create and switch to feature branch
    When I type "git checkout -b feature-branch" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 1 seconds
    
    When I type "echo 'Feature branch content' > feature.txt" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 1 seconds
    
    When I type "git add feature.txt && git commit -m \"Feature branch commit\"" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 2 seconds
    And I capture evidence with description "Feature branch operations completed"
    
    # Switch back to main and show branch status
    When I type "git checkout main" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 1 seconds
    
    When I type "git branch -a" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 1 seconds
    And I capture evidence with description "Branch listing displayed"
    
    # Cleanup test repository
    When I type "cd .. && rm -rf GitAdvancedTest" in managed application "git_bash_advanced"
    And I press "ENTER" key in managed application "git_bash_advanced"
    And I wait for 2 seconds
    And I capture evidence with description "Advanced test cleanup completed"
    
    # Cleanup
    When I terminate the managed application "git_bash_advanced"
    Then application "git_bash_advanced" should have exactly 0 instances
