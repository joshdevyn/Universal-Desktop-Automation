package com.automation.cucumber.stepdefinitions;

import io.cucumber.java.en.*;
import com.automation.core.ProcessManager;
import com.automation.models.ManagedApplicationContext;
import com.automation.utils.VariableManager;
import com.automation.utils.WaitUtilsStatic;
import com.automation.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.io.File;

/**
 * Step definitions for wait operations and timing synchronization
 * Supports waiting for windows, images, text, and custom conditions
 */
public class WaitStepDefinitions extends CommonStepDefinitionsBase {
    private static final Logger logger = LoggerFactory.getLogger(WaitStepDefinitions.class);    @When("I wait for {int} second")
    public void i_wait_for_second(Integer int1) {
        logger.info("Waiting for {} seconds", int1);
        WaitUtilsStatic.waitSeconds(int1);
        addVerification("Wait Seconds", true, 
            String.format("Waited for %d seconds", int1));
        logger.info("Waited for {} seconds", int1);
    }

    @When("I wait for {int} seconds")
    public void i_wait_for_seconds(Integer int1) {
        logger.info("Waiting for {} seconds", int1);
        WaitUtilsStatic.waitSeconds(int1);
        addVerification("Wait Seconds", true, 
            String.format("Waited for %d seconds", int1));
        logger.info("Waited for {} seconds", int1);
    }    @When("I wait for managed application {string} to appear")
    public void i_wait_for_managed_application_to_appear(String managedApplicationName) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        int timeoutSeconds = ConfigManager.getIntProperty("window.focus.timeout", 30);
        logger.info("Enterprise Wait: Monitoring for managed application '{}' to appear with timeout {}s", interpolatedAppName, timeoutSeconds);
        
        boolean applicationAppeared = WaitUtilsStatic.waitForCondition(() -> {
            try {
                ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
                return context != null;
            } catch (Exception e) {
                return false;
            }
        }, timeoutSeconds, String.format("Managed application '%s' to appear", interpolatedAppName));
        
        if (applicationAppeared) {
            addVerification("Wait for Managed Application", true, 
                String.format("Managed application '%s' appeared within %d seconds", interpolatedAppName, timeoutSeconds));
            logger.info("Enterprise Success: Managed application '{}' appeared within {} seconds", interpolatedAppName, timeoutSeconds);
        } else {
            addVerification("Wait for Managed Application", false, 
                String.format("Managed application '%s' did not appear within %d seconds", interpolatedAppName, timeoutSeconds));
            captureScreenshot("wait_managed_app_failed");
            throw new RuntimeException("Managed application did not appear within timeout: " + interpolatedAppName);
        }
    }    @When("I wait for managed application {string} to appear with timeout {int} seconds")
    public void i_wait_for_managed_application_to_appear_with_timeout(String managedApplicationName, int timeoutSeconds) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("Enterprise Wait: Monitoring for managed application '{}' to appear with timeout {}s", interpolatedAppName, timeoutSeconds);
        
        boolean applicationAppeared = WaitUtilsStatic.waitForCondition(() -> {
            try {
                ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
                return context != null;
            } catch (Exception e) {
                return false;
            }
        }, timeoutSeconds, String.format("Managed application '%s' to appear", interpolatedAppName));
        
        addVerification("Wait for Managed Application with Timeout", applicationAppeared,
            String.format("%s Managed application '%s' %s within %d seconds", 
                applicationAppeared ? "✅" : "❌", interpolatedAppName, applicationAppeared ? "appeared" : "did not appear", timeoutSeconds));
        
        if (!applicationAppeared) {
            captureScreenshot("wait_managed_app_timeout_failed");
            throw new RuntimeException("Managed application did not appear within timeout: " + interpolatedAppName);
        }
        
        logger.info("Enterprise Success: Managed application '{}' appeared successfully within {} seconds", interpolatedAppName, timeoutSeconds);
    }@When("I wait for image {string} to appear")
    public void i_wait_for_image_to_appear(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        String imagePath = ConfigManager.getImagePath(interpolatedImageName);
        
        boolean imageAppeared = WaitUtilsStatic.waitForImage(imagePath, 30);
        addVerification("Wait for Image", imageAppeared,
            String.format("Image '%s' %s", interpolatedImageName, imageAppeared ? "appeared" : "did not appear"));
        
        if (!imageAppeared) {
            captureScreenshot("wait_image_failed");
            throw new RuntimeException("Image did not appear: " + interpolatedImageName);
        }
    }

    @When("I wait for image {string} to appear with timeout {int} seconds")
    public void i_wait_for_image_to_appear_with_timeout(String imageName, int timeoutSeconds) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        String imagePath = ConfigManager.getImagePath(interpolatedImageName);
        
        boolean imageAppeared = WaitUtilsStatic.waitForImage(imagePath, timeoutSeconds);
        addVerification("Wait for Image with Timeout", imageAppeared,
            String.format("Image '%s' %s within %d seconds", 
                interpolatedImageName, imageAppeared ? "appeared" : "did not appear", timeoutSeconds));
        
        if (!imageAppeared) {
            captureScreenshot("wait_image_timeout_failed");
            throw new RuntimeException("Image did not appear within timeout: " + interpolatedImageName);
        }
    }

    @When("I wait for image {string} to appear in region {string}")
    public void i_wait_for_image_to_appear_in_region(String imageName, String regionName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
        
        boolean imageAppeared = waitForImageInRegion(interpolatedImageName, region, 30);
        addVerification("Wait for Image in Region", imageAppeared,
            String.format("Image '%s' %s in region '%s'", 
                interpolatedImageName, imageAppeared ? "appeared" : "did not appear", regionName));
        
        if (!imageAppeared) {
            captureScreenshot("wait_image_region_failed");
            throw new RuntimeException("Image did not appear in region: " + interpolatedImageName);
        }
    }

    @When("I wait for text {string} to appear")
    public void i_wait_for_text_to_appear(String text) {
        String interpolatedText = VariableManager.interpolate(text);
        
        // Use screen capture to define a full screen region
        Rectangle screenRegion = new Rectangle(0, 0, 1920, 1080); // Default screen size
        boolean textAppeared = WaitUtilsStatic.waitForText(interpolatedText, screenRegion, 30);
        addVerification("Wait for Text", textAppeared,
            String.format("Text '%s' %s", interpolatedText, textAppeared ? "appeared" : "did not appear"));
        
        if (!textAppeared) {
            captureScreenshot("wait_text_failed");
            throw new RuntimeException("Text did not appear: " + interpolatedText);
        }
    }

    @When("I wait for text {string} to appear with timeout {int} seconds")
    public void i_wait_for_text_to_appear_with_timeout(String text, int timeoutSeconds) {
        String interpolatedText = VariableManager.interpolate(text);
        
        Rectangle screenRegion = new Rectangle(0, 0, 1920, 1080);
        boolean textAppeared = WaitUtilsStatic.waitForText(interpolatedText, screenRegion, timeoutSeconds);
        addVerification("Wait for Text with Timeout", textAppeared,
            String.format("Text '%s' %s within %d seconds", 
                interpolatedText, textAppeared ? "appeared" : "did not appear", timeoutSeconds));
        
        if (!textAppeared) {
            captureScreenshot("wait_text_timeout_failed");
            throw new RuntimeException("Text did not appear within timeout: " + interpolatedText);
        }
    }

    @When("I wait for text {string} to appear in region {string}")
    public void i_wait_for_text_to_appear_in_region(String text, String regionName) {
        String interpolatedText = VariableManager.interpolate(text);
        Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
        
        boolean textAppeared = WaitUtilsStatic.waitForText(interpolatedText, region, 30);
        addVerification("Wait for Text in Region", textAppeared,
            String.format("Text '%s' %s in region '%s'", 
                interpolatedText, textAppeared ? "appeared" : "did not appear", regionName));
        
        if (!textAppeared) {
            captureScreenshot("wait_text_region_failed");
            throw new RuntimeException("Text did not appear in region: " + interpolatedText);
        }
    }

    @When("I wait for text {string} to appear in region {string} with timeout {int} seconds")
    public void i_wait_for_text_to_appear_in_region_with_timeout(String text, String regionName, int timeoutSeconds) {
        String interpolatedText = VariableManager.interpolate(text);
        Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
        
        boolean textAppeared = WaitUtilsStatic.waitForText(interpolatedText, region, timeoutSeconds);
        addVerification("Wait for Text in Region with Timeout", textAppeared,
            String.format("Text '%s' %s in region '%s' within %d seconds", 
                interpolatedText, textAppeared ? "appeared" : "did not appear", regionName, timeoutSeconds));
        
        if (!textAppeared) {
            captureScreenshot("wait_text_region_timeout_failed");
            throw new RuntimeException("Text did not appear in region within timeout: " + interpolatedText);
        }
    }

    @When("I wait for text {string} to disappear")
    public void i_wait_for_text_to_disappear(String text) {
        String interpolatedText = VariableManager.interpolate(text);
        
        boolean textDisappeared = waitForTextToDisappear(interpolatedText, 30);
        addVerification("Wait for Text to Disappear", textDisappeared,
            String.format("Text '%s' %s", interpolatedText, textDisappeared ? "disappeared" : "did not disappear"));
        
        if (!textDisappeared) {
            captureScreenshot("wait_text_disappear_failed");
            throw new RuntimeException("Text did not disappear: " + interpolatedText);
        }
    }

    @When("I wait for image {string} to disappear")
    public void i_wait_for_image_to_disappear(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        String imagePath = ConfigManager.getImagePath(interpolatedImageName);
        
        boolean imageDisappeared = waitForImageToDisappear(imagePath, 30);
        addVerification("Wait for Image to Disappear", imageDisappeared,
            String.format("Image '%s' %s", interpolatedImageName, imageDisappeared ? "disappeared" : "did not disappear"));
        
        if (!imageDisappeared) {
            captureScreenshot("wait_image_disappear_failed");
            throw new RuntimeException("Image did not disappear: " + interpolatedImageName);
        }
    }

    // =====================================================================================
    // ENTERPRISE WAIT AUTOMATION - SMART SYNCHRONIZATION FOR ANY APPLICATION
    // =====================================================================================
    
    @When("I wait until screen becomes stable for {int} seconds")
    public void i_wait_until_screen_becomes_stable(int stabilityDurationSeconds) {
        logger.info("Waiting until screen becomes stable for {} seconds", stabilityDurationSeconds);
        
        try {
            boolean isStable = waitForScreenStability(stabilityDurationSeconds, 30); // Max 30 seconds wait
            addVerification("Wait for Screen Stability", isStable,
                String.format("Screen %s stable for %d seconds", 
                    isStable ? "became" : "did not become", stabilityDurationSeconds));
            
            if (!isStable) {
                captureScreenshot("screen_stability_timeout");
                throw new RuntimeException("Screen did not become stable within timeout");
            }
            
            logger.info("Screen successfully became stable for {} seconds", stabilityDurationSeconds);
        } catch (Exception e) {
            logger.error("Failed to wait for screen stability: {}", e.getMessage(), e);
            addVerification("Wait for Screen Stability", false, 
                "Screen stability wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for screen stability", e);
        }
    }
    
    @When("I wait for color {string} to appear at coordinates {int}, {int}")
    public void i_wait_for_color_at_coordinates(String colorHex, int x, int y) {
        logger.info("Waiting for color '{}' to appear at coordinates ({}, {})", colorHex, x, y);
        
        try {
            boolean colorAppeared = waitForColorAtCoordinates(colorHex, x, y, 30);
            addVerification("Wait for Color", colorAppeared,
                String.format("Color '%s' %s at coordinates (%d, %d)", 
                    colorHex, colorAppeared ? "appeared" : "did not appear", x, y));
            
            if (!colorAppeared) {
                captureScreenshot("color_wait_failed");
                throw new RuntimeException("Color did not appear at coordinates: " + colorHex);
            }
            
            logger.info("Color '{}' successfully appeared at coordinates ({}, {})", colorHex, x, y);
        } catch (Exception e) {
            logger.error("Failed to wait for color '{}' at coordinates ({}, {}): {}", colorHex, x, y, e.getMessage(), e);
            addVerification("Wait for Color", false, 
                "Color wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for color at coordinates", e);
        }
    }
    
    @When("I wait for multiple images in sequence: {string}")
    public void i_wait_for_multiple_images_in_sequence(String imageSequence) {
        logger.info("Waiting for multiple images in sequence: '{}'", imageSequence);
        
        try {
            String[] imageNames = imageSequence.split(",");
            int totalImages = imageNames.length;
            
            for (int i = 0; i < imageNames.length; i++) {
                String imageName = imageNames[i].trim();
                String interpolatedImageName = VariableManager.interpolate(imageName);
                String imagePath = ConfigManager.getImagePath(interpolatedImageName);
                
                logger.debug("Waiting for image {} of {}: '{}'", (i + 1), totalImages, interpolatedImageName);
                
                boolean imageAppeared = WaitUtilsStatic.waitForImage(imagePath, 15); // 15 seconds per image
                if (!imageAppeared) {
                    captureScreenshot("image_sequence_failed_" + interpolatedImageName);
                    throw new RuntimeException("Image sequence failed at: " + interpolatedImageName);
                }
                
                logger.debug("Image {} of {} appeared: '{}'", (i + 1), totalImages, interpolatedImageName);
                WaitUtilsStatic.waitMilliseconds(500); // Small delay between images
            }
            
            addVerification("Wait for Image Sequence", true,
                String.format("Successfully waited for %d images in sequence", totalImages));
            logger.info("Successfully waited for all {} images in sequence", totalImages);
        } catch (Exception e) {
            logger.error("Failed to wait for image sequence '{}': {}", imageSequence, e.getMessage(), e);
            addVerification("Wait for Image Sequence", false, 
                "Image sequence wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for image sequence", e);
        }
    }
    
    @When("I wait for text {string} with polling interval {int} milliseconds and timeout {int} seconds")
    public void i_wait_for_text_with_polling_interval(String text, int pollingIntervalMs, int timeoutSeconds) {
        String interpolatedText = VariableManager.interpolate(text);
        logger.info("Waiting for text '{}' with polling interval {}ms and timeout {}s", 
            interpolatedText, pollingIntervalMs, timeoutSeconds);
        
        try {
            boolean textAppeared = waitForTextWithPolling(interpolatedText, pollingIntervalMs, timeoutSeconds);
            addVerification("Wait for Text with Polling", textAppeared,
                String.format("Text '%s' %s with polling interval %dms", 
                    interpolatedText, textAppeared ? "appeared" : "did not appear", pollingIntervalMs));
            
            if (!textAppeared) {
                captureScreenshot("text_polling_wait_failed");
                throw new RuntimeException("Text did not appear with custom polling: " + interpolatedText);
            }
            
            logger.info("Text '{}' successfully appeared with custom polling", interpolatedText);
        } catch (Exception e) {
            logger.error("Failed to wait for text '{}' with polling: {}", interpolatedText, e.getMessage(), e);
            addVerification("Wait for Text with Polling", false, 
                "Text polling wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for text with custom polling", e);
        }
    }
    
    @When("I wait for any of these texts to appear: {string}")
    public void i_wait_for_any_text_to_appear(String textOptions) {
        logger.info("Waiting for any of these texts to appear: '{}'", textOptions);
        
        try {
            String[] texts = textOptions.split(",");
            String foundText = waitForAnyText(texts, 30);
            
            boolean anyTextFound = foundText != null;
            addVerification("Wait for Any Text", anyTextFound,
                String.format("Any text from options %s: %s", 
                    anyTextFound ? "found" : "not found", 
                    anyTextFound ? "'" + foundText + "'" : "none"));
            
            if (!anyTextFound) {
                captureScreenshot("any_text_wait_failed");
                throw new RuntimeException("None of the specified texts appeared: " + textOptions);
            }
            
            // Store which text was found
            VariableManager.setSessionVariable("found_text", foundText);
            logger.info("Successfully found text: '{}'", foundText);
        } catch (Exception e) {
            logger.error("Failed to wait for any text from options '{}': {}", textOptions, e.getMessage(), e);
            addVerification("Wait for Any Text", false, 
                "Any text wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for any text from options", e);
        }
    }
      @When("I wait for managed application {string} to become active with timeout {int} seconds")
    public void i_wait_for_managed_application_to_become_active(String managedApplicationName, int timeoutSeconds) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("Waiting for managed application '{}' to become active with timeout {}s", interpolatedAppName, timeoutSeconds);
        
        try {
            boolean applicationBecameActive = waitForManagedApplicationActive(interpolatedAppName, timeoutSeconds);
            addVerification("Wait for Managed Application Active", applicationBecameActive,
                String.format("Managed application '%s' %s active within %d seconds", 
                    interpolatedAppName, applicationBecameActive ? "became" : "did not become", timeoutSeconds));
            
            if (!applicationBecameActive) {
                captureScreenshot("managed_app_active_wait_failed");
                throw new RuntimeException("Managed application did not become active: " + interpolatedAppName);
            }
            
            logger.info("Managed application '{}' successfully became active", interpolatedAppName);
        } catch (Exception e) {
            logger.error("Failed to wait for managed application '{}' to become active: {}", interpolatedAppName, e.getMessage(), e);
            addVerification("Wait for Managed Application Active", false, 
                "Managed application active wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for managed application to become active", e);
        }    }
    
    @When("I wait for process {string} to start running")
    public void i_wait_for_process_to_start(String processName) {
        String interpolatedProcess = VariableManager.interpolate(processName);
        logger.info("Waiting for process '{}' to start running", interpolatedProcess);
        
        try {
            boolean processStarted = waitForProcessStart(interpolatedProcess, 30);
            addVerification("Wait for Process Start", processStarted,
                String.format("Process '%s' %s", 
                    interpolatedProcess, processStarted ? "started running" : "did not start"));
            
            if (!processStarted) {
                throw new RuntimeException("Process did not start: " + interpolatedProcess);
            }
            
            logger.info("Process '{}' successfully started running", interpolatedProcess);
        } catch (Exception e) {
            logger.error("Failed to wait for process '{}' to start: {}", interpolatedProcess, e.getMessage(), e);
            addVerification("Wait for Process Start", false, 
                "Process start wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for process to start", e);
        }
    }
    
    @When("I wait for process {string} to stop running")
    public void i_wait_for_process_to_stop(String processName) {
        String interpolatedProcess = VariableManager.interpolate(processName);
        logger.info("Waiting for process '{}' to stop running", interpolatedProcess);
        
        try {
            boolean processStopped = waitForProcessStop(interpolatedProcess, 30);
            addVerification("Wait for Process Stop", processStopped,
                String.format("Process '%s' %s", 
                    interpolatedProcess, processStopped ? "stopped running" : "did not stop"));
            
            if (!processStopped) {
                throw new RuntimeException("Process did not stop: " + interpolatedProcess);
            }
            
            logger.info("Process '{}' successfully stopped running", interpolatedProcess);
        } catch (Exception e) {
            logger.error("Failed to wait for process '{}' to stop: {}", interpolatedProcess, e.getMessage(), e);
            addVerification("Wait for Process Stop", false, 
                "Process stop wait failed: " + e.getMessage());
            throw new RuntimeException("Failed to wait for process to stop", e);
        }
    }
    
    // =====================================================================================
    // ADVANCED HELPER METHODS FOR COMPLEX WAITING SCENARIOS
    // =====================================================================================
    
    private boolean waitForImageInRegion(String imageName, Rectangle region, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                File regionCapture = screenCapture.captureRegionToFile(region);
                File templateImage = new File(ConfigManager.getImagePath(imageName));
                
                Rectangle match = imageMatcher.findImage(regionCapture, templateImage);
                if (match != null) {
                    return true;
                }
                
                Thread.sleep(500);
            } catch (Exception e) {
                logger.warn("Error while waiting for image in region: {}", e.getMessage());
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
    
    private boolean waitForTextToDisappear(String text, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                File screenshot = screenCapture.captureScreen();
                String extractedText = ocrEngine.extractText(screenshot);
                
                if (!extractedText.toLowerCase().contains(text.toLowerCase())) {
                    return true;
                }
                
                Thread.sleep(500);
            } catch (Exception e) {
                logger.warn("Error while waiting for text to disappear: {}", e.getMessage());
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
    
    private boolean waitForImageToDisappear(String imagePath, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                File screenshot = screenCapture.captureScreen();
                File templateImage = new File(imagePath);
                
                Rectangle match = imageMatcher.findImage(screenshot, templateImage);
                if (match == null) {
                    return true;
                }
                
                Thread.sleep(500);
            } catch (Exception e) {
                logger.warn("Error while waiting for image to disappear: {}", e.getMessage());
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
    
    private boolean waitForScreenStability(int stabilityDurationSeconds, int maxWaitSeconds) {
        long stabilityDurationMs = stabilityDurationSeconds * 1000;
        long maxWaitMs = maxWaitSeconds * 1000;
        long startTime = System.currentTimeMillis();
        long lastChangeTime = startTime;
        File previousScreenshot = null;
        
        while (System.currentTimeMillis() - startTime < maxWaitMs) {
            try {
                File currentScreenshot = screenCapture.captureScreen();
                
                if (previousScreenshot != null) {
                    double similarity = imageMatcher.calculateSimilarity(previousScreenshot, currentScreenshot);
                    
                    if (similarity < 0.98) { // Screen changed
                        lastChangeTime = System.currentTimeMillis();
                        logger.debug("Screen changed - similarity: {:.2f}%", similarity * 100);
                    } else {
                        // Check if stable for required duration
                        long stableDuration = System.currentTimeMillis() - lastChangeTime;
                        if (stableDuration >= stabilityDurationMs) {
                            logger.debug("Screen stable for {} seconds", stableDuration / 1000.0);
                            return true;
                        }
                    }
                }
                  previousScreenshot = currentScreenshot;
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                logger.warn("Error checking screen stability: {}", e.getMessage());
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
    
    private boolean waitForColorAtCoordinates(String colorHex, int x, int y, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        // Parse hex color
        int targetColor = Integer.parseInt(colorHex.replace("#", ""), 16);
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                java.awt.image.BufferedImage screenshot = screenCapture.captureFullScreen();
                if (x < screenshot.getWidth() && y < screenshot.getHeight()) {
                    int pixelColor = screenshot.getRGB(x, y) & 0xFFFFFF; // Remove alpha
                    
                    if (pixelColor == targetColor) {
                        return true;
                    }
                }
                  Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                logger.warn("Error checking color at coordinates: {}", e.getMessage());
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
    
    private boolean waitForTextWithPolling(String text, int pollingIntervalMs, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                File screenshot = screenCapture.captureScreen();
                String extractedText = ocrEngine.extractText(screenshot);
                
                if (extractedText.toLowerCase().contains(text.toLowerCase())) {
                    return true;
                }
                  Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                logger.warn("Error while waiting for text with polling: {}", e.getMessage());
                try {
                    Thread.sleep(pollingIntervalMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        return false;
    }
    
    private String waitForAnyText(String[] texts, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                File screenshot = screenCapture.captureScreen();
                String extractedText = ocrEngine.extractText(screenshot).toLowerCase();
                
                for (String text : texts) {
                    String trimmedText = VariableManager.interpolate(text.trim());
                    if (extractedText.contains(trimmedText.toLowerCase())) {
                        return trimmedText;
                    }
                }
                  Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } catch (Exception e) {
                logger.warn("Error while waiting for any text: {}", e.getMessage());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        
        return null;    }
      private boolean waitForManagedApplicationActive(String managedApplicationName, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedApplicationName);
                if (context != null) {
                    // Try to focus the application and check if it becomes active
                    boolean focused = windowController.focusWindow(context);
                    if (focused) {
                        return true;
                    }
                }
                
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                logger.warn("Error checking managed application active: {}", e.getMessage());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }}
        
        return false;
    }
      private boolean waitForManagedApplicationToDisappear(String managedApplicationName, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedApplicationName);
                if (context == null) {
                    // Application process is no longer running - it disappeared!
                    return true;
                }
                
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                // If ProcessManager throws exception, the application might not be managed or disappeared
                logger.debug("ProcessManager exception while checking disappearance: {}", e.getMessage());
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
    
    private boolean waitForProcessStart(String processName, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                boolean isRunning = windowController.isProcessRunning(processName);
                if (isRunning) {
                    return true;
                }
                  Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                logger.warn("Error checking process start: {}", e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        return false;
    }
    
    private boolean waitForProcessStop(String processName, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                boolean isRunning = windowController.isProcessRunning(processName);
                if (!isRunning) {
                    return true;
                }
                  Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                logger.warn("Error checking process stop: {}", e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }        }
        
        return false;
    }
    
    // =====================================================================================
    // ADDITIONAL WAIT STEPS FOR APPLICATIONS
    // =====================================================================================
    
    // =====================================================================================
    // MISSING WAIT STEP DEFINITIONS 
    // =====================================================================================
      @When("I wait for managed application {string} to disappear")
    public void i_wait_for_managed_application_to_disappear(String managedApplicationName) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("Waiting for managed application '{}' to disappear", interpolatedAppName);
        
        boolean applicationDisappeared = waitForManagedApplicationToDisappear(interpolatedAppName, 30);
        addVerification("Wait for Managed Application to Disappear", applicationDisappeared,
            String.format("Managed application '%s' %s", interpolatedAppName, applicationDisappeared ? "disappeared" : "did not disappear"));
        
        if (!applicationDisappeared) {
            captureScreenshot("wait_managed_app_disappear_failed");
            throw new RuntimeException("Managed application did not disappear: " + interpolatedAppName);
        }
        
        logger.info("Managed application '{}' successfully disappeared", interpolatedAppName);
    }
    
    @When("I wait for text {string} to appear with timeout {int} second")
    public void i_wait_for_text_to_appear_with_timeout_singular(String text, int timeoutSeconds) {
        i_wait_for_text_to_appear_with_timeout(text, timeoutSeconds);
    }
    
    // =====================================================================================
    // MANAGED APPLICATION CONTEXT WAIT OPERATIONS
    // =====================================================================================
    
    @When("I wait for text {string} to appear in managed application {string}")
    public void i_wait_for_text_to_appear_in_managed_application(String text, String managedAppName) {
        i_wait_for_text_to_appear_in_managed_application_with_timeout(text, managedAppName, 30);
    }
    
    @When("I wait for text {string} to appear in managed application {string} with timeout {int} seconds")
    public void i_wait_for_text_to_appear_in_managed_application_with_timeout(String text, String managedAppName, int timeoutSeconds) {
        String interpolatedText = VariableManager.interpolate(text);
        String interpolatedAppName = VariableManager.interpolate(managedAppName);
          logger.info("🎯 Enterprise Wait: Text '{}' in application '{}' with timeout {}s", interpolatedText, interpolatedAppName, timeoutSeconds);
          try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                throw new RuntimeException("Managed application not found: " + interpolatedAppName);
            }
              // Check if this is a console application (like CMD) with no windows
            boolean isConsoleApp = context.getAllWindows().isEmpty() || 
                                 (context.getExecutablePath() != null && 
                                  context.getExecutablePath().toLowerCase().endsWith("cmd.exe"));
            
            logger.info("🔍 CONSOLE DETECTION: App='{}', Windows={}, ExePath='{}', IsConsole={}", 
                interpolatedAppName, context.getAllWindows().size(), 
                context.getExecutablePath(), isConsoleApp);            Rectangle searchBounds;
            
            if (isConsoleApp) {
                logger.info("🖥️ CONSOLE APPLICATION DETECTED: Using precise window bounds for '{}'", interpolatedAppName);
                // Focus the console window and get its precise bounds
                boolean focused = windowController.focusWindow(context);
                if (!focused) {
                    throw new RuntimeException("Failed to focus console application: " + interpolatedAppName);
                }
                searchBounds = windowController.getWindowBounds(context);
                logger.info("🎯 PRECISE CONSOLE BOUNDS: Using {}x{} at ({},{}) for OCR text search", 
                    searchBounds.width, searchBounds.height, searchBounds.x, searchBounds.y);
            } else {
                // For GUI applications, try to focus and get window bounds
                logger.info("🪟 GUI APPLICATION: Attempting to focus window for '{}'", interpolatedAppName);
                boolean focused = windowController.focusWindow(context);
                if (!focused) {
                    throw new RuntimeException("Failed to focus managed application: " + interpolatedAppName);
                }
                searchBounds = windowController.getWindowBounds(context);
            }            // Check if OCR maximization is enabled in configuration
            boolean ocrMaximizationEnabled = Boolean.parseBoolean(
                System.getProperty("ocr.maximize.windows", "true"));
            
            com.automation.core.WindowController.WindowStateInfo originalState = null;
            if (ocrMaximizationEnabled) {
                // Temporarily maximize window for better OCR accuracy
                logger.debug("🔍 Temporarily maximizing window for OCR text search (enabled by config)");
                originalState = windowController.temporarilyMaximizeForOCR(context);
            } else {
                logger.debug("🔍 OCR maximization disabled by configuration, using current window state");
            }
            
            boolean textAppeared = false;
            try {                // For maximized windows, capture full screen for better OCR reliability
                if (originalState != null) {
                    logger.debug("🔍 Using full screen capture for maximized window OCR");
                    textAppeared = WaitUtilsStatic.waitForTextOnScreen(interpolatedText, timeoutSeconds);
                } else {
                    // Use region capture if maximization disabled or failed
                    logger.debug("🔍 Using region capture for OCR");
                    textAppeared = WaitUtilsStatic.waitForText(interpolatedText, searchBounds, timeoutSeconds);
                }
                
                addVerification("Wait for Text in Managed Application", textAppeared,
                    String.format("%s Text '%s' %s in application '%s' within %d seconds", 
                        textAppeared ? "✅" : "❌", interpolatedText, textAppeared ? "appeared" : "did not appear", interpolatedAppName, timeoutSeconds));
                
            } finally {
                // Always restore window state after OCR
                if (originalState != null) {
                    logger.debug("🔍 Restoring window state after OCR operation");
                    windowController.restoreWindowAfterOCR(context, originalState);
                }
            }
            
            if (!textAppeared) {
                captureScreenshot("wait_text_managed_app_failed");
                throw new RuntimeException("Text did not appear in managed application: " + interpolatedText);
            }
            
            logger.info("🚀 Enterprise Success: Text '{}' found in application '{}' within {} seconds", interpolatedText, interpolatedAppName, timeoutSeconds);
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to wait for text '%s' in managed application '%s': %s", 
                interpolatedText, interpolatedAppName, e.getMessage());
            addVerification("Wait for Text in Managed Application", false, errorMsg);
            logger.error("FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    @When("I wait for text {string} to appear in managed application {string} window {int}")
    public void i_wait_for_text_to_appear_in_managed_application_window(String text, String managedAppName, int windowIndex) {
        i_wait_for_text_to_appear_in_managed_application_window_with_timeout(text, managedAppName, windowIndex, 30);
    }
    
    @When("I wait for text {string} to appear in managed application {string} window {int} with timeout {int} seconds")
    public void i_wait_for_text_to_appear_in_managed_application_window_with_timeout(String text, String managedAppName, int windowIndex, int timeoutSeconds) {
        String interpolatedText = VariableManager.interpolate(text);
        String interpolatedAppName = VariableManager.interpolate(managedAppName);
        
        logger.info("MANAGED WAIT: Text '{}' in application '{}' window {} with timeout {}s", 
            interpolatedText, interpolatedAppName, windowIndex, timeoutSeconds);
          try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                throw new RuntimeException("Managed application not found: " + interpolatedAppName);
            }
            
            boolean focused = windowController.focusWindowByIndex(context, windowIndex);
            if (!focused) {
                throw new RuntimeException("Failed to focus managed application window: " + interpolatedAppName + " window " + windowIndex);            }
            
            Rectangle windowBounds = windowController.getWindowBounds(context);
            boolean textAppeared = WaitUtilsStatic.waitForText(interpolatedText, windowBounds, timeoutSeconds);
            
            addVerification("Wait for Text in Managed Application Window", textAppeared,
                String.format("Text '%s' %s in application '%s' window %d within %d seconds", 
                    interpolatedText, textAppeared ? "appeared" : "did not appear", interpolatedAppName, windowIndex, timeoutSeconds));
            
            if (!textAppeared) {
                captureScreenshot("wait_text_managed_app_window_failed");
                throw new RuntimeException("Text did not appear in managed application window: " + interpolatedText);
            }
            
            logger.info("SUCCESS: Text '{}' found in application '{}' window {} within {} seconds", 
                interpolatedText, interpolatedAppName, windowIndex, timeoutSeconds);
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to wait for text '%s' in managed application '%s' window %d: %s", 
                interpolatedText, interpolatedAppName, windowIndex, e.getMessage());
            addVerification("Wait for Text in Managed Application Window", false, errorMsg);
            logger.error("FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
}
