package com.automation.cucumber.configuration;

import com.automation.config.ConfigManager;
import io.cucumber.java.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Cucumber Configuration - Framework-wide cucumber settings and initialization
 * Manages cucumber-specific configurations and test environment setup
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class CucumberConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(CucumberConfiguration.class);
    private static boolean isInitialized = false;
    
    /**
     * Global initialization before all tests
     * Sets up the test environment and validates prerequisites
     */
    @BeforeAll
    public static void globalInitialization() {
        if (!isInitialized) {
            logger.info("========================================");
            logger.info("INITIALIZING CUCUMBER TEST FRAMEWORK");
            logger.info("========================================");
            
            // Initialize framework components
            initializeFramework();
            
            // Validate test environment
            validateEnvironment();
            
            // Setup reporting directories
            setupReportingDirectories();
            
            // Log framework information
            logFrameworkInfo();
            
            isInitialized = true;
            
            logger.info("========================================");
            logger.info("CUCUMBER FRAMEWORK INITIALIZATION COMPLETE");
            logger.info("========================================");
        }
    }
    
    /**
     * Initialize core framework components
     */
    private static void initializeFramework() {        try {
            logger.info("Initializing framework components...");
            
            // Load configuration - this will trigger initialization
            ConfigManager.getInstance();
            
            // Initialize screenshot directories
            String screenshotPath = ConfigManager.getProperty("screenshot.path.base", "target/screenshots");
            new File(screenshotPath).mkdirs();
            
            // Initialize image directories
            String imagePath = ConfigManager.getProperty("image.path.base", "src/test/resources/images");
            File imageDir = new File(imagePath);
            if (!imageDir.exists()) {
                logger.warn("Image directory does not exist: {}", imagePath);
            }
            
            logger.info("Framework components initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize framework components: {}", e.getMessage());
            throw new RuntimeException("Framework initialization failed", e);
        }
    }
    
    /**
     * Validate test environment prerequisites
     */
    private static void validateEnvironment() {
        try {
            logger.info("Validating test environment...");
            
            // Check Java version
            String javaVersion = System.getProperty("java.version");
            logger.info("Java Version: {}", javaVersion);
            
            // Check operating system
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            logger.info("Operating System: {} {}", osName, osVersion);
            
            // Validate required directories
            validateDirectory("target/cucumber-reports");
            validateDirectory("target/screenshots");
            validateDirectory("src/test/resources/images");
            validateDirectory("src/test/resources/data");
            
            // Check if Tesseract is available (if OCR is enabled)
            if (ConfigManager.getBooleanProperty("ocr.enabled", true)) {
                validateTesseract();
            }
            
            logger.info("Environment validation completed successfully");
            
        } catch (Exception e) {
            logger.error("Environment validation failed: {}", e.getMessage());
            throw new RuntimeException("Environment validation failed", e);
        }
    }
    
    /**
     * Setup reporting directories
     */
    private static void setupReportingDirectories() {
        try {
            logger.info("Setting up reporting directories...");
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            
            // Create timestamped report directories
            String[] reportTypes = {"master", "smoke", "regression", "integration", "parallel", "mock-apps"};
            
            for (String reportType : reportTypes) {
                String reportDir = "target/cucumber-reports/" + reportType;
                new File(reportDir).mkdirs();
                
                String timestampedDir = reportDir + "/archive/" + timestamp;
                new File(timestampedDir).mkdirs();
            }
            
            // Create debug directories if enabled
            if (ConfigManager.getBooleanProperty("debug.enabled", false)) {
                new File("target/debug").mkdirs();
                new File("target/debug/" + timestamp).mkdirs();
            }
            
            logger.info("Reporting directories setup completed");
            
        } catch (Exception e) {
            logger.warn("Failed to setup some reporting directories: {}", e.getMessage());
        }
    }
    
    /**
     * Log framework information
     */
    private static void logFrameworkInfo() {
        logger.info("========================================");
        logger.info("FRAMEWORK INFORMATION");
        logger.info("========================================");
        logger.info("Framework Name: Universal Desktop Automation Framework");
        logger.info("Framework Version: 2.0");
        logger.info("Author: Automation Framework Team");
        logger.info("Initialization Time: {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("Working Directory: {}", System.getProperty("user.dir"));
        logger.info("User: {}", System.getProperty("user.name"));
        
        // Log key configurations
        logger.info("========================================");
        logger.info("KEY CONFIGURATIONS");
        logger.info("========================================");
        logger.info("Debug Mode: {}", ConfigManager.getBooleanProperty("debug.enabled", false));
        logger.info("Screenshot on Step: {}", ConfigManager.getBooleanProperty("debug.screenshot.on.step", false));
        logger.info("OCR Enabled: {}", ConfigManager.getBooleanProperty("ocr.enabled", true));
        logger.info("Image Similarity Threshold: {}", ConfigManager.getDoubleProperty("image.similarity.threshold", 0.8));
        logger.info("Default Timeout: {} seconds", ConfigManager.getIntProperty("window.focus.timeout", 30));
        logger.info("Parallel Execution: {}", ConfigManager.getBooleanProperty("parallel.execution.enabled", false));
        logger.info("Max Parallel Threads: {}", ConfigManager.getIntProperty("max.parallel.threads", 3));
        logger.info("========================================");
    }
    
    /**
     * Validate directory exists and create if needed
     */
    private static void validateDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                logger.info("Created directory: {}", directoryPath);
            } else {
                logger.warn("Failed to create directory: {}", directoryPath);
            }
        } else {
            logger.debug("Directory exists: {}", directoryPath);
        }
    }
    
    /**
     * Validate Tesseract OCR availability
     */
    private static void validateTesseract() {
        try {
            String tesseractPath = ConfigManager.getProperty("tesseract.path", "tesseract");
            ProcessBuilder pb = new ProcessBuilder(tesseractPath, "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("Tesseract OCR is available");
            } else {
                logger.warn("Tesseract OCR validation failed with exit code: {}", exitCode);
            }
        } catch (Exception e) {
            logger.warn("Tesseract OCR not available or not configured properly: {}", e.getMessage());
        }
    }
    
    /**
     * Get cucumber-specific configuration
     */
    public static String getCucumberProperty(String key, String defaultValue) {
        return ConfigManager.getProperty("cucumber." + key, defaultValue);
    }
    
    /**
     * Check if cucumber feature is enabled
     */
    public static boolean isCucumberFeatureEnabled(String feature) {
        return ConfigManager.getBooleanProperty("cucumber.features." + feature + ".enabled", true);
    }
    
    /**
     * Get cucumber timeout configuration
     */
    public static int getCucumberTimeout(String timeoutType) {
        return ConfigManager.getIntProperty("cucumber.timeouts." + timeoutType, 30);
    }
}
