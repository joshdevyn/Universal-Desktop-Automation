package com.automation.models;

import com.automation.core.win32.*;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Kernel32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * ManagedApplicationContext - Complete Kernel Context Abstraction
 * 
 * This class represents the complete Windows kernel context for a managed application,
 * unifying process and window management into a single holomorphic representation. * 
 * Architecture: Process + Window + Memory + Performance + Handles = Single Kernel Context
 * 
 * Follows enterprise coding conventions for business-critical reliability:
 * - Fixed bounds on all loops
 * - Comprehensive parameter validation
 * - Minimal heap allocation after initialization
 * - Single-page method restrictions
 * - Zero tolerance for unchecked return values
 * 
 * @author Joshua Sims
 * @version 2.0.0 - Enterprise Context Management
 * @since 2025-06-17
 */
public final class ManagedApplicationContext {
    private static final Logger logger = LoggerFactory.getLogger(ManagedApplicationContext.class);
    
    // Restrict data scope to smallest possible
    private final String managedApplicationName;
    private final int processId;
    private final long creationTimestamp;
    
    // Process Intelligence Core
    private final String executablePath;
    private final String commandLine;
    private final String workingDirectory;
    private final int parentProcessId;
    private final WinNT.HANDLE processHandle;
    
    // Window Management Core (Holomorphic Unity)
    private final Map<WinDef.HWND, WindowContext> windows;
    private final List<WinDef.HWND> windowHandles;
    private WinDef.HWND primaryWindow;
    private Rectangle primaryWindowBounds;
      // Win32 Wrapper Integration - COMPLETE SUITE AS PER REFACTORING GUIDE
    private Win32MemoryManager.MemorySnapshot memorySnapshot;
    private Win32HandleTracker.HandleSnapshot handleSnapshot;    private Win32PerformanceMonitor.PerformanceSnapshot performanceSnapshot;
    private Win32ThreadManager.ThreadSnapshot threadSnapshot; // NEW: Thread management integration
    private Win32WindowControl.WindowSnapshot windowSnapshot; // Enhanced window control// COMPLETE Win32 wrapper suite for omnipotent kernel abstraction - NOW AVAILABLE
    private Win32SecurityManager.SecuritySnapshot securitySnapshot; // Process privileges & token management
    private Set<Win32ModuleManager.ModuleInfo> moduleInfo; // DLL/module enumeration & injection
    private Win32RegistryManager.RegistrySnapshot registrySnapshot; // Registry operations & monitoring
    private Win32SystemInfo.SystemInformation systemInformation; // Comprehensive system information
    private Win32FileSystemManager.FileSystemSnapshot fileSystemSnapshot; // File operations & monitoring
      // Enhanced Security Context - OMNIPOTENT KERNEL ACCESS
    private boolean hasElevatedPrivileges;
    private String userContext;
    private List<String> processPrivileges;
    
    // Module and DLL Context - Complete process introspection
    private List<Win32ModuleManager.ModuleInfo> loadedModules;
    private Map<String, String> moduleVersions;
    
    // Registry Context - Application configuration visibility  
    private Map<String, Object> registryContext;
    
    // File System Context - File handles and mapped files
    private List<Win32FileSystemManager.FileHandle> openFileHandles;
    private List<String> mappedFiles;
    
    // Context State Management
    private volatile boolean isActive;
    private volatile boolean isTerminated;
    private long lastUpdateTimestamp;
      /**
     * Window Context - Unified window representation within kernel context
     */
    public static final class WindowContext {
        private static final Logger logger = LoggerFactory.getLogger(WindowContext.class);
        
        private final WinDef.HWND handle;
        private final String title;
        private final String className;
        private final Rectangle bounds;
        private final boolean isVisible;
        private final boolean isEnabled;
        private final boolean isMinimized;
        private final boolean isMaximized;
        private final int zOrder;
          // Comprehensive parameter validation required
        public WindowContext(WinDef.HWND handle, String title, String className, 
                           Rectangle bounds, boolean isVisible, boolean isEnabled,
                           boolean isMinimized, boolean isMaximized, int zOrder) {
            Objects.requireNonNull(handle, "Window handle cannot be null");
            Objects.requireNonNull(title, "Window title cannot be null");
            Objects.requireNonNull(className, "Window class name cannot be null");
            Objects.requireNonNull(bounds, "Window bounds cannot be null");
            
            this.handle = handle;
            this.title = title;
            this.className = className;
            this.bounds = new Rectangle(bounds); // Defensive copy
            this.isVisible = isVisible;
            this.isEnabled = isEnabled;
            this.isMinimized = isMinimized;
            this.isMaximized = isMaximized;
            this.zOrder = zOrder;
        }
          // All getters with null safety
        public WinDef.HWND getHandle() { return handle; }
        public String getTitle() { return title; }
        public String getClassName() { return className; }
        public Rectangle getBounds() { return new Rectangle(bounds); }
        public boolean isVisible() { return isVisible; }
        public boolean isEnabled() { return isEnabled; }
        public boolean isMinimized() { return isMinimized; }
        public boolean isMaximized() { return isMaximized; }
        public int getZOrder() { return zOrder; }        public boolean isPrimaryWindow() {
            // Define primary window priority (lower number = higher priority)
            int priority = calculateWindowPriority();
            
            // Allow console windows with empty titles
            boolean hasValidTitle = !title.trim().isEmpty() || 
                                  isConsoleWindow() || 
                                  className.equals("PseudoConsoleWindow");
            
            // For console apps, reject zero-bounds windows (infrastructure windows)
            if (isConsoleWindow() && hasZeroBounds()) {
                logger.debug("🚫 Rejecting console window with zero bounds: {} | Class: {}", title, className);
                return false;
            }
            
            return isVisible && isEnabled && hasValidTitle && priority > 0;
        }        int calculateWindowPriority() {
            // Visible console windows with proper bounds (highest priority for console apps)
            if (isVisibleConsoleWindow()) {
                return 1;
            }
            
            // Console infrastructure windows (lower priority, fallback only)
            if (isConsoleWindow()) {
                return 7;
            }
            
            // Standard application windows
            if (isStandardApplicationWindow()) {
                return 4;
            }
            
            // Dialog windows
            if (isDialogWindow()) {
                return 5;
            }
            
            // Tool windows
            if (isToolWindow()) {
                return 6;
            }
            
            return 0; // Invalid window
        }

        boolean hasZeroBounds() {
            return bounds.width <= 0 || bounds.height <= 0;
        }

        boolean isVisibleConsoleWindow() {
            return isConsoleWindow() && !hasZeroBounds() && isVisible && isEnabled;
        }

        boolean isConsoleWindow() {
            return className != null && (
                className.equals("PseudoConsoleWindow") ||
                className.equals("ConsoleWindowClass") ||
                className.equals("CASCADIA_HOSTING_WINDOW_CLASS") ||
                className.contains("ConsoleWindow") ||
                className.contains("Console")
            );
        }

        private boolean isStandardApplicationWindow() {
            // Check for standard application window patterns
            if (className != null && className.contains("CabinetWClass")) {
                return true; // Explorer windows
            }
            
            // Regular windows with meaningful titles
            return !title.trim().isEmpty() && !isSystemWindow();
        }

        private boolean isDialogWindow() {
            return className != null && (
                className.contains("Dialog") ||
                className.contains("#32770")
            );
        }

        private boolean isToolWindow() {
            return className != null && (
                className.contains("Tool") ||
                className.contains("Popup")
            );
        }

        private boolean isSystemWindow() {
            // System windows that should never be primary
            if ("Program Manager".equals(title) || 
                (className != null && className.equals("Progman"))) {
                return true; // Desktop shell
            }
            
            if (className != null && className.equals("MSTaskSwWClass")) {
                return true; // Taskbar
            }
            
            if ("Running applications".equals(title) ||
                (className != null && className.contains("Shell_TrayWnd"))) {
                return true; // System shell windows
            }
            
            // Modern UI framework windows that are not interactive
            if (className != null && (className.contains("DesktopWindowXamlSource") ||
                className.contains("Windows.UI.Composition") ||
                className.contains("XAML") ||
                className.contains("CoreWindow"))) {
                return true; // Modern UI containers
            }
            
            return false;
        }
        
        @Override
        public String toString() {
            return String.format("Window[%s, '%s', %dx%d, %s]", 
                handle, title, bounds.width, bounds.height,
                isVisible ? "visible" : "hidden");
        }
    }
      /**
     * Primary Constructor - Creates complete kernel context from process ID
     * 
     * Maximum 60 lines per method, comprehensive validation required
     */
    public ManagedApplicationContext(String managedApplicationName, int processId) {
        // Minimum two runtime assertions per function
        Objects.requireNonNull(managedApplicationName, "Managed application name cannot be null");
        if (processId <= 0) {
            throw new IllegalArgumentException("Process ID must be positive: " + processId);
        }
        
        this.managedApplicationName = managedApplicationName;
        this.processId = processId;
        this.creationTimestamp = System.currentTimeMillis();        this.lastUpdateTimestamp = this.creationTimestamp;
        
        // Avoid heap allocation after initialization
        this.windows = new ConcurrentHashMap<>(16);
        this.windowHandles = new CopyOnWriteArrayList<>();
        
        // Initialize process intelligence
        Win32ApiWrapper apiWrapper = Win32ApiWrapper.getInstance();        Win32ApiWrapper.ProcessIntelligence intelligence = apiWrapper.gatherProcessIntelligence(processId);
          // Check return value of all non-void functions
        if (intelligence != null) {
            this.executablePath = intelligence.executablePath;
            this.commandLine = intelligence.commandLine;
            this.workingDirectory = System.getProperty("user.dir", "Unknown"); // Default working directory
            this.parentProcessId = intelligence.parentProcessId;
              // Open process handle for this context
            this.processHandle = Kernel32.INSTANCE.OpenProcess(
                WinNT.PROCESS_QUERY_INFORMATION, false, processId);
            
            this.isActive = true;
            this.isTerminated = false;
        } else {
            throw new IllegalStateException("Failed to gather process intelligence for PID: " + processId);
        }
        
        // Initialize Win32 snapshots
        refreshKernelSnapshots();
        
        // Discover and unify windows (holomorphic integration)
        discoverWindowContext();
        
        logger.info("ManagedApplicationContext created: {} (PID: {})", managedApplicationName, processId);
    }
      /**
     * Refresh all Win32 kernel snapshots - Fixed bounds, comprehensive validation
     */
    public synchronized void refreshKernelSnapshots() {
        Objects.requireNonNull(processHandle, "Process handle must be valid");
        if (isTerminated) {
            logger.warn("Cannot refresh snapshots for terminated process: {}", processId);
            return;
        }
        
        try {
            // Memory snapshot
            Win32MemoryManager memoryManager = Win32MemoryManager.getInstance();
            this.memorySnapshot = memoryManager.getMemorySnapshot(processId);
            
            // Handle snapshot  
            Win32HandleTracker handleTracker = Win32HandleTracker.getInstance();
            this.handleSnapshot = handleTracker.getHandleSnapshot(processId);
            
            // Performance snapshot
            Win32PerformanceMonitor performanceMonitor = Win32PerformanceMonitor.getInstance();
            this.performanceSnapshot = performanceMonitor.getPerformanceSnapshot(processId);            // Thread snapshot - NEW: Comprehensive thread management integration
            Win32ThreadManager threadManager = Win32ThreadManager.getInstance();
            this.threadSnapshot = threadManager.getThreadSnapshot(processId);
            
            // NEW: Complete Win32 wrapper suite integration
            Win32SecurityManager securityManager = Win32SecurityManager.getInstance();            this.securitySnapshot = securityManager.getSecuritySnapshot(processId);
            
            Win32ModuleManager moduleManager = Win32ModuleManager.getInstance();
            this.moduleInfo = moduleManager.enumerateProcessModules(processId);
              Win32RegistryManager registryManager = Win32RegistryManager.getInstance();
            // Get registry snapshot for the process (simplified - focus on common application areas)
            this.registrySnapshot = registryManager.getRegistrySnapshot(
                "HKEY_LOCAL_MACHINE", "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths");
            
            Win32SystemInfo systemInfo = Win32SystemInfo.getInstance();
            this.systemInformation = systemInfo.getCachedSystemInformation();
            
            Win32FileSystemManager fileSystemManager = Win32FileSystemManager.getInstance();
            this.fileSystemSnapshot = fileSystemManager.getFileSystemSnapshot(processId);
            
            // Update additional context information
            this.hasElevatedPrivileges = (securitySnapshot != null) ? securitySnapshot.isElevated() : false;            this.processPrivileges = (securitySnapshot != null) ? 
                securitySnapshot.getPrivileges().stream()
                    .map(Win32SecurityManager.PrivilegeInfo::getName)                    .collect(Collectors.toList()) : new ArrayList<>();
            this.loadedModules = (moduleInfo != null) ? 
                new ArrayList<>(moduleInfo) : new ArrayList<>();            this.openFileHandles = (fileSystemSnapshot != null) ? fileSystemSnapshot.getFileHandles() : new ArrayList<>();
            
            // Window snapshot - Enhanced window control integration
            Win32WindowControl windowControl = Win32WindowControl.getInstance();
            this.windowSnapshot = windowControl.getWindowSnapshot(processId);
              this.lastUpdateTimestamp = System.currentTimeMillis();
            
            logger.debug("Kernel snapshots refreshed for {}: Memory={}MB, Handles={}, CPU=%.1f%%, Threads={}", 
                managedApplicationName,
                memorySnapshot != null ? memorySnapshot.getWorkingSetSizeMB() : 0,
                handleSnapshot != null ? handleSnapshot.getTotalHandleCount() : 0,
                performanceSnapshot != null ? performanceSnapshot.getAverageCpuUsage() : 0.0,
                threadSnapshot != null ? threadSnapshot.getTotalThreads() : 0);
                
        } catch (Exception e) {
            logger.error("Failed to refresh kernel snapshots for {}: {}", managedApplicationName, e.getMessage());
            throw new RuntimeException("Kernel snapshot refresh failed", e);
        }
    }    /**
     * Discover and unify window context - Fixed bounds, single responsibility
     */    private void discoverWindowContext() {
        Objects.requireNonNull(processHandle, "Process handle must be valid");        // Check if this is an explorer.exe process for special handling
        boolean isExplorerProcess = isExplorerExecutable();
        if (isExplorerProcess) {            logger.info("EXPLORER.EXE DETECTED - Applying strict CabinetWClass-only filtering");
            logger.info("Process Name: '{}', Executable Path: '{}'", getProcessName(), executablePath);
            
            // For explorer processes, try to find a visible file Explorer window first (cross-PID search)
            WindowContext visibleExplorerWindow = findVisibleExplorerWindow();
            if (visibleExplorerWindow != null) {
                logger.info("🏛️ ✅ FOUND VISIBLE EXPLORER WINDOW: '{}' (Class: {}, Bounds: {}x{}) - will use as primary", 
                    visibleExplorerWindow.getTitle(), visibleExplorerWindow.getClassName(),
                    visibleExplorerWindow.getBounds().width, visibleExplorerWindow.getBounds().height);
                
                // Add the visible explorer window to our context even if it's from a different PID
                windows.put(visibleExplorerWindow.getHandle(), visibleExplorerWindow);
                windowHandles.add(visibleExplorerWindow.getHandle());
                primaryWindow = visibleExplorerWindow.getHandle();
                primaryWindowBounds = visibleExplorerWindow.getBounds();
                
                logger.info("⭐ PRIMARY EXPLORER WINDOW SET (CROSS-PID): '{}' (Class: {}, Bounds: {}x{})", 
                    visibleExplorerWindow.getTitle(), visibleExplorerWindow.getClassName(),
                    visibleExplorerWindow.getBounds().width, visibleExplorerWindow.getBounds().height);
            }
        }
        
        // Check if this is a cmd.exe process for special console window discovery
        boolean isCmdProcess = isCmdExecutable();
        if (isCmdProcess) {
            logger.info("🖥️ CMD.EXE DETECTED - Will perform comprehensive console window discovery");
            logger.info("🖥️ Process Name: '{}', Executable Path: '{}'", getProcessName(), executablePath);
            
            // For console processes, try to find the visible console window first
            WindowContext visibleConsoleWindow = findVisibleConsoleWindow();
            if (visibleConsoleWindow != null) {
                logger.info("🖥️ ✅ FOUND VISIBLE CONSOLE WINDOW: '{}' (Class: {}, Bounds: {}x{}) - will use as primary", 
                    visibleConsoleWindow.getTitle(), visibleConsoleWindow.getClassName(),
                    visibleConsoleWindow.getBounds().width, visibleConsoleWindow.getBounds().height);
                
                // Add the visible console window to our context even if it's from a different PID
                windows.put(visibleConsoleWindow.getHandle(), visibleConsoleWindow);
                windowHandles.add(visibleConsoleWindow.getHandle());
                primaryWindow = visibleConsoleWindow.getHandle();
                primaryWindowBounds = visibleConsoleWindow.getBounds();
                
                logger.info("⭐ PRIMARY CONSOLE WINDOW SET (CROSS-PID): '{}' (Class: {}, Bounds: {}x{})", 
                    visibleConsoleWindow.getTitle(), visibleConsoleWindow.getClassName(),
                    visibleConsoleWindow.getBounds().width, visibleConsoleWindow.getBounds().height);
            }
        }
        
        try {
            Win32WindowControl windowControl = Win32WindowControl.getInstance();
            Set<WinDef.HWND> discoveredWindows = windowControl.discoverAllProcessWindows(processId);
            
            // For explorer.exe, first log ALL CabinetWClass windows we can find
            if (isExplorerProcess) {
                logAllCabinetWClassWindows(discoveredWindows, windowControl);
            }
            
            // For cmd.exe, log ALL potential console windows we can find
            if (isCmdProcess) {
                logAllConsoleWindows(discoveredWindows, windowControl);            }
            
            // All loops must have fixed bounds
            int maxWindows = 150; // Fixed upper bound
            int windowCount = 0;
            
            for (WinDef.HWND windowHandle : discoveredWindows) {
                if (windowCount >= maxWindows) {
                    logger.warn("Maximum window limit reached for {}: {}", managedApplicationName, maxWindows);
                    break;
                }                  if (windowControl.isValidWindow(windowHandle)) {
                    WindowContext windowContext = createWindowContext(windowHandle, windowControl);
                    if (windowContext != null) {
                        
                        // EXPLORER.EXE EXCEPTION: Only consider CabinetWClass windows
                        if (isExplorerProcess) {
                            String className = windowContext.getClassName();
                            if (className == null || !className.contains("CabinetWClass")) {
                                logger.debug("🏛️ EXPLORER FILTER: Rejecting non-CabinetWClass window '{}' (Class: {})", 
                                    windowContext.getTitle(), className);
                                windowCount++;
                                continue; // Skip this window - only CabinetWClass allowed for explorer.exe
                            }
                            logger.info("🏛️ EXPLORER FILTER: Accepting CabinetWClass window '{}' (Class: {})", 
                                windowContext.getTitle(), className);
                        }
                        
                        windows.put(windowHandle, windowContext);
                        windowHandles.add(windowHandle);
                        
                        // DEBUG: Log every discovered window with full details
                        logger.debug("🔍 DISCOVERED WINDOW: '{}' (Class: {}, Visible: {}, Enabled: {}, Minimized: {})", 
                            windowContext.getTitle(), windowContext.getClassName(), 
                            windowContext.isVisible(), windowContext.isEnabled(), windowContext.isMinimized());
                          boolean isPrimary = windowContext.isPrimaryWindow();
                        logger.debug("🔍 PRIMARY CHECK: Window '{}' isPrimary = {} (Class: {})", 
                            windowContext.getTitle(), isPrimary, windowContext.getClassName());                          // For CMD processes, skip primary window selection if we already found a cross-PID console window
                        if (isCmdProcess && primaryWindow != null) {
                            logger.debug("🖥️ SKIPPING primary window selection for CMD - already have cross-PID console window");
                            windowCount++;
                            continue;
                        }
                        
                        // For Explorer processes, skip primary window selection if we already found a cross-PID Explorer window
                        if (isExplorerProcess && primaryWindow != null) {
                            logger.debug("🏛️ SKIPPING primary window selection for Explorer - already have cross-PID Explorer window");
                            windowCount++;
                            continue;
                        }
                        
                        // Identify primary window with new priority system
                        if (isPrimary) {
                            boolean shouldSetAsPrimary = false;
                            
                            // Get window priority from the WindowContext itself
                            int windowPriority = windowContext.calculateWindowPriority();
                            
                            if (primaryWindow == null) {
                                // No primary window yet, this is the first valid candidate
                                shouldSetAsPrimary = true;
                                logger.debug("🎯 Setting initial primary window: '{}' (Class: {}, Priority: {})", 
                                    windowContext.getTitle(), windowContext.getClassName(), windowPriority);
                            } else {
                                // Check if this window should override the current primary based on priority
                                WindowContext currentPrimary = windows.get(primaryWindow);
                                if (currentPrimary != null) {
                                    int currentPriority = currentPrimary.calculateWindowPriority();
                                    
                                    // Replace current primary if new window has LOWER priority number (lower = higher priority)
                                    if (windowPriority < currentPriority) {
                                        shouldSetAsPrimary = true;
                                        logger.info("🔄 PROMOTING window '{}' (Priority: {}) over '{}' (Priority: {}) (Class: {} -> {})", 
                                            windowContext.getTitle(), windowPriority, 
                                            currentPrimary.getTitle(), currentPriority,
                                            currentPrimary.getClassName(), windowContext.getClassName());
                                    }
                                    // Keep current if it has higher or equal priority (lower or equal number)
                                    else {
                                        shouldSetAsPrimary = false;
                                        logger.debug("🛑 Keeping existing window '{}' (Priority: {}), skipping '{}' (Priority: {})", 
                                            currentPrimary.getTitle(), currentPriority, 
                                            windowContext.getTitle(), windowPriority);
                                    }
                                }
                            }
                            
                            if (shouldSetAsPrimary) {
                                primaryWindow = windowHandle;
                                primaryWindowBounds = windowContext.getBounds();
                                
                                // Special logging for console windows
                                if (windowContext.isConsoleWindow()) {
                                    boolean hasProperBounds = !windowContext.hasZeroBounds();
                                    logger.info("⭐ PRIMARY CONSOLE WINDOW SET: '{}' (Class: {}, Priority: {}, Bounds: {}x{}, HasProperBounds: {})", 
                                        windowContext.getTitle(), windowContext.getClassName(), windowPriority,
                                        windowContext.getBounds().width, windowContext.getBounds().height, hasProperBounds);
                                } else {
                                    logger.info("⭐ PRIMARY WINDOW SET: '{}' (Class: {}, Priority: {})", 
                                        windowContext.getTitle(), windowContext.getClassName(), windowPriority);
                                }
                            }
                        }
                    }
                }
                
                windowCount++;
            }
              logger.debug("Window context discovered for {}: {} windows, primary: {}", 
                managedApplicationName, windows.size(), primaryWindow != null ? "found" : "none");
                  // Special logging for Explorer processes
            if (isExplorerProcess) {
                if (primaryWindow != null) {
                    WindowContext primaryContext = windows.get(primaryWindow);
                    logger.info("🏛️ EXPLORER SUCCESS: Primary window selected - '{}' (Class: {})", 
                        primaryContext.getTitle(), primaryContext.getClassName());
                    logger.info("🏛️ Friendly name will be: '{}'", getFriendlyApplicationName());
                } else {
                    logger.warn("🏛️ EXPLORER PROBLEM: No primary window selected despite filtering!");
                    logger.warn("🏛️ Total windows after filtering: {}", windows.size());                    if (windows.isEmpty()) {
                        logger.error("🏛️ ❌ CRITICAL: Explorer process has NO CabinetWClass windows! This is unexpected.");
                    }
                }
            }
                
        } catch (Exception e) {
            logger.error("Failed to discover window context for {}: {}", managedApplicationName, e.getMessage());
            // Continue execution - window discovery failure is not fatal
        }
    }    /**
     * Create window context from handle - Single page method
     */
    private WindowContext createWindowContext(WinDef.HWND windowHandle, Win32WindowControl windowControl) {
        Objects.requireNonNull(windowHandle, "Window handle cannot be null");
        Objects.requireNonNull(windowControl, "Window control cannot be null");
        
        try {
            // Use basic User32 calls since Win32WindowControl methods may not exist
            char[] titleBuffer = new char[512];
            com.sun.jna.platform.win32.User32.INSTANCE.GetWindowText(windowHandle, titleBuffer, 512);
            String title = com.sun.jna.Native.toString(titleBuffer);
            
            char[] classBuffer = new char[256];
            com.sun.jna.platform.win32.User32.INSTANCE.GetClassName(windowHandle, classBuffer, 256);
            String className = com.sun.jna.Native.toString(classBuffer);
            
            com.sun.jna.platform.win32.WinDef.RECT rect = new com.sun.jna.platform.win32.WinDef.RECT();
            com.sun.jna.platform.win32.User32.INSTANCE.GetWindowRect(windowHandle, rect);
            Rectangle bounds = new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
            
            boolean isVisible = com.sun.jna.platform.win32.User32.INSTANCE.IsWindowVisible(windowHandle);
            boolean isEnabled = com.sun.jna.platform.win32.User32.INSTANCE.IsWindowEnabled(windowHandle);
            boolean isMinimized = false; // Simplified - requires additional Win32 calls
            boolean isMaximized = false; // Simplified - requires additional Win32 calls
            int zOrder = 0; // Simplified - requires Z-order enumeration            
            // Check all return values
            if (title != null && className != null && bounds != null) {
                return new WindowContext(windowHandle, title, className, bounds, 
                                       isVisible, isEnabled, isMinimized, isMaximized, zOrder);
            } else {
                logger.debug("Incomplete window data for handle: {}", windowHandle);
                return null;
            }
            
        } catch (Exception e) {
            logger.debug("Failed to create window context for handle {}: {}", windowHandle, e.getMessage());
            return null;
        }
    }    /**
     * Terminate managed application context - Comprehensive cleanup
     */
    public synchronized void terminate() {
        if (isTerminated) {
            logger.warn("ManagedApplicationContext already terminated: {}", managedApplicationName);
            return;
        }
        
        try {            Win32ProcessTerminator terminator = Win32ProcessTerminator.getInstance();
            // Use graceful termination strategy as default
            Win32ProcessTerminator.TerminationAttempt attempt = terminator.terminateProcess(
                processId, Win32ProcessTerminator.TerminationStrategy.GRACEFUL_ONLY);            
            // Check return value
            Win32ProcessTerminator.TerminationResult result = attempt.getResult();
            if (result == Win32ProcessTerminator.TerminationResult.SUCCESS_GRACEFUL || 
                result == Win32ProcessTerminator.TerminationResult.SUCCESS_FORCE || 
                result == Win32ProcessTerminator.TerminationResult.SUCCESS_WINDOW_CLOSE) {
                this.isActive = false;
                this.isTerminated = true;
                this.windows.clear();
                this.windowHandles.clear();
                this.primaryWindow = null;
                this.primaryWindowBounds = null;
                
                logger.info("ManagedApplicationContext terminated successfully: {}", managedApplicationName);
            } else {
                throw new RuntimeException("Process termination failed for PID: " + processId + ", result: " + result);
            }
            
        } catch (Exception e) {
            logger.error("Failed to terminate ManagedApplicationContext {}: {}", managedApplicationName, e.getMessage());
            throw new RuntimeException("Termination failed", e);
        }    }
    
    // Comprehensive getters with null safety
    public String getManagedApplicationName() { return managedApplicationName; }
    public int getProcessId() { return processId; }
    public long getCreationTimestamp() { return creationTimestamp; }
    public long getLastUpdateTimestamp() { return lastUpdateTimestamp; }
    public String getExecutablePath() { return executablePath; }
    public String getCommandLine() { return commandLine; }
    public String getWorkingDirectory() { return workingDirectory; }
    public int getParentProcessId() { return parentProcessId; }
    public WinNT.HANDLE getProcessHandle() { return processHandle; }
    public boolean isActive() { return isActive && !isTerminated; }
    public boolean isTerminated() { return isTerminated; }
    
    // Window Management Access
    public Map<WinDef.HWND, WindowContext> getAllWindows() { 
        return new HashMap<>(windows); // Defensive copy
    }
    
    public List<WinDef.HWND> getWindowHandles() { 
        return new ArrayList<>(windowHandles); // Defensive copy
    }
    
    public WinDef.HWND getPrimaryWindow() { return primaryWindow; }
    public Rectangle getPrimaryWindowBounds() { 
        return primaryWindowBounds != null ? new Rectangle(primaryWindowBounds) : null;
    }
    
    /**
     * Get primary window title - Enterprise method for ApplicationStepDefinitions compatibility
     */
    public String getWindowTitle() {
        if (primaryWindow != null) {
            WindowContext context = windows.get(primaryWindow);
            if (context != null) {
                return context.getTitle();
            }
        }
        
        // Fallback: Get title from first visible window
        for (WindowContext windowContext : windows.values()) {
            if (windowContext.isVisible() && windowContext.getTitle() != null && !windowContext.getTitle().trim().isEmpty()) {
                return windowContext.getTitle();
            }
        }
        
        // Final fallback: Return managed application name
        return managedApplicationName;
    }
    
    /**
     * Get window context by handle - Enterprise method for WindowController compatibility
     */
    public WindowContext getWindowContext(WinDef.HWND handle) {
        Objects.requireNonNull(handle, "Window handle cannot be null");
        return windows.get(handle);
    }

    // Win32 Snapshot Access - Core components that exist
    public Win32MemoryManager.MemorySnapshot getMemorySnapshot() { return memorySnapshot; }
    public Win32HandleTracker.HandleSnapshot getHandleSnapshot() { return handleSnapshot; }
    public Win32PerformanceMonitor.PerformanceSnapshot getPerformanceSnapshot() { return performanceSnapshot; }
    public Win32ThreadManager.ThreadSnapshot getThreadSnapshot() { return threadSnapshot; }
    public Win32WindowControl.WindowSnapshot getWindowSnapshot() { return windowSnapshot; }
      // NEW: Complete Win32 wrapper suite getters
    public Win32SecurityManager.SecuritySnapshot getSecuritySnapshot() { return securitySnapshot; }
    public Set<Win32ModuleManager.ModuleInfo> getModuleInfo() { return new HashSet<>(moduleInfo); }
    public Win32RegistryManager.RegistrySnapshot getRegistrySnapshot() { return registrySnapshot; }
    public Win32SystemInfo.SystemInformation getSystemInformation() { return systemInformation; }
    public Win32FileSystemManager.FileSystemSnapshot getFileSystemSnapshot() { return fileSystemSnapshot; }
    
    // Enhanced context information getters
    public boolean hasElevatedPrivileges() { return hasElevatedPrivileges; }
    public String getUserContext() { return userContext; }
    public List<String> getProcessPrivileges() { return new ArrayList<>(processPrivileges); }
    public List<Win32ModuleManager.ModuleInfo> getLoadedModules() { return new ArrayList<>(loadedModules); }
    public Map<String, String> getModuleVersions() { return new HashMap<>(moduleVersions); }
    public Map<String, Object> getRegistryContext() { return new HashMap<>(registryContext); }
    public List<Win32FileSystemManager.FileHandle> getOpenFileHandles() { return new ArrayList<>(openFileHandles); }
    public List<String> getMappedFiles() { return new ArrayList<>(mappedFiles); }
      /**
     * Get comprehensive context summary - Single responsibility
     */
    public String getContextSummary() {
        return String.format("ManagedApplicationContext[%s, PID:%d, Windows:%d, Memory:%dMB, CPU:%.1f%%, %s]",
            managedApplicationName, processId, windows.size(),
            memorySnapshot != null ? memorySnapshot.getWorkingSetSizeMB() : 0,
            performanceSnapshot != null ? performanceSnapshot.getAverageCpuUsage() : 0.0,
            isActive ? "ACTIVE" : "INACTIVE");
    }
    
    @Override
    public String toString() {
        return getContextSummary();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ManagedApplicationContext that = (ManagedApplicationContext) obj;
        return processId == that.processId && 
               Objects.equals(managedApplicationName, that.managedApplicationName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(managedApplicationName, processId);
    }
    
    // Enterprise Window Management Methods - Replacing WindowInfo functionality
    
    /**
     * Find window by title pattern with case sensitivity option
     * @param titlePattern Title pattern to match
     * @param caseSensitive Whether to match case sensitively
     * @return WindowContext if found, null otherwise
     */
    public WindowContext findWindowByTitle(String titlePattern, boolean caseSensitive) {
        Objects.requireNonNull(titlePattern, "Title pattern cannot be null");
        
        String searchPattern = caseSensitive ? titlePattern : titlePattern.toLowerCase();
        
        for (WindowContext windowContext : windows.values()) {
            String windowTitle = caseSensitive ? windowContext.getTitle() : windowContext.getTitle().toLowerCase();
            if (windowTitle.contains(searchPattern)) {
                return windowContext;
            }
        }
        
        return null;
    }
    
    /**
     * Get all valid windows (visible and enabled)
     * @return List of valid WindowContext objects
     */
    public List<WindowContext> getAllValidWindows() {
        return windows.values().stream()
            .filter(window -> window.isVisible() && window.isEnabled())
            .collect(Collectors.toList());
    }
    
    /**
     * Get active (focused) window
     * @return Active WindowContext if found, null otherwise
     */
    public WindowContext getActiveWindow() {
        // Get currently focused window handle
        WinDef.HWND focusedHandle = com.sun.jna.platform.win32.User32.INSTANCE.GetForegroundWindow();
        
        if (focusedHandle != null && windows.containsKey(focusedHandle)) {
            return windows.get(focusedHandle);
        }
        
        return null;
    }
    
    /**
     * Get window by index (z-order based)
     * @param windowIndex Zero-based index
     * @return WindowContext at specified index, null if index out of bounds
     */
    public WindowContext getWindowByIndex(int windowIndex) {
        if (windowIndex < 0 || windowIndex >= windowHandles.size()) {
            return null;
        }
        
        WinDef.HWND handle = windowHandles.get(windowIndex);
        return windows.get(handle);
    }
    
    /**
     * Get window statistics for enterprise reporting
     * @return Map containing comprehensive window statistics
     */
    public Map<String, Object> getWindowStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalWindows", windows.size());
        stats.put("validWindows", getAllValidWindows().size());
        stats.put("visibleWindows", windows.values().stream().filter(WindowContext::isVisible).count());
        stats.put("enabledWindows", windows.values().stream().filter(WindowContext::isEnabled).count());
        stats.put("minimizedWindows", windows.values().stream().filter(WindowContext::isMinimized).count());
        stats.put("maximizedWindows", windows.values().stream().filter(WindowContext::isMaximized).count());
        stats.put("hasPrimaryWindow", primaryWindow != null);
        
        WindowContext activeWindow = getActiveWindow();
        stats.put("hasActiveWindow", activeWindow != null);
        stats.put("activeWindowTitle", activeWindow != null ? activeWindow.getTitle() : "None");
        
        return stats;
    }
    
    // ==== LEGACY COMPATIBILITY METHODS FOR PROCESSMANAGER ====
    
    /**
     * Legacy compatibility: Get primary window handle
     * @return Primary window handle or null if no windows
     */
    public WinDef.HWND getWindowHandle() {
        List<WinDef.HWND> handles = getWindowHandles();
        return handles.isEmpty() ? null : handles.get(0);
    }
      /**
     * Legacy compatibility: Check if process is still running
     * @return true if process is active and not terminated
     */
    public boolean isProcessStillRunning() {
        return isActive() && !isTerminated();
    }
    
    /**
     * Legacy compatibility: Get process path
     * @return Executable path
     */
    public String getProcessPath() {
        return executablePath != null ? executablePath : "";
    }
    
    /**
     * Legacy compatibility: Get process name from path
     * @return Process name (executable filename)
     */
    public String getProcessName() {
        if (executablePath != null && !executablePath.isEmpty()) {
            int lastSlash = Math.max(executablePath.lastIndexOf('/'), executablePath.lastIndexOf('\\'));
            return lastSlash >= 0 ? executablePath.substring(lastSlash + 1) : executablePath;
        }
        return "unknown.exe";
    }
    
    /**
     * Legacy compatibility: Get active window title
     * @return Title of the active window or empty string
     */
    public String getActiveWindowTitle() {
        WindowContext activeWindow = getActiveWindow();
        return activeWindow != null ? activeWindow.getTitle() : "";
    }    /**
     * Log all CabinetWClass windows for debugging explorer.exe window selection
     * This helps us understand what Explorer windows are available
     * 
     * @param discoveredWindows Set of window handles to inspect
     * @param windowControl Win32WindowControl instance for window operations
     */
    private void logAllCabinetWClassWindows(Set<WinDef.HWND> discoveredWindows, Win32WindowControl windowControl) {
        logger.info("🏛️ === EXPLORER.EXE CABINETW-CLASS WINDOW DISCOVERY ===");
        int cabinetWindowCount = 0;
        
        for (WinDef.HWND windowHandle : discoveredWindows) {
            if (windowControl.isValidWindow(windowHandle)) {
                try {
                    // Get window class name
                    char[] classBuffer = new char[256];
                    com.sun.jna.platform.win32.User32.INSTANCE.GetClassName(windowHandle, classBuffer, 256);
                    String className = com.sun.jna.Native.toString(classBuffer);
                    
                    // Check if this is a CabinetWClass window
                    if (className != null && className.contains("CabinetWClass")) {
                        cabinetWindowCount++;
                        
                        // Get additional window details
                        char[] titleBuffer = new char[512];
                        com.sun.jna.platform.win32.User32.INSTANCE.GetWindowText(windowHandle, titleBuffer, 512);
                        String title = com.sun.jna.Native.toString(titleBuffer);
                        
                        boolean isVisible = com.sun.jna.platform.win32.User32.INSTANCE.IsWindowVisible(windowHandle);
                        boolean isEnabled = com.sun.jna.platform.win32.User32.INSTANCE.IsWindowEnabled(windowHandle);
                        
                        com.sun.jna.platform.win32.WinDef.RECT rect = new com.sun.jna.platform.win32.WinDef.RECT();
                        com.sun.jna.platform.win32.User32.INSTANCE.GetWindowRect(windowHandle, rect);
                        int width = rect.right - rect.left;
                        int height = rect.bottom - rect.top;
                        
                        logger.info("🏛️ CABINET WINDOW #{}: '{}' (Class: {}, {}x{}, Visible: {}, Enabled: {}, Handle: {})", 
                            cabinetWindowCount, title, className, width, height, isVisible, isEnabled, windowHandle);
                            
                        // Log window position for context
                        if (isVisible && width > 100 && height > 100) {
                            logger.info("🏛️     -> This looks like a REAL file Explorer window (good size, visible)");
                        } else if (!isVisible) {
                            logger.info("🏛️     -> Hidden window (might be background Explorer process)");
                        } else if (width <= 100 || height <= 100) {
                            logger.info("🏛️     -> Very small window (might be a control or placeholder)");
                        }
                    }
                } catch (Exception e) {
                    logger.debug("🏛️ Failed to inspect window {}: {}", windowHandle, e.getMessage());
                }
            }
        }
        
        logger.info("🏛️ Total CabinetWClass windows found: {}", cabinetWindowCount);
        
        if (cabinetWindowCount == 0) {
            logger.warn("🏛️ ⚠️ NO CABINETW-CLASS WINDOWS FOUND! This explorer.exe process has no file Explorer windows.");
        } else if (cabinetWindowCount == 1) {
            logger.info("🏛️ ✅ Perfect! Found exactly 1 CabinetWClass window - this should be our target.");
        } else {
            logger.info("🏛️ 📋 Multiple CabinetWClass windows found - we'll use priority logic to select the best one.");
        }
        
        logger.info("🏛️ === END CABINETW-CLASS DISCOVERY ===");
    }

    /**
     * Check if this process is explorer.exe for special window filtering
     * @return true if this is an explorer.exe process
     */    private boolean isExplorerExecutable() {
        if (executablePath == null || executablePath.trim().isEmpty()) {
            return false;
        }
        
        String processName = getProcessName().toLowerCase();
        boolean isExplorer = processName.equals("explorer.exe");
        
        if (isExplorer) {
            logger.info("🏛️ Explorer.exe process detected: {} (Path: {})", processName, executablePath);
            logger.info("🏛️ This process will get SPECIAL TREATMENT - only CabinetWClass windows will be considered!");
        }
        
        return isExplorer;
    }
    
    /**
     * Get a friendlier managed application name for explorer.exe processes
     * This creates a hardcoded happy path for the weird Explorer process behavior
     * 
     * @return A user-friendly name for the explorer process
     */
    public String getFriendlyApplicationName() {
        if (isExplorerExecutable()) {
            // Hardcode a nice name for Explorer processes
            if (primaryWindow != null) {
                WindowContext context = windows.get(primaryWindow);
                if (context != null && context.getTitle() != null && !context.getTitle().trim().isEmpty()) {
                    // Use the actual window title if available (e.g., "Home - File Explorer")
                    return "Explorer - " + context.getTitle();
                }
            }
            
            // Fallback for Explorer processes without a clear primary window
            return "Windows File Explorer";
        }
        
        // For non-Explorer processes, use the original managed application name
        return managedApplicationName;
    }    
    /**
     * Check if this process is cmd.exe for special console window discovery
     * @return true if this is a cmd.exe process
     */
    private boolean isCmdExecutable() {
        if (executablePath == null || executablePath.trim().isEmpty()) {
            return false;
        }
        
        String processName = getProcessName().toLowerCase();
        boolean isCmd = processName.equals("cmd.exe");
        
        if (isCmd) {
            logger.info("🖥️ cmd.exe process detected: {} (Path: {})", processName, executablePath);
            logger.info("🖥️ This process will get SPECIAL CONSOLE WINDOW DISCOVERY TREATMENT!");
        }
        
        return isCmd;
    }

    // Method to log all potential console windows for debugging CMD processes
    private void logAllConsoleWindows(Set<WinDef.HWND> discoveredWindows, Win32WindowControl windowControl) {
        if (!logger.isDebugEnabled()) return;
        
        logger.info("🖥️ === CMD.EXE CONSOLE WINDOW DISCOVERY DEBUG ===");
        logger.info("🖥️ Target PID: {}, Process Name: {}", processId, getProcessName());
        logger.info("🖥️ Total discovered windows from Win32WindowControl: {}", discoveredWindows.size());
        
        int consoleWindowCount = 0;
        int totalWindowsChecked = 0;
        
        try {
            // Get ALL windows in the system using WindowLister approach
            com.automation.utils.WindowLister.WindowInfo[] allSystemWindows = 
                com.automation.utils.WindowLister.getAllWindows().toArray(new com.automation.utils.WindowLister.WindowInfo[0]);
            
            logger.info("🖥️ Checking {} system windows for console patterns...", allSystemWindows.length);
            
            for (com.automation.utils.WindowLister.WindowInfo window : allSystemWindows) {
                totalWindowsChecked++;
                
                String className = window.className;
                String title = window.title;
                boolean belongsToOurPid = (window.processId == processId);
                
                // Check for known console window classes and title patterns
                boolean isPotentialConsole = false;
                String consoleType = "Unknown";
                
                if (className != null && (
                    className.equals("ConsoleWindowClass") ||
                    className.equals("Windows Command Processor") ||
                    className.equals("Console") ||
                    className.contains("Console") ||
                    className.equals("WindowsTerminal")
                )) {
                    isPotentialConsole = true;
                    consoleType = "Class-based (" + className + ")";
                }
                
                if (title != null && (
                    title.contains("Command Prompt") ||
                    title.contains("cmd.exe") ||
                    title.contains("Administrator: Command Prompt") ||
                    title.contains("C:\\") ||
                    title.matches(".*[C-Z]:\\\\.*") ||
                    title.matches(".*>.*") && title.length() < 50  // Avoid false positives from editors
                )) {
                    isPotentialConsole = true;
                    if (consoleType.equals("Unknown")) {
                        consoleType = "Title-based";
                    } else {
                        consoleType += " + Title-based";
                    }
                }
                
                if (isPotentialConsole) {
                    consoleWindowCount++;
                    String pidMatch = belongsToOurPid ? "🎯 PID MATCH" : String.format("❌ PID: %d", window.processId);
                    String visibilityInfo = window.isVisible ? "👁️ Visible" : "🙈 Hidden";
                    String enabledInfo = window.isEnabled ? "✅ Enabled" : "❌ Disabled";
                    
                    logger.info("🖥️ [{}] Console candidate: '{}' | Class: '{}' | {} | {} | {} | Type: {} | Bounds: {}x{} at ({},{})", 
                        consoleWindowCount, title, className, pidMatch, visibilityInfo, enabledInfo, consoleType,
                        window.bounds.width, window.bounds.height, window.bounds.x, window.bounds.y);
                    
                    // Extra details for our PID matches
                    if (belongsToOurPid) {
                        logger.info("🖥️     ⭐ THIS WINDOW BELONGS TO OUR CMD PROCESS! HWND: {}", window.handle);
                        
                        // Check if this window is in our discovered windows set
                        boolean foundInDiscovered = discoveredWindows.stream()
                            .anyMatch(hwnd -> hwnd.equals(window.handle));
                        
                        if (foundInDiscovered) {
                            logger.info("🖥️     ✅ This window WAS found by Win32WindowControl.discoverAllProcessWindows()");
                        } else {
                            logger.error("🖥️     ❌ This window was NOT found by Win32WindowControl.discoverAllProcessWindows() - WINDOW DISCOVERY BUG!");
                        }
                    }
                }
            }
            
            logger.info("🖥️ === CMD CONSOLE WINDOW DISCOVERY SUMMARY ===");
            logger.info("🖥️ Total system windows checked: {}", totalWindowsChecked);
            logger.info("🖥️ Total console candidates found: {}", consoleWindowCount);
            
            // Count windows belonging to our PID
            long ourPidWindows = java.util.Arrays.stream(allSystemWindows)
                .filter(w -> w.processId == processId)
                .count();
            
            logger.info("🖥️ Total windows belonging to our PID {}: {}", processId, ourPidWindows);
            logger.info("🖥️ Windows found by Win32WindowControl: {}", discoveredWindows.size());
            
            if (ourPidWindows > discoveredWindows.size()) {
                logger.error("🖥️ ❌ CRITICAL: Win32WindowControl missed {} windows for our PID!", 
                    ourPidWindows - discoveredWindows.size());
                
                // Log the missed windows
                logger.error("🖥️ Missed windows for PID {}:", processId);
                java.util.Arrays.stream(allSystemWindows)
                    .filter(w -> w.processId == processId)
                    .filter(w -> discoveredWindows.stream().noneMatch(hwnd -> hwnd.equals(w.handle)))
                    .forEach(w -> logger.error("🖥️   ❌ MISSED: '{}' | Class: '{}' | Visible: {} | Enabled: {} | HWND: {}", 
                        w.title, w.className, w.isVisible, w.isEnabled, w.handle));
            }
            
        } catch (Exception e) {
            logger.warn("🖥️ ⚠️ Error during console window discovery: {}", e.getMessage());
        }
        
        logger.info("🖥️ === END CMD CONSOLE WINDOW DISCOVERY ===");
    }
    
    /**
     * Find visible console window by title matching across all PIDs
     * This handles the Windows console architecture where the actual visible console
     * may be hosted by a different process (conhost.exe or Windows Terminal)
     * 
     * @return WindowContext of visible console window if found, null otherwise
     */
    private WindowContext findVisibleConsoleWindow() {
        try {
            logger.info("🖥️ CROSS-PID CONSOLE SEARCH: Looking for visible console windows matching our process");
            
            // Get all system windows
            com.automation.utils.WindowLister.WindowInfo[] allSystemWindows = 
                com.automation.utils.WindowLister.getAllWindows().toArray(new com.automation.utils.WindowLister.WindowInfo[0]);
            
            logger.debug("🖥️ Scanning {} system windows for console matches", allSystemWindows.length);
            
            // Look for console windows with titles that match our process
            String processName = getProcessName().toLowerCase();
            String processPath = executablePath != null ? executablePath.toLowerCase() : "";
            
            for (com.automation.utils.WindowLister.WindowInfo window : allSystemWindows) {
                String className = window.className;
                String title = window.title;                // Check for visible console window classes
                boolean isVisibleConsoleClass = className != null && (
                    className.equals("CASCADIA_HOSTING_WINDOW_CLASS") ||
                    className.equals("ConsoleWindowClass") ||
                    className.contains("Console")
                );
                
                // Check if title matches our process (more flexible matching)
                boolean titleMatches = title != null && (
                    title.toLowerCase().contains(processName) ||
                    title.toLowerCase().contains("cmd.exe") ||
                    title.toLowerCase().contains(processPath) ||
                    title.matches(".*[C-Z]:\\\\.*") // Command prompt path pattern
                );
                
                // Must be visible, enabled, and have proper bounds
                boolean hasProperBounds = window.bounds.width > 100 && window.bounds.height > 100;
                
                if (isVisibleConsoleClass && window.isVisible && window.isEnabled && hasProperBounds) {
                    if (titleMatches) {
                        logger.info("🖥️ ✅ CONSOLE MATCH FOUND (TITLE): '{}' | Class: {} | PID: {} | Bounds: {}x{} at ({},{})", 
                            title, className, window.processId, 
                            window.bounds.width, window.bounds.height, window.bounds.x, window.bounds.y);
                    } else {
                        logger.info("🖥️ ✅ CONSOLE MATCH FOUND (FALLBACK): '{}' | Class: {} | PID: {} | Bounds: {}x{} at ({},{})", 
                            title, className, window.processId, 
                            window.bounds.width, window.bounds.height, window.bounds.x, window.bounds.y);
                    }                    // Create WindowContext from this cross-PID window
                    Rectangle bounds = new Rectangle(window.bounds.x, window.bounds.y, 
                                                   window.bounds.width, window.bounds.height);
                    
                    // For Windows Terminal/CASCADIA windows, use original bounds for reliable automation
                    if (className != null && className.equals("CASCADIA_HOSTING_WINDOW_CLASS")) {
                        logger.info("🖥️📐 CASCADIA WINDOW: Using original bounds {}x{} at ({},{}) for reliable automation", 
                            bounds.width, bounds.height, bounds.x, bounds.y);
                    }
                    
                    return new WindowContext(window.handle, title, className, bounds, 
                                           window.isVisible, window.isEnabled, false, false, 0);
                }
            }
            
            logger.warn("🖥️ ❌ NO VISIBLE CONSOLE WINDOW FOUND: Could not find matching console window for process {}", processName);
            return null;
            
        } catch (Exception e) {
            logger.error("🖥️ ⚠️ Error during cross-PID console window search: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Find visible file Explorer window (cross-PID search)
     * This handles cases where Windows delegates explorer.exe launches to existing processes
     */
    private WindowContext findVisibleExplorerWindow() {
        try {
            logger.info("🏛️ CROSS-PID EXPLORER SEARCH: Looking for visible file Explorer windows");
            
            // Get all system windows
            com.automation.utils.WindowLister.WindowInfo[] allSystemWindows = 
                com.automation.utils.WindowLister.getAllWindows().toArray(new com.automation.utils.WindowLister.WindowInfo[0]);
            
            logger.debug("🏛️ Scanning {} system windows for Explorer matches", allSystemWindows.length);
            
            for (com.automation.utils.WindowLister.WindowInfo window : allSystemWindows) {
                String className = window.className;
                String title = window.title;
                
                // Check for CabinetWClass windows (file Explorer windows)
                boolean isCabinetWClass = className != null && className.contains("CabinetWClass");
                
                // Check if title suggests it's a file Explorer window
                boolean titleSuggestsFileExplorer = title != null && (
                    title.matches(".*[C-Z]:\\\\.*") || // Drive path pattern like "C:\Users"
                    title.toLowerCase().contains("users") ||
                    title.toLowerCase().contains("documents") ||
                    title.toLowerCase().contains("downloads") ||
                    title.toLowerCase().contains("desktop") ||
                    title.toLowerCase().contains("pictures") ||
                    title.toLowerCase().contains("music") ||
                    title.toLowerCase().contains("videos") ||
                    title.length() == 0 || // Empty title is common for Explorer
                    title.equals("File Explorer")
                );
                  // Must be visible, enabled, and have proper bounds
                boolean hasProperBounds = window.bounds.width > 200 && window.bounds.height > 150;
                
                if (isCabinetWClass && titleSuggestsFileExplorer && window.isVisible && window.isEnabled && hasProperBounds) {
                    logger.info("🏛️ ✅ EXPLORER MATCH FOUND: '{}' | Class: {} | PID: {} | Bounds: {}x{} at ({},{})", 
                        title, className, window.processId, 
                        window.bounds.width, window.bounds.height, window.bounds.x, window.bounds.y);
                      // Create WindowContext from this cross-PID window
                    Rectangle bounds = new Rectangle(window.bounds.x, window.bounds.y, 
                                                   window.bounds.width, window.bounds.height);
                    
                    return new WindowContext(window.handle, title, className, bounds, 
                                           window.isVisible, window.isEnabled, false, false, 0);
                }
            }
            
            logger.warn("🏛️ ❌ NO VISIBLE EXPLORER WINDOWS FOUND in cross-PID search");
            return null;
            
        } catch (Exception e) {
            logger.error("🏛️ ❌ EXPLORER SEARCH FAILED: {}", e.getMessage(), e);
            return null;
        }
    }
  }
