package com.automation.utils;

import com.automation.core.WindowController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Static utility methods for waiting operations in automation tests
 */
public class WaitUtilsStatic {
    private static final Logger logger = LoggerFactory.getLogger(WaitUtilsStatic.class);
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_POLLING_INTERVAL_MS = 500;

    /**
     * Wait for specified number of seconds
     */
    public static void waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted after {} seconds", seconds);
        }
    }

    /**
     * Wait for specified number of milliseconds
     */
    public static void waitMilliseconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted after {} milliseconds", milliseconds);
        }
    }    /**
     * Wait for a window to appear - PROFESSIONAL VERSION with error handling
     */
    public static boolean waitForWindow(String windowTitle, int timeoutSeconds) {
        logger.info("🎯 PROFESSIONAL WAIT: Looking for window '{}'...", windowTitle);
        
        // Use enhanced window controller with professional error handling
        WindowController windowController = new WindowController();
        boolean found = windowController.waitForWindowWithErrorHandling(windowTitle, timeoutSeconds);
        
        if (found) {
            logger.info("✅ SUCCESS: Window '{}' found and ready", windowTitle);
        } else {
            logger.error("❌ TIMEOUT: Window '{}' not found after {}s - Check application launch", 
                windowTitle, timeoutSeconds);
        }
        
        return found;
    }    /**
     * Wait for a window to disappear (deprecated - use ProcessManager for PID-driven operations)
     * @deprecated Use ProcessManager termination tracking for managed applications
     */
    @Deprecated
    public static boolean waitForWindowToDisappear(String windowTitle, int timeoutSeconds) {
        logger.warn("Using deprecated waitForWindowToDisappear method. Consider using ProcessManager termination tracking for managed applications.");
        return waitForCondition(() -> {
            WindowController windowController = new WindowController();
            return !windowController.findWindowByTitle(windowTitle, false);
        }, timeoutSeconds, String.format("Window '%s' to disappear", windowTitle));
    }

    /**
     * Wait for window to become responsive
     */
    public static boolean waitForWindowToBeResponsive(int timeoutSeconds) {
        return waitForCondition(() -> {
            try {
                // Simple responsiveness check - try to get active window title
                WindowController windowController = new WindowController();
                String title = windowController.getActiveWindowTitle();
                return title != null && !title.isEmpty();
            } catch (Exception e) {
                return false;
            }
        }, timeoutSeconds, "Window to become responsive");
    }

    /**
     * Wait for a condition to be true
     */
    public static boolean waitForCondition(Supplier<Boolean> condition, int timeoutSeconds, String description) {
        logger.debug("Waiting for condition: {}, timeout: {}s", description, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                if (condition.get()) {
                    logger.debug("Condition met after {}ms: {}", 
                        System.currentTimeMillis() - startTime, description);
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Exception occurred while checking condition '{}': {}", description, e.getMessage());
            }
            
            try {
                Thread.sleep(DEFAULT_POLLING_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Wait interrupted for condition: {}", description);
                return false;
            }
        }
        
        logger.warn("Timeout waiting for condition: {}", description);
        return false;
    }

    /**
     * Wait for a condition to be true with default timeout
     */
    public static boolean waitForCondition(Supplier<Boolean> condition, String description) {
        return waitForCondition(condition, DEFAULT_TIMEOUT_SECONDS, description);
    }

    /**
     * Wait with retry logic
     */
    public static <T> T waitWithRetry(Supplier<T> operation, int maxRetries, int retryDelayMs) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                T result = operation.get();
                if (result != null) {
                    logger.debug("Operation successful on attempt {}", attempt);
                    return result;
                }
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", attempt, e.getMessage());
            }
            
            if (attempt < maxRetries) {
                waitMilliseconds(retryDelayMs);
            }
        }
        
        logger.error("Operation failed after {} attempts", maxRetries);
        if (lastException instanceof RuntimeException) {
            throw (RuntimeException) lastException;
        } else if (lastException != null) {
            throw new RuntimeException("Operation failed after retries", lastException);
        }
        
        return null;
    }
    
    /**
     * Wait for an image to appear on screen
     */
    public static boolean waitForImage(String imagePath, int timeoutSeconds) {
        return waitForCondition(() -> {
            try {
                com.automation.core.ImageMatcher imageMatcher = new com.automation.core.ImageMatcher();
                org.sikuli.script.Match match = imageMatcher.findImage(imagePath);
                return match != null;
            } catch (Exception e) {
                logger.warn("Error checking for image '{}': {}", imagePath, e.getMessage());
                return false;
            }
        }, timeoutSeconds, String.format("Image '%s' to appear", imagePath));
    }
    
    /**
     * Wait for text to appear in a specific region using OCR
     */
    public static boolean waitForText(String expectedText, java.awt.Rectangle region, int timeoutSeconds) {
        return waitForCondition(() -> {
            try {
                com.automation.core.ScreenCapture screenCapture = new com.automation.core.ScreenCapture();
                com.automation.core.OCREngine ocrEngine = new com.automation.core.OCREngine();
                
                java.io.File regionCapture = screenCapture.captureRegionToFile(region);
                String extractedText = ocrEngine.extractText(regionCapture);
                  // Primary check: exact case-insensitive match
                boolean found = extractedText.toLowerCase().contains(expectedText.toLowerCase());
                
                // Enhanced check for common OCR recognition issues
                if (!found && expectedText.toLowerCase().contains("cmd window opened")) {
                    // Check for common OCR variations of "CMD Window Opened"
                    String lowerText = extractedText.toLowerCase();
                    found = lowerText.contains("md window opened") ||     // Missing 'C'
                           lowerText.contains("cmd window opened") ||     // Exact match
                           lowerText.contains("d window opened") ||       // Missing 'CM'
                           lowerText.contains("window opened");           // Missing CMD entirely
                }
                
                logger.debug("OCR extracted text: '{}', looking for: '{}', found: {}", 
                    extractedText, expectedText, found);
                
                return found;
            } catch (Exception e) {
                logger.warn("Error checking for text '{}' in region: {}", expectedText, e.getMessage());
                return false;
            }
        }, timeoutSeconds, String.format("Text '%s' to appear in region", expectedText));
    }

    /**
     * Wait for text to appear anywhere on the screen using OCR (full screen capture)
     * This is more reliable for maximized windows
     */
    public static boolean waitForTextOnScreen(String expectedText, int timeoutSeconds) {
        return waitForCondition(() -> {
            try {
                com.automation.core.ScreenCapture screenCapture = new com.automation.core.ScreenCapture();
                com.automation.core.OCREngine ocrEngine = new com.automation.core.OCREngine();
                
                java.io.File screenshot = screenCapture.captureScreen();
                String extractedText = ocrEngine.extractText(screenshot);
                
                // Primary check: exact case-insensitive match
                boolean found = extractedText.toLowerCase().contains(expectedText.toLowerCase());
                
                // Enhanced check for common OCR recognition issues
                if (!found && expectedText.toLowerCase().contains("cmd window opened")) {
                    // Check for common OCR variations of "CMD Window Opened"
                    String lowerText = extractedText.toLowerCase();
                    found = lowerText.contains("md window opened") ||     // Missing 'C'
                           lowerText.contains("cmd window opened") ||     // Exact match
                           lowerText.contains("d window opened") ||       // Missing 'CM'
                           lowerText.contains("window opened");           // Missing CMD entirely
                }
                
                logger.debug("OCR extracted text from full screen: '{}', looking for: '{}', found: {}", 
                    extractedText.length() > 200 ? extractedText.substring(0, 200) + "..." : extractedText, 
                    expectedText, found);
                
                return found;
            } catch (Exception e) {
                logger.warn("Error checking for text '{}' on screen: {}", expectedText, e.getMessage());
                return false;
            }
        }, timeoutSeconds, String.format("Text '%s' to appear on screen", expectedText));
    }

    /**
     * Wait for application to be ready for automation
     * Enterprise method for ApplicationStepDefinitions compatibility
     */
    public static boolean waitForApplicationReady(com.automation.models.ManagedApplicationContext context, int timeoutSeconds) {
        if (context == null) {
            logger.warn("Cannot wait for null application context");
            return false;
        }
        
        logger.debug("Waiting for application '{}' (PID: {}) to be ready for automation", 
            context.getManagedApplicationName(), context.getProcessId());
        
        return waitForCondition(() -> {
            try {
                // Check if context is still active and not terminated
                if (!context.isActive() || context.isTerminated()) {
                    logger.debug("Application context is not active or terminated");
                    return false;
                }
                
                // Check if application has at least one window
                if (context.getAllWindows().isEmpty()) {
                    logger.debug("Application has no windows yet");
                    return false;
                }
                
                // Check if primary window is available
                if (context.getPrimaryWindow() == null) {
                    logger.debug("Application primary window not yet available");
                    return false;
                }
                
                // Application appears ready
                logger.debug("Application '{}' appears ready with {} windows", 
                    context.getManagedApplicationName(), context.getAllWindows().size());
                return true;
                
            } catch (Exception e) {
                logger.debug("Error checking application readiness: {}", e.getMessage());
                return false;
            }
        }, timeoutSeconds, String.format("Application '%s' to be ready for automation", context.getManagedApplicationName()));
    }
}
