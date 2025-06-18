package com.automation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized configuration manager for the automation framework
 * Handles loading and accessing configuration from YAML and properties files
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    
    // Configuration file paths
    private static final String APPLICATIONS_CONFIG_PATH = "src/main/resources/config/applications.yml";
    private static final String FRAMEWORK_CONFIG_PATH = "src/main/resources/config/automation.properties";
    private static final String CLASSPATH_APPLICATIONS_CONFIG = "/config/applications.yml";
    private static final String CLASSPATH_FRAMEWORK_CONFIG = "/config/automation.properties";
    
    // Cached configurations
    private static final Map<String, Object> applicationsConfig = new ConcurrentHashMap<>();
    private static final Properties frameworkProperties = new Properties();
    
    // Singleton instance
    private static ConfigManager instance;
    private static boolean initialized = false;
    
    // Current application context
    private static String currentApplicationName;
    
    // Jackson mapper for YAML operations
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    private ConfigManager() {
        // Private constructor for singleton
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
            initialize();
        }
        return instance;
    }
    
    /**
     * Initialize configuration manager by loading all configuration files
     */
    private static void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            loadApplicationsConfig();
            loadFrameworkProperties();
            createDefaultConfigsIfNeeded();
            initialized = true;
            logger.info("ConfigManager initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize ConfigManager", e);
            throw new RuntimeException("Configuration initialization failed", e);
        }
    }
    
    /**
     * Load applications configuration from YAML file
     */
    private static void loadApplicationsConfig() {
        Yaml yaml = new Yaml();
        InputStream inputStream = null;
        
        try {
            // Try to load from file system first
            if (Files.exists(Paths.get(APPLICATIONS_CONFIG_PATH))) {
                inputStream = new FileInputStream(APPLICATIONS_CONFIG_PATH);
                logger.debug("Loading applications config from: {}", APPLICATIONS_CONFIG_PATH);
            } else {
                // Fall back to classpath
                inputStream = ConfigManager.class.getResourceAsStream(CLASSPATH_APPLICATIONS_CONFIG);
                logger.debug("Loading applications config from classpath: {}", CLASSPATH_APPLICATIONS_CONFIG);
            }
            
            if (inputStream != null) {
                Map<String, Object> config = yaml.load(inputStream);
                if (config != null) {
                    applicationsConfig.putAll(config);
                    logger.info("Loaded applications configuration with {} entries", 
                        getApplicationNames().size());
                } else {
                    logger.warn("Applications configuration is empty");
                }
            } else {
                logger.warn("Could not find applications configuration file, will create default");
                createDefaultApplicationConfig();
            }
        } catch (Exception e) {
            logger.error("Failed to load applications configuration", e);
            createDefaultApplicationConfig();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Failed to close input stream", e);
                }
            }
        }
    }
    
    /**
     * Load framework properties from properties file
     */
    private static void loadFrameworkProperties() {
        InputStream inputStream = null;
        
        try {
            // Try to load from file system first
            if (Files.exists(Paths.get(FRAMEWORK_CONFIG_PATH))) {
                inputStream = new FileInputStream(FRAMEWORK_CONFIG_PATH);
                logger.debug("Loading framework properties from: {}", FRAMEWORK_CONFIG_PATH);
            } else {
                // Fall back to classpath
                inputStream = ConfigManager.class.getResourceAsStream(CLASSPATH_FRAMEWORK_CONFIG);
                logger.debug("Loading framework properties from classpath: {}", CLASSPATH_FRAMEWORK_CONFIG);
            }
            
            if (inputStream != null) {
                frameworkProperties.load(inputStream);
                logger.info("Loaded framework properties with {} entries", frameworkProperties.size());
            } else {
                logger.warn("Could not find framework properties file, will create default");
                createDefaultGlobalProperties();
            }
        } catch (Exception e) {
            logger.error("Failed to load framework properties", e);
            createDefaultGlobalProperties();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Failed to close input stream", e);
                }
            }
        }
    }
    
    /**
     * Create default configuration files if they don't exist
     */
    private static void createDefaultConfigsIfNeeded() {
        if (applicationsConfig.isEmpty()) {
            createDefaultApplicationConfig();
        }
        if (frameworkProperties.isEmpty()) {
            createDefaultGlobalProperties();
        }
    }
    
    /**
     * Create default global properties file
     */
    private static void createDefaultGlobalProperties() {
        frameworkProperties.clear();
        
        // Default settings
        frameworkProperties.setProperty("screenshot.directory", "src/main/resources/images/screenshots");
        frameworkProperties.setProperty("image.path.base", "src/test/resources/images");
        frameworkProperties.setProperty("report.directory", "target/reports");
        frameworkProperties.setProperty("tesseract.datapath", "src/main/resources/tessdata");
        frameworkProperties.setProperty("tesseract.language", "eng");
        frameworkProperties.setProperty("default.timeout", "30");
        frameworkProperties.setProperty("image.similarity.threshold", "0.8");
        frameworkProperties.setProperty("ocr.confidence.threshold", "70");
        frameworkProperties.setProperty("ocr.preprocessing.enabled", "true");
        frameworkProperties.setProperty("wait.default.polling", "500");
        frameworkProperties.setProperty("automation.delay", "100");
        frameworkProperties.setProperty("debug.enabled", "false");
        frameworkProperties.setProperty("debug.screenshot.on.step", "false");
        frameworkProperties.setProperty("debug.highlight.matches", "true");
        
        // Save default properties
        saveGlobalProperties();
    }
    
    /**
     * Create default application configuration file
     */
    private static void createDefaultApplicationConfig() {
        Map<String, Object> defaultConfig = createDefaultAppConfigData();
        
        try {
            // Create config directory if it doesn't exist
            File configDir = new File("src/main/resources/config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            yamlMapper.writeValue(new File(APPLICATIONS_CONFIG_PATH), defaultConfig);
            applicationsConfig.putAll(defaultConfig);
            
            logger.info("Default application configuration created: {}", APPLICATIONS_CONFIG_PATH);
            
        } catch (IOException e) {
            logger.error("Failed to create default application configuration", e);
        }
    }
    
    /**
     * Create default application configuration data
     */
    private static Map<String, Object> createDefaultAppConfigData() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> applications = new HashMap<>();
        
        // Calculator Example
        Map<String, Object> calculator = new HashMap<>();
        calculator.put("window_title", "Calculator");
        calculator.put("executable_path", "calc.exe");
        calculator.put("process_name", "Calculator.exe");
        calculator.put("launch_timeout", 30);
        
        Map<String, Object> calcImages = new HashMap<>();
        calcImages.put("button_1", "calculator_button_1.png");
        calcImages.put("button_2", "calculator_button_2.png");
        calcImages.put("button_plus", "calculator_button_plus.png");
        calcImages.put("button_equals", "calculator_button_equals.png");
        calculator.put("images", calcImages);
        
        Map<String, Object> calcRegions = new HashMap<>();
        Map<String, Object> displayArea = new HashMap<>();
        displayArea.put("x", 50);
        displayArea.put("y", 50);
        displayArea.put("width", 300);
        displayArea.put("height", 100);
        calcRegions.put("display_area", displayArea);
        calculator.put("regions", calcRegions);
        
        applications.put("calculator", calculator);
        
        // Notepad Example
        Map<String, Object> notepad = new HashMap<>();
        notepad.put("window_title", "Untitled - Notepad");
        notepad.put("executable_path", "notepad.exe");
        notepad.put("process_name", "notepad.exe");
        notepad.put("launch_timeout", 20);
        
        Map<String, Object> notepadRegions = new HashMap<>();
        Map<String, Object> textArea = new HashMap<>();
        textArea.put("x", 10);
        textArea.put("y", 30);
        textArea.put("width", 500);
        textArea.put("height", 400);
        notepadRegions.put("text_area", textArea);
        notepad.put("regions", notepadRegions);
        
        applications.put("notepad", notepad);
        
        // Mock applications for testing
        createMockApplicationConfigs(applications);
        
        config.put("applications", applications);
        return config;
    }
    
    /**
     * Create mock application configurations for testing
     */
    private static void createMockApplicationConfigs(Map<String, Object> applications) {
        // Oracle Forms Mock
        Map<String, Object> oracleForms = new HashMap<>();
        oracleForms.put("window_title", "Oracle Forms - Mock");
        oracleForms.put("executable_path", "mock_oracle_forms.exe");
        oracleForms.put("process_name", "oracle_forms_mock.exe");
        oracleForms.put("launch_timeout", 45);
        applications.put("oracle_forms_mock", oracleForms);
        
        // SAP GUI Mock
        Map<String, Object> sapGui = new HashMap<>();
        sapGui.put("window_title", "SAP GUI - Mock");
        sapGui.put("executable_path", "mock_sap_gui.exe");
        sapGui.put("process_name", "sap_gui_mock.exe");
        sapGui.put("launch_timeout", 60);
        applications.put("sap_gui_mock", sapGui);
        
        // AS400 Terminal Mock
        Map<String, Object> as400Terminal = new HashMap<>();
        as400Terminal.put("window_title", "AS400 Terminal - Mock");
        as400Terminal.put("executable_path", "mock_as400_terminal.exe");
        as400Terminal.put("process_name", "as400_terminal_mock.exe");
        as400Terminal.put("launch_timeout", 30);
        applications.put("as400_terminal_mock", as400Terminal);
        
        // Excel Mock
        Map<String, Object> excel = new HashMap<>();
        excel.put("window_title", "Microsoft Excel - Mock");
        excel.put("executable_path", "mock_excel.exe");
        excel.put("process_name", "excel_mock.exe");
        excel.put("launch_timeout", 30);
        applications.put("excel_mock", excel);
        
        // Tandem Terminal Mock
        Map<String, Object> tandemTerminal = new HashMap<>();
        tandemTerminal.put("window_title", "Tandem Terminal");
        tandemTerminal.put("executable_path", "mock_tandem_terminal.exe");
        tandemTerminal.put("process_name", "tandem_terminal_mock.exe");
        tandemTerminal.put("launch_timeout", 30);
        tandemTerminal.put("logout_command", "LOGOFF");
        
        // Key mappings for terminal
        Map<String, String> terminalKeyMappings = new HashMap<>();
        terminalKeyMappings.put("TAB", "9");
        terminalKeyMappings.put("ENTER", "13");
        terminalKeyMappings.put("ESCAPE", "27");
        terminalKeyMappings.put("F1", "112");
        terminalKeyMappings.put("F3", "114");
        terminalKeyMappings.put("F12", "123");
        terminalKeyMappings.put("CLEAR_LINE", "27"); // ESC for clear line
        tandemTerminal.put("key_mappings", terminalKeyMappings);
        
        // Wait times for terminal operations
        Map<String, Integer> terminalWaitTimes = new HashMap<>();
        terminalWaitTimes.put("login_prompt_wait", 3);
        terminalWaitTimes.put("login_process_wait", 5);
        terminalWaitTimes.put("command_execution_wait", 2);
        terminalWaitTimes.put("function_key_wait", 1);
        terminalWaitTimes.put("logout_wait", 3);
        tandemTerminal.put("wait_times", terminalWaitTimes);
        
        // Credentials for terminal login
        Map<String, String> terminalCredentials = new HashMap<>();
        terminalCredentials.put("username", "testuser");
        terminalCredentials.put("password", "testpass");
        tandemTerminal.put("credentials", terminalCredentials);
        
        applications.put("tandem_terminal", tandemTerminal);
    }
    
    /**
     * Save global properties to file
     */
    private static void saveGlobalProperties() {
        try {
            // Create config directory if it doesn't exist
            File configDir = new File("src/main/resources/config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            try (OutputStream output = new FileOutputStream(FRAMEWORK_CONFIG_PATH)) {
                frameworkProperties.store(output, "Universal Desktop Automation Framework Configuration");
                logger.info("Global properties saved to: {}", FRAMEWORK_CONFIG_PATH);
            }
            
        } catch (IOException e) {
            logger.error("Failed to save global properties", e);
        }
    }
    
    /**
     * Get application configuration by name
     * @param applicationName Name of the application
     * @return Configuration map for the application
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getApplicationConfig(String applicationName) {
        ensureInitialized();
        
        Map<String, Object> applications = (Map<String, Object>) applicationsConfig.get("applications");
        if (applications != null && applications.containsKey(applicationName)) {
            return (Map<String, Object>) applications.get(applicationName);
        }
        
        logger.warn("No configuration found for application: {}", applicationName);
        return new HashMap<>();
    }
    
    /**
     * Get all available application names
     * @return Set of application names
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getApplicationNames() {
        ensureInitialized();
        
        Map<String, Object> applications = (Map<String, Object>) applicationsConfig.get("applications");
        if (applications != null) {
            return applications.keySet();
        }
        
        return new HashSet<>();
    }
    
    /**
     * Get string property from framework configuration
     * @param propertyName Name of the property
     * @return Property value or null if not found
     */
    public static String getProperty(String propertyName) {
        ensureInitialized();
        return frameworkProperties.getProperty(propertyName);
    }
    
    /**
     * Get string property with default value
     * @param propertyName Name of the property
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public static String getProperty(String propertyName, String defaultValue) {
        ensureInitialized();
        return frameworkProperties.getProperty(propertyName, defaultValue);
    }
    
    /**
     * Get integer property from framework configuration
     * @param propertyName Name of the property
     * @return Property value as integer
     */
    public static int getIntProperty(String propertyName) {
        String value = getProperty(propertyName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer property {}: {}", propertyName, value);
            }
        }
        return 0;
    }
    
    /**
     * Get integer property with default value
     * @param propertyName Name of the property
     * @param defaultValue Default value if property not found or invalid
     * @return Property value as integer or default value
     */
    public static int getIntProperty(String propertyName, int defaultValue) {
        String value = getProperty(propertyName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer property {}: {}, using default: {}", 
                    propertyName, value, defaultValue);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get double property from framework configuration
     * @param propertyName Name of the property
     * @return Property value as double
     */
    public static double getDoubleProperty(String propertyName) {
        String value = getProperty(propertyName);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid double property {}: {}", propertyName, value);
            }
        }
        return 0.0;
    }
    
    /**
     * Get double property with default value
     * @param propertyName Name of the property
     * @param defaultValue Default value if property not found or invalid
     * @return Property value as double or default value
     */
    public static double getDoubleProperty(String propertyName, double defaultValue) {
        String value = getProperty(propertyName);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid double property {}: {}, using default: {}", 
                    propertyName, value, defaultValue);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get boolean property from framework configuration
     * @param propertyName Name of the property
     * @return Property value as boolean
     */
    public static boolean getBooleanProperty(String propertyName) {
        String value = getProperty(propertyName);
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Get boolean property with default value
     * @param propertyName Name of the property
     * @param defaultValue Default value if property not found
     * @return Property value as boolean or default value
     */
    public static boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        String value = getProperty(propertyName);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
    
    /**
     * Helper method to safely convert Object to int with default value
     * @param value Object to convert
     * @param defaultValue Default value if conversion fails
     * @return Integer value or default
     */
    private static int getIntValue(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        
        if (value instanceof Integer) {
            return (Integer) value;
        }
        
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse integer from string: {}", value);
                return defaultValue;
            }
        }
        
        logger.warn("Unexpected value type for integer conversion: {}", value.getClass());
        return defaultValue;
    }
    
    /**
     * Get OCR region configuration for an application
     * @param applicationName Name of the application
     * @param regionName Name of the region
     * @return Rectangle representing the region
     */
    @SuppressWarnings("unchecked")
    public static Rectangle getRegion(String applicationName, String regionName) {
        Map<String, Object> appConfig = getApplicationConfig(applicationName);
        Map<String, Object> regions = (Map<String, Object>) appConfig.get("ocr_regions");
        
        if (regions != null && regions.containsKey(regionName)) {
            Map<String, Object> region = (Map<String, Object>) regions.get(regionName);
            return new Rectangle(
                (Integer) region.get("x"),
                (Integer) region.get("y"),
                (Integer) region.get("width"),
                (Integer) region.get("height")
            );
        }
        
        logger.warn("No region '{}' found for application '{}'", regionName, applicationName);
        return new Rectangle(0, 0, 100, 100); // Default small region
    }
    
    /**
     * Get region configuration from any application
     * @param appConfig Application configuration map
     * @param regionName Name of the region
     * @return Rectangle representing the region bounds
     */
    @SuppressWarnings("unchecked")
    public static Rectangle getRegion(Map<String, Object> appConfig, String regionName) {
        Map<String, Object> regions = (Map<String, Object>) appConfig.get("regions");
        
        if (regions != null && regions.containsKey(regionName)) {
            Map<String, Object> regionConfig = (Map<String, Object>) regions.get(regionName);
            
            int x = getIntValue(regionConfig.get("x"), 0);
            int y = getIntValue(regionConfig.get("y"), 0);
            int width = getIntValue(regionConfig.get("width"), 100);
            int height = getIntValue(regionConfig.get("height"), 100);
            
            return new Rectangle(x, y, width, height);
        }
        
        logger.warn("Region '{}' not found in application configuration", regionName);
        return new Rectangle(0, 0, 100, 100); // Default region
    }
    
    /**
     * Get current application region configuration
     * @param regionName Name of the region
     * @return Rectangle representing the region bounds
     */
    @SuppressWarnings("unchecked")
    public static Rectangle getCurrentAppRegion(String regionName) {
        if (currentApplicationName == null) {
            throw new IllegalStateException("No current application set. Call setCurrentApplication() first.");
        }
        
        Map<String, Object> appConfig = getApplicationConfig(currentApplicationName);
        Map<String, Object> regions = (Map<String, Object>) appConfig.get("regions");
        
        if (regions == null || !regions.containsKey(regionName)) {
            throw new IllegalArgumentException("Region '" + regionName + "' not found for application '" + currentApplicationName + "'");
        }
        
        Map<String, Object> regionConfig = (Map<String, Object>) regions.get(regionName);
        int x = getIntValue(regionConfig.get("x"), 0);
        int y = getIntValue(regionConfig.get("y"), 0);
        int width = getIntValue(regionConfig.get("width"), 100);
        int height = getIntValue(regionConfig.get("height"), 100);
        
        return new Rectangle(x, y, width, height);
    }
    
    /**
     * Get wait time configuration for an application
     * @param applicationName Name of the application
     * @param waitType Type of wait (e.g., "screen_load", "command_response")
     * @return Wait time in milliseconds
     */
    @SuppressWarnings("unchecked")
    public static int getWaitTime(String applicationName, String waitType) {
        Map<String, Object> appConfig = getApplicationConfig(applicationName);
        Map<String, Object> waitTimes = (Map<String, Object>) appConfig.get("wait_times");
        
        if (waitTimes != null && waitTimes.containsKey(waitType)) {
            return (Integer) waitTimes.get(waitType);
        }
        
        // Return default wait time from framework properties
        return getIntProperty("default.timeout", 30) * 1000;
    }
    
    /**
     * Get image path for templates directory
     * @param imageName Name of the image file
     * @return Full path to the image
     */
    public static String getImagePath(String imageName) {
        // First try to get from current application config
        if (currentApplicationName != null) {
            Map<String, Object> appConfig = getApplicationConfig(currentApplicationName);
            @SuppressWarnings("unchecked")
            Map<String, Object> images = (Map<String, Object>) appConfig.get("images");
            
            if (images != null && images.containsKey(imageName)) {
                String imagePath = (String) images.get(imageName);
                
                // If it's already a full path, return it
                if (imagePath.contains("/") || imagePath.contains("\\")) {
                    return imagePath;
                }
                
                // Otherwise, build path with application folder
                String baseImagePath = getProperty("image.path.base", "src/test/resources/images");
                return baseImagePath + "/" + currentApplicationName + "/" + imagePath;
            }
        }
        
        // Fall back to framework default image path
        String baseImagePath = getProperty("image.path.base", "src/test/resources/images");
        return baseImagePath + "/" + imageName;
    }
    
    /**
     * Get screenshot directory path
     * @return Path to screenshot directory
     */
    public static String getScreenshotDirectory() {
        return getProperty("screenshot.directory", "src/main/resources/images/screenshots");
    }
    
    /**
     * Get report directory path
     * @return Path to report directory
     */
    public static String getReportDirectory() {
        return getProperty("report.directory", "src/test/resources/reports");
    }
    
    /**
     * Get key mapping for an application
     * @param applicationName Name of the application
     * @param keyName Name of the key mapping
     * @return Key mapping string
     */
    @SuppressWarnings("unchecked")
    public static String getKeyMapping(String applicationName, String keyName) {
        Map<String, Object> appConfig = getApplicationConfig(applicationName);
        Map<String, Object> keyMappings = (Map<String, Object>) appConfig.get("key_mappings");
        
        if (keyMappings != null && keyMappings.containsKey(keyName)) {
            return (String) keyMappings.get(keyName);
        }
        
        return keyName.toUpperCase(); // Default to uppercase key name
    }
    
    /**
     * Get OCR settings for an application
     * @param applicationName Name of the application
     * @return Map of OCR settings
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getOCRSettings(String applicationName) {
        Map<String, Object> appConfig = getApplicationConfig(applicationName);
        Map<String, Object> ocrSettings = (Map<String, Object>) appConfig.get("ocr_settings");
        
        if (ocrSettings != null) {
            return new HashMap<>(ocrSettings);
        }
        
        // Return default OCR settings from framework properties
        Map<String, Object> defaultSettings = new HashMap<>();
        defaultSettings.put("confidence_threshold", getIntProperty("ocr.confidence.threshold", 70));
        defaultSettings.put("preprocessing", getBooleanProperty("ocr.preprocessing.enabled", true));
        defaultSettings.put("language", getProperty("tesseract.language", "eng"));
        
        return defaultSettings;
    }
    
    /**
     * Reload all configurations
     */
    public static synchronized void reload() {
        logger.info("Reloading configurations...");
        applicationsConfig.clear();
        frameworkProperties.clear();
        initialized = false;
        initialize();
    }
    
    /**
     * Set a property value (for testing purposes)
     * @param propertyName Name of the property
     * @param value Value to set
     */
    public static void setProperty(String propertyName, String value) {
        ensureInitialized();
        frameworkProperties.setProperty(propertyName, value);
    }
    
    /**
     * Get all framework properties
     * @return Copy of all framework properties
     */
    public static Properties getAllProperties() {
        ensureInitialized();
        return new Properties(frameworkProperties);
    }
    
    /**
     * Get all applications configuration
     * @return Copy of all applications configuration
     */
    public static Map<String, Object> getAllApplicationsConfig() {
        ensureInitialized();
        return new HashMap<>(applicationsConfig);
    }
    
    /**
     * Ensure configuration is initialized
     */
    private static void ensureInitialized() {
        if (!initialized) {
            getInstance();
        }
    }
    
    /**
     * Validate configuration integrity
     * @return true if configuration is valid
     */
    public static boolean validateConfiguration() {
        try {
            ensureInitialized();
            
            // Check if applications config is loaded
            if (applicationsConfig.isEmpty()) {
                logger.error("Applications configuration is empty");
                return false;
            }
            
            // Check if framework properties are loaded
            if (frameworkProperties.isEmpty()) {
                logger.error("Framework properties are empty");
                return false;
            }
            
            // Validate each application configuration
            for (String appName : getApplicationNames()) {
                Map<String, Object> appConfig = getApplicationConfig(appName);
                if (!validateApplicationConfig(appName, appConfig)) {
                    return false;
                }
            }
            
            logger.info("Configuration validation passed");
            return true;
            
        } catch (Exception e) {
            logger.error("Configuration validation failed", e);
            return false;
        }
    }
    
    /**
     * Validate individual application configuration
     */
    private static boolean validateApplicationConfig(String appName, Map<String, Object> config) {
        // Check required fields
        String[] requiredFields = {"window_title", "launch_timeout"};
        
        for (String field : requiredFields) {
            if (!config.containsKey(field) || config.get(field) == null) {
                logger.error("Application '{}' missing required field: {}", appName, field);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get current application configuration (for step definitions)
     */
    public static void setCurrentApplication(String applicationName) {
        currentApplicationName = applicationName;
        logger.info("Current application set to: {}", applicationName);
    }
    
    public static String getCurrentApplication() {
        return currentApplicationName;
    }
    
    /**
     * Get nested property using dot notation (e.g., "ocr_regions.status_line.x")
     */
    @SuppressWarnings("unchecked")
    public static Object getNestedProperty(Map<String, Object> config, String propertyPath) {
        String[] parts = propertyPath.split("\\.");
        Object current = config;
        
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
        }
        
        return current;
    }
    
    /**
     * Get OCR regions for application (legacy method for backward compatibility)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Integer>> getOCRRegions(String applicationName) {
        Map<String, Object> appConfig = getApplicationConfig(applicationName);
        Map<String, Object> regions = (Map<String, Object>) appConfig.get("ocr_regions");
        
        if (regions != null) {
            Map<String, Map<String, Integer>> result = new HashMap<>();
            for (Map.Entry<String, Object> entry : regions.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    Map<String, Object> regionData = (Map<String, Object>) entry.getValue();
                    Map<String, Integer> regionInts = new HashMap<>();
                    
                    for (Map.Entry<String, Object> regionEntry : regionData.entrySet()) {
                        if (regionEntry.getValue() instanceof Number) {
                            regionInts.put(regionEntry.getKey(), ((Number) regionEntry.getValue()).intValue());
                        }
                    }
                    result.put(entry.getKey(), regionInts);
                }
            }
            return result;
        }
        return new HashMap<>();
    }
    
    /**
     * Get key mappings for application (legacy method for backward compatibility)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getKeyMappings(String applicationName) {
        Object mappings = getNestedProperty(getApplicationConfig(applicationName), "key_mappings");
        if (mappings instanceof Map) {
            return (Map<String, String>) mappings;
        }
        return new HashMap<>();
    }
    
    /**
     * Get wait times for application (legacy method for backward compatibility)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Integer> getWaitTimes(String applicationName) {
        Object waitTimes = getNestedProperty(getApplicationConfig(applicationName), "wait_times");
        if (waitTimes instanceof Map) {
            Map<String, Integer> result = new HashMap<>();
            Map<String, Object> waitTimesMap = (Map<String, Object>) waitTimes;
            for (Map.Entry<String, Object> entry : waitTimesMap.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    result.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                }
            }
            return result;
        }
        return new HashMap<>();
    }
    
    /**
     * Check if application configuration exists
     */
    public static boolean hasApplicationConfig(String applicationName) {
        return getApplicationNames().contains(applicationName);
    }
}
