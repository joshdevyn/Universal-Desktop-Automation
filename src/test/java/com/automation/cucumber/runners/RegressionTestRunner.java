package com.automation.cucumber.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Regression Test Runner - Comprehensive validation suite
 * Executes full regression tests for thorough system validation
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
        "src/test/resources/features/regression",
        "src/test/resources/features/integration"
    },    glue = {
        "com.automation.cucumber.stepdefinitions",
        "com.automation.cucumber.patterns",
        "com.automation.cucumber.hooks",
        "com.automation.cucumber.configuration"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/regression",
        "json:target/cucumber-reports/regression/Cucumber.json",
        "junit:target/cucumber-reports/regression/Cucumber.xml",
        // "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "timeline:target/cucumber-reports/regression/timeline",
        "rerun:target/cucumber-reports/regression/rerun.txt"
    },
    tags = "@regression",
    monochrome = true,
    publish = true,
    dryRun = false
)
public class RegressionTestRunner {
    /**
     * Entry point for regression testing execution
     * Validates complete application functionality
     * Provides comprehensive test coverage analysis
     */
}
