package com.automation.cucumber.stepdefinitions;

import io.cucumber.java.en.*;
import com.automation.utils.VariableManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * Step definitions for variable management and data manipulation
 * Supports setting, getting, transforming, and managing test variables
 */
public class VariableStepDefinitions extends CommonStepDefinitionsBase {
    private static final Logger logger = LoggerFactory.getLogger(VariableStepDefinitions.class);    
    @When("I set variable {string} to {string}")
    public void i_set_variable_to(String variableName, String value) {
        String interpolatedValue = VariableManager.interpolate(value);
        VariableManager.setSessionVariable(variableName, interpolatedValue);
        
        addVerification("Set Variable", true, 
            String.format("Variable '%s' set to '%s'", variableName, interpolatedValue));    }

    @When("I set variable {string} to current timestamp")
    public void i_set_variable_to_current_timestamp(String variableName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        VariableManager.setSessionVariable(variableName, timestamp);
        
        addVerification("Set Variable to Current Timestamp", true, 
            String.format("Variable '%s' set to current timestamp '%s'", variableName, timestamp));
        
        logger.info("Set variable '{}' to current timestamp '{}'", variableName, timestamp);
    }

    @When("I set session variable {string} to {string}")
    public void i_set_session_variable_to(String variableName, String value) {
        String interpolatedValue = VariableManager.interpolate(value);
        VariableManager.setSessionVariable(variableName, interpolatedValue);
        
        addVerification("Set Session Variable", true, 
            String.format("Session variable '%s' set to '%s'", variableName, interpolatedValue));
        
        logger.info("Set session variable '{}' to '{}'", variableName, interpolatedValue);
    }

    @When("I set global variable {string} to {string}")
    public void i_set_global_variable_to(String variableName, String value) {
        String interpolatedValue = VariableManager.interpolate(value);
        VariableManager.setGlobalVariable(variableName, interpolatedValue);
        
        addVerification("Set Global Variable", true, 
            String.format("Global variable '%s' set to '%s'", variableName, interpolatedValue));
    }

    @When("I copy variable {string} to {string}")
    public void i_copy_variable_to(String sourceVariable, String targetVariable) {
        String sourceValue = VariableManager.getSessionVariable(sourceVariable);
        if (sourceValue == null) {
            sourceValue = VariableManager.getGlobalVariable(sourceVariable);
        }
        
        if (sourceValue != null) {
            VariableManager.setSessionVariable(targetVariable, sourceValue);
            addVerification("Copy Variable", true, 
                String.format("Copied variable '%s' to '%s' with value '%s'", 
                    sourceVariable, targetVariable, sourceValue));
        } else {
            addVerification("Copy Variable", false, 
                String.format("Source variable '%s' not found", sourceVariable));
            throw new RuntimeException("Source variable not found: " + sourceVariable);
        }
    }

    @When("I append {string} to variable {string}")
    public void i_append_to_variable(String valueToAppend, String variableName) {
        String interpolatedValue = VariableManager.interpolate(valueToAppend);
        VariableManager.appendToVariable(variableName, interpolatedValue);
        
        String newValue = VariableManager.getSessionVariable(variableName);
        addVerification("Append to Variable", true, 
            String.format("Appended '%s' to variable '%s'. New value: '%s'", 
                interpolatedValue, variableName, newValue));
    }

    @When("I increment variable {string}")
    public void i_increment_variable(String variableName) {
        try {
            VariableManager.incrementVariable(variableName);
            String newValue = VariableManager.getSessionVariable(variableName);
            
            addVerification("Increment Variable", true, 
                String.format("Incremented variable '%s' to '%s'", variableName, newValue));
        } catch (Exception e) {
            addVerification("Increment Variable", false, 
                "Failed to increment variable: " + e.getMessage());
            throw new RuntimeException("Failed to increment variable: " + variableName, e);
        }
    }

    @When("I increment variable {string} by {string}")
    public void i_increment_variable_by(String variableName, String incrementValue) {
        String interpolatedIncrement = VariableManager.interpolate(incrementValue);
        
        try {
            VariableManager.incrementVariable(variableName, Integer.parseInt(interpolatedIncrement));
            String newValue = VariableManager.getSessionVariable(variableName);
            
            addVerification("Increment Variable By Value", true, 
                String.format("Incremented variable '%s' by %s to '%s'", 
                    variableName, interpolatedIncrement, newValue));
        } catch (NumberFormatException e) {
            addVerification("Increment Variable By Value", false, 
                "Invalid increment value: " + interpolatedIncrement);
            throw new RuntimeException("Invalid increment value: " + interpolatedIncrement);
        } catch (Exception e) {
            addVerification("Increment Variable By Value", false, 
                "Failed to increment variable: " + e.getMessage());
            throw new RuntimeException("Failed to increment variable: " + variableName, e);
        }
    }

    @When("I generate random number and store in variable {string}")
    public void i_generate_random_number_and_store(String variableName) {
        Random random = new Random();
        int randomNumber = random.nextInt(10000); // 0-9999
        
        VariableManager.setSessionVariable(variableName, String.valueOf(randomNumber));
        addVerification("Generate Random Number", true, 
            String.format("Generated random number %d and stored in variable '%s'", 
                randomNumber, variableName));
    }

    @When("I generate random number between {int} and {int} and store in variable {string}")
    public void i_generate_random_number_between_and_store(int min, int max, String variableName) {
        Random random = new Random();
        int randomNumber = random.nextInt(max - min + 1) + min;
        
        VariableManager.setSessionVariable(variableName, String.valueOf(randomNumber));
        addVerification("Generate Random Number in Range", true, 
            String.format("Generated random number %d (between %d and %d) and stored in variable '%s'", 
                randomNumber, min, max, variableName));
    }

    @When("I generate UUID and store in variable {string}")
    public void i_generate_uuid_and_store(String variableName) {
        String uuid = UUID.randomUUID().toString();
        
        VariableManager.setSessionVariable(variableName, uuid);
        addVerification("Generate UUID", true, 
            String.format("Generated UUID '%s' and stored in variable '%s'", uuid, variableName));
    }

    @When("I generate timestamp and store in variable {string}")
    public void i_generate_timestamp_and_store(String variableName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        VariableManager.setSessionVariable(variableName, timestamp);
        addVerification("Generate Timestamp", true, 
            String.format("Generated timestamp '%s' and stored in variable '%s'", timestamp, variableName));
    }

    @When("I generate date string with format {string} and store in variable {string}")
    public void i_generate_date_string_and_store(String dateFormat, String variableName) {
        try {
            String interpolatedFormat = VariableManager.interpolate(dateFormat);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(interpolatedFormat);
            String dateString = LocalDateTime.now().format(formatter);
            
            VariableManager.setSessionVariable(variableName, dateString);
            addVerification("Generate Date String", true, 
                String.format("Generated date string '%s' with format '%s' and stored in variable '%s'", 
                    dateString, interpolatedFormat, variableName));
        } catch (Exception e) {
            addVerification("Generate Date String", false, 
                "Failed to generate date string: " + e.getMessage());
            throw new RuntimeException("Failed to generate date string with format: " + dateFormat, e);
        }
    }

    @When("I transform variable {string} to uppercase")
    public void i_transform_variable_to_uppercase(String variableName) {
        String currentValue = VariableManager.getSessionVariable(variableName);
        if (currentValue != null) {
            String uppercaseValue = currentValue.toUpperCase();
            VariableManager.setSessionVariable(variableName, uppercaseValue);
            
            addVerification("Transform to Uppercase", true, 
                String.format("Transformed variable '%s' from '%s' to '%s'", 
                    variableName, currentValue, uppercaseValue));
        } else {
            addVerification("Transform to Uppercase", false, 
                "Variable not found: " + variableName);
            throw new RuntimeException("Variable not found: " + variableName);
        }
    }

    @When("I transform variable {string} to lowercase")
    public void i_transform_variable_to_lowercase(String variableName) {
        String currentValue = VariableManager.getSessionVariable(variableName);
        if (currentValue != null) {
            String lowercaseValue = currentValue.toLowerCase();
            VariableManager.setSessionVariable(variableName, lowercaseValue);
            
            addVerification("Transform to Lowercase", true, 
                String.format("Transformed variable '%s' from '%s' to '%s'", 
                    variableName, currentValue, lowercaseValue));
        } else {
            addVerification("Transform to Lowercase", false, 
                "Variable not found: " + variableName);
            throw new RuntimeException("Variable not found: " + variableName);
        }
    }

    @When("I extract substring from variable {string} starting at position {int} with length {int} and store in variable {string}")
    public void i_extract_substring_and_store(String sourceVariable, int startPosition, int length, String targetVariable) {
        String sourceValue = VariableManager.getSessionVariable(sourceVariable);
        if (sourceValue != null) {
            try {
                String substring = sourceValue.substring(startPosition, Math.min(startPosition + length, sourceValue.length()));
                VariableManager.setSessionVariable(targetVariable, substring);
                
                addVerification("Extract Substring", true, 
                    String.format("Extracted substring '%s' from variable '%s' (pos %d, length %d) and stored in '%s'", 
                        substring, sourceVariable, startPosition, length, targetVariable));
            } catch (StringIndexOutOfBoundsException e) {
                addVerification("Extract Substring", false, 
                    "Invalid substring parameters: " + e.getMessage());
                throw new RuntimeException("Invalid substring parameters", e);
            }
        } else {
            addVerification("Extract Substring", false, 
                "Source variable not found: " + sourceVariable);
            throw new RuntimeException("Source variable not found: " + sourceVariable);
        }
    }

    @When("I replace {string} with {string} in variable {string}")
    public void i_replace_in_variable(String searchText, String replaceText, String variableName) {
        String interpolatedSearch = VariableManager.interpolate(searchText);
        String interpolatedReplace = VariableManager.interpolate(replaceText);
        String currentValue = VariableManager.getSessionVariable(variableName);
        
        if (currentValue != null) {
            String newValue = currentValue.replace(interpolatedSearch, interpolatedReplace);
            VariableManager.setSessionVariable(variableName, newValue);
            
            addVerification("Replace in Variable", true, 
                String.format("Replaced '%s' with '%s' in variable '%s'. New value: '%s'", 
                    interpolatedSearch, interpolatedReplace, variableName, newValue));
        } else {
            addVerification("Replace in Variable", false, 
                "Variable not found: " + variableName);
            throw new RuntimeException("Variable not found: " + variableName);
        }
    }

    @When("I clear variable {string}")
    public void i_clear_variable(String variableName) {
        VariableManager.clearSessionVariable(variableName);
        addVerification("Clear Variable", true, 
            String.format("Cleared variable '%s'", variableName));
    }

    @When("I clear all session variables")
    public void i_clear_all_session_variables() {
        VariableManager.clearAllSessionVariables();
        addVerification("Clear All Session Variables", true, 
            "Cleared all session variables");
    }

    @When("I print variable {string}")
    public void i_print_variable(String variableName) {
        String value = VariableManager.getSessionVariable(variableName);
        if (value == null) {
            value = VariableManager.getGlobalVariable(variableName);
        }
        
        if (value != null) {
            logger.info("Variable '{}' = '{}'", variableName, value);
            addVerification("Print Variable", true, 
                String.format("Variable '%s' = '%s'", variableName, value));
        } else {
            logger.warn("Variable '{}' not found", variableName);
            addVerification("Print Variable", false, 
                "Variable not found: " + variableName);
        }
    }

    @When("I print all variables")
    public void i_print_all_variables() {
        logger.info("=== All Variables ===");
        
        // Print global variables
        logger.info("Global Variables:");
        VariableManager.getAllGlobalVariables().forEach((key, value) -> 
            logger.info("  {} = {}", key, value));
        
        // Print session variables
        logger.info("Session Variables:");
        VariableManager.getAllSessionVariables().forEach((key, value) -> 
            logger.info("  {} = {}", key, value));
        
        addVerification("Print All Variables", true, 
            "Printed all variables to log");
    }

    @When("I load environment variables")
    public void i_load_environment_variables() {
        VariableManager.loadEnvironmentVariables();
        addVerification("Load Environment Variables", true, 
            "Loaded environment variables into global scope");
    }

    @When("I calculate {string} + {string} and store result in variable {string}")
    public void i_calculate_addition_and_store(String value1, String value2, String resultVariable) {
        String interpolatedValue1 = VariableManager.interpolate(value1);
        String interpolatedValue2 = VariableManager.interpolate(value2);
        
        try {
            double num1 = Double.parseDouble(interpolatedValue1);
            double num2 = Double.parseDouble(interpolatedValue2);
            double result = num1 + num2;
            
            VariableManager.setSessionVariable(resultVariable, String.valueOf(result));
            addVerification("Calculate Addition", true, 
                String.format("Calculated %s + %s = %s and stored in variable '%s'", 
                    interpolatedValue1, interpolatedValue2, result, resultVariable));
        } catch (NumberFormatException e) {
            addVerification("Calculate Addition", false, 
                "Invalid numeric values for calculation");
            throw new RuntimeException("Invalid numeric values for calculation", e);
        }
    }

    @When("I calculate {string} - {string} and store result in variable {string}")
    public void i_calculate_subtraction_and_store(String value1, String value2, String resultVariable) {
        String interpolatedValue1 = VariableManager.interpolate(value1);
        String interpolatedValue2 = VariableManager.interpolate(value2);
        
        try {
            double num1 = Double.parseDouble(interpolatedValue1);
            double num2 = Double.parseDouble(interpolatedValue2);
            double result = num1 - num2;
            
            VariableManager.setSessionVariable(resultVariable, String.valueOf(result));
            addVerification("Calculate Subtraction", true, 
                String.format("Calculated %s - %s = %s and stored in variable '%s'", 
                    interpolatedValue1, interpolatedValue2, result, resultVariable));
        } catch (NumberFormatException e) {
            addVerification("Calculate Subtraction", false, 
                "Invalid numeric values for calculation");
            throw new RuntimeException("Invalid numeric values for calculation", e);
        }
    }

    @When("I calculate {string} * {string} and store result in variable {string}")
    public void i_calculate_multiplication_and_store(String value1, String value2, String resultVariable) {
        String interpolatedValue1 = VariableManager.interpolate(value1);
        String interpolatedValue2 = VariableManager.interpolate(value2);
        
        try {
            double num1 = Double.parseDouble(interpolatedValue1);
            double num2 = Double.parseDouble(interpolatedValue2);
            double result = num1 * num2;
            
            VariableManager.setSessionVariable(resultVariable, String.valueOf(result));
            addVerification("Calculate Multiplication", true, 
                String.format("Calculated %s * %s = %s and stored in variable '%s'", 
                    interpolatedValue1, interpolatedValue2, result, resultVariable));
        } catch (NumberFormatException e) {
            addVerification("Calculate Multiplication", false, 
                "Invalid numeric values for calculation");
            throw new RuntimeException("Invalid numeric values for calculation", e);
        }
    }

    @When("I calculate {string} \\/ {string} and store result in variable {string}")
    public void i_calculate_division_and_store(String value1, String value2, String resultVariable) {
        String interpolatedValue1 = VariableManager.interpolate(value1);
        String interpolatedValue2 = VariableManager.interpolate(value2);
        
        try {
            double num1 = Double.parseDouble(interpolatedValue1);
            double num2 = Double.parseDouble(interpolatedValue2);
            
            if (num2 == 0) {
                throw new ArithmeticException("Division by zero");
            }
            
            double result = num1 / num2;
            VariableManager.setSessionVariable(resultVariable, String.valueOf(result));
            addVerification("Calculate Division", true, 
                String.format("Calculated %s / %s = %s and stored in variable '%s'", 
                    interpolatedValue1, interpolatedValue2, result, resultVariable));
        } catch (NumberFormatException e) {
            addVerification("Calculate Division", false, 
                "Invalid numeric values for calculation");
            throw new RuntimeException("Invalid numeric values for calculation", e);
        } catch (ArithmeticException e) {
            addVerification("Calculate Division", false, 
                "Division by zero error");
            throw new RuntimeException("Division by zero error", e);
        }
    }
    
    // =====================================================================================
    // REVOLUTIONARY VARIABLE AUTOMATION - ENTERPRISE DATA MANIPULATION
    // =====================================================================================
    
    @When("I create data structure with name {string} and store in variable {string}")
    public void i_create_data_structure(String structureName, String variableName) {
        logger.info("Creating data structure '{}' and storing in variable '{}'", structureName, variableName);
        
        try {
            // Create a JSON-like data structure
            StringBuilder structure = new StringBuilder();
            structure.append("{\"name\":\"").append(structureName).append("\",");
            structure.append("\"created\":\"").append(java.time.LocalDateTime.now().toString()).append("\",");
            structure.append("\"id\":\"").append(UUID.randomUUID().toString().substring(0, 8)).append("\"}");
            
            VariableManager.setSessionVariable(variableName, structure.toString());
            addVerification("Create Data Structure", true, 
                String.format("Created data structure '%s' and stored in variable '%s'", structureName, variableName));
            
            logger.info("Successfully created data structure '{}' and stored in variable '{}'", structureName, variableName);
        } catch (Exception e) {
            logger.error("Failed to create data structure '{}': {}", structureName, e.getMessage(), e);
            addVerification("Create Data Structure", false, 
                "Data structure creation failed: " + e.getMessage());
            throw new RuntimeException("Failed to create data structure", e);
        }
    }
    
    @When("I format variable {string} as currency and store in variable {string}")
    public void i_format_variable_as_currency(String sourceVariable, String targetVariable) {
        logger.info("Formatting variable '{}' as currency and storing in variable '{}'", sourceVariable, targetVariable);
        
        try {
            String sourceValue = VariableManager.getSessionVariable(sourceVariable);
            if (sourceValue != null) {
                double amount = Double.parseDouble(sourceValue);
                java.text.NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance();
                String formattedCurrency = currencyFormat.format(amount);
                
                VariableManager.setSessionVariable(targetVariable, formattedCurrency);
                addVerification("Format Currency", true, 
                    String.format("Formatted '%s' (%s) as currency '%s' and stored in variable '%s'", 
                        sourceVariable, sourceValue, formattedCurrency, targetVariable));
                
                logger.info("Successfully formatted variable '{}' as currency: '{}'", sourceVariable, formattedCurrency);
            } else {
                addVerification("Format Currency", false, "Source variable not found: " + sourceVariable);
                throw new RuntimeException("Source variable not found: " + sourceVariable);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid number format in variable '{}': {}", sourceVariable, e.getMessage());
            addVerification("Format Currency", false, "Invalid number format: " + e.getMessage());
            throw new RuntimeException("Invalid number format for currency conversion", e);
        } catch (Exception e) {
            logger.error("Failed to format currency for variable '{}': {}", sourceVariable, e.getMessage(), e);
            addVerification("Format Currency", false, "Currency formatting failed: " + e.getMessage());
            throw new RuntimeException("Failed to format currency", e);
        }
    }
    
    @When("I parse JSON from variable {string} and extract field {string} to variable {string}")
    public void i_parse_json_and_extract_field(String jsonVariable, String fieldPath, String targetVariable) {
        logger.info("Parsing JSON from variable '{}' and extracting field '{}' to variable '{}'", 
            jsonVariable, fieldPath, targetVariable);
        
        try {
            String jsonString = VariableManager.getSessionVariable(jsonVariable);
            if (jsonString != null) {
                // Simple JSON parsing for basic fields (for full JSON support, would use Jackson/Gson)
                String extractedValue = extractJsonField(jsonString, fieldPath);
                
                VariableManager.setSessionVariable(targetVariable, extractedValue);
                addVerification("Parse JSON Field", true, 
                    String.format("Extracted field '%s' value '%s' from JSON and stored in variable '%s'", 
                        fieldPath, extractedValue, targetVariable));
                
                logger.info("Successfully extracted JSON field '{}' value '{}' to variable '{}'", 
                    fieldPath, extractedValue, targetVariable);
            } else {
                addVerification("Parse JSON Field", false, "JSON variable not found: " + jsonVariable);
                throw new RuntimeException("JSON variable not found: " + jsonVariable);
            }
        } catch (Exception e) {
            logger.error("Failed to parse JSON field '{}' from variable '{}': {}", fieldPath, jsonVariable, e.getMessage(), e);
            addVerification("Parse JSON Field", false, "JSON parsing failed: " + e.getMessage());
            throw new RuntimeException("Failed to parse JSON field", e);
        }
    }
    
    @When("I encode variable {string} as Base64 and store in variable {string}")
    public void i_encode_variable_as_base64(String sourceVariable, String targetVariable) {
        logger.info("Encoding variable '{}' as Base64 and storing in variable '{}'", sourceVariable, targetVariable);
        
        try {
            String sourceValue = VariableManager.getSessionVariable(sourceVariable);
            if (sourceValue != null) {
                String encodedValue = java.util.Base64.getEncoder().encodeToString(sourceValue.getBytes());
                
                VariableManager.setSessionVariable(targetVariable, encodedValue);
                addVerification("Base64 Encode", true, 
                    String.format("Encoded variable '%s' as Base64 and stored in variable '%s'", 
                        sourceVariable, targetVariable));
                
                logger.info("Successfully encoded variable '{}' as Base64 and stored in variable '{}'", 
                    sourceVariable, targetVariable);
            } else {
                addVerification("Base64 Encode", false, "Source variable not found: " + sourceVariable);
                throw new RuntimeException("Source variable not found: " + sourceVariable);
            }
        } catch (Exception e) {
            logger.error("Failed to encode variable '{}' as Base64: {}", sourceVariable, e.getMessage(), e);
            addVerification("Base64 Encode", false, "Base64 encoding failed: " + e.getMessage());
            throw new RuntimeException("Failed to encode as Base64", e);
        }
    }
    
    @When("I decode variable {string} from Base64 and store in variable {string}")
    public void i_decode_variable_from_base64(String sourceVariable, String targetVariable) {
        logger.info("Decoding variable '{}' from Base64 and storing in variable '{}'", sourceVariable, targetVariable);
        
        try {
            String sourceValue = VariableManager.getSessionVariable(sourceVariable);
            if (sourceValue != null) {
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(sourceValue);
                String decodedValue = new String(decodedBytes);
                
                VariableManager.setSessionVariable(targetVariable, decodedValue);
                addVerification("Base64 Decode", true, 
                    String.format("Decoded variable '%s' from Base64 and stored in variable '%s'", 
                        sourceVariable, targetVariable));
                
                logger.info("Successfully decoded variable '{}' from Base64 and stored in variable '{}'", 
                    sourceVariable, targetVariable);
            } else {
                addVerification("Base64 Decode", false, "Source variable not found: " + sourceVariable);
                throw new RuntimeException("Source variable not found: " + sourceVariable);
            }
        } catch (Exception e) {
            logger.error("Failed to decode variable '{}' from Base64: {}", sourceVariable, e.getMessage(), e);
            addVerification("Base64 Decode", false, "Base64 decoding failed: " + e.getMessage());
            throw new RuntimeException("Failed to decode from Base64", e);
        }
    }
    
    @When("I split variable {string} by delimiter {string} and store parts with prefix {string}")
    public void i_split_variable_by_delimiter(String sourceVariable, String delimiter, String prefix) {
        logger.info("Splitting variable '{}' by delimiter '{}' and storing parts with prefix '{}'", 
            sourceVariable, delimiter, prefix);
        
        try {
            String sourceValue = VariableManager.getSessionVariable(sourceVariable);
            if (sourceValue != null) {
                String[] parts = sourceValue.split(java.util.regex.Pattern.quote(delimiter));
                
                for (int i = 0; i < parts.length; i++) {
                    String partVariable = prefix + "_part_" + (i + 1);
                    VariableManager.setSessionVariable(partVariable, parts[i].trim());
                    logger.debug("Stored part {} in variable '{}': '{}'", (i + 1), partVariable, parts[i].trim());
                }
                
                // Store total count
                VariableManager.setSessionVariable(prefix + "_count", String.valueOf(parts.length));
                
                addVerification("Split Variable", true, 
                    String.format("Split variable '%s' into %d parts with prefix '%s'", 
                        sourceVariable, parts.length, prefix));
                
                logger.info("Successfully split variable '{}' into {} parts with prefix '{}'", 
                    sourceVariable, parts.length, prefix);
            } else {
                addVerification("Split Variable", false, "Source variable not found: " + sourceVariable);
                throw new RuntimeException("Source variable not found: " + sourceVariable);
            }
        } catch (Exception e) {
            logger.error("Failed to split variable '{}': {}", sourceVariable, e.getMessage(), e);
            addVerification("Split Variable", false, "Variable splitting failed: " + e.getMessage());
            throw new RuntimeException("Failed to split variable", e);
        }
    }
    
    @When("I join variables with prefix {string} using delimiter {string} and store in variable {string}")
    public void i_join_variables_with_prefix(String prefix, String delimiter, String targetVariable) {
        logger.info("Joining variables with prefix '{}' using delimiter '{}' and storing in variable '{}'", 
            prefix, delimiter, targetVariable);
        
        try {
            String countVar = prefix + "_count";
            String countStr = VariableManager.getSessionVariable(countVar);
            
            if (countStr != null) {
                int count = Integer.parseInt(countStr);
                StringBuilder joined = new StringBuilder();
                
                for (int i = 1; i <= count; i++) {
                    String partVariable = prefix + "_part_" + i;
                    String partValue = VariableManager.getSessionVariable(partVariable);
                    
                    if (partValue != null) {
                        if (joined.length() > 0) {
                            joined.append(delimiter);
                        }
                        joined.append(partValue);
                    }
                }
                
                VariableManager.setSessionVariable(targetVariable, joined.toString());
                addVerification("Join Variables", true, 
                    String.format("Joined %d variables with prefix '%s' and stored in variable '%s'", 
                        count, prefix, targetVariable));
                
                logger.info("Successfully joined {} variables with prefix '{}' and stored in variable '{}'", 
                    count, prefix, targetVariable);
            } else {
                addVerification("Join Variables", false, "Count variable not found: " + countVar);
                throw new RuntimeException("Count variable not found: " + countVar);
            }
        } catch (Exception e) {
            logger.error("Failed to join variables with prefix '{}': {}", prefix, e.getMessage(), e);
            addVerification("Join Variables", false, "Variable joining failed: " + e.getMessage());
            throw new RuntimeException("Failed to join variables", e);
        }
    }
    
    @When("I validate variable {string} against regex pattern {string}")
    public void i_validate_variable_against_regex(String variableName, String regexPattern) {
        String interpolatedPattern = VariableManager.interpolate(regexPattern);
        logger.info("Validating variable '{}' against regex pattern '{}'", variableName, interpolatedPattern);
        
        try {
            String variableValue = VariableManager.getSessionVariable(variableName);
            if (variableValue != null) {
                boolean matches = variableValue.matches(interpolatedPattern);
                
                addVerification("Regex Validation", matches, 
                    String.format("Variable '%s' value '%s' %s match pattern '%s'", 
                        variableName, variableValue, matches ? "does" : "does not", interpolatedPattern));
                
                if (!matches) {
                    throw new AssertionError(String.format("Variable '%s' value '%s' does not match pattern '%s'", 
                        variableName, variableValue, interpolatedPattern));
                }
                
                logger.info("Successfully validated variable '{}' against regex pattern '{}'", variableName, interpolatedPattern);
            } else {
                addVerification("Regex Validation", false, "Variable not found: " + variableName);
                throw new RuntimeException("Variable not found: " + variableName);
            }
        } catch (Exception e) {
            logger.error("Failed to validate variable '{}' against regex: {}", variableName, e.getMessage(), e);
            addVerification("Regex Validation", false, "Regex validation failed: " + e.getMessage());
            throw new RuntimeException("Failed to validate variable against regex", e);
        }
    }
    
    @When("I backup all session variables to file {string}")
    public void i_backup_session_variables_to_file(String fileName) {
        logger.info("Backing up all session variables to file '{}'", fileName);
        
        try {            java.util.Map<String, Object> allVariables = VariableManager.getAllSessionVariables();
            StringBuilder backup = new StringBuilder();
            
            backup.append("# Session Variables Backup - ").append(java.time.LocalDateTime.now()).append("\n");
            for (java.util.Map.Entry<String, Object> entry : allVariables.entrySet()) {
                backup.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue())).append("\n");
            }
            
            java.nio.file.Path backupPath = java.nio.file.Paths.get("target", "variables", fileName);
            java.nio.file.Files.createDirectories(backupPath.getParent());
            java.nio.file.Files.write(backupPath, backup.toString().getBytes());
            
            addVerification("Backup Variables", true, 
                String.format("Backed up %d session variables to file '%s'", allVariables.size(), fileName));
            
            logger.info("Successfully backed up {} session variables to file '{}'", allVariables.size(), fileName);
        } catch (Exception e) {
            logger.error("Failed to backup variables to file '{}': {}", fileName, e.getMessage(), e);
            addVerification("Backup Variables", false, "Variable backup failed: " + e.getMessage());
            throw new RuntimeException("Failed to backup variables", e);
        }
    }
    
    @When("I restore session variables from file {string}")
    public void i_restore_session_variables_from_file(String fileName) {
        logger.info("Restoring session variables from file '{}'", fileName);
        
        try {
            java.nio.file.Path backupPath = java.nio.file.Paths.get("target", "variables", fileName);
            java.util.List<String> lines = java.nio.file.Files.readAllLines(backupPath);
            
            int restoredCount = 0;
            for (String line : lines) {
                if (!line.startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        VariableManager.setSessionVariable(parts[0], parts[1]);
                        restoredCount++;
                    }
                }
            }
            
            addVerification("Restore Variables", true, 
                String.format("Restored %d session variables from file '%s'", restoredCount, fileName));
            
            logger.info("Successfully restored {} session variables from file '{}'", restoredCount, fileName);
        } catch (Exception e) {
            logger.error("Failed to restore variables from file '{}': {}", fileName, e.getMessage(), e);
            addVerification("Restore Variables", false, "Variable restore failed: " + e.getMessage());
            throw new RuntimeException("Failed to restore variables", e);
        }    }
    
    @Then("the variable {string} should contain {string}")
    public void the_variable_should_contain(String variableName, String expectedValue) {
        try {
            String actualValue = VariableManager.getSessionVariable(variableName);
            if (actualValue == null) {
                actualValue = VariableManager.getGlobalVariable(variableName);
            }
            
            String interpolatedExpected = VariableManager.interpolate(expectedValue);
            boolean contains = actualValue != null && actualValue.contains(interpolatedExpected);
            
            addVerification("Variable Contains Check", contains, 
                String.format("Variable '%s' = '%s' %s contain '%s'", 
                    variableName, actualValue, contains ? "does" : "does not", interpolatedExpected));
                    
            if (!contains) {
                throw new AssertionError(String.format("Variable '%s' with value '%s' does not contain '%s'", 
                    variableName, actualValue, interpolatedExpected));
            }
            
        } catch (Exception e) {
            addVerification("Variable Contains Check", false, 
                String.format("Failed to check variable '%s': %s", variableName, e.getMessage()));
            throw e;
        }
    }

    @Then("the variable {string} should equal {string}")
    public void the_variable_should_equal(String variableName, String expectedValue) {
        try {
            String actualValue = VariableManager.getSessionVariable(variableName);
            if (actualValue == null) {
                actualValue = VariableManager.getGlobalVariable(variableName);
            }
            
            String interpolatedExpected = VariableManager.interpolate(expectedValue);
            boolean equals = interpolatedExpected.equals(actualValue);
            
            addVerification("Variable Equals Check", equals, 
                String.format("Variable '%s' = '%s' %s equal '%s'", 
                    variableName, actualValue, equals ? "does" : "does not", interpolatedExpected));
                    
            if (!equals) {
                throw new AssertionError(String.format("Variable '%s' with value '%s' does not equal '%s'", 
                    variableName, actualValue, interpolatedExpected));
            }
            
        } catch (Exception e) {
            addVerification("Variable Equals Check", false, 
                String.format("Failed to check variable '%s': %s", variableName, e.getMessage()));
            throw e;
        }
    }
    
    // =====================================================================================
    // ADVANCED HELPER METHODS FOR COMPLEX VARIABLE OPERATIONS
    // =====================================================================================
    
    private String extractJsonField(String jsonString, String fieldPath) {
        // Simple JSON field extraction (for basic use cases)
        // For production use, would implement proper JSON parsing
        try {
            String pattern = "\"" + fieldPath + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(jsonString);
            
            if (m.find()) {
                return m.group(1);
            } else {
                // Try without quotes for numeric values
                pattern = "\"" + fieldPath + "\"\\s*:\\s*([^,}]+)";
                p = java.util.regex.Pattern.compile(pattern);
                m = p.matcher(jsonString);
                
                if (m.find()) {
                    return m.group(1).trim();
                }
            }
            
            throw new RuntimeException("Field not found in JSON: " + fieldPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract JSON field: " + fieldPath, e);
        }
    }
}
