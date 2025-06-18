package com.automation.core;

import com.automation.core.win32.Win32WindowControl;
import com.automation.models.ManagedApplicationContext;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ARCHITECTURE: Step definition compatibility layer with Win32 integration
 */
public class WindowController {
    private static final Logger logger = LoggerFactory.getLogger(WindowController.class);
    
    // Singleton pattern for enterprise compatibility
    private static volatile WindowController instance;
    
    // Pre-allocated instances to avoid heap allocation
    private final Robot robot;
    private volatile WinDef.HWND currentWindow;
    
    // Simple control flow - Constructor with basic initialization
    public WindowController() {
        try {
            this.robot = new Robot();
            this.robot.setAutoDelay(50); // Fixed delay bound
        } catch (AWTException e) {
            logger.error("Failed to initialize Robot for automation", e);
            throw new RuntimeException("Failed to initialize Robot", e);
        }
    }
    
    // Singleton getInstance method for enterprise compatibility
    public static WindowController getInstance() {
        if (instance == null) {
            synchronized (WindowController.class) {
                if (instance == null) {
                    instance = new WindowController();
                }
            }
        }
        return instance;
    }
    
    /**
     * PID-DRIVEN: Set current window by PID
     */
    public boolean setCurrentWindowByPID(int pid) {
        logger.debug("🎯 Setting current window by PID: {}", pid);
        
        WinDef.HWND window = findMainWindowByPID(pid);
        if (window != null) {
            currentWindow = window;
            String windowTitle = getWindowTitle(window);
            logger.debug("✅ Current window set for PID {}: '{}'", pid, windowTitle);
            return true;
        }
        
        logger.warn("⚠️ No window found for PID: {}", pid);
        return false;
    }
    
    /**
     * LEGACY - Find window by title (DEPRECATED - kept for backward compatibility only)
     * @deprecated Use focusWindow(ProcessInfo) instead for PID-driven reliability
     */
    @Deprecated
    public boolean findWindowByTitle(String title) {
        return findWindowByTitle(title, true);
    }
    
    /**
     * LEGACY - Find window by title with option for partial match (DEPRECATED)
     * @deprecated Use focusWindow(ProcessInfo) instead for PID-driven reliability
     */
    @Deprecated
    public boolean findWindowByTitle(String title, boolean exactMatch) {
        logger.warn("⚠️ DEPRECATED: Using unreliable title-based window detection: '{}'", title);
        logger.warn("⚠️ RECOMMENDATION: Use focusWindow(ProcessInfo) for PID-driven reliability");
        
        WindowFinder finder = new WindowFinder(title, exactMatch);
        User32.INSTANCE.EnumWindows(finder, null);
        
        if (finder.getFoundWindow() != null) {
            currentWindow = finder.getFoundWindow();
            logger.info("Window found: {}", title);
            return true;
        }
        
        logger.warn("Window not found: {}", title);
        return false;
    }
    
    /**
     * Find window by class name
     */
    public boolean findWindowByClassName(String className) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(className, null);
        if (hwnd != null) {
            currentWindow = hwnd;
            logger.info("Window found by class name: {}", className);
            return true;
        }
        
        logger.warn("Window not found by class name: {}", className);
        return false;
    }    /**
     * Activate the current window (bring to front)
     */
    public boolean activateWindow() {
        if (currentWindow == null) {
            logger.error("No window selected to activate");
            return false;
        }
        
        try {
            logger.info("🎯 AGGRESSIVE WINDOW ACTIVATION: Forcing window to foreground");
            
            // Step 1: Show window and ensure it's not minimized
            User32.INSTANCE.ShowWindow(currentWindow, WinUser.SW_RESTORE);
            Thread.sleep(200);
            User32.INSTANCE.ShowWindow(currentWindow, WinUser.SW_SHOW);
            Thread.sleep(200);
            
            // Step 2: Multiple attempts at bringing to top
            for (int i = 0; i < 3; i++) {
                User32.INSTANCE.BringWindowToTop(currentWindow);
                Thread.sleep(100);
            }
            
            // Step 3: Force set as foreground window with multiple attempts
            for (int i = 0; i < 3; i++) {
                User32.INSTANCE.SetForegroundWindow(currentWindow);
                Thread.sleep(150);
                
                // Check if it worked
                WinDef.HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();
                if (foregroundWindow != null && foregroundWindow.equals(currentWindow)) {
                    logger.info("✅ FOREGROUND SUCCESS: Window is now in foreground");
                    break;
                }
            }
            
            // Step 4: Use SetWindowPos to force topmost temporarily
            User32.INSTANCE.SetWindowPos(currentWindow, 
                new WinDef.HWND(Pointer.createConstant(-1)), // HWND_TOPMOST
                0, 0, 0, 0, 
                0x0001 | 0x0002 | 0x0040); // SWP_NOSIZE | SWP_NOMOVE | SWP_SHOWWINDOW
            Thread.sleep(100);
            
            // Step 5: Remove topmost flag but keep it on top
            User32.INSTANCE.SetWindowPos(currentWindow, 
                new WinDef.HWND(Pointer.createConstant(-2)), // HWND_NOTOPMOST  
                0, 0, 0, 0, 
                0x0001 | 0x0002 | 0x0040); // SWP_NOSIZE | SWP_NOMOVE | SWP_SHOWWINDOW
            Thread.sleep(200);
            
            // Step 6: Final verification
            WinDef.HWND finalForeground = User32.INSTANCE.GetForegroundWindow();
            if (finalForeground != null && finalForeground.equals(currentWindow)) {
                logger.info("✅ ACTIVATION SUCCESS: Window is actively in foreground");
            } else {
                logger.warn("⚠️ ACTIVATION PARTIAL: Window activated but may not be in foreground");
            }
            
            logger.info("Window activation completed");
            return true;
        } catch (Exception e) {
            logger.error("Failed to activate window", e);
            return false;
        }
    }
    
    /**
     * Get window bounds
     */
    public Rectangle getWindowBounds() {
        if (currentWindow == null) {
            return null;
        }
        
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(currentWindow, rect);
        
        return new Rectangle(
            rect.left,
            rect.top,
            rect.right - rect.left,
            rect.bottom - rect.top
        );
    }
    
    /**
     * Send text to the current window
     */
    public void sendText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        logger.debug("Sending text: {}", text);
        
        // Ensure window is active
        activateWindow();
        
        // Send each character
        for (char c : text.toCharArray()) {
            sendCharacter(c);
            robot.delay(10); // Small delay between characters
        }
    }
      /**
     * Send a single character
     */
    private void sendCharacter(char c) {
        // Handle special characters that require specific key codes
        int keyCode = getCharacterKeyCode(c);
        if (keyCode == -1) {
            logger.warn("Unable to send character: {}", c);
            return;
        }
        
        if (Character.isUpperCase(c) || isShiftRequired(c)) {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } else {
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }
    }
    
    /**
     * Get the key code for a character
     */
    private int getCharacterKeyCode(char c) {
        switch (c) {
            // Numbers
            case '0': return KeyEvent.VK_0;
            case '1': return KeyEvent.VK_1;
            case '2': return KeyEvent.VK_2;
            case '3': return KeyEvent.VK_3;
            case '4': return KeyEvent.VK_4;
            case '5': return KeyEvent.VK_5;
            case '6': return KeyEvent.VK_6;
            case '7': return KeyEvent.VK_7;
            case '8': return KeyEvent.VK_8;
            case '9': return KeyEvent.VK_9;
            
            // Letters (use uppercase)
            case 'a': case 'A': return KeyEvent.VK_A;
            case 'b': case 'B': return KeyEvent.VK_B;
            case 'c': case 'C': return KeyEvent.VK_C;
            case 'd': case 'D': return KeyEvent.VK_D;
            case 'e': case 'E': return KeyEvent.VK_E;
            case 'f': case 'F': return KeyEvent.VK_F;
            case 'g': case 'G': return KeyEvent.VK_G;
            case 'h': case 'H': return KeyEvent.VK_H;
            case 'i': case 'I': return KeyEvent.VK_I;
            case 'j': case 'J': return KeyEvent.VK_J;
            case 'k': case 'K': return KeyEvent.VK_K;
            case 'l': case 'L': return KeyEvent.VK_L;
            case 'm': case 'M': return KeyEvent.VK_M;
            case 'n': case 'N': return KeyEvent.VK_N;
            case 'o': case 'O': return KeyEvent.VK_O;
            case 'p': case 'P': return KeyEvent.VK_P;
            case 'q': case 'Q': return KeyEvent.VK_Q;
            case 'r': case 'R': return KeyEvent.VK_R;
            case 's': case 'S': return KeyEvent.VK_S;
            case 't': case 'T': return KeyEvent.VK_T;
            case 'u': case 'U': return KeyEvent.VK_U;
            case 'v': case 'V': return KeyEvent.VK_V;
            case 'w': case 'W': return KeyEvent.VK_W;
            case 'x': case 'X': return KeyEvent.VK_X;
            case 'y': case 'Y': return KeyEvent.VK_Y;
            case 'z': case 'Z': return KeyEvent.VK_Z;
              // Special characters and symbols
            case ' ': return KeyEvent.VK_SPACE;
            case '+': return KeyEvent.VK_EQUALS; // Plus is Shift+Equals
            case '-': return KeyEvent.VK_MINUS;
            case '*': return KeyEvent.VK_8; // Asterisk is Shift+8
            case '/': return KeyEvent.VK_SLASH;
            case '=': return KeyEvent.VK_EQUALS;
            case '.': return KeyEvent.VK_PERIOD;
            case ',': return KeyEvent.VK_COMMA;
            case ';': return KeyEvent.VK_SEMICOLON;
            case ':': return KeyEvent.VK_SEMICOLON; // Colon is Shift+Semicolon
            case '!': return KeyEvent.VK_1; // Exclamation is Shift+1
            case '?': return KeyEvent.VK_SLASH; // Question is Shift+Slash
            case '@': return KeyEvent.VK_2; // At is Shift+2
            case '#': return KeyEvent.VK_3; // Hash is Shift+3
            case '$': return KeyEvent.VK_4; // Dollar is Shift+4
            case '%': return KeyEvent.VK_5; // Percent is Shift+5
            case '^': return KeyEvent.VK_6; // Caret is Shift+6
            case '&': return KeyEvent.VK_7; // Ampersand is Shift+7
            case '(': return KeyEvent.VK_9; // Left paren is Shift+9
            case ')': return KeyEvent.VK_0; // Right paren is Shift+0
            case '[': return KeyEvent.VK_OPEN_BRACKET;
            case ']': return KeyEvent.VK_CLOSE_BRACKET;
            case '{': return KeyEvent.VK_OPEN_BRACKET; // Left brace is Shift+[
            case '}': return KeyEvent.VK_CLOSE_BRACKET; // Right brace is Shift+]
            case '\\': return KeyEvent.VK_BACK_SLASH;
            case '|': return KeyEvent.VK_BACK_SLASH; // Pipe is Shift+backslash
            case '\'': return KeyEvent.VK_QUOTE;
            case '"': return KeyEvent.VK_QUOTE; // Double quote is Shift+quote
            case '`': return KeyEvent.VK_BACK_QUOTE;
            case '~': return KeyEvent.VK_BACK_QUOTE; // Tilde is Shift+backtick
            case '_': return KeyEvent.VK_MINUS; // Underscore is Shift+minus
            case '<': return KeyEvent.VK_COMMA; // Less than is Shift+comma
            case '>': return KeyEvent.VK_PERIOD; // Greater than is Shift+period
            
            default:
                // For unknown characters, log warning and return -1
                logger.warn("No key code mapping for character: '{}' ({})", c, (int)c);
                return -1;
        }
    }
      /**
     * Check if character requires shift key
     */
    private boolean isShiftRequired(char c) {
        // Characters that require Shift key to produce
        return "!@#$%^&*()_+{}|:\"<>?~".indexOf(c) >= 0;
    }
    
    /**
     * Send special keys (Enter, Tab, Function keys, etc.)
     */
    public void sendKey(String key) {
        logger.debug("Sending key: {}", key);
        
        activateWindow();
        
        int keyCode = getKeyCode(key);
        if (keyCode != -1) {
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } else {
            logger.warn("Unknown key: {}", key);
        }
    }
    
    /**
     * Send key combination (e.g., Ctrl+C, Alt+F4)
     */
    public void sendKeyCombo(String... keys) {
        logger.debug("Sending key combination: {}", String.join("+", keys));
        
        activateWindow();
        
        // Press all keys
        int[] keyCodes = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            keyCodes[i] = getKeyCode(keys[i]);
            if (keyCodes[i] != -1) {
                robot.keyPress(keyCodes[i]);
            }
        }
        
        // Release all keys in reverse order
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keyCodes[i] != -1) {
                robot.keyRelease(keyCodes[i]);
            }
        }
    }
    
    /**
     * Perform mouse click at specified coordinates
     */
    public void mouseClick(int x, int y) {
        logger.debug("Mouse click at ({}, {})", x, y);
        
        robot.mouseMove(x, y);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    /**
     * Perform right mouse click
     */
    public void mouseRightClick(int x, int y) {
        logger.debug("Right mouse click at ({}, {})", x, y);
        
        robot.mouseMove(x, y);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }
    
    /**
     * Perform double click
     */
    public void mouseDoubleClick(int x, int y) {
        logger.debug("Double click at ({}, {})", x, y);
        
        robot.mouseMove(x, y);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(50);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }    /**
     * PID-DRIVEN: Wait for window to appear for managed process (RECOMMENDED)
     */
    public boolean waitForWindow(ManagedApplicationContext processInfo, int timeoutSeconds) {
        logger.info("🎯 PID-DRIVEN WAIT: Waiting for window for PID {} (timeout: {}s)", 
            processInfo.getProcessId(), timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeout) {
            // Try to find window for this PID
            WinDef.HWND window = findMainWindowByPID(processInfo.getProcessId());
            if (window != null) {
                currentWindow = window;
                String windowTitle = getWindowTitle(window);
                logger.info("✅ PID-DRIVEN WAIT SUCCESS: Found window for PID {} - '{}'", 
                    processInfo.getProcessId(), windowTitle);
                return true;
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
          logger.warn("⚠️ PID-DRIVEN WAIT TIMEOUT: No window found for PID {} after {} seconds", 
            processInfo.getProcessId(), timeoutSeconds);
        return false;
    }

    /**
     * Find console window (conhost.exe or Windows Terminal) for a console process
     * Enhanced with better PID tracking and window detection
     */
    private WinDef.HWND findConsoleWindowForProcess(int consolePid) {
        logger.debug("🔍 ENHANCED CMD SEARCH: Looking for console window hosting PID: {}", consolePid);
        
        // Strategy 1: Look for console windows with matching titles containing the process
        EnhancedConsoleWindowFinder finder = new EnhancedConsoleWindowFinder(consolePid);
        User32.INSTANCE.EnumWindows(finder, null);
        
        WinDef.HWND consoleWindow = finder.getConsoleWindow();
        if (consoleWindow != null) {
            logger.info("✅ ENHANCED CMD FOUND: Located specific console host for PID {}", consolePid);
            // Validate that this window is actually the correct CMD window
            if (validateConsoleWindow(consoleWindow, consolePid)) {
                return consoleWindow;
            } else {
                logger.warn("⚠️ VALIDATION FAILED: Console window doesn't match PID {}", consolePid);
            }
        }
          // Strategy 2: Look for recently created CMD windows that might match our PID
        logger.debug("🔍 RECENT CMD SEARCH: Looking for recently created CMD windows for PID {}", consolePid);
        RecentConsoleWindowFinder recentFinder = new RecentConsoleWindowFinder();
        User32.INSTANCE.EnumWindows(recentFinder, null);
        
        consoleWindow = recentFinder.getBestConsoleWindow();
        if (consoleWindow != null) {
            logger.info("✅ RECENT CMD FOUND: Located recent console window for process");
            return consoleWindow;
        }
        
        // Strategy 3: Fallback to any visible console window (least preferred)
        logger.debug("🔍 FALLBACK: Searching for any active CMD console window");
        GeneralConsoleWindowFinder generalFinder = new GeneralConsoleWindowFinder();
        User32.INSTANCE.EnumWindows(generalFinder, null);
        
        consoleWindow = generalFinder.getConsoleWindow();
        if (consoleWindow != null) {
            logger.warn("⚠️ FALLBACK CMD: Using general console window (may not be correct process)");
            return consoleWindow;
        }
        
        logger.error("❌ NO CMD WINDOW FOUND: Unable to locate console window for PID: {}", consolePid);
        return null;
    }
    
    /**
     * Validate that a console window belongs to the specified PID
     */
    private boolean validateConsoleWindow(WinDef.HWND consoleWindow, int expectedPid) {
        try {
            IntByReference windowPid = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(consoleWindow, windowPid);
            
            // For console windows, the PID might be different (conhost.exe vs cmd.exe)
            // So we check if the window title contains elements that suggest it's our CMD
            char[] buffer = new char[1024];
            User32.INSTANCE.GetWindowText(consoleWindow, buffer, 1024);
            String windowTitle = Native.toString(buffer).toLowerCase();
            
            // Check for CMD-specific indicators
            boolean isValidCmd = windowTitle.contains("command prompt") ||
                               windowTitle.contains("cmd") ||
                               windowTitle.contains("c:\\") ||
                               windowTitle.contains("windows\\system32") ||
                               windowTitle.matches(".*[a-z]:\\\\.*>.*");
            
            if (isValidCmd) {
                logger.debug("✅ CMD VALIDATION: Window '{}' appears to be valid CMD", windowTitle);
                return true;
            } else {
                logger.debug("⚠️ CMD VALIDATION: Window '{}' doesn't appear to be CMD", windowTitle);
                return false;
            }
        } catch (Exception e) {
            logger.warn("⚠️ CMD VALIDATION ERROR: Failed to validate console window: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * LEGACY - Wait for window to appear by title (DEPRECATED)
     * @deprecated Use waitForWindow(ProcessInfo, int) instead for PID-driven reliability
     */
    @Deprecated
    public boolean waitForWindow(String title, int timeoutSeconds) {
        logger.warn("⚠️ DEPRECATED: Using unreliable title-based window waiting: '{}'", title);
        logger.warn("⚠️ RECOMMENDATION: Use waitForWindow(ProcessInfo, int) for PID-driven reliability");
        
        logger.info("Waiting for window '{}' for {} seconds", title, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeout) {
            if (findWindowByTitle(title)) {
                return true;
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        logger.warn("Timeout waiting for window: {}", title);
        return false;
    }
    
    /**
     * PROFESSIONAL TIMEOUT HANDLING - Prevents infinite loops
     * Enhanced wait for window with detailed error reporting and suggestions
     */
    public boolean waitForWindowWithErrorHandling(String title, int timeoutSeconds) {
        logger.info("⏳ Waiting for window '{}' for {} seconds...", title, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;
        int attempts = 0;
        
        while (System.currentTimeMillis() - startTime < timeout) {
            attempts++;
            
            if (findWindowByTitle(title)) {
                logger.info("✅ Window '{}' found after {} attempts in {}ms", 
                    title, attempts, System.currentTimeMillis() - startTime);
                return true;
            }
            
            // Log every 5 seconds to show we're not stuck
            if (attempts % 10 == 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                logger.info("⏳ Still waiting for '{}' - {}s elapsed, {} attempts", 
                    title, elapsed / 1000, attempts);
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("❌ Wait interrupted for window: {}", title);
                return false;
            }
        }
        
        // PROFESSIONAL ERROR HANDLING - Provide helpful suggestions
        logger.error("❌ TIMEOUT: Window '{}' not found after {} seconds ({} attempts)", 
            title, timeoutSeconds, attempts);
        
        // Suggest similar windows
        suggestSimilarWindows(title);
        
        return false;
    }
    
    /**
     * Discover and suggest similar window titles to help with debugging
     */
    private void suggestSimilarWindows(String targetTitle) {
        logger.info("🔍 Searching for similar windows...");
        
        SimilarWindowFinder finder = new SimilarWindowFinder(targetTitle);
        User32.INSTANCE.EnumWindows(finder, null);
        
        if (!finder.getSimilarWindows().isEmpty()) {
            logger.info("💡 Found similar windows:");
            for (String similar : finder.getSimilarWindows()) {
                logger.info("   → '{}'", similar);
            }
        } else {
            logger.info("💡 No similar windows found. Current visible windows:");
            logAllVisibleWindows();
        }
    }
    
    /**
     * Log all currently visible windows for debugging
     */
    private void logAllVisibleWindows() {
        AllWindowsFinder finder = new AllWindowsFinder();
        User32.INSTANCE.EnumWindows(finder, null);
        
        if (!finder.getVisibleWindows().isEmpty()) {
            for (String window : finder.getVisibleWindows()) {
                logger.info("   → '{}'", window);
            }
        } else {
            logger.warn("   No visible windows found");
        }
    }
    
    /**
     * Inner class for finding similar window titles
     */
    private static class SimilarWindowFinder implements User32.WNDENUMPROC {
        private final String targetTitle;
        private final java.util.List<String> similarWindows = new java.util.ArrayList<>();
        
        public SimilarWindowFinder(String targetTitle) {
            this.targetTitle = targetTitle.toLowerCase();
        }
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String windowTitle = Native.toString(buffer);
            
            if (User32.INSTANCE.IsWindowVisible(hWnd) && !windowTitle.trim().isEmpty()) {
                String lowerTitle = windowTitle.toLowerCase();
                
                // Check for partial matches or similar words
                if (lowerTitle.contains(targetTitle) || 
                    targetTitle.contains(lowerTitle) ||
                    calculateSimilarity(targetTitle, lowerTitle) > 0.5) {
                    similarWindows.add(windowTitle);
                }
            }
            
            return true; // Continue enumeration
        }
        
        private double calculateSimilarity(String s1, String s2) {
            // Simple similarity calculation
            String[] words1 = s1.split("\\s+");
            String[] words2 = s2.split("\\s+");
            
            int matches = 0;
            for (String word1 : words1) {
                for (String word2 : words2) {
                    if (word1.equals(word2) || word1.contains(word2) || word2.contains(word1)) {
                        matches++;
                        break;
                    }
                }
            }
            
            return (double) matches / Math.max(words1.length, words2.length);
        }
        
        public java.util.List<String> getSimilarWindows() {
            return similarWindows;
        }
    }
    
    /**
     * Inner class for finding all visible windows
     */
    private static class AllWindowsFinder implements User32.WNDENUMPROC {
        private final java.util.List<String> visibleWindows = new java.util.ArrayList<>();
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String windowTitle = Native.toString(buffer);
            
            if (User32.INSTANCE.IsWindowVisible(hWnd) && !windowTitle.trim().isEmpty()) {
                visibleWindows.add(windowTitle);
            }
            
            return true; // Continue enumeration
        }
        
        public java.util.List<String> getVisibleWindows() {
            return visibleWindows;
        }
    }
    
    /**
     * Restore window from minimized/maximized state
     */
    public void restoreWindow() {
        if (currentWindow != null) {
            User32.INSTANCE.ShowWindow(currentWindow, WinUser.SW_RESTORE);
            logger.debug("Window restored");
        }
    }
    
    /**
     * Resize window to specified dimensions
     */
    public void resizeWindow(int width, int height) {
        if (currentWindow != null) {
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(currentWindow, rect);
            
            User32.INSTANCE.SetWindowPos(currentWindow, null, 
                rect.left, rect.top, width, height, 
                WinUser.SWP_NOZORDER | WinUser.SWP_NOACTIVATE);
            
            logger.debug("Window resized to {}x{}", width, height);
        }
    }
    
    /**
     * Move window to specified position
     */
    public void moveWindow(int x, int y) {
        if (currentWindow != null) {
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(currentWindow, rect);
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            
            User32.INSTANCE.SetWindowPos(currentWindow, null, 
                x, y, width, height, 
                WinUser.SWP_NOZORDER | WinUser.SWP_NOACTIVATE);
            
            logger.debug("Window moved to ({}, {})", x, y);
        }
    }
    
    /**
     * Center window on screen
     */
    public void centerWindow() {
        if (currentWindow != null) {
            try {
                // Get screen dimensions
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                // Get window dimensions
                WinDef.RECT rect = new WinDef.RECT();
                User32.INSTANCE.GetWindowRect(currentWindow, rect);
                int width = rect.right - rect.left;
                int height = rect.bottom - rect.top;
                
                // Calculate center position
                int x = (screenSize.width - width) / 2;
                int y = (screenSize.height - height) / 2;
                
                // Move window to center
                User32.INSTANCE.SetWindowPos(currentWindow, null, 
                    x, y, width, height, 
                    WinUser.SWP_NOZORDER | WinUser.SWP_NOACTIVATE);
                
                logger.debug("Window centered on screen");
            } catch (Exception e) {
                logger.error("Failed to center window", e);
            }
        }
    }
    
    /**
     * Bring window to foreground
     */
    public void bringToForeground() {
        if (currentWindow != null) {
            User32.INSTANCE.SetForegroundWindow(currentWindow);
            User32.INSTANCE.BringWindowToTop(currentWindow);
            logger.debug("Window brought to foreground");
        }
    }
    
    /**
     * Send window to background
     */
    public void sendToBackground() {
        if (currentWindow != null) {
            User32.INSTANCE.SetWindowPos(currentWindow, new WinDef.HWND(new Pointer(1)), 
                0, 0, 0, 0, 
                WinUser.SWP_NOMOVE | WinUser.SWP_NOZORDER | WinUser.SWP_NOACTIVATE);
            logger.debug("Window sent to background");
        }
    }
    
    /**
     * Find window by partial title match
     */
    public boolean findWindowByPartialTitle(String partialTitle, boolean caseSensitive) {
        logger.info("Searching for window containing title: {}", partialTitle);
        
        PartialWindowFinder finder = new PartialWindowFinder(partialTitle, caseSensitive);
        User32.INSTANCE.EnumWindows(finder, null);
        
        if (finder.getFoundWindow() != null) {
            currentWindow = finder.getFoundWindow();
            logger.info("Window found containing title: {}", partialTitle);
            return true;
        }
        
        logger.warn("Window not found containing title: {}", partialTitle);
        return false;
    }
    
    /**
     * Set window bounds (position and size)
     */
    public void setWindowBounds(Rectangle bounds) {
        if (currentWindow != null && bounds != null) {
            User32.INSTANCE.SetWindowPos(currentWindow, null, 
                bounds.x, bounds.y, bounds.width, bounds.height, 
                WinUser.SWP_NOZORDER | WinUser.SWP_NOACTIVATE);
            
            logger.debug("Window bounds set to: x={}, y={}, width={}, height={}", 
                bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
    
    /**
     * Get current window handle
     */
    public WinDef.HWND getCurrentWindow() {
        return currentWindow;
    }
      /**
     * Set current window handle
     */
    public void setCurrentWindow(WinDef.HWND window) {
        this.currentWindow = window;
    }
    
    /**
     * Close current window
     */
    public void closeWindow() {
        if (currentWindow != null) {
            User32.INSTANCE.SendMessage(currentWindow, WinUser.WM_CLOSE, null, null);
            currentWindow = null;
        }
    }
    
    /**
     * Minimize current window
     */
    public void minimizeWindow() {
        if (currentWindow != null) {
            User32.INSTANCE.ShowWindow(currentWindow, WinUser.SW_MINIMIZE);
        }
    }
    
    /**
     * Maximize current window
     */
    public void maximizeWindow() {
        if (currentWindow != null) {
            User32.INSTANCE.ShowWindow(currentWindow, WinUser.SW_MAXIMIZE);
        }
    }
    
    /**
     * Temporarily maximize a managed application window for OCR operations only.
     * This is used specifically for text recognition to improve accuracy.
     * 
     * @param context The managed application context
     * @return The original window state information for restoration
     */
    public WindowStateInfo temporarilyMaximizeForOCR(ManagedApplicationContext context) {
        if (context == null || context.getPrimaryWindow() == null) {
            logger.warn("Cannot maximize window for OCR - no valid window context");
            return null;
        }
        
        WinDef.HWND windowHandle = context.getPrimaryWindow();
          // Get current window placement to restore later
        WinUser.WINDOWPLACEMENT placement = new WinUser.WINDOWPLACEMENT();
        boolean gotPlacement = User32.INSTANCE.GetWindowPlacement(windowHandle, placement).booleanValue();
        
        if (!gotPlacement) {
            logger.warn("Could not get current window placement for OCR maximization");
            return null;
        }
        
        // Store original state
        WindowStateInfo originalState = new WindowStateInfo(placement);        // Maximize the window for better OCR
        boolean maximized = User32.INSTANCE.ShowWindow(windowHandle, WinUser.SW_MAXIMIZE);
        if (maximized) {
            logger.debug("🔍 Temporarily maximized window for OCR: '{}'", context.getManagedApplicationName());
            try {
                Thread.sleep(500); // Allow window to fully maximize (increased from 200ms)
                
                // Force window to be actually maximized by setting position
                User32.INSTANCE.SetWindowPos(windowHandle, null, -8, -8, 2576, 1416, 0x0040); // SWP_SHOWWINDOW
                Thread.sleep(200); // Allow position to take effect
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            logger.warn("Failed to maximize window for OCR");
            return null;
        }
        
        return originalState;
    }
    
    /**
     * Restore window to its original state after OCR operation
     * 
     * @param context The managed application context
     * @param originalState The original window state to restore
     */
    public void restoreWindowAfterOCR(ManagedApplicationContext context, WindowStateInfo originalState) {
        if (context == null || context.getPrimaryWindow() == null || originalState == null) {
            return;
        }
        
        WinDef.HWND windowHandle = context.getPrimaryWindow();
          // Restore original window placement
        boolean restored = User32.INSTANCE.SetWindowPlacement(windowHandle, originalState.getPlacement()).booleanValue();
        if (restored) {
            logger.debug("🔍 Restored window state after OCR: '{}'", context.getManagedApplicationName());
            try {
                Thread.sleep(100); // Allow window to restore
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            logger.warn("Failed to restore window state after OCR");
        }
    }
    
    /**
     * Helper class to store window state information
     */
    public static class WindowStateInfo {
        private final WinUser.WINDOWPLACEMENT placement;
        
        public WindowStateInfo(WinUser.WINDOWPLACEMENT placement) {
            this.placement = new WinUser.WINDOWPLACEMENT();
            this.placement.length = placement.length;
            this.placement.flags = placement.flags;
            this.placement.showCmd = placement.showCmd;
            this.placement.ptMinPosition = placement.ptMinPosition;
            this.placement.ptMaxPosition = placement.ptMaxPosition;
            this.placement.rcNormalPosition = placement.rcNormalPosition;
        }
        
        public WinUser.WINDOWPLACEMENT getPlacement() {
            return placement;
        }
    }

    /**
     * Click at specific coordinates (alias for mouseClick)
     */
    public void clickAt(int x, int y) {
        mouseClick(x, y);
    }
    
    /**
     * Double click at specific coordinates (alias for mouseDoubleClick)
     */
    public void doubleClickAt(int x, int y) {
        mouseDoubleClick(x, y);
    }
    
    /**
     * Right click at specific coordinates (alias for mouseRightClick)
     */
    public void rightClickAt(int x, int y) {
        mouseRightClick(x, y);
    }
    
    /**
     * Hover mouse at specific coordinates
     */
    public void hoverAt(int x, int y) {
        logger.debug("Hovering mouse at ({}, {})", x, y);
        robot.mouseMove(x, y);
        robot.delay(100);
    }
    
    /**
     * Drag and drop from source to target coordinates
     */
    public void dragAndDrop(int sourceX, int sourceY, int targetX, int targetY) {
        logger.debug("Drag and drop from ({}, {}) to ({}, {})", sourceX, sourceY, targetX, targetY);
        
        // Move to source and press
        robot.mouseMove(sourceX, sourceY);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
        
        // Drag to target
        robot.mouseMove(targetX, targetY);
        robot.delay(100);
        
        // Release at target
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
    }
    
    /**
     * Get the title of the currently active window
     */
    public String getActiveWindowTitle() {
        try {
            WinDef.HWND activeWindow = User32.INSTANCE.GetForegroundWindow();
            if (activeWindow != null) {
                char[] buffer = new char[512];
                User32.INSTANCE.GetWindowText(activeWindow, buffer, 512);
                return Native.toString(buffer);
            }
        } catch (Exception e) {
            logger.error("Failed to get active window title", e);
        }
        return "";
    }
    
    /**
     * Check if a window with the given title is available
     */
    public boolean isWindowAvailable(String title) {
        return findWindowByTitle(title, false);
    }
      /**
     * Check if a process is currently running
     */
    public boolean isProcessRunning(String processName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq " + processName, "/FO", "CSV");
            Process process = processBuilder.start();
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(processName.toLowerCase())) {
                    return true;
                }
            }
            
            process.waitFor();
            return false;
        } catch (Exception e) {
            logger.error("Failed to check if process is running: " + processName, e);
            return false;
        }
    }
    
    /**
     * Send keys using string representation
     */
    public void sendKeys(String keys) {
        sendText(keys);
    }
    
    /**
     * Send a key using keycode
     */
    public void sendKey(int keyCode) {
        activateWindow();
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }
    
    /**
     * Focus window by title
     */
    public boolean focusWindow(String title) {
        if (findWindowByTitle(title)) {
            return activateWindow();
        }
        return false;
    }
    
    /**
     * Close an application by process name or window title
     * This method attempts graceful closure first, then force termination if needed
     * @param processName The process name or window title to close
     * @return true if application was closed successfully
     */
    public boolean closeApplication(String processName) {
        try {
            // First try to find window by treating processName as window title
            if (findWindowByTitle(processName)) {
                activateWindow();
                Thread.sleep(500);
                
                // Send Alt+F4 for graceful close
                sendKeyCombo("ALT", "F4");
                Thread.sleep(2000);
                
                // Check if window closed
                if (!findWindowByTitle(processName)) {
                    logger.info("Application closed gracefully: {}", processName);
                    return true;
                }
            }
            
            // If graceful close failed, try to terminate process
            try {
                ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/IM", processName);
                Process process = pb.start();
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    logger.info("Application terminated forcefully: {}", processName);
                    return true;
                } else {
                    logger.warn("Failed to terminate application: {}", processName);
                    return false;
                }
            } catch (Exception e) {
                logger.error("Error terminating application {}: {}", processName, e.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error closing application {}: {}", processName, e.getMessage());
            return false;
        }
    }
    
    /**
     * Inner class for partial window title enumeration
     */
    private static class PartialWindowFinder implements User32.WNDENUMPROC {
        private final String partialTitle;
        private final boolean caseSensitive;
        private WinDef.HWND foundWindow;
        
        public PartialWindowFinder(String partialTitle, boolean caseSensitive) {
            this.partialTitle = partialTitle;
            this.caseSensitive = caseSensitive;
        }
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String windowTitle = Native.toString(buffer);
            
            boolean matches;
            if (caseSensitive) {
                matches = windowTitle.contains(partialTitle);
            } else {
                matches = windowTitle.toLowerCase().contains(partialTitle.toLowerCase());
            }
                
            if (matches && User32.INSTANCE.IsWindowVisible(hWnd)) {
                foundWindow = hWnd;
                return false; // Stop enumeration
            }
            
            return true; // Continue enumeration
        }
        
        public WinDef.HWND getFoundWindow() {
            return foundWindow;
        }
    }
    
    /**
     * Move mouse to specific coordinates without clicking
     */
    public void moveMouse(int x, int y) {
        logger.debug("Moving mouse to ({}, {})", x, y);
        robot.mouseMove(x, y);
        robot.delay(50);
    }
    
    /**
     * Press and hold a key down
     */
    public void keyDown(String key) {
        logger.debug("Pressing key down: {}", key);
        activateWindow();
        
        int keyCode = getKeyCode(key);
        if (keyCode != -1) {
            robot.keyPress(keyCode);
        } else {
            logger.warn("Unknown key for keyDown: {}", key);
        }
    }
    
    /**
     * Release a held key
     */
    public void keyUp(String key) {
        logger.debug("Releasing key: {}", key);
        
        int keyCode = getKeyCode(key);
        if (keyCode != -1) {
            robot.keyRelease(keyCode);
        } else {
            logger.warn("Unknown key for keyUp: {}", key);
        }
    }
    
    /**
     * Perform middle mouse click at specified coordinates
     */
    public void mouseMiddleClick(int x, int y) {
        logger.debug("Middle mouse click at ({}, {})", x, y);
        
        robot.mouseMove(x, y);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
    }
    
    /**
     * Perform mouse click at current position (no coordinates)
     */
    public void mouseClick() {
        logger.debug("Mouse click at current position");
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    /**
     * Perform right mouse click at current position (no coordinates)
     */
    public void mouseRightClick() {
        logger.debug("Right mouse click at current position");
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }
    
    /**
     * Perform double click at current position (no coordinates)
     */
    public void mouseDoubleClick() {
        logger.debug("Double click at current position");
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(50);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    
    /**
     * Convert key string to KeyEvent constant
     */
    private int getKeyCode(String key) {
        switch (key.toLowerCase()) {
            case "enter": return KeyEvent.VK_ENTER;
            case "tab": return KeyEvent.VK_TAB;
            case "escape": case "esc": return KeyEvent.VK_ESCAPE;
            case "space": return KeyEvent.VK_SPACE;
            case "backspace": return KeyEvent.VK_BACK_SPACE;
            case "delete": return KeyEvent.VK_DELETE;
            case "home": return KeyEvent.VK_HOME;
            case "end": return KeyEvent.VK_END;
            case "page up": case "pageup": return KeyEvent.VK_PAGE_UP;
            case "page down": case "pagedown": return KeyEvent.VK_PAGE_DOWN;
            case "left": return KeyEvent.VK_LEFT;
            case "right": return KeyEvent.VK_RIGHT;
            case "up": return KeyEvent.VK_UP;
            case "down": return KeyEvent.VK_DOWN;
            case "ctrl": return KeyEvent.VK_CONTROL;
            case "alt": return KeyEvent.VK_ALT;
            case "shift": return KeyEvent.VK_SHIFT;
            case "f1": return KeyEvent.VK_F1;
            case "f2": return KeyEvent.VK_F2;
            case "f3": return KeyEvent.VK_F3;
            case "f4": return KeyEvent.VK_F4;
            case "f5": return KeyEvent.VK_F5;
            case "f6": return KeyEvent.VK_F6;
            case "f7": return KeyEvent.VK_F7;
            case "f8": return KeyEvent.VK_F8;
            case "f9": return KeyEvent.VK_F9;            case "f10": return KeyEvent.VK_F10;
            case "f11": return KeyEvent.VK_F11;
            case "f12": return KeyEvent.VK_F12;
            case "+": case "plus": case "add": return KeyEvent.VK_PLUS;
            case "-": case "minus": case "subtract": return KeyEvent.VK_MINUS;
            case "*": case "multiply": case "asterisk": return KeyEvent.VK_MULTIPLY;
            case "/": case "divide": case "slash": return KeyEvent.VK_DIVIDE;
            case "=": case "equals": case "equal": return KeyEvent.VK_EQUALS;
            // Numeric keypad keys
            case "numpad0": return KeyEvent.VK_NUMPAD0;
            case "numpad1": return KeyEvent.VK_NUMPAD1;
            case "numpad2": return KeyEvent.VK_NUMPAD2;
            case "numpad3": return KeyEvent.VK_NUMPAD3;
            case "numpad4": return KeyEvent.VK_NUMPAD4;
            case "numpad5": return KeyEvent.VK_NUMPAD5;
            case "numpad6": return KeyEvent.VK_NUMPAD6;
            case "numpad7": return KeyEvent.VK_NUMPAD7;
            case "numpad8": return KeyEvent.VK_NUMPAD8;
            case "numpad9": return KeyEvent.VK_NUMPAD9;
            case "numpad+": case "numpadplus": return KeyEvent.VK_ADD;
            case "numpad-": case "numpadminus": return KeyEvent.VK_SUBTRACT;
            case "numpad*": case "numpadmultiply": return KeyEvent.VK_MULTIPLY;
            case "numpad/": case "numpaddivide": return KeyEvent.VK_DIVIDE;
            case "numpadenter": return KeyEvent.VK_ENTER;
            case "numpad.": case "numpaddecimal": return KeyEvent.VK_DECIMAL;
            // Number keys 0-9
            case "0": return KeyEvent.VK_0;
            case "1": return KeyEvent.VK_1;
            case "2": return KeyEvent.VK_2;
            case "3": return KeyEvent.VK_3;
            case "4": return KeyEvent.VK_4;
            case "5": return KeyEvent.VK_5;
            case "6": return KeyEvent.VK_6;
            case "7": return KeyEvent.VK_7;            case "8": return KeyEvent.VK_8;
            case "9": return KeyEvent.VK_9;
            // Letter keys A-Z
            case "a": return KeyEvent.VK_A;
            case "b": return KeyEvent.VK_B;
            case "c": return KeyEvent.VK_C;
            case "d": return KeyEvent.VK_D;
            case "e": return KeyEvent.VK_E;
            case "f": return KeyEvent.VK_F;
            case "g": return KeyEvent.VK_G;
            case "h": return KeyEvent.VK_H;
            case "i": return KeyEvent.VK_I;
            case "j": return KeyEvent.VK_J;
            case "k": return KeyEvent.VK_K;
            case "l": return KeyEvent.VK_L;
            case "m": return KeyEvent.VK_M;
            case "n": return KeyEvent.VK_N;
            case "o": return KeyEvent.VK_O;
            case "p": return KeyEvent.VK_P;
            case "q": return KeyEvent.VK_Q;
            case "r": return KeyEvent.VK_R;
            case "s": return KeyEvent.VK_S;
            case "t": return KeyEvent.VK_T;
            case "u": return KeyEvent.VK_U;
            case "v": return KeyEvent.VK_V;
            case "w": return KeyEvent.VK_W;
            case "x": return KeyEvent.VK_X;
            case "y": return KeyEvent.VK_Y;
            case "z": return KeyEvent.VK_Z;
            default:
                // Try to parse as single character for punctuation and special chars
                if (key.length() == 1) {
                    char c = Character.toUpperCase(key.charAt(0));
                    // For letters, we should have explicit mappings above
                    if (Character.isLetter(c)) {
                        logger.warn("Letter key '{}' should have explicit mapping", key);
                        return KeyEvent.getExtendedKeyCodeForChar(c);
                    }
                    // For other characters, try to get the key code
                    try {
                        return KeyEvent.getExtendedKeyCodeForChar(c);
                    } catch (Exception e) {
                        logger.warn("Invalid key character: {}", key);
                        return -1;
                    }
                }
                logger.warn("Unknown key: {}", key);
                return -1;
        }
    }

    /**
     * Inner class for finding windows by title
     */
    private static class WindowFinder implements User32.WNDENUMPROC {
        private final String targetTitle;
        private final boolean exactMatch;
        private WinDef.HWND foundWindow;
        
        public WindowFinder(String targetTitle, boolean exactMatch) {
            this.targetTitle = targetTitle;
            this.exactMatch = exactMatch;
        }
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
            String windowTitle = Native.toString(buffer);
            
            boolean matches;
            if (exactMatch) {
                matches = windowTitle.equals(targetTitle);
            } else {
                matches = windowTitle.toLowerCase().contains(targetTitle.toLowerCase());
            }
                
            if (matches && User32.INSTANCE.IsWindowVisible(hWnd)) {
                foundWindow = hWnd;
                return false; // Stop enumeration
            }
            
            return true; // Continue enumeration
        }
        
        public WinDef.HWND getFoundWindow() {
            return foundWindow;
        }
    }    /**
     * ENTERPRISE CONVERSION: Wait for process and window (now returns ManagedApplicationContext)
     * @param processName The process name (e.g., "msedge.exe", "calc.exe") 
     * @param timeoutSeconds Maximum time to wait
     * @return ManagedApplicationContext object with PID and window handle, or null if failed
     */
    public ManagedApplicationContext waitForProcessAndWindow(String processName, int timeoutSeconds) {
        logger.info("🔍 ENTERPRISE PROCESS-BASED DETECTION: Waiting for process '{}' (timeout: {}s)", processName, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;
        
        // Store initial PIDs to detect new processes
        java.util.Set<Integer> initialPids = new java.util.HashSet<>(getProcessIds(processName));
        logger.debug("Initial PIDs for '{}': {}", processName, initialPids);
        
        // Wait for new process to start or existing process to have window
        while (System.currentTimeMillis() - startTime < timeout) {
            java.util.List<Integer> currentPids = getProcessIds(processName);
            
            if (!currentPids.isEmpty()) {
                logger.debug("Found {} process(es) for '{}'", currentPids.size(), processName);
                
                // Try to find main window for any PID  
                for (Integer pid : currentPids) {
                    WinDef.HWND window = findMainWindowByPID(pid);
                    if (window != null) {
                        currentWindow = window;
                        String windowTitle = getWindowTitle(window);
                        
                        // Create ManagedApplicationContext from PID using ProcessManager
                        ManagedApplicationContext context = ProcessManager.getInstance().createContextFromPID(processName, pid);
                        
                        logger.info("✅ ENTERPRISE SUCCESS: Found window for process '{}' (PID: {}, Title: '{}')", 
                            processName, pid, windowTitle);
                        
                        return context;
                    }
                }
            }
            
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("❌ Wait interrupted for process: {}", processName);
                return null;
            }
        }
        
        logger.error("❌ TIMEOUT: No window found for process '{}' after {} seconds", processName, timeoutSeconds);
        return null;
    }
      /**
     * LEGACY METHOD - kept for backward compatibility but deprecated
     * @deprecated Use waitForProcessAndWindow() instead for better reliability
     */
    @Deprecated
    public boolean waitForProcessAndWindowLegacy(String processName, int timeoutSeconds) {
        ManagedApplicationContext result = waitForProcessAndWindow(processName, timeoutSeconds);
        return result != null;
    }
    
    /**
     * Find main window for a given process name
     * Uses tasklist to get PID, then finds windows associated with that PID
     * @param processName The process name (e.g., "msedge.exe")
     * @param timeoutSeconds Time to wait for window to appear after process starts
     * @return true if window found and set as current
     */
    public boolean findWindowByProcessName(String processName, int timeoutSeconds) {
        logger.info("Finding main window for process: {}", processName);
        
        try {
            // Get process ID(s) for this process name
            java.util.List<Integer> pids = getProcessIds(processName);
            
            if (pids.isEmpty()) {
                logger.warn("No running processes found for: {}", processName);
                return false;
            }
            
            logger.debug("Found {} process(es) for '{}': {}", pids.size(), processName, pids);
            
            // Try to find main window for each PID
            long startTime = System.currentTimeMillis();
            long timeout = timeoutSeconds * 1000L;
            
            while (System.currentTimeMillis() - startTime < timeout) {
                for (Integer pid : pids) {
                    WinDef.HWND window = findMainWindowByPID(pid);
                    if (window != null) {
                        currentWindow = window;
                        String windowTitle = getWindowTitle(window);
                        logger.info("✅ Found main window for process '{}' (PID: {}): '{}'", 
                            processName, pid, windowTitle);
                        return true;
                    }
                }
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            
            logger.warn("❌ No main window found for process '{}' after {} seconds", processName, timeoutSeconds);
            return false;
            
        } catch (Exception e) {
            logger.error("Error finding window for process '{}': {}", processName, e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all process IDs for a given process name
     * @param processName The process name (e.g., "msedge.exe")
     * @return List of process IDs
     */
    private java.util.List<Integer> getProcessIds(String processName) {
        java.util.List<Integer> pids = new java.util.ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq " + processName, "/FO", "CSV", "/NH");
            Process process = pb.start();
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(",")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        try {
                            // Remove quotes and parse PID
                            String pidStr = parts[1].replace("\"", "").trim();
                            int pid = Integer.parseInt(pidStr);
                            pids.add(pid);
                        } catch (NumberFormatException e) {
                            // Skip invalid PID entries
                        }
                    }
                }
            }
            
            process.waitFor();
            
        } catch (Exception e) {
            logger.error("Failed to get process IDs for '{}': {}", processName, e.getMessage());
        }
        
        return pids;
    }
      /**
     * Find the main window for a given process ID
     * Enhanced to handle console applications (CMD, PowerShell) specifically
     * @param pid Process ID
     * @return Window handle or null if not found
     */
    private WinDef.HWND findMainWindowByPID(int pid) {
        logger.debug("🔍 WINDOW DISCOVERY: Finding main window for PID {}", pid);
        
        // First, try the standard window finder for regular applications
        ProcessWindowFinder finder = new ProcessWindowFinder(pid);
        User32.INSTANCE.EnumWindows(finder, null);
        WinDef.HWND standardWindow = finder.getMainWindow();
        
        if (standardWindow != null) {
            String windowTitle = getWindowTitle(standardWindow);
            logger.debug("✅ STANDARD WINDOW FOUND: PID {} has window '{}'", pid, windowTitle);
            return standardWindow;
        }
        
        // If no standard window found, check if this might be a console application
        logger.debug("🔍 CONSOLE CHECK: No standard window found for PID {}, checking for console application", pid);
        
        // Use the enhanced console window detection that already exists
        WinDef.HWND consoleWindow = findConsoleWindowForProcess(pid);
        if (consoleWindow != null) {
            String windowTitle = getWindowTitle(consoleWindow);
            logger.info("✅ CONSOLE WINDOW FOUND: PID {} has console window '{}'", pid, windowTitle);
            return consoleWindow;
        }
        
        // Final fallback: try to find ANY window for this PID (less restrictive)
        logger.debug("🔍 FALLBACK: Trying less restrictive window search for PID {}", pid);
        LessRestrictiveWindowFinder fallbackFinder = new LessRestrictiveWindowFinder(pid);
        User32.INSTANCE.EnumWindows(fallbackFinder, null);
        WinDef.HWND fallbackWindow = fallbackFinder.getAnyWindow();
        
        if (fallbackWindow != null) {
            String windowTitle = getWindowTitle(fallbackWindow);
            logger.info("✅ FALLBACK WINDOW FOUND: PID {} has window '{}'", pid, windowTitle);
            return fallbackWindow;
        }
        
        logger.warn("❌ NO WINDOW FOUND: Unable to locate any window for PID {}", pid);
        return null;
    }
    
    /**
     * Get window title from window handle
     * @param hwnd Window handle
     * @return Window title or empty string
     */
    private String getWindowTitle(WinDef.HWND hwnd) {
        try {
            char[] buffer = new char[512];
            User32.INSTANCE.GetWindowText(hwnd, buffer, 512);
            return Native.toString(buffer);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Enhanced process checking with detailed logging
     * @param processName Process name to check
     * @return true if process is running
     */
    public boolean isProcessRunningVerbose(String processName) {
        logger.debug("Checking if process '{}' is running...", processName);
        
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq " + processName, "/FO", "CSV");
            Process process = pb.start();
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
            
            String line;
            int processCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(processName.toLowerCase()) && 
                    !line.startsWith("INFO:")) { // Skip header/info lines
                    processCount++;
                    logger.debug("Found process instance: {}", line.trim());
                }
            }
            
            process.waitFor();
            
            boolean isRunning = processCount > 0;
            logger.debug("Process '{}' running: {} ({} instances)", processName, isRunning, processCount);
            return isRunning;
            
        } catch (Exception e) {
            logger.error("Failed to check if process '{}' is running: {}", processName, e.getMessage());
            return false;
        }
    }
    
    /**
     * Inner class for finding main window by process ID
     */
    private static class ProcessWindowFinder implements User32.WNDENUMPROC {
        private final int targetPid;
        private WinDef.HWND mainWindow;
        
        public ProcessWindowFinder(int pid) {
            this.targetPid = pid;
        }
          @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            // Get process ID for this window
            IntByReference processId = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hWnd, processId);
            
            int windowPid = processId.getValue();
            
            if (windowPid == targetPid) {
                // Check if this is a main window (visible and has title)
                if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                    char[] buffer = new char[512];
                    User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
                    String windowTitle = Native.toString(buffer);
                    
                    if (!windowTitle.trim().isEmpty()) {
                        // Check if this is likely the main window (not a child/tool window)
                        WinDef.HWND parent = User32.INSTANCE.GetParent(hWnd);
                        if (parent == null) { // Top-level window
                            mainWindow = hWnd;
                            return false; // Found main window, stop enumeration
                        }
                    }
                }
            }
            
            return true; // Continue enumeration
        }
          public WinDef.HWND getMainWindow() {
            return mainWindow;
        }    }
    
    /**
     * Inner class for finding any console window as fallback
     */
    private static class GeneralConsoleWindowFinder implements User32.WNDENUMPROC {
        private WinDef.HWND consoleWindow;
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                char[] buffer = new char[512];
                User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
                String windowTitle = Native.toString(buffer).toLowerCase();
                
                // Look for any active console window
                if (windowTitle.contains("command prompt") || 
                    windowTitle.contains("cmd") ||
                    windowTitle.contains("powershell") ||
                    windowTitle.contains("terminal") ||
                    windowTitle.matches(".*c:\\\\.*") ||
                    windowTitle.contains("microsoft windows")) {
                    
                    consoleWindow = hWnd;
                    return false; // Found console window, stop enumeration
                }
            }
            
            return true; // Continue enumeration
        }
        
        public WinDef.HWND getConsoleWindow() {
            return consoleWindow;
        }    }
      /**
     * Enhanced Console Window Finder - More sophisticated CMD detection
     */
    private static class EnhancedConsoleWindowFinder implements User32.WNDENUMPROC {
        private WinDef.HWND consoleWindow;
        private String bestWindowTitle = "";
        
        public EnhancedConsoleWindowFinder(int pid) {
            // PID not used in this enhanced version, but kept for interface compatibility
        }
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                char[] buffer = new char[1024];
                User32.INSTANCE.GetWindowText(hWnd, buffer, 1024);
                String windowTitle = Native.toString(buffer).toLowerCase();
                
                // Enhanced CMD detection patterns
                boolean isCmdWindow = windowTitle.contains("command prompt") || 
                                   windowTitle.contains("cmd.exe") ||
                                   windowTitle.contains("c:\\windows\\system32\\cmd.exe") ||
                                   windowTitle.contains("administrator: command prompt") ||
                                   windowTitle.matches(".*[a-z]:\\\\.*>.*") ||
                                   windowTitle.matches(".*c:\\\\.*") ||
                                   (windowTitle.contains("c:") && windowTitle.contains(">"));
                
                if (isCmdWindow) {
                    // Check if this is a better match than previous ones
                    if (isHigherPriorityWindow(windowTitle)) {
                        consoleWindow = hWnd;
                        bestWindowTitle = windowTitle;
                        logger.debug("🎯 ENHANCED CMD: Found better CMD window: '{}'", windowTitle);
                    }
                }
            }
            
            return true; // Continue enumeration to find the best match
        }
        
        private boolean isHigherPriorityWindow(String windowTitle) {
            // Prioritize windows with specific CMD indicators
            if (windowTitle.contains("administrator: command prompt")) return true;
            if (windowTitle.contains("command prompt") && bestWindowTitle.isEmpty()) return true;
            if (windowTitle.matches(".*[a-z]:\\\\.*>.*") && !bestWindowTitle.contains("command prompt")) return true;
            return false;
        }
        
        public WinDef.HWND getConsoleWindow() {
            return consoleWindow;
        }
    }
      /**
     * Recent Console Window Finder - Finds recently opened CMD windows
     */
    private static class RecentConsoleWindowFinder implements User32.WNDENUMPROC {
        private WinDef.HWND bestConsoleWindow;
        private WinDef.HWND mostRecentConsoleWindow;
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                char[] buffer = new char[1024];
                User32.INSTANCE.GetWindowText(hWnd, buffer, 1024);
                String windowTitle = Native.toString(buffer).toLowerCase();
                
                // More specific CMD window detection - avoid false positives from editors
                boolean isCmdWindow = windowTitle.contains("command prompt") || 
                                    windowTitle.equals("cmd.exe") ||
                                    windowTitle.matches(".*c:\\\\windows\\\\system32\\\\cmd\\.exe.*") ||
                                    windowTitle.matches(".*[a-z]:\\\\.*>$") ||  // Ends with prompt like "C:\>", not just contains
                                    (windowTitle.contains("cmd.exe") && !windowTitle.contains("feature") && !windowTitle.contains("test"));
                
                if (isCmdWindow) {
                    // Prefer "Command Prompt" windows over others
                    if (windowTitle.contains("command prompt")) {
                        bestConsoleWindow = hWnd;
                        logger.debug("🕒 RECENT CMD: Found Command Prompt window: '{}'", windowTitle);
                    } else if (bestConsoleWindow == null) {
                        // Use as fallback if no "Command Prompt" found
                        mostRecentConsoleWindow = hWnd;
                        logger.debug("🕒 RECENT CMD: Found potential CMD window: '{}'", windowTitle);
                    }
                }
            }
            
            return true; // Continue enumeration
        }
        
        public WinDef.HWND getBestConsoleWindow() {
            return bestConsoleWindow != null ? bestConsoleWindow : mostRecentConsoleWindow;
        }
    }    
    // ==== ENHANCED MULTI-WINDOW SUPPORT ====
      /**
     * Discover and manage all windows for a process
     * @param processInfo ManagedApplicationContext to enhance with window discovery
     * @return Number of windows discovered
     */
    public int discoverAllWindowsForProcess(ManagedApplicationContext processInfo) {
        logger.info("🔍 MULTI-WINDOW DISCOVERY: Scanning all windows for process {}", processInfo.getProcessName());
        
        // Since ManagedApplicationContext already discovers windows during construction,
        // we just return the count of already discovered windows
        List<WinDef.HWND> windowHandles = processInfo.getWindowHandles();
        int discoveredCount = windowHandles.size();
        
        logger.info("🔍 DISCOVERY COMPLETE: Found {} windows for process {}", 
            discoveredCount, processInfo.getProcessName());
        return discoveredCount;
    }/**
     * ENTERPRISE: Focus specific window within a multi-window process using ManagedApplicationContext
     * @param context ManagedApplicationContext containing the target window
     * @param windowTitlePattern Pattern to match window title
     * @param caseSensitive Whether matching should be case sensitive
     * @return true if window was found and focused
     */
    public boolean focusWindowByTitle(ManagedApplicationContext context, String windowTitlePattern, boolean caseSensitive) {
        logger.info("🎯 ENTERPRISE MULTI-WINDOW FOCUS: Targeting window '{}' in application {}", 
            windowTitlePattern, context.getManagedApplicationName());
        
        // Find matching window by title pattern using enterprise method
        ManagedApplicationContext.WindowContext matchingWindow = context.findWindowByTitle(windowTitlePattern, caseSensitive);
        if (matchingWindow != null) {
            // Focus the window using Win32WindowControl
            Win32WindowControl windowControl = Win32WindowControl.getInstance();
            boolean focused = windowControl.activateWindow(matchingWindow.getHandle());
            
            if (focused) {
                // Update current window reference for legacy compatibility
                currentWindow = matchingWindow.getHandle();
                logger.info("✅ ENTERPRISE FOCUS SUCCESS: Window '{}' is now active", windowTitlePattern);
                return true;
            }
        }
        
        logger.warn("❌ ENTERPRISE FOCUS FAILED: Could not focus window '{}'", windowTitlePattern);
        
        // Log available windows for debugging using enterprise context
        logger.info("💡 AVAILABLE WINDOWS for {}:", context.getManagedApplicationName());
        for (ManagedApplicationContext.WindowContext window : context.getAllValidWindows()) {
            logger.info("   → {} [{}]", window.getTitle(), window.getClassName());
        }
        
        return false;
    }    /*
     * LEGACY METHOD DISABLED - Focus window by class name within a multi-window process  
     * @deprecated Use focusWindowByClass(ManagedApplicationContext, String) instead
     * 
    public boolean focusWindowByClass(ProcessInfo processInfo, String className) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: focusWindowByClass(ProcessInfo, String) has been replaced");
        logger.warn("⚠️ USE ENTERPRISE VERSION: focusWindowByClass(ManagedApplicationContext, String)");
        return false;
    }
    */
      /**
     * ENTERPRISE: Focus window by class name using ManagedApplicationContext
     * @param context ManagedApplicationContext to search
     * @param className Target window class name
     * @return true if window found and focused
     */
    public boolean focusWindowByClass(ManagedApplicationContext context, String className) {
        logger.info("🎯 ENTERPRISE CLASS-BASED FOCUS: Targeting window class '{}' in application {}", 
            className, context.getManagedApplicationName());
        
        // Find matching window by class name using enterprise methods
        for (ManagedApplicationContext.WindowContext window : context.getAllValidWindows()) {
            if (className.equals(window.getClassName())) {
                // Focus the window using Win32WindowControl
                Win32WindowControl windowControl = Win32WindowControl.getInstance();
                boolean focused = windowControl.activateWindow(window.getHandle());
                
                if (focused) {
                    // Update current window reference for legacy compatibility
                    currentWindow = window.getHandle();
                    logger.info("✅ ENTERPRISE CLASS-BASED FOCUS SUCCESS: Window class '{}' is now active", className);
                    return true;
                }
            }
        }
        
        logger.warn("❌ ENTERPRISE CLASS-BASED FOCUS FAILED: No windows found with class '{}'", className);
        return false;
    }    /*
     * LEGACY METHODS DISABLED FOR ENTERPRISE MIGRATION
     * 
     * The following legacy methods have been disabled and replaced with enterprise versions
     * that use ManagedApplicationContext instead of ProcessInfo/WindowInfo.
     * 
     * These methods will be removed entirely in Phase 4 of the migration.
     */
     
    /*
    // LEGACY: List all windows for a process with detailed information  
    public List<String> listProcessWindows(ProcessInfo processInfo) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: listProcessWindows(ProcessInfo)");
        logger.warn("⚠️ USE ENTERPRISE VERSION: listApplicationWindows(ManagedApplicationContext)");
        return new java.util.ArrayList<>();
    }
    
    // LEGACY: Get window statistics for a multi-window process
    public Map<String, Object> getProcessWindowStatistics(ProcessInfo processInfo) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: getProcessWindowStatistics(ProcessInfo)");  
        logger.warn("⚠️ USE ENTERPRISE VERSION: getApplicationWindowStatistics(ManagedApplicationContext)");
        return new java.util.HashMap<>();
    }
    
    // LEGACY: Enhanced window bounds for active window in multi-window process
    public Rectangle getActiveWindowBounds(ProcessInfo processInfo) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: getActiveWindowBounds(ProcessInfo)");
        logger.warn("⚠️ USE ENTERPRISE VERSION: getWindowBounds(ManagedApplicationContext)");
        return null;
    }
    
    // LEGACY: Focus a specific window by index for multi-window processes  
    public boolean focusWindowByIndex(ProcessInfo processInfo, int windowIndex) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: focusWindowByIndex(ProcessInfo, int)");
        logger.warn("⚠️ USE ENTERPRISE VERSION: focusWindowByIndex(ManagedApplicationContext, int)");
        return false;
    }
    */
      /*
     * ADDITIONAL LEGACY METHODS DISABLED FOR ENTERPRISE MIGRATION
     */
     
    /*
    // LEGACY: Enhanced process and window waiting with multi-window support
    public ProcessInfo waitForProcessWithMultipleWindows(String processName, int expectedWindowCount, int timeoutSeconds) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: waitForProcessWithMultipleWindows() has been replaced");
        logger.warn("⚠️ CONTACT DEVELOPMENT TEAM: This method needs enterprise replacement");
        return null;
    }
    
    // LEGACY: Enhanced window bounds for active window in multi-window process
    public Rectangle getActiveWindowBounds(ProcessInfo processInfo) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: getActiveWindowBounds(ProcessInfo)");
        logger.warn("⚠️ USE ENTERPRISE VERSION: getWindowBounds(ManagedApplicationContext)");
        return null;
    }
    
    // LEGACY: Focus a specific window by index for multi-window processes
    public boolean focusWindowByIndex(ProcessInfo processInfo, int windowIndex) {
        logger.warn("⚠️ LEGACY METHOD DISABLED: focusWindowByIndex(ProcessInfo, int)");
        logger.warn("⚠️ USE ENTERPRISE VERSION: focusWindowByIndex(ManagedApplicationContext, int)");
        return false;
    }
    */
    
    /**
     * Send key combination using string notation (e.g., "ALT+D", "CTRL+C")
     */
    public void sendKeyCombination(String keyCombo) {
        logger.debug("Sending key combination: {}", keyCombo);
        
        activateWindow();
        
        // Parse combination like "ALT+D" or "CTRL+SHIFT+A"
        String[] keys = keyCombo.split("\\+");
        
        // Convert to the format expected by sendKeyCombo
        sendKeyCombo(keys);
    }
    
    /**
     * Less restrictive window finder for applications that don't follow standard patterns
     * This finder accepts ANY visible window belonging to the target PID
     */
    private static class LessRestrictiveWindowFinder implements User32.WNDENUMPROC {
        private final int targetPid;
        private WinDef.HWND anyWindow;
        
        public LessRestrictiveWindowFinder(int pid) {
            this.targetPid = pid;
        }
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            // Get process ID for this window
            IntByReference processId = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hWnd, processId);
            
            int windowPid = processId.getValue();
            
            // Accept ANY visible window from our target process
            if (windowPid == targetPid && User32.INSTANCE.IsWindowVisible(hWnd)) {
                anyWindow = hWnd;
                return false; // Found a window, stop enumeration (take the first one found)
            }
            
            return true; // Continue enumeration
        }
        
        public WinDef.HWND getAnyWindow() {
            return anyWindow;
        }
    }
    
    // ============================================================================
    // ENTERPRISE ARCHITECTURE METHODS - ManagedApplicationContext Integration
    // ============================================================================
    
    /**
     * ENTERPRISE: Focus window using ManagedApplicationContext
     * @param context ManagedApplicationContext containing the target window
     * @return true if window was focused successfully
     */
    public boolean focusWindow(ManagedApplicationContext context) {
        if (context == null) {
            logger.error("Cannot focus window: ManagedApplicationContext is null");
            return false;
        }
        
        logger.info("🎯 ENTERPRISE FOCUS: Targeting application {} (PID: {})", 
            context.getManagedApplicationName(), context.getProcessId());
        
        // Use primary window from context
        WinDef.HWND primaryWindow = context.getPrimaryWindow();
        if (primaryWindow != null) {
            currentWindow = primaryWindow;
            if (activateWindow()) {
                logger.info("✅ ENTERPRISE FOCUS SUCCESS: Primary window activated for {}", 
                    context.getManagedApplicationName());
                return true;
            }
        }
        
        // Fallback: try to find any valid window
        for (ManagedApplicationContext.WindowContext window : context.getAllValidWindows()) {
            currentWindow = window.getHandle();
            if (activateWindow()) {
                logger.info("✅ ENTERPRISE FOCUS FALLBACK: Window activated for {}", 
                    context.getManagedApplicationName());
                return true;
            }
        }
        
        logger.warn("❌ ENTERPRISE FOCUS FAILED: No windows could be activated for {}", 
            context.getManagedApplicationName());
        return false;
    }
    
    /**
     * ENTERPRISE: Focus window by index using ManagedApplicationContext
     * @param context ManagedApplicationContext containing the target windows
     * @param windowIndex Zero-based window index
     * @return true if window was focused successfully
     */
    public boolean focusWindowByIndex(ManagedApplicationContext context, int windowIndex) {
        if (context == null) {
            logger.error("Cannot focus window by index: ManagedApplicationContext is null");
            return false;
        }
        
        logger.info("🎯 ENTERPRISE WINDOW INDEX FOCUS: Targeting window {} in application {}", 
            windowIndex, context.getManagedApplicationName());
        
        ManagedApplicationContext.WindowContext targetWindow = context.getWindowByIndex(windowIndex);
        if (targetWindow != null) {
            // Focus the window using Win32WindowControl
            Win32WindowControl windowControl = Win32WindowControl.getInstance();
            boolean focused = windowControl.activateWindow(targetWindow.getHandle());
            
            if (focused) {
                // Update current window reference for legacy compatibility
                currentWindow = targetWindow.getHandle();
                logger.info("✅ ENTERPRISE INDEX FOCUS SUCCESS: Window {} activated for {}", 
                    windowIndex, context.getManagedApplicationName());
                return true;
            }
        } else {
            int validWindowCount = context.getAllValidWindows().size();
            logger.warn("❌ ENTERPRISE INDEX FOCUS FAILED: Invalid window index {} for {} (available: {})", 
                windowIndex, context.getManagedApplicationName(), validWindowCount);
        }
        
        return false;
    }
    
    /**
     * ENTERPRISE: Get window bounds using ManagedApplicationContext
     * @param context ManagedApplicationContext to get bounds for
     * @return Rectangle containing window bounds, or null if failed
     */
    public Rectangle getWindowBounds(ManagedApplicationContext context) {
        if (context == null) {
            logger.warn("Cannot get window bounds: ManagedApplicationContext is null");
            return null;
        }
        
        // Use primary window bounds if available
        Rectangle primaryBounds = context.getPrimaryWindowBounds();
        if (primaryBounds != null) {
            logger.debug("✅ ENTERPRISE BOUNDS: Using primary window bounds for {}", 
                context.getManagedApplicationName());
            return primaryBounds;
        }
        
        // Fallback: get bounds from any valid window
        for (ManagedApplicationContext.WindowContext window : context.getAllValidWindows()) {
            Rectangle bounds = window.getBounds();
            if (bounds != null) {
                logger.debug("✅ ENTERPRISE BOUNDS FALLBACK: Using valid window bounds for {}", 
                    context.getManagedApplicationName());
                return bounds;
            }
        }
        
        logger.warn("❌ ENTERPRISE BOUNDS FAILED: No window bounds available for {}", 
            context.getManagedApplicationName());
        return null;
    }
    
    /**
     * ENTERPRISE: List all windows for application using ManagedApplicationContext
     * @param context ManagedApplicationContext to examine
     * @return List of window descriptions
     */
    public List<String> listApplicationWindows(ManagedApplicationContext context) {
        if (context == null) {
            logger.warn("Cannot list windows: ManagedApplicationContext is null");
            return new java.util.ArrayList<>();
        }
        
        logger.info("📋 ENTERPRISE WINDOW LIST: Getting all windows for application {}", 
            context.getManagedApplicationName());
        
        List<String> descriptions = new java.util.ArrayList<>();
        int windowIndex = 0;
        
        for (ManagedApplicationContext.WindowContext window : context.getAllValidWindows()) {
            String description = String.format("[%d] %s (Class: %s, Visible: %s, Size: %dx%d)", 
                windowIndex,
                window.getTitle(),
                window.getClassName(),
                window.isVisible(),
                window.getBounds().width,
                window.getBounds().height
            );
            descriptions.add(description);
            windowIndex++;
        }
        
        logger.info("📋 ENTERPRISE WINDOW LIST COMPLETE: Found {} windows for {}", 
            descriptions.size(), context.getManagedApplicationName());
        
        return descriptions;
    }
    
    /**
     * ENTERPRISE: Get comprehensive window statistics using ManagedApplicationContext
     * @param context ManagedApplicationContext to analyze
     * @return Map containing comprehensive window statistics
     */
    public Map<String, Object> getApplicationWindowStatistics(ManagedApplicationContext context) {
        if (context == null) {
            logger.warn("Cannot get window statistics: ManagedApplicationContext is null");
            return new HashMap<>();
        }
        
        logger.info("📊 ENTERPRISE WINDOW STATISTICS: Analyzing windows for application {}", 
            context.getManagedApplicationName());
        
        // Use the enterprise method from ManagedApplicationContext
        Map<String, Object> stats = context.getWindowStatistics();
        
        // Add additional WindowController-specific statistics
        stats.put("applicationName", context.getManagedApplicationName());
        stats.put("processId", context.getProcessId());
        stats.put("primaryWindowBounds", context.getPrimaryWindowBounds());
        
        // Add current window info if it belongs to this context
        if (currentWindow != null) {
            ManagedApplicationContext.WindowContext currentWindowContext = context.getWindowContext(currentWindow);
            if (currentWindowContext != null) {
                stats.put("currentWindowTitle", currentWindowContext.getTitle());
                stats.put("currentWindowClass", currentWindowContext.getClassName());
                stats.put("isCurrentWindowActive", true);
            } else {
                stats.put("isCurrentWindowActive", false);
            }
        } else {
            stats.put("isCurrentWindowActive", false);
        }
        
        logger.info("📊 ENTERPRISE STATISTICS COMPLETE: Generated {} statistics for {}", 
            stats.size(), context.getManagedApplicationName());
        
        return stats;
    }
}
