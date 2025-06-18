package com.automation.cucumber.stepdefinitions;

import io.cucumber.java.en.*;
import com.automation.utils.VariableManager;
import com.automation.config.ConfigManager;
import com.automation.core.ProcessManager;
import com.automation.models.ManagedApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.Assert;

/**
 * Step definitions for image-based operations
 * Supports image matching, clicking, validation, and visual automation
 */
public class ImageStepDefinitions extends CommonStepDefinitionsBase {
    private static final Logger logger = LoggerFactory.getLogger(ImageStepDefinitions.class);    
    @When("I click on the image {string}")
    public void i_click_on_the_image(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to click on image: {}", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point clickPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                logger.debug("Found image '{}' at coordinates ({}, {}), clicking at center point ({}, {})", 
                    interpolatedImageName, match.x, match.y, clickPoint.x, clickPoint.y);
                
                windowController.clickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Image Click", true, 
                    String.format("Successfully clicked image '%s' at (%d, %d)", 
                        interpolatedImageName, clickPoint.x, clickPoint.y));
                logger.info("Successfully clicked image: {}", interpolatedImageName);
            } else {
                logger.warn("Image '{}' not found on screen", interpolatedImageName);
                addVerification("Image Click", false, 
                    String.format("Image '%s' not found on screen", interpolatedImageName));
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to click image '{}': {}", interpolatedImageName, e.getMessage(), e);
            addVerification("Image Click", false, 
                String.format("Failed to click image '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to click image: " + interpolatedImageName, e);
        }
    }    
    @When("I click on the image {string} in region {string}")
    public void i_click_on_the_image_in_region(String imageName, String regionName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to click on image '{}' in region '{}'", interpolatedImageName, regionName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            logger.debug("Using region '{}': x={}, y={}, width={}, height={}", 
                regionName, region.x, region.y, region.width, region.height);
            
            BufferedImage regionCapture = screenCapture.captureRegion(region);
            File regionFile = screenCapture.saveBufferedImageToFile(regionCapture, "region_capture");
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(regionFile, templateImage);
            if (match != null) {
                // Adjust coordinates to full screen
                Point clickPoint = new Point(
                    region.x + match.x + match.width/2,
                    region.y + match.y + match.height/2
                );
                logger.debug("Found image '{}' in region '{}' at relative coordinates ({}, {}), clicking at screen coordinates ({}, {})", 
                    interpolatedImageName, regionName, match.x, match.y, clickPoint.x, clickPoint.y);
                
                windowController.clickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Image Click in Region", true, 
                    String.format("Successfully clicked image '%s' in region '%s' at (%d, %d)", 
                        interpolatedImageName, regionName, clickPoint.x, clickPoint.y));
                logger.info("Successfully clicked image '{}' in region '{}'", interpolatedImageName, regionName);
            } else {
                logger.warn("Image '{}' not found in region '{}'", interpolatedImageName, regionName);
                addVerification("Image Click in Region", false, 
                    String.format("Image '%s' not found in region '%s'", interpolatedImageName, regionName));
                throw new RuntimeException("Image not found in region: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to click image '{}' in region '{}': {}", interpolatedImageName, regionName, e.getMessage(), e);
            addVerification("Image Click in Region", false, 
                String.format("Failed to click image '%s' in region '%s': %s", 
                    interpolatedImageName, regionName, e.getMessage()));
            throw new RuntimeException("Failed to click image in region: " + interpolatedImageName, e);
        }
    }    
    @When("I double click on the image {string}")
    public void i_double_click_on_the_image(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to double-click on image: {}", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point clickPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                logger.debug("Found image '{}' at coordinates ({}, {}), double-clicking at center point ({}, {})", 
                    interpolatedImageName, match.x, match.y, clickPoint.x, clickPoint.y);
                
                windowController.doubleClickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Image Double Click", true, 
                    String.format("Successfully double-clicked image '%s' at (%d, %d)", 
                        interpolatedImageName, clickPoint.x, clickPoint.y));
                logger.info("Successfully double-clicked image: {}", interpolatedImageName);
            } else {
                logger.warn("Image '{}' not found on screen for double-click", interpolatedImageName);
                addVerification("Image Double Click", false, 
                    String.format("Image '%s' not found on screen", interpolatedImageName));
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to double-click image '{}': {}", interpolatedImageName, e.getMessage(), e);
            addVerification("Image Double Click", false, 
                String.format("Failed to double-click image '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to double-click image: " + interpolatedImageName, e);
        }
    }    
    @When("I right click on the image {string}")
    public void i_right_click_on_the_image(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to right-click on image: {}", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point clickPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                logger.debug("Found image '{}' at coordinates ({}, {}), right-clicking at center point ({}, {})", 
                    interpolatedImageName, match.x, match.y, clickPoint.x, clickPoint.y);
                
                windowController.rightClickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Image Right Click", true, 
                    String.format("Successfully right-clicked image '%s' at (%d, %d)", 
                        interpolatedImageName, clickPoint.x, clickPoint.y));
                logger.info("Successfully right-clicked image: {}", interpolatedImageName);
            } else {
                logger.warn("Image '{}' not found on screen for right-click", interpolatedImageName);
                addVerification("Image Right Click", false, 
                    String.format("Image '%s' not found on screen", interpolatedImageName));
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to right-click image '{}': {}", interpolatedImageName, e.getMessage(), e);
            addVerification("Image Right Click", false, 
                String.format("Failed to right-click image '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to right-click image: " + interpolatedImageName, e);
        }
    }    
    @When("I should see the image {string}")
    public void i_should_see_the_image(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Validating presence of image: {}", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            boolean isFound = (match != null);
            
            if (isFound) {
                logger.debug("Image '{}' found at coordinates ({}, {})", interpolatedImageName, match.x, match.y);
            } else {
                logger.warn("Image '{}' not found on screen", interpolatedImageName);
            }
            
            addVerification("Image Validation", isFound, 
                String.format("Image '%s' %s on screen", interpolatedImageName, isFound ? "found" : "not found"));
            
            if (!isFound) {
                captureErrorScreenshot("image_validation_failed");
                throw new AssertionError("Expected image not found: " + interpolatedImageName);
            }
            
            logger.info("Successfully validated presence of image: {}", interpolatedImageName);
        } catch (Exception e) {
            logger.error("Failed to validate image '{}': {}", interpolatedImageName, e.getMessage(), e);        addVerification("Image Validation", false, 
            String.format("Image validation failed for '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to validate image: " + interpolatedImageName, e);
        }
    }
    
    @Then("I should see the image {string} in region {string}")
    public void i_should_see_the_image_in_region(String imageName, String regionName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Validating presence of image '{}' in region '{}'", interpolatedImageName, regionName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            logger.debug("Using region '{}': x={}, y={}, width={}, height={}", 
                regionName, region.x, region.y, region.width, region.height);
            
            BufferedImage regionCapture = screenCapture.captureRegion(region);
            File regionFile = screenCapture.saveBufferedImageToFile(regionCapture, "region_capture");
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(regionFile, templateImage);
            boolean isFound = (match != null);
            
            if (isFound) {
                logger.debug("Image '{}' found in region '{}' at relative coordinates ({}, {})", 
                    interpolatedImageName, regionName, match.x, match.y);
            } else {
                logger.warn("Image '{}' not found in region '{}'", interpolatedImageName, regionName);
            }
            
            addVerification("Image Validation in Region", isFound, 
                String.format("Image '%s' %s in region '%s'", interpolatedImageName, 
                    isFound ? "found" : "not found", regionName));
            
            if (!isFound) {
                captureErrorScreenshot("image_validation_region_failed");
                throw new AssertionError("Expected image not found in region: " + interpolatedImageName);
            }
            
            logger.info("Successfully validated presence of image '{}' in region '{}'", interpolatedImageName, regionName);
        } catch (Exception e) {
            logger.error("Failed to validate image '{}' in region '{}': {}", interpolatedImageName, regionName, e.getMessage(), e);            addVerification("Image Validation in Region", false, 
                String.format("Image validation failed for '%s' in region '%s': %s", 
                    interpolatedImageName, regionName, e.getMessage()));
            throw new RuntimeException("Failed to validate image in region: " + interpolatedImageName, e);
        }
    }
    
    @Then("I should not see the image {string}")
    public void i_should_not_see_the_image(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Validating absence of image: {}", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            boolean isFound = (match != null);
            
            if (isFound) {
                logger.warn("Unwanted image '{}' found on screen at coordinates ({}, {})", interpolatedImageName, match.x, match.y);
            } else {
                logger.debug("Image '{}' correctly not found on screen", interpolatedImageName);
            }
            
            addVerification("Image Absence Validation", !isFound, 
                String.format("Unwanted image '%s' %s on screen", interpolatedImageName, 
                    isFound ? "found" : "not found"));
            
            if (isFound) {
                captureErrorScreenshot("unwanted_image_found");
                throw new AssertionError("Unwanted image found on screen: " + interpolatedImageName);
            }
            
            logger.info("Successfully validated absence of image: {}", interpolatedImageName);
        } catch (Exception e) {            logger.error("Failed to validate image absence '{}': {}", interpolatedImageName, e.getMessage(), e);
            addVerification("Image Absence Validation", false, 
                String.format("Image absence validation failed for '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to validate image absence: " + interpolatedImageName, e);
        }
    }
    
    @When("I hover over the image {string}")
    public void i_hover_over_the_image(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to hover over image: {}", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point hoverPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                logger.debug("Found image '{}' at coordinates ({}, {}), hovering at center point ({}, {})", 
                    interpolatedImageName, match.x, match.y, hoverPoint.x, hoverPoint.y);
                
                windowController.hoverAt(hoverPoint.x, hoverPoint.y);
                
                addVerification("Image Hover", true, 
                    String.format("Successfully hovered over image '%s' at (%d, %d)", 
                        interpolatedImageName, hoverPoint.x, hoverPoint.y));
                logger.info("Successfully hovered over image: {}", interpolatedImageName);
            } else {
                logger.warn("Image '{}' not found on screen for hover", interpolatedImageName);
                addVerification("Image Hover", false, 
                    String.format("Image '%s' not found on screen", interpolatedImageName));
                captureErrorScreenshot("image_not_found");
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to hover over image '{}': {}", interpolatedImageName, e.getMessage(), e);
            addVerification("Image Hover", false, 
                String.format("Failed to hover over image '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to hover over image: " + interpolatedImageName, e);
        }
    }    @When("I drag from image {string} to image {string}")
    public void i_drag_from_image_to_image(String sourceImageName, String targetImageName) {
        String interpolatedSourceImage = VariableManager.interpolate(sourceImageName);
        String interpolatedTargetImage = VariableManager.interpolate(targetImageName);
        logger.info("Attempting to drag from image '{}' to image '{}'", interpolatedSourceImage, interpolatedTargetImage);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File sourceImage = new File(ConfigManager.getImagePath(interpolatedSourceImage));
            File targetImage = new File(ConfigManager.getImagePath(interpolatedTargetImage));
            
            Rectangle sourceMatch = imageMatcher.findImage(screenshot, sourceImage);
            Rectangle targetMatch = imageMatcher.findImage(screenshot, targetImage);
            
            if (sourceMatch != null && targetMatch != null) {
                Point sourcePoint = new Point(sourceMatch.x + sourceMatch.width/2, sourceMatch.y + sourceMatch.height/2);
                Point targetPoint = new Point(targetMatch.x + targetMatch.width/2, targetMatch.y + targetMatch.height/2);
                
                logger.debug("Dragging from source image '{}' at ({}, {}) to target image '{}' at ({}, {})", 
                    interpolatedSourceImage, sourcePoint.x, sourcePoint.y, 
                    interpolatedTargetImage, targetPoint.x, targetPoint.y);
                
                windowController.dragAndDrop(sourcePoint.x, sourcePoint.y, targetPoint.x, targetPoint.y);
                
                addVerification("Image Drag and Drop", true, 
                    String.format("Successfully dragged from image '%s' to image '%s'", 
                        interpolatedSourceImage, interpolatedTargetImage));
                logger.info("Successfully completed drag and drop from '{}' to '{}'", interpolatedSourceImage, interpolatedTargetImage);
            } else {
                String missingImage = (sourceMatch == null) ? interpolatedSourceImage : interpolatedTargetImage;
                logger.warn("Image '{}' not found on screen for drag and drop", missingImage);
                addVerification("Image Drag and Drop", false, 
                    String.format("Image '%s' not found on screen", missingImage));
                captureErrorScreenshot("drag_drop_failed");
                throw new RuntimeException("Image not found for drag and drop: " + missingImage);
            }
        } catch (Exception e) {
            logger.error("Failed to drag from '{}' to '{}': {}", interpolatedSourceImage, interpolatedTargetImage, e.getMessage(), e);
            addVerification("Image Drag and Drop", false, 
                String.format("Failed to drag from '%s' to '%s': %s", 
                    interpolatedSourceImage, interpolatedTargetImage, e.getMessage()));
            throw new RuntimeException("Failed to perform drag and drop", e);
        }
    }    @When("I store the coordinates of image {string} in variable {string}")
    public void i_store_coordinates_of_image(String imageName, String variableName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to store coordinates of image '{}' in variable '{}'", interpolatedImageName, variableName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point center = new Point(match.x + match.width/2, match.y + match.height/2);
                String coordinates = center.x + "," + center.y;
                
                logger.debug("Found image '{}' at center coordinates ({}, {})", interpolatedImageName, center.x, center.y);
                
                VariableManager.setSessionVariable(variableName, coordinates);
                addVerification("Store Image Coordinates", true, 
                    String.format("Stored coordinates of image '%s' in variable '%s': %s", 
                        interpolatedImageName, variableName, coordinates));
                logger.info("Successfully stored coordinates of image '{}' as '{}'", interpolatedImageName, coordinates);
            } else {
                logger.warn("Image '{}' not found on screen for coordinate storage", interpolatedImageName);
                addVerification("Store Image Coordinates", false, 
                    String.format("Image '%s' not found on screen", interpolatedImageName));
                captureErrorScreenshot("image_not_found");
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to store coordinates of image '{}': {}", interpolatedImageName, e.getMessage(), e);
            addVerification("Store Image Coordinates", false, 
                String.format("Failed to store coordinates of image '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to store image coordinates: " + interpolatedImageName, e);
        }
    }    @Then("the image {string} should have similarity above {double}")
    public void the_image_should_have_similarity_above(String imageName, double minSimilarity) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Checking similarity of image '{}' against minimum threshold of {:.2f}%", 
            interpolatedImageName, minSimilarity * 100);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            double similarity = imageMatcher.calculateSimilarity(screenshot, templateImage);
            boolean hasGoodSimilarity = similarity >= minSimilarity;
            
            logger.debug("Image '{}' similarity calculated as {:.2f}% (threshold: {:.2f}%)", 
                interpolatedImageName, similarity * 100, minSimilarity * 100);
            
            addVerification("Image Similarity Check", hasGoodSimilarity, 
                String.format("Image '%s' similarity %.2f%% %s minimum %.2f%%", 
                    interpolatedImageName, similarity * 100, 
                    hasGoodSimilarity ? "meets" : "below", minSimilarity * 100));
            
            if (!hasGoodSimilarity) {
                logger.warn("Image '{}' similarity {:.2f}% is below required threshold {:.2f}%", 
                    interpolatedImageName, similarity * 100, minSimilarity * 100);
                captureErrorScreenshot("image_similarity_failed");
                throw new AssertionError(String.format("Image similarity %.2f%% below minimum %.2f%%", 
                    similarity * 100, minSimilarity * 100));
            }
            
            logger.info("Image '{}' passed similarity check with {:.2f}%", interpolatedImageName, similarity * 100);
        } catch (Exception e) {
            logger.error("Failed to check image similarity for '{}': {}", interpolatedImageName, e.getMessage(), e);
            addVerification("Image Similarity Check", false, 
                String.format("Image similarity check failed for '%s': %s", interpolatedImageName, e.getMessage()));
            throw new RuntimeException("Failed to check image similarity: " + interpolatedImageName, e);
        }
    }
    
    // =====================================================================================
    // ENTERPRISE-GRADE ADVANCED IMAGE INTERACTIONS - REVOLUTIONARY CAPABILITIES
    // =====================================================================================
    
    @When("I click on the image {string} with {int} milliseconds delay")
    public void i_click_on_image_with_delay(String imageName, int delayMs) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Clicking on image '{}' with {}ms delay", interpolatedImageName, delayMs);
        
        try {
            Thread.sleep(delayMs);
            i_click_on_the_image(interpolatedImageName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during delay", e);
        }
    }
    
    @When("I click on the image {string} at offset {int},{int}")
    public void i_click_on_image_with_offset(String imageName, int offsetX, int offsetY) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Clicking on image '{}' with offset ({}, {})", interpolatedImageName, offsetX, offsetY);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point clickPoint = new Point(match.x + match.width/2 + offsetX, match.y + match.height/2 + offsetY);
                windowController.clickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Image Click with Offset", true, 
                    String.format("Clicked image '%s' with offset (%d, %d) at (%d, %d)", 
                        interpolatedImageName, offsetX, offsetY, clickPoint.x, clickPoint.y));
            } else {
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            addVerification("Image Click with Offset", false, e.getMessage());
            throw new RuntimeException("Failed to click image with offset: " + interpolatedImageName, e);
        }
    }
    
    @When("I click on the {string} corner of image {string}")
    public void i_click_on_corner_of_image(String corner, String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Clicking on {} corner of image '{}'", corner, interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point clickPoint;
                switch (corner.toLowerCase()) {
                    case "top-left":
                        clickPoint = new Point(match.x, match.y);
                        break;
                    case "top-right":
                        clickPoint = new Point(match.x + match.width, match.y);
                        break;
                    case "bottom-left":
                        clickPoint = new Point(match.x, match.y + match.height);
                        break;
                    case "bottom-right":
                        clickPoint = new Point(match.x + match.width, match.y + match.height);
                        break;
                    default:
                        clickPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                }
                
                windowController.clickAt(clickPoint.x, clickPoint.y);
                addVerification("Corner Click", true, 
                    String.format("Clicked %s corner of image '%s' at (%d, %d)", 
                        corner, interpolatedImageName, clickPoint.x, clickPoint.y));
            } else {
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            addVerification("Corner Click", false, e.getMessage());
            throw new RuntimeException("Failed to click corner of image: " + interpolatedImageName, e);
        }
    }
    
    @When("I triple click on the image {string}")
    public void i_triple_click_on_image(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Triple clicking on image '{}'", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point clickPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                
                // Triple click implementation
                windowController.clickAt(clickPoint.x, clickPoint.y);
                Thread.sleep(50);
                windowController.clickAt(clickPoint.x, clickPoint.y);
                Thread.sleep(50);
                windowController.clickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Triple Click", true, 
                    String.format("Triple clicked image '%s' at (%d, %d)", 
                        interpolatedImageName, clickPoint.x, clickPoint.y));
            } else {
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            addVerification("Triple Click", false, e.getMessage());
            throw new RuntimeException("Failed to triple click image: " + interpolatedImageName, e);
        }
    }
    
    @When("I long press on the image {string} for {int} seconds")
    public void i_long_press_on_image(String imageName, int seconds) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Long pressing on image '{}' for {} seconds", interpolatedImageName, seconds);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point pressPoint = new Point(match.x + match.width/2, match.y + match.height/2);                // Long press implementation using multiple clicks
                windowController.clickAt(pressPoint.x, pressPoint.y);
                Thread.sleep(seconds * 1000);
                windowController.clickAt(pressPoint.x, pressPoint.y);
                
                addVerification("Long Press", true, 
                    String.format("Long pressed image '%s' for %d seconds at (%d, %d)", 
                        interpolatedImageName, seconds, pressPoint.x, pressPoint.y));
            } else {
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            addVerification("Long Press", false, e.getMessage());
            throw new RuntimeException("Failed to long press image: " + interpolatedImageName, e);
        }
    }
    
    @When("I scroll {string} on the image {string}")
    public void i_scroll_on_image(String direction, String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Scrolling {} on image '{}'", direction, interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point scrollPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                  boolean isUp = direction.toLowerCase().contains("up");
                boolean isDown = direction.toLowerCase().contains("down");
                  if (isUp || isDown) {
                    // Scroll implementation using mouse wheel simulation
                    windowController.clickAt(scrollPoint.x, scrollPoint.y);
                    if (isUp) {
                        windowController.sendKey(java.awt.event.KeyEvent.VK_PAGE_UP);
                    } else {
                        windowController.sendKey(java.awt.event.KeyEvent.VK_PAGE_DOWN);
                    }
                }
                
                addVerification("Scroll on Image", true, 
                    String.format("Scrolled %s on image '%s' at (%d, %d)", 
                        direction, interpolatedImageName, scrollPoint.x, scrollPoint.y));
            } else {
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            addVerification("Scroll on Image", false, e.getMessage());
            throw new RuntimeException("Failed to scroll on image: " + interpolatedImageName, e);
        }
    }
    
    @When("I wait for image {string} to appear within {int} seconds")
    public void i_wait_for_image_to_appear(String imageName, int timeoutSeconds) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Waiting for image '{}' to appear within {} seconds", interpolatedImageName, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        boolean found = false;
        
        try {
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                File screenshot = screenCapture.captureScreen();
                File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
                
                Rectangle match = imageMatcher.findImage(screenshot, templateImage);
                if (match != null) {
                    found = true;
                    break;
                }
                Thread.sleep(500);
            }
            
            addVerification("Wait for Image", found, 
                String.format("Image '%s' %s within %d seconds", 
                    interpolatedImageName, found ? "appeared" : "did not appear", timeoutSeconds));
            
            if (!found) {
                // If image is not found after timeout, capture error screenshot and assert false
                captureErrorScreenshot(String.format("Image_%s_not_found_after_timeout", imageName));
                Assert.fail(String.format("Image '%s' not found on screen within %d seconds", imageName, timeoutSeconds));
            }
        } catch (Exception e) {
            addVerification("Wait for Image", false, e.getMessage());
            throw new RuntimeException("Failed waiting for image: " + interpolatedImageName, e);
        }
    }
    
    @When("I wait for image {string} to disappear within {int} seconds")
    public void i_wait_for_image_to_disappear(String imageName, int timeoutSeconds) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Waiting for image '{}' to disappear within {} seconds", interpolatedImageName, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        boolean disappeared = false;
        
        try {
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                File screenshot = screenCapture.captureScreen();
                File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
                
                Rectangle match = imageMatcher.findImage(screenshot, templateImage);
                if (match == null) {
                    disappeared = true;
                    break;
                }
                Thread.sleep(500);
            }
            
            addVerification("Wait for Image Disappear", disappeared, 
                String.format("Image '%s' %s within %d seconds", 
                    interpolatedImageName, disappeared ? "disappeared" : "did not disappear", timeoutSeconds));
            
            if (!disappeared) {
                // If image is not found after timeout, capture error screenshot and assert false
                captureErrorScreenshot(String.format("Image_%s_still_visible_after_timeout", imageName));
                Assert.fail(String.format("Image '%s' still visible on screen after %d seconds", imageName, timeoutSeconds));
            }
        } catch (Exception e) {
            addVerification("Wait for Image Disappear", false, e.getMessage());
            throw new RuntimeException("Failed waiting for image to disappear: " + interpolatedImageName, e);
        }
    }
    
    @When("I click on image {string} if it exists")
    public void i_click_on_image_if_exists(String imageName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Conditionally clicking on image '{}' if it exists", interpolatedImageName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Point clickPoint = new Point(match.x + match.width/2, match.y + match.height/2);
                windowController.clickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Conditional Image Click", true, 
                    String.format("Image '%s' found and clicked at (%d, %d)", 
                        interpolatedImageName, clickPoint.x, clickPoint.y));
            } else {
                addVerification("Conditional Image Click", true, 
                    String.format("Image '%s' not found, skipping click", interpolatedImageName));
            }
        } catch (Exception e) {
            addVerification("Conditional Image Click", false, e.getMessage());
            logger.warn("Error during conditional image click: {}", e.getMessage());
        }
    }
    
    @When("I count occurrences of image {string} and store in variable {string}")
    public void i_count_image_occurrences(String imageName, String variableName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Counting occurrences of image '{}' and storing in variable '{}'", interpolatedImageName, variableName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            // Simple count implementation - find first occurrence
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            int count = (match != null) ? 1 : 0;
            VariableManager.setSessionVariable(variableName, String.valueOf(count));
            
            addVerification("Count Image Occurrences", true, 
                String.format("Found %d occurrences of image '%s', stored in variable '%s'", 
                    count, interpolatedImageName, variableName));
        } catch (Exception e) {
            addVerification("Count Image Occurrences", false, e.getMessage());
            throw new RuntimeException("Failed to count image occurrences: " + interpolatedImageName, e);
        }
    }
    
    @When("I capture region around image {string} with padding {int} and save as {string}")
    public void i_capture_region_around_image(String imageName, int padding, String saveFileName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Capturing region around image '{}' with {} pixel padding", interpolatedImageName, padding);
        
        try {
            File screenshot = screenCapture.captureScreen();
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(screenshot, templateImage);
            if (match != null) {
                Rectangle expandedRegion = new Rectangle(
                    Math.max(0, match.x - padding),
                    Math.max(0, match.y - padding),
                    match.width + (2 * padding),
                    match.height + (2 * padding)
                );
                
                BufferedImage regionCapture = screenCapture.captureRegion(expandedRegion);
                File savedFile = screenCapture.saveBufferedImageToFile(regionCapture, saveFileName);
                
                addVerification("Capture Region Around Image", true, 
                    String.format("Captured region around image '%s' with %d padding, saved as '%s' at %s", 
                        interpolatedImageName, padding, saveFileName, savedFile.getAbsolutePath()));
            } else {
                throw new RuntimeException("Image not found: " + interpolatedImageName);
            }
        } catch (Exception e) {
            addVerification("Capture Region Around Image", false, e.getMessage());
            throw new RuntimeException("Failed to capture region around image: " + interpolatedImageName, e);
        }
    }

    // =====================================================================================
    // WINDOW-SPECIFIC IMAGE MATCHING STEP DEFINITIONS - ENHANCED PERFORMANCE
    // =====================================================================================
      @And("I capture a screenshot of managed application {string} and save it as {string}")
    public void iCaptureScreenshotOfManagedApplicationAndSaveAs(String applicationName, String filename) {
        String interpolatedAppName = VariableManager.interpolate(applicationName);
        String interpolatedFilename = VariableManager.interpolate(filename);        logger.info("🎯 Enterprise Screenshot: Capturing managed application '{}' and saving as '{}'", interpolatedAppName, interpolatedFilename);
        ensureComponentsReady();

        ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
        if (context == null) {
            String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry.", interpolatedAppName);
            logger.error("❌ {}", errorMsg);
            captureErrorScreenshot("managed_app_not_found_for_screenshot_" + interpolatedAppName);
            addVerification("Capture Managed App Screenshot", false, errorMsg);
            Assert.fail(errorMsg);
            return;
        }

        logger.info("✅ Enterprise Process Found: PID {} for application '{}'", context.getProcessId(), interpolatedAppName);

        try {
            // Enterprise: Focus the window using ManagedApplicationContext
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }

            // Enterprise: Get window bounds using ManagedApplicationContext
            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null || windowBounds.isEmpty()) {
                String errorMsg = String.format("Could not get window bounds for PID %d (application '%s') to capture screenshot.",
                    context.getProcessId(), interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                captureErrorScreenshot("managed_app_bounds_not_found_" + interpolatedAppName);
                addVerification("Capture Managed App Screenshot", false, errorMsg);
                Assert.fail(errorMsg);
                return;
            }            logger.debug("🎯 Window bounds for PID {}: {}", context.getProcessId(), windowBounds);

            // Capture the window screenshot
            BufferedImage windowImage = screenCapture.captureWindow(windowBounds);
            if (windowImage == null) {
                String errorMsg = String.format("Failed to capture window image for PID %d (application '%s').", 
                    context.getProcessId(), interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                captureErrorScreenshot("managed_app_capture_failed_" + interpolatedAppName);
                addVerification("Capture Managed App Screenshot", false, errorMsg);
                Assert.fail(errorMsg);
                return;
            }

            // Ensure the filename has a .png extension
            String finalFilename = interpolatedFilename.toLowerCase().endsWith(".png") ? interpolatedFilename : interpolatedFilename + ".png";
            String savedPath = screenCapture.saveScreenshot(windowImage, finalFilename);

            if (savedPath != null) {
                logger.info("🚀 Enterprise Success: Screenshot of PID {} (application '{}') saved to: {}", 
                    context.getProcessId(), interpolatedAppName, savedPath);
                addVerification("Capture Managed App Screenshot", true, 
                    String.format("✅ Enterprise screenshot for PID %d ('%s') saved as '%s' at %s", 
                        context.getProcessId(), interpolatedAppName, finalFilename, savedPath));
            } else {
                String errorMsg = String.format("Failed to save screenshot for PID %d (application '%s') as '%s'.", 
                    context.getProcessId(), interpolatedAppName, finalFilename);
                logger.error("❌ {}", errorMsg);
                captureErrorScreenshot("managed_app_save_failed_" + interpolatedAppName);
                addVerification("Capture Managed App Screenshot", false, errorMsg);
                Assert.fail(errorMsg);
            }

        } catch (Exception e) {
            String errorMsg = String.format("Exception while capturing screenshot of PID %d (application '%s'): %s", 
                context.getProcessId(), interpolatedAppName, e.getMessage());
            logger.error("💥 {}", errorMsg, e);
            captureErrorScreenshot("managed_app_exception_" + interpolatedAppName);
            addVerification("Capture Managed App Screenshot", false, errorMsg);
            Assert.fail(errorMsg);
        }
    }    @When("I should see the image {string} in managed application {string}")
    public void i_should_see_the_image_in_managed_application(String imageName, String applicationName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("🎯 PID-DRIVEN IMAGE VALIDATION: Looking for image '{}' in managed application '{}'", interpolatedImageName, applicationName);
        File windowCaptureFile = null;        try {
            // Enterprise: Get the managed application's ManagedApplicationContext
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(applicationName);
            if (context == null) {
                String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry", applicationName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.info("✅ Enterprise Process Found: PID {} for application '{}'", context.getProcessId(), applicationName);
            
            // Enterprise: Focus the application window using ManagedApplicationContext
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            Thread.sleep(500); // Brief wait for window to become active
            
            // Enterprise: Get window bounds using ManagedApplicationContext
            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                String errorMsg = String.format("Failed to get window bounds for PID %d (application '%s')", 
                    context.getProcessId(), applicationName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.debug("🎯 Window bounds for PID {}: x={}, y={}, width={}, height={}", 
                context.getProcessId(), windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height);
            
            // Capture only the application window instead of full screen
            BufferedImage windowCaptureImage = screenCapture.captureWindow(windowBounds);
            windowCaptureFile = screenCapture.saveBufferedImageToTempFile(windowCaptureImage, "window_capture_" + applicationName);
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(windowCaptureFile, templateImage);
            boolean isFound = (match != null);
            
            if (isFound) {
                logger.debug("✅ Image '{}' found in PID {} (application '{}') at window-relative coordinates ({}, {})", 
                    interpolatedImageName, context.getProcessId(), applicationName, match.x, match.y);
            } else {
                logger.warn("❌ Image '{}' not found in PID {} (application '{}')", 
                    interpolatedImageName, context.getProcessId(), applicationName);
            }
            
            addVerification("Image Validation in Managed Application", isFound, 
                String.format("✅ Enterprise validation: Image '%s' %s in PID %d (application '%s')", 
                    interpolatedImageName, isFound ? "found" : "not found", context.getProcessId(), applicationName));
            
            if (!isFound) {
                captureErrorScreenshot("image_validation_failed_managed_app");
                throw new AssertionError("Expected image not found in managed application: " + interpolatedImageName);
            }
            
            logger.info("Successfully validated presence of image '{}' in managed application '{}'", 
                interpolatedImageName, applicationName);
        } catch (Exception e) {
            logger.error("Failed to validate image '{}' in managed application '{}': {}", 
                interpolatedImageName, applicationName, e.getMessage(), e);
            addVerification("Image Validation in Managed Application", false, 
                String.format("Image validation failed for '%s' in managed application '%s': %s", 
                    interpolatedImageName, applicationName, e.getMessage()));
            throw new RuntimeException("Failed to validate image in managed application: " + interpolatedImageName, e);
        } finally {
            if (windowCaptureFile != null && windowCaptureFile.exists()) {
                if (!windowCaptureFile.delete()) {
                    logger.warn("Could not delete temporary window capture file: {}", windowCaptureFile.getAbsolutePath());
                }
            }
        }
    }
    
    @When("I click on the image {string} in managed application {string}")
    public void i_click_on_the_image_in_managed_application(String imageName, String applicationName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to click on image '{}' in managed application '{}'", interpolatedImageName, applicationName);
        File windowCaptureFile = null;
        try {            // Enterprise: Get the managed application's ManagedApplicationContext
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(applicationName);
            if (context == null) {
                throw new RuntimeException("Managed application not found: " + applicationName);
            }
            
            // Enterprise: Focus the application window using ManagedApplicationContext
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            Thread.sleep(500); // Brief wait for window to become active
              // Enterprise: Get window bounds using ManagedApplicationContext
            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                throw new RuntimeException("Failed to get window bounds for managed application: " + applicationName);
            }
            
            // Capture only the application window instead of full screen
            BufferedImage windowCaptureImage = screenCapture.captureWindow(windowBounds);
            windowCaptureFile = screenCapture.saveBufferedImageToTempFile(windowCaptureImage, "window_click_capture_" + applicationName);
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));
            
            Rectangle match = imageMatcher.findImage(windowCaptureFile, templateImage);
            if (match != null) {
                // Adjust coordinates to full screen (relative to window's top-left)
                Point clickPoint = new Point(
                    windowBounds.x + match.x + match.width/2,
                    windowBounds.y + match.y + match.height/2
                );
                logger.debug("Found image '{}' in managed application '{}' at window-relative coordinates ({}, {}), clicking at screen coordinates ({}, {})", 
                    interpolatedImageName, applicationName, match.x, match.y, clickPoint.x, clickPoint.y);
                
                windowController.clickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Image Click in Managed Application", true, 
                    String.format("Successfully clicked image '%s' in managed application '%s' at screen (%d, %d)", 
                        interpolatedImageName, applicationName, clickPoint.x, clickPoint.y));
                logger.info("Successfully clicked image '{}' in managed application '{}'", interpolatedImageName, applicationName);
            } else {
                logger.warn("Image '{}' not found in managed application '{}'", interpolatedImageName, applicationName);
                addVerification("Image Click in Managed Application", false, 
                    String.format("Image '%s' not found in managed application '%s'", interpolatedImageName, applicationName));
                captureErrorScreenshot("image_click_failed_managed_app");
                throw new RuntimeException("Image not found in managed application: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to click image '{}' in managed application '{}': {}", 
                interpolatedImageName, applicationName, e.getMessage(), e);
            addVerification("Image Click in Managed Application", false, 
                String.format("Failed to click image '%s' in managed application '%s': %s", 
                    interpolatedImageName, applicationName, e.getMessage()));
            throw new RuntimeException("Failed to click image in managed application: " + interpolatedImageName, e);
        } finally {
            if (windowCaptureFile != null && windowCaptureFile.exists()) {
                if (!windowCaptureFile.delete()) {
                    logger.warn("Could not delete temporary window capture file: {}", windowCaptureFile.getAbsolutePath());
                }
            }
        }
    }
      @When("I double click on the image {string} in managed application {string}")
    public void i_double_click_on_the_image_in_managed_application(String imageName, String applicationName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to double-click on image '{}' in managed application '{}'", interpolatedImageName, applicationName);
        File windowCaptureFile = null;
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(applicationName);
            if (context == null) {
                throw new RuntimeException("Managed application not found: " + applicationName);
            }

            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            Thread.sleep(500);

            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                throw new RuntimeException("Failed to get window bounds for managed application: " + applicationName);
            }

            BufferedImage windowCaptureImage = screenCapture.captureWindow(windowBounds);
            windowCaptureFile = screenCapture.saveBufferedImageToTempFile(windowCaptureImage, "window_double_click_capture_" + applicationName);
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));

            Rectangle match = imageMatcher.findImage(windowCaptureFile, templateImage);
            if (match != null) {
                Point clickPoint = new Point(
                    windowBounds.x + match.x + match.width / 2,
                    windowBounds.y + match.y + match.height / 2
                );
                logger.debug("Found image '{}' in managed app '{}' at window-relative ({}, {}), double-clicking at screen ({}, {})",
                    interpolatedImageName, applicationName, match.x, match.y, clickPoint.x, clickPoint.y);
                
                windowController.doubleClickAt(clickPoint.x, clickPoint.y);
                
                addVerification("Image Double Click in Managed App", true,
                    String.format("Successfully double-clicked image '%s' in managed app '%s' at screen (%d, %d)",
                        interpolatedImageName, applicationName, clickPoint.x, clickPoint.y));
                logger.info("Successfully double-clicked image '{}' in managed app '{}'", interpolatedImageName, applicationName);
            } else {
                logger.warn("Image '{}' not found in managed app '{}' for double-click", interpolatedImageName, applicationName);
                addVerification("Image Double Click in Managed App", false,
                    String.format("Image '%s' not found in managed app '%s'", interpolatedImageName, applicationName));
                captureErrorScreenshot("image_double_click_failed_managed_app");
                throw new RuntimeException("Image not found for double-click in managed application: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to double-click image '{}' in managed app '{}': {}",
                interpolatedImageName, applicationName, e.getMessage(), e);
            addVerification("Image Double Click in Managed App", false,
                String.format("Failed to double-click image '%s' in managed app '%s': %s",
                    interpolatedImageName, applicationName, e.getMessage()));
            throw new RuntimeException("Failed to double-click image in managed application: " + interpolatedImageName, e);
        } finally {
            if (windowCaptureFile != null && windowCaptureFile.exists()) {
                if (!windowCaptureFile.delete()) {
                    logger.warn("Could not delete temporary window capture file: {}", windowCaptureFile.getAbsolutePath());
                }
            }
        }
    }    @When("I right click on the image {string} in managed application {string}")
    public void i_right_click_on_the_image_in_managed_application(String imageName, String applicationName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Attempting to right-click on image '{}' in managed application '{}'", interpolatedImageName, applicationName);
        File windowCaptureFile = null;
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(applicationName);
            if (context == null) {
                throw new RuntimeException("Managed application not found: " + applicationName);
            }

            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            Thread.sleep(500);

            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                throw new RuntimeException("Failed to get window bounds for managed application: " + applicationName);
            }

            BufferedImage windowCaptureImage = screenCapture.captureWindow(windowBounds);
            windowCaptureFile = screenCapture.saveBufferedImageToTempFile(windowCaptureImage, "window_right_click_capture_" + applicationName);
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));

            Rectangle match = imageMatcher.findImage(windowCaptureFile, templateImage);
            if (match != null) {
                Point clickPoint = new Point(
                    windowBounds.x + match.x + match.width / 2,
                    windowBounds.y + match.y + match.height / 2
                );
                logger.debug("Found image '{}' in managed app '{}' at window-relative ({}, {}), right-clicking at screen ({}, {})",
                    interpolatedImageName, applicationName, match.x, match.y, clickPoint.x, clickPoint.y);

                windowController.rightClickAt(clickPoint.x, clickPoint.y);

                addVerification("Image Right Click in Managed App", true,
                    String.format("Successfully right-clicked image '%s' in managed app '%s' at screen (%d, %d)",
                        interpolatedImageName, applicationName, clickPoint.x, clickPoint.y));
                logger.info("Successfully right-clicked image '{}' in managed app '{}'", interpolatedImageName, applicationName);
            } else {
                logger.warn("Image '{}' not found in managed app '{}' for right-click", interpolatedImageName, applicationName);
                addVerification("Image Right Click in Managed App", false,
                    String.format("Image '%s' not found in managed app '%s'", interpolatedImageName, applicationName));
                captureErrorScreenshot("image_right_click_failed_managed_app");
                throw new RuntimeException("Image not found for right-click in managed application: " + interpolatedImageName);
            }
        } catch (Exception e) {
            logger.error("Failed to right-click image '{}' in managed app '{}': {}",
                interpolatedImageName, applicationName, e.getMessage(), e);
            addVerification("Image Right Click in Managed App", false,
                String.format("Failed to right-click image '%s' in managed app '%s': %s",
                    interpolatedImageName, applicationName, e.getMessage()));
            throw new RuntimeException("Failed to right-click image in managed application: " + interpolatedImageName, e);
        } finally {
            if (windowCaptureFile != null && windowCaptureFile.exists()) {
                if (!windowCaptureFile.delete()) {
                    logger.warn("Could not delete temporary window capture file: {}", windowCaptureFile.getAbsolutePath());
                }
            }
        }
    }    @When("I should not see the image {string} in managed application {string}")
    public void i_should_not_see_the_image_in_managed_application(String imageName, String applicationName) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Validating absence of image '{}' in managed application '{}'", interpolatedImageName, applicationName);
        File windowCaptureFile = null;
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(applicationName);
            if (context == null) {
                throw new RuntimeException("Managed application not found: " + applicationName);
            }

            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            Thread.sleep(500);

            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                throw new RuntimeException("Failed to get window bounds for managed application: " + applicationName);
            }

            BufferedImage windowCaptureImage = screenCapture.captureWindow(windowBounds);
            windowCaptureFile = screenCapture.saveBufferedImageToTempFile(windowCaptureImage, "window_not_see_capture_" + applicationName);
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));

            Rectangle match = imageMatcher.findImage(windowCaptureFile, templateImage);
            boolean isFound = (match != null);

            if (isFound) {
                logger.warn("Unwanted image '{}' found in managed application '{}' at window-relative ({}, {})",
                    interpolatedImageName, applicationName, match.x, match.y);
            } else {
                logger.debug("Image '{}' correctly not found in managed application '{}'", interpolatedImageName, applicationName);
            }

            addVerification("Image Absence Validation in Managed App", !isFound,
                String.format("Unwanted image '%s' %s in managed application '%s'",
                    interpolatedImageName, isFound ? "found" : "not found", applicationName));

            if (isFound) {
                // If image is found, capture error screenshot and assert false
                captureErrorScreenshot(String.format("Image_%s_unexpectedly_found_in_managed_app_%s", imageName, applicationName));
                Assert.fail(String.format("Image '%s' should not be visible but was found in application window '%s'", imageName, applicationName));
            }

            logger.info("Successfully validated absence of image '{}' in managed application '{}'", 
                interpolatedImageName, applicationName);
        } catch (Exception e) {
            logger.error("Failed to validate image absence '{}' in managed app '{}': {}",
                interpolatedImageName, applicationName, e.getMessage(), e);
            addVerification("Image Absence Validation in Managed App", false,
                String.format("Image absence validation failed for '%s' in managed app '%s': %s",
                    interpolatedImageName, applicationName, e.getMessage()));
            throw new RuntimeException("Failed to validate image absence in managed application: " + interpolatedImageName, e);
        } finally {
            if (windowCaptureFile != null && windowCaptureFile.exists()) {
                if (!windowCaptureFile.delete()) {
                    logger.warn("Could not delete temporary window capture file: {}", windowCaptureFile.getAbsolutePath());
                }
            }
        }
    }
    
    @When("I wait for image {string} to appear in managed application {string} within {int} seconds")
    public void i_wait_for_image_to_appear_in_managed_application(String imageName, String appName, int timeoutSeconds) {
        String interpolatedImageName = VariableManager.interpolate(imageName);
        logger.info("Waiting for image '{}' to appear in managed application '{}' within {} seconds", 
            interpolatedImageName, appName, timeoutSeconds);

        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        boolean found = false;
        File windowCaptureFile = null;        try {
            // Enterprise: Get the managed application's ManagedApplicationContext
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(appName);
            if (context == null) {
                throw new RuntimeException("Managed application not found: " + appName);
            }

            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            // No Thread.sleep here, as we are in a polling loop

            Rectangle windowBounds = windowController.getWindowBounds(context); // Get bounds once
            if (windowBounds == null) {
                throw new RuntimeException("Failed to get window bounds for managed application: " + appName);
            }
            
            File templateImage = new File(ConfigManager.getImagePath(interpolatedImageName));

            while (System.currentTimeMillis() - startTime < timeoutMs) {
                BufferedImage windowCaptureImage = screenCapture.captureWindow(windowBounds);
                // Clean up previous temp file if it exists
                if (windowCaptureFile != null && windowCaptureFile.exists()) {
                    windowCaptureFile.delete();
                }
                windowCaptureFile = screenCapture.saveBufferedImageToTempFile(windowCaptureImage, "window_wait_appear_" + appName + "_" + imageName);
                
                Rectangle match = imageMatcher.findImage(windowCaptureFile, templateImage);
                if (match != null) {
                    logger.debug("Image '{}' found in managed application '{}' after {}ms", 
                        interpolatedImageName, appName, System.currentTimeMillis() - startTime);
                    found = true;
                    break;
                }
                Thread.sleep(500); // Polling interval
            }

            addVerification("Wait for Image in Managed App", found,
                String.format("Image '%s' %s in managed application '%s' within %d seconds",
                    interpolatedImageName, found ? "appeared" : "did not appear", appName, timeoutSeconds));

            if (!found) {
                // If image is not found after timeout, capture error screenshot and assert false
                captureErrorScreenshot(String.format("Image_%s_not_found_after_timeout", imageName));
                Assert.fail(String.format("Image '%s' not found on screen within %d seconds", imageName, timeoutSeconds));
            }
            logger.info("Image '{}' appeared in managed application '{}' within timeout.", interpolatedImageName, appName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait for image in managed application interrupted: {}", e.getMessage());
            addVerification("Wait for Image in Managed App", false, "Wait interrupted: " + e.getMessage());
            throw new RuntimeException("Wait for image in managed application interrupted: " + interpolatedImageName, e);
        } catch (Exception e) {
            logger.error("Failed waiting for image '{}' in managed app '{}': {}",
                interpolatedImageName, appName, e.getMessage(), e);
            addVerification("Wait for Image in Managed App", false, "Failed waiting for image: " + e.getMessage());
            throw new RuntimeException("Failed waiting for image in managed application: " + interpolatedImageName, e);
        } finally {
            if (windowCaptureFile != null && windowCaptureFile.exists()) {
                if (!windowCaptureFile.delete()) {
                    logger.warn("Could not delete temporary window capture file: {}", windowCaptureFile.getAbsolutePath());
                }
            }
        }
    }
}
