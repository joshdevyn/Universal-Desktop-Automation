package com.automation.core;

import com.automation.config.ConfigManager;
import com.automation.utils.ReportUtils;
import com.automation.models.ApplicationWindow;
import com.automation.models.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.*;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * TestBase provides common functionality for all automation tests using JUnit 5
 */
public class TestBase {
    protected static final Logger logger = LoggerFactory.getLogger(TestBase.class);
      // Core automation components
    protected WindowController windowController;
    protected ScreenCapture screenCapture;
    protected OCREngine ocrEngine;
    protected ImageMatcher imageMatcher;
    protected ReportUtils reportUtils;
    
    // Test context
    protected ApplicationWindow currentApplication;
    protected Map<String, Object> testData;
    protected TestResult currentTestResult;
    
    @BeforeAll
    public static void beforeSuite() {
        logger.info("=== Starting Test Suite ===");
    }
    
    @AfterAll
    public static void afterSuite() {
        logger.info("=== Test Suite Completed ===");
    }
    
    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        logger.info("Starting test: {}", testInfo.getDisplayName());
        initializeFramework();
        setupTestMethod();
    }
    
    @AfterEach
    public void afterEach(TestInfo testInfo) {
        logger.info("Completed test: {}", testInfo.getDisplayName());
        if (currentTestResult != null) {
            handleTestResult(testInfo);
        }
        cleanupTestMethod();
    }
    
    /**
     * Initialize the automation framework
     */
    protected void initializeFramework() {
        try {            // Initialize core components
            windowController = new WindowController();
            screenCapture = new ScreenCapture();
            ocrEngine = new OCREngine();
            imageMatcher = new ImageMatcher();
            reportUtils = new ReportUtils();
            
            // Initialize test data
            testData = new HashMap<>();
            
            logger.info("Automation framework initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize automation framework", e);
            throw new RuntimeException("Framework initialization failed", e);
        }
    }
    
    /**
     * Setup for test method
     */
    protected void setupTestMethod() {
        currentTestResult = new TestResult();
        currentTestResult.setStartTime(System.currentTimeMillis());
        
        // Take initial screenshot
        String initialScreenshot = captureScreenshot("test_start");
        if (initialScreenshot != null) {
            currentTestResult.addScreenshot("initial", initialScreenshot);
        }
    }
    
    /**
     * Handle test result and reporting
     */
    protected void handleTestResult(TestInfo testInfo) {
        currentTestResult.setEndTime(System.currentTimeMillis());
        currentTestResult.setTestName(testInfo.getDisplayName());
        currentTestResult.setStatus("COMPLETED");
        
        // Capture final screenshot
        String finalScreenshot = captureScreenshot("test_end");
        if (finalScreenshot != null) {
            currentTestResult.addScreenshot("final", finalScreenshot);
        }
        
        // Generate test report
        try {
            reportUtils.generateTestReport(currentTestResult);
        } catch (Exception e) {
            logger.warn("Failed to generate test report", e);
        }
    }
    
    /**
     * Cleanup for test method
     */
    protected void cleanupTestMethod() {
        // Override in subclasses for method-specific cleanup
        try {
            if (windowController != null) {
                // Perform any cleanup operations
            }
        } catch (Exception e) {
            logger.warn("Error during test cleanup", e);
        }
    }
      /**
     * Capture screenshot with timestamp
     */
    protected String captureScreenshot(String prefix) {
        try {
            if (screenCapture == null) {
                logger.warn("ScreenCapture not initialized, skipping screenshot");
                return null;
            }
            
            long timestamp = System.currentTimeMillis();
            String filename = String.format("%s_%d.png", prefix, timestamp);
            
            // Use the actual ScreenCapture API - capture full screen and save
            BufferedImage screenshot = screenCapture.captureFullScreen();
            return screenCapture.saveScreenshot(screenshot, filename);
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot", e);
            return null;
        }
    }
      /**
     * Get configuration value using static method
     */
    protected String getConfigValue(String key) {
        try {
            return ConfigManager.getProperty(key);
        } catch (Exception e) {
            logger.warn("Failed to get config value for key: {}", key, e);
            return null;
        }
    }
    
    /**
     * Get configuration value with default
     */
    protected String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get stack trace as string
     */
    protected String getStackTrace(Throwable throwable) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
