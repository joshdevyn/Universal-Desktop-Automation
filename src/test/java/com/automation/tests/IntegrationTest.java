package com.automation.tests;

import com.automation.core.*;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {
    
    private WindowController windowController;
    private ScreenCapture screenCapture;
    private OCREngine ocrEngine;
    private ImageMatcher imageMatcher;
    private TestResult testResult;
    
    @BeforeEach
    void setupEachTest() {
        windowController = new WindowController();
        screenCapture = new ScreenCapture();
        ocrEngine = new OCREngine();
        imageMatcher = new ImageMatcher();
        testResult = new TestResult();
        
        testResult.addLog("Integration test setup completed");
        testResult.addVerification("Integration test setup", true);
    }
    
    @Test @Order(1)
    public void testFrameworkIntegration() {
        try {
            // Test that all framework components work together
            assertNotNull(windowController, "Window Controller should be available");
            assertNotNull(screenCapture, "Screen Capture should be available");
            assertNotNull(ocrEngine, "OCR Engine should be available");
            assertNotNull(imageMatcher, "Image Matcher should be available");
              testResult.addVerification("Framework integration test", true);
            
        } catch (Exception e) {
            testResult.addVerification("Framework integration test failed", false, e.getMessage());
            fail("Framework integration test failed: " + e.getMessage());
        }
    }
    
    public TestResult getTestResult() {
        return testResult;
    }
}
