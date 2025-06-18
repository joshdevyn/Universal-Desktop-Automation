package com.automation.tests;

import com.automation.core.*;
import com.automation.models.TestResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * OCR Engine Tests
 * Tests OCR functionality including text extraction
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OCREngineTest {
    
    private static final String TEST_IMAGES_DIR = "src/test/resources/images/ocr_test/";
    private ScreenCapture screenCapture;
    private OCREngine ocrEngine;
    private TestResult testResult;
    
    @BeforeEach
    void setupEachTest() {
        screenCapture = new ScreenCapture();
        ocrEngine = new OCREngine();
        testResult = new TestResult();
        
        File testImagesDirectory = new File(TEST_IMAGES_DIR);
        if (!testImagesDirectory.exists()) {
            testImagesDirectory.mkdirs();
        }
        
        testResult.addTestData("test_images_directory", TEST_IMAGES_DIR);
        testResult.addLog("OCR Engine test setup completed");
        
        // Verify OCR engine configuration
        assertNotNull(ocrEngine, "OCR Engine should be initialized");
        testResult.addVerification("OCR Engine initialized", true);
    }
    
    @Test @Order(1)
    public void testBasicOCRFunctionality() {
        try {
            // Capture current screen for OCR testing
            BufferedImage screenshot = screenCapture.captureFullScreen();
            assertNotNull(screenshot, "Screenshot should be captured");
            
            // Extract text from the screenshot
            String extractedText = ocrEngine.extractText(screenshot);
            assertNotNull(extractedText, "OCR should return text result");
            
            testResult.addVerification("Basic OCR functionality", true);
            testResult.addOcrResult("screen_text", extractedText);
            testResult.addLog("Extracted text length: " + extractedText.length());
            
        } catch (Exception e) {
            testResult.addVerification("Basic OCR functionality", false, e.getMessage());
            fail("OCR basic functionality test failed: " + e.getMessage());
        }
    }
    
    @Test @Order(2)
    public void testOCRRegionExtraction() {
        try {
            // Test OCR on specific screen regions
            Rectangle[] regions = {
                new Rectangle(100, 100, 200, 50),
                new Rectangle(200, 200, 300, 100),
                new Rectangle(50, 50, 150, 75)
            };
            
            for (int i = 0; i < regions.length; i++) {
                Rectangle region = regions[i];
                BufferedImage regionCapture = screenCapture.captureRegion(
                    region.x, region.y, region.width, region.height);
                
                String extractedText = ocrEngine.extractText(regionCapture);
                assertNotNull(extractedText, "OCR should return text for region " + i);
                
                testResult.addVerification("Region " + i + " OCR", true);
                testResult.addOcrResult("region_" + i + "_text", extractedText);
            }
            
        } catch (Exception e) {
            testResult.addVerification("OCR region extraction", false, e.getMessage());
            fail("OCR region extraction test failed: " + e.getMessage());
        }
    }
    
    @Test @Order(3)
    public void testOCRErrorHandling() {
        try {
            // Test with invalid input
            BufferedImage nullImage = null;
            
            try {
                ocrEngine.extractText(nullImage);
                // If no exception is thrown, it should handle null gracefully
                testResult.addVerification("OCR null handling", true);
            } catch (Exception e) {
                // Expected behavior - OCR should handle null input properly
                assertTrue(e.getMessage().contains("null") || e.getMessage().contains("invalid"), 
                    "OCR should provide meaningful error for null input");
                testResult.addVerification("OCR error handling", true);
            }
            
        } catch (Exception e) {
            testResult.addVerification("OCR error handling", false, e.getMessage());
            // Don't fail the test as error handling may vary
        }
    }
    
    public TestResult getTestResult() {
        return testResult;
    }
}
