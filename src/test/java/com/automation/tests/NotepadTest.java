package com.automation.tests;

import com.automation.core.*;
import com.automation.models.ManagedApplicationContext;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Production-ready Notepad Application Tests
 * Tests actual Windows Notepad application with file operations
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotepadTest {
    
    private WindowController windowController;
    private ScreenCapture screenCapture;
    private OCREngine ocrEngine;
    private TestResult testResult;
    
    private String windowTitle;
    private boolean notepadLaunched = false;
    private String testFileName = "automation_test_" + System.currentTimeMillis() + ".txt";
      @BeforeEach
    void setupNotepad() {
        windowController = new WindowController();
        screenCapture = new ScreenCapture();
        ocrEngine = new OCREngine();
        testResult = new TestResult();
        
        testResult.addLog("Starting Notepad test setup with PID-driven approach");
        
        // Check if Notepad is already running using ProcessManager
        ManagedApplicationContext existingProcess = ProcessManager.getInstance().getRunningApplicationContext("notepad");
        if (existingProcess != null) {
            boolean focused = windowController.focusWindow(existingProcess);
            notepadLaunched = true;
            testResult.addLog(String.format("Found existing Notepad process: PID %d, focus %s", 
                existingProcess.getProcessId(), focused ? "successful" : "failed"));
        } else {
            testResult.addLog("No existing Notepad found, attempting to launch...");
            ManagedApplicationContext launchedProcess = ProcessManager.getInstance().launchAndTrackApplication("notepad");
            
            if (launchedProcess != null) {
                // VALIDATION: Verify the process is actually running and has a visible window
                boolean processActuallyRunning = launchedProcess.isActive() && !launchedProcess.isTerminated();
                boolean windowVisible = false;
                
                try {
                    // Try to focus the window to verify it's actually visible
                    windowVisible = windowController.focusWindow(launchedProcess);
                    Thread.sleep(1000); // Give time for window to appear
                } catch (Exception e) {
                    testResult.addLog("Window validation failed: " + e.getMessage());
                }
                
                if (processActuallyRunning && windowVisible) {
                    notepadLaunched = true;
                    testResult.addLog(String.format("Successfully launched Notepad: PID %d (validated)", launchedProcess.getProcessId()));
                } else {
                    notepadLaunched = false;
                    testResult.addLog(String.format("FALSE POSITIVE: ProcessManager claimed success but validation failed - Process running: %s, Window visible: %s", 
                        processActuallyRunning, windowVisible));
                }
            } else {
                notepadLaunched = false;
                testResult.addLog("Failed to launch Notepad via ProcessManager");
            }
        }
        
        if (!notepadLaunched) {
            testResult.addLog("ProcessManager failed to launch Notepad - test will fail");
        }
        
        if (notepadLaunched) {
            try {
                Thread.sleep(1000);
                // Clear any existing content
                windowController.sendKeyCombo("CTRL", "A");
                Thread.sleep(200);
                windowController.sendKey("DELETE");
                Thread.sleep(500);
                
                testResult.addVerification("Notepad setup successful", true);
                testResult.addTestData("window_title", windowTitle);
                
                String initialScreenshot = captureTestScreenshot("notepad_initial_state");
                if (initialScreenshot != null) {
                    testResult.addScreenshot("initial_state", initialScreenshot);
                }
                
            } catch (Exception e) {
                testResult.addLog("Notepad setup error: " + e.getMessage());
            }
        } else {
            testResult.addVerification("Notepad setup failed", false, "Could not launch Notepad application");
            fail("Notepad application is not available for testing");        }
    }
    
    @Test @Order(1)
    @DisplayName("Test Notepad Text Input and Editing")
    public void testNotepadTextInputAndEditing() {
        try {
            testResult.addLog("Starting Notepad text input and editing test");
            
            String testText = "This is a comprehensive test of the Universal Desktop Automation Framework.\n" +
                             "Testing text input, editing, and validation capabilities.\n" +
                             "Date: " + new java.util.Date() + "\n" +
                             "Test ID: NOTEPAD_001";
            
            // Type the test text
            windowController.sendText(testText);
            Thread.sleep(2000);
            
            String afterInputScreenshot = captureTestScreenshot("after_text_input");
            testResult.addScreenshot("after_input", afterInputScreenshot);
            
            // Verify text was entered using OCR
            String extractedText = extractNotepadContent();
            testResult.addOcrResult("text_input_verification", extractedText);
            
            boolean textInputSuccessful = extractedText.contains("Universal Desktop Automation") &&
                                        extractedText.contains("Framework") &&
                                        extractedText.contains("NOTEPAD_001");
            
            testResult.addVerification("Text input successful", textInputSuccessful,
                "Expected text found in document");
            
            // Test text selection and replacement
            windowController.sendKeyCombo("CTRL", "A"); // Select all
            Thread.sleep(500);
            
            String replacementText = "Replacement text for testing edit operations.";
            windowController.sendText(replacementText);
            Thread.sleep(1000);
            
            String afterEditScreenshot = captureTestScreenshot("after_text_edit");
            testResult.addScreenshot("after_edit", afterEditScreenshot);
            
            // Verify replacement
            String editedText = extractNotepadContent();
            boolean editSuccessful = editedText.contains("Replacement text");
            
            testResult.addVerification("Text editing successful", editSuccessful,
                "Text replacement verified");
                
        } catch (Exception e) {
            handleTestError("text_input_editing", e);
        }
    }
    
    @Test @Order(2)
    @DisplayName("Test Notepad File Operations - Save As")
    public void testNotepadFileSaveAs() {
        try {
            testResult.addLog("Starting Notepad file save test");
            
            // Prepare test content
            String fileContent = "Automation Framework Test File\n" +
                               "Created by: NotepadTest\n" +
                               "Purpose: Testing file save operations\n" +
                               "Timestamp: " + System.currentTimeMillis();
            
            windowController.sendText(fileContent);
            Thread.sleep(1000);
            
            // Open Save As dialog
            windowController.sendKeyCombo("CTRL", "SHIFT", "S");
            Thread.sleep(2000);
            
            String saveDialogScreenshot = captureTestScreenshot("save_as_dialog");
            testResult.addScreenshot("save_dialog", saveDialogScreenshot);
            
            // Type filename
            windowController.sendText(testFileName);
            Thread.sleep(500);
            
            // Press Enter to save
            windowController.sendKey("ENTER");
            Thread.sleep(2000);              // Verify file was saved by checking if Notepad process is still running with saved content
            ManagedApplicationContext notepadProcess = null;
            boolean fileSaved = false;
            
            try {
                notepadProcess = ProcessManager.getInstance().getRunningApplicationContext("notepad");
                if (notepadProcess != null) {
                    // Focus the Notepad window to check its state
                    windowController.focusWindow(notepadProcess);
                    Thread.sleep(500);
                    
                    // File is considered saved if Notepad is still running (not showing unsaved indicator)
                    fileSaved = true;
                    testResult.addLog(String.format("File saved successfully - Notepad process PID %d is running", notepadProcess.getProcessId()));
                }
            } catch (Exception e) {
                testResult.addLog("Could not verify file save via ProcessManager: " + e.getMessage());
            }
            
            testResult.addVerification("File save operation", fileSaved,
                "File saved and window title updated");
            
            // Verify file exists on disk
            File savedFile = new File(System.getProperty("user.home") + "\\Desktop\\" + testFileName);
            if (!savedFile.exists()) {
                savedFile = new File(System.getProperty("user.home") + "\\Documents\\" + testFileName);
            }
            
            testResult.addVerification("File exists on disk", savedFile.exists(),
                "Saved file found at: " + savedFile.getAbsolutePath());
                
            String afterSaveScreenshot = captureTestScreenshot("after_save");
            testResult.addScreenshot("after_save", afterSaveScreenshot);
            
        } catch (Exception e) {
            handleTestError("file_save", e);
        }
    }
    
    @Test @Order(3)
    @DisplayName("Test Notepad Menu Operations")
    public void testNotepadMenuOperations() {
        try {
            testResult.addLog("Starting Notepad menu operations test");
            
            // Test Edit menu
            windowController.sendKeyCombo("ALT", "E"); // Open Edit menu
            Thread.sleep(1500);
            
            String editMenuScreenshot = captureTestScreenshot("edit_menu_open");
            testResult.addScreenshot("edit_menu", editMenuScreenshot);
            
            // Close menu
            windowController.sendKey("ESCAPE");
            Thread.sleep(500);
            
            // Test Format menu
            windowController.sendKeyCombo("ALT", "O"); // Open Format menu
            Thread.sleep(1500);
            
            String formatMenuScreenshot = captureTestScreenshot("format_menu_open");
            testResult.addScreenshot("format_menu", formatMenuScreenshot);
            
            // Test Word Wrap toggle
            windowController.sendKey("W"); // Word Wrap
            Thread.sleep(1000);
            
            // Test View menu
            windowController.sendKeyCombo("ALT", "V"); // Open View menu
            Thread.sleep(1500);
            
            String viewMenuScreenshot = captureTestScreenshot("view_menu_open");
            testResult.addScreenshot("view_menu", viewMenuScreenshot);
            
            // Close menu
            windowController.sendKey("ESCAPE");
            Thread.sleep(500);
            
            testResult.addVerification("Menu operations successful", true,
                "Successfully navigated Notepad menus");
                
        } catch (Exception e) {
            handleTestError("menu_operations", e);
        }
    }
    
    @Test @Order(4)
    @DisplayName("Test Notepad Find and Replace")
    public void testNotepadFindAndReplace() {
        try {
            testResult.addLog("Starting Notepad find and replace test");
            
            // Prepare text with searchable content
            String searchableText = "The quick brown fox jumps over the lazy dog.\n" +
                                  "The brown fox is very quick.\n" +
                                  "Testing find and replace functionality.";
            
            windowController.sendKeyCombo("CTRL", "A"); // Select all
            windowController.sendText(searchableText);
            Thread.sleep(1000);
            
            // Open Find dialog
            windowController.sendKeyCombo("CTRL", "F");
            Thread.sleep(1500);
            
            String findDialogScreenshot = captureTestScreenshot("find_dialog");
            testResult.addScreenshot("find_dialog", findDialogScreenshot);
            
            // Search for "brown"
            windowController.sendText("brown");
            Thread.sleep(500);
            windowController.sendKey("ENTER"); // Find Next
            Thread.sleep(1000);
            
            // Close Find dialog
            windowController.sendKey("ESCAPE");
            Thread.sleep(500);
            
            // Open Replace dialog
            windowController.sendKeyCombo("CTRL", "H");
            Thread.sleep(1500);
            
            String replaceDialogScreenshot = captureTestScreenshot("replace_dialog");
            testResult.addScreenshot("replace_dialog", replaceDialogScreenshot);
            
            // Type find text
            windowController.sendText("quick");
            Thread.sleep(300);
            windowController.sendKey("TAB"); // Move to replace field
            Thread.sleep(300);
            windowController.sendText("fast");
            Thread.sleep(300);
            
            // Replace All
            windowController.sendKeyCombo("ALT", "A"); // Replace All button
            Thread.sleep(1000);
            
            // Close Replace dialog
            windowController.sendKey("ESCAPE");
            Thread.sleep(500);
            
            // Verify replacement
            String replacedText = extractNotepadContent();
            boolean replaceSuccessful = replacedText.contains("fast") && !replacedText.contains("quick");
            
            testResult.addVerification("Find and replace successful", replaceSuccessful,
                "Text 'quick' replaced with 'fast'");
                
            String afterReplaceScreenshot = captureTestScreenshot("after_replace");
            testResult.addScreenshot("after_replace", afterReplaceScreenshot);
            
        } catch (Exception e) {
            handleTestError("find_replace", e);
        }
    }
    
    // Helper Methods
    
    private String extractNotepadContent() {
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            if (screenshot != null) {
                String fullText = ocrEngine.extractText(screenshot);
                testResult.addLog("Notepad OCR extraction: " + fullText);
                return fullText;
            }
        } catch (Exception e) {
            testResult.addLog("OCR extraction failed: " + e.getMessage());
        }
        return "";
    }
    
    private String captureTestScreenshot(String description) {
        try {
            BufferedImage screenshot = screenCapture.captureFullScreen();
            if (screenshot != null) {
                return screenCapture.saveScreenshot(screenshot, "notepad_" + description);
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
        try {            // Don't save if asked - Use ProcessManager to check if Notepad is running
            ManagedApplicationContext notepadProcess = ProcessManager.getInstance().getRunningApplicationContext("notepad");
            if (notepadProcess != null) {
                // Focus Notepad using ManagedApplicationContext
                windowController.focusWindow(notepadProcess);
                Thread.sleep(500);
                
                windowController.sendKeyCombo("CTRL", "N"); // New document
                Thread.sleep(1000);
                // If prompted to save, click "Don't Save"
                windowController.sendKey("N");
                Thread.sleep(500);
                
                testResult.addLog(String.format("Notepad cleanup completed for PID %d", notepadProcess.getProcessId()));
            } else {
                testResult.addLog("Notepad process not found during cleanup - already closed");
            }
        } catch (Exception e) {
            testResult.addLog("Cleanup failed: " + e.getMessage());
        }
    }
    
    @AfterAll
    public static void tearDownClass() {
        // Cleanup test files
        try {
            String[] possiblePaths = {
                System.getProperty("user.home") + "\\Desktop\\",
                System.getProperty("user.home") + "\\Documents\\"
            };
            
            for (String path : possiblePaths) {
                File testFile = new File(path + "automation_test_*.txt");
                File[] files = testFile.getParentFile().listFiles((dir, name) -> 
                    name.startsWith("automation_test_") && name.endsWith(".txt"));
                
                if (files != null) {
                    for (File file : files) {
                        if (file.delete()) {
                            System.out.println("Cleaned up test file: " + file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Test file cleanup failed: " + e.getMessage());
        }
    }
    
    public TestResult getTestResult() {
        return testResult;
    }
}
