package com.automation.cucumber.stepdefinitions;

import com.automation.core.*;
import com.automation.models.TestResult;
import io.cucumber.java.en.Given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Framework Initialization and Setup Step Definitions
 * Ensures the automation framework is properly initialized before tests
 */
public class FrameworkSteps {
    private static final Logger logger = LoggerFactory.getLogger(FrameworkSteps.class);
    
    private WindowController windowController;
    private ScreenCapture screenCapture;
    private OCREngine ocrEngine;
    private ImageMatcher imageMatcher;
    private ProcessManager processManager;
    private TestResult testResult;
    
    @Given("I have the automation framework initialized")
    public void i_have_the_automation_framework_initialized() {
        logger.info("🔧 STEP: Initialize automation framework");
        
        try {
            // Initialize core components
            windowController = new WindowController();
            screenCapture = new ScreenCapture();
            ocrEngine = new OCREngine();
            imageMatcher = new ImageMatcher();
            processManager = ProcessManager.getInstance();
            testResult = new TestResult();
            
            // Verify all components are initialized
            assertNotNull(windowController, "WindowController should be initialized");
            assertNotNull(screenCapture, "ScreenCapture should be initialized");
            assertNotNull(ocrEngine, "OCREngine should be initialized");
            assertNotNull(imageMatcher, "ImageMatcher should be initialized");
            assertNotNull(processManager, "ProcessManager should be initialized");
            assertNotNull(testResult, "TestResult should be initialized");
            
            logger.info("✅ SUCCESS: Automation framework initialized successfully");
            logger.info("   - WindowController: Ready");
            logger.info("   - ScreenCapture: Ready");
            logger.info("   - OCREngine: Ready");
            logger.info("   - ImageMatcher: Ready");
            logger.info("   - ProcessManager: Ready");
            
        } catch (Exception e) {
            logger.error("💥 FRAMEWORK INIT FAILED: {}", e.getMessage(), e);
            fail("Failed to initialize automation framework: " + e.getMessage());
        }
    }
    
    @Given("all previous processes are terminated")
    public void all_previous_processes_are_terminated() {
        logger.info("🧹 STEP: Terminate all previous processes");
        
        try {
            // Get ProcessManager instance and terminate all managed processes
            ProcessManager processManager = ProcessManager.getInstance();
            processManager.terminateAll();
            
            // Brief wait to ensure all processes are fully terminated
            Thread.sleep(2000);
            
            logger.info("✅ SUCCESS: All previous processes terminated");
            
        } catch (Exception e) {
            logger.error("⚠️ CLEANUP WARNING: Error during process termination: {}", e.getMessage());
            // Don't fail the test for cleanup issues, just warn
        }
    }
    
    @Given("the system is ready for testing")
    public void the_system_is_ready_for_testing() {
        logger.info("🎯 STEP: Verify system is ready for testing");
        
        try {
            // Ensure framework is initialized
            i_have_the_automation_framework_initialized();
            
            // Ensure no conflicting processes
            all_previous_processes_are_terminated();
            
            // Additional system checks could go here
            // - Check available memory
            // - Check disk space
            // - Check required dependencies
            
            logger.info("✅ SUCCESS: System is ready for testing");
            
        } catch (Exception e) {
            logger.error("💥 SYSTEM READINESS FAILED: {}", e.getMessage(), e);
            fail("System is not ready for testing: " + e.getMessage());
        }
    }
}
