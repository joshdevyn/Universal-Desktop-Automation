package com.automation.cucumber.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Integration Test Runner - Cross-system workflow validation
 * Executes integration tests between multiple applications
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
        "src/test/resources/features/integration",
        "src/test/resources/features/real-apps"
    },    glue = {
        "com.automation.cucumber.stepdefinitions",
        "com.automation.cucumber.patterns",
        "com.automation.cucumber.hooks", 
        "com.automation.cucumber.configuration"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/integration",
        "json:target/cucumber-reports/integration/Cucumber.json",
        "junit:target/cucumber-reports/integration/Cucumber.xml",
        // "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "timeline:target/cucumber-reports/integration/timeline"
    },
    tags = "@integration",
    monochrome = true,
    publish = true,
    dryRun = false
)
public class IntegrationTestRunner {
    /**
     * Entry point for integration testing execution
     * Validates end-to-end workflows across applications
     * Tests data flow and system interactions
     */
}
