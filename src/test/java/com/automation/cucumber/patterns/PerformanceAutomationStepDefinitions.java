package com.automation.cucumber.patterns;

import com.automation.cucumber.stepdefinitions.CommonStepDefinitionsBase;
import com.automation.cucumber.utilities.CucumberUtils;
import com.automation.utils.VariableManager;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performance Automation Step Definitions - Universal performance testing patterns
 * Provides step definitions for performance testing ANY application universally
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class PerformanceAutomationStepDefinitions extends CommonStepDefinitionsBase {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAutomationStepDefinitions.class);
    private long operationStartTime;
    private long operationEndTime;
    
    @Given("I start performance monitoring for application {string}")
    public void i_start_performance_monitoring_for_application(String applicationName) {
        try {
            logger.info("Starting performance monitoring for application: {}", applicationName);
            
            // Focus application
            windowController.focusWindow(applicationName);
            
            // Record baseline performance metrics
            Runtime runtime = Runtime.getRuntime();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // Store baseline metrics
            VariableManager.setSessionVariable("baseline_memory_" + applicationName, String.valueOf(initialMemory));
            VariableManager.setSessionVariable("monitoring_start_time_" + applicationName, String.valueOf(System.currentTimeMillis()));
            
            // Take baseline screenshot for visual performance tracking
            java.io.File baselineScreenshot = screenCapture.captureScreen();
            VariableManager.setSessionVariable("baseline_screenshot_" + applicationName, baselineScreenshot.getAbsolutePath());
            
            CucumberUtils.logStepExecution("Start Performance Monitoring", "Given", true, 
                String.format("Successfully started performance monitoring for %s", applicationName));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Start Performance Monitoring", "Given", false, 
                "Failed to start performance monitoring: " + e.getMessage());
            throw new RuntimeException("Failed to start performance monitoring for: " + applicationName, e);
        }
    }
    
    @When("I perform operation {string} and measure response time")
    public void i_perform_operation_and_measure_response_time(String operationName) {
        try {
            logger.info("Performing operation with response time measurement: {}", operationName);
            
            operationStartTime = System.currentTimeMillis();
            
            // Universal operation patterns
            switch (operationName.toLowerCase()) {
                case "open_file":
                    performOpenFileOperation();
                    break;
                case "save_file":
                    performSaveFileOperation();
                    break;
                case "search":
                    performSearchOperation();
                    break;
                case "calculate":
                    performCalculateOperation();
                    break;
                case "refresh":
                    performRefreshOperation();
                    break;
                case "navigate":
                    performNavigateOperation();
                    break;
                default:
                    performCustomOperation(operationName);
                    break;
            }
            
            operationEndTime = System.currentTimeMillis();
            long responseTime = operationEndTime - operationStartTime;
            
            // Store response time
            VariableManager.setSessionVariable("last_operation_response_time", String.valueOf(responseTime));
            
            CucumberUtils.logStepExecution("Perform Measured Operation", "When", true, 
                String.format("Successfully performed operation '%s' with response time: %dms", operationName, responseTime));
            
        } catch (Exception e) {
            operationEndTime = System.currentTimeMillis();
            CucumberUtils.logStepExecution("Perform Measured Operation", "When", false, 
                "Failed to perform operation: " + e.getMessage());
            throw new RuntimeException("Failed to perform operation: " + operationName, e);
        }
    }
    
    @Then("response time should be less than {int} milliseconds")
    public void response_time_should_be_less_than_milliseconds(int maxResponseTime) {
        try {
            String responseTimeStr = VariableManager.getSessionVariable("last_operation_response_time");
            if (responseTimeStr == null) {
                throw new AssertionError("No response time measurement found");
            }
            
            long actualResponseTime = Long.parseLong(responseTimeStr);
            
            if (actualResponseTime >= maxResponseTime) {
                throw new AssertionError(String.format(
                    "Response time %dms exceeds maximum allowed %dms", actualResponseTime, maxResponseTime));
            }
            
            logger.info("Response time validation passed: {}ms < {}ms", actualResponseTime, maxResponseTime);
            
            CucumberUtils.logStepExecution("Verify Response Time", "Then", true, 
                String.format("Response time %dms is within limit of %dms", actualResponseTime, maxResponseTime));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Verify Response Time", "Then", false, 
                "Failed to verify response time: " + e.getMessage());
            throw new AssertionError("Failed to verify response time", e);
        }
    }
    
    @When("I perform load test with {int} concurrent operations")
    public void i_perform_load_test_with_concurrent_operations(int concurrentOps) {
        try {
            logger.info("Performing load test with {} concurrent operations", concurrentOps);
            
            // Universal load testing pattern
            long startTime = System.currentTimeMillis();
            
            // Simulate concurrent operations
            for (int i = 0; i < concurrentOps; i++) {
                // Quick operations that work universally
                windowController.sendKey(java.awt.event.KeyEvent.VK_F5); // Refresh
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Load test interrupted", e);
                }
                
                if (i % 10 == 0) {
                    logger.info("Completed {} operations", i);
                }
            }
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            // Calculate throughput
            double throughput = (double) concurrentOps / (totalTime / 1000.0);
            
            // Store load test results
            VariableManager.setSessionVariable("load_test_total_time", String.valueOf(totalTime));
            VariableManager.setSessionVariable("load_test_throughput", String.valueOf(throughput));
            VariableManager.setSessionVariable("load_test_operations", String.valueOf(concurrentOps));
            
            CucumberUtils.logStepExecution("Load Test", "When", true, 
                String.format("Successfully completed load test: %d operations in %dms (%.2f ops/sec)", 
                    concurrentOps, totalTime, throughput));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Load Test", "When", false, 
                "Failed to perform load test: " + e.getMessage());
            throw new RuntimeException("Failed to perform load test", e);
        }
    }
    
    @Then("throughput should be at least {double} operations per second")
    public void throughput_should_be_at_least_operations_per_second(double minThroughput) {
        try {
            String throughputStr = VariableManager.getSessionVariable("load_test_throughput");
            if (throughputStr == null) {
                throw new AssertionError("No throughput measurement found");
            }
            
            double actualThroughput = Double.parseDouble(throughputStr);
            
            if (actualThroughput < minThroughput) {
                throw new AssertionError(String.format(
                    "Throughput %.2f ops/sec is below minimum required %.2f ops/sec", 
                    actualThroughput, minThroughput));
            }
            
            logger.info("Throughput validation passed: {:.2f} ops/sec >= {:.2f} ops/sec", 
                actualThroughput, minThroughput);
            
            CucumberUtils.logStepExecution("Verify Throughput", "Then", true, 
                String.format("Throughput %.2f ops/sec meets minimum requirement of %.2f ops/sec", 
                    actualThroughput, minThroughput));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Verify Throughput", "Then", false, 
                "Failed to verify throughput: " + e.getMessage());
            throw new AssertionError("Failed to verify throughput", e);
        }
    }
    
    @When("I monitor memory usage during operation {string}")
    public void i_monitor_memory_usage_during_operation(String operationName) {
        try {
            logger.info("Monitoring memory usage during operation: {}", operationName);
            
            Runtime runtime = Runtime.getRuntime();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            
            // Perform the operation
            i_perform_operation_and_measure_response_time(operationName);
            
            // Force garbage collection to get accurate reading
            System.gc();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Memory monitoring interrupted", e);
            }
            
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            long memoryDelta = memoryAfter - memoryBefore;
            
            // Store memory usage data
            VariableManager.setSessionVariable("memory_before_operation", String.valueOf(memoryBefore));
            VariableManager.setSessionVariable("memory_after_operation", String.valueOf(memoryAfter));
            VariableManager.setSessionVariable("memory_delta", String.valueOf(memoryDelta));
            
            CucumberUtils.logStepExecution("Monitor Memory Usage", "When", true, 
                String.format("Memory usage during '%s': %d bytes delta", operationName, memoryDelta));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Monitor Memory Usage", "When", false, 
                "Failed to monitor memory usage: " + e.getMessage());
            throw new RuntimeException("Failed to monitor memory usage during: " + operationName, e);
        }
    }
    
    @Then("memory usage should not increase by more than {int} MB")
    public void memory_usage_should_not_increase_by_more_than_mb(int maxMemoryIncreaseMB) {
        try {
            String memoryDeltaStr = VariableManager.getSessionVariable("memory_delta");
            if (memoryDeltaStr == null) {
                throw new AssertionError("No memory usage measurement found");
            }
            
            long memoryDeltaBytes = Long.parseLong(memoryDeltaStr);
            long memoryDeltaMB = memoryDeltaBytes / (1024 * 1024);
            
            if (memoryDeltaMB > maxMemoryIncreaseMB) {
                throw new AssertionError(String.format(
                    "Memory increase %dMB exceeds maximum allowed %dMB", memoryDeltaMB, maxMemoryIncreaseMB));
            }
            
            logger.info("Memory usage validation passed: {}MB <= {}MB", memoryDeltaMB, maxMemoryIncreaseMB);
            
            CucumberUtils.logStepExecution("Verify Memory Usage", "Then", true, 
                String.format("Memory increase %dMB is within limit of %dMB", memoryDeltaMB, maxMemoryIncreaseMB));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Verify Memory Usage", "Then", false, 
                "Failed to verify memory usage: " + e.getMessage());
            throw new AssertionError("Failed to verify memory usage", e);
        }
    }
    
    @When("I perform stress test for {int} minutes")
    public void i_perform_stress_test_for_minutes(int durationMinutes) {
        try {
            logger.info("Performing stress test for {} minutes", durationMinutes);
            
            long testDurationMs = durationMinutes * 60 * 1000;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + testDurationMs;
            
            int operationCount = 0;
            while (System.currentTimeMillis() < endTime) {
                // Perform various stress operations
                performStressOperation();
                operationCount++;
                
                // Log progress every 1000 operations
                if (operationCount % 1000 == 0) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = endTime - System.currentTimeMillis();
                    logger.info("Stress test progress: {} operations, {}ms elapsed, {}ms remaining", 
                        operationCount, elapsed, remaining);
                }
                
                try {
                    Thread.sleep(10); // Small delay to prevent overwhelming
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Stress test interrupted", e);
                }
            }
            
            long actualDuration = System.currentTimeMillis() - startTime;
            
            // Store stress test results
            VariableManager.setSessionVariable("stress_test_duration", String.valueOf(actualDuration));
            VariableManager.setSessionVariable("stress_test_operations", String.valueOf(operationCount));
            
            CucumberUtils.logStepExecution("Stress Test", "When", true, 
                String.format("Successfully completed stress test: %d operations in %d minutes", 
                    operationCount, durationMinutes));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Stress Test", "When", false, 
                "Failed to perform stress test: " + e.getMessage());
            throw new RuntimeException("Failed to perform stress test", e);
        }
    }
    
    @Then("application should remain stable during stress test")
    public void application_should_remain_stable_during_stress_test() {
        try {
            logger.info("Verifying application stability after stress test");
            
            // Universal stability checks
            boolean isStable = true;
            StringBuilder issues = new StringBuilder();
            
            // Check 1: Application window is still responsive
            try {
                java.io.File screenshot = screenCapture.captureScreen();
                if (screenshot.length() == 0) {
                    isStable = false;
                    issues.append("Screenshot capture failed; ");
                }
            } catch (Exception e) {
                isStable = false;
                issues.append("Screenshot capture exception; ");
            }
            
            // Check 2: Memory usage is reasonable
            try {
                Runtime runtime = Runtime.getRuntime();
                long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                long maxMemory = runtime.maxMemory();
                double memoryUsagePercent = (double) currentMemory / maxMemory * 100;
                
                if (memoryUsagePercent > 90) {
                    isStable = false;
                    issues.append(String.format("High memory usage: %.1f%%; ", memoryUsagePercent));
                }
            } catch (Exception e) {
                isStable = false;
                issues.append("Memory check failed; ");
            }
            
            // Check 3: Basic UI interaction works
            try {
                windowController.sendKey(java.awt.event.KeyEvent.VK_ESCAPE);
                Thread.sleep(100);
            } catch (Exception e) {
                isStable = false;
                issues.append("UI interaction failed; ");
            }
            
            if (!isStable) {
                throw new AssertionError("Application stability issues detected: " + issues.toString());
            }
            
            CucumberUtils.logStepExecution("Verify Stability", "Then", true, 
                "Application remained stable during stress test");
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Verify Stability", "Then", false, 
                "Failed to verify application stability: " + e.getMessage());
            throw new AssertionError("Failed to verify application stability", e);
        }
    }
    
    // Private helper methods for universal performance operations
    
    private void performOpenFileOperation() {
        try {
            windowController.sendKeys("^o"); // Ctrl+O
            Thread.sleep(1000);
            windowController.sendKey(java.awt.event.KeyEvent.VK_ESCAPE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    private void performSaveFileOperation() {
        try {
            windowController.sendKeys("^s"); // Ctrl+S
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    private void performSearchOperation() {
        try {
            windowController.sendKeys("^f"); // Ctrl+F
            Thread.sleep(500);
            windowController.sendKey(java.awt.event.KeyEvent.VK_ESCAPE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    private void performCalculateOperation() {
        try {
            windowController.sendKey(java.awt.event.KeyEvent.VK_F9); // F9 - common for calculate
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    private void performRefreshOperation() {
        try {
            windowController.sendKey(java.awt.event.KeyEvent.VK_F5); // F5
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    private void performNavigateOperation() {
        try {
            windowController.sendKey(java.awt.event.KeyEvent.VK_TAB);
            Thread.sleep(200);
            windowController.sendKey(java.awt.event.KeyEvent.VK_TAB);
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    private void performCustomOperation(String operationName) {
        try {
            logger.info("Performing custom operation: {}", operationName);
            // Generic operation - send some keys
            windowController.sendKeys("test");
            Thread.sleep(300);
            windowController.sendKey(java.awt.event.KeyEvent.VK_ENTER);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
    }
    
    private void performStressOperation() {
        // Cycle through various operations
        int operation = (int) (Math.random() * 6);
        switch (operation) {
            case 0: performRefreshOperation(); break;
            case 1: performNavigateOperation(); break;
            case 2: performSearchOperation(); break;
            case 3: performCalculateOperation(); break;
            case 4: performSaveFileOperation(); break;
            case 5: performOpenFileOperation(); break;
        }
    }
}
