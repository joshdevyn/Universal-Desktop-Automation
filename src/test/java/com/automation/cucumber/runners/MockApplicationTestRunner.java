package com.automation.cucumber.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Mock Application Test Runner - Framework demonstration with mock apps
 * Executes tests using framework's built-in mock applications
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
@RunWith(Cucumber.class)
@CucumberOptions(    features = {
        "src/test/resources/features/mock-apps",
        "src/test/resources/features/real-apps/system",
        "src/test/resources/features/simplified"
    },    glue = {
        "com.automation.cucumber.stepdefinitions",
        "com.automation.cucumber.patterns",
        "com.automation.cucumber.hooks.SimplifiedTestHooks",
        "com.automation.cucumber.configuration"
    },    plugin = {
        "pretty",
        "html:target/cucumber-reports/mock-apps/html",
        "json:target/cucumber-reports/mock-apps/cucumber.json",
        "junit:target/cucumber-reports/mock-apps/Cucumber.xml",
        // "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "timeline:target/cucumber-reports/mock-apps/timeline"
    },
    tags = "@demo or @calculator or @mock-app",
    monochrome = true,
    publish = true,
    dryRun = false
)
public class MockApplicationTestRunner {
    /**
     * Entry point for mock application testing
     * Demonstrates framework capabilities using built-in applications
     * Perfect for training and proof-of-concept scenarios
     */
}
