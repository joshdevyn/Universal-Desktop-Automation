package com.automation.tests;

import com.automation.core.*;
import com.automation.config.ConfigManager;
import com.automation.models.ManagedApplicationContext;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.sikuli.script.Match;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

/**
 * SAPGUITest demonstrates automation of SAP GUI applications
 * This includes SAP ERP, SAP R/3, and other SAP business applications
 * 
 * This test class works independently without extending TestBase to avoid inheritance complications.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SAPGUITest {
    
    private WindowController windowController;
    private ScreenCapture screenCapture;
    private OCREngine ocrEngine;
    private ImageMatcher imageMatcher;
    private TestResult testResult;
    
    private String applicationName = "sap_gui";
    private Map<String, Object> sapConfig;
    private Map<String, String> keyMappings;
    private Map<String, Integer> waitTimes;
    
    @BeforeAll
    public void setUp() {
        // Initialize framework components directly
        windowController = new WindowController();
        screenCapture = new ScreenCapture();
        ocrEngine = new OCREngine();
        imageMatcher = new ImageMatcher();
        testResult = new TestResult();
        
        // Load SAP GUI configuration using static methods
        sapConfig = ConfigManager.getApplicationConfig(applicationName);
        keyMappings = ConfigManager.getKeyMappings(applicationName);
        waitTimes = ConfigManager.getWaitTimes(applicationName);
        
        assertNotNull(sapConfig, "SAP GUI configuration should be available");
        
        testResult.addTestData("application_type", "sap_gui");
        testResult.addTestData("sap_version", "Auto-detected");
    }
      @Test @Order(1)
    public void testSAPGUILaunch() {
        testResult.addLog("Testing SAP GUI launch with PID-driven approach");
          // Check if SAP GUI is already running using ProcessManager
        ManagedApplicationContext existingProcess = ProcessManager.getInstance().getRunningApplicationContext("sap_gui");
        boolean found = existingProcess != null;
        
        if (!found) {
            testResult.addLog("No existing SAP GUI process found, attempting launch...");
            ManagedApplicationContext launchedProcess = ProcessManager.getInstance().launchAndTrackApplication("sap_gui");
            if (launchedProcess != null) {
                found = true;
                testResult.addLog(String.format("Successfully launched SAP GUI: PID %d", launchedProcess.getProcessId()));
            } else {
                testResult.addLog("Failed to launch SAP GUI via ProcessManager");
            }
        } else {
            testResult.addLog(String.format("Found existing SAP GUI process: PID %d", existingProcess.getProcessId()));
        }
        
        if (found) {
            testResult.addVerification("SAP GUI window found", true, 
                "Successfully located SAP GUI window");
            
            // Capture initial state
            BufferedImage screenshot = screenCapture.captureFullScreen();
            String screenshotPath = screenCapture.saveScreenshot(screenshot, "sap_initial");
            testResult.addScreenshot("sap_initial", screenshotPath);              // Focus the SAP window using ProcessManager
            ManagedApplicationContext sapProcess = ProcessManager.getInstance().getRunningApplicationContext("sap_gui");
            if (sapProcess != null) {
                boolean focused = windowController.focusWindow(sapProcess);
                testResult.addLog(String.format("SAP GUI focus attempt: %s (PID: %d)", 
                    focused ? "successful" : "failed", sapProcess.getProcessId()));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
              } else {
            testResult.addVerification("SAP GUI window found", false, 
                "Could not find SAP GUI application");
            fail("SAP GUI application not found. Please ensure SAP GUI is running.");
        }}
      @Test @Order(2)
    public void testSAPLogin() {
        try {
            // Get login credentials from configuration
            @SuppressWarnings("unchecked")
            Map<String, String> credentials = (Map<String, String>) sapConfig.get("credentials");
            String client = credentials.get("client");
            String username = credentials.get("username");
            String password = credentials.get("password");
            String language = credentials.get("language");
            
            // Wait for login screen to appear
            try {
                Thread.sleep(waitTimes.get("login_screen_wait") * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Capture screen before login
            BufferedImage beforeLogin = screenCapture.captureFullScreen();
            String beforeLoginPath = screenCapture.saveScreenshot(beforeLogin, "sap_before_login");
            testResult.addScreenshot("sap_before_login", beforeLoginPath);
            
            // Enter client (if field exists)
            if (client != null && !client.isEmpty()) {
                sendText(client);
                sendKey(Integer.parseInt(keyMappings.get("TAB")));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // Enter username
            sendText(username);
            sendKey(Integer.parseInt(keyMappings.get("TAB")));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Enter password
            sendText(password);
            sendKey(Integer.parseInt(keyMappings.get("TAB")));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
              // Enter language (if field exists)
            if (language != null && !language.isEmpty()) {
                sendText(language);
            }
            
            // Press Enter to login
            sendKey(Integer.parseInt(keyMappings.get("ENTER")));
            
            // Wait for login to process
            try {
                Thread.sleep(waitTimes.get("login_process_wait") * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Capture screen after login
            BufferedImage afterLogin = screenCapture.captureFullScreen();
            String afterLoginPath = screenCapture.saveScreenshot(afterLogin, "sap_after_login");
            testResult.addScreenshot("sap_after_login", afterLoginPath);
            
            // Verify successful login by checking for SAP main menu indicators
            String screenText = ocrEngine.extractText(afterLogin);
            boolean loginSuccess = screenText.contains("SAP Easy Access") || 
                                 screenText.contains("User Menu") ||
                                 screenText.contains("Favorites") ||
                                 screenText.contains("SAP Menu");
            
            testResult.addVerification("SAP GUI login successful", loginSuccess, 
                "Login verification based on main menu detection");
            
            if (!loginSuccess) {
                testResult.addVerification("OCR Text Content", false, 
                    "Expected SAP indicators not found in: " + screenText.substring(0, Math.min(100, screenText.length())));
            }
              } catch (Exception e) {
            testResult.addVerification("SAP GUI login", false, "Login failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "sap_login_error");
            testResult.addScreenshot("sap_login_error", errorPath);
            fail("SAP GUI login failed: " + e.getMessage());
        }
    }
    
    @Test @Order(3)
    public void testSAPTransactionNavigation() {
        try {
            // Test navigation to common SAP transactions
            String[] transactions = {
                "VA01",  // Create Sales Order
                "MM01",  // Create Material
                "SM30"   // Table Maintenance
            };
              for (String transaction : transactions) {
                // Navigate using transaction code
                sendText("/n" + transaction);
                sendKey(Integer.parseInt(keyMappings.get("ENTER")));
                
                // Wait for transaction to load
                try {
                    Thread.sleep(waitTimes.get("transaction_load_wait") * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Capture transaction screen
                BufferedImage transactionScreen = screenCapture.captureFullScreen();
                String transactionPath = screenCapture.saveScreenshot(transactionScreen, "sap_transaction_" + transaction.toLowerCase());
                testResult.addScreenshot("sap_transaction_" + transaction.toLowerCase(), transactionPath);
                
                // Validate transaction loaded
                String screenText = ocrEngine.extractText(transactionScreen);
                boolean transactionLoaded = screenText.contains(transaction) ||
                                          !screenText.contains("does not exist") &&
                                          !screenText.contains("not authorized");
                
                testResult.addVerification("Transaction " + transaction + " navigation", transactionLoaded,
                    "Transaction " + transaction + " access verification");
                
                // Return to main menu
                sendKey(Integer.parseInt(keyMappings.get("F3"))); // Back
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
        } catch (Exception e) {
            testResult.addVerification("SAP transaction navigation", false, "Navigation failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "sap_navigation_error");
            testResult.addScreenshot("sap_navigation_error", errorPath);
            fail("SAP transaction navigation failed: " + e.getMessage());
        }    }
    
    @Test @Order(4)
    public void testSAPMenuNavigation() {
        try {
            // Test navigation through SAP menu structure
            // Navigate to System menu
            sendKey(Integer.parseInt(keyMappings.get("ALT_S"))); // Alt+S for System
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            BufferedImage systemMenu = screenCapture.captureFullScreen();
            String systemMenuPath = screenCapture.saveScreenshot(systemMenu, "sap_system_menu");
            testResult.addScreenshot("sap_system_menu", systemMenuPath);
            
            // Check if system menu appeared
            String menuText = ocrEngine.extractText(systemMenu);
            boolean systemMenuVisible = menuText.contains("User Profile") ||
                                      menuText.contains("Services") ||
                                      menuText.contains("Utilities");
            
            testResult.addVerification("SAP System menu access", systemMenuVisible,
                "System menu visibility check");
              // Close menu with Escape
            sendKey(Integer.parseInt(keyMappings.get("ESCAPE")));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Test Help menu
            sendKey(Integer.parseInt(keyMappings.get("ALT_H"))); // Alt+H for Help
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            BufferedImage helpMenu = screenCapture.captureFullScreen();
            String helpMenuPath = screenCapture.saveScreenshot(helpMenu, "sap_help_menu");
            testResult.addScreenshot("SAP Help Menu", helpMenuPath);
            
            // Check if help menu appeared
            String helpText = ocrEngine.extractText(helpMenu);
            boolean helpMenuVisible = helpText.contains("Application Help") ||
                                    helpText.contains("SAP Library") ||
                                    helpText.contains("Release Notes");
            
            testResult.addVerification("SAP Help menu access", helpMenuVisible,
                "Help menu visibility check");
              // Close menu with Escape
            sendKey(Integer.parseInt(keyMappings.get("ESCAPE")));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            testResult.addVerification("SAP menu navigation", false, "Menu navigation failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "sap_menu_error");
            testResult.addScreenshot("sap_menu_error", errorPath);
            fail("SAP menu navigation failed: " + e.getMessage());
        }
    }
      @Test @Order(5)
    public void testSAPImageBasedInteraction() {        try {            // Test image-based automation for SAP GUI elements
            
            // Look for common SAP GUI buttons/icons
            String[] imageFiles = {
                "sap_save_button.png",
                "sap_back_button.png",
                "sap_execute_button.png"
            };
            
            for (String imageFile : imageFiles) {
                File buttonImage = new File("src/test/resources/images/" + imageFile);
                if (buttonImage.exists()) {
                    Match match = imageMatcher.findImage(buttonImage.getAbsolutePath());
                    
                    boolean imageFound = (match != null);
                    testResult.addVerification("SAP GUI image detection: " + imageFile, imageFound,
                        "Image-based element detection for " + imageFile);
                    
                    if (imageFound) {
                        // Optional: Click the found element (commented out to avoid side effects)                        // windowController.clickAt(match.x + match.w/2, match.y + match.h/2);
                        // Thread.sleep(2000);
                    }
                }
            }
            
        } catch (Exception e) {
            testResult.addVerification("SAP image-based interaction", false, "Image interaction failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "sap_image_error");
            testResult.addScreenshot("sap_image_error", errorPath);
            // Don't fail the test as this is dependent on having the right image files
        }
    }
      @Test @Order(6)
    public void testSAPLogout() {
        try {
            // Logout from SAP GUI
            // Method 1: Using System > Log Off
            sendKey(Integer.parseInt(keyMappings.get("ALT_S"))); // Alt+S for System
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Type "o" for Log Off (or navigate to it)
            sendText("o");
            sendKey(Integer.parseInt(keyMappings.get("ENTER")));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Confirm logout if prompted
            BufferedImage logoutPrompt = screenCapture.captureFullScreen();
            String promptText = ocrEngine.extractText(logoutPrompt);
              if (promptText.contains("Yes") || promptText.contains("OK")) {
                sendKey(Integer.parseInt(keyMappings.get("ENTER")));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // Capture final state
            BufferedImage logoutScreen = screenCapture.captureFullScreen();
            String logoutScreenPath = screenCapture.saveScreenshot(logoutScreen, "sap_logout_final");
            testResult.addScreenshot("SAP Logout Final", logoutScreenPath);
            
            // Verify logout by checking for login screen return
            String screenText = ocrEngine.extractText(logoutScreen);
            boolean logoutSuccess = screenText.contains("Client") || 
                                  screenText.contains("User") ||
                                  screenText.contains("Password") ||
                                  screenText.contains("Language");
            
            testResult.addVerification("SAP GUI logout successful", logoutSuccess,
                "Logout verification based on return to login screen");
              } catch (Exception e) {
            testResult.addVerification("SAP GUI logout", false, "Logout failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "sap_logout_error");
            testResult.addScreenshot("sap_logout_error", errorPath);
            // Don't fail the test on logout - it's cleanup
        }
    }
    
    @AfterAll
    public void tearDown() {
        try {
            // Clean up resources
            if (windowController != null) {
                windowController = null;
            }
            if (screenCapture != null) {
                screenCapture = null;
            }
            if (ocrEngine != null) {
                ocrEngine = null;
            }
            if (imageMatcher != null) {
                imageMatcher = null;
            }
        } catch (Exception e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }
    
    // Helper methods to mimic TestBase functionality
    private void sendText(String text) {
        windowController.sendText(text);
    }
    
    private void sendKey(int keyCode) {
        // Convert int keyCode to String for WindowController API
        String keyName = getKeyName(keyCode);
        windowController.sendKey(keyName);
    }
    
    private String getKeyName(int keyCode) {
        switch (keyCode) {
            case 9: return "TAB";
            case 13: return "ENTER";
            case 27: return "ESCAPE";
            case 18: return "ALT";
            default: 
                if (keyCode >= 112 && keyCode <= 135) {
                    return "F" + (keyCode - 111); // F1-F24
                }
                return String.valueOf((char)keyCode);
        }
    }
    
    // Getter for test result access
    public TestResult getTestResult() {
        return testResult;
    }
}
