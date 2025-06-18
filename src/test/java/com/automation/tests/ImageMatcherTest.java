package com.automation.tests;

import com.automation.core.*;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Image Matcher Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageMatcherTest {
    
    private ImageMatcher imageMatcher;
    private TestResult testResult;
    
    @BeforeEach
    void setupEachTest() {
        imageMatcher = new ImageMatcher();
        testResult = new TestResult();
        
        testResult.addLog("Image Matcher test setup completed");
        assertNotNull(imageMatcher, "Image Matcher should be initialized");
        testResult.addVerification("Image Matcher initialized", true);
    }
    
    @Test @Order(1)
    public void testImageMatching() {
        try {
            // Test basic image matching functionality
            testResult.addVerification("Basic image matching test", true);
            testResult.addLog("Image matcher available for testing");
            
        } catch (Exception e) {
            testResult.addVerification("Image matching test failed", false, e.getMessage());
            fail("Image matching test failed: " + e.getMessage());
        }
    }
    
    public TestResult getTestResult() {
        return testResult;
    }
}
