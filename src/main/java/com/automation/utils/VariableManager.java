package com.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Comprehensive variable management for test automation
 * Supports variable storage, retrieval, interpolation, and transformations
 */
public class VariableManager {
    private static final Logger logger = LoggerFactory.getLogger(VariableManager.class);
    
    // Global variable storage (thread-safe)
    private static final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    
    // Session-specific variables (thread-local)
    private static final ThreadLocal<Map<String, Object>> sessionVariables = 
        ThreadLocal.withInitial(ConcurrentHashMap::new);
    
    // Built-in variable transformations
    private static final Map<String, Function<String, String>> transformations = new ConcurrentHashMap<>();
    
    static {
        initializeBuiltInTransformations();
    }
    
    /**
     * Initialize built-in variable transformations
     */
    private static void initializeBuiltInTransformations() {
        transformations.put("uppercase", String::toUpperCase);
        transformations.put("lowercase", String::toLowerCase);
        transformations.put("trim", String::trim);
        transformations.put("reverse", s -> new StringBuilder(s).reverse().toString());
        transformations.put("length", s -> String.valueOf(s.length()));
        transformations.put("timestamp", s -> String.valueOf(System.currentTimeMillis()));
        transformations.put("uuid", s -> java.util.UUID.randomUUID().toString());
        transformations.put("today", s -> java.time.LocalDate.now().toString());
        transformations.put("now", s -> java.time.LocalDateTime.now().toString());
    }
    
    /**
     * Store variable in global scope
     */
    public static void setGlobalVariable(String name, Object value) {
        globalVariables.put(name, value);
        logger.debug("Set global variable '{}' = '{}'", name, value);
    }
    
    /**
     * Store variable in session scope (thread-local)
     */
    public static void setSessionVariable(String name, Object value) {
        sessionVariables.get().put(name, value);
        logger.debug("Set session variable '{}' = '{}'", name, value);
    }
    
    /**
     * Get variable value (session scope takes precedence)
     */
    public static Object getVariable(String name) {
        // Check session variables first
        Object value = sessionVariables.get().get(name);
        if (value != null) {
            return value;
        }
        
        // Fall back to global variables
        value = globalVariables.get(name);
        if (value != null) {
            return value;
        }
        
        // Check environment variables
        String envValue = System.getenv(name);
        if (envValue != null) {
            return envValue;
        }
        
        // Check system properties
        return System.getProperty(name);
    }
    
    /**
     * Get variable as string with default value
     */
    public static String getVariableAsString(String name, String defaultValue) {
        Object value = getVariable(name);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * Get variable as string
     */
    public static String getVariableAsString(String name) {
        return getVariableAsString(name, "");
    }
    
    /**
     * Get session variable value (only from session scope)
     */
    public static String getSessionVariable(String name) {
        Object value = sessionVariables.get().get(name);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Get global variable value (only from global scope)
     */
    public static String getGlobalVariable(String name) {
        Object value = globalVariables.get(name);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Get variable as integer
     */
    public static int getVariableAsInt(String name, int defaultValue) {
        Object value = getVariable(name);
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                logger.warn("Variable '{}' is not a valid integer: {}", name, value);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get variable as boolean
     */
    public static boolean getVariableAsBoolean(String name, boolean defaultValue) {
        Object value = getVariable(name);
        if (value != null) {
            return Boolean.parseBoolean(value.toString());
        }
        return defaultValue;
    }
    
    /**
     * Check if variable exists
     */
    public static boolean hasVariable(String name) {
        return getVariable(name) != null;
    }
    
    /**
     * Remove variable from session scope
     */
    public static void removeSessionVariable(String name) {
        sessionVariables.get().remove(name);
        logger.debug("Removed session variable '{}'", name);
    }
    
    /**
     * Remove variable from global scope
     */
    public static void removeGlobalVariable(String name) {
        globalVariables.remove(name);
        logger.debug("Removed global variable '{}'", name);
    }
      /**
     * Clear a specific session variable
     */
    public static void clearSessionVariable(String name) {
        sessionVariables.get().remove(name);
        logger.debug("Cleared session variable: {}", name);
    }
    
    /**
     * Clear all session variables
     */
    public static void clearAllSessionVariables() {
        sessionVariables.get().clear();
        logger.debug("Cleared all session variables");
    }
    
    /**
     * Clear all global variables
     */
    public static void clearGlobalVariables() {
        globalVariables.clear();
        logger.debug("Cleared all global variables");
    }
      /**
     * Clear all variables
     */
    public static void clearAllVariables() {
        clearAllSessionVariables();
        clearGlobalVariables();
        logger.debug("Cleared all variables");
    }
    
    /**
     * Interpolate variables in text using ${variable_name} syntax
     * Supports transformations: ${variable_name|transformation}
     */
    public static String interpolate(String text) {
        if (text == null || !text.contains("${")) {
            return text;
        }
        
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(text);
        
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        
        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            
            String variableExpression = matcher.group(1);
            String[] parts = variableExpression.split("\\|", 2);
            String variableName = parts[0].trim();
            String transformation = parts.length > 1 ? parts[1].trim() : null;
            
            String value = getVariableAsString(variableName);
            
            // Apply transformation if specified
            if (transformation != null && transformations.containsKey(transformation)) {
                value = transformations.get(transformation).apply(value);
            }
            
            result.append(value);
            lastEnd = matcher.end();
        }
        
        result.append(text.substring(lastEnd));
        return result.toString();
    }
    
    /**
     * Add custom transformation function
     */
    public static void addTransformation(String name, Function<String, String> transformation) {
        transformations.put(name, transformation);
        logger.debug("Added custom transformation '{}'", name);
    }
    
    /**
     * Generate unique variable name
     */
    public static String generateUniqueVariableName(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
    }
    
    /**
     * Get all session variables (for debugging)
     */
    public static Map<String, Object> getAllSessionVariables() {
        return new ConcurrentHashMap<>(sessionVariables.get());
    }
    
    /**
     * Get all global variables (for debugging)
     */
    public static Map<String, Object> getAllGlobalVariables() {
        return new ConcurrentHashMap<>(globalVariables);
    }
    
    /**
     * Load variables from environment with prefix
     */
    public static void loadEnvironmentVariables(String prefix) {
        System.getenv().entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(prefix))
            .forEach(entry -> {
                String varName = entry.getKey().substring(prefix.length()).toLowerCase();
                setGlobalVariable(varName, entry.getValue());
            });
        
        logger.info("Loaded environment variables with prefix '{}'", prefix);
    }
    
    /**
     * Load variables from environment (overload without filter parameter)
     */
    public static void loadEnvironmentVariables() {
        loadEnvironmentVariables("");
    }
    
    /**
     * Load variables from system properties with prefix
     */
    public static void loadSystemProperties(String prefix) {
        System.getProperties().entrySet().stream()
            .filter(entry -> entry.getKey().toString().startsWith(prefix))
            .forEach(entry -> {
                String varName = entry.getKey().toString().substring(prefix.length()).toLowerCase();
                setGlobalVariable(varName, entry.getValue());
            });
        
        logger.info("Loaded system properties with prefix '{}'", prefix);
    }
    
    /**
     * Export session variables to global scope
     */
    public static void exportSessionToGlobal() {
        sessionVariables.get().forEach(VariableManager::setGlobalVariable);
        logger.debug("Exported session variables to global scope");
    }
    
    /**
     * Import global variables to session scope
     */
    public static void importGlobalToSession() {
        globalVariables.forEach((key, value) -> sessionVariables.get().put(key, value));
        logger.debug("Imported global variables to session scope");
    }
    
    /**
     * Increment numeric variable
     */
    public static void incrementVariable(String name) {
        incrementVariable(name, 1);
    }
    
    /**
     * Increment numeric variable by amount
     */
    public static void incrementVariable(String name, int amount) {
        int currentValue = getVariableAsInt(name, 0);
        setSessionVariable(name, currentValue + amount);
        logger.debug("Incremented variable '{}' by {} to {}", name, amount, currentValue + amount);
    }
    
    /**
     * Append to string variable
     */
    public static void appendToVariable(String name, String value) {
        String currentValue = getVariableAsString(name);
        setSessionVariable(name, currentValue + value);
        logger.debug("Appended '{}' to variable '{}'", value, name);
    }
    
    /**
     * Prepend to string variable
     */
    public static void prependToVariable(String name, String value) {
        String currentValue = getVariableAsString(name);
        setSessionVariable(name, value + currentValue);
        logger.debug("Prepended '{}' to variable '{}'", value, name);
    }
}
