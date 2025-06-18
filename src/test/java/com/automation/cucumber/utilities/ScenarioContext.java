package com.automation.cucumber.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scenario Context - Universal test state management between steps
 * Provides thread-safe state management for sharing data between steps in any scenario
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class ScenarioContext {
    
    private static final Logger logger = LoggerFactory.getLogger(ScenarioContext.class);
    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(ConcurrentHashMap::new);
    
    /**
     * Store a value in the scenario context
     * 
     * @param key Unique key for the value
     * @param value Value to store
     */
    public static void setValue(String key, Object value) {
        context.get().put(key, value);
        logger.debug("Stored value in scenario context: {} = {}", key, value);
    }
    
    /**
     * Retrieve a value from the scenario context
     * 
     * @param key Key to retrieve
     * @return Stored value or null if not found
     */
    public static Object getValue(String key) {
        Object value = context.get().get(key);
        logger.debug("Retrieved value from scenario context: {} = {}", key, value);
        return value;
    }
    
    /**
     * Retrieve a string value from the scenario context
     * 
     * @param key Key to retrieve
     * @return String value or null if not found
     */
    public static String getStringValue(String key) {
        Object value = getValue(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Retrieve an integer value from the scenario context
     * 
     * @param key Key to retrieve
     * @return Integer value or null if not found or not convertible
     */
    public static Integer getIntegerValue(String key) {
        Object value = getValue(key);
        if (value == null) return null;
        
        try {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                return Integer.parseInt(value.toString());
            }
        } catch (NumberFormatException e) {
            logger.warn("Failed to convert value to integer: {} = {}", key, value);
            return null;
        }
    }
    
    /**
     * Retrieve a boolean value from the scenario context
     * 
     * @param key Key to retrieve
     * @return Boolean value or null if not found
     */
    public static Boolean getBooleanValue(String key) {
        Object value = getValue(key);
        if (value == null) return null;
        
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return Boolean.parseBoolean(value.toString());
        }
    }
    
    /**
     * Check if a key exists in the scenario context
     * 
     * @param key Key to check
     * @return true if key exists, false otherwise
     */
    public static boolean containsKey(String key) {
        boolean exists = context.get().containsKey(key);
        logger.debug("Key existence check: {} = {}", key, exists);
        return exists;
    }
    
    /**
     * Remove a value from the scenario context
     * 
     * @param key Key to remove
     * @return Removed value or null if not found
     */
    public static Object removeValue(String key) {
        Object removed = context.get().remove(key);
        logger.debug("Removed value from scenario context: {} = {}", key, removed);
        return removed;
    }
    
    /**
     * Get all keys in the scenario context
     * 
     * @return Set of all keys
     */
    public static java.util.Set<String> getAllKeys() {
        return context.get().keySet();
    }
    
    /**
     * Get all values in the scenario context
     * 
     * @return Map of all key-value pairs
     */
    public static Map<String, Object> getAllValues() {
        return new ConcurrentHashMap<>(context.get());
    }
    
    /**
     * Clear the scenario context
     */
    public static void clear() {
        int size = context.get().size();
        context.get().clear();
        logger.debug("Cleared scenario context ({} items removed)", size);
    }
    
    /**
     * Get the size of the scenario context
     * 
     * @return Number of items in context
     */
    public static int size() {
        return context.get().size();
    }
    
    /**
     * Check if the scenario context is empty
     * 
     * @return true if empty, false otherwise
     */
    public static boolean isEmpty() {
        return context.get().isEmpty();
    }
    
    /**
     * Store application-specific data
     * 
     * @param applicationName Name of the application
     * @param key Key for the data
     * @param value Value to store
     */
    public static void setApplicationValue(String applicationName, String key, Object value) {
        String appKey = "app." + applicationName.toLowerCase() + "." + key;
        setValue(appKey, value);
    }
    
    /**
     * Retrieve application-specific data
     * 
     * @param applicationName Name of the application
     * @param key Key for the data
     * @return Stored value or null if not found
     */
    public static Object getApplicationValue(String applicationName, String key) {
        String appKey = "app." + applicationName.toLowerCase() + "." + key;
        return getValue(appKey);
    }
    
    /**
     * Store test step data
     * 
     * @param stepName Name of the step
     * @param key Key for the data
     * @param value Value to store
     */
    public static void setStepValue(String stepName, String key, Object value) {
        String stepKey = "step." + sanitizeKey(stepName) + "." + key;
        setValue(stepKey, value);
    }
    
    /**
     * Retrieve test step data
     * 
     * @param stepName Name of the step
     * @param key Key for the data
     * @return Stored value or null if not found
     */
    public static Object getStepValue(String stepName, String key) {
        String stepKey = "step." + sanitizeKey(stepName) + "." + key;
        return getValue(stepKey);
    }
    
    /**
     * Store extracted data from any application
     * 
     * @param sourceApplication Source application name
     * @param extractionMethod Method used for extraction
     * @param data Extracted data
     */
    public static void setExtractedData(String sourceApplication, String extractionMethod, String data) {
        setValue("extracted.source", sourceApplication);
        setValue("extracted.method", extractionMethod);
        setValue("extracted.data", data);
        setValue("extracted.timestamp", System.currentTimeMillis());
    }
    
    /**
     * Get extracted data
     * 
     * @return Extracted data or null if not found
     */
    public static String getExtractedData() {
        return getStringValue("extracted.data");
    }
    
    /**
     * Get extracted data source
     * 
     * @return Source application name or null if not found
     */
    public static String getExtractedDataSource() {
        return getStringValue("extracted.source");
    }
    
    /**
     * Store performance metrics
     * 
     * @param operationName Name of the operation
     * @param startTime Operation start time
     * @param endTime Operation end time
     * @param responseTime Response time in milliseconds
     */
    public static void setPerformanceMetrics(String operationName, long startTime, long endTime, long responseTime) {
        String prefix = "perf." + sanitizeKey(operationName);
        setValue(prefix + ".start_time", startTime);
        setValue(prefix + ".end_time", endTime);
        setValue(prefix + ".response_time", responseTime);
    }
    
    /**
     * Get performance response time
     * 
     * @param operationName Name of the operation
     * @return Response time or null if not found
     */
    public static Long getPerformanceResponseTime(String operationName) {
        String key = "perf." + sanitizeKey(operationName) + ".response_time";
        Object value = getValue(key);
        return value instanceof Number ? ((Number) value).longValue() : null;
    }
    
    /**
     * Store validation results
     * 
     * @param validationName Name of the validation
     * @param expected Expected value
     * @param actual Actual value
     * @param passed Whether validation passed
     */
    public static void setValidationResult(String validationName, String expected, String actual, boolean passed) {
        String prefix = "validation." + sanitizeKey(validationName);
        setValue(prefix + ".expected", expected);
        setValue(prefix + ".actual", actual);
        setValue(prefix + ".passed", passed);
        setValue(prefix + ".timestamp", System.currentTimeMillis());
    }
    
    /**
     * Get validation result
     * 
     * @param validationName Name of the validation
     * @return Validation result or null if not found
     */
    public static Boolean getValidationResult(String validationName) {
        String key = "validation." + sanitizeKey(validationName) + ".passed";
        return getBooleanValue(key);
    }
    
    /**
     * Get a summary of the scenario context
     * 
     * @return Formatted summary string
     */
    public static String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Scenario Context Summary ===\n");
        summary.append("Total items: ").append(size()).append("\n");
        
        Map<String, Object> allValues = getAllValues();
        
        // Count by category
        long appValues = allValues.keySet().stream().filter(key -> key.startsWith("app.")).count();
        long stepValues = allValues.keySet().stream().filter(key -> key.startsWith("step.")).count();
        long perfValues = allValues.keySet().stream().filter(key -> key.startsWith("perf.")).count();
        long validationValues = allValues.keySet().stream().filter(key -> key.startsWith("validation.")).count();
        long extractedValues = allValues.keySet().stream().filter(key -> key.startsWith("extracted.")).count();
        
        summary.append("Application data: ").append(appValues).append("\n");
        summary.append("Step data: ").append(stepValues).append("\n");
        summary.append("Performance data: ").append(perfValues).append("\n");
        summary.append("Validation data: ").append(validationValues).append("\n");
        summary.append("Extracted data: ").append(extractedValues).append("\n");
        
        // Recent activity (last 5 items)
        summary.append("\nRecent activity:\n");
        allValues.entrySet().stream()
                .sorted((e1, e2) -> {
                    // Try to sort by timestamp if available
                    Object ts1 = allValues.get(e1.getKey().replace(".data", ".timestamp"));
                    Object ts2 = allValues.get(e2.getKey().replace(".data", ".timestamp"));
                    if (ts1 instanceof Number && ts2 instanceof Number) {
                        return Long.compare(((Number) ts2).longValue(), ((Number) ts1).longValue());
                    }
                    return e2.getKey().compareTo(e1.getKey());
                })
                .limit(5)
                .forEach(entry -> summary.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n"));
        
        return summary.toString();
    }
    
    // Private helper methods
    
    private static String sanitizeKey(String key) {
        return key.toLowerCase().replaceAll("[^a-z0-9_]", "_");
    }
}
