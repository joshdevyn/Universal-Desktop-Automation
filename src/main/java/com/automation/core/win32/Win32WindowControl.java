package com.automation.core.win32;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SURGICAL WINDOW CONTROL MODULE
 * 
 * Complete Windows window manipulation APIs:
 * - Window discovery and enumeration
 * - Window state management (show, hide, minimize, maximize)
 * - Window positioning and sizing
 * - Window focus and activation
 * - Window property inspection
 * - Child window enumeration
 * - Window class and style analysis
 * - Z-order management
 * 
 * FITS LIKE A FUCKING GLOVE around Windows window management
 */
public class Win32WindowControl {
    private static final Logger logger = LoggerFactory.getLogger(Win32WindowControl.class);
    private static Win32WindowControl instance;
    
    // Window cache for performance
    private final Map<WinDef.HWND, WindowDetails> windowCache = new ConcurrentHashMap<>();
    private final Map<Integer, Set<WinDef.HWND>> processWindowCache = new ConcurrentHashMap<>();
    
    // Window state constants
    public static final int SW_HIDE = 0;
    public static final int SW_SHOWNORMAL = 1;
    public static final int SW_SHOWMINIMIZED = 2;
    public static final int SW_SHOWMAXIMIZED = 3;
    public static final int SW_SHOWNOACTIVATE = 4;
    public static final int SW_SHOW = 5;
    public static final int SW_MINIMIZE = 6;
    public static final int SW_SHOWMINNOACTIVE = 7;
    public static final int SW_SHOWNA = 8;
    public static final int SW_RESTORE = 9;
    public static final int SW_SHOWDEFAULT = 10;
    
    // Window placement flags
    public static final int SWP_NOSIZE = 0x0001;
    public static final int SWP_NOMOVE = 0x0002;
    public static final int SWP_NOZORDER = 0x0004;
    public static final int SWP_NOACTIVATE = 0x0010;
    public static final int SWP_SHOWWINDOW = 0x0040;
    public static final int SWP_HIDEWINDOW = 0x0080;
    
    // Special HWND values for SetWindowPos
    public static final WinDef.HWND HWND_TOP = new WinDef.HWND(Pointer.createConstant(0));
    public static final WinDef.HWND HWND_BOTTOM = new WinDef.HWND(Pointer.createConstant(1));
    public static final WinDef.HWND HWND_TOPMOST = new WinDef.HWND(Pointer.createConstant(-1));
    public static final WinDef.HWND HWND_NOTOPMOST = new WinDef.HWND(Pointer.createConstant(-2));
    
    public static Win32WindowControl getInstance() {
        if (instance == null) {
            synchronized (Win32WindowControl.class) {
                if (instance == null) {
                    instance = new Win32WindowControl();
                }
            }
        }
        return instance;
    }
    
    private Win32WindowControl() {
        logger.info("SURGICAL WINDOW CONTROL: Initializing comprehensive window management");
    }
    
    // ===== WINDOW DISCOVERY =====
    
    /**
     * COMPREHENSIVE window discovery for a process using multiple strategies
     */
    public Set<WinDef.HWND> discoverAllProcessWindows(int pid) {
        logger.debug("WINDOW DISCOVERY: Comprehensive scan for PID {}", pid);
        
        Set<WinDef.HWND> allWindows = new HashSet<>();
        
        // Strategy 1: Standard EnumWindows
        WindowEnumerator enumerator = new WindowEnumerator(pid);
        User32.INSTANCE.EnumWindows(enumerator, null);
        allWindows.addAll(enumerator.getFoundWindows());
        
        // Strategy 2: Find windows by thread enumeration
        allWindows.addAll(findWindowsByThreads(pid));
        
        // Strategy 3: Desktop child enumeration
        allWindows.addAll(findDesktopChildWindows(pid));
        
        // Strategy 4: Find console windows
        allWindows.addAll(findConsoleWindows(pid));
        
        // Cache the results
        processWindowCache.put(pid, new HashSet<>(allWindows));
        
        // Cache window details
        for (WinDef.HWND hwnd : allWindows) {
            if (isValidWindow(hwnd)) {
                cacheWindowDetails(hwnd);
            }
        }
        
        logger.debug("DISCOVERY COMPLETE: PID {} has {} windows", pid, allWindows.size());
        return allWindows;
    }
    
    /**
     * Find windows using thread enumeration
     */
    private Set<WinDef.HWND> findWindowsByThreads(int pid) {
        Set<WinDef.HWND> windows = new HashSet<>();
        
        try {
            // Get all threads for the process
            List<Integer> threadIds = Win32ApiWrapper.getInstance().gatherProcessIntelligence(pid).threadIds;
            
            for (Integer threadId : threadIds) {
                // Get GUI thread info
                WinUser.GUITHREADINFO guiInfo = new WinUser.GUITHREADINFO();
                guiInfo.cbSize = guiInfo.size();
                
                if (User32.INSTANCE.GetGUIThreadInfo(threadId, guiInfo)) {
                    addValidWindow(windows, guiInfo.hwndActive);
                    addValidWindow(windows, guiInfo.hwndFocus);
                    addValidWindow(windows, guiInfo.hwndCapture);
                    addValidWindow(windows, guiInfo.hwndMenuOwner);
                    addValidWindow(windows, guiInfo.hwndMoveSize);
                    addValidWindow(windows, guiInfo.hwndCaret);
                }
                
                // Enumerate thread windows
                ThreadWindowEnumerator threadEnum = new ThreadWindowEnumerator();
                User32.INSTANCE.EnumThreadWindows(threadId, threadEnum, null);
                windows.addAll(threadEnum.getFoundWindows());
            }
        } catch (Exception e) {
            logger.debug("Thread window enumeration failed for PID {}: {}", pid, e.getMessage());
        }
        
        return windows;
    }
    
    /**
     * Find desktop child windows
     */
    private Set<WinDef.HWND> findDesktopChildWindows(int pid) {
        Set<WinDef.HWND> windows = new HashSet<>();
        
        try {
            WinDef.HWND desktop = User32.INSTANCE.GetDesktopWindow();
            ChildWindowEnumerator childEnum = new ChildWindowEnumerator(pid);
            User32.INSTANCE.EnumChildWindows(desktop, childEnum, null);
            windows.addAll(childEnum.getFoundWindows());
        } catch (Exception e) {
            logger.debug("Desktop child enumeration failed for PID {}: {}", pid, e.getMessage());
        }
        
        return windows;
    }
    
    /**
     * Find console windows for console applications
     */
    private Set<WinDef.HWND> findConsoleWindows(int pid) {
        Set<WinDef.HWND> windows = new HashSet<>();
        
        try {
            // Find console windows by looking for specific window classes
            String[] consoleClasses = {"ConsoleWindowClass", "Windows Command Processor"};
            
            for (String className : consoleClasses) {
                WinDef.HWND consoleWindow = User32.INSTANCE.FindWindow(className, null);
                if (consoleWindow != null) {
                    IntByReference windowPid = new IntByReference();
                    User32.INSTANCE.GetWindowThreadProcessId(consoleWindow, windowPid);
                    
                    if (windowPid.getValue() == pid) {
                        windows.add(consoleWindow);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Console window search failed for PID {}: {}", pid, e.getMessage());
        }
        
        return windows;
    }
    
    /**
     * Cache comprehensive window details
     */
    private void cacheWindowDetails(WinDef.HWND hwnd) {
        if (windowCache.containsKey(hwnd)) {
            return; // Already cached
        }
        
        try {
            WindowDetails details = new WindowDetails();
            details.handle = hwnd;
            details.title = getWindowTitle(hwnd);
            details.className = getWindowClassName(hwnd);
            details.bounds = getWindowBounds(hwnd);            details.isVisible = User32.INSTANCE.IsWindowVisible(hwnd);
            details.isEnabled = User32.INSTANCE.IsWindowEnabled(hwnd);
            details.isIconic = isWindowMinimized(hwnd);
            details.isZoomed = isWindowMaximized(hwnd);
            details.style = getWindowStyle(hwnd);
            details.exStyle = getWindowExStyle(hwnd);
            details.parent = User32.INSTANCE.GetParent(hwnd);
            details.owner = User32.INSTANCE.GetWindow(hwnd, new WinDef.DWORD(User32.GW_OWNER));
            details.cacheTime = System.currentTimeMillis();
            
            // Get process ID for this window
            IntByReference pid = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
            details.processId = pid.getValue();
            
            windowCache.put(hwnd, details);
            
        } catch (Exception e) {
            logger.debug("Failed to cache window details for {}: {}", hwnd, e.getMessage());
        }
    }
    
    // ===== WINDOW MANIPULATION =====
    
    /**
     * SURGICAL window activation - brings window to foreground
     */
    public boolean activateWindow(WinDef.HWND hwnd) {
        if (!isValidWindow(hwnd)) {
            logger.warn("ACTIVATION FAILED: Invalid window handle");
            return false;
        }
        
        try {
            logger.debug("ACTIVATING WINDOW: {} ('{}')", hwnd, getWindowTitle(hwnd));
            
            // Multi-step activation for maximum reliability
            boolean success = false;
            
            // Step 1: Show window if hidden
            if (!User32.INSTANCE.IsWindowVisible(hwnd)) {
                User32.INSTANCE.ShowWindow(hwnd, SW_SHOW);
            }
              // Step 2: Restore if minimized
            if (isWindowMinimized(hwnd)) {
                User32.INSTANCE.ShowWindow(hwnd, SW_RESTORE);
            }
            
            // Step 3: Bring to foreground
            success = User32.INSTANCE.SetForegroundWindow(hwnd);
              // Step 4: Activate window (alternative to SetActiveWindow)
            if (!success) {
                success = setActiveWindowAlternative(hwnd);
            }
            
            // Step 5: Set focus
            if (!success) {
                success = User32.INSTANCE.SetFocus(hwnd) != null;
            }
            
            // Step 6: Bring window to top of Z-order
            if (success) {
                User32.INSTANCE.BringWindowToTop(hwnd);
                User32.INSTANCE.SetWindowPos(hwnd, HWND_TOP, 0, 0, 0, 0,
                    SWP_NOMOVE | SWP_NOSIZE | SWP_SHOWWINDOW);
            }
            
            if (success) {
                logger.debug("ACTIVATION SUCCESS: Window activated");
                updateWindowCache(hwnd);
            } else {
                logger.warn("ACTIVATION PARTIAL: Some activation steps may have failed");
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("ACTIVATION EXCEPTION: Failed to activate window: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Set window position and size with surgical precision
     */
    public boolean setWindowBounds(WinDef.HWND hwnd, Rectangle bounds) {
        if (!isValidWindow(hwnd)) {
            return false;
        }
        
        try {
            boolean success = User32.INSTANCE.SetWindowPos(hwnd, null,
                bounds.x, bounds.y, bounds.width, bounds.height,
                SWP_NOZORDER | SWP_NOACTIVATE);
            
            if (success) {
                updateWindowCache(hwnd);
                logger.debug("WINDOW POSITIONED: {} to {}", hwnd, bounds);
            }
            
            return success;
        } catch (Exception e) {
            logger.error("Failed to set window bounds: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Show window with specific state
     */
    public boolean showWindow(WinDef.HWND hwnd, int showCommand) {
        if (!isValidWindow(hwnd)) {
            return false;
        }
        
        try {
            boolean success = User32.INSTANCE.ShowWindow(hwnd, showCommand);
            if (success) {
                updateWindowCache(hwnd);
                logger.debug("WINDOW STATE CHANGED: {} to command {}", hwnd, showCommand);
            }
            return success;
        } catch (Exception e) {
            logger.error("Failed to show window: {}", e.getMessage());
            return false;
        }
    }
    
    // ===== WINDOW INSPECTION =====
    
    /**
     * Get comprehensive window details
     */
    public WindowDetails getWindowDetails(WinDef.HWND hwnd) {
        WindowDetails cached = windowCache.get(hwnd);
        if (cached != null && (System.currentTimeMillis() - cached.cacheTime) < 5000) {
            return cached; // Use cached if recent
        }
        
        cacheWindowDetails(hwnd);
        return windowCache.get(hwnd);
    }
    
    /**
     * Get window title
     */
    public String getWindowTitle(WinDef.HWND hwnd) {
        try {
            char[] buffer = new char[512];
            int length = User32.INSTANCE.GetWindowText(hwnd, buffer, 512);
            return length > 0 ? Native.toString(buffer) : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get window class name
     */
    public String getWindowClassName(WinDef.HWND hwnd) {
        try {
            char[] buffer = new char[256];
            int length = User32.INSTANCE.GetClassName(hwnd, buffer, 256);
            return length > 0 ? Native.toString(buffer) : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get window bounds (position and size)
     */
    public Rectangle getWindowBounds(WinDef.HWND hwnd) {
        try {
            WinDef.RECT rect = new WinDef.RECT();
            if (User32.INSTANCE.GetWindowRect(hwnd, rect)) {
                return new Rectangle(rect.left, rect.top, 
                    rect.right - rect.left, rect.bottom - rect.top);
            }
        } catch (Exception e) {
            logger.debug("Failed to get window bounds: {}", e.getMessage());
        }
        
        return new Rectangle(0, 0, 0, 0);
    }
    
    /**
     * Get window style
     */
    private int getWindowStyle(WinDef.HWND hwnd) {
        try {
            return User32.INSTANCE.GetWindowLong(hwnd, User32.GWL_STYLE);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get window extended style
     */
    private int getWindowExStyle(WinDef.HWND hwnd) {
        try {
            return User32.INSTANCE.GetWindowLong(hwnd, User32.GWL_EXSTYLE);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Check if window is minimized (alternative to IsIconic)
     */    private boolean isWindowMinimized(WinDef.HWND hwnd) {
        try {
            WinUser.WINDOWPLACEMENT placement = new WinUser.WINDOWPLACEMENT();
            boolean success = User32.INSTANCE.GetWindowPlacement(hwnd, placement).booleanValue();
            if (success) {
                return placement.showCmd == SW_SHOWMINIMIZED;
            }
        } catch (Exception e) {
            logger.debug("Failed to check window minimized state: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Check if window is maximized (alternative to IsZoomed)
     */    private boolean isWindowMaximized(WinDef.HWND hwnd) {
        try {
            WinUser.WINDOWPLACEMENT placement = new WinUser.WINDOWPLACEMENT();
            boolean success = User32.INSTANCE.GetWindowPlacement(hwnd, placement).booleanValue();
            if (success) {
                return placement.showCmd == SW_SHOWMAXIMIZED;
            }
        } catch (Exception e) {
            logger.debug("Failed to check window maximized state: {}", e.getMessage());
        }
        return false;
    }
    
    // ===== UTILITY METHODS =====
    
    /**
     * Check if window handle is valid
     */
    public boolean isValidWindow(WinDef.HWND hwnd) {
        return hwnd != null && User32.INSTANCE.IsWindow(hwnd);
    }
    
    /**
     * Add window to set if valid
     */
    private void addValidWindow(Set<WinDef.HWND> windows, WinDef.HWND hwnd) {
        if (isValidWindow(hwnd)) {
            windows.add(hwnd);
        }
    }
    
    /**
     * Update window cache
     */
    private void updateWindowCache(WinDef.HWND hwnd) {
        windowCache.remove(hwnd); // Remove old cache
        cacheWindowDetails(hwnd); // Re-cache with fresh data
    }
    
    // ===== JNA COMPATIBILITY METHODS =====
      /**
     * Alternative method to set active window for JNA compatibility
     * Keeps method focused on a single responsibility
     */
    private boolean setActiveWindowAlternative(WinDef.HWND hwnd) {
        try {
            if (!isValidWindow(hwnd)) {
                return false;
            }
            
            // Try multiple approaches for setting active window
            boolean success = false;
            
            // Approach 1: Use SetForegroundWindow
            success = User32.INSTANCE.SetForegroundWindow(hwnd);
            
            // Approach 2: If that fails, try bringing to top and setting foreground
            if (!success) {
                User32.INSTANCE.BringWindowToTop(hwnd);
                success = User32.INSTANCE.SetForegroundWindow(hwnd);
            }
            
            // Approach 3: Use SetWindowPos to activate
            if (!success) {
                success = User32.INSTANCE.SetWindowPos(hwnd, HWND_TOP, 0, 0, 0, 0,
                    SWP_NOMOVE | SWP_NOSIZE | SWP_SHOWWINDOW);
            }
            
            return success;
            
        } catch (Exception e) {
            logger.debug("Failed to set active window alternative: {}", e.getMessage());
            return false;
        }
    }
    
    // ===== WINDOW ENUMERATORS =====
    
    private static class WindowEnumerator implements User32.WNDENUMPROC {
        private final int targetPid;
        private final Set<WinDef.HWND> foundWindows = new HashSet<>();
        
        public WindowEnumerator(int pid) {
            this.targetPid = pid;
        }
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            try {
                IntByReference processId = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hWnd, processId);
                
                if (processId.getValue() == targetPid) {
                    foundWindows.add(hWnd);
                }
            } catch (Exception e) {
                // Continue enumeration
            }
            
            return true;
        }
        
        public Set<WinDef.HWND> getFoundWindows() {
            return foundWindows;
        }
    }
    
    private static class ThreadWindowEnumerator implements User32.WNDENUMPROC {
        private final Set<WinDef.HWND> foundWindows = new HashSet<>();
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            foundWindows.add(hWnd);
            return true;
        }
        
        public Set<WinDef.HWND> getFoundWindows() {
            return foundWindows;
        }
    }
    
    private static class ChildWindowEnumerator implements User32.WNDENUMPROC {
        private final int targetPid;
        private final Set<WinDef.HWND> foundWindows = new HashSet<>();
        
        public ChildWindowEnumerator(int pid) {
            this.targetPid = pid;
        }
        
        @Override
        public boolean callback(WinDef.HWND hWnd, Pointer data) {
            try {
                IntByReference processId = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hWnd, processId);
                
                if (processId.getValue() == targetPid) {
                    foundWindows.add(hWnd);
                }
            } catch (Exception e) {
                // Continue enumeration
            }
            
            return true;
        }
        
        public Set<WinDef.HWND> getFoundWindows() {
            return foundWindows;
        }
    }
      /**
     * Get comprehensive window snapshot for a process
     * Keeps method focused on a single responsibility
     */
    public WindowSnapshot getWindowSnapshot(int processId) {
        // Validate process ID parameter
        if (processId <= 0) {
            logger.warn("Invalid process ID: {}", processId);
            return null;
        }
        
        try {
            // Discover all windows for the process
            Set<WinDef.HWND> windowHandles = discoverAllProcessWindows(processId);
            Map<WinDef.HWND, WindowDetails> windowDetails = new HashMap<>();
            
            // Get detailed information for each window
            for (WinDef.HWND handle : windowHandles) {
                WindowDetails details = getWindowDetails(handle);
                if (details != null) {
                    windowDetails.put(handle, details);
                }
            }
            
            // Create comprehensive snapshot
            WindowSnapshot snapshot = new WindowSnapshot(processId, windowHandles, windowDetails);
            
            logger.debug("Window snapshot captured: {}", snapshot.getSummary());
            return snapshot;
            
        } catch (Exception e) {
            logger.error("Failed to get window snapshot for PID {}: {}", processId, e.getMessage());
            return null;
        }
    }

    // ===== DATA STRUCTURES =====
    
    /**
     * Window snapshot for comprehensive window state tracking
     */
    public static class WindowSnapshot {
        private final int processId;
        private final long timestamp;
        private final Set<WinDef.HWND> windowHandles;
        private final Map<WinDef.HWND, WindowDetails> windowDetails;
        private final WinDef.HWND primaryWindow;
        private final int totalWindows;
        private final int visibleWindows;
        private final int enabledWindows;
          public WindowSnapshot(int processId, Set<WinDef.HWND> windowHandles, 
                            Map<WinDef.HWND, WindowDetails> windowDetails) {
            // Validate input parameters
            if (processId <= 0) {
                throw new IllegalArgumentException("Process ID must be positive");
            }
            if (windowHandles == null) {
                throw new IllegalArgumentException("Window handles cannot be null");
            }
            if (windowDetails == null) {
                throw new IllegalArgumentException("Window details cannot be null");
            }
            
            this.processId = processId;
            this.timestamp = System.currentTimeMillis();
            this.windowHandles = new HashSet<>(windowHandles);
            this.windowDetails = new HashMap<>(windowDetails);
            this.totalWindows = windowHandles.size();
            this.visibleWindows = (int) windowDetails.values().stream().mapToLong(w -> w.isVisible ? 1 : 0).sum();
            this.enabledWindows = (int) windowDetails.values().stream().mapToLong(w -> w.isEnabled ? 1 : 0).sum();
            this.primaryWindow = findPrimaryWindow();
        }
          private WinDef.HWND findPrimaryWindow() {
            // Special handling for explorer.exe - prefer file Explorer windows over desktop shell
            boolean isExplorer = windowDetails.values().stream()
                .anyMatch(w -> w.className != null && w.className.contains("CabinetWClass"));
            
            if (isExplorer) {
                // First priority: Look for file Explorer windows (CabinetWClass)
                WinDef.HWND explorerWindow = windowDetails.values().stream()
                    .filter(w -> w.isVisible && w.isEnabled && !w.isIconic)
                    .filter(w -> w.className != null && w.className.contains("CabinetWClass"))
                    .filter(w -> w.title != null && w.title.length() > 0)
                    .filter(w -> !w.title.equals("Program Manager")) // Exclude desktop shell
                    .map(w -> w.handle)
                    .findFirst()
                    .orElse(null);
                
                if (explorerWindow != null) {
                    return explorerWindow;
                }
            }
            
            // Default behavior for all other applications
            return windowDetails.values().stream()
                .filter(w -> w.isVisible && w.isEnabled && !w.isIconic)
                .filter(w -> w.title != null && w.title.length() > 0)
                .filter(w -> !w.title.equals("Program Manager")) // Generally exclude desktop shell
                .map(w -> w.handle)
                .findFirst()
                .orElse(null);
        }
        
        // Comprehensive getters
        public int getProcessId() { return processId; }
        public long getTimestamp() { return timestamp; }
        public Set<WinDef.HWND> getWindowHandles() { return new HashSet<>(windowHandles); }
        public Map<WinDef.HWND, WindowDetails> getWindowDetails() { return new HashMap<>(windowDetails); }
        public WinDef.HWND getPrimaryWindow() { return primaryWindow; }
        public int getTotalWindows() { return totalWindows; }
        public int getVisibleWindows() { return visibleWindows; }
        public int getEnabledWindows() { return enabledWindows; }
        
        public String getSummary() {
            return String.format("WindowSnapshot[PID:%d, Total:%d, Visible:%d, Enabled:%d, Primary:%s]",
                processId, totalWindows, visibleWindows, enabledWindows, 
                primaryWindow != null ? "found" : "none");
        }
    }

    public static class WindowDetails {
        public WinDef.HWND handle;
        public String title;
        public String className;
        public Rectangle bounds;
        public boolean isVisible;
        public boolean isEnabled;
        public boolean isIconic;
        public boolean isZoomed;
        public int style;
        public int exStyle;
        public WinDef.HWND parent;
        public WinDef.HWND owner;
        public int processId;
        public long cacheTime;
        
        @Override
        public String toString() {
            return String.format("Window[%s, '%s', Class='%s', Bounds=%s, Visible=%s]",
                handle, title, className, bounds, isVisible);
        }
    }
}
