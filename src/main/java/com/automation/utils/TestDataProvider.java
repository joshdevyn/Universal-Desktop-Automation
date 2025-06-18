package com.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for managing test data from various sources including
 * CSV files, Excel files, JSON, YAML, and database connections.
 * Supports data-driven testing with flexible data providers.
 */
public class TestDataProvider {
    
    private static final String DATA_DIR = "src/test/resources/testdata/";
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    
    /**
     * Loads test data from CSV file
     * @param fileName CSV file name
     * @return List of Maps representing each row
     */
    public static List<Map<String, String>> loadCSVData(String fileName) {
        List<Map<String, String>> data = new ArrayList<>();
        String filePath = DATA_DIR + fileName;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = null;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                
                if (isFirstLine) {
                    headers = values;
                    isFirstLine = false;
                    continue;
                }
                
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }
                data.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CSV data from " + fileName, e);
        }
        
        return data;
    }
    
    /**
     * Loads test data from YAML file
     * @param fileName YAML file name
     * @return Map containing the data structure
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadYAMLData(String fileName) {
        String filePath = DATA_DIR + fileName;
        
        try {
            return yamlMapper.readValue(new File(filePath), Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load YAML data from " + fileName, e);
        }
    }
    
    /**
     * Loads test data from JSON file
     * @param fileName JSON file name
     * @return Map containing the data structure
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadJSONData(String fileName) {
        String filePath = DATA_DIR + fileName;
        
        try {
            return jsonMapper.readValue(new File(filePath), Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON data from " + fileName, e);
        }
    }
    
    /**
     * Loads properties file data
     * @param fileName Properties file name
     * @return Properties object
     */
    public static Properties loadPropertiesData(String fileName) {
        Properties props = new Properties();
        String filePath = DATA_DIR + fileName;
        
        try (InputStream input = new FileInputStream(filePath)) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties data from " + fileName, e);
        }
        
        return props;
    }
    
    /**
     * Converts data to TestNG DataProvider format
     * @param data List of test data maps
     * @return Object[][] for TestNG DataProvider
     */
    public static Object[][] toDataProviderFormat(List<Map<String, String>> data) {
        Object[][] result = new Object[data.size()][];
        
        for (int i = 0; i < data.size(); i++) {
            result[i] = new Object[]{data.get(i)};
        }
        
        return result;
    }
    
    /**
     * Filters test data based on criteria
     * @param data Original test data
     * @param filterKey Key to filter on
     * @param filterValue Value to match
     * @return Filtered data
     */
    public static List<Map<String, String>> filterData(List<Map<String, String>> data, 
                                                       String filterKey, String filterValue) {
        return data.stream()
                .filter(row -> filterValue.equals(row.get(filterKey)))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets test data for specific test case
     * @param fileName Data file name
     * @param testCaseName Test case identifier
     * @return Test data for the specific test case
     */
    public static Map<String, String> getTestCaseData(String fileName, String testCaseName) {
        List<Map<String, String>> data = loadCSVData(fileName);
        return data.stream()
                .filter(row -> testCaseName.equals(row.get("testCase")))
                .findFirst()
                .orElse(new HashMap<>());
    }
    
    /**
     * Gets all test data for a specific test suite
     * @param fileName Data file name
     * @param suiteName Test suite identifier
     * @return List of test data for the suite
     */
    public static List<Map<String, String>> getTestSuiteData(String fileName, String suiteName) {
        List<Map<String, String>> data = loadCSVData(fileName);
        return filterData(data, "testSuite", suiteName);
    }
    
    /**
     * Generates random test data based on patterns
     * @param pattern Data pattern (e.g., "user_####" for user_1234)
     * @return Generated data
     */
    public static String generateTestData(String pattern) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        
        for (char c : pattern.toCharArray()) {
            if (c == '#') {
                result.append(random.nextInt(10));
            } else if (c == '@') {
                result.append((char) ('a' + random.nextInt(26)));
            } else if (c == '&') {
                result.append((char) ('A' + random.nextInt(26)));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Validates test data against schema
     * @param data Test data to validate
     * @param requiredFields Required field names
     * @return true if valid, false otherwise
     */
    public static boolean validateTestData(Map<String, String> data, String... requiredFields) {
        for (String field : requiredFields) {
            if (!data.containsKey(field) || data.get(field) == null || data.get(field).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Merges multiple data sources
     * @param primaryData Primary data source
     * @param secondaryData Secondary data source (overrides primary)
     * @return Merged data
     */
    public static Map<String, String> mergeData(Map<String, String> primaryData, 
                                               Map<String, String> secondaryData) {
        Map<String, String> merged = new HashMap<>(primaryData);
        merged.putAll(secondaryData);
        return merged;
    }
    
    /**
     * Creates test data from environment variables
     * @param prefix Prefix for environment variables
     * @return Map of test data from environment
     */
    public static Map<String, String> getEnvironmentData(String prefix) {
        Map<String, String> envData = new HashMap<>();
        
        System.getenv().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .forEach(entry -> {
                    String key = entry.getKey().substring(prefix.length()).toLowerCase();
                    envData.put(key, entry.getValue());
                });
        
        return envData;
    }
}
