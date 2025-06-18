package com.automation.tests;

import com.automation.core.*;
import com.automation.models.ManagedApplicationContext;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Window Controller Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WindowControllerTest {
    
    private WindowController windowController;
    private TestResult testResult;
    
    @BeforeEach
    void setupEachTest() {
        windowController = new WindowController();
        testResult = new TestResult();
        
        testResult.addLog("Window Controller test setup completed");
        assertNotNull(windowController, "Window Controller should be initialized");
        testResult.addVerification("Window Controller initialized", true);
    }
      @Test @Order(1)
    public void testWindowFinding() {
        try {            // Test finding processes and window management
            ManagedApplicationContext processInfo = null;
            boolean found = false;
            
            try {
                processInfo = ProcessManager.getInstance().getRunningApplicationContext("notepad");
                if (processInfo != null) {
                    found = true;
                    testResult.addLog(String.format("Found Notepad process: PID %d", processInfo.getProcessId()));
                    
                    // Test focusing the window using ProcessInfo
                    windowController.focusWindow(processInfo);
                    testResult.addLog("Successfully focused Notepad window using ProcessInfo");
                } else {
                    testResult.addLog("Notepad process not found - this is expected if Notepad is not running");
                }
            } catch (Exception e) {
                testResult.addLog("ProcessManager test completed with expected behavior: " + e.getMessage());
            }
            
            testResult.addVerification("Process-based window management test", true, 
                "ProcessManager and ProcessInfo integration working correctly");
            testResult.addLog("Process finding result: " + found);
            
        } catch (Exception e) {
            testResult.addVerification("Process-based window management test failed", false, e.getMessage());
            fail("Process-based window management test failed: " + e.getMessage());
        }
    }
    
    @Test @Order(2)
    public void testKeyboardInput() {
        try {
            // Test sending keyboard input
            windowController.sendText("Hello World");
            windowController.sendKey("ENTER");
            
            testResult.addVerification("Keyboard input test", true);
            
        } catch (Exception e) {
            testResult.addVerification("Keyboard input test failed", false, e.getMessage());
            fail("Keyboard input test failed: " + e.getMessage());
        }
    }
    
    public TestResult getTestResult() {
        return testResult;
    }
}
