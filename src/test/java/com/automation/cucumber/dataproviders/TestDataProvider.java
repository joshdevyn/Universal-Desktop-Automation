package com.automation.cucumber.dataproviders;

import com.automation.cucumber.utilities.CucumberUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.cucumber.datatable.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Test Data Provider - Manages test data loading and transformation
 * Supports CSV, JSON, and DataTable formats for BDD scenarios
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class TestDataProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataProvider.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final CsvMapper csvMapper = new CsvMapper();
    
    /**
     * Load test data from CSV file
     * 
     * @param filename CSV filename (relative to test data directory)
     * @return List of maps representing CSV rows
     */
    public static List<Map<String, String>> loadCsvData(String filename) {
        String fullPath = Paths.get(CucumberUtils.getTestDataPath(), filename).toString();
        
        try {
            logger.info("Loading CSV data from: {}", fullPath);
            
            File csvFile = new File(fullPath);
            if (!csvFile.exists()) {
                throw new FileNotFoundException("CSV file not found: " + fullPath);
            }
            
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            List<Map<String, String>> data = csvMapper
                .readerFor(Map.class)
                .with(schema)
                .<Map<String, String>>readValues(csvFile)
                .readAll();
            
            logger.info("Loaded {} rows from CSV file: {}", data.size(), filename);
            return data;
            
        } catch (Exception e) {
            logger.error("Failed to load CSV data from {}: {}", fullPath, e.getMessage());
            throw new RuntimeException("Failed to load CSV data: " + filename, e);
        }
    }
    
    /**
     * Load test data from JSON file
     * 
     * @param filename JSON filename (relative to test data directory)
     * @param dataClass Target data class
     * @param <T> Generic type
     * @return List of objects
     */
    public static <T> List<T> loadJsonData(String filename, Class<T> dataClass) {
        String fullPath = Paths.get(CucumberUtils.getTestDataPath(), filename).toString();
        
        try {
            logger.info("Loading JSON data from: {}", fullPath);
            
            String jsonContent = new String(Files.readAllBytes(Paths.get(fullPath)));
            List<T> data = jsonMapper.readValue(jsonContent, 
                jsonMapper.getTypeFactory().constructCollectionType(List.class, dataClass));
            
            logger.info("Loaded {} objects from JSON file: {}", data.size(), filename);
            return data;
            
        } catch (Exception e) {
            logger.error("Failed to load JSON data from {}: {}", fullPath, e.getMessage());
            throw new RuntimeException("Failed to load JSON data: " + filename, e);
        }
    }
    
    /**
     * Convert DataTable to list of maps
     * 
     * @param dataTable Cucumber DataTable
     * @return List of maps representing table rows
     */
    public static List<Map<String, String>> convertDataTable(DataTable dataTable) {
        try {
            List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
            logger.debug("Converted DataTable with {} rows", data.size());
            return data;
        } catch (Exception e) {
            logger.error("Failed to convert DataTable: {}", e.getMessage());
            throw new RuntimeException("Failed to convert DataTable", e);
        }
    }
    
    /**
     * Load application-specific test data
     * 
     * @param applicationName Application name
     * @param dataSetName Data set name
     * @return Map of test data
     */
    public static Map<String, String> loadApplicationData(String applicationName, String dataSetName) {
        String filename = String.format("%s_%s.csv", applicationName, dataSetName);
        List<Map<String, String>> data = loadCsvData(filename);
        
        if (data.isEmpty()) {
            logger.warn("No test data found for application: {}, dataset: {}", applicationName, dataSetName);
            return new HashMap<>();
        }
        
        // Return first row if multiple rows exist
        return data.get(0);
    }
    
    /**
     * Load user credentials data
     * 
     * @param userType User type (admin, standard, guest, etc.)
     * @return User credentials map
     */
    public static Map<String, String> loadUserCredentials(String userType) {
        try {
            List<Map<String, String>> credentials = loadCsvData("user_credentials.csv");
            
            Optional<Map<String, String>> userCreds = credentials.stream()
                .filter(cred -> userType.equalsIgnoreCase(cred.get("user_type")))
                .findFirst();
            
            if (userCreds.isPresent()) {
                logger.debug("Loaded credentials for user type: {}", userType);
                return userCreds.get();
            } else {
                logger.warn("No credentials found for user type: {}", userType);
                return new HashMap<>();
            }
            
        } catch (Exception e) {
            logger.error("Failed to load user credentials for type {}: {}", userType, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Load test configuration data
     * 
     * @param configName Configuration name
     * @return Configuration map
     */
    public static Map<String, String> loadTestConfiguration(String configName) {
        try {
            List<Map<String, String>> configs = loadCsvData("test_configurations.csv");
            
            Optional<Map<String, String>> config = configs.stream()
                .filter(cfg -> configName.equalsIgnoreCase(cfg.get("config_name")))
                .findFirst();
            
            if (config.isPresent()) {
                logger.debug("Loaded test configuration: {}", configName);
                return config.get();
            } else {
                logger.warn("No test configuration found: {}", configName);
                return new HashMap<>();
            }
            
        } catch (Exception e) {
            logger.error("Failed to load test configuration {}: {}", configName, e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Generate test data combinations
     * 
     * @param parameters Map of parameter lists
     * @return List of all parameter combinations
     */
    public static List<Map<String, String>> generateCombinations(Map<String, List<String>> parameters) {
        List<Map<String, String>> combinations = new ArrayList<>();
        
        if (parameters.isEmpty()) {
            return combinations;
        }
        
        List<String> keys = new ArrayList<>(parameters.keySet());
        generateCombinationsRecursive(parameters, keys, 0, new HashMap<>(), combinations);
        
        logger.info("Generated {} test data combinations", combinations.size());
        return combinations;
    }
    
    private static void generateCombinationsRecursive(
            Map<String, List<String>> parameters,
            List<String> keys,
            int keyIndex,
            Map<String, String> current,
            List<Map<String, String>> combinations) {
        
        if (keyIndex == keys.size()) {
            combinations.add(new HashMap<>(current));
            return;
        }
        
        String currentKey = keys.get(keyIndex);
        List<String> values = parameters.get(currentKey);
        
        for (String value : values) {
            current.put(currentKey, value);
            generateCombinationsRecursive(parameters, keys, keyIndex + 1, current, combinations);
        }
    }
    
    /**
     * Filter test data by criteria
     * 
     * @param data Original test data
     * @param filterCriteria Filter criteria map
     * @return Filtered test data
     */
    public static List<Map<String, String>> filterTestData(
            List<Map<String, String>> data, 
            Map<String, String> filterCriteria) {
        
        return data.stream()
            .filter(row -> {
                for (Map.Entry<String, String> criteria : filterCriteria.entrySet()) {
                    String rowValue = row.get(criteria.getKey());
                    if (rowValue == null || !rowValue.equals(criteria.getValue())) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Create test data template file
     * 
     * @param filename Template filename
     * @param headers Column headers
     * @param sampleData Sample data rows
     */
    public static void createTestDataTemplate(String filename, List<String> headers, List<List<String>> sampleData) {
        String fullPath = Paths.get(CucumberUtils.getTestDataPath(), filename).toString();
        
        try {
            CucumberUtils.ensureDirectoryExists(CucumberUtils.getTestDataPath());
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fullPath))) {
                // Write headers
                writer.println(String.join(",", headers));
                
                // Write sample data
                for (List<String> row : sampleData) {
                    writer.println(String.join(",", row));
                }
            }
            
            logger.info("Created test data template: {}", fullPath);
            
        } catch (Exception e) {
            logger.error("Failed to create test data template {}: {}", fullPath, e.getMessage());
            throw new RuntimeException("Failed to create test data template: " + filename, e);
        }
    }
    
    /**
     * Validate test data structure
     * 
     * @param data Test data to validate
     * @param requiredFields Required field names
     * @return Validation result
     */
    public static boolean validateTestData(List<Map<String, String>> data, List<String> requiredFields) {
        if (data.isEmpty()) {
            logger.warn("Test data is empty");
            return false;
        }
        
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            
            for (String field : requiredFields) {
                if (!row.containsKey(field) || row.get(field) == null || row.get(field).trim().isEmpty()) {
                    logger.error("Row {} missing required field: {}", i + 1, field);
                    return false;
                }
            }
        }
        
        logger.debug("Test data validation passed for {} rows", data.size());
        return true;
    }
    
    /**
     * Convert test data to CSV format
     * 
     * @param data Test data
     * @param outputPath Output file path
     */
    public static void exportToCsv(List<Map<String, String>> data, String outputPath) {
        try {
            if (data.isEmpty()) {
                logger.warn("No data to export");
                return;
            }
            
            Set<String> allKeys = data.stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet());
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
                // Write headers
                writer.println(String.join(",", allKeys));
                
                // Write data rows
                for (Map<String, String> row : data) {
                    List<String> values = allKeys.stream()
                        .map(key -> row.getOrDefault(key, ""))
                        .collect(Collectors.toList());
                    writer.println(String.join(",", values));
                }
            }
            
            logger.info("Exported {} rows to CSV: {}", data.size(), outputPath);
            
        } catch (Exception e) {
            logger.error("Failed to export data to CSV {}: {}", outputPath, e.getMessage());
            throw new RuntimeException("Failed to export to CSV: " + outputPath, e);
        }
    }
}
