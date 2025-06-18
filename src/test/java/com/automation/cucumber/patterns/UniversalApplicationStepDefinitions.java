package com.automation.cucumber.patterns;

import com.automation.cucumber.stepdefinitions.CommonStepDefinitionsBase;
import com.automation.cucumber.utilities.CucumberUtils;
import com.automation.config.ConfigManager;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Universal Application Step Definitions - Application-agnostic automation patterns
 * Provides universal step definitions that work with ANY Windows application
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class UniversalApplicationStepDefinitions extends CommonStepDefinitionsBase {
    
    private static final Logger logger = LoggerFactory.getLogger(UniversalApplicationStepDefinitions.class);
    
    @Given("I have the application {string} ready for automation")
    public void i_have_the_application_ready_for_automation(String applicationName) {
        try {
            Map<String, Object> config = ConfigManager.getApplicationConfig(applicationName);
            String windowTitle = (String) config.get("window_title");
            
            // Universal application readiness check
            if (!windowController.isWindowAvailable(windowTitle)) {
                throw new RuntimeException("Application is not available: " + applicationName);
            }
            
            // Focus the window (universal pattern)
            windowController.focusWindow(windowTitle);
            
            // Wait for application to be ready (universal timing)
            Thread.sleep(1000);
            
            CucumberUtils.logStepExecution("Application Ready", "Given", true, 
                String.format("Application '%s' is ready and focused", applicationName));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Application Ready", "Given", false, 
                "Failed to prepare application: " + applicationName + " - " + e.getMessage());
            throw new RuntimeException("Failed to prepare application: " + applicationName, e);
        }
    }
    
    @When("I navigate through application menu path {string}")
    public void i_navigate_through_application_menu_path(String menuPath) {
        try {
            logger.info("Navigating through application menu path: {}", menuPath);
            
            // Universal menu navigation pattern (works with any app)
            String[] menuItems = menuPath.split(">");
            
            for (String menuItem : menuItems) {
                String normalizedMenuItem = menuItem.toLowerCase().trim().replace(" ", "_");
                String imageName = "menu_" + normalizedMenuItem + ".png";
                String imagePath = getImagePath(imageName);
                
                if (!clickImage(imagePath, "Menu Item: " + menuItem.trim())) {
                    // Fallback: try keyboard navigation
                    navigateMenuByKeyboard(menuItem.trim());
                }
                
                // Universal delay between menu actions
                Thread.sleep(500);
            }
            
            CucumberUtils.logStepExecution("Navigate Menu Path", "When", true, 
                "Successfully navigated menu path: " + menuPath);
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Navigate Menu Path", "When", false, 
                "Failed to navigate menu path: " + menuPath + " - " + e.getMessage());
            throw new RuntimeException("Failed to navigate application menu path: " + menuPath, e);
        }
    }
    
    @When("I perform universal function key action {string}")
    public void i_perform_universal_function_key_action(String functionKeyAction) {
        try {
            logger.info("Performing universal function key action: {}", functionKeyAction);
            
            int keyCode = getUniversalFunctionKey(functionKeyAction);
            windowController.sendKey(keyCode);
            
            // Universal wait for function key processing
            Thread.sleep(1000);
            
            CucumberUtils.logStepExecution("Function Key Action", "When", true, 
                "Successfully performed function key action: " + functionKeyAction);
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Function Key Action", "When", false, 
                "Failed to perform function key action: " + functionKeyAction + " - " + e.getMessage());
            throw new RuntimeException("Failed to perform function key action: " + functionKeyAction, e);
        }
    }
    
    @When("I enter data in application field {string} with value {string}")
    public void i_enter_data_in_application_field(String fieldIdentifier, String value) {
        try {
            logger.info("Entering data in application field: {} = {}", fieldIdentifier, value);
            
            // Universal field interaction pattern
            boolean fieldFound = false;
            
            // Try multiple universal approaches to find and interact with field
            String[] fieldPatterns = {
                "field_" + fieldIdentifier.toLowerCase().replace(" ", "_") + ".png",
                "input_" + fieldIdentifier.toLowerCase().replace(" ", "_") + ".png",
                fieldIdentifier.toLowerCase().replace(" ", "_") + "_field.png"
            };
            
            for (String pattern : fieldPatterns) {
                String fieldImagePath = getImagePath(pattern);
                if (clickImage(fieldImagePath, "Application Field: " + fieldIdentifier)) {
                    fieldFound = true;
                    break;
                }
            }
            
            if (!fieldFound) {
                // Fallback: use OCR to find field label and tab to it
                findFieldByOCR(fieldIdentifier);
            }
            
            // Universal data entry pattern
            clearFieldUniversally();
            windowController.sendKeys(value);
            Thread.sleep(300);
            
            // Universal field exit (Tab or Enter)
            windowController.sendKey(java.awt.event.KeyEvent.VK_TAB);
            
            CucumberUtils.logStepExecution("Enter Field Data", "When", true, 
                String.format("Successfully entered '%s' in field '%s'", value, fieldIdentifier));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Enter Field Data", "When", false, 
                String.format("Failed to enter data in field '%s': %s", fieldIdentifier, e.getMessage()));
            throw new RuntimeException("Failed to enter data in application field: " + fieldIdentifier, e);
        }
    }
    
    @Then("I should see application message {string}")
    public void i_should_see_application_message(String expectedMessage) {
        try {
            logger.info("Verifying application message: {}", expectedMessage);
            
            // Universal message verification (works with any app)
            java.io.File screenshot = screenCapture.captureScreen();
            String screenText = ocrEngine.extractText(screenshot);
            
            boolean messageFound = screenText.toLowerCase().contains(expectedMessage.toLowerCase());
            
            if (!messageFound) {
                // Try looking in common message areas
                messageFound = checkCommonMessageAreas(expectedMessage);
            }
            
            if (!messageFound) {
                throw new AssertionError("Expected application message not found: " + expectedMessage);
            }
            
            CucumberUtils.logStepExecution("Verify Message", "Then", true, 
                "Successfully verified application message: " + expectedMessage);
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Verify Message", "Then", false, 
                "Failed to verify application message: " + expectedMessage + " - " + e.getMessage());
            throw new AssertionError("Failed to verify application message: " + expectedMessage, e);
        }
    }
    
    @When("I perform universal query operation with criteria {string}")
    public void i_perform_universal_query_operation_with_criteria(String criteria) {
        try {
            logger.info("Performing universal query operation with criteria: {}", criteria);
            
            // Universal query pattern (works across different applications)
            // Try common query methods
            boolean queryInitiated = false;
            
            // Method 1: F11 (common in many enterprise apps)
            if (!queryInitiated) {
                try {
                    windowController.sendKey(java.awt.event.KeyEvent.VK_F11);
                    Thread.sleep(1000);
                    queryInitiated = true;
                } catch (Exception e) {
                    logger.debug("F11 query method failed, trying alternatives");
                }
            }
            
            // Method 2: Ctrl+F (universal find)
            if (!queryInitiated) {
                try {
                    windowController.sendKeys("^f");
                    Thread.sleep(1000);
                    queryInitiated = true;
                } catch (Exception e) {
                    logger.debug("Ctrl+F method failed, trying menu-based approach");
                }
            }
            
            // Method 3: Menu-based query
            if (!queryInitiated) {
                try {
                    i_navigate_through_application_menu_path("Edit>Find");
                    queryInitiated = true;
                } catch (Exception e) {
                    logger.debug("Menu-based query failed");
                }
            }
            
            if (!queryInitiated) {
                throw new RuntimeException("Could not initiate query operation");
            }
            
            // Enter criteria (universal)
            windowController.sendKeys(criteria);
            Thread.sleep(500);
            
            // Execute query (universal approaches)
            try {
                windowController.sendKey(java.awt.event.KeyEvent.VK_ENTER);
            } catch (Exception e) {
                windowController.sendKey(java.awt.event.KeyEvent.VK_F8);
            }
            
            Thread.sleep(2000); // Universal wait for query results
            
            CucumberUtils.logStepExecution("Universal Query", "When", true, 
                "Successfully executed universal query with criteria: " + criteria);
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Universal Query", "When", false, 
                "Failed to execute universal query: " + e.getMessage());
            throw new RuntimeException("Failed to perform universal query operation: " + criteria, e);
        }
    }
    
    @Then("application should display {int} result(s)")
    public void application_should_display_results(int expectedCount) {
        try {
            logger.info("Verifying application displays {} result(s)", expectedCount);
            
            // Universal result counting (works with any application)
            java.io.File screenshot = screenCapture.captureScreen();
            String screenText = ocrEngine.extractText(screenshot);
            
            // Look for common result count patterns
            String[] countPatterns = {
                String.valueOf(expectedCount) + " record",
                String.valueOf(expectedCount) + " result",
                String.valueOf(expectedCount) + " item",
                String.valueOf(expectedCount) + " row",
                "Records: " + expectedCount,
                "Count: " + expectedCount,
                "Total: " + expectedCount
            };
            
            boolean countFound = false;
            for (String pattern : countPatterns) {
                if (screenText.toLowerCase().contains(pattern.toLowerCase())) {
                    countFound = true;
                    break;
                }
            }
            
            if (!countFound) {
                throw new AssertionError(String.format(
                    "Expected %d result(s) but could not verify count in screen text", expectedCount));
            }
            
            CucumberUtils.logStepExecution("Verify Result Count", "Then", true, 
                String.format("Successfully verified %d result(s) displayed", expectedCount));
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Verify Result Count", "Then", false, 
                "Failed to verify result count: " + e.getMessage());
            throw new AssertionError("Failed to verify application result count: " + expectedCount, e);
        }
    }
    
    @When("I save the current application transaction")
    public void i_save_the_current_application_transaction() {
        try {
            logger.info("Saving current application transaction");
            
            // Universal save patterns (try multiple approaches)
            boolean saveAttempted = false;
            
            // Method 1: Ctrl+S (most universal)
            try {
                windowController.sendKeys("^s");
                Thread.sleep(1000);
                saveAttempted = true;
            } catch (Exception e) {
                logger.debug("Ctrl+S save failed, trying alternatives");
            }
            
            // Method 2: F10 (common in enterprise apps)
            if (!saveAttempted) {
                try {
                    windowController.sendKey(java.awt.event.KeyEvent.VK_F10);
                    Thread.sleep(1000);
                    saveAttempted = true;
                } catch (Exception e) {
                    logger.debug("F10 save failed, trying menu approach");
                }
            }
            
            // Method 3: Menu-based save
            if (!saveAttempted) {
                try {
                    i_navigate_through_application_menu_path("File>Save");
                    saveAttempted = true;
                } catch (Exception e) {
                    logger.debug("Menu-based save failed");
                }
            }
            
            if (!saveAttempted) {
                throw new RuntimeException("Could not perform save operation");
            }
            
            Thread.sleep(2000); // Universal wait for save operation
            
            CucumberUtils.logStepExecution("Save Transaction", "When", true, 
                "Successfully saved application transaction");
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Save Transaction", "When", false, 
                "Failed to save application transaction: " + e.getMessage());
            throw new RuntimeException("Failed to save application transaction", e);
        }
    }
    
    @Then("application transaction should be saved successfully")
    public void application_transaction_should_be_saved_successfully() {
        try {
            logger.info("Verifying application transaction was saved successfully");
            
            // Universal save confirmation patterns
            String[] successPatterns = {
                "saved", "save successful", "transaction complete", 
                "commit complete", "update successful", "changes saved",
                "file saved", "document saved", "record saved"
            };
            
            java.io.File screenshot = screenCapture.captureScreen();
            String screenText = ocrEngine.extractText(screenshot);
            
            boolean saveConfirmed = false;
            for (String pattern : successPatterns) {
                if (screenText.toLowerCase().contains(pattern.toLowerCase())) {
                    saveConfirmed = true;
                    break;
                }
            }
            
            if (!saveConfirmed) {
                throw new AssertionError("No save confirmation found in application");
            }
            
            CucumberUtils.logStepExecution("Verify Save Success", "Then", true, 
                "Successfully verified application transaction was saved");
            
        } catch (Exception e) {
            CucumberUtils.logStepExecution("Verify Save Success", "Then", false, 
                "Failed to verify application save: " + e.getMessage());
            throw new AssertionError("Failed to verify application transaction save", e);
        }
    }
    
    // Private helper methods for universal automation patterns
    
    private int getUniversalFunctionKey(String functionKeyAction) {
        switch (functionKeyAction.toUpperCase()) {
            case "F1": case "HELP": return java.awt.event.KeyEvent.VK_F1;
            case "F2": case "EDIT": case "RENAME": return java.awt.event.KeyEvent.VK_F2;
            case "F3": case "SEARCH": case "FIND_NEXT": return java.awt.event.KeyEvent.VK_F3;
            case "F4": case "ADDRESS": case "LIST": return java.awt.event.KeyEvent.VK_F4;
            case "F5": case "REFRESH": case "GO": return java.awt.event.KeyEvent.VK_F5;
            case "F6": case "PREVIOUS": case "SWITCH": return java.awt.event.KeyEvent.VK_F6;
            case "F7": case "NEXT": case "SPELLING": return java.awt.event.KeyEvent.VK_F7;
            case "F8": case "EXECUTE": case "EXTEND": return java.awt.event.KeyEvent.VK_F8;
            case "F9": case "RECALCULATE": case "SEND": return java.awt.event.KeyEvent.VK_F9;
            case "F10": case "MENU": case "COMMIT": case "SAVE": return java.awt.event.KeyEvent.VK_F10;
            case "F11": case "FULLSCREEN": case "QUERY": return java.awt.event.KeyEvent.VK_F11;
            case "F12": case "SAVE_AS": case "COUNT": return java.awt.event.KeyEvent.VK_F12;
            default: throw new IllegalArgumentException("Unsupported function key: " + functionKeyAction);
        }
    }
    
    private void navigateMenuByKeyboard(String menuItem) {
        try {
            // Universal keyboard menu navigation
            windowController.sendKey(java.awt.event.KeyEvent.VK_ALT);
            Thread.sleep(200);
            
            // Send first letter of menu item
            if (!menuItem.isEmpty()) {
                char firstChar = Character.toLowerCase(menuItem.charAt(0));
                windowController.sendKeys(String.valueOf(firstChar));
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Menu navigation interrupted", e);
        }
    }
    
    private void clearFieldUniversally() {
        // Universal field clearing pattern
        try {
            windowController.sendKeys("^a"); // Ctrl+A to select all
            Thread.sleep(100);
            windowController.sendKey(java.awt.event.KeyEvent.VK_DELETE);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Fallback: just press Delete multiple times
            for (int i = 0; i < 10; i++) {
                windowController.sendKey(java.awt.event.KeyEvent.VK_BACK_SPACE);
            }
        } catch (Exception e) {
            // Fallback: just press Delete multiple times
            for (int i = 0; i < 10; i++) {
                windowController.sendKey(java.awt.event.KeyEvent.VK_BACK_SPACE);
            }
        }
    }
    
    private void findFieldByOCR(String fieldLabel) {
        try {
            java.awt.image.BufferedImage screenshot = screenCapture.captureFullScreen();
            File screenshotFile = screenCapture.saveBufferedImageToFile(screenshot, "temp_ocr_field_search");
            String screenText = ocrEngine.extractText(screenshotFile);
            
            if (screenText.toLowerCase().contains(fieldLabel.toLowerCase())) {
                // Field label found, try tabbing to it
                for (int i = 0; i < 20; i++) {
                    windowController.sendKey(java.awt.event.KeyEvent.VK_TAB);
                    Thread.sleep(100);
                    
                    // Check if we're in the right field (this would need more sophisticated logic)
                    // For now, we'll assume we found the field after some tabs
                    if (i > 5) { // Give it a few tabs before assuming we found it
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("OCR field finding failed: {}", e.getMessage());
        }
    }
    
    private boolean checkCommonMessageAreas(String expectedMessage) {
        // Check common message areas in applications
        java.awt.Rectangle[] messageAreas = {
            new java.awt.Rectangle(0, 0, 1920, 100),        // Top area
            new java.awt.Rectangle(0, 950, 1920, 130),      // Bottom area
            new java.awt.Rectangle(0, 300, 1920, 400),      // Center area
            new java.awt.Rectangle(0, 500, 1920, 100)       // Status bar area
        };
        
        for (java.awt.Rectangle area : messageAreas) {
            try {
                java.awt.image.BufferedImage regionCapture = screenCapture.captureRegion(area);
                File regionFile = screenCapture.saveBufferedImageToFile(regionCapture, "temp_message_area");
                String regionText = ocrEngine.extractText(regionFile);
                
                if (regionText.toLowerCase().contains(expectedMessage.toLowerCase())) {
                    return true;
                }
            } catch (Exception e) {
                // Continue checking other areas
            }
        }
        return false;
    }
}
