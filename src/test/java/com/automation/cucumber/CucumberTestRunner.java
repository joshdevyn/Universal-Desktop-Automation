package com.automation.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Legacy Cucumber Test Runner - Maintained for backward compatibility
 * 
 * @deprecated Use specific runners in com.automation.cucumber.runners package instead
 * @see com.automation.cucumber.runners.MasterTestRunner
 * @see com.automation.cucumber.runners.SmokeTestRunner
 * @see com.automation.cucumber.runners.RegressionTestRunner
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
@Deprecated
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features", 
    glue = {
        "com.automation.cucumber.stepdefinitions",
        "com.automation.cucumber.hooks",
        "com.automation.cucumber.configuration"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/legacy/html-report",
        "json:target/cucumber-reports/legacy/cucumber-report.json",
        "junit:target/cucumber-reports/legacy/junit-report.xml"
    },
    tags = "@smoke or @calculator or @demo or @simple-cmd"
)
public class CucumberTestRunner {
    // This class serves as the entry point for Cucumber BDD tests
    // All configuration is done via annotations above
}
