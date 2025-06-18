package com.automation.cucumber.stepdefinitions;

import com.automation.core.*;
import com.automation.config.ConfigManager;
import com.automation.models.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Common Step Definitions Base - Shared functionality for all step definition classes
 * Provides core automation capabilities and shared helper methods
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public abstract class CommonStepDefinitionsBase {
    
    protected static final Logger logger = LoggerFactory.getLogger(CommonStepDefinitionsBase.class);
    
    // Core automation components - available to all step definitions
    protected WindowController windowController;
    protected ScreenCapture screenCapture;
    protected OCREngine ocrEngine;
    protected ImageMatcher imageMatcher;
    
    // Test context
    protected TestResult currentTestResult;
    protected String currentApplicationName;
    
    /**
     * Constructor - Initialize automation components
     */
    public CommonStepDefinitionsBase() {
        initializeComponents();
    }
    
    /**
     * Initialize automation components
     */
    protected void initializeComponents() {
        try {
            windowController = new WindowController();
            screenCapture = new ScreenCapture();
            ocrEngine = new OCREngine();
            imageMatcher = new ImageMatcher();
            currentTestResult = new TestResult();
            
            logger.debug("Automation components initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize automation components: {}", e.getMessage());
            throw new RuntimeException("Component initialization failed", e);
        }
    }
    
    /**
     * Ensure components are initialized - safety check
     */
    protected void ensureComponentsReady() {
        if (windowController == null || screenCapture == null || 
            ocrEngine == null || imageMatcher == null) {
            initializeComponents();
        }
    }
    
    /**
     * Get image path for current application context
     */
    protected String getImagePath(String imageName) {
        initializeComponents();
        
        // Build image path based on current application context
        String basePath = ConfigManager.getProperty("image.path.base", "src/test/resources/images");
        
        if (currentApplicationName != null) {
            return basePath + "/" + currentApplicationName + "/" + imageName;
        } else {
            return basePath + "/" + imageName;
        }
    }
    
    /**
     * Click on image with error handling and reporting
     */
    protected boolean clickImage(String imagePath, String description) {
        try {
            initializeComponents();
            
            BufferedImage screenshot = screenCapture.captureFullScreen();
            File screenshotFile = screenCapture.saveBufferedImageToFile(screenshot, "click_operation");
            File buttonImage = new File(imagePath);
            
            if (!buttonImage.exists()) {
                logger.warn("Image file not found: {}", imagePath);
                return false;
            }
            
            Rectangle match = imageMatcher.findImage(screenshotFile, buttonImage);
            if (match != null) {
                int clickX = match.x + match.width / 2;
                int clickY = match.y + match.height / 2;
                windowController.clickAt(clickX, clickY);
                
                logger.info("Successfully clicked image {} at ({}, {})", imagePath, clickX, clickY);
                return true;
            } else {
                logger.warn("Image {} not found on screen", imagePath);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to click image {}: {}", imagePath, e.getMessage());
            return false;
        }
    }
    
    /**
     * Set current application context
     */
    protected void setCurrentApplication(String applicationName) {
        this.currentApplicationName = applicationName;
    }
    
    /**
     * Get current test result
     */
    protected TestResult getCurrentTestResult() {
        if (currentTestResult == null) {
            currentTestResult = new TestResult();
        }
        return currentTestResult;
    }
    
    /**
     * Wait for element with timeout
     */
    protected boolean waitForElement(String imagePath, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                BufferedImage screenshot = screenCapture.captureFullScreen();
                File screenshotFile = screenCapture.saveBufferedImageToFile(screenshot, "wait_operation");
                File elementImage = new File(imagePath);
                
                Rectangle match = imageMatcher.findImage(screenshotFile, elementImage);
                if (match != null) {
                    return true;
                }
                
                Thread.sleep(500);
            } catch (Exception e) {
                logger.warn("Error while waiting for element: {}", e.getMessage());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Verify text exists on screen
     */
    protected boolean verifyTextOnScreen(String expectedText) {
        try {
            initializeComponents();
            
            BufferedImage screenshot = screenCapture.captureFullScreen();
            File screenshotFile = screenCapture.saveBufferedImageToFile(screenshot, "text_verification");
            String screenText = ocrEngine.extractText(screenshotFile);
            
            return screenText.toLowerCase().contains(expectedText.toLowerCase());
        } catch (Exception e) {
            logger.error("Failed to verify text on screen: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Verify text exists in specific region
     */
    protected boolean verifyTextInRegion(Rectangle region, String expectedText) {
        try {
            initializeComponents();
            
            BufferedImage regionCapture = screenCapture.captureRegion(region);
            File regionFile = screenCapture.saveBufferedImageToFile(regionCapture, "region_verification");
            String regionText = ocrEngine.extractText(regionFile);
            
            return regionText.toLowerCase().contains(expectedText.toLowerCase());
        } catch (Exception e) {
            logger.error("Failed to verify text in region: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle test step failure with screenshot capture
     */
    protected void handleTestFailure(String stepName, Exception e) {
        try {
            logger.error("Test step '{}' failed: {}", stepName, e.getMessage());
            
            // Capture error screenshot
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String screenshotPath = screenCapture.saveBufferedImageToFile(errorScreenshot, "error_" + stepName).getAbsolutePath();
            
            // Add to test result if available
            if (currentTestResult != null) {
                currentTestResult.addVerification(stepName, false, e.getMessage());
                currentTestResult.addScreenshot("error", screenshotPath);
            }
        } catch (Exception screenshotException) {
            logger.error("Failed to capture error screenshot: {}", screenshotException.getMessage());
        }
    }
    
    /**
     * Clean up resources after test step
     */
    protected void cleanupAfterStep() {
        // Override in subclasses if needed
    }
    
    /**
     * Add verification to current test result
     */
    protected void addVerification(String step, boolean passed, String details) {
        if (currentTestResult == null) {
            currentTestResult = new TestResult();
        }
        currentTestResult.addVerification(step, passed, details);
        
        if (passed) {
            logger.info("✓ {}: {}", step, details);
        } else {
            logger.error("✗ {}: {}", step, details);
        }
    }
    
    /**
     * Capture a screenshot with a custom name
     */
    protected void captureScreenshot(String name) {
        try {
            initializeComponents();
            // Use captureAndSaveWithTimestamp to ensure unique filenames and proper saving
            String screenshotPath = screenCapture.captureAndSaveWithTimestamp(name);

            if (currentTestResult != null && screenshotPath != null) {
                currentTestResult.addScreenshot(name, screenshotPath);
            }

            logger.debug("Screenshot captured: {} at {}", name, screenshotPath);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot '{}': {}", name, e.getMessage());
        }
    }

    /**
     * Captures an error screenshot and adds it to the current test result.
     * The screenshot is named based on the provided error description.
     *
     * @param errorDescription A description of the error, used for naming the screenshot.
     */
    protected void captureErrorScreenshot(String errorDescription) {
        try {
            ensureComponentsReady(); // Make sure screenCapture is initialized
            if (screenCapture == null) {
                logger.error("ScreenCapture service is not available, cannot capture error screenshot.");
                return;
            }

            // Define a subdirectory for error screenshots, e.g., "error_screenshots"
            // This could also be made configurable via ConfigManager
            String errorScreenshotSubDir = "error_screenshots"; 
            String screenshotPath = screenCapture.captureAndSaveErrorScreenshot(errorDescription, errorScreenshotSubDir);

            if (screenshotPath != null) {
                logger.info("Error screenshot captured: {}", screenshotPath);
                if (currentTestResult != null) {
                    // Add screenshot to test results, perhaps with a specific category like "error"
                    currentTestResult.addScreenshot("ERROR_" + errorDescription, screenshotPath);
                }
            } else {
                logger.warn("Failed to capture or save error screenshot for: {}", errorDescription);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while capturing error screenshot for '{}': {}", errorDescription, e.getMessage(), e);
        }
    }

    /**
     * Safe windowController access with initialization check
     */
    protected WindowController getWindowController() {
        ensureComponentsReady();
        return windowController;
    }
    
    /**
     * Safe screenCapture access with initialization check
     */
    protected ScreenCapture getScreenCapture() {
        ensureComponentsReady();
        return screenCapture;
    }
    
    /**
     * Safe ocrEngine access with initialization check
     */
    protected OCREngine getOCREngine() {
        ensureComponentsReady();
        return ocrEngine;
    }
    
    /**
     * Safe imageMatcher access with initialization check
     */
    protected ImageMatcher getImageMatcher() {
        ensureComponentsReady();
        return imageMatcher;
    }
}
