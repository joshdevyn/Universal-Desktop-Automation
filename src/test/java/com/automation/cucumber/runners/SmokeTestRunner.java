package com.automation.cucumber.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Smoke Test Runner - Quick health checks across critical functionality
 * Executes smoke tests for rapid feedback on system stability
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
        "src/test/resources/features/smoke",
        "src/test/resources/features/master-suite"    },    glue = {
        "com.automation.cucumber.stepdefinitions",
        "com.automation.cucumber.patterns",
        "com.automation.cucumber.hooks.SimplifiedTestHooks",
        "com.automation.cucumber.configuration"
    },    plugin = {
        "pretty",
        "html:target/cucumber-reports/smoke/html",
        "json:target/cucumber-reports/smoke/cucumber.json",
        "junit:target/cucumber-reports/smoke/junit.xml",
        // "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "timeline:target/cucumber-reports/smoke/timeline"
    },
    tags = "@smoke",
    monochrome = true,
    publish = true,
    dryRun = false
)
public class SmokeTestRunner {
    /**
     * Entry point for smoke testing execution
     * Validates critical application functionality quickly
     * Ideal for CI/CD pipeline integration
     */
}
