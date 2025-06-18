package com.automation.cucumber.utilities;

import com.automation.core.ScreenCapture;
import com.automation.config.ConfigManager;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cucumber Utilities - Helper methods for cucumber test execution
 * Provides common functionality used across step definitions and hooks
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class CucumberUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(CucumberUtils.class);
    private static final ScreenCapture screenCapture = new ScreenCapture();
    private static final ConcurrentHashMap<String, Object> scenarioContext = new ConcurrentHashMap<>();
    private static final AtomicInteger screenshotCounter = new AtomicInteger(0);
    
    /**
     * Attach screenshot to cucumber scenario
     * 
     * @param scenario Current scenario
     * @param description Screenshot description
     */
    public static void attachScreenshot(Scenario scenario, String description) {
        try {
            File screenshot = screenCapture.captureScreen();
            byte[] screenshotBytes = java.nio.file.Files.readAllBytes(screenshot.toPath());
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String screenshotName = String.format("%s_%s_%d", 
                description, timestamp, screenshotCounter.incrementAndGet());
            
            scenario.attach(screenshotBytes, "image/png", screenshotName);
            
            logger.debug("Screenshot attached to scenario: {} - {}", scenario.getName(), screenshotName);
            
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to scenario: {}", e.getMessage());
        }
    }
    
    /**
     * Store data in scenario context
     * 
     * @param key Context key
     * @param value Context value
     */
    public static void setScenarioContext(String key, Object value) {
        scenarioContext.put(key, value);
        logger.debug("Stored in scenario context: {} = {}", key, value);
    }
    
    /**
     * Retrieve data from scenario context
     * 
     * @param key Context key
     * @return Context value or null if not found
     */
    public static Object getScenarioContext(String key) {
        return scenarioContext.get(key);
    }
    
    /**
     * Retrieve typed data from scenario context
     * 
     * @param key Context key
     * @param type Expected type
     * @param <T> Generic type
     * @return Typed context value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getScenarioContext(String key, Class<T> type) {
        Object value = scenarioContext.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Clear scenario context
     */
    public static void clearScenarioContext() {
        scenarioContext.clear();
        logger.debug("Scenario context cleared");
    }
    
    /**
     * Check if scenario context contains key
     * 
     * @param key Context key
     * @return true if key exists
     */
    public static boolean hasScenarioContext(String key) {
        return scenarioContext.containsKey(key);
    }
    
    /**
     * Generate unique filename for test artifacts
     * 
     * @param scenario Current scenario
     * @param extension File extension
     * @return Unique filename
     */
    public static String generateUniqueFilename(Scenario scenario, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        String sanitizedName = sanitizeFilename(scenario.getName());
        return String.format("%s_%s.%s", sanitizedName, timestamp, extension);
    }
    
    /**
     * Sanitize filename by removing invalid characters
     * 
     * @param filename Original filename
     * @return Sanitized filename
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_")
                      .replaceAll("_{2,}", "_")
                      .replaceAll("^_|_$", "");
    }
    
    /**
     * Get test data directory path
     * 
     * @return Test data directory path
     */
    public static String getTestDataPath() {
        return ConfigManager.getProperty("test.data.path", "src/test/resources/testdata");
    }
    
    /**
     * Get image resources directory path
     * 
     * @return Image resources directory path
     */
    public static String getImageResourcesPath() {
        return ConfigManager.getProperty("image.path.base", "src/test/resources/images");
    }
    
    /**
     * Get screenshot output directory path
     * 
     * @return Screenshot output directory path
     */
    public static String getScreenshotPath() {
        return ConfigManager.getProperty("screenshot.path.base", "target/screenshots");
    }
    
    /**
     * Log step execution with standardized format
     * 
     * @param stepName Step name
     * @param stepType Step type (Given, When, Then)
     * @param success Execution result
     * @param details Additional details
     */
    public static void logStepExecution(String stepName, String stepType, boolean success, String details) {
        String status = success ? "✓" : "✗";
        String logMessage = String.format("[%s] %s %s: %s", status, stepType, stepName, details);
        
        if (success) {
            logger.info(logMessage);
        } else {
            logger.error(logMessage);
        }
    }
    
    /**
     * Create test artifact directory if it doesn't exist
     * 
     * @param directoryPath Directory path to create
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureDirectoryExists(String directoryPath) {
        try {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.debug("Created directory: {}", directoryPath);
                }
                return created;
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to create directory {}: {}", directoryPath, e.getMessage());
            return false;
        }
    }
    
    /**
     * Get scenario execution time in milliseconds
     * 
     * @param scenario Current scenario
     * @return Execution time or -1 if not available
     */
    public static long getScenarioExecutionTime(Scenario scenario) {
        Object startTime = getScenarioContext("scenario_start_time");
        if (startTime instanceof Long) {
            return System.currentTimeMillis() - (Long) startTime;
        }
        return -1;
    }
    
    /**
     * Mark scenario start time
     * 
     * @param scenario Current scenario
     */
    public static void markScenarioStartTime(Scenario scenario) {
        setScenarioContext("scenario_start_time", System.currentTimeMillis());
    }
    
    /**
     * Format duration in human-readable format
     * 
     * @param durationMs Duration in milliseconds
     * @return Formatted duration string
     */
    public static String formatDuration(long durationMs) {
        if (durationMs < 0) return "Unknown";
        
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else if (seconds > 0) {
            return String.format("%d.%03ds", seconds, durationMs % 1000);
        } else {
            return String.format("%dms", durationMs);
        }
    }
    
    /**
     * Extract feature name from scenario
     * 
     * @param scenario Current scenario
     * @return Feature name
     */
    public static String getFeatureName(Scenario scenario) {
        String uri = scenario.getUri().toString();
        int lastSlash = uri.lastIndexOf('/');
        int lastDot = uri.lastIndexOf('.');
        
        if (lastSlash >= 0 && lastDot > lastSlash) {
            return uri.substring(lastSlash + 1, lastDot);
        }
        return "unknown_feature";
    }
    
    /**
     * Check if running in debug mode
     * 
     * @return true if debug mode is enabled
     */
    public static boolean isDebugMode() {
        return ConfigManager.getBooleanProperty("debug.enabled", false);
    }
    
    /**
     * Check if screenshot on step is enabled
     * 
     * @return true if screenshot on step is enabled
     */
    public static boolean isScreenshotOnStepEnabled() {
        return ConfigManager.getBooleanProperty("debug.screenshot.on.step", false);
    }
}
