package com.automation.cucumber.stepdefinitions;

import com.automation.core.ProcessManager;
import com.automation.models.ManagedApplicationContext;
import com.automation.utils.WaitUtilsStatic;
import com.automation.utils.VariableManager;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Input Interaction Step Definitions
 * Handles keyboard and mouse interactions
 */
public class InputStepDefinitions extends CommonStepDefinitionsBase {
    private static final Logger logger = LoggerFactory.getLogger(InputStepDefinitions.class);
    
    @When("I type {string} in the active field")
    public void i_type_in_the_active_field(String text) {
        windowController.sendText(text);
        addVerification("Type Text", true, 
            String.format("Typed text: '%s'", text));
        WaitUtilsStatic.waitMilliseconds(500);
    }    
    @When("I press key {string}")
    public void i_press_key(String keyName) {
        windowController.sendKey(keyName);
        addVerification("Press Key", true, 
            String.format("Pressed key: '%s'", keyName));
        WaitUtilsStatic.waitMilliseconds(300);
    }    
    @When("I press key combination {string}")
    public void i_press_key_combination(String keyCombo) {
        String[] keys = keyCombo.split("\\+");
        windowController.sendKeyCombo(keys);
        addVerification("Press Key Combination", true, 
            String.format("Pressed key combination: '%s'", keyCombo));
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I click at coordinates {int}, {int}")
    public void i_click_at_coordinates(int x, int y) {
        windowController.mouseClick(x, y);
        addVerification("Click Coordinates", true, 
            String.format("Clicked at coordinates (%d, %d)", x, y));
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I right-click at coordinates {int}, {int}")
    public void i_right_click_at_coordinates(int x, int y) {
        windowController.mouseRightClick(x, y);
        addVerification("Right Click Coordinates", true, 
            String.format("Right-clicked at coordinates (%d, %d)", x, y));
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I double-click at coordinates {int}, {int}")
    public void i_double_click_at_coordinates(int x, int y) {
        windowController.mouseDoubleClick(x, y);
        addVerification("Double Click Coordinates", true, 
            String.format("Double-clicked at coordinates (%d, %d)", x, y));
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    // =====================================================================================
    // REVOLUTIONARY KEYBOARD AUTOMATION - EVERY KEY COMBINATION IMAGINABLE
    // =====================================================================================
    
    @When("I press function key F{int}")
    public void i_press_function_key(int functionKeyNumber) {
        String keyName = "F" + functionKeyNumber;
        windowController.sendKey(keyName);
        addVerification("Function Key Press", true, 
            String.format("Pressed function key: %s", keyName));
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I press {string} key {int} times")
    public void i_press_key_multiple_times(String keyName, int times) {
        for (int i = 0; i < times; i++) {
            windowController.sendKey(keyName);
            WaitUtilsStatic.waitMilliseconds(100);
        }
        addVerification("Multiple Key Press", true, 
            String.format("Pressed key '%s' %d times", keyName, times));
    }
    
    @When("I hold key {string} for {int} milliseconds")
    public void i_hold_key_for_duration(String keyName, int durationMs) {
        windowController.keyDown(keyName);
        WaitUtilsStatic.waitMilliseconds(durationMs);
        windowController.keyUp(keyName);
        addVerification("Hold Key", true, 
            String.format("Held key '%s' for %d milliseconds", keyName, durationMs));
    }
    
    @When("I type with typing speed {int} milliseconds per character: {string}")
    public void i_type_text_with_speed(int speedMs, String text) {
        for (char c : text.toCharArray()) {
            windowController.sendText(String.valueOf(c));
            WaitUtilsStatic.waitMilliseconds(speedMs);
        }
        addVerification("Slow Type", true, 
            String.format("Typed text '%s' with %d ms per character", text, speedMs));
    }
    
    @When("I clear text field using {string}")
    public void i_clear_text_field(String method) {
        switch (method.toLowerCase()) {
            case "ctrl+a":
                windowController.sendKeyCombo(new String[]{"Ctrl", "a"});
                WaitUtilsStatic.waitMilliseconds(100);
                windowController.sendKey("Delete");
                break;
            case "backspace":
                for (int i = 0; i < 100; i++) { // Clear up to 100 characters
                    windowController.sendKey("BackSpace");
                    WaitUtilsStatic.waitMilliseconds(10);
                }
                break;
            case "delete":
                windowController.sendKeyCombo(new String[]{"Ctrl", "End"});
                WaitUtilsStatic.waitMilliseconds(100);
                for (int i = 0; i < 100; i++) {
                    windowController.sendKey("BackSpace");
                    WaitUtilsStatic.waitMilliseconds(10);
                }
                break;
        }
        addVerification("Clear Text Field", true, 
            String.format("Cleared text field using method: %s", method));
    }
    
    @When("I type special characters: {string}")
    public void i_type_special_characters(String specialText) {
        // Handle special characters that might need specific encoding
        windowController.sendText(specialText);
        addVerification("Special Characters", true, 
            String.format("Typed special characters: '%s'", specialText));
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I paste clipboard content")
    public void i_paste_clipboard_content() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "v"});
        addVerification("Paste Clipboard", true, "Pasted clipboard content");
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I copy selected text")
    public void i_copy_selected_text() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "c"});
        addVerification("Copy Text", true, "Copied selected text to clipboard");
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I cut selected text")
    public void i_cut_selected_text() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "x"});
        addVerification("Cut Text", true, "Cut selected text to clipboard");
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I select all text")
    public void i_select_all_text() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "a"});
        addVerification("Select All", true, "Selected all text");
        WaitUtilsStatic.waitMilliseconds(200);
    }
    
    @When("I undo last action")
    public void i_undo_last_action() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "z"});
        addVerification("Undo", true, "Performed undo action");
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I redo last action")
    public void i_redo_last_action() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "y"});
        addVerification("Redo", true, "Performed redo action");
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    // =====================================================================================
    // ADVANCED MOUSE AUTOMATION - PRECISION CONTROL FOR ANY APPLICATION
    // =====================================================================================
    
    @When("I move mouse to coordinates {int}, {int}")
    public void i_move_mouse_to_coordinates(int x, int y) {
        windowController.moveMouse(x, y);
        addVerification("Move Mouse", true, 
            String.format("Moved mouse to coordinates (%d, %d)", x, y));
        WaitUtilsStatic.waitMilliseconds(200);
    }
    
    @When("I drag from coordinates {int}, {int} to {int}, {int}")
    public void i_drag_from_coordinates_to_coordinates(int x1, int y1, int x2, int y2) {
        windowController.dragAndDrop(x1, y1, x2, y2);
        addVerification("Drag Coordinates", true, 
            String.format("Dragged from (%d, %d) to (%d, %d)", x1, y1, x2, y2));
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I middle-click at coordinates {int}, {int}")
    public void i_middle_click_at_coordinates(int x, int y) {
        windowController.mouseMiddleClick(x, y);
        addVerification("Middle Click", true, 
            String.format("Middle-clicked at coordinates (%d, %d)", x, y));
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I scroll {string} at coordinates {int}, {int}")
    public void i_scroll_at_coordinates(String direction, int x, int y) {
        windowController.moveMouse(x, y);
        WaitUtilsStatic.waitMilliseconds(100);
        
        if (direction.toLowerCase().contains("up")) {
            windowController.sendKey("Page_Up");
        } else if (direction.toLowerCase().contains("down")) {
            windowController.sendKey("Page_Down");
        }
        
        addVerification("Scroll at Coordinates", true, 
            String.format("Scrolled %s at coordinates (%d, %d)", direction, x, y));
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I perform mouse gesture: {string}")
    public void i_perform_mouse_gesture(String gesture) {
        // Parse gesture commands like "move(100,100)->click->move(200,200)->click"
        String[] gestureParts = gesture.split("->");
        
        for (String part : gestureParts) {
            part = part.trim();
            if (part.startsWith("move(")) {
                // Extract coordinates from move(x,y)
                String coords = part.substring(5, part.length() - 1);
                String[] xy = coords.split(",");
                int x = Integer.parseInt(xy[0].trim());
                int y = Integer.parseInt(xy[1].trim());
                windowController.moveMouse(x, y);
                WaitUtilsStatic.waitMilliseconds(100);
            } else if (part.equals("click")) {
                windowController.mouseClick();
                WaitUtilsStatic.waitMilliseconds(100);
            } else if (part.equals("rightclick")) {
                windowController.mouseRightClick();
                WaitUtilsStatic.waitMilliseconds(100);
            } else if (part.equals("doubleclick")) {
                windowController.mouseDoubleClick();
                WaitUtilsStatic.waitMilliseconds(100);
            }
        }
        
        addVerification("Mouse Gesture", true, 
            String.format("Performed mouse gesture: %s", gesture));
    }
    
    // =====================================================================================
    // ENTERPRISE FILE AND DIALOG AUTOMATION
    // =====================================================================================
    
    @When("I save the file as {string}")
    public void i_save_file_with_name(String fileName) {
        windowController.sendKeyCombo(new String[]{"Ctrl", "s"});
        WaitUtilsStatic.waitMilliseconds(1000); // Wait for Save dialog
        windowController.sendText(fileName);
        WaitUtilsStatic.waitMilliseconds(300);
        windowController.sendKey("Enter");
        addVerification("Save File", true, 
            String.format("Saved file with name: %s", fileName));
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I open file with name {string}")
    public void i_open_file_with_name(String fileName) {
        windowController.sendKeyCombo(new String[]{"Ctrl", "o"});
        WaitUtilsStatic.waitMilliseconds(1000); // Wait for Open dialog
        windowController.sendText(fileName);
        WaitUtilsStatic.waitMilliseconds(300);
        windowController.sendKey("Enter");
        addVerification("Open File", true, 
            String.format("Opened file with name: %s", fileName));
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I navigate to menu {string}")
    public void i_navigate_to_menu(String menuPath) {
        // Handle menu paths like "File->New->Document"
        String[] menuItems = menuPath.split("->");
        
        for (int i = 0; i < menuItems.length; i++) {
            String menuItem = menuItems[i].trim();
            
            if (i == 0) {
                // First menu item - press Alt to activate menu bar
                windowController.sendKey("Alt");
                WaitUtilsStatic.waitMilliseconds(200);
            }
            
            // Send the first letter of each menu item to navigate
            if (!menuItem.isEmpty()) {
                windowController.sendKey(menuItem.substring(0, 1).toLowerCase());
                WaitUtilsStatic.waitMilliseconds(300);
            }
        }
        
        addVerification("Navigate Menu", true, 
            String.format("Navigated to menu: %s", menuPath));
    }
    
    @When("I press Alt+{string} to access menu")
    public void i_press_alt_key_for_menu(String key) {
        windowController.sendKeyCombo(new String[]{"Alt", key});
        addVerification("Alt Menu Access", true, 
            String.format("Pressed Alt+%s for menu access", key));
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    // =====================================================================================
    // TEXT MANIPULATION AND EDITING AUTOMATION
    // =====================================================================================
    
    @When("I move cursor to beginning of line")
    public void i_move_cursor_to_beginning_of_line() {
        windowController.sendKey("Home");
        addVerification("Cursor Home", true, "Moved cursor to beginning of line");
        WaitUtilsStatic.waitMilliseconds(100);
    }
    
    @When("I move cursor to end of line")
    public void i_move_cursor_to_end_of_line() {
        windowController.sendKey("End");
        addVerification("Cursor End", true, "Moved cursor to end of line");
        WaitUtilsStatic.waitMilliseconds(100);
    }
    
    @When("I move cursor to beginning of document")
    public void i_move_cursor_to_beginning_of_document() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "Home"});
        addVerification("Cursor Document Start", true, "Moved cursor to beginning of document");
        WaitUtilsStatic.waitMilliseconds(200);
    }
    
    @When("I move cursor to end of document")
    public void i_move_cursor_to_end_of_document() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "End"});
        addVerification("Cursor Document End", true, "Moved cursor to end of document");
        WaitUtilsStatic.waitMilliseconds(200);
    }
    
    @When("I select current word")
    public void i_select_current_word() {
        windowController.sendKeyCombo(new String[]{"Ctrl", "w"});
        addVerification("Select Word", true, "Selected current word");
        WaitUtilsStatic.waitMilliseconds(200);
    }
    
    @When("I select current line")
    public void i_select_current_line() {
        windowController.sendKey("Home");
        WaitUtilsStatic.waitMilliseconds(100);
        windowController.sendKeyCombo(new String[]{"Shift", "End"});
        addVerification("Select Line", true, "Selected current line");
        WaitUtilsStatic.waitMilliseconds(200);
    }
    
    @When("I move cursor {string} by {int} characters")
    public void i_move_cursor_by_characters(String direction, int count) {
        String key = direction.toLowerCase().equals("left") ? "Left" : 
                    direction.toLowerCase().equals("right") ? "Right" :
                    direction.toLowerCase().equals("up") ? "Up" : "Down";
        
        for (int i = 0; i < count; i++) {
            windowController.sendKey(key);
            WaitUtilsStatic.waitMilliseconds(50);
        }
        
        addVerification("Move Cursor", true, 
            String.format("Moved cursor %s by %d characters", direction, count));
    }
    
    @When("I find and replace {string} with {string}")
    public void i_find_and_replace_text(String findText, String replaceText) {
        windowController.sendKeyCombo(new String[]{"Ctrl", "h"});
        WaitUtilsStatic.waitMilliseconds(500);
        windowController.sendText(findText);
        windowController.sendKey("Tab");
        WaitUtilsStatic.waitMilliseconds(200);
        windowController.sendText(replaceText);
        windowController.sendKey("Enter");
        
        addVerification("Find Replace", true, 
            String.format("Find and replace '%s' with '%s'", findText, replaceText));
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    // =====================================================================================
    // WINDOW AND APPLICATION CONTROL AUTOMATION
    // =====================================================================================
    
    @When("I switch to next window")
    public void i_switch_to_next_window() {
        windowController.sendKeyCombo(new String[]{"Alt", "Tab"});
        addVerification("Switch Window", true, "Switched to next window");
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I minimize current window")
    public void i_minimize_current_window() {
        windowController.sendKeyCombo(new String[]{"Alt", "F9"});
        addVerification("Minimize Window", true, "Minimized current window");
        WaitUtilsStatic.waitMilliseconds(300);
    }
    
    @When("I maximize current window")
    public void i_maximize_current_window() {
        windowController.sendKeyCombo(new String[]{"Alt", "F10"});
        addVerification("Maximize Window", true, "Maximized current window");
        WaitUtilsStatic.waitMilliseconds(300);
    }    @When("I close current window")
    public void i_close_current_window() {
        windowController.sendKeyCombo(new String[]{"Alt", "F4"});
        addVerification("Close Window", true, "Closed current window");
        WaitUtilsStatic.waitMilliseconds(500);
    }
    
    @When("I take screenshot with description {string}")
    public void i_take_screenshot_with_description(String description) {
        captureScreenshot(description.replaceAll("[^a-zA-Z0-9]", "_"));
        addVerification("Screenshot", true, 
            String.format("Captured screenshot: %s", description));
    }
    
    // =====================================================================================
    // ADDITIONAL INPUT STEPS FOR APPLICATION AUTOMATION
    // =====================================================================================
    
    // =====================================================================================
    // MISSING INPUT STEP DEFINITIONS - COMMONLY USED
    // =====================================================================================
    @When("I type text {string}")
    public void i_type_text(String text) {
        String interpolatedText = VariableManager.interpolate(text);
        logger.info("Typing text: '{}'", interpolatedText);
        
        try {
            windowController.sendText(interpolatedText);
            addVerification("Type Text", true, 
                String.format("Successfully typed text: '%s'", interpolatedText));
            WaitUtilsStatic.waitMilliseconds(300);
        } catch (Exception e) {
            logger.error("Failed to type text '{}': {}", interpolatedText, e.getMessage(), e);
            addVerification("Type Text", false, 
                String.format("Failed to type text '%s': %s", interpolatedText, e.getMessage()));
            throw new RuntimeException("Failed to type text: " + interpolatedText, e);
        }
    }
    
    @When("I press {string} key")
    public void i_press_key_enhanced(String keyName) {
        String interpolatedKey = VariableManager.interpolate(keyName);
        logger.info("Pressing key: '{}'", interpolatedKey);
        
        try {
            windowController.sendKey(interpolatedKey);
            addVerification("Press Key", true, 
                String.format("Successfully pressed key: '%s'", interpolatedKey));
            WaitUtilsStatic.waitMilliseconds(300);
        } catch (Exception e) {
            logger.error("Failed to press key '{}': {}", interpolatedKey, e.getMessage(), e);
            addVerification("Press Key", false, 
                String.format("Failed to press key '%s': %s", interpolatedKey, e.getMessage()));
            throw new RuntimeException("Failed to press key: " + interpolatedKey, e);
        }
    }
    
    @When("I press {string} key combination")
    public void i_press_key_combination_enhanced(String keyCombo) {
        String interpolatedCombo = VariableManager.interpolate(keyCombo);
        logger.info("Pressing key combination: '{}'", interpolatedCombo);
        
        try {
            String[] keys = interpolatedCombo.split("\\+");
            windowController.sendKeyCombo(keys);
            addVerification("Press Key Combination", true, 
                String.format("Successfully pressed key combination: '%s'", interpolatedCombo));
            WaitUtilsStatic.waitMilliseconds(500);
        } catch (Exception e) {
            logger.error("Failed to press key combination '{}': {}", interpolatedCombo, e.getMessage(), e);        addVerification("Press Key Combination", false, 
                String.format("Failed to press key combination '%s': %s", interpolatedCombo, e.getMessage()));
            throw new RuntimeException("Failed to press key combination: " + interpolatedCombo, e);
        }
    }

    @When("I press {string} key combination {int} times")
    public void i_press_key_combination_times(String keyCombo, int times) {
        String interpolatedCombo = VariableManager.interpolate(keyCombo);
        logger.info("Pressing key combination '{}' {} times", interpolatedCombo, times);
        
        try {
            String[] keys = interpolatedCombo.split("\\+");
            for (int i = 0; i < times; i++) {
                windowController.sendKeyCombo(keys);
                WaitUtilsStatic.waitMilliseconds(100);
            }
            addVerification("Press Key Combination Multiple Times", true, 
                String.format("Successfully pressed key combination '%s' %d times", interpolatedCombo, times));
        } catch (Exception e) {
            logger.error("Failed to press key combination '{}': {}", interpolatedCombo, e.getMessage(), e);
            addVerification("Press Key Combination Multiple Times", false, 
                String.format("Failed to press key combination '%s' %d times: %s", interpolatedCombo, times, e.getMessage()));
            throw new RuntimeException("Failed to press key combination multiple times: " + interpolatedCombo, e);
        }
    }

    
    // =====================================================================================
    // COORDINATE-BASED INTERACTIONS
    // =====================================================================================
    
    @When("I click at coordinates extracted from text {string}")
    public void i_click_at_coordinates_extracted_from_text(String textWithCoordinates) {
        try {
            // Extract coordinates from text (expecting format like "Click at (100, 200)")
            String pattern = "\\((\\d+),\\s*(\\d+)\\)";
            java.util.regex.Pattern regexPattern = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = regexPattern.matcher(textWithCoordinates);
            
            if (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                
                windowController.mouseClick(x, y);
                addVerification("Click Extracted Coordinates", true, 
                    String.format("Clicked at extracted coordinates (%d, %d)", x, y));
                WaitUtilsStatic.waitMilliseconds(300);
            } else {
                throw new IllegalArgumentException("No valid coordinates found in text: " + textWithCoordinates);
            }
        } catch (Exception e) {
            addVerification("Click Extracted Coordinates", false, 
                String.format("Failed to extract and click coordinates from '%s': %s", textWithCoordinates, e.getMessage()));
            throw new RuntimeException("Failed to extract coordinates from text: " + textWithCoordinates, e);
        }
    }
    
    // =====================================================================================
    // DRAWING OPERATIONS FOR PAINT APPLICATION
    // =====================================================================================    
    @When("I draw line from coordinates \\({int}, {int}) to \\({int}, {int})")
    public void i_draw_line_from_coordinates_to(int x1, int y1, int x2, int y2) {
        try {
            // Mouse press at start position, move to end position, then release
            windowController.mouseClick(x1, y1);
            WaitUtilsStatic.waitMilliseconds(100);
            windowController.dragAndDrop(x1, y1, x2, y2);
            
            addVerification("Draw Line", true, 
                String.format("Drew line from (%d, %d) to (%d, %d)", x1, y1, x2, y2));
            WaitUtilsStatic.waitMilliseconds(300);
        } catch (Exception e) {
            addVerification("Draw Line", false, 
                String.format("Failed to draw line: %s", e.getMessage()));
            throw new RuntimeException("Failed to draw line", e);
        }
    }
    
    @When("I draw rectangle from coordinates \\({int}, {int}) to \\({int}, {int})")
    public void i_draw_rectangle_from_coordinates_to(int x1, int y1, int x2, int y2) {
        try {
            // Use drag and drop for rectangle drawing
            windowController.dragAndDrop(x1, y1, x2, y2);
            
            addVerification("Draw Rectangle", true, 
                String.format("Drew rectangle from (%d, %d) to (%d, %d)", x1, y1, x2, y2));
            WaitUtilsStatic.waitMilliseconds(300);
        } catch (Exception e) {
            addVerification("Draw Rectangle", false, 
                String.format("Failed to draw rectangle: %s", e.getMessage()));
            throw new RuntimeException("Failed to draw rectangle", e);
        }
    }
    
    @When("I draw ellipse from coordinates \\({int}, {int}) to \\({int}, {int})")
    public void i_draw_ellipse_from_coordinates_to(int x1, int y1, int x2, int y2) {
        try {
            // Use drag and drop for ellipse drawing  
            windowController.dragAndDrop(x1, y1, x2, y2);
            
            addVerification("Draw Ellipse", true, 
                String.format("Drew ellipse from (%d, %d) to (%d, %d)", x1, y1, x2, y2));
            WaitUtilsStatic.waitMilliseconds(300);
        } catch (Exception e) {
            addVerification("Draw Ellipse", false, 
                String.format("Failed to draw ellipse: %s", e.getMessage()));
            throw new RuntimeException("Failed to draw ellipse", e);
        }
    }
    
    // =====================================================================================
    // TOOLBAR AND UI INTERACTIONS
    // =====================================================================================
    
    @When("I click on tool {string} in toolbar")
    public void i_click_on_tool_in_toolbar(String toolName) {
        try {
            // Try to find and click the tool by OCR text recognition
            java.io.File screenshot = screenCapture.captureScreen();
            String screenText = ocrEngine.extractText(screenshot);
            
            if (screenText.toLowerCase().contains(toolName.toLowerCase())) {
                // Use image matching to find the tool
                String imagePath = String.format("toolbar_%s.png", toolName.toLowerCase().replace(" ", "_"));
                
                java.io.File toolImage = new java.io.File(getImagePath(imagePath));
                if (toolImage.exists()) {
                    java.awt.Rectangle match = imageMatcher.findImage(screenshot, toolImage);
                    if (match != null) {
                        int clickX = match.x + match.width / 2;
                        int clickY = match.y + match.height / 2;
                        windowController.mouseClick(clickX, clickY);
                        
                        addVerification("Click Toolbar Tool", true, 
                            String.format("Clicked on tool '%s' in toolbar", toolName));
                        WaitUtilsStatic.waitMilliseconds(300);
                        return;
                    }
                }
            }
            
            // Fallback: Generic toolbar click based on tool name
            addVerification("Click Toolbar Tool", false, 
                String.format("Tool '%s' not found in toolbar", toolName));
            throw new RuntimeException("Tool not found in toolbar: " + toolName);
            
        } catch (Exception e) {
            addVerification("Click Toolbar Tool", false, 
                String.format("Failed to click toolbar tool '%s': %s", toolName, e.getMessage()));
            throw new RuntimeException("Failed to click toolbar tool: " + toolName, e);
        }
    }
    
    @When("I scroll down to find {string}")
    public void i_scroll_down_to_find(String targetText) {
        try {
            String interpolatedText = com.automation.utils.VariableManager.interpolate(targetText);
            boolean found = false;
            int maxScrolls = 10;
            
            for (int i = 0; i < maxScrolls && !found; i++) {
                // Check if text is visible
                java.io.File screenshot = screenCapture.captureScreen();
                String screenText = ocrEngine.extractText(screenshot);
                
                if (screenText.toLowerCase().contains(interpolatedText.toLowerCase())) {
                    found = true;
                    break;
                }
                
                // Scroll down
                windowController.sendKey("PAGE_DOWN");
                WaitUtilsStatic.waitMilliseconds(500);
            }
            
            if (found) {
                addVerification("Scroll to Find Text", true, 
                    String.format("Found text '%s' after scrolling", interpolatedText));
            } else {
                addVerification("Scroll to Find Text", false, 
                    String.format("Text '%s' not found after %d scroll attempts", interpolatedText, maxScrolls));
                throw new RuntimeException("Text not found after scrolling: " + interpolatedText);
            }
            
        } catch (Exception e) {
            addVerification("Scroll to Find Text", false, 
                String.format("Failed to scroll and find text '%s': %s", targetText, e.getMessage()));
            throw new RuntimeException("Failed to scroll and find text: " + targetText, e);
        }
    }
    
    // =====================================================================================
    // MANAGED APPLICATION CONTEXT INPUT OPERATIONS
    // =====================================================================================      @When("I type {string} in managed application {string}")
    public void i_type_in_managed_application(String text, String managedAppName) {
        logger.info("🎯 ENTERPRISE INPUT: Type '{}' in application '{}'", text, managedAppName);
        
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window for input operation");
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                // Special handling for console applications like CMD
                if (isConsoleApplication(context.getExecutablePath())) {
                    logger.warn("⚠️ CMD FOCUS FAILED: Proceeding with console fallback input method for '{}'", managedAppName);
                    // Try alternative approach: brief delay and send to foreground window
                    Thread.sleep(500);
                } else {
                    throw new RuntimeException("❌ Failed to focus enterprise application: " + managedAppName);
                }
            }
            
            windowController.sendKeys(text);
            
            addVerification("Type in Managed Application", true, 
                String.format("✅ Successfully typed '%s' in enterprise application '%s'", text, managedAppName));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to type '%s' in enterprise application '%s': %s", 
                text, managedAppName, e.getMessage());
            addVerification("Type in Managed Application", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
      @When("I type {string} in managed application {string} window {int}")
    public void i_type_in_managed_application_window(String text, String managedAppName, int windowIndex) {
        logger.info("🎯 ENTERPRISE INPUT: Type '{}' in application '{}' window {}", text, managedAppName, windowIndex);
        
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window {} for input operation", windowIndex);
            boolean focused = windowController.focusWindowByIndex(context, windowIndex);
            if (!focused) {
                throw new RuntimeException("❌ Failed to focus enterprise application window: " + managedAppName + " window " + windowIndex);
            }
            
            windowController.sendKeys(text);
            
            addVerification("Type in Managed Application Window", true, 
                String.format("✅ Successfully typed '%s' in enterprise application '%s' window %d", text, managedAppName, windowIndex));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to type '%s' in enterprise application '%s' window %d: %s", 
                text, managedAppName, windowIndex, e.getMessage());
            addVerification("Type in Managed Application Window", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }      @When("I press {string} key in managed application {string}")
    public void i_press_key_in_managed_application(String key, String managedAppName) {
        logger.info("🎯 ENTERPRISE INPUT: Press '{}' key in application '{}'", key, managedAppName);
        
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window for key press operation");
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                // Special handling for console applications like CMD
                if (isConsoleApplication(context.getExecutablePath())) {
                    logger.warn("⚠️ CMD FOCUS FAILED: Proceeding with console fallback input method for key '{}' in '{}'", key, managedAppName);
                    // Try alternative approach: brief delay and send to foreground window
                    Thread.sleep(500);
                } else {
                    throw new RuntimeException("❌ Failed to focus enterprise application: " + managedAppName);
                }
            }
              windowController.sendKey(key);
            
            addVerification("Press Key in Managed Application", true, 
                String.format("✅ Successfully pressed '%s' key in enterprise application '%s'", key, managedAppName));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to press '%s' key in enterprise application '%s': %s", 
                key, managedAppName, e.getMessage());
            addVerification("Press Key in Managed Application", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
      @When("I press {string} key in managed application {string} window {int}")
    public void i_press_key_in_managed_application_window(String key, String managedAppName, int windowIndex) {
        logger.info("🎯 ENTERPRISE INPUT: Press '{}' key in application '{}' window {}", key, managedAppName, windowIndex);
        
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window {} for key press operation", windowIndex);
            boolean focused = windowController.focusWindowByIndex(context, windowIndex);
            if (!focused) {
                throw new RuntimeException("❌ Failed to focus enterprise application window: " + managedAppName + " window " + windowIndex);
            }
            
            windowController.sendKey(key);
            
            addVerification("Press Key in Managed Application Window", true, 
                String.format("✅ Successfully pressed '%s' key in enterprise application '%s' window %d", key, managedAppName, windowIndex));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to press '%s' key in enterprise application '%s' window %d: %s", 
                key, managedAppName, windowIndex, e.getMessage());
            addVerification("Press Key in Managed Application Window", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
      @When("I press {string} key combination in managed application {string}")
    public void i_press_key_combination_in_managed_application(String keyCombo, String managedAppName) {
        logger.info("🎯 ENTERPRISE INPUT: Press '{}' key combination in application '{}'", keyCombo, managedAppName);
          try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window for key combination operation");
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                // Special handling for console applications like CMD
                if (isConsoleApplication(context.getExecutablePath())) {
                    logger.warn("⚠️ CMD FOCUS FAILED: Proceeding with console fallback input method for key combo '{}' in '{}'", keyCombo, managedAppName);
                    // Try alternative approach: brief delay and send to foreground window
                    Thread.sleep(500);
                } else {
                    throw new RuntimeException("❌ Failed to focus enterprise application: " + managedAppName);
                }
            }
            
            windowController.sendKeyCombination(keyCombo);
            
            addVerification("Press Key Combination in Managed Application", true, 
                String.format("✅ Successfully pressed '%s' key combination in enterprise application '%s'", keyCombo, managedAppName));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to press '%s' key combination in enterprise application '%s': %s", 
                keyCombo, managedAppName, e.getMessage());
            addVerification("Press Key Combination in Managed Application", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
      @When("I press {string} key combination in managed application {string} window {int}")
    public void i_press_key_combination_in_managed_application_window(String keyCombo, String managedAppName, int windowIndex) {
        logger.info("🎯 ENTERPRISE INPUT: Press '{}' key combination in application '{}' window {}", keyCombo, managedAppName, windowIndex);
        
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window {} for key combination operation", windowIndex);
            boolean focused = windowController.focusWindowByIndex(context, windowIndex);
            if (!focused) {
                throw new RuntimeException("❌ Failed to focus enterprise application window: " + managedAppName + " window " + windowIndex);
            }
            
            windowController.sendKeyCombination(keyCombo);
            
            addVerification("Press Key Combination in Managed Application Window", true, 
                String.format("✅ Successfully pressed '%s' key combination in enterprise application '%s' window %d", keyCombo, managedAppName, windowIndex));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to press '%s' key combination in enterprise application '%s' window %d: %s", 
                keyCombo, managedAppName, windowIndex, e.getMessage());
            addVerification("Press Key Combination in Managed Application Window", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
      @When("I click at coordinates {int}, {int} in managed application {string}")
    public void i_click_at_coordinates_in_managed_application(int x, int y, String managedAppName) {
        logger.info("🎯 ENTERPRISE INPUT: Click at coordinates ({}, {}) in application '{}'", x, y, managedAppName);
        
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window for coordinate click operation");
            boolean focused = windowController.focusWindow(context);
            if (!focused) {
                throw new RuntimeException("❌ Failed to focus enterprise application: " + managedAppName);
            }
            
            windowController.clickAt(x, y);
            
            addVerification("Click Coordinates in Managed Application", true, 
                String.format("✅ Successfully clicked at coordinates (%d, %d) in enterprise application '%s'", x, y, managedAppName));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to click at coordinates (%d, %d) in enterprise application '%s': %s", 
                x, y, managedAppName, e.getMessage());
            addVerification("Click Coordinates in Managed Application", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
      @When("I click at coordinates {int}, {int} in managed application {string} window {int}")
    public void i_click_at_coordinates_in_managed_application_window(int x, int y, String managedAppName, int windowIndex) {
        logger.info("🎯 ENTERPRISE INPUT: Click at coordinates ({}, {}) in application '{}' window {}", x, y, managedAppName, windowIndex);
        
        try {
            ManagedApplicationContext context = ProcessManager.getInstance().getRunningApplicationContext(managedAppName);
            if (context == null) {
                throw new RuntimeException("🚫 Enterprise application context not found: " + managedAppName);
            }
            
            logger.info("🚀 Focusing enterprise application window {} for coordinate click operation", windowIndex);
            boolean focused = windowController.focusWindowByIndex(context, windowIndex);
            if (!focused) {
                throw new RuntimeException("❌ Failed to focus enterprise application window: " + managedAppName + " window " + windowIndex);
            }
            
            windowController.clickAt(x, y);
              addVerification("Click Coordinates in Managed Application Window", true, 
                String.format("✅ Successfully clicked at coordinates (%d, %d) in enterprise application '%s' window %d", x, y, managedAppName, windowIndex));
            
        } catch (Exception e) {
            String errorMsg = String.format("❌ Failed to click at coordinates (%d, %d) in enterprise application '%s' window %d: %s", 
                x, y, managedAppName, windowIndex, e.getMessage());
            addVerification("Click Coordinates in Managed Application Window", false, errorMsg);
            logger.error("❌ ENTERPRISE INPUT FAILED: {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    // =====================================================================================
    // HELPER METHODS
    // =====================================================================================
    
    /**
     * Check if the given process name is a console application
     */
    private boolean isConsoleApplication(String processName) {
        if (processName == null) return false;
        
        String[] consoleApps = {
            "cmd.exe", "powershell.exe", "pwsh.exe", "bash.exe", 
            "python.exe", "node.exe", "java.exe", "ping.exe",
            "telnet.exe", "ftp.exe", "ssh.exe"
        };
        
        String lowerProcessName = processName.toLowerCase();
        for (String consoleApp : consoleApps) {
            if (lowerProcessName.equals(consoleApp)) {
                return true;
            }
        }
        return false;
    }
}
