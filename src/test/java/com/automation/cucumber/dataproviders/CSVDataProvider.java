package com.automation.cucumber.dataproviders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Universal CSV Data Provider for BDD scenarios
 * Supports dynamic data loading with variable substitution and type conversion
 */
public class CSVDataProvider {
    private static final Logger logger = LoggerFactory.getLogger(CSVDataProvider.class);
    private static final String DATA_DIRECTORY = "src/test/resources/testdata/";
    private static final String CSV_EXTENSION = ".csv";
    
    /**
     * Load CSV data for a specific scenario
     * @param fileName CSV file name (without extension)
     * @return List of data maps with column headers as keys
     */
    public static List<Map<String, String>> loadData(String fileName) {
        String fullPath = DATA_DIRECTORY + fileName + CSV_EXTENSION;
        Path csvPath = Paths.get(fullPath);
        
        logger.info("Loading CSV data from: {}", fullPath);
        
        if (!Files.exists(csvPath)) {
            logger.warn("CSV file not found: {}, creating sample data", fullPath);
            createSampleDataFile(fullPath);
        }
        
        List<Map<String, String>> data = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                logger.warn("CSV file is empty or has no headers: {}", fullPath);
                return data;
            }
            
            String[] headers = parseCSVLine(headerLine);
            logger.debug("CSV headers: {}", Arrays.toString(headers));
            
            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] values = parseCSVLine(line);
                Map<String, String> rowData = new HashMap<>();
                
                for (int i = 0; i < headers.length; i++) {
                    String value = (i < values.length) ? values[i].trim() : "";
                    // Support variable substitution
                    value = substituteVariables(value);
                    rowData.put(headers[i].trim(), value);
                }
                
                rowData.put("_row_number", String.valueOf(rowNumber));
                data.add(rowData);
                rowNumber++;
            }
            
            logger.info("Loaded {} rows of data from {}", data.size(), fileName);
            
        } catch (IOException e) {
            logger.error("Failed to load CSV data from {}: {}", fullPath, e.getMessage());
            throw new RuntimeException("Failed to load test data", e);
        }
        
        return data;
    }
    
    /**
     * Load specific rows based on test case IDs
     * @param fileName CSV file name
     * @param testCaseIds List of test case IDs to load
     * @return Filtered data maps
     */
    public static List<Map<String, String>> loadDataForTestCases(String fileName, List<String> testCaseIds) {
        List<Map<String, String>> allData = loadData(fileName);
        
        return allData.stream()
            .filter(row -> testCaseIds.contains(row.get("testCaseId")) || 
                          testCaseIds.contains(row.get("test_case_id")) ||
                          testCaseIds.contains(row.get("testCase")))
            .collect(Collectors.toList());
    }
    
    /**
     * Load data with filtering based on column values
     * @param fileName CSV file name
     * @param filterColumn Column to filter on
     * @param filterValues Values to include
     * @return Filtered data maps
     */
    public static List<Map<String, String>> loadDataWithFilter(String fileName, String filterColumn, List<String> filterValues) {
        List<Map<String, String>> allData = loadData(fileName);
        
        return allData.stream()
            .filter(row -> filterValues.contains(row.get(filterColumn)))
            .collect(Collectors.toList());
    }
    
    /**
     * Get single row of data by row number
     * @param fileName CSV file name
     * @param rowNumber Row number (1-based)
     * @return Data map for the specific row
     */
    public static Map<String, String> getDataRow(String fileName, int rowNumber) {
        List<Map<String, String>> allData = loadData(fileName);
        
        if (rowNumber <= 0 || rowNumber > allData.size()) {
            throw new IllegalArgumentException("Row number " + rowNumber + " is out of range (1-" + allData.size() + ")");
        }
        
        return allData.get(rowNumber - 1);
    }
    
    /**
     * Load CSV data from specific file path
     * @param filePath Full path to CSV file
     * @return List of data maps with column headers as keys
     */
    public static List<Map<String, String>> loadFromFile(String filePath) {
        logger.info("Loading CSV data from file path: {}", filePath);
        
        if (!Files.exists(Paths.get(filePath))) {
            logger.warn("CSV file not found: {}, creating sample data", filePath);
            createSampleDataFile(filePath);
        }
        
        List<Map<String, String>> data = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                logger.warn("CSV file is empty or has no headers: {}", filePath);
                return data;
            }
            
            String[] headers = parseCSVLine(headerLine);
            logger.debug("CSV headers: {}", Arrays.toString(headers));
            
            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] values = parseCSVLine(line);
                Map<String, String> rowData = new HashMap<>();
                
                for (int i = 0; i < headers.length; i++) {
                    String value = (i < values.length) ? values[i].trim() : "";
                    // Support variable substitution
                    value = substituteVariables(value);
                    rowData.put(headers[i].trim(), value);
                }
                
                rowData.put("_row_number", String.valueOf(rowNumber));
                data.add(rowData);
                rowNumber++;
            }
            
            logger.info("Loaded {} rows of data from file: {}", data.size(), filePath);
            
        } catch (IOException e) {
            logger.error("Failed to load CSV data from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load test data", e);
        }
        
        return data;
    }
    
    /**
     * Load CSV data from file path and convert to Object array for data providers
     * @param filePath Full path to CSV file
     * @return Object array suitable for TestNG data providers
     */
    public static Object[][] loadFromFile(String filePath, String unusedParam) {
        List<Map<String, String>> data = loadFromFile(filePath);
        Object[][] result = new Object[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            result[i] = new Object[]{data.get(i)};
        }
        return result;
    }

    /**
     * Parse CSV line handling quoted values and commas
     * @param line CSV line to parse
     * @return Array of parsed values
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
      /**
     * Substitute environment variables and system properties
     * @param value Value that may contain variables
     * @return Value with substituted variables
     */
    private static String substituteVariables(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        String result = value;
        
        // Environment variables: ${ENV_VAR}
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(result);
        
        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = null;
            
            // Try environment variables first
            String envValue = System.getenv(varName);
            if (envValue != null) {
                replacement = envValue;
            } else {
                // Try system properties
                String propValue = System.getProperty(varName);
                if (propValue != null) {
                    replacement = propValue;
                }
            }
            
            if (replacement != null) {
                result = result.replace(matcher.group(0), replacement);
            } else {
                logger.warn("Variable not found: {}", varName);
            }
        }
        
        // Special placeholders
        result = result.replace("${TIMESTAMP}", String.valueOf(System.currentTimeMillis()));
        result = result.replace("${DATE}", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        result = result.replace("${TIME}", new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()));
        result = result.replace("${RANDOM}", String.valueOf(new Random().nextInt(10000)));
        
        return result;
    }
    
    /**
     * Create sample data file for testing
     * @param filePath Path to create the sample file
     */
    private static void createSampleDataFile(String filePath) {
        try {
            Path parentDir = Paths.get(filePath).getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            
            String sampleContent = """
                # Sample CSV data file for universal automation testing
                testCaseId,description,application,input1,input2,expected,priority,enabled
                TC001,Basic calculator addition,calculator,2,3,5,high,true
                TC002,Calculator multiplication,calculator,4,5,20,medium,true
                TC003,Text editor input,notepad,"Hello World","","Hello World",low,true
                TC004,Login validation,any_app,"${TEST_USERNAME}","${TEST_PASSWORD}",success,high,true
                TC005,Date validation,any_app,"${DATE}","","valid",medium,false
                """;
            
            Files.write(Paths.get(filePath), sampleContent.getBytes());
            logger.info("Created sample CSV file: {}", filePath);
            
        } catch (IOException e) {
            logger.error("Failed to create sample CSV file: {}", e.getMessage());
        }
    }
    
    /**
     * Validate CSV data structure
     * @param fileName CSV file name
     * @return Validation results
     */
    public static Map<String, Object> validateDataStructure(String fileName) {
        Map<String, Object> validation = new HashMap<>();
        
        try {
            List<Map<String, String>> data = loadData(fileName);
            
            validation.put("isValid", true);
            validation.put("rowCount", data.size());
            validation.put("columnCount", data.isEmpty() ? 0 : data.get(0).size());
            
            if (!data.isEmpty()) {
                validation.put("columns", new ArrayList<>(data.get(0).keySet()));
            }
            
            // Check for required columns
            List<String> requiredColumns = Arrays.asList("testCaseId", "description", "enabled");
            List<String> missingColumns = new ArrayList<>();
            
            if (!data.isEmpty()) {
                Set<String> availableColumns = data.get(0).keySet();
                for (String required : requiredColumns) {
                    if (!availableColumns.contains(required)) {
                        missingColumns.add(required);
                    }
                }
            }
            
            validation.put("missingColumns", missingColumns);
            validation.put("hasRequiredColumns", missingColumns.isEmpty());
            
            logger.info("CSV validation for {}: {} rows, {} columns", fileName, data.size(), 
                data.isEmpty() ? 0 : data.get(0).size());
            
        } catch (Exception e) {
            validation.put("isValid", false);
            validation.put("error", e.getMessage());
            logger.error("CSV validation failed for {}: {}", fileName, e.getMessage());
        }
        
        return validation;
    }
}
