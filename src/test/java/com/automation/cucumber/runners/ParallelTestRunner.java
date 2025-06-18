package com.automation.cucumber.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Parallel Test Runner - High-performance parallel execution
 * Executes tests in parallel for faster feedback
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",    glue = {
        "com.automation.cucumber.stepdefinitions",
        "com.automation.cucumber.patterns",
        "com.automation.cucumber.hooks",
        "com.automation.cucumber.configuration"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/parallel",
        "json:target/cucumber-reports/parallel/Cucumber.json",
        "junit:target/cucumber-reports/parallel/Cucumber.xml",
        // "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "timeline:target/cucumber-reports/parallel/timeline"
    },
    tags = "@parallel",
    monochrome = true,
    publish = true,
    dryRun = false
)
public class ParallelTestRunner {
    /**
     * Entry point for parallel test execution
     * Optimized for high-performance testing environments
     * Requires thread-safe step definitions and utilities
     */
}
