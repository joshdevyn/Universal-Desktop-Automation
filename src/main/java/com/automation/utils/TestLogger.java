package com.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Universal Test Logger - Provides structured logging for test execution
 * Handles test step logging, performance metrics, and error tracking
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class TestLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(TestLogger.class);
    
    /**
     * Log a test step with success/failure status
     * 
     * @param step The test step description
     * @param success Whether the step passed or failed
     * @param details Additional details about the step execution
     */
    public static void logTestStep(String step, boolean success, String details) {
        if (success) {
            logger.info("✓ {}: {}", step, details);
        } else {
            logger.error("✗ {}: {}", step, details);
        }
    }
    
    /**
     * Log performance metrics for operations
     * 
     * @param operation The operation name
     * @param durationMs Duration in milliseconds
     */
    public static void logPerformanceMetric(String operation, long durationMs) {
        logger.info("Performance - {}: {}ms", operation, durationMs);
        
        if (durationMs > 5000) {
            logger.warn("Slow operation detected: {} took {}ms", operation, durationMs);
        }
    }
    
    /**
     * Log application lifecycle events
     * 
     * @param applicationName Name of the application
     * @param event Lifecycle event (startup, shutdown, etc.)
     * @param success Whether the event was successful
     * @param details Additional event details
     */
    public static void logApplicationEvent(String applicationName, String event, boolean success, String details) {
        String status = success ? "SUCCESS" : "FAILURE";
        logger.info("APP EVENT [{}] - {}: {} - {}", status, applicationName, event, details);
    }
    
    /**
     * Log automation action results
     * 
     * @param action The automation action performed
     * @param target The target element or area
     * @param success Whether the action was successful
     * @param details Additional action details
     */
    public static void logAutomationAction(String action, String target, boolean success, String details) {
        String status = success ? "✓" : "✗";
        logger.info("{} {} on '{}': {}", status, action, target, details);
    }
    
    /**
     * Log OCR operation results
     * 
     * @param region The screen region analyzed
     * @param extractedText The text extracted by OCR
     * @param confidence OCR confidence level
     */
    public static void logOCRResult(String region, String extractedText, double confidence) {
        logger.info("OCR Result [{}] - Text: '{}', Confidence: {:.2f}%", 
                   region, extractedText, confidence);
        
        if (confidence < 70.0) {
            logger.warn("Low OCR confidence: {:.2f}% for text: '{}'", confidence, extractedText);
        }
    }
    
    /**
     * Log image matching results
     * 
     * @param imageName The template image name
     * @param found Whether the image was found
     * @param confidence Match confidence if found
     * @param location Location coordinates if found
     */
    public static void logImageMatch(String imageName, boolean found, double confidence, String location) {
        if (found) {
            logger.info("✓ Image Match: '{}' found at {} with {:.2f}% confidence", 
                       imageName, location, confidence);
        } else {
            logger.warn("✗ Image Match: '{}' not found", imageName);
        }
    }
    
    /**
     * Log error conditions with context
     * 
     * @param operation The operation that failed
     * @param error The error that occurred
     * @param context Additional context information
     */
    public static void logError(String operation, String error, String context) {
        logger.error("ERROR in {}: {} | Context: {}", operation, error, context);
    }
    
    /**
     * Log warning conditions
     * 
     * @param operation The operation with warnings
     * @param warning The warning message
     * @param context Additional context information
     */
    public static void logWarning(String operation, String warning, String context) {
        logger.warn("WARNING in {}: {} | Context: {}", operation, warning, context);
    }
    
    /**
     * Log debug information (only in debug mode)
     * 
     * @param category Debug category
     * @param message Debug message
     * @param details Additional debug details
     */
    public static void logDebug(String category, String message, String details) {
        logger.debug("[{}] {}: {}", category, message, details);
    }
    
    /**
     * Log test data information
     * 
     * @param dataType Type of test data
     * @param source Data source (file, database, etc.)
     * @param recordCount Number of records loaded
     */
    public static void logTestData(String dataType, String source, int recordCount) {
        logger.info("Test Data Loaded - Type: {}, Source: {}, Records: {}", 
                   dataType, source, recordCount);
    }
    
    /**
     * Log scenario start
     * 
     * @param scenarioName Name of the test scenario
     * @param tags Scenario tags
     */
    public static void logScenarioStart(String scenarioName, String tags) {
        logger.info("🚀 SCENARIO START: {} [Tags: {}]", scenarioName, tags);
    }
    
    /**
     * Log scenario completion
     * 
     * @param scenarioName Name of the test scenario
     * @param success Whether the scenario passed
     * @param duration Execution duration in milliseconds
     */
    public static void logScenarioEnd(String scenarioName, boolean success, long duration) {
        String status = success ? "✅ PASSED" : "❌ FAILED";
        logger.info("{}: {} ({}ms)", status, scenarioName, duration);
    }
    
    /**
     * Log feature start
     * 
     * @param featureName Name of the feature
     */
    public static void logFeatureStart(String featureName) {
        logger.info("🏁 FEATURE START: {}", featureName);
    }
    
    /**
     * Log feature completion
     * 
     * @param featureName Name of the feature
     * @param passedScenarios Number of passed scenarios
     * @param failedScenarios Number of failed scenarios
     */
    public static void logFeatureEnd(String featureName, int passedScenarios, int failedScenarios) {
        logger.info("🏁 FEATURE END: {} (Passed: {}, Failed: {})", 
                   featureName, passedScenarios, failedScenarios);
    }
}
