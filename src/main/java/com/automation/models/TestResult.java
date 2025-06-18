package com.automation.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TestResult holds the results of a test execution
 */
public class TestResult {
    private static final Logger logger = LoggerFactory.getLogger(TestResult.class);
    
    private String testName;
    private String testClass;
    private String status; // PASSED, FAILED, SKIPPED
    private long startTime;
    private long endTime;
    private long duration;
    private String errorMessage;
    private String stackTrace;
    private Map<String, String> screenshots;
    private List<String> logs;
    private List<VerificationResult> verifications;
    private Map<String, Object> testData;
    private Map<String, String> ocrResults;
    private String description;
    
    public TestResult() {
        this.screenshots = new HashMap<>();
        this.logs = new ArrayList<>();
        this.verifications = new ArrayList<>();
        this.testData = new HashMap<>();
        this.ocrResults = new HashMap<>();
        this.status = "UNKNOWN";
    }
    
    public TestResult(String testName) {
        this();
        this.testName = testName;
    }
    
    public TestResult(String testName, String testClass) {
        this(testName);
        this.testClass = testClass;
    }
    
    // Getters and Setters
    
    public String getTestName() {
        return testName;
    }
    
    public void setTestName(String testName) {
        this.testName = testName;
    }
    
    public String getTestClass() {
        return testClass;
    }
    
    public void setTestClass(String testClass) {
        this.testClass = testClass;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        
        // Calculate duration when status is set
        if (startTime > 0 && endTime > 0) {
            this.duration = endTime - startTime;
        }
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
        
        // Calculate duration when end time is set
        if (startTime > 0) {
            this.duration = endTime - startTime;
        }
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getStackTrace() {
        return stackTrace;
    }
    
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
    
    public Map<String, String> getScreenshots() {
        return screenshots;
    }
    
    public void setScreenshots(Map<String, String> screenshots) {
        this.screenshots = screenshots;
    }
    
    public List<String> getLogs() {
        return logs;
    }
    
    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
    
    public List<VerificationResult> getVerifications() {
        return verifications;
    }
    
    public void setVerifications(List<VerificationResult> verifications) {
        this.verifications = verifications;
    }
    
    public Map<String, Object> getTestData() {
        return testData;
    }
    
    public void setTestData(Map<String, Object> testData) {
        this.testData = testData;
    }
    
    public Map<String, String> getOcrResults() {
        return ocrResults;
    }
    
    public void setOcrResults(Map<String, String> ocrResults) {
        this.ocrResults = ocrResults;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Utility methods
    
    public void addScreenshot(String label, String filePath) {
        if (filePath != null) {
            screenshots.put(label, filePath);
        }
    }
    
    public String getScreenshot(String label) {
        return screenshots.get(label);
    }
    
    public void addLog(String message) {
        logs.add(String.format("[%d] %s", System.currentTimeMillis(), message));
    }
    
    public void addVerification(String description, boolean passed) {
        verifications.add(new VerificationResult(description, passed));
    }
    
    public void addVerification(String description, boolean passed, String details) {
        verifications.add(new VerificationResult(description, passed, details));
    }
    
    public void addTestData(String key, Object value) {
        testData.put(key, value);
    }
    
    /**
     * Add test data/metadata for the test result
     * @param key The key for the test data
     * @param value The value for the test data
     */
    public void addTestData(String key, String value) {
        if (testData == null) {
            testData = new HashMap<>();
        }
        testData.put(key, value);
        logger.debug("Added test data: {} = {}", key, value);
    }
    
    public Object getTestData(String key) {
        return testData.get(key);
    }
    
    public void addOcrResult(String region, String text) {
        ocrResults.put(region, text);
    }
    
    public String getOcrResult(String region) {
        return ocrResults.get(region);
    }
    
    public boolean isPassed() {
        return "PASSED".equals(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    public boolean isSkipped() {
        return "SKIPPED".equals(status);
    }
    
    public int getPassedVerifications() {
        return (int) verifications.stream().filter(VerificationResult::isPassed).count();
    }
    
    public int getFailedVerifications() {
        return (int) verifications.stream().filter(v -> !v.isPassed()).count();
    }
    
    public int getTotalVerifications() {
        return verifications.size();
    }
    
    public boolean hasVerificationFailures() {
        return verifications.stream().anyMatch(v -> !v.isPassed());
    }
    
    public String getDurationFormatted() {
        if (duration == 0) {
            return "0ms";
        }
        
        long seconds = duration / 1000;
        long milliseconds = duration % 1000;
        
        if (seconds > 0) {
            return String.format("%d.%03ds", seconds, milliseconds);
        } else {
            return String.format("%dms", milliseconds);
        }
    }
    
    public String getStartTimeFormatted() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(startTime));
    }
    
    public String getEndTimeFormatted() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(endTime));
    }
    
    @Override
    public String toString() {
        return String.format("TestResult{testName='%s', status='%s', duration=%s, verifications=%d/%d passed}",
                           testName, status, getDurationFormatted(), getPassedVerifications(), getTotalVerifications());
    }
    
    /**
     * Inner class for verification results
     */
    public static class VerificationResult {
        private String description;
        private boolean passed;
        private String details;
        private long timestamp;
        
        public VerificationResult(String description, boolean passed) {
            this.description = description;
            this.passed = passed;
            this.timestamp = System.currentTimeMillis();
        }
        
        public VerificationResult(String description, boolean passed, String details) {
            this(description, passed);
            this.details = details;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public boolean isPassed() {
            return passed;
        }
        
        public void setPassed(boolean passed) {
            this.passed = passed;
        }
        
        public String getDetails() {
            return details;
        }
        
        public void setDetails(String details) {
            this.details = details;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("VerificationResult{description='%s', passed=%s, details='%s'}",
                               description, passed, details);
        }
    }
}
