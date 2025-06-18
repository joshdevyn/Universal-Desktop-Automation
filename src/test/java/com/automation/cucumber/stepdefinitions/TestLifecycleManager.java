package com.automation.cucumber.stepdefinitions;

import com.automation.core.ProcessManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Lifecycle Manager - Ensures clean test environment
 * Handles: Process cleanup, resource management, test isolation
 */
public class TestLifecycleManager {
    private static final Logger logger = LoggerFactory.getLogger(TestLifecycleManager.class);
    
    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("🧪 TEST START: {}", scenario.getName());
        
        // Ensure clean slate for each test
        ProcessManager.getInstance().terminateAll();
        
        // Wait for cleanup to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("✅ TEST READY: Clean environment prepared");
    }
      @After
    public void afterScenario(Scenario scenario) {
        logger.info("🧪 TEST END: {} - Status: {}", scenario.getName(), 
            scenario.isFailed() ? "FAILED" : "PASSED");
        
        if (scenario.isFailed()) {
            logger.error("❌ TEST FAILED: {}", scenario.getName());
            // Use force cleanup for failed tests to ensure no lingering processes
            ProcessManager.getInstance().forceCleanupAllProcesses();
        } else {
            // Use normal cleanup for successful tests
            ProcessManager.getInstance().terminateAll();
        }
        
        logger.info("✅ CLEANUP COMPLETE: Test environment cleaned");
    }
}
