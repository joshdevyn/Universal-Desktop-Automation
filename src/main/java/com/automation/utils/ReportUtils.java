package com.automation.utils;

import com.automation.models.TestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * ReportUtils provides comprehensive test reporting capabilities using ExtentReports
 */
public class ReportUtils {
    private static final Logger logger = LoggerFactory.getLogger(ReportUtils.class);
    
    private ExtentReports extentReports;
    private ExtentTest currentTest;
    private String reportDirectory;
    private String reportFileName;
    
    public ReportUtils() {
        this("src/test/resources/reports");
    }
    
    public ReportUtils(String reportDirectory) {
        this.reportDirectory = reportDirectory;
        initializeReporting();
    }
    
    /**
     * Initialize ExtentReports
     */
    private void initializeReporting() {
        try {
            // Create report directory if it doesn't exist
            File reportDir = new File(reportDirectory);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            
            // Generate timestamped report filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            reportFileName = "AutomationReport_" + timestamp + ".html";
            String reportPath = new File(reportDirectory, reportFileName).getAbsolutePath();
            
            // Configure ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            // Initialize ExtentReports
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            
            // Set system information
            setSystemInformation();
            
            logger.info("ExtentReports initialized successfully: {}", reportPath);
            
        } catch (Exception e) {
            logger.error("Failed to initialize ExtentReports", e);
            throw new RuntimeException("Report initialization failed", e);
        }
    }
    
    /**
     * Configure Spark Reporter settings
     */
    private void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Universal Desktop Automation Test Report");
        sparkReporter.config().setReportName("Automation Test Results");
        sparkReporter.config().setEncoding("UTF-8");
        
        // Custom CSS and JavaScript can be added here
        sparkReporter.config().setCss(
            ".test-content .test-time-info { display: block !important; }" +
            ".screenshot { max-width: 100%; height: auto; border: 1px solid #ddd; margin: 10px 0; }" +
            ".ocr-result { background-color: #f8f9fa; border-left: 4px solid #007bff; padding: 10px; margin: 10px 0; }"
        );
    }
    
    /**
     * Set system information in the report
     */
    private void setSystemInformation() {
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("OS Version", System.getProperty("os.version"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User", System.getProperty("user.name"));
        extentReports.setSystemInfo("Framework", "Universal Desktop Automation Framework");
        extentReports.setSystemInfo("Report Generated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    
    /**
     * Start a new test in the report
     */
    public ExtentTest startTest(String testName) {
        return startTest(testName, "");
    }
    
    /**
     * Start a new test with description
     */
    public ExtentTest startTest(String testName, String description) {
        currentTest = extentReports.createTest(testName, description);
        logger.debug("Started test in report: {}", testName);
        return currentTest;
    }
    
    /**
     * End current test
     */
    public void endTest() {
        currentTest = null;
        logger.debug("Ended current test in report");
    }
    
    /**
     * Log info message
     */
    public void logInfo(String message) {
        if (currentTest != null) {
            currentTest.log(Status.INFO, message);
        }
    }
    
    /**
     * Log pass message
     */
    public void logPass(String message) {
        if (currentTest != null) {
            currentTest.log(Status.PASS, message);
        }
    }
    
    /**
     * Log fail message
     */
    public void logFail(String message) {
        if (currentTest != null) {
            currentTest.log(Status.FAIL, message);
        }
    }
    
    /**
     * Log warning message
     */
    public void logWarning(String message) {
        if (currentTest != null) {
            currentTest.log(Status.WARNING, message);
        }
    }
    
    /**
     * Log skip message
     */
    public void logSkip(String message) {
        if (currentTest != null) {
            currentTest.log(Status.SKIP, message);
        }
    }
    
    /**
     * Add screenshot to current test
     */
    public void addScreenshot(String description, String screenshotPath) {
        if (currentTest != null && screenshotPath != null) {
            try {
                File screenshotFile = new File(screenshotPath);
                if (screenshotFile.exists()) {
                    currentTest.log(Status.INFO, description, 
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } else {
                    currentTest.log(Status.WARNING, "Screenshot not found: " + screenshotPath);
                }
            } catch (Exception e) {
                logger.error("Failed to add screenshot to report: {}", screenshotPath, e);
                currentTest.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
            }
        }
    }
    
    /**
     * Add OCR result to current test
     */
    public void addOCRResult(String region, String extractedText, double confidence) {
        if (currentTest != null) {
            String ocrHtml = String.format(
                "<div class='ocr-result'>" +
                "<strong>OCR Result - %s</strong><br>" +
                "Extracted Text: <code>%s</code><br>" +
                "Confidence: %.2f%%" +
                "</div>",
                region, 
                extractedText.replace("<", "&lt;").replace(">", "&gt;"),
                confidence
            );
            
            currentTest.log(Status.INFO, ocrHtml);
        }
    }
    
    /**
     * Add verification result
     */
    public void addVerification(String description, boolean passed, String details) {
        if (currentTest != null) {
            Status status = passed ? Status.PASS : Status.FAIL;
            String message = description;
            
            if (details != null && !details.isEmpty()) {
                message += " - " + details;
            }
            
            currentTest.log(status, message);
        }
    }
    
    /**
     * Generate comprehensive test report from TestResult
     */
    public void generateTestReport(TestResult testResult) {
        if (testResult == null) {
            return;
        }
        
        ExtentTest test = startTest(testResult.getTestName(), testResult.getDescription());
        
        // Add test information
        test.assignCategory(testResult.getTestClass());
        
        // Add test duration
        test.getModel().setStartTime(new Date(testResult.getStartTime()));
        test.getModel().setEndTime(new Date(testResult.getEndTime()));
        
        // Add test data
        if (!testResult.getTestData().isEmpty()) {
            StringBuilder testDataHtml = new StringBuilder("<div class='test-data'><strong>Test Data:</strong><ul>");
            for (Map.Entry<String, Object> entry : testResult.getTestData().entrySet()) {
                testDataHtml.append("<li>").append(entry.getKey()).append(": ").append(entry.getValue()).append("</li>");
            }
            testDataHtml.append("</ul></div>");
            test.log(Status.INFO, testDataHtml.toString());
        }
        
        // Add screenshots
        for (Map.Entry<String, String> screenshot : testResult.getScreenshots().entrySet()) {
            addScreenshot(screenshot.getKey(), screenshot.getValue());
        }
        
        // Add OCR results
        for (Map.Entry<String, String> ocrResult : testResult.getOcrResults().entrySet()) {
            addOCRResult(ocrResult.getKey(), ocrResult.getValue(), 0.0);
        }
        
        // Add verifications
        for (TestResult.VerificationResult verification : testResult.getVerifications()) {
            addVerification(verification.getDescription(), verification.isPassed(), verification.getDetails());
        }
        
        // Add logs
        for (String logMessage : testResult.getLogs()) {
            test.log(Status.INFO, logMessage);
        }
        
        // Add error information if test failed
        if (testResult.isFailed()) {
            if (testResult.getErrorMessage() != null) {
                test.log(Status.FAIL, "Error: " + testResult.getErrorMessage());
            }
            
            if (testResult.getStackTrace() != null) {
                test.log(Status.FAIL, "<details><summary>Stack Trace</summary><pre>" + 
                    testResult.getStackTrace() + "</pre></details>");
            }
        }
        
        // Set final test status
        Status finalStatus = Status.PASS;
        if (testResult.isFailed()) {
            finalStatus = Status.FAIL;
        } else if (testResult.isSkipped()) {
            finalStatus = Status.SKIP;
        }
        
        test.log(finalStatus, "Test " + testResult.getStatus().toLowerCase() + 
            " in " + testResult.getDurationFormatted());
        
        endTest();
    }
    
    /**
     * Create a child test (for test steps)
     */
    public ExtentTest createNode(String nodeName) {
        return createNode(nodeName, "");
    }
    
    /**
     * Create a child test with description
     */
    public ExtentTest createNode(String nodeName, String description) {
        if (currentTest != null) {
            return currentTest.createNode(nodeName, description);
        }
        return null;
    }
    
    /**
     * Flush and finalize the report
     */
    public void generateSuiteReport() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("Test report generated successfully: {}", 
                new File(reportDirectory, reportFileName).getAbsolutePath());
        }
    }
    
    /**
     * Get report file path
     */
    public String getReportPath() {
        return new File(reportDirectory, reportFileName).getAbsolutePath();
    }
    
    /**
     * Add custom HTML content
     */
    public void addCustomHtml(String html) {
        if (currentTest != null) {
            currentTest.log(Status.INFO, html);
        }
    }
    
    /**
     * Add test execution summary
     */
    public void addExecutionSummary(int totalTests, int passedTests, int failedTests, int skippedTests) {
        if (currentTest != null) {
            String summaryHtml = String.format(
                "<div class='execution-summary'>" +
                "<h4>Execution Summary</h4>" +
                "<table border='1' style='border-collapse: collapse; width: 100%%;'>" +
                "<tr><td><strong>Total Tests</strong></td><td>%d</td></tr>" +
                "<tr style='color: green;'><td><strong>Passed</strong></td><td>%d</td></tr>" +
                "<tr style='color: red;'><td><strong>Failed</strong></td><td>%d</td></tr>" +
                "<tr style='color: orange;'><td><strong>Skipped</strong></td><td>%d</td></tr>" +
                "</table>" +
                "</div>",
                totalTests, passedTests, failedTests, skippedTests
            );
            
            currentTest.log(Status.INFO, summaryHtml);
        }
    }
    
    // Getters
    
    public ExtentReports getExtentReports() {
        return extentReports;
    }
    
    public ExtentTest getCurrentTest() {
        return currentTest;
    }
    
    public String getReportDirectory() {
        return reportDirectory;
    }
    
    public String getReportFileName() {
        return reportFileName;
    }
}
