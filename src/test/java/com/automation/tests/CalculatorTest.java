package com.automation.tests;

import com.automation.core.*;
import com.automation.models.ManagedApplicationContext;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

/**
 * Production-ready Calculator Application Tests
 * Tests actual Windows Calculator application with comprehensive error handling
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalculatorTest {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculatorTest.class);
    
    private WindowController windowController;
    private ScreenCapture screenCapture;
    private OCREngine ocrEngine;
    private TestResult testResult;
    
    private String windowTitle;
    private boolean calculatorLaunched = false;
    
    @BeforeEach
    void setupCalculator() {
        windowController = new WindowController();
        screenCapture = new ScreenCapture();
        ocrEngine = new OCREngine();        
        testResult = new TestResult();
        
        testResult.addLog("Starting Calculator test setup with PID-driven approach");
          // Check if Calculator is already running using ProcessManager
        ManagedApplicationContext existingProcess = ProcessManager.getInstance().getRunningApplicationContext("calculator");
        if (existingProcess != null) {
            boolean focused = windowController.focusWindow(existingProcess);
            calculatorLaunched = true;
            testResult.addLog(String.format("Found existing Calculator process: PID %d, focus %s", 
                existingProcess.getProcessId(), focused ? "successful" : "failed"));
        } else {
            testResult.addLog("No existing Calculator found, attempting to launch...");
            ManagedApplicationContext launchedProcess = ProcessManager.getInstance().launchAndTrackApplication("calculator");
            if (launchedProcess != null) {
                calculatorLaunched = true;
                testResult.addLog(String.format("Successfully launched Calculator: PID %d", launchedProcess.getProcessId()));
            } else {
                calculatorLaunched = false;
                testResult.addLog("Failed to launch Calculator via ProcessManager");
            }
        }
        
        if (calculatorLaunched) {
            // Clear calculator state
            try {
                Thread.sleep(1000);
                windowController.sendKey("ESCAPE"); // Clear any dialogs
                Thread.sleep(500);
                windowController.sendKeyCombo("CTRL", "A"); // Select all
                Thread.sleep(300);
                windowController.sendKey("DELETE"); // Clear
                Thread.sleep(500);
                
                testResult.addVerification("Calculator setup successful", true);
                testResult.addTestData("window_title", windowTitle);
                
                // Capture initial state
                String initialScreenshot = captureTestScreenshot("calculator_initial_state");
                if (initialScreenshot != null) {
                    testResult.addScreenshot("initial_state", initialScreenshot);
                }
                
            } catch (Exception e) {
                testResult.addLog("Calculator clearing failed: " + e.getMessage());
            }
        } else {
            testResult.addVerification("Calculator setup failed", false, "Could not launch Calculator application");
            fail("Calculator application is not available for testing");
        }
    }
    
    /**
     * Legacy calculator launch method (deprecated - ProcessManager handles this now)
     * @deprecated Use ProcessManager.getInstance().launchAndTrack("calculator") instead
     */
    @Deprecated
    @SuppressWarnings("unused")
    private boolean launchCalculator(String[] commands) {
        for (String command : commands) {
            try {
                testResult.addLog("Attempting to launch Calculator with: " + command);
                
                // Method 1: Direct process execution
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.start();
                Thread.sleep(3000);
                
                // Check if Calculator window appeared
                String[] possibleTitles = {"Calculator", "計算機", "Calculadora"};
                for (String title : possibleTitles) {
                    if (windowController.findWindowByTitle(title, false)) {
                        windowTitle = title;
                        windowController.activateWindow();
                        testResult.addLog("Successfully launched Calculator: " + title);
                        return true;
                    }
                }
                
                // Method 2: Using Windows Run dialog
                windowController.sendKeyCombo("WINDOWS", "R");
                Thread.sleep(1000);
                windowController.sendText(command);
                windowController.sendKey("ENTER");
                Thread.sleep(3000);
                
                for (String title : possibleTitles) {
                    if (windowController.findWindowByTitle(title, false)) {
                        windowTitle = title;
                        windowController.activateWindow();
                        testResult.addLog("Successfully launched Calculator via Run dialog: " + title);
                        return true;
                    }
                }
                
            } catch (Exception e) {
                testResult.addLog("Failed to launch Calculator with " + command + ": " + e.getMessage());
            }
        }
        return false;
    }
    
    @Test @Order(1)
    @DisplayName("Test Calculator Basic Operations - Addition")
    public void testCalculatorAddition() {
        try {
            testResult.addLog("Starting Calculator addition test: 25 + 17 = 42");
            
            // Perform calculation: 25 + 17 = 42
            performCalculation("25", "+", "17", "=");
            
            // Wait for result
            Thread.sleep(1000);
            
            // Capture after state
            String afterScreenshot = captureTestScreenshot("after_addition");
            testResult.addScreenshot("after_addition", afterScreenshot);
            
            // Verify result using multiple methods
            String result = extractCalculatorDisplay();
            testResult.addOcrResult("addition_result", result);
            
            // Check for expected result (42)
            boolean resultCorrect = result.contains("42") || verifyResultByKeyboard("42");
            
            testResult.addVerification("Calculator addition test", resultCorrect, 
                "Expected: 42, OCR Result: " + result);
            
            if (resultCorrect) {
                testResult.addLog("✓ Addition test passed: 25 + 17 = 42");
            } else {
                testResult.addLog("✗ Addition test failed - Expected: 42, Got: " + result);
            }
            
        } catch (Exception e) {
            handleTestError("addition", e);
        } finally {
            clearCalculator();
        }
    }
    
    @Test @Order(2)
    @DisplayName("Test Calculator Basic Operations - Subtraction")
    public void testCalculatorSubtraction() {
        try {
            testResult.addLog("Starting Calculator subtraction test: 100 - 37 = 63");
            
            // Perform calculation: 100 - 37 = 63
            performCalculation("100", "-", "37", "=");
            Thread.sleep(1000);
            
            String result = extractCalculatorDisplay();
            testResult.addOcrResult("subtraction_result", result);
            
            boolean resultCorrect = result.contains("63") || verifyResultByKeyboard("63");
            testResult.addVerification("Calculator subtraction test", resultCorrect,
                "Expected: 63, OCR Result: " + result);
            
            String screenshot = captureTestScreenshot("after_subtraction");
            testResult.addScreenshot("subtraction_result", screenshot);
            
        } catch (Exception e) {
            handleTestError("subtraction", e);
        } finally {
            clearCalculator();
        }
    }
    
    @Test @Order(3)
    @DisplayName("Test Calculator Basic Operations - Multiplication")
    public void testCalculatorMultiplication() {
        try {
            testResult.addLog("Starting Calculator multiplication test: 12 * 8 = 96");
            
            // Perform calculation: 12 * 8 = 96
            performCalculation("12", "*", "8", "=");
            Thread.sleep(1000);
            
            String result = extractCalculatorDisplay();
            testResult.addOcrResult("multiplication_result", result);
            
            boolean resultCorrect = result.contains("96") || verifyResultByKeyboard("96");
            testResult.addVerification("Calculator multiplication test", resultCorrect,
                "Expected: 96, OCR Result: " + result);
                
            String screenshot = captureTestScreenshot("after_multiplication");
            testResult.addScreenshot("multiplication_result", screenshot);
            
        } catch (Exception e) {
            handleTestError("multiplication", e);
        } finally {
            clearCalculator();
        }
    }
    
    @Test @Order(4)
    @DisplayName("Test Calculator Basic Operations - Division")
    public void testCalculatorDivision() {
        try {
            testResult.addLog("Starting Calculator division test: 144 / 12 = 12");
            
            // Perform calculation: 144 / 12 = 12
            performCalculation("144", "/", "12", "=");
            Thread.sleep(1000);
            
            String result = extractCalculatorDisplay();
            testResult.addOcrResult("division_result", result);
            
            boolean resultCorrect = result.contains("12") || verifyResultByKeyboard("12");
            testResult.addVerification("Calculator division test", resultCorrect,
                "Expected: 12, OCR Result: " + result);
                
            String screenshot = captureTestScreenshot("after_division");
            testResult.addScreenshot("division_result", screenshot);
            
        } catch (Exception e) {
            handleTestError("division", e);
        } finally {
            clearCalculator();
        }
    }
    
    @Test @Order(5)
    @DisplayName("Test Calculator Memory Functions")
    public void testCalculatorMemoryFunctions() {
        try {
            testResult.addLog("Starting Calculator memory functions test");
            
            // Store 25 in memory
            performCalculation("25", "", "", "");
            windowController.sendKeyCombo("CTRL", "M"); // Memory Store
            Thread.sleep(500);
            
            clearCalculator();
            
            // Add 15 to memory
            performCalculation("15", "", "", "");
            windowController.sendKeyCombo("CTRL", "P"); // Memory Plus
            Thread.sleep(500);
            
            clearCalculator();
            
            // Recall memory (should be 40)
            windowController.sendKeyCombo("CTRL", "R"); // Memory Recall
            Thread.sleep(1000);
            
            String result = extractCalculatorDisplay();
            testResult.addOcrResult("memory_result", result);
            
            boolean resultCorrect = result.contains("40") || verifyResultByKeyboard("40");
            testResult.addVerification("Calculator memory functions test", resultCorrect,
                "Expected: 40, OCR Result: " + result);
                
            String screenshot = captureTestScreenshot("after_memory_test");
            testResult.addScreenshot("memory_result", screenshot);
            
        } catch (Exception e) {
            handleTestError("memory_functions", e);
        } finally {
            // Clear memory
            try {
                windowController.sendKeyCombo("CTRL", "L"); // Memory Clear
                Thread.sleep(300);
            } catch (Exception ignored) {}
            clearCalculator();
        }
    }
    
    @Test @Order(6)
    @DisplayName("Test Calculator Error Handling - Division by Zero")
    public void testCalculatorDivisionByZero() {
        try {
            testResult.addLog("Starting Calculator division by zero test: 10 / 0");
            
            // Perform calculation: 10 / 0
            performCalculation("10", "/", "0", "=");
            Thread.sleep(1500);
            
            String result = extractCalculatorDisplay();
            testResult.addOcrResult("division_by_zero_result", result);
            
            // Check for error message (different in different Calculator versions)
            boolean errorHandled = result.toLowerCase().contains("error") ||
                                 result.toLowerCase().contains("cannot") ||
                                 result.toLowerCase().contains("infinity") ||
                                 result.toLowerCase().contains("undefined") ||
                                 result.contains("∞");
            
            testResult.addVerification("Calculator division by zero error handling", errorHandled,
                "Expected error message, OCR Result: " + result);
                
            String screenshot = captureTestScreenshot("division_by_zero_error");
            testResult.addScreenshot("error_handling", screenshot);
            
        } catch (Exception e) {
            handleTestError("division_by_zero", e);
        } finally {
            clearCalculator();
        }
    }
    
    // Helper Methods
    
    private void performCalculation(String num1, String operator, String num2, String action) {
        try {
            if (!num1.isEmpty()) {
                for (char c : num1.toCharArray()) {
                    windowController.sendKey(String.valueOf(c));
                    Thread.sleep(100);
                }
            }
            
            if (!operator.isEmpty()) {
                switch (operator) {
                    case "+":
                        windowController.sendKey("PLUS");
                        break;
                    case "-":
                        windowController.sendKey("MINUS");
                        break;
                    case "*":
                        windowController.sendKey("MULTIPLY");
                        break;
                    case "/":
                        windowController.sendKey("DIVIDE");
                        break;
                }
                Thread.sleep(200);
            }
            
            if (!num2.isEmpty()) {
                for (char c : num2.toCharArray()) {
                    windowController.sendKey(String.valueOf(c));
                    Thread.sleep(100);
                }
            }
            
            if (!action.isEmpty() && action.equals("=")) {
                windowController.sendKey("ENTER");
                Thread.sleep(300);
            }
            
        } catch (Exception e) {
            testResult.addLog("Error performing calculation: " + e.getMessage());
        }
    }
    
    private String extractCalculatorDisplay() {
        try {
            // Capture focused window
            BufferedImage screenshot = screenCapture.captureFullScreen();
            
            if (screenshot != null) {
                // Try OCR on the full calculator window
                String fullText = ocrEngine.extractText(screenshot);
                testResult.addLog("Full Calculator OCR: " + fullText);
                
                // Extract numbers from the OCR result
                String[] lines = fullText.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.matches(".*\\d+.*") && !line.contains("=") && !line.contains("+") && !line.contains("-")) {
                        return line.replaceAll("[^0-9.-]", "");
                    }
                }
                
                return fullText.replaceAll("[^0-9.-]", "");
            }
            
        } catch (Exception e) {
            testResult.addLog("OCR extraction failed: " + e.getMessage());
        }
        
        return "";
    }
    
    private boolean verifyResultByKeyboard(String expected) {
        try {
            // Alternative verification: copy the result and check clipboard
            windowController.sendKeyCombo("CTRL", "C");
            Thread.sleep(500);
            
            // This would require clipboard access implementation
            // For now, return false to rely on OCR
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private void clearCalculator() {
        try {
            windowController.sendKey("ESCAPE");
            Thread.sleep(200);
            windowController.sendKey("C");
            Thread.sleep(300);
        } catch (Exception e) {
            testResult.addLog("Failed to clear calculator: " + e.getMessage());
        }
    }
    
    private String captureTestScreenshot(String description) {
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            if (screenshot != null) {
                return screenCapture.saveScreenshot(screenshot, "calculator_" + description);
            }
        } catch (Exception e) {
            testResult.addLog("Failed to capture screenshot: " + e.getMessage());
        }
        return null;
    }
    
    private void handleTestError(String testName, Exception e) {
        testResult.addVerification(testName + " test failed", false, e.getMessage());
        String errorScreenshot = captureTestScreenshot(testName + "_error");
        if (errorScreenshot != null) {
            testResult.addScreenshot(testName + "_error", errorScreenshot);
        }
        testResult.addLog("Test error in " + testName + ": " + e.getMessage());
    }
    
    @AfterEach
    public void cleanupAfterTest() {
        clearCalculator();
    }
      @AfterAll
    public static void tearDownClass() {
        // Close Calculator application after all tests complete
        try {
            ManagedApplicationContext calcContext = ProcessManager.getInstance()
                .getRunningApplicationContext("calculator");
            
            if (calcContext != null) {                logger.info("Terminating Calculator application (PID: {})", calcContext.getProcessId());
                boolean terminated = ProcessManager.getInstance().terminateApplication("calculator");
                if (terminated) {
                    logger.info("Calculator application terminated successfully");
                } else {
                    logger.warn("Failed to terminate Calculator application");
                }
            } else {
                logger.info("Calculator application was already terminated");
            }
        } catch (Exception e) {
            logger.warn("Error during Calculator termination: {}", e.getMessage());
        }
    }
    
    public TestResult getTestResult() {
        return testResult;
    }
}
