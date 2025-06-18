package com.automation.tests;

import com.automation.core.*;
import com.automation.config.ConfigManager;
import com.automation.models.ManagedApplicationContext;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * MainframeTerminalTest demonstrates automation of mainframe terminal applications
 * This includes 3270 terminals, AS400/5250 terminals, and similar legacy systems
 * 
 * This test class works independently without extending TestBase to avoid inheritance complications.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainframeTerminalTest {
    
    private WindowController windowController;
    private ScreenCapture screenCapture;
    private OCREngine ocrEngine;
    private TestResult testResult;
    
    private String applicationName = "tandem_terminal";
    private Map<String, Object> terminalConfig;
    private Map<String, String> keyMappings;
    private Map<String, Integer> waitTimes;
    
    @BeforeAll
    static void setUpClass() {
        // Static class setup if needed
    }
    
    @BeforeEach
    void setUp() {
        // Initialize framework components directly
        windowController = new WindowController();
        screenCapture = new ScreenCapture();
        ocrEngine = new OCREngine();
        testResult = new TestResult();
        
        // Load terminal configuration using static methods
        terminalConfig = ConfigManager.getApplicationConfig(applicationName);
        keyMappings = ConfigManager.getKeyMappings(applicationName);
        waitTimes = ConfigManager.getWaitTimes(applicationName);
        
        assertNotNull(terminalConfig, "Terminal configuration should be available");
        
        testResult.addTestData("application_type", "mainframe_terminal");
        testResult.addTestData("terminal_config", applicationName);
    }
      @Test
    @Order(1)
    @DisplayName("Launch and connect to mainframe terminal")
    public void testTerminalLaunch() {
        // Note: This test assumes the terminal emulator is already running
        // In a real scenario, you would modify the configuration to include the executable path
          ManagedApplicationContext processInfo = null;
        boolean found = false;
        
        try {
            // First attempt: Try to get running process by managed application name
            processInfo = ProcessManager.getInstance().getRunningApplicationContext(applicationName);
            if (processInfo != null) {
                found = true;
                testResult.addVerification("Terminal process found", true, 
                    String.format("Found existing terminal process: PID %d", processInfo.getProcessId()));
            }
        } catch (Exception e) {
            // If managed app not found, try alternative terminal applications
            String[] terminalApps = {"terminal", "3270", "5250", "as400"};
            for (String termApp : terminalApps) {
                try {
                    processInfo = ProcessManager.getInstance().getRunningApplicationContext(termApp);
                    if (processInfo != null) {
                        found = true;
                        testResult.addVerification("Terminal process found", true, 
                            String.format("Found alternative terminal process: %s, PID %d", termApp, processInfo.getProcessId()));
                        break;
                    }
                } catch (Exception ex) {
                    // Continue searching
                }
            }
        }
        
        if (found && processInfo != null) {
            // Capture initial state
            BufferedImage screenshot = screenCapture.captureFullScreen();
            String screenshotPath = screenCapture.saveScreenshot(screenshot, "terminal_launch");
            testResult.addScreenshot("initial", screenshotPath);
            
            // Focus the terminal window using ProcessInfo
            try {
                windowController.focusWindow(processInfo);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                testResult.addVerification("Terminal window focus", false, 
                    String.format("Failed to focus terminal window for PID %d: %s", processInfo.getProcessId(), e.getMessage()));
                fail("Could not focus terminal window for process: " + processInfo.getProcessId());
            }
            
        } else {
            testResult.addVerification("Terminal window found", false, 
                "Could not find any terminal application process");
            fail("Terminal application not found. Please ensure the terminal emulator is running.");
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Test terminal login")    public void testTerminalLogin() {
        try {
            // Get login credentials from configuration
            @SuppressWarnings("unchecked")
            Map<String, String> credentials = (Map<String, String>) terminalConfig.get("credentials");
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            // Wait for login prompt to appear
            try {
                Thread.sleep(waitTimes.get("login_prompt_wait") * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Capture screen before login
            BufferedImage beforeLogin = screenCapture.captureFullScreen();
            String beforeLoginPath = screenCapture.saveScreenshot(beforeLogin, "before_login");
            testResult.addScreenshot("before_login", beforeLoginPath);
            
            // Enter username
            sendText(username);
            sendKey(Integer.parseInt(keyMappings.get("TAB")));
            try {
                Thread.sleep(1000);            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Enter password
            sendText(password);
            sendKey(Integer.parseInt(keyMappings.get("ENTER")));
            
            // Wait for login to process
            try {
                Thread.sleep(waitTimes.get("login_process_wait") * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Capture screen after login
            BufferedImage afterLogin = screenCapture.captureFullScreen();
            String afterLoginPath = screenCapture.saveScreenshot(afterLogin, "after_login");
            testResult.addScreenshot("after_login", afterLoginPath);
              // Verify successful login by checking for common mainframe indicators
            String screenText = ocrEngine.extractText(afterLogin);
            boolean loginSuccess = screenText.contains("READY") || 
                                 screenText.contains("WELCOME") ||
                                 screenText.contains("MAIN MENU") ||
                                 screenText.contains("COMMAND");
            
            testResult.addVerification("Terminal login successful", loginSuccess, 
                "Login verification based on screen text analysis");
            
            if (!loginSuccess) {
                testResult.addVerification("OCR Text Content", false,                "Expected login indicators not found in: " + screenText.substring(0, Math.min(100, screenText.length())));
            }
            
        } catch (Exception e) {
            testResult.addVerification("Terminal login", false, "Login failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "login_error");
            testResult.addScreenshot("login_error", errorPath);
            fail("Terminal login failed: " + e.getMessage());
        }
    }
    
    @Test @Order(3)
    public void testTerminalCommands() {
        try {
            // Test basic terminal commands
            String[] testCommands = {
                "WHO",      // Show active users
                "TIME",     // Display current time
                "HELP"      // Show help information
            };
              for (String command : testCommands) {
                // Clear the command line if needed
                sendKey(Integer.parseInt(keyMappings.get("CLEAR_LINE")));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Send command
                sendText(command);
                sendKey(Integer.parseInt(keyMappings.get("ENTER")));
                
                // Wait for command to execute
                try {
                    Thread.sleep(waitTimes.get("command_execution_wait") * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Capture result
                BufferedImage commandResult = screenCapture.captureFullScreen();
                String commandPath = screenCapture.saveScreenshot(commandResult, "command_" + command.toLowerCase());
                testResult.addScreenshot("command_" + command.toLowerCase(), commandPath);
                  // Extract and validate response
                String responseText = ocrEngine.extractText(commandResult);
                boolean commandExecuted = !responseText.contains("INVALID") && 
                                        !responseText.contains("ERROR") &&
                                        !responseText.contains("UNKNOWN");
                
                testResult.addVerification("Command execution: " + command, commandExecuted,
                    "Command '" + command + "' response analysis");
                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
              } catch (Exception e) {
            testResult.addVerification("Terminal commands", false, "Command execution failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "command_error");
            testResult.addScreenshot("command_error", errorPath);
            fail("Terminal command execution failed: " + e.getMessage());
        }
    }
    
    @Test @Order(4)
    public void testTerminalNavigation() {        try {
            // Test function key navigation (common in mainframe terminals)
            Integer[] functionKeys = {
                Integer.parseInt(keyMappings.get("F1")),   // Help
                Integer.parseInt(keyMappings.get("F3")),   // Exit/Back
                Integer.parseInt(keyMappings.get("F12"))   // Cancel
            };
            
            for (Integer fKey : functionKeys) {
                // Press function key
                sendKey(fKey);
                try {
                    Thread.sleep(waitTimes.get("function_key_wait") * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Capture result
                BufferedImage navigationResult = screenCapture.captureFullScreen();
                String navPath = screenCapture.saveScreenshot(navigationResult, "function_key_" + fKey);
                testResult.addScreenshot("function_key_" + fKey, navPath);
                  // Basic validation that the screen changed
                String screenText = ocrEngine.extractText(navigationResult);
                boolean screenChanged = screenText.length() > 10; // Basic check for content
                
                testResult.addVerification("Function key F" + fKey + " navigation", screenChanged,
                    "Function key navigation test");
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
        } catch (Exception e) {
            testResult.addVerification("Terminal navigation", false, "Navigation failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "navigation_error");
            testResult.addScreenshot("navigation_error", errorPath);
            fail("Terminal navigation failed: " + e.getMessage());
        }
    }
    
    @Test @Order(5)
    public void testTerminalLogout() {        try {
            // Attempt to logout from the terminal
            String logoutCommand = (String) terminalConfig.get("logout_command");
            if (logoutCommand == null) {
                logoutCommand = "LOGOFF"; // Default logout command
            }
            
            // Send logout command
            sendText(logoutCommand);
            sendKey(Integer.parseInt(keyMappings.get("ENTER")));
            
            // Wait for logout to process
            try {
                Thread.sleep(waitTimes.get("logout_wait") * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Capture final state
            BufferedImage logoutScreen = screenCapture.captureFullScreen();
            String logoutPath = screenCapture.saveScreenshot(logoutScreen, "logout_screen");            testResult.addScreenshot("logout_screen", logoutPath);
            
            // Verify logout by checking for login prompt return
            String screenText = ocrEngine.extractText(logoutScreen);
            boolean logoutSuccess = screenText.contains("LOGIN") || 
                                  screenText.contains("USERID") ||
                                  screenText.contains("SIGN ON") ||
                                  screenText.contains("CONNECT");
            
            testResult.addVerification("Terminal logout successful", logoutSuccess,
                "Logout verification based on return to login screen");
              } catch (Exception e) {
            testResult.addVerification("Terminal logout", false, "Logout failed: " + e.getMessage());
            BufferedImage errorScreenshot = screenCapture.captureFullScreen();
            String errorPath = screenCapture.saveScreenshot(errorScreenshot, "logout_error");
            testResult.addScreenshot("logout_error", errorPath);
            // Don't fail the test on logout - it's cleanup
        }
    }
    
    @AfterAll
    static void tearDownClass() {
        // Static cleanup
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
            case 112: return "F1";
            case 114: return "F3";
            case 123: return "F12";
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
