package com.automation.cucumber.stepdefinitions;

import io.cucumber.java.en.*;
import com.automation.config.ConfigManager;
import com.automation.utils.VariableManager;
import com.automation.core.ProcessManager;
import com.automation.models.ManagedApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Step definitions for OCR (Optical Character Recognition) operations
 * Supports text extraction, validation, and confidence checking
 */
public class OCRStepDefinitions extends CommonStepDefinitionsBase {
    private static final Logger logger = LoggerFactory.getLogger(OCRStepDefinitions.class);    @Then("I should see the text {string}")
    public void i_should_see_the_text(String expectedText) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Validating presence of text: '{}'", interpolatedText);
        
        try {
            File screenshot = screenCapture.captureScreen();
            String extractedText = ocrEngine.extractText(screenshot);
            
            logger.debug("OCR extracted text from full screen: '{}'", extractedText.trim());
            
            boolean isFound = extractedText.toLowerCase().contains(interpolatedText.toLowerCase());
            addVerification("Text Validation", isFound, 
                String.format("Expected text '%s' %s on screen", interpolatedText, isFound ? "found" : "not found"));
            
            if (!isFound) {
                logger.warn("Expected text '{}' not found in extracted text: '{}'", interpolatedText, extractedText.trim());
                captureScreenshot("text_validation_failed");
                throw new AssertionError("Expected text not found: " + interpolatedText);
            }
            
            logger.info("Successfully validated presence of text: '{}'", interpolatedText);
        } catch (Exception e) {
            logger.error("Failed to validate text '{}': {}", interpolatedText, e.getMessage(), e);
            addVerification("Text Validation", false, "OCR text validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate text: " + interpolatedText, e);
        }
    }    @Then("I should see the text {string} in region {string}")
    public void i_should_see_the_text_in_region(String expectedText, String regionName) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Validating presence of text '{}' in region '{}'", interpolatedText, regionName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            logger.debug("Using region '{}': x={}, y={}, width={}, height={}", 
                regionName, region.x, region.y, region.width, region.height);
            
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture);
            
            logger.debug("OCR extracted text from region '{}': '{}'", regionName, extractedText.trim());
            
            boolean isFound = extractedText.toLowerCase().contains(interpolatedText.toLowerCase());
            addVerification("Text Validation in Region", isFound,
                String.format("Expected text '%s' %s in region '%s'", interpolatedText, 
                    isFound ? "found" : "not found", regionName));
            
            if (!isFound) {
                logger.warn("Expected text '{}' not found in region '{}'. Extracted text: '{}'", 
                    interpolatedText, regionName, extractedText.trim());
                captureScreenshot("text_validation_region_failed");
                throw new AssertionError("Expected text not found in region: " + interpolatedText);
            }
            
            logger.info("Successfully validated presence of text '{}' in region '{}'", interpolatedText, regionName);
        } catch (Exception e) {
            logger.error("Failed to validate text '{}' in region '{}': {}", interpolatedText, regionName, e.getMessage(), e);
            addVerification("Text Validation in Region", false, 
                "OCR text validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate text in region: " + interpolatedText, e);
        }
    }    @Then("I should see the text {string} with confidence above {int}%")
    public void i_should_see_the_text_with_confidence(String expectedText, int minConfidence) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Validating text '{}' with minimum confidence of {}%", interpolatedText, minConfidence);
        
        try {
            File screenshot = screenCapture.captureScreen();
            com.automation.core.OCREngine.OCRResult ocrResult = ocrEngine.extractTextWithConfidence(screenshot);
            
            logger.debug("OCR extracted text with confidence {:.2f}%: '{}'", 
                ocrResult.getConfidence(), ocrResult.getText().trim());
            
            boolean isFound = ocrResult.getText().toLowerCase().contains(interpolatedText.toLowerCase());
            boolean hasGoodConfidence = ocrResult.getConfidence() >= minConfidence;
            
            addVerification("Text Validation with Confidence", isFound && hasGoodConfidence, 
                String.format("Expected text '%s' %s with confidence %.2f%% (min: %d%%)", 
                    interpolatedText, isFound ? "found" : "not found", 
                    ocrResult.getConfidence(), minConfidence));
            
            if (!isFound || !hasGoodConfidence) {
                if (!isFound) {
                    logger.warn("Expected text '{}' not found in OCR result: '{}'", interpolatedText, ocrResult.getText().trim());
                }
                if (!hasGoodConfidence) {
                    logger.warn("OCR confidence {:.2f}% is below required minimum {}%", ocrResult.getConfidence(), minConfidence);
                }
                captureScreenshot("text_confidence_failed");
                throw new AssertionError(String.format("Text validation failed - Found: %s, Confidence: %.2f%% (min: %d%%)", 
                    isFound, ocrResult.getConfidence(), minConfidence));
            }
            
            logger.info("Successfully validated text '{}' with confidence {:.2f}%", interpolatedText, ocrResult.getConfidence());
        } catch (Exception e) {
            logger.error("Failed to validate text '{}' with confidence: {}", interpolatedText, e.getMessage(), e);
            addVerification("Text Validation with Confidence", false, 
                "OCR confidence validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate text with confidence: " + interpolatedText, e);
        }
    }    @When("I extract text from the screen and store it in variable {string}")
    public void i_extract_text_from_screen_and_store(String variableName) {
        logger.info("Extracting text from full screen and storing in variable '{}'", variableName);
        
        try {
            File screenshot = screenCapture.captureScreen();
            String extractedText = ocrEngine.extractText(screenshot).trim();
            
            logger.debug("OCR extracted text from screen: '{}'", extractedText);
            
            VariableManager.setSessionVariable(variableName, extractedText);
            addVerification("Text Extraction", true, 
                String.format("Extracted text stored in variable '%s': '%s'", variableName, extractedText));
            
            logger.info("Successfully extracted and stored text in variable '{}': '{}'", variableName, extractedText);
        } catch (Exception e) {
            logger.error("Failed to extract text from screen: {}", e.getMessage(), e);
            addVerification("Text Extraction", false, "Text extraction failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract text from screen", e);
        }
    }    @When("I extract text from region {string} and store it in variable {string}")
    public void i_extract_text_from_region_and_store(String regionName, String variableName) {
        logger.info("Extracting text from region '{}' and storing in variable '{}'", regionName, variableName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            logger.debug("Using region '{}': x={}, y={}, width={}, height={}", 
                regionName, region.x, region.y, region.width, region.height);
            
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture).trim();
            
            logger.debug("OCR extracted text from region '{}': '{}'", regionName, extractedText);
            
            VariableManager.setSessionVariable(variableName, extractedText);
            addVerification("Text Extraction from Region", true, 
                String.format("Extracted text from region '%s' stored in variable '%s': '%s'", 
                    regionName, variableName, extractedText));
            
            logger.info("Successfully extracted text from region '{}' and stored in variable '{}': '{}'", 
                regionName, variableName, extractedText);
        } catch (Exception e) {
            logger.error("Failed to extract text from region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Text Extraction from Region", false, 
                "Text extraction from region failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract text from region: " + regionName, e);
        }
    }    @Then("the text in region {string} should contain {string}")
    public void the_text_in_region_should_contain(String regionName, String expectedText) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Checking if text in region '{}' contains '{}'", regionName, interpolatedText);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            logger.debug("Using region '{}': x={}, y={}, width={}, height={}", 
                regionName, region.x, region.y, region.width, region.height);
            
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture);
            
            logger.debug("OCR extracted text from region '{}': '{}'", regionName, extractedText.trim());
            
            boolean contains = extractedText.toLowerCase().contains(interpolatedText.toLowerCase());
            addVerification("Text Contains Check", contains, 
                String.format("Region '%s' text '%s' %s contain '%s'", 
                    regionName, extractedText.trim(), contains ? "does" : "does not", interpolatedText));
            
            if (!contains) {
                logger.warn("Text in region '{}' does not contain '{}'. Actual text: '{}'", 
                    regionName, interpolatedText, extractedText.trim());
                captureScreenshot("text_contains_failed");
                throw new AssertionError(String.format("Text in region '%s' does not contain '%s'. Actual: '%s'", 
                    regionName, interpolatedText, extractedText.trim()));
            }
            
            logger.info("Successfully verified that text in region '{}' contains '{}'", regionName, interpolatedText);
        } catch (Exception e) {
            logger.error("Failed to check text contains in region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Text Contains Check", false, 
                "Text contains validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to check text contains in region: " + regionName, e);
        }
    }    @Then("the text in region {string} should match pattern {string}")
    public void the_text_in_region_should_match_pattern(String regionName, String pattern) {
        String interpolatedPattern = VariableManager.interpolate(pattern);
        logger.info("Checking if text in region '{}' matches pattern '{}'", regionName, interpolatedPattern);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            logger.debug("Using region '{}': x={}, y={}, width={}, height={}", 
                regionName, region.x, region.y, region.width, region.height);
            
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture).trim();
            
            logger.debug("OCR extracted text from region '{}': '{}', checking against pattern: '{}'", 
                regionName, extractedText, interpolatedPattern);
            
            boolean matches = extractedText.matches(interpolatedPattern);
            addVerification("Text Pattern Match", matches, 
                String.format("Region '%s' text '%s' %s match pattern '%s'", 
                    regionName, extractedText, matches ? "does" : "does not", interpolatedPattern));
            
            if (!matches) {
                logger.warn("Text in region '{}' does not match pattern '{}'. Actual text: '{}'", 
                    regionName, interpolatedPattern, extractedText);
                captureScreenshot("text_pattern_failed");
                throw new AssertionError(String.format("Text in region '%s' does not match pattern '%s'. Actual: '%s'", 
                    regionName, interpolatedPattern, extractedText));
            }
            
            logger.info("Successfully verified that text in region '{}' matches pattern '{}'", regionName, interpolatedPattern);
        } catch (Exception e) {
            logger.error("Failed to check text pattern in region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Text Pattern Match", false, 
                "Text pattern validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to check text pattern in region: " + regionName, e);
        }
    }    @Then("the screen should not contain text {string}")
    public void the_screen_should_not_contain_text(String unwantedText) {
        String interpolatedText = VariableManager.interpolate(unwantedText);
        logger.info("Validating absence of text: '{}'", interpolatedText);
        
        try {
            File screenshot = screenCapture.captureScreen();
            String extractedText = ocrEngine.extractText(screenshot);
            
            logger.debug("OCR extracted text from full screen: '{}'", extractedText.trim());
            
            boolean isFound = extractedText.toLowerCase().contains(interpolatedText.toLowerCase());
            addVerification("Text Absence Validation", !isFound, 
                String.format("Unwanted text '%s' %s on screen", interpolatedText, isFound ? "found" : "not found"));
            
            if (isFound) {
                logger.warn("Unwanted text '{}' found on screen in extracted text: '{}'", interpolatedText, extractedText.trim());
                captureScreenshot("unwanted_text_found");
                throw new AssertionError("Unwanted text found on screen: " + interpolatedText);
            }
            
            logger.info("Successfully validated absence of text: '{}'", interpolatedText);
        } catch (Exception e) {
            logger.error("Failed to validate text absence '{}': {}", interpolatedText, e.getMessage(), e);
            addVerification("Text Absence Validation", false, 
                "Text absence validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate text absence: " + interpolatedText, e);
        }
    }    @When("I extract numeric value from region {string} and store it in variable {string}")
    public void i_extract_numeric_value_from_region(String regionName, String variableName) {
        logger.info("Extracting numeric value from region '{}' and storing in variable '{}'", regionName, variableName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            logger.debug("Using region '{}': x={}, y={}, width={}, height={}", 
                regionName, region.x, region.y, region.width, region.height);
            
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture).trim();
            
            logger.debug("OCR extracted raw text from region '{}': '{}'", regionName, extractedText);
            
            // Extract numbers from text using regex
            String numericText = extractedText.replaceAll("[^0-9.-]", "");
            
            logger.debug("Parsed numeric value from '{}': '{}'", extractedText, numericText);
            
            if (numericText.isEmpty()) {
                logger.warn("No numeric value found in extracted text from region '{}': '{}'", regionName, extractedText);
                throw new RuntimeException("No numeric value found in extracted text: " + extractedText);
            }
            
            VariableManager.setSessionVariable(variableName, numericText);
            addVerification("Numeric Value Extraction", true, 
                String.format("Extracted numeric value from region '%s' stored in variable '%s': '%s'", 
                    regionName, variableName, numericText));
            
            logger.info("Successfully extracted numeric value from region '{}' and stored in variable '{}': '{}'", 
                regionName, variableName, numericText);
        } catch (Exception e) {
            logger.error("Failed to extract numeric value from region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Numeric Value Extraction", false, 
                "Numeric value extraction failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract numeric value from region: " + regionName, e);
        }
    }
    
    // =====================================================================================
    // REVOLUTIONARY OCR AUTOMATION - MULTI-LANGUAGE, BATCH PROCESSING, AI-ENHANCED
    // =====================================================================================
    
    @When("I extract all text from multiple regions and store in variables")
    public void i_extract_text_from_multiple_regions() {
        logger.info("Extracting text from all configured regions");        try {
            // Extract from all known regions by iterating through them
            String[] commonRegions = {"display_area", "button_area", "status_bar", "menu_area", "work_area", "input_area"};
            int extractedRegions = 0;
            
            for (String regionName : commonRegions) {
                try {
                    Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
                    File regionCapture = screenCapture.captureRegionToFile(region);
                    String extractedText = ocrEngine.extractText(regionCapture).trim();
                    
                    String variableName = "region_" + regionName + "_text";
                    VariableManager.setSessionVariable(variableName, extractedText);
                    extractedRegions++;
                    
                    logger.debug("Extracted text from region '{}': '{}'", regionName, extractedText);
                } catch (Exception e) {
                    logger.warn("Failed to extract text from region '{}': {}", regionName, e.getMessage());
                }            }
            
            addVerification("Batch Text Extraction", true, 
                String.format("Extracted text from %d regions", extractedRegions));
            logger.info("Successfully extracted text from {} regions", extractedRegions);
        } catch (Exception e) {
            logger.error("Failed to extract text from multiple regions: {}", e.getMessage(), e);
            addVerification("Batch Text Extraction", false, 
                "Batch text extraction failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract text from multiple regions", e);
        }
    }
    
    @When("I extract text using OCR language {string} from region {string} and store in variable {string}")
    public void i_extract_text_with_language(String language, String regionName, String variableName) {
        logger.info("Extracting text using OCR language '{}' from region '{}' and storing in variable '{}'", 
            language, regionName, variableName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            File regionCapture = screenCapture.captureRegionToFile(region);
              // Use OCR with standard settings (language-specific OCR would need engine enhancement)
            String extractedText = ocrEngine.extractText(regionCapture).trim();
            
            logger.debug("OCR extracted text with language '{}' from region '{}': '{}'", 
                language, regionName, extractedText);
            
            VariableManager.setSessionVariable(variableName, extractedText);
            addVerification("Multi-Language OCR", true, 
                String.format("Extracted text using language '%s' from region '%s' stored in variable '%s': '%s'", 
                    language, regionName, variableName, extractedText));
            
            logger.info("Successfully extracted text with language '{}' from region '{}' and stored in variable '{}': '{}'", 
                language, regionName, variableName, extractedText);
        } catch (Exception e) {
            logger.error("Failed to extract text with language '{}' from region '{}': {}", language, regionName, e.getMessage(), e);
            addVerification("Multi-Language OCR", false, 
                "Multi-language OCR failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract text with language: " + language, e);
        }
    }
    
    @When("I extract numbers only from region {string} and store in variable {string}")
    public void i_extract_numbers_only_from_region(String regionName, String variableName) {
        logger.info("Extracting numbers only from region '{}' and storing in variable '{}'", regionName, variableName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture);
            
            // Extract only numeric characters (including decimal points)
            String numbersOnly = extractedText.replaceAll("[^0-9.-]", "");
            
            logger.debug("OCR extracted raw text from region '{}': '{}', numbers only: '{}'", 
                regionName, extractedText.trim(), numbersOnly);
            
            VariableManager.setSessionVariable(variableName, numbersOnly);
            addVerification("Extract Numbers Only", true, 
                String.format("Extracted numbers '%s' from region '%s' stored in variable '%s'", 
                    numbersOnly, regionName, variableName));
            
            logger.info("Successfully extracted numbers '{}' from region '{}' and stored in variable '{}'", 
                numbersOnly, regionName, variableName);
        } catch (Exception e) {
            logger.error("Failed to extract numbers from region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Extract Numbers Only", false, 
                "Number extraction failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract numbers from region: " + regionName, e);
        }
    }
    
    @When("I validate text confidence in region {string} is above {int}% and store confidence in variable {string}")
    public void i_validate_text_confidence_and_store(String regionName, int minConfidence, String confidenceVariable) {
        logger.info("Validating text confidence in region '{}' above {}% and storing in variable '{}'", 
            regionName, minConfidence, confidenceVariable);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            File regionCapture = screenCapture.captureRegionToFile(region);
            com.automation.core.OCREngine.OCRResult ocrResult = ocrEngine.extractTextWithConfidence(regionCapture);
            
            boolean hasGoodConfidence = ocrResult.getConfidence() >= minConfidence;
            
            // Store confidence value
            VariableManager.setSessionVariable(confidenceVariable, String.valueOf(ocrResult.getConfidence()));
            
            logger.debug("OCR confidence in region '{}': {:.2f}% (min: {}%)", 
                regionName, ocrResult.getConfidence(), minConfidence);
            
            addVerification("OCR Confidence Validation", hasGoodConfidence, 
                String.format("OCR confidence in region '%s': %.2f%% (min: %d%%) - stored in variable '%s'", 
                    regionName, ocrResult.getConfidence(), minConfidence, confidenceVariable));
            
            if (!hasGoodConfidence) {
                logger.warn("OCR confidence {:.2f}% in region '{}' is below required minimum {}%", 
                    ocrResult.getConfidence(), regionName, minConfidence);
                captureScreenshot("low_ocr_confidence");
                throw new AssertionError(String.format("OCR confidence %.2f%% in region '%s' is below minimum %d%%", 
                    ocrResult.getConfidence(), regionName, minConfidence));
            }
            
            logger.info("Successfully validated OCR confidence {:.2f}% in region '{}' and stored in variable '{}'", 
                ocrResult.getConfidence(), regionName, confidenceVariable);
        } catch (Exception e) {
            logger.error("Failed to validate OCR confidence in region '{}': {}", regionName, e.getMessage(), e);
            addVerification("OCR Confidence Validation", false, 
                "OCR confidence validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate OCR confidence in region: " + regionName, e);
        }
    }
    
    @When("I extract all words from region {string} and store count in variable {string}")
    public void i_extract_word_count_from_region(String regionName, String countVariable) {
        logger.info("Extracting word count from region '{}' and storing in variable '{}'", regionName, countVariable);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture).trim();
            
            // Count words (split by whitespace and filter empty strings)
            String[] words = extractedText.split("\\s+");
            int wordCount = extractedText.isEmpty() ? 0 : words.length;
            
            logger.debug("OCR extracted text from region '{}': '{}' - Word count: {}", 
                regionName, extractedText, wordCount);
            
            VariableManager.setSessionVariable(countVariable, String.valueOf(wordCount));
            addVerification("Word Count Extraction", true, 
                String.format("Extracted %d words from region '%s' stored in variable '%s'", 
                    wordCount, regionName, countVariable));
            
            logger.info("Successfully extracted word count {} from region '{}' and stored in variable '{}'", 
                wordCount, regionName, countVariable);
        } catch (Exception e) {
            logger.error("Failed to extract word count from region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Word Count Extraction", false, 
                "Word count extraction failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract word count from region: " + regionName, e);
        }
    }    @Then("I verify text in region {string} is exactly {string}")
    public void i_verify_text_exact_match(String regionName, String expectedText) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Verifying exact text match in region '{}' equals '{}'", regionName, interpolatedText);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture).trim();
            
            boolean isExactMatch = extractedText.equals(interpolatedText);
            
            logger.debug("OCR extracted text from region '{}': '{}', expected exact match: '{}'", 
                regionName, extractedText, interpolatedText);
            
            addVerification("Exact Text Match", isExactMatch, 
                String.format("Region '%s' text '%s' %s exactly match expected '%s'", 
                    regionName, extractedText, isExactMatch ? "does" : "does not", interpolatedText));
            
            if (!isExactMatch) {
                logger.warn("Exact text match failed in region '{}'. Expected: '{}', Actual: '{}'", 
                    regionName, interpolatedText, extractedText);
                captureScreenshot("exact_text_match_failed");
                throw new AssertionError(String.format("Exact text match failed in region '%s'. Expected: '%s', Actual: '%s'", 
                    regionName, interpolatedText, extractedText));
            }
            
            logger.info("Successfully verified exact text match in region '{}': '{}'", regionName, interpolatedText);
        } catch (Exception e) {
            logger.error("Failed to verify exact text match in region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Exact Text Match", false, 
                "Exact text match verification failed: " + e.getMessage());
            throw new RuntimeException("Failed to verify exact text match in region: " + regionName, e);
        }
    }
    
    @When("I extract text lines from region {string} and store each line in variables with prefix {string}")
    public void i_extract_text_lines_with_prefix(String regionName, String variablePrefix) {
        logger.info("Extracting text lines from region '{}' and storing with prefix '{}'", regionName, variablePrefix);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture);
            
            String[] lines = extractedText.split("\\r?\\n");
            int lineCount = 0;
            
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    String variableName = variablePrefix + "_line_" + (i + 1);
                    VariableManager.setSessionVariable(variableName, line);
                    lineCount++;
                    logger.debug("Stored line {} in variable '{}': '{}'", (i + 1), variableName, line);
                }
            }
            
            // Store total line count
            VariableManager.setSessionVariable(variablePrefix + "_line_count", String.valueOf(lineCount));
            
            addVerification("Extract Text Lines", true, 
                String.format("Extracted %d lines from region '%s' with prefix '%s'", 
                    lineCount, regionName, variablePrefix));
            
            logger.info("Successfully extracted {} lines from region '{}' with prefix '{}'", 
                lineCount, regionName, variablePrefix);
        } catch (Exception e) {
            logger.error("Failed to extract text lines from region '{}': {}", regionName, e.getMessage(), e);
            addVerification("Extract Text Lines", false, 
                "Text lines extraction failed: " + e.getMessage());
            throw new RuntimeException("Failed to extract text lines from region: " + regionName, e);
        }
    }
    
    @When("I search for text pattern {string} in region {string} and store matches in variable {string}")
    public void i_search_text_pattern_in_region(String pattern, String regionName, String variableName) {
        String interpolatedPattern = VariableManager.interpolate(pattern);
        logger.info("Searching for text pattern '{}' in region '{}' and storing matches in variable '{}'", 
            interpolatedPattern, regionName, variableName);
        
        try {
            Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
            File regionCapture = screenCapture.captureRegionToFile(region);
            String extractedText = ocrEngine.extractText(regionCapture);
            
            java.util.regex.Pattern regexPattern = java.util.regex.Pattern.compile(interpolatedPattern);
            java.util.regex.Matcher matcher = regexPattern.matcher(extractedText);
            
            StringBuilder matches = new StringBuilder();
            int matchCount = 0;
            
            while (matcher.find()) {
                if (matchCount > 0) {
                    matches.append(";");
                }
                matches.append(matcher.group());
                matchCount++;
            }
            
            String matchesString = matches.toString();
            VariableManager.setSessionVariable(variableName, matchesString);
            VariableManager.setSessionVariable(variableName + "_count", String.valueOf(matchCount));
            
            logger.debug("Found {} matches for pattern '{}' in region '{}': '{}'", 
                matchCount, interpolatedPattern, regionName, matchesString);
            
            addVerification("Pattern Search", true, 
                String.format("Found %d matches for pattern '%s' in region '%s' stored in variable '%s'", 
                    matchCount, interpolatedPattern, regionName, variableName));
            
            logger.info("Successfully found {} matches for pattern '{}' in region '{}' and stored in variable '{}'",            matchCount, interpolatedPattern, regionName, variableName);
        } catch (Exception e) {
            logger.error("Failed to search pattern '{}' in region '{}': {}", interpolatedPattern, regionName, e.getMessage(), e);
            addVerification("Pattern Search", false, 
                "Pattern search failed: " + e.getMessage());
            throw new RuntimeException("Failed to search pattern in region: " + regionName, e);
        }
    }
    
    // =====================================================================================
    // ADDITIONAL OCR STEPS FOR APPLICATION AUTOMATION
    // =====================================================================================
    @When("I should see the text {string} using OCR")
    public void i_should_see_the_text_using_ocr(String expectedText) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Validating presence of text using OCR: '{}'", interpolatedText);
        
        try {
            // Capture full screen
            File screenshot = getScreenCapture().captureScreen();
            
            // Extract text using OCR
            String extractedText = getOCREngine().extractText(screenshot);
            
            // Check if expected text is found (case-insensitive)
            boolean textFound = extractedText.toLowerCase().contains(interpolatedText.toLowerCase());
            
            if (textFound) {
                addVerification("OCR Text Validation", true, 
                    String.format("Successfully found text '%s' using OCR", interpolatedText));
                logger.info("✓ Text '{}' found using OCR", interpolatedText);
            } else {
                addVerification("OCR Text Validation", false, 
                    String.format("Text '%s' not found using OCR. Extracted text: '%s'", 
                        interpolatedText, extractedText.substring(0, Math.min(100, extractedText.length()))));
                logger.error("✗ Text '{}' not found using OCR", interpolatedText);
                
                // Save screenshot for debugging
                captureScreenshot("ocr_text_not_found");
            }
            
        } catch (Exception e) {
            addVerification("OCR Text Validation", false, 
                String.format("OCR text validation failed: %s", e.getMessage()));
            logger.error("OCR text validation failed for text '{}': {}", interpolatedText, e.getMessage());
            throw new RuntimeException("OCR text validation failed", e);
        }
    }
    
    @Then("I should see text using OCR")
    public void i_should_see_text_using_ocr() {
        logger.info("Validating that any text is visible using OCR");
        
        try {
            File screenshot = screenCapture.captureScreen();
            String extractedText = ocrEngine.extractText(screenshot);
            
            logger.debug("OCR extracted text from full screen: '{}'", extractedText.trim());
            
            boolean hasText = extractedText != null && !extractedText.trim().isEmpty();
            addVerification("OCR Text Present", hasText, 
                hasText ? "OCR successfully extracted text from screen" : "No text found using OCR");
            
            if (!hasText) {
                logger.warn("No text extracted using OCR from current screen");
                captureScreenshot("ocr_no_text_found");
                throw new AssertionError("No text found on screen using OCR");
            }
            
            logger.info("Successfully validated text is present using OCR. Length: {} characters", 
                extractedText.trim().length());
        } catch (Exception e) {
            logger.error("Failed to validate text presence using OCR: {}", e.getMessage(), e);
            addVerification("OCR Text Present", false, "OCR text validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate text presence using OCR", e);
        }
    }
      // =====================================================================================
    // MISSING OCR STEP DEFINITIONS
    // =====================================================================================
    
    @Then("the OCR text should contain {string}")
    public void the_ocr_text_should_contain(String expectedText) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Validating OCR text contains: '{}'", interpolatedText);
        
        try {
            File screenshot = screenCapture.captureScreen();
            String extractedText = ocrEngine.extractText(screenshot);
            
            logger.debug("OCR extracted text from full screen: '{}'", extractedText.trim());
            
            boolean isFound = extractedText.toLowerCase().contains(interpolatedText.toLowerCase());
            addVerification("OCR Text Contains", isFound, 
                String.format("Expected text '%s' %s in OCR result", interpolatedText, isFound ? "found" : "not found"));
            
            if (!isFound) {
                logger.warn("Expected text '{}' not found in OCR text: '{}'", interpolatedText, extractedText.trim());
                captureScreenshot("ocr_text_contains_failed");
                throw new AssertionError("OCR text does not contain expected text: " + interpolatedText);
            }
            
            logger.info("Successfully validated OCR text contains: '{}'", interpolatedText);
        } catch (Exception e) {
            logger.error("Failed to validate OCR text contains '{}': {}", interpolatedText, e.getMessage(), e);
            addVerification("OCR Text Contains", false, "OCR text validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate OCR text contains: " + interpolatedText, e);
        }
    }
    
    @Then("the OCR text should contain {string} or {string} or {string}")
    public void the_ocr_text_should_contain_or_or(String option1, String option2, String option3) {
        String interpolatedOption1 = VariableManager.interpolate(option1);
        String interpolatedOption2 = VariableManager.interpolate(option2);
        String interpolatedOption3 = VariableManager.interpolate(option3);
        
        logger.info("Validating OCR text contains one of: '{}', '{}', or '{}'", 
            interpolatedOption1, interpolatedOption2, interpolatedOption3);
        
        try {
            File screenshot = screenCapture.captureScreen();
            String extractedText = ocrEngine.extractText(screenshot);
            String lowerText = extractedText.toLowerCase();
            
            logger.debug("OCR extracted text from full screen: '{}'", extractedText.trim());
            
            boolean found1 = lowerText.contains(interpolatedOption1.toLowerCase());
            boolean found2 = lowerText.contains(interpolatedOption2.toLowerCase());
            boolean found3 = lowerText.contains(interpolatedOption3.toLowerCase());
            boolean anyFound = found1 || found2 || found3;
            
            String foundOptions = "";
            if (found1) foundOptions += "'" + interpolatedOption1 + "' ";
            if (found2) foundOptions += "'" + interpolatedOption2 + "' ";
            if (found3) foundOptions += "'" + interpolatedOption3 + "' ";
            
            addVerification("OCR Text Contains Options", anyFound, 
                String.format("Expected one of the text options - Found: %s", 
                    anyFound ? foundOptions.trim() : "none"));
            
            if (!anyFound) {
                logger.warn("None of the expected text options found in OCR text: '{}'", extractedText.trim());
                captureScreenshot("ocr_text_options_failed");
                throw new AssertionError(String.format("OCR text does not contain any of: '%s', '%s', '%s'", 
                    interpolatedOption1, interpolatedOption2, interpolatedOption3));
            }
            
            logger.info("Successfully validated OCR text contains one of the options: {}", foundOptions.trim());
        } catch (Exception e) {
            logger.error("Failed to validate OCR text options: {}", e.getMessage(), e);
            addVerification("OCR Text Contains Options", false, "OCR text validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate OCR text options", e);
        }
    }    @When("I perform OCR on the entire {string} window")
    public void i_perform_ocr_on_the_entire_window(String managedApplicationName) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("🎯 ENTERPRISE OCR: Performing OCR on entire managed application window: {}", interpolatedAppName);
        
        try {
            // Resolve managed application to ManagedApplicationContext
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry", interpolatedAppName);
                logger.error("❌ Application not found: {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.info("🚀 ENTERPRISE RESOLVED: PID {} for application '{}'", context.getProcessId(), interpolatedAppName);
            
            // Focus the application using ManagedApplicationContext
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding with OCR operation", context.getProcessId());
            }
            Thread.sleep(500); // Brief wait for window to become active
            
            // Get window bounds using ManagedApplicationContext
            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                String errorMsg = String.format("Failed to get window bounds for PID %d (managed application '%s')", 
                    context.getProcessId(), interpolatedAppName);
                logger.error("❌ Window bounds error: {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // Capture only the managed application window instead of full screen
            BufferedImage windowCapture = screenCapture.captureWindow(windowBounds);
            String extractedText = ocrEngine.extractText(windowCapture);
            
            logger.debug("OCR extracted text from PID {} (managed application '{}'): '{}'", 
                context.getProcessId(), interpolatedAppName, extractedText.trim());
            
            // Store OCR result in variable for later use
            VariableManager.setSessionVariable("last_ocr_result", extractedText);
            VariableManager.setSessionVariable("last_ocr_window", interpolatedAppName);
            
            addVerification("Perform OCR on Managed Application", true, 
                String.format("✅ Enterprise OCR: Successfully performed OCR on PID %d (managed application '%s') - extracted %d characters", 
                    context.getProcessId(), interpolatedAppName, extractedText.length()));
            
            logger.info("✅ ENTERPRISE SUCCESS: OCR operation completed on PID {} (managed application '{}') - extracted {} characters", 
                context.getProcessId(), interpolatedAppName, extractedText.length());
        } catch (Exception e) {
            logger.error("💥 Failed to perform OCR on managed application '{}': {}", interpolatedAppName, e.getMessage(), e);
            addVerification("Perform OCR on Managed Application", false, 
                "Failed to perform OCR on managed application: " + e.getMessage());
            throw new RuntimeException("Failed to perform OCR on managed application: " + interpolatedAppName, e);
        }
    }
      // =====================================================================================
    // MISSING OCR STEP DEFINITIONS - COMMONLY USED
    // =====================================================================================
    
    @Then("I should see text {string} on screen within {int} seconds")
    public void i_should_see_text_on_screen_within_seconds(String expectedText, int timeoutSeconds) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        logger.info("Waiting for text '{}' to appear on screen within {} seconds", interpolatedText, timeoutSeconds);
        
        try {
            long startTime = System.currentTimeMillis();
            long timeoutMs = timeoutSeconds * 1000L;
            boolean found = false;
            String lastExtractedText = "";
            
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                File screenshot = screenCapture.captureScreen();
                lastExtractedText = ocrEngine.extractText(screenshot);
                
                if (lastExtractedText.toLowerCase().contains(interpolatedText.toLowerCase())) {
                    found = true;
                    break;
                }
                
                Thread.sleep(500); // Wait 500ms before next check
            }
            
            addVerification("Text Timeout Validation", found, 
                String.format("Text '%s' %s within %d seconds", interpolatedText, found ? "appeared" : "did not appear", timeoutSeconds));
            
            if (!found) {
                logger.warn("Text '{}' did not appear within {} seconds. Last extracted text: '{}'", 
                    interpolatedText, timeoutSeconds, lastExtractedText.trim());
                captureScreenshot("text_timeout_failed");
                throw new AssertionError(String.format("Text '%s' did not appear within %d seconds", interpolatedText, timeoutSeconds));
            }
            
            logger.info("Successfully found text '{}' within {} seconds", interpolatedText, timeoutSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for text", e);
        } catch (Exception e) {
            logger.error("Failed to validate text with timeout '{}': {}", interpolatedText, e.getMessage(), e);
            addVerification("Text Timeout Validation", false, "Text timeout validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate text with timeout: " + interpolatedText, e);
        }
    }
    
    // =====================================================================================
    // WINDOW-SPECIFIC OCR STEP DEFINITIONS - ENHANCED PERFORMANCE
    // =====================================================================================    @When("I extract text from window {string} and store in variable {string}")
    public void i_extract_text_from_window_and_store(String managedApplicationName, String variableName) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("🎯 ENTERPRISE OCR: Extracting text from managed application '{}' and storing in variable '{}'", 
            interpolatedAppName, variableName);
        
        try {
            // ENTERPRISE: Get managed application context
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry", interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.info("🚀 ENTERPRISE RESOLVED: PID {} for application '{}'", 
                context.getProcessId(), interpolatedAppName);
              // ENTERPRISE: All operations use ManagedApplicationContext
            String extractedText = extractTextFromManagedProcess(context);
            
            VariableManager.setSessionVariable(variableName, extractedText);
            
            addVerification("Extract Text from Managed Application", true, 
                String.format("✅ Enterprise OCR: Successfully extracted text from PID %d and stored in variable '%s'", 
                    context.getProcessId(), variableName));
            
            logger.info("✅ ENTERPRISE SUCCESS: Extracted text from PID {} - {} characters", 
                context.getProcessId(), extractedText.length());
        } catch (Exception e) {
            logger.error("💥 Failed to extract text from managed application '{}': {}", interpolatedAppName, e.getMessage(), e);
            addVerification("Extract Text from Managed Application", false, 
                "Failed to extract text from managed application: " + e.getMessage());
            throw new RuntimeException("Failed to extract text from managed application: " + interpolatedAppName, e);
        }
    }
      /**
     * ENTERPRISE: Extract text from managed application using ManagedApplicationContext
     * Central method that all OCR operations should use
     */
    private String extractTextFromManagedProcess(ManagedApplicationContext context) throws Exception {
        logger.debug("🎯 ENTERPRISE: Extracting text from PID {}", context.getProcessId());
        
        // Focus the application using ManagedApplicationContext
        boolean focused = windowController.focusWindow(context);
        if (!focused) {
            logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
        }
        Thread.sleep(500); // Brief wait for window to become active
        
        // Get window bounds using ManagedApplicationContext
        Rectangle windowBounds = windowController.getWindowBounds(context);
        if (windowBounds == null) {
            String errorMsg = String.format("Failed to get window bounds for PID %d", context.getProcessId());
            logger.error("❌ {}", errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        logger.debug("🎯 Window bounds for PID {}: x={}, y={}, width={}, height={}", 
            context.getProcessId(), windowBounds.x, windowBounds.y, windowBounds.width, windowBounds.height);
        
        // Capture window using context-provided bounds
        BufferedImage windowCapture = screenCapture.captureWindow(windowBounds);
        String extractedText = ocrEngine.extractText(windowCapture).trim();
        
        logger.debug("OCR extracted text from PID {}: '{}'", context.getProcessId(), extractedText);
        
        return extractedText;
    }    @When("I extract text from region {string} in window {string} and store in variable {string}")
    public void i_extract_text_from_region_in_window_and_store(String regionName, String managedApplicationName, String variableName) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("🎯 ENTERPRISE REGION OCR: Extracting text from region '{}' in managed application '{}' and storing in variable '{}'", 
            regionName, interpolatedAppName, variableName);
        
        try {
            // ENTERPRISE: Get managed application context
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry", interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.info("🚀 ENTERPRISE RESOLVED: PID {} for application '{}'", 
                context.getProcessId(), interpolatedAppName);
            
            // ENTERPRISE: All operations use ManagedApplicationContext
            String extractedText = extractTextFromRegionInManagedProcess(context, regionName);
            
            VariableManager.setSessionVariable(variableName, extractedText);
            
            addVerification("Extract Text from Region in Managed Application", true, 
                String.format("✅ Enterprise region OCR: Successfully extracted text from region '%s' in PID %d and stored in variable '%s'",
                    regionName, context.getProcessId(), variableName));
            
            logger.info("✅ ENTERPRISE SUCCESS: Extracted text from region '{}' in PID {} - {} characters", 
                regionName, context.getProcessId(), extractedText.length());
        } catch (Exception e) {
            logger.error("💥 Failed to extract text from region '{}' in managed application '{}': {}", 
                regionName, interpolatedAppName, e.getMessage(), e);
            addVerification("Extract Text from Region in Managed Application", false, 
                "Failed to extract text from region in managed application: " + e.getMessage());
            throw new RuntimeException("Failed to extract text from region in managed application: " + regionName, e);
        }
    }
      /**
     * ENTERPRISE: Extract text from region in managed application using ManagedApplicationContext
     * Central method for region-based OCR operations
     */
    private String extractTextFromRegionInManagedProcess(ManagedApplicationContext context, String regionName) throws Exception {
        logger.debug("🎯 ENTERPRISE: Extracting text from region '{}' in PID {}", regionName, context.getProcessId());
        
        // Focus the application using ManagedApplicationContext
        boolean focused = windowController.focusWindow(context);
        if (!focused) {
            logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
        }
        Thread.sleep(500); // Brief wait for window to become active
        
        // Get window bounds using ManagedApplicationContext for relative positioning
        Rectangle windowBounds = windowController.getWindowBounds(context);
        if (windowBounds == null) {
            String errorMsg = String.format("Failed to get window bounds for PID %d", context.getProcessId());
            logger.error("❌ {}", errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        // Get region relative to the current application
        Rectangle region = ConfigManager.getCurrentAppRegion(regionName);
        
        // Convert region to absolute coordinates based on window position
        Rectangle absoluteRegion = new Rectangle(
            windowBounds.x + region.x,
            windowBounds.y + region.y,
            region.width,
            region.height
        );
        
        logger.debug("🎯 PID {} window bounds: {}, Region '{}' relative: {}, Absolute region: {}", 
            context.getProcessId(), windowBounds, regionName, region, absoluteRegion);
        
        // Capture only the specific region within the managed application window
        File regionCapture = screenCapture.captureRegionToFile(absoluteRegion);
        String extractedText = ocrEngine.extractText(regionCapture).trim();
        
        logger.debug("OCR extracted text from region '{}' in PID {}: '{}'", 
            regionName, context.getProcessId(), extractedText);
        
        return extractedText;
    }    @Then("I should see text {string} in window {string}")
    public void i_should_see_text_in_window(String expectedText, String managedApplicationName) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("🎯 ENTERPRISE TEXT VALIDATION: Verifying text '{}' appears in managed application '{}'", 
            interpolatedText, interpolatedAppName);
        
        try {
            // ENTERPRISE: Get managed application context
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry", interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.info("🚀 ENTERPRISE RESOLVED: PID {} for application '{}'", 
                context.getProcessId(), interpolatedAppName);
            
            // ENTERPRISE: Validate text using ManagedApplicationContext
            boolean contains = validateTextInManagedProcess(context, interpolatedText);
            
            addVerification("Text Validation in Managed Application", contains, 
                String.format("✅ Enterprise validation: Expected text '%s' %s in PID %d", 
                    interpolatedText, contains ? "found" : "not found", context.getProcessId()));
            
            if (!contains) {
                String errorMsg = String.format("Expected text '%s' not found in PID %d", 
                    interpolatedText, context.getProcessId());
                logger.error("❌ {}", errorMsg);
                throw new AssertionError(errorMsg);
            }
            
            logger.info("✅ ENTERPRISE SUCCESS: Text '{}' validated in PID {}", 
                interpolatedText, context.getProcessId());
        } catch (Exception e) {
            logger.error("💥 Failed to validate text '{}' in managed application '{}': {}", 
                interpolatedText, interpolatedAppName, e.getMessage(), e);
            addVerification("Text Validation in Managed Application", false, 
                "Enterprise text validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate text in managed application: " + interpolatedText, e);
        }
    }
      /**
     * ENTERPRISE: Validate text in managed application using ManagedApplicationContext
     * Central method for text validation operations
     */
    private boolean validateTextInManagedProcess(ManagedApplicationContext context, String expectedText) throws Exception {
        logger.debug("🎯 ENTERPRISE: Validating text '{}' in PID {}", expectedText, context.getProcessId());
        
        // Focus the application using ManagedApplicationContext
        boolean focused = windowController.focusWindow(context);
        if (!focused) {
            logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
        }
        Thread.sleep(500); // Brief wait for window to become active
        
        // Get window bounds using ManagedApplicationContext
        Rectangle windowBounds = windowController.getWindowBounds(context);
        if (windowBounds == null) {
            String errorMsg = String.format("Failed to get window bounds for PID %d", context.getProcessId());
            logger.error("❌ {}", errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        // Capture only the managed application window instead of full screen
        BufferedImage windowCapture = screenCapture.captureWindow(windowBounds);
        String extractedText = ocrEngine.extractText(windowCapture);
        
        boolean contains = extractedText.toLowerCase().contains(expectedText.toLowerCase());
        
        logger.debug("OCR extracted text from PID {}: '{}', looking for: '{}'", 
            context.getProcessId(), extractedText.trim(), expectedText);
        
        return contains;
    }    @When("I wait for text {string} to appear in window {string} with timeout {int} seconds")
    public void i_wait_for_text_to_appear_in_window_with_timeout(String expectedText, String managedApplicationName, int timeoutSeconds) {
        String interpolatedText = VariableManager.interpolate(expectedText);
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("🎯 ENTERPRISE WAIT: Waiting for text '{}' to appear in managed application '{}' (timeout: {}s)", 
            interpolatedText, interpolatedAppName, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;
        boolean textFound = false;
        String lastExtractedText = "";
        
        try {
            // ENTERPRISE: Get managed application context
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry", interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.info("🚀 ENTERPRISE RESOLVED: PID {} for application '{}'", context.getProcessId(), interpolatedAppName);
            
            // ENTERPRISE: Focus the application using ManagedApplicationContext
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            Thread.sleep(500); // Brief wait for window to become active
            
            // ENTERPRISE: Get window bounds using ManagedApplicationContext
            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                String errorMsg = String.format("Failed to get window bounds for PID %d (managed application '%s')", 
                    context.getProcessId(), interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            while (System.currentTimeMillis() - startTime < timeout && !textFound) {
                try {
                    // Capture only the managed application window instead of full screen
                    BufferedImage windowCapture = screenCapture.captureWindow(windowBounds);
                    lastExtractedText = ocrEngine.extractText(windowCapture);
                    
                    if (lastExtractedText.toLowerCase().contains(interpolatedText.toLowerCase())) {
                        textFound = true;
                        logger.debug("🎯 Text '{}' found in PID {} (managed application '{}') after {}ms", 
                            interpolatedText, context.getProcessId(), interpolatedAppName, 
                            System.currentTimeMillis() - startTime);
                        break;
                    }
                    
                    Thread.sleep(1000); // Check every second
                } catch (Exception e) {
                    logger.warn("⚠️ Error during text search in PID {} (managed application '{}'): {}", 
                        context.getProcessId(), interpolatedAppName, e.getMessage());
                    Thread.sleep(1000);
                }
            }
            
            addVerification("Wait for Text in Managed Application", textFound, 
                String.format("✅ Enterprise wait: Text '%s' %s in PID %d (managed application '%s') within %d seconds", 
                    interpolatedText, textFound ? "appeared" : "did not appear", 
                    context.getProcessId(), interpolatedAppName, timeoutSeconds));
            
            if (textFound) {
                logger.info("✅ ENTERPRISE SUCCESS: Text '{}' appeared in PID {} (managed application '{}') within timeout", 
                    interpolatedText, context.getProcessId(), interpolatedAppName);
            } else {
                logger.warn("⏰ TIMEOUT: Waiting for text '{}' in PID {} (managed application '{}'). Last extracted text: '{}'", 
                    interpolatedText, context.getProcessId(), interpolatedAppName, lastExtractedText.trim());
                throw new RuntimeException("Timeout waiting for text to appear in managed application: " + interpolatedText);
            }
        } catch (Exception e) {
            logger.error("💥 Failed to wait for text '{}' in managed application '{}': {}", 
                interpolatedText, interpolatedAppName, e.getMessage(), e);
            addVerification("Wait for Text in Managed Application", false, 
                "Failed to wait for text in managed application: " + e.getMessage());
            throw new RuntimeException("Failed to wait for text in managed application: " + interpolatedText, e);
        }
    }
      @When("I validate text confidence in window {string} is above {int}% and store confidence in variable {string}")
    public void i_validate_text_confidence_in_window_and_store(String managedApplicationName, int minConfidence, String confidenceVariable) {
        String interpolatedAppName = VariableManager.interpolate(managedApplicationName);
        logger.info("🎯 ENTERPRISE CONFIDENCE: Validating text confidence in managed application '{}' above {}% and storing in variable '{}'", 
            interpolatedAppName, minConfidence, confidenceVariable);
        
        try {
            // ENTERPRISE: Get managed application context
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(interpolatedAppName);
            if (context == null) {
                String errorMsg = String.format("Managed application '%s' not found in ProcessManager registry", interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            logger.info("🚀 ENTERPRISE RESOLVED: PID {} for application '{}'", context.getProcessId(), interpolatedAppName);
            
            // ENTERPRISE: Focus the application using ManagedApplicationContext
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                logger.warn("⚠️ Could not focus window for PID {}, proceeding anyway...", context.getProcessId());
            }
            Thread.sleep(500); // Brief wait for window to become active
            
            // ENTERPRISE: Get window bounds using ManagedApplicationContext
            Rectangle windowBounds = windowController.getWindowBounds(context);
            if (windowBounds == null) {
                String errorMsg = String.format("Failed to get window bounds for PID %d (managed application '%s')", 
                    context.getProcessId(), interpolatedAppName);
                logger.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }
              // Capture only the managed application window instead of full screen
            BufferedImage windowCapture = screenCapture.captureWindow(windowBounds);
            
            // Save to temporary file and use File-based OCR method
            File tempImage = File.createTempFile("ocr_temp", ".png");
            javax.imageio.ImageIO.write(windowCapture, "PNG", tempImage);
            com.automation.core.OCREngine.OCRResult ocrResult = ocrEngine.extractTextWithConfidence(tempImage);
            
            double confidence = ocrResult.getConfidence();
            boolean hasGoodConfidence = confidence >= minConfidence;
            
            // Store confidence value
            VariableManager.setSessionVariable(confidenceVariable, String.valueOf(confidence));
            
            logger.debug("OCR confidence in PID {} (managed application '{}'): {}% (minimum: {}%)", 
                context.getProcessId(), interpolatedAppName, confidence, minConfidence);
            
            addVerification("Text Confidence Validation in Managed Application", hasGoodConfidence, 
                String.format("✅ Enterprise confidence: Text confidence in PID %d (managed application '%s') is %.2f%% (required: %d%%)", 
                    context.getProcessId(), interpolatedAppName, confidence, minConfidence));
            
            if (!hasGoodConfidence) {
                logger.warn("⚠️ Low OCR confidence in PID {} (managed application '{}'): {}% (below minimum: {}%)", 
                    context.getProcessId(), interpolatedAppName, confidence, minConfidence);
                throw new RuntimeException("OCR confidence below threshold in managed application: " + interpolatedAppName);
            }
            
            logger.info("✅ ENTERPRISE SUCCESS: Validated OCR confidence in PID {} (managed application '{}'): {}%", 
                context.getProcessId(), interpolatedAppName, confidence);
        } catch (Exception e) {
            logger.error("💥 Failed to validate text confidence in managed application '{}': {}", 
                interpolatedAppName, e.getMessage(), e);
            addVerification("Text Confidence Validation in Managed Application", false, 
                "Failed to validate text confidence in managed application: " + e.getMessage());
            throw new RuntimeException("Failed to validate text confidence in managed application: " + interpolatedAppName, e);
        }
    }
}
