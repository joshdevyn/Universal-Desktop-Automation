package com.automation.cucumber.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Master Test Runner - Complete framework validation
 * Executes all available tests for comprehensive coverage
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
@RunWith(Cucumber.class)
@CucumberOptions(    features = "src/test/resources/features",    
            glue = {
        "com.automation.cucumber.stepdefinitions",
        "com.automation.cucumber.patterns",
        "com.automation.cucumber.hooks.SimplifiedTestHooks",
        "com.automation.cucumber.configuration"
    },      plugin = {
        "pretty",
        "html:target/cucumber-reports/master/html",
        "json:target/cucumber-reports/master/cucumber.json",
        "junit:target/cucumber-reports/master/junit.xml",
        // "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        "timeline:target/cucumber-reports/master/timeline",
        "rerun:target/cucumber-reports/master/rerun.txt",
        "usage:target/cucumber-reports/master/usage.json"
    },    tags = "not @skip and not @wip",
    monochrome = true,
    publish = true,
    dryRun = false
)
public class MasterTestRunner {
    /**
     * Entry point for complete test suite execution
     * Runs all available features and scenarios
     * Provides maximum test coverage and validation
     */
}
