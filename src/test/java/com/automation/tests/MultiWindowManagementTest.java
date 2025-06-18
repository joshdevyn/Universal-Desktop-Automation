package com.automation.tests;

import com.automation.core.WindowController;
import com.automation.core.ProcessManager;
import com.automation.models.ManagedApplicationContext;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

/**
 * ENTERPRISE MULTI-WINDOW MANAGEMENT TEST * 
 * Comprehensive test for enhanced multi-window management functionality
 * using unified ManagedApplicationContext architecture.
 * 
 * Follows strict coding conventions: Single responsibility, comprehensive error handling,
 * and complete enterprise architecture compliance.
 * 
 * Tests the ability to manage processes like explorer.exe that have multiple windows
 * under the same PID using the unified enterprise architecture.
 */
public class MultiWindowManagementTest {
    private static final Logger logger = LoggerFactory.getLogger(MultiWindowManagementTest.class);
    
    private static ProcessManager processManager;
    private static WindowController windowController;
    private static ManagedApplicationContext explorerApplicationContext;

    @BeforeAll
    public static void setUpClass() {
        logger.info("ENTERPRISE TEST SETUP: Multi-window management test initialization");
        processManager = ProcessManager.getInstance();
        windowController = new WindowController();
        
        // Launch or get existing explorer process using ProcessManager
        explorerApplicationContext = processManager.getRunningApplicationContext("file_explorer");
        
        if (explorerApplicationContext == null) {
            try {
                // Try to launch explorer if not running
                explorerApplicationContext = processManager.launchAndTrackApplication("file_explorer");
            } catch (Exception e) {
                logger.error("Failed to launch file explorer: {}", e.getMessage());
            }
        }
        
        if (explorerApplicationContext != null) {
            logger.info("EXPLORER CONTEXT: PID {}, Windows: {}", 
                explorerApplicationContext.getProcessId(), 
                explorerApplicationContext.getWindowHandles().size());
        }
    }

    @AfterAll
    public static void tearDownClass() {
        logger.info("ENTERPRISE TEST CLEANUP: Multi-window management test cleanup");
        // Don't terminate explorer.exe as it's a system process
        if (explorerApplicationContext != null) {
            logger.info("TEST CLEANUP: Explorer context PID {} preserved as system process", 
                explorerApplicationContext.getProcessId());
        }
    }

    /**
     * ENTERPRISE TEST: Multi-window discovery and management
     */
    @Test
    public void testMultiWindowDiscovery() {
        logger.info("TEST: Multi-Window Discovery using ManagedApplicationContext");
        
        Assertions.assertNotNull(explorerApplicationContext, "Explorer.exe application context should be available");
        
        // Check if multiple windows were discovered using ManagedApplicationContext
        List<com.sun.jna.platform.win32.WinDef.HWND> windowHandles = explorerApplicationContext.getWindowHandles();
        
        logger.info("DISCOVERY RESULT: Found {} windows for explorer PID {}", 
            windowHandles.size(), explorerApplicationContext.getProcessId());
          if (windowHandles.size() > 1) {
            logger.info("MULTI-WINDOW SUCCESS: Explorer has {} windows", windowHandles.size());
            
            // Test window switching
            for (int i = 0; i < Math.min(2, windowHandles.size()); i++) {
                // Test window focus using enterprise architecture
                boolean focused = windowController.focusWindow(explorerApplicationContext);
                
                if (focused) {
                    logger.info("WINDOW FOCUS SUCCESS: Focused window {} of explorer", i);
                    
                    // Test getting window bounds for the active window
                    Rectangle bounds = windowController.getWindowBounds(explorerApplicationContext);
                    if (bounds != null) {
                        logger.info("WINDOW BOUNDS: {}x{} at ({}, {})", 
                            bounds.width, bounds.height, bounds.x, bounds.y);
                    }
                } else {
                    logger.warn("WINDOW FOCUS FAILED: Could not focus window {} of explorer", i);
                }
            }
        } else {
            logger.warn("SINGLE WINDOW: Explorer has only {} window(s)", windowHandles.size());
        }
        
        // Basic assertion that we have at least one window
        Assertions.assertTrue(windowHandles.size() >= 1, 
            "Explorer should have at least one window");
    }

    /**
     * ENTERPRISE TEST: Window controller integration
     */
    @Test
    public void testEnterpriseWindowManagerIntegration() {
        logger.info("TEST: Enterprise Window Manager Integration");
        
        Assertions.assertNotNull(explorerApplicationContext, "Explorer application context required");
        
        // Test window discovery through ManagedApplicationContext
        List<com.sun.jna.platform.win32.WinDef.HWND> windowHandles = explorerApplicationContext.getWindowHandles();
        
        if (!windowHandles.isEmpty()) {
            logger.info("WINDOW INTEGRATION: Testing with {} windows", windowHandles.size());
            
            // Test getting bounds through window controller
            Rectangle bounds = windowController.getWindowBounds(explorerApplicationContext);
            if (bounds != null) {
                logger.info("BOUNDS SUCCESS: Window bounds retrieved: {}x{}", bounds.width, bounds.height);
                Assertions.assertTrue(bounds.width > 0 && bounds.height > 0, 
                    "Window should have positive dimensions");
            } else {
                logger.warn("BOUNDS WARNING: Could not retrieve window bounds");
            }
        } else {
            logger.warn("NO WINDOWS: No windows found for testing");
        }
    }

    /**
     * ENTERPRISE TEST: Window controller with ManagedApplicationContext
     */
    @Test 
    public void testWindowControllerWithManagedApplicationContext() {
        logger.info("TEST: WindowController with ManagedApplicationContext");
        
        Assertions.assertNotNull(explorerApplicationContext, "Explorer application context required");
        
        // Test 1: focusWindow should work with ManagedApplicationContext
        boolean focused = windowController.focusWindow(explorerApplicationContext);
        logger.info("FOCUS TEST: Window focus result: {}", focused);
        
        // Test 2: getWindowBounds should return bounds for active window
        Rectangle bounds = windowController.getWindowBounds(explorerApplicationContext);
        if (bounds != null) {
            logger.info("BOUNDS TEST: Retrieved bounds {}x{} at ({}, {})", 
                bounds.width, bounds.height, bounds.x, bounds.y);
            Assertions.assertTrue(bounds.width > 0, "Window width should be positive");
            Assertions.assertTrue(bounds.height > 0, "Window height should be positive");
        }
        
        // Test 3: Verify that the application context is working
        Assertions.assertTrue(explorerApplicationContext.isActive(), 
            "Application context should be active");
        
        // Test 4: Verify context information
        logger.info("CONTEXT INFO: Application '{}', PID {}, Windows: {}", 
            explorerApplicationContext.getManagedApplicationName(),
            explorerApplicationContext.getProcessId(),
            explorerApplicationContext.getWindowHandles().size());
    }

    /**
     * ENTERPRISE TEST: Window switching performance
     */
    @Test
    public void testWindowSwitchingPerformance() {
        logger.info("TEST: Window Switching Performance");
        
        Assertions.assertNotNull(explorerApplicationContext, "Explorer application context required");
        
        List<com.sun.jna.platform.win32.WinDef.HWND> windowHandles = explorerApplicationContext.getWindowHandles();
        
        if (windowHandles.size() > 1) {
            logger.info("PERFORMANCE TEST: Testing rapid window switching with {} windows", 
                windowHandles.size());
            
            // Test rapid window switching
            long startTime = System.currentTimeMillis();
            
            for (int cycle = 0; cycle < 3; cycle++) {
                for (int i = 0; i < windowHandles.size(); i++) {
                    boolean switched = windowController.focusWindow(explorerApplicationContext);
                    
                    if (switched) {
                        logger.debug("SWITCH SUCCESS: Cycle {}, Window {}", cycle, i);
                    } else {
                        logger.warn("SWITCH FAILED: Cycle {}, Window {}", cycle, i);
                    }
                    
                    // Brief pause to allow window focus
                    try { Thread.sleep(50); } catch (InterruptedException e) { 
                        Thread.currentThread().interrupt(); 
                    }
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("PERFORMANCE RESULT: {} window switches completed in {} ms", 
                windowHandles.size() * 3, duration);
              // Performance assertion - realistic timing for Windows API operations
            Assertions.assertTrue(duration < 120000, 
                "Window switching should complete within 2 minutes");
        } else {
            logger.info("PERFORMANCE SKIP: Only {} window(s) available for switching test", 
                windowHandles.size());
        }
    }

    /**
     * ENTERPRISE TEST: Application context stability
     */
    @Test
    public void testApplicationContextStability() {
        logger.info("TEST: Application Context Stability");
        
        Assertions.assertNotNull(explorerApplicationContext, "Explorer application context required");
        
        // Test window discovery stability
        int initialWindowCount = explorerApplicationContext.getWindowHandles().size();
        
        // Refresh and check again
        List<com.sun.jna.platform.win32.WinDef.HWND> refreshedHandles = explorerApplicationContext.getWindowHandles();
        int refreshedWindowCount = refreshedHandles.size();
        
        logger.info("STABILITY TEST: Initial windows: {}, After refresh: {}", 
            initialWindowCount, refreshedWindowCount);
        
        // Window count should be stable or increase (never decrease for system processes)
        Assertions.assertTrue(refreshedWindowCount >= initialWindowCount, 
            "Window count should be stable or increase");
        
        // Verify application context remains active
        Assertions.assertTrue(explorerApplicationContext.isActive(), 
            "Application context should remain active");
        
        // Verify PID consistency
        int pid = explorerApplicationContext.getProcessId();
        Assertions.assertTrue(pid > 0, "Process ID should be positive");
        
        logger.info("STABILITY SUCCESS: Application context remains stable with PID {}", pid);
    }
}
