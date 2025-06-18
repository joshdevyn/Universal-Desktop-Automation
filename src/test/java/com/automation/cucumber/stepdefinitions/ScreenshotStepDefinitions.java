package com.automation.cucumber.stepdefinitions;

import io.cucumber.java.en.*;
import com.automation.utils.VariableManager;
import com.automation.core.ProcessManager;
import com.automation.models.ManagedApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Step definitions for screenshot and screen capture operations
 * Uses core API methods that actually exist in the framework
 */
public class ScreenshotStepDefinitions extends CommonStepDefinitionsBase {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotStepDefinitions.class);
    @When("I take a screenshot")
    public void i_take_a_screenshot() {
        logger.info("Taking a manual screenshot");
        
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            String savedPath = screenCapture.saveScreenshot(screenshot, "manual_screenshot_" + System.currentTimeMillis() + ".png");
            
            logger.debug("Screenshot captured and saved to: {}", savedPath);
            addVerification("Take Screenshot", true, 
                String.format("Screenshot captured and saved: %s", savedPath));
            logger.info("Successfully captured manual screenshot: {}", savedPath);
            
        } catch (Exception e) {
            logger.error("Failed to take screenshot: {}", e.getMessage(), e);
            addVerification("Take Screenshot", false, 
                "Failed to take screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to take screenshot", e);
        }
    }
    @When("I take a screenshot with name {string}")
    public void i_take_a_screenshot_with_name(String screenshotName) {
        String interpolatedName = VariableManager.interpolate(screenshotName);
        logger.info("Taking a named screenshot: '{}'", interpolatedName);
        
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            String savedPath = screenCapture.saveScreenshotWithTimestamp(screenshot, interpolatedName);
            
            logger.debug("Named screenshot '{}' captured and saved to: {}", interpolatedName, savedPath);
            addVerification("Take Named Screenshot", true, 
                String.format("Screenshot '%s' captured and saved: %s", interpolatedName, savedPath));
            logger.info("Successfully captured named screenshot '{}': {}", interpolatedName, savedPath);
            
        } catch (Exception e) {
            logger.error("Failed to take named screenshot '{}': {}", interpolatedName, e.getMessage(), e);
            addVerification("Take Named Screenshot", false, 
                "Failed to take named screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to take named screenshot: " + interpolatedName, e);
        }
    }

    @When("I take a screenshot of region {int},{int},{int},{int}")
    public void i_take_a_screenshot_of_region(int x, int y, int width, int height) {
        logger.info("Taking screenshot of region: x={}, y={}, width={}, height={}", x, y, width, height);
        
        try {
            BufferedImage regionScreenshot = screenCapture.captureRegion(x, y, width, height);
            String savedPath = screenCapture.saveScreenshotWithTimestamp(regionScreenshot, 
                String.format("region_%d_%d_%d_%d", x, y, width, height));
            
            logger.debug("Region screenshot captured and saved to: {}", savedPath);
            addVerification("Take Region Screenshot", true, 
                String.format("Region (%d,%d,%d,%d) screenshot captured and saved: %s", 
                    x, y, width, height, savedPath));
            logger.info("Successfully captured region screenshot: {}", savedPath);
            
        } catch (Exception e) {
            logger.error("Failed to take region screenshot ({},{},{},{}): {}", x, y, width, height, e.getMessage(), e);
            addVerification("Take Region Screenshot", false, 
                "Failed to take region screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to take region screenshot", e);
        }
    }

    @When("I take a screenshot of coordinates {int},{int} to {int},{int}")
    public void i_take_a_screenshot_of_coordinates(int x1, int y1, int x2, int y2) {
        logger.info("Taking screenshot of coordinates from ({},{}) to ({},{})", x1, y1, x2, y2);
        
        try {
            int width = x2 - x1;
            int height = y2 - y1;
            logger.debug("Calculated region dimensions: width={}, height={}", width, height);
            
            BufferedImage customScreenshot = screenCapture.captureRegion(x1, y1, width, height);
            String savedPath = screenCapture.saveScreenshotWithTimestamp(customScreenshot, 
                String.format("custom_region_%d_%d_%d_%d", x1, y1, x2, y2));
            
            logger.debug("Custom region screenshot captured and saved to: {}", savedPath);
            addVerification("Take Custom Region Screenshot", true, 
                String.format("Custom region (%d,%d) to (%d,%d) screenshot captured and saved: %s", 
                    x1, y1, x2, y2, savedPath));
            logger.info("Successfully captured custom region screenshot: {}", savedPath);
            
        } catch (Exception e) {            logger.error("Failed to take custom region screenshot ({},{}) to ({},{}): {}", 
                x1, y1, x2, y2, e.getMessage(), e);
            addVerification("Take Custom Region Screenshot", false, 
                "Failed to take custom region screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to take custom region screenshot", e);
        }
    }

    @When("I capture evidence with description {string}")
    public void i_capture_evidence_with_description(String description) {
        String interpolatedDescription = VariableManager.interpolate(description);
        logger.info("Capturing evidence with description: '{}'", interpolatedDescription);
        
        try {
            String savedPath = screenCapture.captureEvidence(interpolatedDescription);
            
            logger.debug("Evidence screenshot captured and saved to: {}", savedPath);
            addVerification("Capture Evidence", true, 
                String.format("Evidence captured with description: '%s'. Saved: %s", 
                    interpolatedDescription, savedPath));
            logger.info("Successfully captured evidence '{}': {}", interpolatedDescription, savedPath);
            
        } catch (Exception e) {
            logger.error("Failed to capture evidence '{}': {}", interpolatedDescription, e.getMessage(), e);
            addVerification("Capture Evidence", false, 
                "Failed to capture evidence: " + e.getMessage());
            throw new RuntimeException("Failed to capture evidence: " + interpolatedDescription, e);
        }
    }

    @When("I store screenshot path in variable {string}")
    public void i_store_screenshot_path_in_variable(String variableName) {
        logger.info("Taking screenshot and storing path in variable: '{}'", variableName);
        
        try {
            String savedPath = screenCapture.captureAndSaveWithTimestamp("stored_screenshot");
            
            logger.debug("Screenshot captured and saved to: {}", savedPath);
            VariableManager.setSessionVariable(variableName, savedPath);
            addVerification("Store Screenshot Path", true, 
                String.format("Screenshot path stored in variable '%s': %s", variableName, savedPath));
            logger.info("Successfully stored screenshot path in variable '{}': {}", variableName, savedPath);
            
        } catch (Exception e) {
            logger.error("Failed to store screenshot path in variable '{}': {}", variableName, e.getMessage(), e);
            addVerification("Store Screenshot Path", false, 
                "Failed to store screenshot path: " + e.getMessage());
            throw new RuntimeException("Failed to store screenshot path", e);
        }
    }

    @When("I compare current screen with screenshot {string}")
    public void i_compare_current_screen_with_screenshot(String baselineScreenshotPath) {
        String interpolatedPath = VariableManager.interpolate(baselineScreenshotPath);
        logger.info("Comparing current screen with baseline screenshot: '{}'", interpolatedPath);
        
        try {
            BufferedImage currentScreenshot = screenCapture.captureFullScreen();
            BufferedImage baselineScreenshot = screenCapture.loadImage(interpolatedPath);
            
            if (baselineScreenshot == null) {
                logger.error("Baseline screenshot could not be loaded from: {}", interpolatedPath);
                throw new RuntimeException("Baseline screenshot could not be loaded: " + interpolatedPath);
            }
            
            logger.debug("Both screenshots loaded successfully, calculating similarity");
            double similarity = screenCapture.calculateSimilarity(currentScreenshot, baselineScreenshot);
            boolean isMatch = similarity >= 0.9; // 90% similarity threshold
            
            logger.debug("Screen comparison result - Similarity: {:.2f}% (threshold: 90%)", similarity * 100);
            addVerification("Screen Comparison", isMatch, 
                String.format("Screen comparison with baseline - Similarity: %.2f%% (threshold: 90%%)", 
                    similarity * 100));
            
            if (!isMatch) {
                String diffPath = screenCapture.saveScreenshotWithTimestamp(currentScreenshot, "screen_diff");
                logger.warn("Screen comparison failed - Similarity: {:.2f}%. Current screen saved to: {}", 
                    similarity * 100, diffPath);
                throw new AssertionError(String.format("Screen comparison failed - Similarity: %.2f%%. Current screen saved: %s", 
                    similarity * 100, diffPath));
            }
            
            logger.info("Screen comparison successful - Similarity: {:.2f}%", similarity * 100);
        } catch (Exception e) {
            logger.error("Failed to compare screens with baseline '{}': {}", interpolatedPath, e.getMessage(), e);
            addVerification("Screen Comparison", false, 
                "Failed to compare screens: " + e.getMessage());
            throw new RuntimeException("Failed to compare screens", e);
        }
    }

    @When("I create baseline screenshot with name {string}")
    public void i_create_baseline_screenshot_with_name(String baselineName) {
        String interpolatedName = VariableManager.interpolate(baselineName);
        logger.info("Creating baseline screenshot with name: '{}'", interpolatedName);
        
        try {
            String baselinePath = screenCapture.createBaseline(interpolatedName);
            
            logger.debug("Baseline screenshot created and saved to: {}", baselinePath);
            addVerification("Create Baseline Screenshot", true, 
                String.format("Baseline screenshot '%s' created: %s", interpolatedName, baselinePath));
            logger.info("Successfully created baseline screenshot '{}': {}", interpolatedName, baselinePath);
            
        } catch (Exception e) {
            logger.error("Failed to create baseline screenshot '{}': {}", interpolatedName, e.getMessage(), e);
            addVerification("Create Baseline Screenshot", false, 
                "Failed to create baseline screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to create baseline screenshot: " + interpolatedName, e);
        }
    }

    @When("I capture before and after screenshots for action {string}")
    public void i_capture_before_and_after_screenshots_for_action(String actionDescription) {
        String interpolatedAction = VariableManager.interpolate(actionDescription);
        logger.info("Capturing before screenshot for action: '{}'", interpolatedAction);
        
        try {
            // Capture before screenshot
            String beforePath = screenCapture.captureAndSaveWithTimestamp("before_" + sanitizeFileName(interpolatedAction));
            
            logger.debug("Before screenshot captured and saved to: {}", beforePath);
            
            // Store action info for potential after screenshot
            VariableManager.setSessionVariable("last_action_description", interpolatedAction);
            VariableManager.setSessionVariable("last_before_screenshot", beforePath);
            
            addVerification("Capture Before Screenshot", true, 
                String.format("Before screenshot captured for action '%s': %s", interpolatedAction, beforePath));
            logger.info("Successfully captured before screenshot for action '{}': {}", interpolatedAction, beforePath);
            
        } catch (Exception e) {
            logger.error("Failed to capture before screenshot for action '{}': {}", interpolatedAction, e.getMessage(), e);
            addVerification("Capture Before Screenshot", false, 
                "Failed to capture before screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to capture before screenshot", e);
        }
    }

    @When("I capture after screenshot")
    public void i_capture_after_screenshot() {
        logger.info("Capturing after screenshot");
        
        try {
            String lastAction = VariableManager.getSessionVariable("last_action_description");
            if (lastAction == null) {
                lastAction = "unknown_action";
                logger.warn("No previous action description found, using default: {}", lastAction);
            } else {
                logger.debug("Capturing after screenshot for previous action: '{}'", lastAction);
            }
            
            String afterPath = screenCapture.captureAndSaveWithTimestamp("after_" + sanitizeFileName(lastAction));
            
            logger.debug("After screenshot captured and saved to: {}", afterPath);
            addVerification("Capture After Screenshot", true, 
                String.format("After screenshot captured for action '%s': %s", lastAction, afterPath));
            logger.info("Successfully captured after screenshot for action '{}': {}", lastAction, afterPath);
            
        } catch (Exception e) {
            logger.error("Failed to capture after screenshot: {}", e.getMessage(), e);
            addVerification("Capture After Screenshot", false, 
                "Failed to capture after screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to capture after screenshot", e);
        }
    }

    @When("I capture full desktop screenshot")
    public void i_capture_full_desktop_screenshot() {
        logger.info("Capturing full desktop screenshot");
        
        try {
            String savedPath = screenCapture.captureAndSaveFullDesktop();
            
            logger.debug("Full desktop screenshot captured and saved to: {}", savedPath);
            addVerification("Capture Full Desktop", true, 
                String.format("Full desktop screenshot captured: %s", savedPath));
            logger.info("Successfully captured full desktop screenshot: {}", savedPath);
            
        } catch (Exception e) {
            logger.error("Failed to capture full desktop screenshot: {}", e.getMessage(), e);
            addVerification("Capture Full Desktop", false, 
                "Failed to capture full desktop: " + e.getMessage());
            throw new RuntimeException("Failed to capture full desktop screenshot", e);
        }
    }

    // Helper method
    private String sanitizeFileName(String fileName) {
        // Remove or replace invalid file name characters
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_")
                      .replaceAll("_{2,}", "_")  // Replace multiple underscores with single
                      .toLowerCase();
    }
    
    // =====================================================================================
    // REVOLUTIONARY SCREENSHOT AUTOMATION - ADVANCED VISUAL DOCUMENTATION
    // =====================================================================================
    
    @When("I take timestamped screenshot series with prefix {string} and count {int}")
    public void i_take_timestamped_screenshot_series(String prefix, int count) {
        String interpolatedPrefix = VariableManager.interpolate(prefix);
        logger.info("Taking timestamped screenshot series with prefix '{}' and count {}", interpolatedPrefix, count);
        
        try {
            java.util.List<String> screenshotPaths = new java.util.ArrayList<>();
            
            for (int i = 1; i <= count; i++) {
                BufferedImage screenshot = screenCapture.captureFullScreen();
                String timestamp = String.valueOf(System.currentTimeMillis());
                String fileName = String.format("%s_series_%d_%s", interpolatedPrefix, i, timestamp);
                String savedPath = screenCapture.saveScreenshotWithTimestamp(screenshot, fileName);
                
                screenshotPaths.add(savedPath);
                logger.debug("Screenshot {} of {} captured: {}", i, count, savedPath);
                
                // Small delay between screenshots
                if (i < count) {
                    Thread.sleep(500);
                }
            }
            
            // Store all paths in variables
            for (int i = 0; i < screenshotPaths.size(); i++) {
                String variableName = interpolatedPrefix + "_screenshot_" + (i + 1);
                VariableManager.setSessionVariable(variableName, screenshotPaths.get(i));
            }
            
            VariableManager.setSessionVariable(interpolatedPrefix + "_screenshot_count", String.valueOf(count));
            
            addVerification("Screenshot Series", true, 
                String.format("Captured %d timestamped screenshots with prefix '%s'", count, interpolatedPrefix));
            logger.info("Successfully captured {} timestamped screenshots with prefix '{}'", count, interpolatedPrefix);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot series with prefix '{}': {}", interpolatedPrefix, e.getMessage(), e);
            addVerification("Screenshot Series", false, 
                "Failed to capture screenshot series: " + e.getMessage());
            throw new RuntimeException("Failed to capture screenshot series", e);
        }
    }
    
    @When("I take screenshot with custom metadata: {string}")
    public void i_take_screenshot_with_metadata(String metadata) {
        String interpolatedMetadata = VariableManager.interpolate(metadata);
        logger.info("Taking screenshot with custom metadata: '{}'", interpolatedMetadata);
        
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            
            // Create metadata-enriched filename
            String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String sanitizedMetadata = sanitizeFileName(interpolatedMetadata);
            String fileName = String.format("metadata_%s_%s", timestamp, sanitizedMetadata);
            
            String savedPath = screenCapture.saveScreenshot(screenshot, fileName + ".png");
            
            // Store metadata in a companion text file
            String metadataFilePath = savedPath.replace(".png", "_metadata.txt");
            StringBuilder metadataContent = new StringBuilder();
            metadataContent.append("Screenshot Metadata\n");
            metadataContent.append("===================\n");
            metadataContent.append("Timestamp: ").append(java.time.LocalDateTime.now()).append("\n");
            metadataContent.append("Custom Metadata: ").append(interpolatedMetadata).append("\n");
            metadataContent.append("Screenshot Path: ").append(savedPath).append("\n");
            metadataContent.append("Screen Resolution: ").append(screenshot.getWidth()).append("x").append(screenshot.getHeight()).append("\n");
            
            java.nio.file.Files.write(java.nio.file.Paths.get(metadataFilePath), 
                metadataContent.toString().getBytes());
            
            VariableManager.setSessionVariable("last_screenshot_path", savedPath);
            VariableManager.setSessionVariable("last_metadata_path", metadataFilePath);
            
            addVerification("Screenshot with Metadata", true, 
                String.format("Screenshot with metadata '%s' captured and saved: %s", interpolatedMetadata, savedPath));
            logger.info("Successfully captured screenshot with metadata '{}': {}", interpolatedMetadata, savedPath);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot with metadata '{}': {}", interpolatedMetadata, e.getMessage(), e);
            addVerification("Screenshot with Metadata", false, 
                "Failed to capture screenshot with metadata: " + e.getMessage());
            throw new RuntimeException("Failed to capture screenshot with metadata", e);
        }
    }
    
    @When("I take screenshot and annotate with text {string} at position {int},{int}")
    public void i_take_screenshot_and_annotate(String annotationText, int x, int y) {
        String interpolatedText = VariableManager.interpolate(annotationText);
        logger.info("Taking screenshot and annotating with text '{}' at position ({},{})", interpolatedText, x, y);
        
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            
            // Create annotated version
            BufferedImage annotatedScreenshot = new BufferedImage(
                screenshot.getWidth(), screenshot.getHeight(), BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = annotatedScreenshot.createGraphics();
            
            // Draw original screenshot
            g2d.drawImage(screenshot, 0, 0, null);
            
            // Configure annotation style
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(java.awt.Color.RED);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            
            // Add text annotation
            java.awt.FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(interpolatedText);
            int textHeight = fm.getHeight();
            
            // Draw background rectangle for better readability
            g2d.setColor(new java.awt.Color(255, 255, 255, 200)); // Semi-transparent white
            g2d.fillRect(x - 2, y - textHeight + 2, textWidth + 4, textHeight + 2);
            
            // Draw text
            g2d.setColor(java.awt.Color.RED);
            g2d.drawString(interpolatedText, x, y);
            
            g2d.dispose();
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = String.format("annotated_screenshot_%s", timestamp);
            String savedPath = screenCapture.saveScreenshot(annotatedScreenshot, fileName + ".png");
            
            VariableManager.setSessionVariable("last_annotated_screenshot", savedPath);
            
            addVerification("Annotated Screenshot", true, 
                String.format("Annotated screenshot with text '%s' at (%d,%d) saved: %s", 
                    interpolatedText, x, y, savedPath));
            logger.info("Successfully captured annotated screenshot with text '{}' at ({},{}) saved: {}", 
                interpolatedText, x, y, savedPath);
        } catch (Exception e) {
            logger.error("Failed to capture annotated screenshot: {}", e.getMessage(), e);
            addVerification("Annotated Screenshot", false, 
                "Failed to capture annotated screenshot: " + e.getMessage());
            throw new RuntimeException("Failed to capture annotated screenshot", e);
        }
    }
    
    @When("I take screenshot and draw rectangle at {int},{int} with size {int}x{int}")
    public void i_take_screenshot_and_draw_rectangle(int x, int y, int width, int height) {
        logger.info("Taking screenshot and drawing rectangle at ({},{}) with size {}x{}", x, y, width, height);
        
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            
            // Create annotated version with rectangle
            BufferedImage annotatedScreenshot = new BufferedImage(
                screenshot.getWidth(), screenshot.getHeight(), BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = annotatedScreenshot.createGraphics();
            
            // Draw original screenshot
            g2d.drawImage(screenshot, 0, 0, null);
            
            // Configure rectangle style
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(java.awt.Color.RED);
            g2d.setStroke(new java.awt.BasicStroke(3.0f));
            
            // Draw rectangle
            g2d.drawRect(x, y, width, height);
            
            // Add corner markers for better visibility
            int markerSize = 10;
            g2d.fillRect(x - markerSize/2, y - markerSize/2, markerSize, markerSize);
            g2d.fillRect(x + width - markerSize/2, y - markerSize/2, markerSize, markerSize);
            g2d.fillRect(x - markerSize/2, y + height - markerSize/2, markerSize, markerSize);
            g2d.fillRect(x + width - markerSize/2, y + height - markerSize/2, markerSize, markerSize);
            
            g2d.dispose();
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = String.format("rectangle_screenshot_%s", timestamp);
            String savedPath = screenCapture.saveScreenshot(annotatedScreenshot, fileName + ".png");
            
            VariableManager.setSessionVariable("last_rectangle_screenshot", savedPath);
            
            addVerification("Rectangle Screenshot", true, 
                String.format("Screenshot with rectangle at (%d,%d) size %dx%d saved: %s", 
                    x, y, width, height, savedPath));
            logger.info("Successfully captured screenshot with rectangle at ({},{}) size {}x{} saved: {}", 
                x, y, width, height, savedPath);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot with rectangle: {}", e.getMessage(), e);
            addVerification("Rectangle Screenshot", false, 
                "Failed to capture screenshot with rectangle: " + e.getMessage());
            throw new RuntimeException("Failed to capture screenshot with rectangle", e);
        }
    }
    
    @When("I take multi-monitor screenshot and store paths with prefix {string}")
    public void i_take_multi_monitor_screenshot(String prefix) {
        String interpolatedPrefix = VariableManager.interpolate(prefix);
        logger.info("Taking multi-monitor screenshot and storing paths with prefix '{}'", interpolatedPrefix);
        
        try {
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            java.awt.GraphicsDevice[] screens = ge.getScreenDevices();
            
            java.util.List<String> screenshotPaths = new java.util.ArrayList<>();
            
            for (int i = 0; i < screens.length; i++) {
                java.awt.GraphicsDevice screen = screens[i];
                java.awt.Rectangle bounds = screen.getDefaultConfiguration().getBounds();                BufferedImage monitorCapture = new java.awt.Robot().createScreenCapture(bounds);
                
                String timestamp = String.valueOf(System.currentTimeMillis());
                String fileName = String.format("%s_monitor_%d_%s", interpolatedPrefix, (i + 1), timestamp);
                String savedPath = screenCapture.saveScreenshot(monitorCapture, fileName + ".png");
                
                screenshotPaths.add(savedPath);
                
                // Store individual monitor info
                String monitorVar = interpolatedPrefix + "_monitor_" + (i + 1);
                VariableManager.setSessionVariable(monitorVar + "_path", savedPath);
                VariableManager.setSessionVariable(monitorVar + "_width", String.valueOf(bounds.width));
                VariableManager.setSessionVariable(monitorVar + "_height", String.valueOf(bounds.height));
                VariableManager.setSessionVariable(monitorVar + "_x", String.valueOf(bounds.x));
                VariableManager.setSessionVariable(monitorVar + "_y", String.valueOf(bounds.y));
                
                logger.debug("Monitor {} screenshot captured: {} ({}x{} at {},{}) saved: {}", 
                    (i + 1), bounds, bounds.width, bounds.height, bounds.x, bounds.y, savedPath);
            }
            
            VariableManager.setSessionVariable(interpolatedPrefix + "_monitor_count", String.valueOf(screens.length));
            
            addVerification("Multi-Monitor Screenshot", true, 
                String.format("Captured screenshots from %d monitors with prefix '%s'", screens.length, interpolatedPrefix));
            logger.info("Successfully captured screenshots from {} monitors with prefix '{}'", screens.length, interpolatedPrefix);
        } catch (Exception e) {
            logger.error("Failed to capture multi-monitor screenshots with prefix '{}': {}", interpolatedPrefix, e.getMessage(), e);
            addVerification("Multi-Monitor Screenshot", false, 
                "Failed to capture multi-monitor screenshots: " + e.getMessage());
            throw new RuntimeException("Failed to capture multi-monitor screenshots", e);
        }
    }
    
    @When("I create screenshot comparison report between {string} and {string}")
    public void i_create_screenshot_comparison_report(String screenshot1Path, String screenshot2Path) {
        String interpolatedPath1 = VariableManager.interpolate(screenshot1Path);
        String interpolatedPath2 = VariableManager.interpolate(screenshot2Path);
        logger.info("Creating screenshot comparison report between '{}' and '{}'", interpolatedPath1, interpolatedPath2);
        
        try {
            BufferedImage image1 = screenCapture.loadImage(interpolatedPath1);
            BufferedImage image2 = screenCapture.loadImage(interpolatedPath2);
            
            if (image1 == null || image2 == null) {
                throw new RuntimeException("One or both images could not be loaded");
            }
            
            // Calculate similarity
            double similarity = imageMatcher.calculateSimilarity(image1, image2);
            
            // Create comparison report
            StringBuilder report = new StringBuilder();
            report.append("Screenshot Comparison Report\n");
            report.append("============================\n");
            report.append("Timestamp: ").append(java.time.LocalDateTime.now()).append("\n");
            report.append("Image 1: ").append(interpolatedPath1).append("\n");
            report.append("Image 2: ").append(interpolatedPath2).append("\n");
            report.append("Image 1 Dimensions: ").append(image1.getWidth()).append("x").append(image1.getHeight()).append("\n");
            report.append("Image 2 Dimensions: ").append(image2.getWidth()).append("x").append(image2.getHeight()).append("\n");
            report.append("Similarity Score: ").append(String.format("%.4f", similarity)).append(" (").append(String.format("%.2f%%", similarity * 100)).append(")\n");
            
            if (similarity >= 0.95) {
                report.append("Result: MATCH (Very High Similarity)\n");
            } else if (similarity >= 0.85) {
                report.append("Result: LIKELY MATCH (High Similarity)\n");
            } else if (similarity >= 0.70) {
                report.append("Result: PARTIAL MATCH (Moderate Similarity)\n");
            } else {
                report.append("Result: NO MATCH (Low Similarity)\n");
            }
            
            // Save report
            String timestamp = String.valueOf(System.currentTimeMillis());
            String reportFileName = String.format("comparison_report_%s.txt", timestamp);
            java.nio.file.Path reportPath = java.nio.file.Paths.get("target", "reports", reportFileName);
            java.nio.file.Files.createDirectories(reportPath.getParent());
            java.nio.file.Files.write(reportPath, report.toString().getBytes());
            
            // Store results in variables
            VariableManager.setSessionVariable("last_comparison_similarity", String.valueOf(similarity));
            VariableManager.setSessionVariable("last_comparison_report", reportPath.toString());
            
            addVerification("Screenshot Comparison Report", true, 
                String.format("Comparison report created with similarity %.2f%% saved: %s", 
                    similarity * 100, reportPath.toString()));
            logger.info("Successfully created comparison report with similarity {:.2f}% saved: {}", 
                similarity * 100, reportPath.toString());
        } catch (Exception e) {
            logger.error("Failed to create comparison report between '{}' and '{}': {}", 
                interpolatedPath1, interpolatedPath2, e.getMessage(), e);
            addVerification("Screenshot Comparison Report", false, 
                "Failed to create comparison report: " + e.getMessage());
            throw new RuntimeException("Failed to create comparison report", e);
        }
    }

    @When("I take screenshot and blur sensitive region {int},{int},{int},{int}")
    public void i_take_screenshot_and_blur_region(int x, int y, int width, int height) {
        logger.info("Taking screenshot and blurring sensitive region at ({},{}) with size {}x{}", x, y, width, height);
        
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            
            // Create a copy for blurring
            BufferedImage blurredScreenshot = new BufferedImage(
                screenshot.getWidth(), screenshot.getHeight(), BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = blurredScreenshot.createGraphics();
            g2d.drawImage(screenshot, 0, 0, null);
            
            // Extract the region to blur
            BufferedImage regionToBlur = screenshot.getSubimage(x, y, width, height);
            
            // Apply blur effect (simple box blur implementation)
            BufferedImage blurredRegion = applyBoxBlur(regionToBlur, 15);
            
            // Draw blurred region back onto the screenshot
            g2d.drawImage(blurredRegion, x, y, null);
            g2d.dispose();
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = String.format("blurred_screenshot_%s", timestamp);
            String savedPath = screenCapture.saveScreenshot(blurredScreenshot, fileName + ".png");
            
            VariableManager.setSessionVariable("last_blurred_screenshot", savedPath);
            
            addVerification("Blurred Screenshot", true, 
                String.format("Screenshot with blurred region at (%d,%d) size %dx%d saved: %s", 
                    x, y, width, height, savedPath));
            logger.info("Successfully captured screenshot with blurred region at ({},{}) size {}x{} saved: {}", 
                x, y, width, height, savedPath);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot with blurred region: {}", e.getMessage(), e);
            addVerification("Blurred Screenshot", false, 
                "Failed to capture screenshot with blurred region: " + e.getMessage());
            throw new RuntimeException("Failed to capture screenshot with blurred region", e);
        }
    }
    
    @When("I create screenshot contact sheet from variable prefix {string}")
    public void i_create_screenshot_contact_sheet(String prefix) {
        String interpolatedPrefix = VariableManager.interpolate(prefix);
        logger.info("Creating screenshot contact sheet from variable prefix '{}'", interpolatedPrefix);
        
        try {
            String countVar = interpolatedPrefix + "_screenshot_count";
            String countStr = VariableManager.getSessionVariable(countVar);
            
            if (countStr == null) {
                countVar = interpolatedPrefix + "_count";
                countStr = VariableManager.getSessionVariable(countVar);
            }
            
            if (countStr == null) {
                throw new RuntimeException("No count variable found for prefix: " + interpolatedPrefix);
            }
            
            int count = Integer.parseInt(countStr);
            java.util.List<BufferedImage> images = new java.util.ArrayList<>();
            
            // Load all images
            for (int i = 1; i <= count; i++) {
                String pathVar = interpolatedPrefix + "_screenshot_" + i;
                String imagePath = VariableManager.getSessionVariable(pathVar);
                if (imagePath != null) {
                    BufferedImage image = screenCapture.loadImage(imagePath);
                    if (image != null) {
                        images.add(image);
                    }
                }
            }
            
            if (images.isEmpty()) {
                throw new RuntimeException("No valid images found for contact sheet");
            }
            
            // Calculate grid dimensions
            int cols = (int) Math.ceil(Math.sqrt(images.size()));
            int rows = (int) Math.ceil((double) images.size() / cols);
            
            // Find maximum dimensions for scaling
            int maxWidth = images.stream().mapToInt(BufferedImage::getWidth).max().orElse(800);
            int maxHeight = images.stream().mapToInt(BufferedImage::getHeight).max().orElse(600);
            
            // Scale down for contact sheet
            int thumbWidth = Math.min(200, maxWidth / 2);
            int thumbHeight = Math.min(150, maxHeight / 2);
            
            // Create contact sheet
            int sheetWidth = cols * thumbWidth + (cols - 1) * 10 + 20; // 10px spacing, 20px margin
            int sheetHeight = rows * thumbHeight + (rows - 1) * 10 + 20;
            
            BufferedImage contactSheet = new BufferedImage(sheetWidth, sheetHeight, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = contactSheet.createGraphics();
            
            // Fill background
            g2d.setColor(java.awt.Color.WHITE);
            g2d.fillRect(0, 0, sheetWidth, sheetHeight);
            
            // Draw images
            for (int i = 0; i < images.size(); i++) {
                int row = i / cols;
                int col = i % cols;
                int x = 10 + col * (thumbWidth + 10);
                int y = 10 + row * (thumbHeight + 10);
                
                BufferedImage scaledImage = scaleImage(images.get(i), thumbWidth, thumbHeight);
                g2d.drawImage(scaledImage, x, y, null);
                
                // Add image number
                g2d.setColor(java.awt.Color.BLACK);
                g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
                g2d.drawString(String.valueOf(i + 1), x + 5, y + 15);
            }
            
            g2d.dispose();
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = String.format("contact_sheet_%s_%s", interpolatedPrefix, timestamp);
            String savedPath = screenCapture.saveScreenshot(contactSheet, fileName + ".png");
            
            VariableManager.setSessionVariable("last_contact_sheet", savedPath);
            
            addVerification("Screenshot Contact Sheet", true, 
                String.format("Contact sheet created from %d images with prefix '%s' saved: %s", 
                    images.size(), interpolatedPrefix, savedPath));
            logger.info("Successfully created contact sheet from {} images with prefix '{}' saved: {}", 
                images.size(), interpolatedPrefix, savedPath);
        } catch (Exception e) {
            logger.error("Failed to create contact sheet with prefix '{}': {}", interpolatedPrefix, e.getMessage(), e);
            addVerification("Screenshot Contact Sheet", false, 
                "Failed to create contact sheet: " + e.getMessage());
            throw new RuntimeException("Failed to create contact sheet", e);
        }
    }
    
    // =====================================================================================
    // ADVANCED HELPER METHODS FOR IMAGE PROCESSING AND MANIPULATION
    // =====================================================================================
    
    private BufferedImage applyBoxBlur(BufferedImage source, int blurRadius) {
        if (blurRadius <= 0) return source;
        
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage blurred = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Simple box blur implementation
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = 0, green = 0, blue = 0, count = 0;
                
                // Average pixels in blur radius
                for (int dy = -blurRadius; dy <= blurRadius; dy++) {
                    for (int dx = -blurRadius; dx <= blurRadius; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int rgb = source.getRGB(nx, ny);
                            red += (rgb >> 16) & 0xFF;
                            green += (rgb >> 8) & 0xFF;
                            blue += rgb & 0xFF;
                            count++;
                        }
                    }
                }
                
                if (count > 0) {
                    red /= count;
                    green /= count;
                    blue /= count;
                    
                    int blurredRgb = (red << 16) | (green << 8) | blue;
                    blurred.setRGB(x, y, blurredRgb);
                }
            }
        }
        
        return blurred;
    }
    
    private BufferedImage scaleImage(BufferedImage source, int targetWidth, int targetHeight) {
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = scaled.createGraphics();
        
        // Use high-quality scaling
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, 
            java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        
        return scaled;
    }
    
    @When("I create animated GIF from screenshot series with prefix {string}")
    public void i_create_animated_gif_from_series(String prefix) {
        String interpolatedPrefix = VariableManager.interpolate(prefix);
        logger.info("Creating animated GIF from screenshot series with prefix '{}'", interpolatedPrefix);
        
        try {
            String countVar = interpolatedPrefix + "_screenshot_count";
            String countStr = VariableManager.getSessionVariable(countVar);
            
            if (countStr == null) {
                throw new RuntimeException("No count variable found for prefix: " + interpolatedPrefix);
            }
            
            int count = Integer.parseInt(countStr);
            java.util.List<BufferedImage> frames = new java.util.ArrayList<>();
            
            // Load all images as frames
            for (int i = 1; i <= count; i++) {
                String pathVar = interpolatedPrefix + "_screenshot_" + i;
                String imagePath = VariableManager.getSessionVariable(pathVar);
                if (imagePath != null) {
                    BufferedImage image = screenCapture.loadImage(imagePath);
                    if (image != null) {
                        frames.add(image);
                    }
                }
            }
            
            if (frames.isEmpty()) {
                throw new RuntimeException("No valid frames found for animated GIF");
            }
            
            // For this implementation, we'll create a simple slideshow-style image
            // In a full implementation, would use a GIF library like ImageIO or AnimatedGifLib
            BufferedImage combinedImage = createSlideshowImage(frames);
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = String.format("animated_series_%s_%s", interpolatedPrefix, timestamp);
            String savedPath = screenCapture.saveScreenshot(combinedImage, fileName + ".png");
            
            VariableManager.setSessionVariable("last_animated_gif", savedPath);
            
            addVerification("Animated GIF Creation", true, 
                String.format("Animated slideshow created from %d frames with prefix '%s' saved: %s", 
                    frames.size(), interpolatedPrefix, savedPath));
            logger.info("Successfully created animated slideshow from {} frames with prefix '{}' saved: {}", 
                frames.size(), interpolatedPrefix, savedPath);
        } catch (Exception e) {
            logger.error("Failed to create animated GIF with prefix '{}': {}", interpolatedPrefix, e.getMessage(), e);
            addVerification("Animated GIF Creation", false, 
                "Failed to create animated GIF: " + e.getMessage());
            throw new RuntimeException("Failed to create animated GIF", e);
        }
    }
    
    private BufferedImage createSlideshowImage(java.util.List<BufferedImage> frames) {
        if (frames.isEmpty()) return null;
        
        // Find max dimensions
        int maxWidth = frames.stream().mapToInt(BufferedImage::getWidth).max().orElse(800);
        int maxHeight = frames.stream().mapToInt(BufferedImage::getHeight).max().orElse(600);
        
        // Create slideshow layout (2x2 grid or similar)
        int cols = Math.min(4, (int) Math.ceil(Math.sqrt(frames.size())));
        int rows = (int) Math.ceil((double) frames.size() / cols);
        
        int frameWidth = maxWidth / cols;
        int frameHeight = maxHeight / rows;
        
        BufferedImage slideshow = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = slideshow.createGraphics();
        
        // Fill background
        g2d.setColor(java.awt.Color.BLACK);
        g2d.fillRect(0, 0, maxWidth, maxHeight);
        
        // Draw frames
        for (int i = 0; i < frames.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int x = col * frameWidth;
            int y = row * frameHeight;
            
            BufferedImage scaledFrame = scaleImage(frames.get(i), frameWidth, frameHeight);
            g2d.drawImage(scaledFrame, x, y, null);
            
            // Add frame number
            g2d.setColor(java.awt.Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            g2d.drawString("Frame " + (i + 1), x + 10, y + 25);
        }
        
        g2d.dispose();
        return slideshow;
    }
    
    @When("I take screenshot and extract text regions to variables with prefix {string}")
    public void i_take_screenshot_and_extract_text_regions(String prefix) {
        String interpolatedPrefix = VariableManager.interpolate(prefix);
        logger.info("Taking screenshot and extracting text regions to variables with prefix '{}'", interpolatedPrefix);
        
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String screenshotName = String.format("text_extraction_%s_%s", interpolatedPrefix, timestamp);
            String savedPath = screenCapture.saveScreenshot(screenshot, screenshotName + ".png");
            
            // Extract text from full screenshot
            File screenshotFile = new File(savedPath);
            String fullText = ocrEngine.extractText(screenshotFile);
            
            // Store full text
            VariableManager.setSessionVariable(interpolatedPrefix + "_full_text", fullText);
            VariableManager.setSessionVariable(interpolatedPrefix + "_screenshot_path", savedPath);
            
            // Split text into lines and store each line
            String[] lines = fullText.split("\\r?\\n");
            java.util.List<String> nonEmptyLines = new java.util.ArrayList<>();
            
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    nonEmptyLines.add(trimmedLine);
                }
            }
            
            // Store each line as a separate variable
            for (int i = 0; i < nonEmptyLines.size(); i++) {
                String lineVar = interpolatedPrefix + "_text_line_" + (i + 1);
                VariableManager.setSessionVariable(lineVar, nonEmptyLines.get(i));
            }
            
            VariableManager.setSessionVariable(interpolatedPrefix + "_text_line_count", String.valueOf(nonEmptyLines.size()));
              addVerification("Text Region Extraction", true, 
                String.format("Extracted %d text lines from screenshot with prefix '%s'", 
                    nonEmptyLines.size(), interpolatedPrefix));
            logger.info("Successfully extracted {} text lines from screenshot with prefix '{}'", 
                nonEmptyLines.size(), interpolatedPrefix);
        } catch (Exception e) {
            logger.error("Failed to extract text regions with prefix '{}': {}", interpolatedPrefix, e.getMessage(), e);
            addVerification("Text Region Extraction", false, 
                "Failed to extract text regions: " + e.getMessage());
            throw new RuntimeException("Failed to extract text regions", e);
        }
    }

    // =====================================================================================
    // MISSING SCREENSHOT STEP DEFINITIONS
    // =====================================================================================
    
    @When("I capture screenshot {string}")
    public void i_capture_screenshot(String screenshotName) {
        i_take_a_screenshot_with_name(screenshotName); // Delegate to existing method
    }
    
    @When("I capture screenshot with name {string}")
    public void i_capture_screenshot_with_name(String screenshotName) {
        i_take_a_screenshot_with_name(screenshotName); // Delegate to existing method
    }
      @Then("I capture screenshot with description {string}")
    public void i_capture_screenshot_with_description(String description) {
        try {
            String interpolatedDesc = VariableManager.interpolate(description);
            String fileName = "screenshot_" + System.currentTimeMillis();
            captureScreenshot(fileName);
            
            addVerification("Capture Screenshot with Description", true, 
                String.format("Captured screenshot with description: %s", interpolatedDesc));
            logger.info("Captured screenshot with description: {}", interpolatedDesc);
        } catch (Exception e) {
            addVerification("Capture Screenshot with Description", false, 
                String.format("Failed to capture screenshot with description: %s", e.getMessage()));
            logger.error("Failed to capture screenshot with description: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to capture screenshot with description", e);
        }
    }

    // =====================================================================================
    // MANAGED APPLICATION CONTEXT - SCREENSHOT OPERATIONS
    // =====================================================================================

    @When("I take a screenshot of managed application {string}")
    public void i_take_a_screenshot_of_managed_application(String applicationName) {        String interpolatedAppName = VariableManager.interpolate(applicationName);
        logger.info("🖼️ ENTERPRISE SCREENSHOT: Taking screenshot of managed application '{}'", interpolatedAppName);
        
        try {
            ManagedApplicationContext applicationContext = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (applicationContext == null) {
                throw new RuntimeException("Managed application not found: " + interpolatedAppName);
            }
            
            boolean focused = windowController.focusWindow(applicationContext);
            if (!focused) {
                throw new RuntimeException("Failed to focus managed application: " + interpolatedAppName);
            }
            
            Rectangle windowBounds = windowController.getWindowBounds(applicationContext);
            BufferedImage windowScreenshot = screenCapture.captureRegion(            windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height);
            String savedPath = screenCapture.saveScreenshotWithTimestamp(windowScreenshot, 
                "managed_app_" + interpolatedAppName);
            
            addVerification("Take Managed Application Screenshot", true,
                String.format("Screenshot of application '%s' captured and saved: %s", 
                    interpolatedAppName, savedPath));
            logger.info("✅ ENTERPRISE SCREENSHOT SUCCESS: Screenshot of managed application '{}' captured: {}", 
                interpolatedAppName, savedPath);
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to take screenshot of managed application '%s': %s", 
                interpolatedAppName, e.getMessage());
            addVerification("Take Managed Application Screenshot", false, errorMsg);
            logger.error("FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }    @When("I take a screenshot of managed application {string} window {int}")
    public void i_take_a_screenshot_of_managed_application_window(String applicationName, int windowIndex) {
        String interpolatedAppName = VariableManager.interpolate(applicationName);
        logger.info("🖼️ ENTERPRISE WINDOW SCREENSHOT: Taking screenshot of managed application '{}' window {}", interpolatedAppName, windowIndex);
        
        try {
            ManagedApplicationContext applicationContext = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (applicationContext == null) {
                throw new RuntimeException("Managed application not found: " + interpolatedAppName);
            }
            
            boolean focused = windowController.focusWindowByIndex(applicationContext, windowIndex);
            if (!focused) {
                throw new RuntimeException("Failed to focus managed application window: " + interpolatedAppName + " window " + windowIndex);
            }
            
            Rectangle windowBounds = windowController.getWindowBounds(applicationContext);
            BufferedImage windowScreenshot = screenCapture.captureRegion(
                windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height);
            String savedPath = screenCapture.saveScreenshotWithTimestamp(windowScreenshot, 
                String.format("managed_app_%s_window_%d", interpolatedAppName, windowIndex));
              addVerification("Take Managed Application Window Screenshot", true,
                String.format("Screenshot of application '%s' window %d captured and saved: %s",
                    interpolatedAppName, windowIndex, savedPath));
            logger.info("✅ ENTERPRISE WINDOW SCREENSHOT SUCCESS: Window {} of '{}' saved to '{}'", 
                windowIndex, interpolatedAppName, savedPath);
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to take screenshot of managed application '%s' window %d: %s", 
                interpolatedAppName, windowIndex, e.getMessage());
            addVerification("Take Managed Application Window Screenshot", false, errorMsg);
            logger.error("FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }    @When("I take a screenshot of managed application {string} with name {string}")
    public void i_take_a_screenshot_of_managed_application_with_name(String applicationName, String screenshotName) {
        String interpolatedAppName = VariableManager.interpolate(applicationName);
        String interpolatedName = VariableManager.interpolate(screenshotName);
        logger.info("🖼️ ENTERPRISE NAMED SCREENSHOT: Taking named screenshot of managed application '{}' with name '{}'", 
            interpolatedAppName, interpolatedName);
        
        try {
            ManagedApplicationContext applicationContext = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (applicationContext == null) {
                throw new RuntimeException("Managed application not found: " + interpolatedAppName);
            }
            
            boolean focused = windowController.focusWindow(applicationContext);
            if (!focused) {
                throw new RuntimeException("Failed to focus managed application: " + interpolatedAppName);
            }
            
            Rectangle windowBounds = windowController.getWindowBounds(applicationContext);
            BufferedImage windowScreenshot = screenCapture.captureRegion(
                windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height);
            String savedPath = screenCapture.saveScreenshotWithTimestamp(windowScreenshot, interpolatedName);
              addVerification("Take Named Managed Application Screenshot", true,
                String.format("Named screenshot '%s' of application '%s' captured and saved: %s", 
                    interpolatedName, interpolatedAppName, savedPath));
            logger.info("✅ ENTERPRISE NAMED SCREENSHOT SUCCESS: Named screenshot '{}' of managed application '{}' captured: {}",
                interpolatedName, interpolatedAppName, savedPath);
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to take named screenshot '%s' of managed application '%s': %s", 
                interpolatedName, interpolatedAppName, e.getMessage());
            addVerification("Take Named Managed Application Screenshot", false, errorMsg);
            logger.error("FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }    @When("I capture evidence of managed application {string} with description {string}")
    public void i_capture_evidence_of_managed_application_with_description(String applicationName, String description) {
        String interpolatedAppName = VariableManager.interpolate(applicationName);
        String interpolatedDescription = VariableManager.interpolate(description);
        logger.info("🔬 ENTERPRISE EVIDENCE CAPTURE: Capturing evidence of managed application '{}' with description: '{}'", 
            interpolatedAppName, interpolatedDescription);
        
        try {
            // ENTERPRISE: Use ManagedApplicationContext instead of ProcessInfo
            ManagedApplicationContext applicationContext = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (applicationContext == null) {
                throw new RuntimeException("Managed application not found: " + interpolatedAppName);
            }
            
            // ENTERPRISE: Focus using ManagedApplicationContext
            boolean focused = windowController.focusWindow(applicationContext);
            if (!focused) {
                throw new RuntimeException("Failed to focus managed application: " + interpolatedAppName);
            }
            
            // ENTERPRISE: Get window bounds using ManagedApplicationContext
            Rectangle windowBounds = windowController.getWindowBounds(applicationContext);
            BufferedImage windowScreenshot = screenCapture.captureRegion(
                windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height);
            String savedPath = screenCapture.saveScreenshotWithTimestamp(windowScreenshot, 
                "evidence_" + interpolatedAppName + "_" + System.currentTimeMillis());
            
            addVerification("Capture Managed Application Evidence", true,
                String.format("Evidence of application '%s' captured with description: '%s'. Saved: %s", 
                    interpolatedAppName, interpolatedDescription, savedPath));
            logger.info("✅ ENTERPRISE EVIDENCE SUCCESS: Evidence of '{}' captured with description '{}': {}", 
                interpolatedAppName, interpolatedDescription, savedPath);
            
        } catch (Exception e) {
            String errorMsg = String.format("Failed to capture evidence of managed application '%s': %s", 
                interpolatedAppName, e.getMessage());
            addVerification("Capture Managed Application Evidence", false, errorMsg);
            logger.error("❌ ENTERPRISE EVIDENCE FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
}
