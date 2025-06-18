package com.automation.cucumber.dataproviders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Universal Data Provider - Dynamic test data sourcing for any application
 * Provides universal data management that works with ANY application universally
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class UniversalDataProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(UniversalDataProvider.class);
    private static final String DATA_BASE_PATH = "src/test/resources/testdata";
    
    /**
     * Universal data provider that works with any test method
     * Automatically discovers and loads appropriate test data
     */
    public static Object[][] getUniversalData(Method method) {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        
        logger.info("Loading universal data for method: {}.{}", className, methodName);
        
        // Try multiple data sources in order of preference
        Object[][] data = tryLoadFromCSV(methodName, className);
        if (data != null) return data;
        
        data = tryLoadFromExcel(methodName, className);
        if (data != null) return data;
        
        data = tryLoadFromProperties(methodName, className);
        if (data != null) return data;
        
        // Generate dynamic data as fallback
        return generateDynamicData(methodName, className);
    }
    
    /**
     * Get test data for specific application
     */
    public static Object[][] getApplicationData(String applicationName, String testType) {
        logger.info("Loading application data for: {} - {}", applicationName, testType);
        
        String fileName = applicationName.toLowerCase() + "_" + testType.toLowerCase();
        
        Object[][] data = tryLoadFromCSV(fileName, "application_data");
        if (data != null) return data;
        
        return generateDefaultApplicationData(applicationName, testType);
    }
    
    /**
     * Get universal test scenarios data
     */
    public static Object[][] getUniversalScenarios() {
        List<Map<String, String>> scenarios = new ArrayList<>();
        
        // Universal calculator scenarios
        scenarios.add(createScenario("Calculator", "Addition", "2", "3", "5"));
        scenarios.add(createScenario("Calculator", "Subtraction", "10", "3", "7"));
        scenarios.add(createScenario("Calculator", "Multiplication", "4", "5", "20"));
        scenarios.add(createScenario("Calculator", "Division", "20", "4", "5"));
        
        // Universal notepad scenarios
        scenarios.add(createScenario("Notepad", "Type Text", "Hello World", "", "Hello World"));
        scenarios.add(createScenario("Notepad", "Save File", "test.txt", "", "File Saved"));
        scenarios.add(createScenario("Notepad", "Open File", "test.txt", "", "File Opened"));
        
        // Universal application scenarios
        scenarios.add(createScenario("Any App", "Function Key", "F5", "", "Refresh"));
        scenarios.add(createScenario("Any App", "Menu Navigation", "File>Open", "", "Menu Opened"));
        scenarios.add(createScenario("Any App", "Copy Paste", "Test Data", "", "Data Copied"));
        
        return convertToObjectArray(scenarios);
    }
    
    /**
     * Get performance test data
     */
    public static Object[][] getPerformanceData() {
        List<Map<String, String>> performanceTests = new ArrayList<>();
        
        // Response time tests
        performanceTests.add(createPerformanceScenario("Calculator", "Simple Operation", "1000", "ms"));
        performanceTests.add(createPerformanceScenario("Notepad", "File Open", "2000", "ms"));
        performanceTests.add(createPerformanceScenario("Any App", "Menu Navigation", "500", "ms"));
        
        // Throughput tests
        performanceTests.add(createPerformanceScenario("Calculator", "Batch Operations", "50", "ops/sec"));
        performanceTests.add(createPerformanceScenario("Notepad", "Text Input", "100", "chars/sec"));
        
        // Load tests
        performanceTests.add(createPerformanceScenario("Any App", "Concurrent Users", "10", "users"));
        performanceTests.add(createPerformanceScenario("Any App", "Extended Load", "300", "seconds"));
        
        return convertToObjectArray(performanceTests);
    }
    
    /**
     * Get integration test data
     */
    public static Object[][] getIntegrationData() {
        List<Map<String, String>> integrationTests = new ArrayList<>();
        
        // Cross-application workflows
        integrationTests.add(createIntegrationScenario("Calculator", "Notepad", "clipboard", "number"));
        integrationTests.add(createIntegrationScenario("Notepad", "Calculator", "file_export", "formula"));
        integrationTests.add(createIntegrationScenario("Excel", "Calculator", "cell_data", "calculation"));
        integrationTests.add(createIntegrationScenario("Oracle Forms", "Excel", "query_result", "report"));
        integrationTests.add(createIntegrationScenario("SAP GUI", "AS400", "data_sync", "transaction"));
        
        // Universal integration patterns
        integrationTests.add(createIntegrationScenario("Any App", "Any App", "universal_copy", "data"));
        integrationTests.add(createIntegrationScenario("Source App", "Target App", "file_transfer", "document"));
        integrationTests.add(createIntegrationScenario("Monitor App", "Action App", "event_trigger", "response"));
        
        return convertToObjectArray(integrationTests);
    }
    
    /**
     * Get error scenario data
     */
    public static Object[][] getErrorScenarioData() {
        List<Map<String, String>> errorScenarios = new ArrayList<>();
        
        // Invalid input scenarios
        errorScenarios.add(createErrorScenario("Calculator", "Division by Zero", "10/0", "Error"));
        errorScenarios.add(createErrorScenario("Notepad", "Invalid File Path", "C:\\invalid\\path.txt", "File Not Found"));
        errorScenarios.add(createErrorScenario("Any App", "Invalid Menu", "NonExistent>Menu", "Menu Not Found"));
        
        // Performance limit scenarios
        errorScenarios.add(createErrorScenario("Any App", "Timeout", "30000ms", "Operation Timeout"));
        errorScenarios.add(createErrorScenario("Any App", "Memory Limit", "1GB", "Out of Memory"));
        
        // Recovery scenarios
        errorScenarios.add(createErrorScenario("Any App", "Connection Lost", "Network Error", "Reconnect"));
        errorScenarios.add(createErrorScenario("Any App", "Application Crash", "Unexpected Exit", "Restart"));
        
        return convertToObjectArray(errorScenarios);
    }
    
    /**
     * Get boundary test data
     */
    public static Object[][] getBoundaryTestData() {
        List<Map<String, String>> boundaryTests = new ArrayList<>();
        
        // Numeric boundaries
        boundaryTests.add(createBoundaryScenario("Calculator", "Max Integer", String.valueOf(Integer.MAX_VALUE), "Valid"));
        boundaryTests.add(createBoundaryScenario("Calculator", "Min Integer", String.valueOf(Integer.MIN_VALUE), "Valid"));
        boundaryTests.add(createBoundaryScenario("Calculator", "Zero", "0", "Valid"));
        
        // String boundaries
        boundaryTests.add(createBoundaryScenario("Notepad", "Empty String", "", "Valid"));
        boundaryTests.add(createBoundaryScenario("Notepad", "Single Character", "A", "Valid"));
        boundaryTests.add(createBoundaryScenario("Notepad", "Max Length", generateLongString(1000), "Valid"));
        
        // Time boundaries
        boundaryTests.add(createBoundaryScenario("Any App", "Instant Response", "0ms", "Valid"));
        boundaryTests.add(createBoundaryScenario("Any App", "Max Timeout", "60000ms", "Valid"));
        
        return convertToObjectArray(boundaryTests);
    }
    
    // Private helper methods
      private static Object[][] tryLoadFromCSV(String fileName, String category) {
        try {
            List<Map<String, String>> data = CSVDataProvider.loadData(fileName);
            return convertToObjectArray(data);
        } catch (Exception e) {
            logger.debug("Could not load CSV data for: {}", fileName);
            return null;
        }
    }
      private static Object[][] tryLoadFromExcel(String fileName, String category) {
        try {
            List<Map<String, String>> data = ExcelDataProvider.loadData(fileName + ".xlsx");
            return convertToObjectArray(data);
        } catch (Exception e) {
            logger.debug("Could not load Excel data for: {}", fileName);
            return null;
        }
    }
    
    private static Object[][] tryLoadFromProperties(String fileName, String category) {
        String filePath = DATA_BASE_PATH + "/" + category + "/" + fileName + ".properties";
        try {
            Properties props = new Properties();
            props.load(new FileReader(filePath));
            return convertPropertiesToObjectArray(props);
        } catch (Exception e) {
            logger.debug("Could not load properties data from: {}", filePath);
            return null;
        }
    }
    
    private static Object[][] generateDynamicData(String methodName, String className) {
        logger.info("Generating dynamic data for: {}.{}", className, methodName);
        
        List<Map<String, String>> dynamicData = new ArrayList<>();
        
        // Generate based on method name patterns
        if (methodName.toLowerCase().contains("calculator")) {
            dynamicData.add(createScenario("Calculator", "Dynamic Test", "1", "1", "2"));
        } else if (methodName.toLowerCase().contains("notepad")) {
            dynamicData.add(createScenario("Notepad", "Dynamic Test", "Dynamic Text", "", "Text Entered"));
        } else if (methodName.toLowerCase().contains("performance")) {
            dynamicData.add(createPerformanceScenario("Dynamic App", "Dynamic Operation", "1000", "ms"));
        } else {
            // Universal fallback
            dynamicData.add(createScenario("Universal App", "Universal Test", "Test Input", "", "Test Output"));
        }
        
        return convertToObjectArray(dynamicData);
    }
    
    private static Object[][] generateDefaultApplicationData(String applicationName, String testType) {
        List<Map<String, String>> defaultData = new ArrayList<>();
        
        switch (testType.toLowerCase()) {
            case "smoke":
                defaultData.add(createScenario(applicationName, "Launch", "", "", "Application Started"));
                defaultData.add(createScenario(applicationName, "Basic Function", "Test", "", "Function Works"));
                defaultData.add(createScenario(applicationName, "Close", "", "", "Application Closed"));
                break;
            case "regression":
                defaultData.add(createScenario(applicationName, "Full Feature Test", "Complete Test", "", "All Features Work"));
                break;
            default:
                defaultData.add(createScenario(applicationName, "Default Test", "Default Input", "", "Default Output"));
        }
        
        return convertToObjectArray(defaultData);
    }
    
    private static Map<String, String> createScenario(String app, String operation, String input1, String input2, String expected) {
        Map<String, String> scenario = new HashMap<>();
        scenario.put("application", app);
        scenario.put("operation", operation);
        scenario.put("input1", input1);
        scenario.put("input2", input2);
        scenario.put("expected", expected);
        scenario.put("testType", "functional");
        return scenario;
    }
    
    private static Map<String, String> createPerformanceScenario(String app, String operation, String threshold, String unit) {
        Map<String, String> scenario = new HashMap<>();
        scenario.put("application", app);
        scenario.put("operation", operation);
        scenario.put("threshold", threshold);
        scenario.put("unit", unit);
        scenario.put("testType", "performance");
        return scenario;
    }
    
    private static Map<String, String> createIntegrationScenario(String sourceApp, String targetApp, String method, String dataType) {
        Map<String, String> scenario = new HashMap<>();
        scenario.put("sourceApp", sourceApp);
        scenario.put("targetApp", targetApp);
        scenario.put("integrationMethod", method);
        scenario.put("dataType", dataType);
        scenario.put("testType", "integration");
        return scenario;
    }
    
    private static Map<String, String> createErrorScenario(String app, String errorType, String trigger, String expected) {
        Map<String, String> scenario = new HashMap<>();
        scenario.put("application", app);
        scenario.put("errorType", errorType);
        scenario.put("trigger", trigger);
        scenario.put("expectedError", expected);
        scenario.put("testType", "error");
        return scenario;
    }
    
    private static Map<String, String> createBoundaryScenario(String app, String boundaryType, String value, String expected) {
        Map<String, String> scenario = new HashMap<>();
        scenario.put("application", app);
        scenario.put("boundaryType", boundaryType);
        scenario.put("boundaryValue", value);
        scenario.put("expected", expected);
        scenario.put("testType", "boundary");
        return scenario;
    }
    
    private static Object[][] convertToObjectArray(List<Map<String, String>> dataList) {
        Object[][] result = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            result[i] = new Object[]{dataList.get(i)};
        }
        return result;
    }
    
    private static Object[][] convertPropertiesToObjectArray(Properties props) {
        List<Map<String, String>> dataList = new ArrayList<>();
        Map<String, String> dataMap = new HashMap<>();
        
        for (String key : props.stringPropertyNames()) {
            dataMap.put(key, props.getProperty(key));
        }
        
        dataList.add(dataMap);
        return convertToObjectArray(dataList);
    }
      private static String generateLongString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('A' + (i % 26)));
        }
        return sb.toString();
    }
    
    // ===== NEW INTEGRATION METHODS WITH CSV AND EXCEL PROVIDERS =====
    
    /**
     * Load test data using new universal data provider system
     * @param dataSource Data source identifier (e.g., "users.csv", "testdata.xlsx:Sheet1")
     * @return List of data maps
     */
    public static List<Map<String, String>> loadTestDataNew(String dataSource) {
        logger.info("Loading test data from new provider: {}", dataSource);
        
        try {
            if (dataSource.toLowerCase().endsWith(".csv")) {
                String fileName = dataSource.replace(".csv", "");
                return CSVDataProvider.loadData(fileName);
            } else if (dataSource.toLowerCase().contains(".xlsx") || dataSource.toLowerCase().contains(".xls")) {
                String[] parts = dataSource.split(":");
                String fileName = parts[0];
                
                if (parts.length > 1) {
                    String sheetName = parts[1];
                    return ExcelDataProvider.loadData(fileName, sheetName);
                } else {
                    return ExcelDataProvider.loadData(fileName);
                }
            } else {
                return CSVDataProvider.loadData(dataSource);
            }
        } catch (Exception e) {
            logger.error("Failed to load data from source {}: {}", dataSource, e.getMessage());
            throw new RuntimeException("Failed to load test data from: " + dataSource, e);
        }
    }
    
    /**
     * Load specific test case using new provider system
     * @param dataSource Data source identifier
     * @param testCaseId Test case ID to load
     * @return Single test case data map
     */
    public static Map<String, String> loadTestCaseNew(String dataSource, String testCaseId) {
        List<Map<String, String>> allData = loadTestDataNew(dataSource);
        
        return allData.stream()
            .filter(row -> testCaseId.equals(row.get("testCaseId")) || 
                          testCaseId.equals(row.get("test_case_id")) ||
                          testCaseId.equals(row.get("testCase")))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));
    }
    
    /**
     * Create sample test data files using new providers
     */
    public static void createSampleDataFilesNew() {
        logger.info("Creating sample test data files using new providers...");
        
        // This will trigger sample file creation in both providers
        try {
            CSVDataProvider.loadData("sample_tests");
            ExcelDataProvider.loadData("sample_tests.xlsx");
            logger.info("Sample test data files created successfully using new providers");
        } catch (Exception e) {
            logger.error("Failed to create sample files: {}", e.getMessage());
        }
    }
}
