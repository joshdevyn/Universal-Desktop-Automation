package com.automation.cucumber.dataproviders;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Universal Excel Data Provider for BDD scenarios
 * Supports .xlsx and .xls files with multiple sheets and advanced data handling
 */
public class ExcelDataProvider {
    private static final Logger logger = LoggerFactory.getLogger(ExcelDataProvider.class);
    private static final String DATA_DIRECTORY = "src/test/resources/testdata/";
    
    /**
     * Load Excel data from first sheet
     * @param fileName Excel file name (with extension)
     * @return List of data maps with column headers as keys
     */
    public static List<Map<String, String>> loadData(String fileName) {
        return loadData(fileName, 0); // Load first sheet
    }
    
    /**
     * Load Excel data from specific sheet
     * @param fileName Excel file name (with extension)
     * @param sheetIndex Sheet index (0-based)
     * @return List of data maps with column headers as keys
     */
    public static List<Map<String, String>> loadData(String fileName, int sheetIndex) {
        String fullPath = DATA_DIRECTORY + fileName;
        Path excelPath = Paths.get(fullPath);
        
        logger.info("Loading Excel data from: {} (Sheet: {})", fullPath, sheetIndex);
        
        if (!Files.exists(excelPath)) {
            logger.warn("Excel file not found: {}, creating sample data", fullPath);
            createSampleExcelFile(fullPath);
        }
        
        List<Map<String, String>> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(excelPath.toFile());
             Workbook workbook = createWorkbook(fileName, fis)) {
            
            if (sheetIndex >= workbook.getNumberOfSheets()) {
                logger.error("Sheet index {} is out of range. File has {} sheets", 
                    sheetIndex, workbook.getNumberOfSheets());
                throw new IllegalArgumentException("Invalid sheet index: " + sheetIndex);
            }
            
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            logger.info("Loading data from sheet: {}", sheet.getSheetName());
            
            data = processSheet(sheet);
            logger.info("Loaded {} rows of data from {} (Sheet: {})", data.size(), fileName, sheet.getSheetName());
            
        } catch (IOException e) {
            logger.error("Failed to load Excel data from {}: {}", fullPath, e.getMessage());
            throw new RuntimeException("Failed to load Excel test data", e);
        }
        
        return data;
    }
    
    /**
     * Load Excel data from specific sheet by name
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return List of data maps
     */
    public static List<Map<String, String>> loadData(String fileName, String sheetName) {
        String fullPath = DATA_DIRECTORY + fileName;
        Path excelPath = Paths.get(fullPath);
        
        logger.info("Loading Excel data from: {} (Sheet: {})", fullPath, sheetName);
        
        List<Map<String, String>> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(excelPath.toFile());
             Workbook workbook = createWorkbook(fileName, fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet '{}' not found in file {}", sheetName, fileName);
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }
            
            data = processSheet(sheet);
            logger.info("Loaded {} rows of data from {} (Sheet: {})", data.size(), fileName, sheetName);
            
        } catch (IOException e) {
            logger.error("Failed to load Excel data from {}: {}", fullPath, e.getMessage());
            throw new RuntimeException("Failed to load Excel test data", e);
        }
        
        return data;
    }
    
    /**
     * Get all sheet names from Excel file
     * @param fileName Excel file name
     * @return List of sheet names
     */
    public static List<String> getSheetNames(String fileName) {
        String fullPath = DATA_DIRECTORY + fileName;
        List<String> sheetNames = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(fullPath);
             Workbook workbook = createWorkbook(fileName, fis)) {
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetAt(i).getSheetName());
            }
            
        } catch (IOException e) {
            logger.error("Failed to read sheet names from {}: {}", fullPath, e.getMessage());
            throw new RuntimeException("Failed to read Excel file", e);
        }
        
        return sheetNames;
    }
    
    /**
     * Load data with filtering based on column values
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param filterColumn Column to filter on
     * @param filterValues Values to include
     * @return Filtered data maps
     */
    public static List<Map<String, String>> loadDataWithFilter(String fileName, String sheetName, 
                                                              String filterColumn, List<String> filterValues) {
        List<Map<String, String>> allData = loadData(fileName, sheetName);
        
        return allData.stream()
            .filter(row -> filterValues.contains(row.get(filterColumn)))
            .collect(Collectors.toList());
    }
    
    /**
     * Load specific test cases by ID
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param testCaseIds List of test case IDs
     * @return Filtered data for specific test cases
     */
    public static List<Map<String, String>> loadDataForTestCases(String fileName, String sheetName, 
                                                                List<String> testCaseIds) {
        List<Map<String, String>> allData = loadData(fileName, sheetName);
        
        return allData.stream()
            .filter(row -> testCaseIds.contains(row.get("testCaseId")) || 
                          testCaseIds.contains(row.get("test_case_id")) ||
                          testCaseIds.contains(row.get("testCase")))
            .collect(Collectors.toList());
    }
    
    /**
     * Load Excel data from first sheet by file path
     * @param filePath Full path to Excel file
     * @return List of data maps with column headers as keys
     */
    public static List<Map<String, String>> loadFromFile(String filePath) {
        return loadFromFile(filePath, 0); // Load first sheet
    }
    
    /**
     * Load Excel data from specific sheet by file path
     * @param filePath Full path to Excel file
     * @param sheetIndex Sheet index (0-based)
     * @return List of data maps with column headers as keys
     */
    public static List<Map<String, String>> loadFromFile(String filePath, int sheetIndex) {
        Path excelPath = Paths.get(filePath);
        
        logger.info("Loading Excel data from file: {} (Sheet: {})", filePath, sheetIndex);
        
        if (!Files.exists(excelPath)) {
            logger.error("Excel file not found: {}", filePath);
            throw new RuntimeException("Excel file not found: " + filePath);
        }
        
        List<Map<String, String>> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(excelPath.toFile());
             Workbook workbook = createWorkbook(excelPath.getFileName().toString(), fis)) {
            
            if (sheetIndex >= workbook.getNumberOfSheets()) {
                logger.error("Sheet index {} is out of range. File has {} sheets", 
                    sheetIndex, workbook.getNumberOfSheets());
                throw new IllegalArgumentException("Invalid sheet index: " + sheetIndex);
            }
            
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            logger.info("Loading data from sheet: {}", sheet.getSheetName());
            
            data = processSheet(sheet);
            logger.info("Loaded {} rows of data from {} (Sheet: {})", data.size(), filePath, sheet.getSheetName());
            
        } catch (IOException e) {
            logger.error("Failed to load Excel data from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load Excel test data", e);
        }
        
        return data;
    }
    
    /**
     * Load Excel data from specific sheet by name using file path
     * @param filePath Full path to Excel file
     * @param sheetName Sheet name
     * @return List of data maps
     */
    public static List<Map<String, String>> loadFromFile(String filePath, String sheetName) {
        Path excelPath = Paths.get(filePath);
        
        logger.info("Loading Excel data from file: {} (Sheet: {})", filePath, sheetName);
        
        if (!Files.exists(excelPath)) {
            logger.error("Excel file not found: {}", filePath);
            throw new RuntimeException("Excel file not found: " + filePath);
        }
        
        List<Map<String, String>> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(excelPath.toFile());
             Workbook workbook = createWorkbook(excelPath.getFileName().toString(), fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet '{}' not found in file {}", sheetName, filePath);
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }
            
            data = processSheet(sheet);
            logger.info("Loaded {} rows of data from {} (Sheet: {})", data.size(), filePath, sheetName);
            
        } catch (IOException e) {
            logger.error("Failed to load Excel data from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load Excel test data", e);
        }
        
        return data;
    }

    /**
     * Get all sheet names from Excel file by file path
     * @param filePath Full path to Excel file
     * @return List of sheet names
     */
    public static List<String> getSheetsFromFile(String filePath) {
        List<String> sheetNames = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = createWorkbook(Paths.get(filePath).getFileName().toString(), fis)) {
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetAt(i).getSheetName());
            }
            
        } catch (IOException e) {
            logger.error("Failed to read sheet names from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to read Excel file", e);
        }
        
        return sheetNames;
    }
    
    /**
     * Process Excel sheet and convert to data maps
     * @param sheet Excel sheet to process
     * @return List of data maps
     */
    private static List<Map<String, String>> processSheet(Sheet sheet) {
        List<Map<String, String>> data = new ArrayList<>();
        
        if (sheet.getPhysicalNumberOfRows() == 0) {
            logger.warn("Sheet '{}' is empty", sheet.getSheetName());
            return data;
        }
        
        // Get headers from first row
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            logger.warn("No header row found in sheet '{}'", sheet.getSheetName());
            return data;
        }
        
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            String header = getCellValueAsString(cell);
            headers.add(header != null ? header.trim() : "Column_" + (i + 1));
        }
        
        logger.debug("Excel headers: {}", headers);
        
        // Process data rows
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowEmpty(row)) {
                continue;
            }
            
            Map<String, String> rowData = new HashMap<>();
            
            for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                String cellValue = getCellValueAsString(cell);
                
                // Handle variable substitution
                if (cellValue != null) {
                    cellValue = substituteVariables(cellValue);
                }
                
                rowData.put(headers.get(cellIndex), cellValue != null ? cellValue.trim() : "");
            }
            
            // Add row metadata
            rowData.put("_row_number", String.valueOf(rowIndex));
            rowData.put("_sheet_name", sheet.getSheetName());
            
            data.add(rowData);
        }
        
        return data;
    }
    
    /**
     * Get cell value as string regardless of cell type
     * @param cell Excel cell
     * @return String representation of cell value
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return getCellValueAsString(cell); // Try to evaluate formula
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            case BLANK:
            case _NONE:
            default:
                return "";
        }
    }
    
    /**
     * Check if row is empty
     * @param row Excel row
     * @return true if row is empty
     */
    private static boolean isRowEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            String cellValue = getCellValueAsString(cell);
            if (cellValue != null && !cellValue.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Create workbook based on file extension
     * @param fileName File name
     * @param inputStream File input stream
     * @return Workbook instance
     * @throws IOException If file cannot be read
     */
    private static Workbook createWorkbook(String fileName, FileInputStream inputStream) throws IOException {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Use .xlsx or .xls files.");
        }
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
     * Create sample Excel file for testing
     * @param filePath Path to create the sample file
     */
    private static void createSampleExcelFile(String filePath) {
        try {
            Path parentDir = Paths.get(filePath).getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            
            try (Workbook workbook = new XSSFWorkbook()) {
                // Create main test data sheet
                Sheet sheet = workbook.createSheet("TestData");
                
                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"testCaseId", "description", "application", "input1", "input2", "expected", "priority", "enabled"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }
                
                // Create sample data rows
                Object[][] sampleData = {
                    {"TC001", "Basic calculator addition", "calculator", "2", "3", "5", "high", "true"},
                    {"TC002", "Calculator multiplication", "calculator", "4", "5", "20", "medium", "true"},
                    {"TC003", "Text editor input", "notepad", "Hello World", "", "Hello World", "low", "true"},
                    {"TC004", "Login validation", "any_app", "${TEST_USERNAME}", "${TEST_PASSWORD}", "success", "high", "true"},
                    {"TC005", "Date validation", "any_app", "${DATE}", "", "valid", "medium", "false"}
                };
                
                for (int i = 0; i < sampleData.length; i++) {
                    Row row = sheet.createRow(i + 1);
                    Object[] rowData = sampleData[i];
                    for (int j = 0; j < rowData.length; j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(rowData[j].toString());
                    }
                }
                
                // Auto-size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // Create additional sheets for different test types
                createSmokeTestSheet(workbook);
                createRegressionTestSheet(workbook);
                
                // Save workbook
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    workbook.write(fos);
                }
                
                logger.info("Created sample Excel file: {}", filePath);
            }
            
        } catch (IOException e) {
            logger.error("Failed to create sample Excel file: {}", e.getMessage());
        }
    }
    
    /**
     * Create smoke test sheet
     * @param workbook Workbook to add sheet to
     */
    private static void createSmokeTestSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("SmokeTests");
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"testCaseId", "application", "testType", "action", "expected", "priority"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // Sample smoke test data
        Object[][] smokeData = {
            {"SMOKE001", "calculator", "startup", "launch_application", "application_opens", "critical"},
            {"SMOKE002", "notepad", "startup", "launch_application", "application_opens", "critical"},
            {"SMOKE003", "calculator", "basic_function", "perform_calculation", "correct_result", "high"},
            {"SMOKE004", "any_app", "window_management", "focus_window", "window_focused", "medium"}
        };
        
        for (int i = 0; i < smokeData.length; i++) {
            Row row = sheet.createRow(i + 1);
            Object[] rowData = smokeData[i];
            for (int j = 0; j < rowData.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rowData[j].toString());
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create regression test sheet
     * @param workbook Workbook to add sheet to
     */
    private static void createRegressionTestSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("RegressionTests");
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"testCaseId", "feature", "scenario", "application", "steps", "verification", "automation_level"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // Sample regression test data
        Object[][] regressionData = {
            {"REG001", "calculation", "basic_operations", "calculator", "add_two_numbers", "result_is_correct", "full"},
            {"REG002", "text_editing", "input_validation", "notepad", "type_text", "text_appears", "full"},
            {"REG003", "window_management", "focus_handling", "any_app", "switch_windows", "correct_window_focused", "partial"},
            {"REG004", "error_handling", "invalid_input", "calculator", "divide_by_zero", "error_message_shown", "manual"}
        };
        
        for (int i = 0; i < regressionData.length; i++) {
            Row row = sheet.createRow(i + 1);
            Object[] rowData = regressionData[i];
            for (int j = 0; j < rowData.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rowData[j].toString());
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Validate Excel data structure
     * @param fileName Excel file name
     * @param sheetName Sheet name (optional, uses first sheet if null)
     * @return Validation results
     */
    public static Map<String, Object> validateDataStructure(String fileName, String sheetName) {
        Map<String, Object> validation = new HashMap<>();
        
        try {
            List<Map<String, String>> data;
            if (sheetName != null) {
                data = loadData(fileName, sheetName);
            } else {
                data = loadData(fileName);
            }
            
            validation.put("isValid", true);
            validation.put("rowCount", data.size());
            validation.put("columnCount", data.isEmpty() ? 0 : data.get(0).size());
            
            if (!data.isEmpty()) {
                validation.put("columns", new ArrayList<>(data.get(0).keySet()));
                validation.put("sheetName", data.get(0).get("_sheet_name"));
            }
            
            // Check for required columns
            List<String> requiredColumns = Arrays.asList("testCaseId", "description");
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
            
            logger.info("Excel validation for {} ({}): {} rows, {} columns", 
                fileName, sheetName, data.size(), data.isEmpty() ? 0 : data.get(0).size());
            
        } catch (Exception e) {
            validation.put("isValid", false);
            validation.put("error", e.getMessage());
            logger.error("Excel validation failed for {} ({}): {}", fileName, sheetName, e.getMessage());
        }
        
        return validation;
    }
}
