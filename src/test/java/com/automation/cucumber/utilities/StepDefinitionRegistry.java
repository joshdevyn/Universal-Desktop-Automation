package com.automation.cucumber.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Step Definition Registry - Dynamic step definition management and discovery
 * Provides runtime step discovery, validation, and documentation for the universal framework
 * 
 * @author Joshua Sims
 * @version 2.0
 * @since 1.0
 */
public class StepDefinitionRegistry {
    
    private static final Logger logger = LoggerFactory.getLogger(StepDefinitionRegistry.class);
    private static final Map<String, StepDefinitionInfo> registeredSteps = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> stepDefinitionClasses = new ConcurrentHashMap<>();
    
    /**
     * Information about a step definition
     */
    public static class StepDefinitionInfo {
        private final String pattern;
        private final String stepType;
        private final Method method;
        private final Class<?> declaringClass;
        private final String description;
        private final boolean isUniversal;
        
        public StepDefinitionInfo(String pattern, String stepType, Method method, 
                                Class<?> declaringClass, String description, boolean isUniversal) {
            this.pattern = pattern;
            this.stepType = stepType;
            this.method = method;
            this.declaringClass = declaringClass;
            this.description = description;
            this.isUniversal = isUniversal;
        }
        
        // Getters
        public String getPattern() { return pattern; }
        public String getStepType() { return stepType; }
        public Method getMethod() { return method; }
        public Class<?> getDeclaringClass() { return declaringClass; }
        public String getDescription() { return description; }
        public boolean isUniversal() { return isUniversal; }
    }
    
    /**
     * Register a step definition
     * 
     * @param pattern Step pattern (regex)
     * @param stepType Type of step (Given, When, Then)
     * @param method Method that implements the step
     * @param description Human-readable description
     * @param isUniversal Whether this step is application-agnostic
     */
    public static void registerStep(String pattern, String stepType, Method method, 
                                  String description, boolean isUniversal) {
        String key = generateStepKey(pattern, stepType);
        StepDefinitionInfo stepInfo = new StepDefinitionInfo(pattern, stepType, method, 
                                                            method.getDeclaringClass(), description, isUniversal);
        
        registeredSteps.put(key, stepInfo);
        stepDefinitionClasses.put(method.getDeclaringClass().getSimpleName(), method.getDeclaringClass());
        
        logger.debug("Registered step: {} [{}] - {}", pattern, stepType, 
                    isUniversal ? "UNIVERSAL" : "SPECIFIC");
    }
    
    /**
     * Auto-discover step definitions from classpath
     */
    public static void discoverStepDefinitions() {
        logger.info("Starting step definition discovery...");
        
        try {
            // Discover steps from our main packages
            discoverFromPackage("com.automation.cucumber.stepdefinitions");
            discoverFromPackage("com.automation.cucumber.patterns");
            
            logger.info("Step definition discovery completed. Total steps: {}", registeredSteps.size());
            
        } catch (Exception e) {
            logger.error("Failed to discover step definitions: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get all registered step definitions
     * 
     * @return Map of step definitions
     */
    public static Map<String, StepDefinitionInfo> getAllSteps() {
        return new HashMap<>(registeredSteps);
    }
    
    /**
     * Get universal step definitions only
     * 
     * @return List of universal step definitions
     */
    public static List<StepDefinitionInfo> getUniversalSteps() {
        return registeredSteps.values().stream()
                .filter(StepDefinitionInfo::isUniversal)
                .sorted(Comparator.comparing(StepDefinitionInfo::getStepType)
                         .thenComparing(StepDefinitionInfo::getPattern))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get steps by type (Given, When, Then)
     * 
     * @param stepType Type of step
     * @return List of step definitions of the specified type
     */
    public static List<StepDefinitionInfo> getStepsByType(String stepType) {
        return registeredSteps.values().stream()
                .filter(step -> step.getStepType().equalsIgnoreCase(stepType))
                .sorted(Comparator.comparing(StepDefinitionInfo::getPattern))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Search for steps matching a pattern
     * 
     * @param searchPattern Pattern to search for
     * @return List of matching step definitions
     */
    public static List<StepDefinitionInfo> searchSteps(String searchPattern) {
        String lowerSearchPattern = searchPattern.toLowerCase();
        
        return registeredSteps.values().stream()
                .filter(step -> 
                    step.getPattern().toLowerCase().contains(lowerSearchPattern) ||
                    step.getDescription().toLowerCase().contains(lowerSearchPattern))
                .sorted(Comparator.comparing(StepDefinitionInfo::getStepType)
                         .thenComparing(StepDefinitionInfo::getPattern))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Validate step definitions for conflicts
     * 
     * @return List of validation issues
     */
    public static List<String> validateStepDefinitions() {
        List<String> issues = new ArrayList<>();
        
        // Check for duplicate patterns
        Map<String, List<StepDefinitionInfo>> patternGroups = new HashMap<>();
        
        for (StepDefinitionInfo step : registeredSteps.values()) {
            String pattern = step.getPattern();
            patternGroups.computeIfAbsent(pattern, k -> new ArrayList<>()).add(step);
        }
        
        for (Map.Entry<String, List<StepDefinitionInfo>> entry : patternGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                issues.add("Duplicate step pattern found: " + entry.getKey() + 
                          " in classes: " + entry.getValue().stream()
                          .map(step -> step.getDeclaringClass().getSimpleName())
                          .collect(java.util.stream.Collectors.joining(", ")));
            }
        }
        
        // Check for missing step types
        Set<String> foundTypes = registeredSteps.values().stream()
                .map(StepDefinitionInfo::getStepType)
                .collect(java.util.stream.Collectors.toSet());
        
        List<String> expectedTypes = Arrays.asList("Given", "When", "Then");
        for (String expectedType : expectedTypes) {
            if (!foundTypes.contains(expectedType)) {
                issues.add("No step definitions found for type: " + expectedType);
            }
        }
        
        return issues;
    }
    
    /**
     * Generate step documentation
     * 
     * @return Formatted documentation string
     */
    public static String generateDocumentation() {
        StringBuilder doc = new StringBuilder();
        doc.append("# Universal Desktop Automation Framework - Step Definitions\n\n");
        
        // Universal steps section
        doc.append("## Universal Steps (Application-Agnostic)\n\n");
        List<StepDefinitionInfo> universalSteps = getUniversalSteps();
        
        String currentType = "";
        for (StepDefinitionInfo step : universalSteps) {
            if (!step.getStepType().equals(currentType)) {
                currentType = step.getStepType();
                doc.append("### ").append(currentType).append(" Steps\n\n");
            }
            
            doc.append("**").append(step.getPattern()).append("**\n");
            doc.append("- *Description*: ").append(step.getDescription()).append("\n");
            doc.append("- *Class*: ").append(step.getDeclaringClass().getSimpleName()).append("\n");
            doc.append("- *Universal*: ✅\n\n");
        }
        
        // Statistics
        doc.append("\n## Statistics\n\n");
        doc.append("- **Total Steps**: ").append(registeredSteps.size()).append("\n");
        doc.append("- **Universal Steps**: ").append(universalSteps.size()).append("\n");
        doc.append("- **Given Steps**: ").append(getStepsByType("Given").size()).append("\n");
        doc.append("- **When Steps**: ").append(getStepsByType("When").size()).append("\n");
        doc.append("- **Then Steps**: ").append(getStepsByType("Then").size()).append("\n");
        doc.append("- **Step Definition Classes**: ").append(stepDefinitionClasses.size()).append("\n");
        
        return doc.toString();
    }
    
    /**
     * Get step definition statistics
     * 
     * @return Map containing various statistics
     */
    public static Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("total_steps", registeredSteps.size());
        stats.put("universal_steps", (int) registeredSteps.values().stream().filter(StepDefinitionInfo::isUniversal).count());
        stats.put("given_steps", getStepsByType("Given").size());
        stats.put("when_steps", getStepsByType("When").size());
        stats.put("then_steps", getStepsByType("Then").size());
        stats.put("step_classes", stepDefinitionClasses.size());
        
        return stats;
    }
    
    /**
     * Check if a step pattern is registered
     * 
     * @param pattern Step pattern
     * @param stepType Step type
     * @return true if registered, false otherwise
     */
    public static boolean isStepRegistered(String pattern, String stepType) {
        String key = generateStepKey(pattern, stepType);
        return registeredSteps.containsKey(key);
    }
    
    /**
     * Get step definition info by pattern and type
     * 
     * @param pattern Step pattern
     * @param stepType Step type
     * @return StepDefinitionInfo if found, null otherwise
     */
    public static StepDefinitionInfo getStepInfo(String pattern, String stepType) {
        String key = generateStepKey(pattern, stepType);
        return registeredSteps.get(key);
    }
    
    /**
     * Clear all registered steps (for testing)
     */
    public static void clearRegistry() {
        registeredSteps.clear();
        stepDefinitionClasses.clear();
        logger.debug("Step definition registry cleared");
    }
    
    // Private helper methods
    
    private static String generateStepKey(String pattern, String stepType) {
        return stepType.toUpperCase() + ":" + pattern;
    }
    
    private static void discoverFromPackage(String packageName) {
        try {
            // This is a simplified discovery - in a real implementation,
            // you would use reflection utilities to scan the classpath
            logger.debug("Discovering step definitions from package: {}", packageName);
            
            // For now, we'll register some key patterns manually
            registerKnownStepPatterns();
            
        } catch (Exception e) {
            logger.error("Failed to discover from package {}: {}", packageName, e.getMessage());
        }
    }
    
    private static void registerKnownStepPatterns() {
        // Register some universal patterns as examples
        try {
            // Universal application patterns
            registerStep("^I have the application \"([^\"]*)\" ready for automation$", "Given", 
                        null, "Prepare any application for automation", true);
            
            registerStep("^I navigate through application menu path \"([^\"]*)\"$", "When", 
                        null, "Navigate through any application menu", true);
            
            registerStep("^I perform universal function key action \"([^\"]*)\"$", "When", 
                        null, "Execute function key actions universally", true);
            
            registerStep("^I should see application message \"([^\"]*)\"$", "Then", 
                        null, "Verify message in any application", true);
            
            // Universal integration patterns
            registerStep("^I extract data from application \"([^\"]*)\" using pattern \"([^\"]*)\"$", "When", 
                        null, "Extract data from any application", true);
            
            registerStep("^I transfer the extracted data to application \"([^\"]*)\"$", "When", 
                        null, "Transfer data to any application", true);
            
            // Universal performance patterns
            registerStep("^I start performance monitoring for application \"([^\"]*)\"$", "Given", 
                        null, "Start performance monitoring for any application", true);
            
            registerStep("^response time should be less than (\\d+) milliseconds$", "Then", 
                        null, "Validate response time universally", true);
            
            logger.info("Registered {} known step patterns", 8);
            
        } catch (Exception e) {
            logger.error("Failed to register known step patterns: {}", e.getMessage());
        }
    }
}
