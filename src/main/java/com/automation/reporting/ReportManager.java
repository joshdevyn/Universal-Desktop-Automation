package com.automation.reporting;

import com.automation.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ReportManager - Centralized test reporting and metrics collection
 * Provides scenario reporting, test result aggregation, and report generation
 * 
 * @author Joshua Sims
 * @version 1.0
 * @since 1.0
 */
public class ReportManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportManager.class);
    private static ReportManager instance;
    
    private final Map<String, ScenarioResult> scenarioResults = new ConcurrentHashMap<>();
    private final String reportBasePath;
    private final String reportName;
    private String currentTestSuiteId;
    
    private ReportManager() {
        this.reportBasePath = ConfigManager.getProperty("report.path", "target/reports");
        this.reportName = ConfigManager.getProperty("report.name", "AutomationReport");
        this.currentTestSuiteId = generateTestSuiteId();
        initializeReportDirectory();
    }
    
    /**
     * Get singleton instance of ReportManager
     */
    public static synchronized ReportManager getInstance() {
        if (instance == null) {
            instance = new ReportManager();
        }
        return instance;
    }
    
    /**
     * Add scenario result to the report
     */
    public void addScenarioResult(String scenarioName, String status, long executionTime, 
                                 String featureName, Collection<String> tags) {
        try {
            ScenarioResult result = new ScenarioResult(
                scenarioName, status, executionTime, featureName, tags, 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            
            scenarioResults.put(generateScenarioId(scenarioName, featureName), result);
            logger.debug("Added scenario result: {} - {}", scenarioName, status);
            
            // Write immediate scenario report
            writeScenarioReport(result);
            
        } catch (Exception e) {
            logger.error("Failed to add scenario result: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Generate summary report for all scenarios
     */
    public void generateSummaryReport() {
        try {
            String summaryPath = Paths.get(reportBasePath, currentTestSuiteId, "summary.html").toString();
            
            StringBuilder html = new StringBuilder();
            html.append(generateHtmlHeader("Test Execution Summary"));
            html.append(generateSummaryTable());
            html.append(generateDetailedResults());
            html.append(generateHtmlFooter());
            
            writeToFile(summaryPath, html.toString());
            logger.info("Summary report generated: {}", summaryPath);
            
        } catch (Exception e) {
            logger.error("Failed to generate summary report: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get test execution statistics
     */
    public TestExecutionStats getExecutionStats() {
        long totalScenarios = scenarioResults.size();
        long passedScenarios = scenarioResults.values().stream()
            .mapToLong(result -> "PASSED".equals(result.getStatus()) ? 1 : 0)
            .sum();
        long failedScenarios = totalScenarios - passedScenarios;
        
        double totalExecutionTime = scenarioResults.values().stream()
            .mapToDouble(ScenarioResult::getExecutionTime)
            .sum();
        
        return new TestExecutionStats(totalScenarios, passedScenarios, failedScenarios, totalExecutionTime);
    }
    
    // Private helper methods
    
    private void initializeReportDirectory() {
        try {
            Path reportDir = Paths.get(reportBasePath, currentTestSuiteId);
            Files.createDirectories(reportDir);
            logger.info("Report directory initialized: {}", reportDir);
        } catch (IOException e) {
            logger.error("Failed to initialize report directory: {}", e.getMessage(), e);
        }
    }
    
    private String generateTestSuiteId() {
        return "TestRun_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
    
    private String generateScenarioId(String scenarioName, String featureName) {
        return featureName + "_" + scenarioName.replaceAll("[^a-zA-Z0-9]", "_");
    }
    
    private void writeScenarioReport(ScenarioResult result) {
        try {
            String scenarioPath = Paths.get(reportBasePath, currentTestSuiteId, 
                "scenarios", result.getFeatureName() + "_" + 
                result.getScenarioName().replaceAll("[^a-zA-Z0-9]", "_") + ".json").toString();
            
            // Create scenarios directory if it doesn't exist
            Files.createDirectories(Paths.get(scenarioPath).getParent());
            
            // Write JSON representation of scenario result
            String json = scenarioResultToJson(result);
            writeToFile(scenarioPath, json);
            
        } catch (Exception e) {
            logger.error("Failed to write scenario report: {}", e.getMessage(), e);
        }
    }
    
    private String scenarioResultToJson(ScenarioResult result) {
        return String.format(
            "{\n" +
            "  \"scenarioName\": \"%s\",\n" +
            "  \"status\": \"%s\",\n" +
            "  \"executionTime\": %d,\n" +
            "  \"featureName\": \"%s\",\n" +
            "  \"tags\": %s,\n" +
            "  \"timestamp\": \"%s\"\n" +
            "}",
            result.getScenarioName(),
            result.getStatus(),
            result.getExecutionTime(),
            result.getFeatureName(),
            result.getTags().toString(),
            result.getTimestamp()
        );
    }
    
    private String generateHtmlHeader(String title) {
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>%s</title>\n" +
            "    <style>\n" +
            "        body { font-family: Arial, sans-serif; margin: 20px; }\n" +
            "        .header { background-color: #f0f0f0; padding: 20px; border-radius: 5px; }\n" +
            "        .stats { display: flex; justify-content: space-around; margin: 20px 0; }\n" +
            "        .stat-box { text-align: center; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }\n" +
            "        .passed { background-color: #d4edda; }\n" +
            "        .failed { background-color: #f8d7da; }\n" +
            "        table { width: 100%%; border-collapse: collapse; margin: 20px 0; }\n" +
            "        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n" +
            "        th { background-color: #f2f2f2; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"header\">\n" +
            "        <h1>%s</h1>\n" +
            "        <p>Generated: %s</p>\n" +
            "    </div>\n",
            this.reportName + " - " + title, this.reportName + " - " + title, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
    
    private String generateSummaryTable() {
        TestExecutionStats stats = getExecutionStats();
        
        return String.format(
            "    <div class=\"stats\">\n" +
            "        <div class=\"stat-box\">\n" +
            "            <h3>Total Scenarios</h3>\n" +
            "            <h2>%d</h2>\n" +
            "        </div>\n" +
            "        <div class=\"stat-box passed\">\n" +
            "            <h3>Passed</h3>\n" +
            "            <h2>%d</h2>\n" +
            "        </div>\n" +
            "        <div class=\"stat-box failed\">\n" +
            "            <h3>Failed</h3>\n" +
            "            <h2>%d</h2>\n" +
            "        </div>\n" +
            "        <div class=\"stat-box\">\n" +
            "            <h3>Total Time</h3>\n" +
            "            <h2>%.2f sec</h2>\n" +
            "        </div>\n" +
            "    </div>\n",
            stats.getTotalScenarios(),
            stats.getPassedScenarios(),
            stats.getFailedScenarios(),
            stats.getTotalExecutionTime() / 1000.0
        );
    }
    
    private String generateDetailedResults() {
        StringBuilder table = new StringBuilder();
        table.append("    <h2>Detailed Results</h2>\n");
        table.append("    <table>\n");
        table.append("        <tr>\n");
        table.append("            <th>Feature</th>\n");
        table.append("            <th>Scenario</th>\n");
        table.append("            <th>Status</th>\n");
        table.append("            <th>Execution Time (ms)</th>\n");
        table.append("            <th>Tags</th>\n");
        table.append("            <th>Timestamp</th>\n");
        table.append("        </tr>\n");
        
        scenarioResults.values().stream()
            .sorted(Comparator.comparing(ScenarioResult::getFeatureName)
                .thenComparing(ScenarioResult::getScenarioName))
            .forEach(result -> {
                String statusClass = "PASSED".equals(result.getStatus()) ? "passed" : "failed";
                table.append(String.format(
                    "        <tr class=\"%s\">\n" +
                    "            <td>%s</td>\n" +
                    "            <td>%s</td>\n" +
                    "            <td>%s</td>\n" +
                    "            <td>%d</td>\n" +
                    "            <td>%s</td>\n" +
                    "            <td>%s</td>\n" +
                    "        </tr>\n",
                    statusClass,
                    result.getFeatureName(),
                    result.getScenarioName(),
                    result.getStatus(),
                    result.getExecutionTime(),
                    String.join(", ", result.getTags()),
                    result.getTimestamp()
                ));
            });
        
        table.append("    </table>\n");
        return table.toString();
    }
    
    private String generateHtmlFooter() {
        return "</body>\n</html>";
    }
    
    private void writeToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
    
    // Inner classes for data structures
    
    public static class ScenarioResult {
        private final String scenarioName;
        private final String status;
        private final long executionTime;
        private final String featureName;
        private final Collection<String> tags;
        private final String timestamp;
        
        public ScenarioResult(String scenarioName, String status, long executionTime,
                            String featureName, Collection<String> tags, String timestamp) {
            this.scenarioName = scenarioName;
            this.status = status;
            this.executionTime = executionTime;
            this.featureName = featureName;
            this.tags = new ArrayList<>(tags);
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getScenarioName() { return scenarioName; }
        public String getStatus() { return status; }
        public long getExecutionTime() { return executionTime; }
        public String getFeatureName() { return featureName; }
        public Collection<String> getTags() { return tags; }
        public String getTimestamp() { return timestamp; }
    }
    
    public static class TestExecutionStats {
        private final long totalScenarios;
        private final long passedScenarios;
        private final long failedScenarios;
        private final double totalExecutionTime;
        
        public TestExecutionStats(long totalScenarios, long passedScenarios,
                                long failedScenarios, double totalExecutionTime) {
            this.totalScenarios = totalScenarios;
            this.passedScenarios = passedScenarios;
            this.failedScenarios = failedScenarios;
            this.totalExecutionTime = totalExecutionTime;
        }
        
        // Getters
        public long getTotalScenarios() { return totalScenarios; }
        public long getPassedScenarios() { return passedScenarios; }
        public long getFailedScenarios() { return failedScenarios; }
        public double getTotalExecutionTime() { return totalExecutionTime; }
    }
}
