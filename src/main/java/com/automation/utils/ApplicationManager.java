package com.automation.utils;

import com.automation.config.ConfigManager;
import com.automation.core.ProcessManager;
import com.automation.core.WindowController;
import com.automation.exceptions.AutomationException;
import com.automation.models.ManagedApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Comprehensive application lifecycle manager for launching, monitoring, and closing applications
 */
public class ApplicationManager {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationManager.class);
    private static ApplicationManager instance;
    private final Map<String, Process> launchedProcesses = new ConcurrentHashMap<>();
    private final WindowController windowController;
    
    private ApplicationManager() {
        this.windowController = new WindowController();
    }
    
    public static synchronized ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }    /**
     * Launch an application based on configuration using PID-driven ProcessManager
     * @param applicationName Name of the application from applications.yml
     * @return true if launched successfully, false otherwise
     * @throws AutomationException if launch fails
     */
    public boolean launchApplication(String applicationName) throws AutomationException {
        logger.info("🚀 PID-DRIVEN LAUNCH: Application '{}'", applicationName);
        
        Map<String, Object> appConfig = ConfigManager.getApplicationConfig(applicationName);
        if (appConfig == null || appConfig.isEmpty()) {
            throw new AutomationException("No configuration found for application: " + applicationName);
        }
        
        String executablePath = (String) appConfig.get("executable_path");
        if (executablePath == null || executablePath.trim().isEmpty()) {
            throw new AutomationException("No executable path configured for application: " + applicationName);
        }
        
        // Check if managed application is already running using ProcessManager
        if (isManagedApplicationRunning(applicationName)) {
            logger.info("✅ ALREADY RUNNING: Managed application '{}' found, focusing...", applicationName);
            return focusManagedApplication(applicationName);
        }
        
        try {
            // Launch using ProcessManager for rock-solid PID tracking
            ManagedApplicationContext processInfo = ProcessManager.getInstance().launchByPath(executablePath, applicationName);
            
            if (processInfo == null) {
                throw new AutomationException("ProcessManager failed to launch application: " + applicationName);
            }
            
            // Focus the newly launched application
            boolean focused = focusManagedApplication(applicationName);
            
            logger.info("✅ PID-DRIVEN SUCCESS: Application '{}' launched with PID: {} and {}focused", 
                applicationName, processInfo.getProcessId(), focused ? "" : "NOT ");
              return true;
            
        } catch (Exception e) {
            logger.error("💥 PID-DRIVEN LAUNCH FAILED: Application '{}': {}", applicationName, e.getMessage());
            throw new AutomationException("Failed to launch application: " + applicationName, e);
        }
    }
      /**
     * Close an application gracefully using PID-driven ProcessManager
     * @param applicationName Name of the application to close
     * @return true if closed successfully, false otherwise
     */
    public boolean closeApplication(String applicationName) {
        logger.info("🛑 PID-DRIVEN CLOSE: Application '{}'", applicationName);
        
        try {
            // Try graceful managed application close first
            if (isManagedApplicationRunning(applicationName)) {
                if (closeManagedApplicationWindow(applicationName)) {
                    logger.info("✅ GRACEFUL CLOSE: Successfully closed managed application: '{}'", applicationName);
                }
                
                // Use ProcessManager for clean termination
                boolean terminated = ProcessManager.getInstance().terminateApplication(applicationName);
                if (terminated) {
                    logger.info("✅ PID-DRIVEN TERMINATION: Application '{}' terminated successfully", applicationName);
                    return true;
                }
            }
            
            // Cleanup legacy launched processes if any remain
            if (launchedProcesses.containsKey(applicationName)) {
                terminateProcess(applicationName);
            }
            
            // Verify application is no longer running
            boolean stillRunning = isManagedApplicationRunning(applicationName);
            logger.info("{} PID-DRIVEN CLOSE: Application '{}' {}running", 
                stillRunning ? "⚠️" : "✅", applicationName, stillRunning ? "still " : "no longer ");
            return !stillRunning;
            
        } catch (Exception e) {
            logger.error("💥 PID-DRIVEN CLOSE FAILED: Application '{}': {}", applicationName, e.getMessage());
            // Force terminate if graceful close failed
            return forceTerminateProcess(applicationName);
        }
    }
    
    /**
     * Close all applications launched by this manager
     */
    public void closeAllApplications() {
        logger.info("Closing all launched applications...");
        
        for (String appName : launchedProcesses.keySet()) {
            try {
                closeApplication(appName);
            } catch (Exception e) {
                logger.warn("Failed to close application {}: {}", appName, e.getMessage());
            }
        }
        
        launchedProcesses.clear();
    }    /**
     * Check if an application is currently running
     * @param windowTitle The window title to check for
     * @return true if the application window is found
     * @deprecated LEGACY BULLSHIT! Use isManagedApplicationRunning() with ProcessManager - titles are flaky and unreliable!
     */
    @Deprecated
    public boolean isApplicationRunning(String windowTitle) {
        throw new UnsupportedOperationException(
            "DEPRECATED: Window title-based detection is unreliable! " +
            "Use ApplicationManager.isManagedApplicationRunning(managedAppName) with ProcessManager instead. " +
            "ProcessManager provides rock-solid PID-driven application detection!"
        );
    }    /**
     * Focus an application window
     * @param windowTitle The window title to focus
     * @return true if focused successfully
     * @deprecated LEGACY BULLSHIT! Use focusManagedApplication() with ProcessManager for reliable focus operations!
     */
    @Deprecated
    public boolean focusApplication(String windowTitle) {
        throw new UnsupportedOperationException(
            "DEPRECATED: Window title-based focus is unreliable! " +
            "Use ApplicationManager.focusManagedApplication(managedAppName) with ProcessManager instead. " +
            "ProcessManager provides rock-solid PID-driven window focus!"
        );
    }
    
    /**
     * Check if a managed application is currently running using PID-driven approach
     * @param managedApplicationName The managed application name registered with ProcessManager
     * @return true if the managed application is found and running
     */
    public boolean isManagedApplicationRunning(String managedApplicationName) {
        if (managedApplicationName == null || managedApplicationName.trim().isEmpty()) {
            return false;
        }          try {
            ManagedApplicationContext processInfo = ProcessManager.getInstance().getRunningApplicationContext(managedApplicationName);
            if (processInfo != null) {
                logger.debug("Managed application '{}' is running with PID: {}", managedApplicationName, processInfo.getProcessId());
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.debug("Error checking if managed application is running: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Focus a managed application using PID-driven approach
     * @param managedApplicationName The managed application name registered with ProcessManager
     * @return true if focused successfully
     */
    public boolean focusManagedApplication(String managedApplicationName) {
        if (managedApplicationName == null || managedApplicationName.trim().isEmpty()) {
            return false;
        }
          try {            ManagedApplicationContext processInfo = ProcessManager.getInstance().getRunningApplicationContext(managedApplicationName);
            if (processInfo == null) {
                logger.warn("Managed application '{}' not found in ProcessManager registry", managedApplicationName);
                return false;
            }
            
            boolean focused = windowController.focusWindow(processInfo);
            if (focused) {
                Thread.sleep(500); // Brief delay for focus to take effect
                logger.debug("Successfully focused managed application '{}' with PID: {}", managedApplicationName, processInfo.getProcessId());
                return true;
            } else {
                logger.warn("Failed to focus managed application '{}' with PID: {}", managedApplicationName, processInfo.getProcessId());
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to focus managed application '{}': {}", managedApplicationName, e.getMessage());
            return false;
        }    }
    
    /**
     * Close managed application window gracefully using PID-driven approach
     * @param managedApplicationName The managed application name registered with ProcessManager
     * @return true if closed successfully
     */
    public boolean closeManagedApplicationWindow(String managedApplicationName) {
        if (managedApplicationName == null || managedApplicationName.trim().isEmpty()) {
            return false;
        }
          try {            ManagedApplicationContext processInfo = ProcessManager.getInstance().getRunningApplicationContext(managedApplicationName);
            if (processInfo == null) {
                logger.debug("Managed application '{}' not found in ProcessManager registry", managedApplicationName);
                return true; // Consider it closed if not found
            }
            
            // Focus window first using ManagedApplicationContext
            boolean focused = windowController.focusWindow(processInfo);
            if (focused) {
                Thread.sleep(500);
                
                // Send Alt+F4 to close window
                windowController.sendKeyCombo("ALT", "F4");
                
                // Wait for managed application to close
                return waitForCondition(() -> !isManagedApplicationRunning(managedApplicationName), 5, 
                    "Managed application '" + managedApplicationName + "' to close");
            }
            
            logger.warn("Could not focus managed application '{}' for graceful closure", managedApplicationName);
            return false;
                
        } catch (Exception e) {
            logger.warn("Failed to close managed application window gracefully: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Terminate a process that we launched
     */
    private boolean terminateProcess(String applicationName) {
        Process process = launchedProcesses.get(applicationName);
        if (process != null && process.isAlive()) {
            try {
                process.destroy();
                boolean terminated = process.waitFor(5, TimeUnit.SECONDS);
                
                if (!terminated) {
                    logger.warn("Process did not terminate gracefully, forcing termination");
                    process.destroyForcibly();
                    process.waitFor(2, TimeUnit.SECONDS);
                }
                
                launchedProcesses.remove(applicationName);
                logger.info("Successfully terminated process for application: {}", applicationName);
                return true;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for process termination: {}", applicationName);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Force terminate a process
     */
    private boolean forceTerminateProcess(String applicationName) {
        Process process = launchedProcesses.get(applicationName);
        if (process != null && process.isAlive()) {
            try {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
                launchedProcesses.remove(applicationName);
                logger.info("Force terminated process for application: {}", applicationName);
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while force terminating process: {}", applicationName);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get information about launched processes
     */
    public Map<String, Boolean> getProcessStatus() {
        Map<String, Boolean> status = new ConcurrentHashMap<>();
        
        for (Map.Entry<String, Process> entry : launchedProcesses.entrySet()) {
            status.put(entry.getKey(), entry.getValue().isAlive());
        }
        
        return status;
    }
    
    /**
     * Restart an application
     */
    public boolean restartApplication(String applicationName) throws AutomationException {
        logger.info("Restarting application: {}", applicationName);
        
        closeApplication(applicationName);
        
        // Wait a moment for cleanup
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return launchApplication(applicationName);
    }
      /**
     * Generic wait condition helper
     */
    private boolean waitForCondition(Supplier<Boolean> condition, int timeoutSeconds, String description) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                if (condition.get()) {
                    logger.debug("Condition met: {}", description);
                    return true;
                }
                Thread.sleep(500);
            } catch (Exception e) {
                logger.warn("Error while waiting for condition: {}", e.getMessage());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        logger.warn("Timeout waiting for condition: {}", description);
        return false;
    }
}
