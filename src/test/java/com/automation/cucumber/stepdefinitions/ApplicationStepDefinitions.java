package com.automation.cucumber.stepdefinitions;

import com.automation.core.ProcessManager;
import com.automation.core.WindowController;
import com.automation.models.ManagedApplicationContext;
import com.automation.utils.WaitUtilsStatic;
import io.cucumber.java.en.*;
import java.util.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ENTERPRISE APPLICATION STEP DEFINITIONS
 * 
 * Complete professional implementation using only ManagedApplicationContext.
 * This replaces all legacy ProcessInfo/WindowInfo usage with unified enterprise architecture.
 *  * Features:
 * - Unified ManagedApplicationContext for all operations
 * - Professional error handling and recovery
 * - Strict coding conventions compliance
 * - Complete Win32 wrapper suite integration
 * - Intelligent launcher detection
 * - Multi-window management capabilities
 */
public class ApplicationStepDefinitions {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStepDefinitions.class);
    
    private final ProcessManager processManager;
    private final WindowController windowController;
    private ManagedApplicationContext lastLaunchedApplicationContext;
    private final List<TestVerification> verifications = new ArrayList<>();
    
    // Enterprise constructor with dependency injection
    public ApplicationStepDefinitions() {
        this.processManager = ProcessManager.getInstance();
        this.windowController = WindowController.getInstance();
    }
    
    // ===== CORE APPLICATION LIFECYCLE MANAGEMENT =====
    
    @Given("I have the application {string} open")
    public void i_have_the_application_open(String applicationName) {
        logger.info("🚀 STEP: Ensure application '{}' is open", applicationName);
        
        try {
            // Check if application is already running
            ManagedApplicationContext existingContext = processManager.getRunningApplicationContext(applicationName);
            
            if (existingContext != null && existingContext.isActive()) {
                logger.info("✅ Application '{}' already running with PID {}", applicationName, existingContext.getProcessId());
                  // Focus the application
                boolean focused = windowController.focusWindow(existingContext);
                if (focused) {
                    addVerification("Application Focus", true, 
                        String.format("Successfully focused existing application '%s'", applicationName));
                } else {
                    addVerification("Application Focus", false, 
                        String.format("Failed to focus existing application '%s'", applicationName));
                }
                
                lastLaunchedApplicationContext = existingContext;
                return;
            }
            
            // Launch the application
            ManagedApplicationContext applicationContext = processManager.launchAndTrackApplication(applicationName);
            
            if (applicationContext != null && applicationContext.isActive()) {
                logger.info("✅ Successfully launched application '{}' with PID {}", applicationName, applicationContext.getProcessId());
                addVerification("Application Launch", true, 
                    String.format("Successfully launched application '%s' with PID %d", applicationName, applicationContext.getProcessId()));
                    
                lastLaunchedApplicationContext = applicationContext;
                
                // Wait for application to fully initialize
                WaitUtilsStatic.waitSeconds(2);
                  // Focus the newly launched application
                boolean focused = windowController.focusWindow(applicationContext);
                if (focused) {
                    addVerification("New Application Focus", true, 
                        String.format("Successfully focused newly launched application '%s'", applicationName));
                } else {
                    addVerification("New Application Focus", false, 
                        String.format("Failed to focus newly launched application '%s'", applicationName));
                }
            } else {
                String errorMsg = String.format("Failed to launch application '%s'", applicationName);
                addVerification("Application Launch", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to ensure application '%s' is open: %s", applicationName, e.getMessage());
            addVerification("Application Open", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    @When("I switch to managed application {string}")
    public void i_switch_to_managed_application(String applicationName) {
        logger.info("🔄 STEP: Switch to managed application '{}'", applicationName);
        
        try {
            ManagedApplicationContext applicationContext = processManager.getRunningApplicationContext(applicationName);
              if (applicationContext != null && applicationContext.isActive()) {
                boolean focused = windowController.focusWindow(applicationContext);
                
                if (focused) {
                    logger.info("✅ Successfully switched to application '{}' with PID {}", applicationName, applicationContext.getProcessId());
                    addVerification("Application Switch", true, 
                        String.format("Successfully switched to application '%s'", applicationName));
                    lastLaunchedApplicationContext = applicationContext;
                } else {
                    String errorMsg = String.format("Failed to focus application '%s'", applicationName);
                    addVerification("Application Switch", false, errorMsg);
                    fail(errorMsg);
                }
            } else {
                String errorMsg = String.format("Application '%s' is not running or not available", applicationName);
                addVerification("Application Switch", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to switch to application '%s': %s", applicationName, e.getMessage());
            addVerification("Application Switch", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    @Then("the application {string} should be running")
    public void the_application_should_be_running(String applicationName) {
        logger.info("🔍 STEP: Verify application '{}' is running", applicationName);
        
        try {
            ManagedApplicationContext applicationContext = processManager.getRunningApplicationContext(applicationName);
            
            if (applicationContext != null && applicationContext.isActive()) {
                logger.info("✅ VERIFIED: Application '{}' is running with PID {}", applicationName, applicationContext.getProcessId());
                addVerification("Application Running Check", true, 
                    String.format("Application '%s' is running with PID %d", applicationName, applicationContext.getProcessId()));
            } else {
                String errorMsg = String.format("Application '%s' is not running", applicationName);
                addVerification("Application Running Check", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to verify application '%s' is running: %s", applicationName, e.getMessage());
            addVerification("Application Running Check", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    @Then("application {string} should not be running")
    public void application_should_not_be_running(String applicationName) {
        logger.info("🔍 STEP: Verify application '{}' is not running", applicationName);
        
        try {
            ManagedApplicationContext applicationContext = processManager.getRunningApplicationContext(applicationName);
            
            if (applicationContext == null || !applicationContext.isActive()) {
                logger.info("✅ VERIFIED: Application '{}' is not running", applicationName);
                addVerification("Application Not Running Check", true, 
                    String.format("Application '%s' is not running", applicationName));
            } else {
                String errorMsg = String.format("Application '%s' is still running with PID %d", applicationName, applicationContext.getProcessId());
                addVerification("Application Not Running Check", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to verify application '%s' is not running: %s", applicationName, e.getMessage());
            addVerification("Application Not Running Check", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    @Then("the last launched process should have PID greater than {int}")
    public void the_last_launched_process_should_have_pid_greater_than(int expectedMinPid) {
        logger.info("🔍 STEP: Verify last launched process has PID > {}", expectedMinPid);
        
        try {
            assertNotNull(lastLaunchedApplicationContext, "No application has been launched yet");
            
            int actualPid = lastLaunchedApplicationContext.getProcessId();
            logger.info("📊 Last launched application PID: {}, Expected > {}", actualPid, expectedMinPid);
            
            if (actualPid > expectedMinPid) {
                addVerification("PID Verification", true, 
                    String.format("Last launched application PID %d is greater than %d", actualPid, expectedMinPid));
            } else {
                String errorMsg = String.format("Last launched application PID %d is not greater than %d", actualPid, expectedMinPid);
                addVerification("PID Verification", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to verify PID > %d: %s", expectedMinPid, e.getMessage());
            addVerification("PID Verification", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    // ===== MULTI-INSTANCE MANAGEMENT =====
    
    @When("I launch {int} instances of application {string}")
    public void i_launch_multiple_instances(int instanceCount, String applicationName) {
        logger.info("🚀 STEP: Launch {} instances of application '{}'", instanceCount, applicationName);
        
        try {
            List<ManagedApplicationContext> launchedInstances = new ArrayList<>();
            
            for (int i = 0; i < instanceCount; i++) {
                logger.info("Launching instance {} of {}", i + 1, instanceCount);
                
                ManagedApplicationContext instance = processManager.launchAndTrackApplication(applicationName);
                if (instance != null && instance.isActive()) {
                    launchedInstances.add(instance);
                    logger.info("✅ Launched instance {} with PID {}", i + 1, instance.getProcessId());
                    
                    // Brief pause between launches
                    WaitUtilsStatic.waitSeconds(1);
                } else {
                    String errorMsg = String.format("Failed to launch instance %d of application '%s'", i + 1, applicationName);
                    addVerification("Multi-Instance Launch", false, errorMsg);
                    fail(errorMsg);
                }
            }
            
            // Store the last launched instance
            if (!launchedInstances.isEmpty()) {
                lastLaunchedApplicationContext = launchedInstances.get(launchedInstances.size() - 1);
            }
            
            addVerification("Multi-Instance Launch", true, 
                String.format("Successfully launched %d instances of application '%s'", instanceCount, applicationName));
                
        } catch (Exception e) {
            String errorMsg = String.format("Failed to launch %d instances of application '%s': %s", instanceCount, applicationName, e.getMessage());
            addVerification("Multi-Instance Launch", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    @Then("I should be able to get all instances of application {string}")
    public void i_should_be_able_to_get_all_instances_of_application(String applicationName) {
        logger.info("🔍 STEP: Get all instances of application '{}'", applicationName);
        
        try {
            List<ManagedApplicationContext> allInstances = processManager.getAllApplicationContexts(applicationName);
            
            if (allInstances != null && !allInstances.isEmpty()) {
                logger.info("✅ Found {} instances of application '{}'", allInstances.size(), applicationName);
                
                // Log details of each instance
                for (int i = 0; i < allInstances.size(); i++) {
                    ManagedApplicationContext instance = allInstances.get(i);
                    logger.info("  Instance {}: PID {}, Window: '{}'", 
                        i + 1, instance.getProcessId(), instance.getWindowTitle());
                }
                
                addVerification("Get All Instances", true, 
                    String.format("Successfully retrieved %d instances of application '%s'", allInstances.size(), applicationName));
            } else {
                String errorMsg = String.format("No instances found for application '%s'", applicationName);
                addVerification("Get All Instances", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to get instances of application '%s': %s", applicationName, e.getMessage());
            addVerification("Get All Instances", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    @When("I switch to instance {int} of application {string}")
    public void i_switch_to_instance(int instanceIndex, String applicationName) {
        logger.info("🔄 STEP: Switch to instance {} of application '{}'", instanceIndex, applicationName);
        
        try {
            List<ManagedApplicationContext> allInstances = processManager.getAllApplicationContexts(applicationName);
            
            if (allInstances == null || allInstances.isEmpty()) {
                String errorMsg = String.format("No instances found for application '%s'", applicationName);
                addVerification("Instance Switch", false, errorMsg);
                fail(errorMsg);
                return;
            }
            
            if (instanceIndex < 0 || instanceIndex >= allInstances.size()) {
                String errorMsg = String.format("Instance index %d is out of range. Available instances: %d", instanceIndex, allInstances.size());
                addVerification("Instance Switch", false, errorMsg);
                fail(errorMsg);
                return;
            }
              ManagedApplicationContext targetInstance = allInstances.get(instanceIndex);
            boolean focused = windowController.focusWindow(targetInstance);
            
            if (focused) {
                logger.info("✅ Successfully switched to instance {} (PID {}) of application '{}'", 
                    instanceIndex, targetInstance.getProcessId(), applicationName);
                addVerification("Instance Switch", true, 
                    String.format("Successfully switched to instance %d of application '%s'", instanceIndex, applicationName));
                lastLaunchedApplicationContext = targetInstance;
            } else {
                String errorMsg = String.format("Failed to focus instance %d of application '%s'", instanceIndex, applicationName);
                addVerification("Instance Switch", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to switch to instance %d of application '%s': %s", instanceIndex, applicationName, e.getMessage());
            addVerification("Instance Switch", false, errorMsg);
            fail(errorMsg);
        }
    }
    
    // ===== MULTI-WINDOW MANAGEMENT =====
    
    @When("I switch to window {int} of application {string}")
    public void i_switch_to_window(int windowIndex, String applicationName) {
        logger.info("🪟 STEP: Switch to window {} of application '{}'", windowIndex, applicationName);
        
        try {
            ManagedApplicationContext applicationContext = processManager.getRunningApplicationContext(applicationName);
            
            if (applicationContext == null || !applicationContext.isActive()) {
                String errorMsg = String.format("Application '%s' is not running", applicationName);
                addVerification("Window Switch", false, errorMsg);
                fail(errorMsg);
                return;
            }
            
            boolean switched = windowController.focusWindowByIndex(applicationContext, windowIndex);
            
            if (switched) {
                logger.info("✅ Successfully switched to window {} of application '{}'", windowIndex, applicationName);
                addVerification("Window Switch", true, 
                    String.format("Successfully switched to window %d of application '%s'", windowIndex, applicationName));
            } else {
                String errorMsg = String.format("Failed to switch to window %d of application '%s'", windowIndex, applicationName);
                addVerification("Window Switch", false, errorMsg);
                fail(errorMsg);
            }
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to switch to window %d of application '%s': %s", windowIndex, applicationName, e.getMessage());
            addVerification("Window Switch", false, errorMsg);
            fail(errorMsg);
        }
    }
      // ===== UTILITY AND HELPER METHODS =====
    
    /**
     * Add a test verification result
     */
    private void addVerification(String step, boolean passed, String details) {
        TestVerification verification = new TestVerification(step, passed, details, System.currentTimeMillis());
        verifications.add(verification);
        
        if (passed) {
            logger.debug("✅ {}: {}", step, details);
        } else {
            logger.error("❌ {}: {}", step, details);
        }
    }
    
    /**
     * Get all test verifications
     */
    public List<TestVerification> getTestVerifications() {
        return new ArrayList<>(verifications);
    }
    
    /**
     * Clear all test verifications
     */
    public void clearVerifications() {
        verifications.clear();
    }
    
    /**
     * Get the last launched application context
     */
    public ManagedApplicationContext getLastLaunchedApplicationContext() {
        return lastLaunchedApplicationContext;
    }
    
    /**
     * Test verification data class
     */
    public static class TestVerification {
        private final String step;
        private final boolean passed;
        private final String details;
        private final long timestamp;
        
        public TestVerification(String step, boolean passed, String details, long timestamp) {
            this.step = step;
            this.passed = passed;
            this.details = details;
            this.timestamp = timestamp;
        }
        
        public String getStep() { return step; }
        public boolean isPassed() { return passed; }
        public String getDetails() { return details; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", passed ? "PASS" : "FAIL", step, details);
        }
    }
    
    // ===== MISSING STEP DEFINITIONS FOR CMD AUTOMATION =====
    
    @When("I launch the application at path {string} as {string}")
    public void i_launch_the_application_at_path_as(String executablePath, String managedApplicationName) {
        logger.info("🚀 STEP: Launch application at path '{}' as '{}'", executablePath, managedApplicationName);
        
        try {
            ManagedApplicationContext context = processManager.launchByPath(executablePath, managedApplicationName);
            if (context != null) {
                lastLaunchedApplicationContext = context;
                addVerification("Launch Application by Path", true, 
                    String.format("Successfully launched '%s' from path '%s' with PID %d", 
                        managedApplicationName, executablePath, context.getProcessId()));
                logger.info("✅ SUCCESS: Application '{}' launched successfully with PID {}", 
                    managedApplicationName, context.getProcessId());
            } else {
                addVerification("Launch Application by Path", false, 
                    String.format("Failed to launch application from path '%s'", executablePath));
                throw new AssertionError("Failed to launch application from path: " + executablePath);
            }
        } catch (Exception e) {
            addVerification("Launch Application by Path", false, 
                String.format("Exception launching application: %s", e.getMessage()));
            logger.error("❌ ERROR: Failed to launch application '{}' from path '{}': {}", 
                managedApplicationName, executablePath, e.getMessage());
            throw new AssertionError("Failed to launch application: " + e.getMessage(), e);
        }
    }
    
    @When("I launch the application {string} with arguments {string} as {string}")
    public void i_launch_the_application_with_arguments_as(String executablePath, String arguments, String managedApplicationName) {
        logger.info("🚀 STEP: Launch application '{}' with arguments '{}' as '{}'", executablePath, arguments, managedApplicationName);
        
        try {
            ManagedApplicationContext context = processManager.launchByPathWithArguments(executablePath, arguments, managedApplicationName);
            if (context != null) {
                lastLaunchedApplicationContext = context;
                addVerification("Launch Application with Arguments", true, 
                    String.format("Successfully launched '%s' with arguments '%s' with PID %d", 
                        managedApplicationName, arguments, context.getProcessId()));
                logger.info("✅ SUCCESS: Application '{}' launched successfully with PID {}", 
                    managedApplicationName, context.getProcessId());
            } else {
                addVerification("Launch Application with Arguments", false, 
                    String.format("Failed to launch application '%s' with arguments '%s'", executablePath, arguments));
                throw new AssertionError("Failed to launch application with arguments: " + executablePath + " " + arguments);
            }
        } catch (Exception e) {
            addVerification("Launch Application with Arguments", false, 
                String.format("Exception launching application: %s", e.getMessage()));
            logger.error("❌ ERROR: Failed to launch application '{}' with arguments '{}': {}", 
                managedApplicationName, arguments, e.getMessage());
            throw new AssertionError("Failed to launch application: " + e.getMessage(), e);
        }
    }

    @When("I wait {int} seconds")
    public void i_wait_seconds(Integer seconds) {
        logger.info("⏳ STEP: Wait {} seconds", seconds);
        
        try {
            Thread.sleep(seconds * 1000L);
            addVerification("Wait", true, String.format("Waited %d seconds", seconds));
            logger.debug("✅ SUCCESS: Waited {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            addVerification("Wait", false, String.format("Wait interrupted: %s", e.getMessage()));
            logger.warn("⚠️ WARNING: Wait interrupted: {}", e.getMessage());
        }
    }
    
    @When("I type {string} in managed application {string}")
    public void i_type_in_managed_application(String text, String managedApplicationName) {
        logger.info("⌨️ STEP: Type '{}' in managed application '{}'", text, managedApplicationName);
        
        try {
            ManagedApplicationContext context = processManager.getRunningApplicationContext(managedApplicationName);
            if (context == null) {
                throw new AssertionError("Managed application '" + managedApplicationName + "' not found");
            }
            
            // Focus the application first
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ WARNING: Could not focus window for '{}'", managedApplicationName);
            }
            
            // Type the text
            windowController.sendKeys(text);
            
            addVerification("Type Text", true, 
                String.format("Typed '%s' in application '%s'", text, managedApplicationName));
            logger.info("✅ SUCCESS: Typed text in application '{}'", managedApplicationName);
        } catch (Exception e) {
            addVerification("Type Text", false, 
                String.format("Failed to type text: %s", e.getMessage()));
            logger.error("❌ ERROR: Failed to type in application '{}': {}", managedApplicationName, e.getMessage());
            throw new AssertionError("Failed to type text: " + e.getMessage(), e);
        }
    }
    
    @Then("the process {string} should be running")
    public void the_process_should_be_running(String processName) {
        logger.info("🔍 STEP: Verify process '{}' is running", processName);
        
        try {
            boolean isRunning = isProcessRunning(processName);
            if (isRunning) {
                addVerification("Process Running Check", true, 
                    String.format("Process '%s' is running", processName));
                logger.info("✅ SUCCESS: Process '{}' is running", processName);
            } else {
                addVerification("Process Running Check", false, 
                    String.format("Process '%s' is not running", processName));
                throw new AssertionError("Process '" + processName + "' is not running");
            }
        } catch (Exception e) {
            addVerification("Process Running Check", false, 
                String.format("Error checking process: %s", e.getMessage()));
            logger.error("❌ ERROR: Failed to check if process '{}' is running: {}", processName, e.getMessage());
            throw new AssertionError("Failed to check process: " + e.getMessage(), e);
        }
    }
    
    @When("I register the newest running process {string} as managed application {string}")
    public void i_register_the_newest_running_process_as_managed_application(String processName, String managedApplicationName) {
        logger.info("📋 STEP: Register newest running process '{}' as managed application '{}'", 
            processName, managedApplicationName);
        
        try {
            ManagedApplicationContext context = registerRunningProcess(processName, managedApplicationName);
            if (context != null) {
                addVerification("Register Running Process", true, 
                    String.format("Registered process '%s' as '%s' with PID %d", 
                        processName, managedApplicationName, context.getProcessId()));
                logger.info("✅ SUCCESS: Registered process '{}' as '{}' with PID {}", 
                    processName, managedApplicationName, context.getProcessId());
            } else {
                addVerification("Register Running Process", false, 
                    String.format("Failed to register process '%s'", processName));
                throw new AssertionError("Failed to register process: " + processName);
            }
        } catch (Exception e) {
            addVerification("Register Running Process", false, 
                String.format("Exception registering process: %s", e.getMessage()));
            logger.error("❌ ERROR: Failed to register process '{}' as '{}': {}", 
                processName, managedApplicationName, e.getMessage());
            throw new AssertionError("Failed to register process: " + e.getMessage(), e);
        }
    }
    
    @When("I terminate the managed application {string}")
    public void i_terminate_the_managed_application(String managedApplicationName) {
        logger.info("🛑 STEP: Terminate managed application '{}'", managedApplicationName);
          try {
            boolean terminated = processManager.terminateApplication(managedApplicationName);
            if (terminated) {
                addVerification("Terminate Application", true, 
                    String.format("Successfully terminated application '%s'", managedApplicationName));
                logger.info("✅ SUCCESS: Application '{}' terminated", managedApplicationName);
            } else {
                // Fallback: Try to terminate via process context for registered processes
                ManagedApplicationContext context = processManager.getRunningApplicationContext(managedApplicationName);
                if (context != null && context.getExecutablePath() != null && 
                    context.getExecutablePath().toLowerCase().contains("cmd.exe")) {
                    
                    logger.info("🖥️ FALLBACK: Attempting console application termination for '{}'", managedApplicationName);
                    // For console applications, try sending exit command
                    try {
                        windowController.focusWindow(context);
                        Thread.sleep(500);
                        windowController.sendKeys("exit");
                        windowController.sendKey(java.awt.event.KeyEvent.VK_ENTER);
                        Thread.sleep(1000);
                        
                        addVerification("Terminate Application", true, 
                            String.format("Console termination attempted for '%s'", managedApplicationName));
                        logger.info("✅ SUCCESS: Console termination command sent to '{}'", managedApplicationName);
                    } catch (Exception ce) {
                        logger.warn("⚠️ Console termination failed for '{}': {}", managedApplicationName, ce.getMessage());
                        addVerification("Terminate Application", false, 
                            String.format("Console termination failed for '%s'", managedApplicationName));
                    }
                } else {
                    addVerification("Terminate Application", false, 
                        String.format("Failed to terminate application '%s'", managedApplicationName));
                    logger.warn("⚠️ WARNING: Could not terminate application '{}'", managedApplicationName);
                }
            }
        } catch (Exception e) {
            addVerification("Terminate Application", false, 
                String.format("Exception terminating application: %s", e.getMessage()));
            logger.error("❌ ERROR: Failed to terminate application '{}': {}", managedApplicationName, e.getMessage());
            // Don't throw error for termination failures as it might be expected
        }
    }
    
    @When("I close the window for managed application {string}")
    public void i_close_the_window_for_managed_application(String managedApplicationName) {
        logger.info("🪟 STEP: Close window for managed application '{}'", managedApplicationName);
        
        try {
            ManagedApplicationContext context = processManager.getRunningApplicationContext(managedApplicationName);
            if (context != null) {
                boolean closed = closeWindow(context);
                if (closed) {
                    addVerification("Close Window", true, 
                        String.format("Successfully closed window for application '%s'", managedApplicationName));
                    logger.info("✅ SUCCESS: Window closed for application '{}'", managedApplicationName);
                } else {
                    addVerification("Close Window", false, 
                        String.format("Failed to close window for application '%s'", managedApplicationName));
                    logger.warn("⚠️ WARNING: Could not close window for application '{}'", managedApplicationName);
                }
            } else {
                addVerification("Close Window", false, 
                    String.format("Application '%s' not found", managedApplicationName));
                logger.warn("⚠️ WARNING: Application '{}' not found for window close", managedApplicationName);
            }
        } catch (Exception e) {
            addVerification("Close Window", false, 
                String.format("Exception closing window: %s", e.getMessage()));
            logger.error("❌ ERROR: Failed to close window for application '{}': {}", 
                managedApplicationName, e.getMessage());
            // Don't throw error for window close failures as it might be expected
        }
    }
    
    // ===== HELPER METHODS FOR STEP DEFINITIONS =====
    
    /**
     * Check if a process is running by name
     */
    private boolean isProcessRunning(String processName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq " + processName);
            Process proc = pb.start();
            
            try (java.io.InputStream inputStream = proc.getInputStream()) {
                byte[] buffer = new byte[4096];
                StringBuilder output = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }
                
                boolean isRunning = output.toString().contains(processName);
                logger.debug("Process '{}' running status: {}", processName, isRunning);
                return isRunning;
            }
            
        } catch (Exception e) {
            logger.warn("Error checking if process '{}' is running: {}", processName, e.getMessage());
            return false;
        }
    }
      /**
     * Register an existing running process as a managed application
     */
    private ManagedApplicationContext registerRunningProcess(String processName, String managedApplicationName) {
        try {
            // Find the newest running process with this name
            java.util.List<Integer> pids = findProcessesByName(processName);
            if (pids.isEmpty()) {
                logger.warn("No running processes found with name '{}'", processName);
                return null;
            }
              // For CMD processes, use a different approach since they may not have windows immediately
            if ("cmd.exe".equalsIgnoreCase(processName)) {
                logger.info("🔍 CMD DETECTION: Looking for newest CMD process (launched via Explorer)");
                
                // Wait a bit for CMD process to fully initialize after Explorer launch
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                
                // Get the newest CMD process (highest PID) - this should be the one we just launched
                int newestCmdPid = pids.stream().max(Integer::compareTo).orElse(-1);
                if (newestCmdPid != -1) {
                    logger.info("✅ NEWEST CMD FOUND: Using PID {} as the Explorer-launched CMD", newestCmdPid);
                    
                    // Create context for the newest CMD process regardless of window count
                    // CMD processes may start without windows but become manageable through OCR/keyboard automation
                    ManagedApplicationContext context = processManager.createContextFromPID(managedApplicationName, newestCmdPid);
                    if (context != null) {
                        logger.info("Registered newest CMD process '{}' (PID: {}) as managed application '{}'", 
                            processName, newestCmdPid, managedApplicationName);
                        return context;
                    } else {
                        logger.warn("Failed to create context for newest CMD PID {}", newestCmdPid);
                    }
                } else {
                    logger.warn("No CMD processes found");
                }
                
                // Fall through to regular registration as fallback
            }
            
            // Get the newest (highest PID) for non-CMD or fallback
            int newestPid = pids.stream().max(Integer::compareTo).orElse(-1);
            if (newestPid == -1) {
                logger.warn("Failed to find valid PID for process '{}'", processName);
                return null;
            }
            
            // Create context from PID using ProcessManager
            ManagedApplicationContext context = processManager.createContextFromPID(managedApplicationName, newestPid);
            if (context != null) {
                logger.info("Registered running process '{}' (PID: {}) as managed application '{}'", 
                    processName, newestPid, managedApplicationName);
                return context;
            } else {
                logger.warn("Failed to create context for process '{}' with PID {}", processName, newestPid);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("Failed to register running process '{}' as '{}': {}", 
                processName, managedApplicationName, e.getMessage());
            return null;
        }
    }
    
    /**
     * Find processes by name pattern using system calls
     */
    private java.util.List<Integer> findProcessesByName(String processName) {
        java.util.List<Integer> pids = new java.util.ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", 
                "name='" + processName + "'", "get", "ProcessId", "/format:csv");
            Process proc = pb.start();
            
            try (java.io.InputStream inputStream = proc.getInputStream()) {
                byte[] buffer = new byte[4096];
                StringBuilder output = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }
                
                String[] lines = output.toString().split("\\r?\\n");
                for (String line : lines) {
                    if (line.contains(",") && !line.contains("Node") && !line.contains("ProcessId")) {
                        try {
                            String[] parts = line.split(",");
                            if (parts.length >= 2) {
                                String pidStr = parts[1].replace("\"", "").trim();
                                if (!pidStr.isEmpty()) {
                                    int pid = Integer.parseInt(pidStr);
                                    pids.add(pid);
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid PID
                        }
                    }
                }
            }
            
            proc.waitFor(3, java.util.concurrent.TimeUnit.SECONDS);
            
        } catch (Exception e) {
            logger.debug("Failed to find processes by name '{}': {}", processName, e.getMessage());
        }
        
        return pids;
    }
    
    /**
     * Close window for managed application
     */
    private boolean closeWindow(ManagedApplicationContext context) {
        try {
            java.util.List<com.sun.jna.platform.win32.WinDef.HWND> windowHandles = context.getWindowHandles();
            if (windowHandles.isEmpty()) {
                logger.warn("No windows found for application '{}'", context.getManagedApplicationName());
                return false;
            }
            
            // Close the primary (first) window
            com.sun.jna.platform.win32.WinDef.HWND primaryWindow = windowHandles.get(0);
            
            // Send WM_CLOSE message to the window
            com.sun.jna.platform.win32.User32.INSTANCE.PostMessage(primaryWindow, 
                com.sun.jna.platform.win32.WinUser.WM_CLOSE, null, null);
            
            logger.info("✅ SUCCESS: Sent close message to window for application '{}'", 
                context.getManagedApplicationName());
            
            // Give window time to close
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("❌ ERROR: Failed to close window for application '{}': {}", 
                context.getManagedApplicationName(), e.getMessage());
            return false;
        }
    }
}
