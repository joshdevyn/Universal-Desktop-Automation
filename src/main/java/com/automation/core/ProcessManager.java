package com.automation.core;

import com.automation.core.win32.*;
import com.automation.models.ManagedApplicationContext;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ARCHITECTURE: ManagedApplicationName (String) ↔ ManagedApplicationContext (Backend Model)
 * INTEGRATION: Complete Win32 wrapper suite for enterprise-grade capabilities
 */
public class ProcessManager {
    private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);
    private static volatile ProcessManager instance;
    
    // Multi-instance process tracking with indexing
    private final Map<String, List<ManagedProcess>> managedProcesses = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> processCounters = new ConcurrentHashMap<>();
    private final Map<String, Integer> activeProcessIndices = new ConcurrentHashMap<>();
      // Store original launch information for multi-instance consistency
    private final Map<String, LaunchInfo> originalLaunchInfo = new ConcurrentHashMap<>();
      // Simple control flow - Private constructor for singleton
    private ProcessManager() {
        logger.info("ProcessManager initialized");
    }
    
    // Check return value - Validated singleton pattern
    public static ProcessManager getInstance() {
        if (instance == null) {
            synchronized (ProcessManager.class) {
                if (instance == null) {
                    instance = new ProcessManager();
                    // Add shutdown hook for cleanup on JVM exit
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        logger.info("JVM shutdown: Cleaning up all managed processes...");
                        instance.forceCleanupAllProcesses();
                    }, "ProcessManager-Cleanup"));
                }
            }
        }
        return instance;
    }    
    
    /**
     * Single page function - Launch application by path
     * Minimum two assertions per function
     */
    public ManagedApplicationContext launchByPath(String executablePath, String applicationName) {
        // Parameter validation assertions
        if (executablePath == null || executablePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Executable path cannot be null or empty");
        }
        if (applicationName == null || applicationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Application name cannot be null or empty");
        }
        
        logger.info("Launching application by path: '{}' from '{}'", applicationName, executablePath);
        
        try {
            // Restrict data scope - local variables
            String resolvedPath = resolveExecutablePath(executablePath);
            if (resolvedPath == null) {
                logger.error("Launch failed: Executable not found: '{}'", executablePath);
                return null;
            }
            
            LaunchInfo launchInfo = createLaunchInfoFromPath(resolvedPath, applicationName);
            if (launchInfo == null) {
                logger.error("Launch failed: Unsupported executable type: '{}'", executablePath);
                return null;
            }
            
            return executeLaunch(applicationName, launchInfo);
            
        } catch (Exception e) {
            logger.error("Launch exception: Failed to launch '{}': {}", applicationName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Launch application by executable path with command line arguments
     * Single page function
     * Minimum two assertions per function
     */
    public ManagedApplicationContext launchByPathWithArguments(String executablePath, String arguments, String applicationName) {
        // Parameter validation assertions
        if (executablePath == null || executablePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Executable path cannot be null or empty");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (applicationName == null || applicationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Application name cannot be null or empty");
        }
        
        logger.info("Launching application by path: '{}' with arguments '{}' from '{}'", applicationName, arguments, executablePath);
        
        try {
            // Restrict data scope - local variables
            String resolvedPath = resolveExecutablePath(executablePath);
            if (resolvedPath == null) {
                logger.error("Launch failed: Executable not found: '{}'", executablePath);
                return null;
            }
            
            LaunchInfo launchInfo = createLaunchInfoFromPathWithArguments(resolvedPath, arguments, applicationName);
            if (launchInfo == null) {
                logger.error("Launch failed: Unsupported executable type: '{}'", executablePath);
                return null;
            }
            
            return executeLaunch(applicationName, launchInfo);
            
        } catch (Exception e) {
            logger.error("Launch exception: Failed to launch '{}' with arguments '{}': {}", applicationName, arguments, e.getMessage(), e);
            return null;
        }
    }
      /**
     * Single page function - Execute launch process
     */
    private ManagedApplicationContext executeLaunch(String applicationName, LaunchInfo launchInfo) {
        // Parameter validation
        Objects.requireNonNull(applicationName, "Application name cannot be null");
        Objects.requireNonNull(launchInfo, "Launch info cannot be null");
        
        try {
            Process process = launchProcess(launchInfo);
            if (process == null) {
                logger.error("Launch failed: Could not start process for '{}'", applicationName);
                return null;
            }
              ManagedApplicationContext processInfo = waitForWindowAndCreateProcessInfo(
                launchInfo.expectedWindowTitle, 
                launchInfo.expectedProcessName, 
                process, 
                30 // Fixed timeout bound
            );
            
            if (processInfo != null) {
                return registerSuccessfulLaunch(applicationName, process, processInfo, launchInfo);
            } else {
                cleanupFailedLaunch(process, applicationName);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("Execute launch failed for '{}': {}", applicationName, e.getMessage());
            return null;
        }
    }    /**
     * Launch application and return ManagedApplicationContext with actual process tracking (legacy method)
     */
    public ManagedApplicationContext launchAndTrack(String applicationName) {
        logger.info("Launching application: '{}'", applicationName);
        
        try {
            // Check if we have original launch info from a previous direct path launch
            LaunchInfo storedLaunchInfo = originalLaunchInfo.get(applicationName);
            if (storedLaunchInfo != null) {
                logger.info("Using stored launch info: Consistent multi-instance launch for '{}'", applicationName);
                return launchByPath(storedLaunchInfo.executablePath, applicationName);
            }
            
            // Fallback: Determine launch strategy if no stored info available
            LaunchInfo launchInfo = determineLaunchStrategy(applicationName);
            if (launchInfo == null) {
                logger.error("Launch failed: No valid launch strategy for '{}'", applicationName);
                return null;
            }
              return launchByPath(launchInfo.executablePath, applicationName);
            
        } catch (Exception e) {
            logger.error("Launch exception: Failed to launch '{}': {}", applicationName, e.getMessage(), e);
            return null;
        }
    }    // ==== ENTERPRISE ARCHITECTURE: ManagedApplicationContext Unification ====
    
    // New tracking for unified architecture
    private final Map<String, List<ManagedApplicationContext>> trackedContexts = new ConcurrentHashMap<>();
    private final Map<String, Integer> activeContextIndices = new ConcurrentHashMap<>();
    
    /**
     * ENTERPRISE: Get running application context by managed application name
     * 
     * COMPLETE LAUNCHER DETECTION: Handles calc.exe → CalculatorApp.exe delegation
     * MULTI-WINDOW MANAGEMENT: Tracks all windows per process with seamless switching
     * UWP/MODERN APP SUPPORT: Handles Universal Windows Platform applications
     * 
     * @param managedApplicationName The managed application name from .feature files
     * @return ManagedApplicationContext for the running process, or null if not found
     */
    public ManagedApplicationContext getRunningApplicationContext(String managedApplicationName) {
        Objects.requireNonNull(managedApplicationName, "Managed application name cannot be null");
        
        // Check tracked contexts first
        List<ManagedApplicationContext> contexts = trackedContexts.get(managedApplicationName);
        if (contexts != null && !contexts.isEmpty()) {
            for (ManagedApplicationContext context : contexts) {
                if (context.isActive() && !context.isTerminated()) {
                    logger.debug("Found active context for {}: PID {}", managedApplicationName, context.getProcessId());
                    return context;
                }
            }
        }
        
        // INTELLIGENT LAUNCHER DETECTION - scan for delegate processes
        ManagedApplicationContext launcherContext = detectLauncherDelegation(managedApplicationName);
        if (launcherContext != null) {
            logger.info("LAUNCHER DETECTED: {} → PID {}", managedApplicationName, launcherContext.getProcessId());
            // Register the detected context for future tracking
            trackedContexts.computeIfAbsent(managedApplicationName, k -> new ArrayList<>()).add(launcherContext);
            return launcherContext;
        }
        
        logger.warn("No running context found for managed application: {}", managedApplicationName);
        return null;
    }
    
    /**
     * ENTERPRISE: Launch application and return ManagedApplicationContext
     * 
     * UNIFIED ARCHITECTURE: Returns ManagedApplicationContext instead of ProcessInfo
     * COMPREHENSIVE TRACKING: Includes Win32 wrapper suite integration
     * 
     * @param managedApplicationName The managed application name
     * @return ManagedApplicationContext with complete kernel context
     */
    public ManagedApplicationContext launchApplicationContext(String managedApplicationName) {
        Objects.requireNonNull(managedApplicationName, "Managed application name cannot be null");
        
        logger.info("ENTERPRISE LAUNCH: Starting application '{}'", managedApplicationName);
        
        try {            // Use existing legacy launch logic to get ManagedApplicationContext
            ManagedApplicationContext legacyProcessInfo = launchAndTrack(managedApplicationName);
            if (legacyProcessInfo == null) {
                logger.error("Launch failed: No ManagedApplicationContext returned for '{}'", managedApplicationName);
                return null;
            }
            
            // Convert ManagedApplicationContext to ManagedApplicationContext
            ManagedApplicationContext context = convertToManagedApplicationContext(
                managedApplicationName, legacyProcessInfo);
            
            if (context != null) {
                // Track the context
                trackedContexts.computeIfAbsent(managedApplicationName, k -> new ArrayList<>()).add(context);
                activeContextIndices.put(managedApplicationName, 0); // First instance is active
                
                logger.info("ENTERPRISE SUCCESS: Application '{}' launched - PID: {}, Windows: {}", 
                    managedApplicationName, context.getProcessId(), context.getAllWindows().size());
                return context;
            } else {
                logger.error("Context conversion failed for '{}'", managedApplicationName);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("ENTERPRISE LAUNCH FAILED: Application '{}' - {}", managedApplicationName, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * ENTERPRISE: Intelligent launcher detection for calc.exe → CalculatorApp.exe patterns
     * 
     * LAUNCHER PATTERNS:
     * - calc.exe → CalculatorApp.exe (Windows 10+ Calculator)
     * - notepad.exe → TextInputHost.exe (Modern Notepad)
     * - mspaint.exe → PaintStudio3D.exe (Modern Paint)
     * 
     * @param managedApplicationName The original application name
     * @return ManagedApplicationContext for the delegated process, or null if not found
     */
    private ManagedApplicationContext detectLauncherDelegation(String managedApplicationName) {
        try {
            // Define launcher delegation patterns
            Map<String, String[]> launcherPatterns = new HashMap<>();
            launcherPatterns.put("calculator", new String[]{"CalculatorApp.exe", "Calculator.exe", "calc.exe"});
            launcherPatterns.put("notepad", new String[]{"Notepad.exe", "TextInputHost.exe", "notepad.exe"});
            launcherPatterns.put("paint", new String[]{"PaintStudio3D.exe", "mspaint.exe"});
            launcherPatterns.put("file_explorer", new String[]{"explorer.exe"});
            launcherPatterns.put("explorer", new String[]{"explorer.exe"});
            
            String appKey = managedApplicationName.toLowerCase().replace("_", "");
            String[] patterns = launcherPatterns.get(appKey);
            
            if (patterns == null) {
                return null; // No known delegation patterns
            }
            
            // Search for delegate processes in priority order
            for (String processPattern : patterns) {
                List<Integer> pids = findProcessesByName(processPattern);
                for (Integer pid : pids) {
                    // Create ManagedApplicationContext from found PID
                    ManagedApplicationContext context = createContextFromPID(managedApplicationName, pid);
                    if (context != null && context.isActive()) {
                        logger.info("DELEGATION DETECTED: {} → {} (PID: {})", 
                            managedApplicationName, processPattern, pid);
                        return context;
                    }
                }
            }
            
            return null; // No delegate found
            
        } catch (Exception e) {
            logger.warn("Launcher detection failed for '{}': {}", managedApplicationName, e.getMessage());
            return null;
        }
    }
    
    /**
     * Find processes by name pattern using system calls
     */
    private List<Integer> findProcessesByName(String processName) {
        List<Integer> pids = new ArrayList<>();
        
        try {
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", 
                "name='" + processName + "'", "get", "ProcessId", "/format:csv");
            Process proc = pb.start();
            
            try (InputStream inputStream = proc.getInputStream()) {
                byte[] buffer = new byte[4096];
                StringBuilder output = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }
                
                String[] lines = output.toString().split("\\r?\\n");
                for (String line : lines) {
                    if (line.contains(",") && !line.contains("Node") && !line.contains("ProcessId")) {
                        try {
                            String[] parts = line.split(",");
                            if (parts.length >= 2) {
                                String pidStr = parts[1].replace("\"", "").trim();
                                if (!pidStr.isEmpty()) {
                                    int pid = Integer.parseInt(pidStr);
                                    pids.add(pid);
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid PID
                        }
                    }
                }
            }
            
            proc.waitFor(3, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            logger.debug("Failed to find processes by name '{}': {}", processName, e.getMessage());
        }
        
        return pids;
    }    /**
     * Create ManagedApplicationContext from PID with comprehensive Win32 integration
     */
    public ManagedApplicationContext createContextFromPID(String managedApplicationName, int pid) {
        try {
            // Create ManagedApplicationContext with full Win32 integration
            ManagedApplicationContext context = new ManagedApplicationContext(managedApplicationName, pid);
            
            // Validate the context is active and not terminated
            if (context.isActive() && !context.isTerminated()) {
                // CRITICAL FIX: Register the context in tracking system so getRunningApplicationContext can find it
                trackedContexts.computeIfAbsent(managedApplicationName, k -> new ArrayList<>()).add(context);
                activeContextIndices.put(managedApplicationName, 0); // Set as active context
                
                logger.info("Created and registered context for {} from PID {}: {} windows", 
                    managedApplicationName, pid, context.getAllWindows().size());
                return context;
            } else {
                logger.debug("Context validation failed for PID {}: active={}, terminated={}", 
                    pid, context.isActive(), context.isTerminated());
                return null;
            }
            
        } catch (Exception e) {
            logger.debug("Failed to create context from PID {}: {}", pid, e.getMessage());
            return null;
        }
    }
      /**
     * Convert legacy ProcessInfo to ManagedApplicationContext
     */
    private ManagedApplicationContext convertToManagedApplicationContext(String managedApplicationName, ManagedApplicationContext processInfo) {
        // Input is already ManagedApplicationContext, just return it
        return processInfo;
    }
    
    /**
     * ENTERPRISE: Multi-window management - Switch to specific window by index
     * 
     * @param managedApplicationName The managed application name
     * @param windowIndex The window index (0-based)
     * @return true if window switch was successful
     */
    public boolean switchToWindowByIndex(String managedApplicationName, int windowIndex) {
        ManagedApplicationContext context = getRunningApplicationContext(managedApplicationName);
        if (context == null) {
            logger.warn("Cannot switch window: No context found for '{}'", managedApplicationName);
            return false;
        }
        
        Map<WinDef.HWND, ManagedApplicationContext.WindowContext> windows = context.getAllWindows();
        if (windows.size() <= windowIndex) {
            logger.warn("Cannot switch window: Index {} out of range for '{}' (has {} windows)", 
                windowIndex, managedApplicationName, windows.size());
            return false;
        }
        
        // Get the window at the specified index
        List<WinDef.HWND> windowHandles = new ArrayList<>(windows.keySet());
        WinDef.HWND targetHandle = windowHandles.get(windowIndex);
        
        try {
            // Use Win32WindowControl to activate the window
            Win32WindowControl windowControl = Win32WindowControl.getInstance();
            boolean success = windowControl.activateWindow(targetHandle);
            
            if (success) {
                logger.info("WINDOW SWITCH SUCCESS: Application '{}' switched to window [{}]", 
                    managedApplicationName, windowIndex);
                return true;
            } else {
                logger.warn("Window activation failed for '{}' window [{}]", managedApplicationName, windowIndex);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Window switch failed for '{}' window [{}]: {}", 
                managedApplicationName, windowIndex, e.getMessage());
            return false;
        }    }
    
    /**
     * ENTERPRISE: Switch to specific instance of application using ManagedApplicationContext
     * 
     * @param managedApplicationName The managed application name
     * @param instanceIndex The instance index (0-based)
     * @return ManagedApplicationContext for the switched instance, or null if not found/failed
     */
    public ManagedApplicationContext switchToApplicationInstance(String managedApplicationName, int instanceIndex) {
        logger.info("🔄 ENTERPRISE INSTANCE SWITCH: Switching to instance {} of '{}'", instanceIndex, managedApplicationName);
        
        try {
            // Get all tracked contexts for this application
            List<ManagedApplicationContext> contexts = trackedContexts.get(managedApplicationName);
            if (contexts == null || contexts.isEmpty()) {
                logger.warn("No tracked contexts found for managed application: {}", managedApplicationName);
                return null;
            }
            
            // Find context by instance index (simulate instance tracking)
            if (instanceIndex < 0 || instanceIndex >= contexts.size()) {
                logger.warn("Instance index {} out of range for '{}' (has {} instances)", 
                    instanceIndex, managedApplicationName, contexts.size());
                return null;
            }
            
            ManagedApplicationContext targetContext = contexts.get(instanceIndex);
            if (targetContext == null || targetContext.isTerminated() || !targetContext.isActive()) {
                logger.warn("Target instance {} for '{}' is not active or terminated", instanceIndex, managedApplicationName);
                return null;
            }
            
            // Use WindowController to focus the context
            WindowController windowController = new WindowController();
            boolean focused = windowController.focusWindow(targetContext);
            
            if (focused) {
                windowController.bringToForeground();
                activeContextIndices.put(managedApplicationName, instanceIndex);
                
                logger.info("✅ ENTERPRISE SWITCHED: To instance {} of '{}' - PID: {}", 
                    instanceIndex, managedApplicationName, targetContext.getProcessId());
                return targetContext;
            } else {
                logger.warn("Failed to focus instance {} of '{}'", instanceIndex, managedApplicationName);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("💥 ENTERPRISE INSTANCE SWITCH EXCEPTION: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * ENTERPRISE: Verify that specific instance of application is active
     * 
     * @param managedApplicationName The managed application name
     * @param instanceIndex The instance index (0-based)
     * @return true if instance is active, false otherwise
     */
    public boolean isApplicationInstanceActive(String managedApplicationName, int instanceIndex) {
        try {
            List<ManagedApplicationContext> contexts = trackedContexts.get(managedApplicationName);
            if (contexts == null || instanceIndex < 0 || instanceIndex >= contexts.size()) {
                return false;
            }
            
            ManagedApplicationContext context = contexts.get(instanceIndex);
            return context != null && context.isActive() && !context.isTerminated();
            
        } catch (Exception e) {
            logger.debug("Failed to check instance {} for '{}': {}", instanceIndex, managedApplicationName, e.getMessage());
            return false;
        }
    }
    
    /**
     * ENTERPRISE: Get all application contexts for a managed application
     * 
     * @param managedApplicationName The managed application name
     * @return List of all ManagedApplicationContext instances for the application
     */
    public List<ManagedApplicationContext> getAllApplicationContexts(String managedApplicationName) {
        Objects.requireNonNull(managedApplicationName, "Managed application name cannot be null");
        
        try {
            List<ManagedApplicationContext> contexts = trackedContexts.get(managedApplicationName);
            if (contexts == null) {
                logger.debug("No contexts found for managed application: {}", managedApplicationName);
                return new ArrayList<>();
            }
            
            // Filter out terminated or inactive contexts
            List<ManagedApplicationContext> activeContexts = contexts.stream()
                .filter(context -> context != null && context.isActive() && !context.isTerminated())
                .collect(ArrayList::new, (list, context) -> list.add(context), ArrayList::addAll);
                
            logger.debug("Found {} active contexts for managed application: {}", activeContexts.size(), managedApplicationName);
            return activeContexts;
            
        } catch (Exception e) {
            logger.error("Failed to get all contexts for '{}': {}", managedApplicationName, e.getMessage());
            return new ArrayList<>();
        }    }

    /**
     * ENTERPRISE: Launch application and track it for enterprise step definitions
     * This method provides compatibility with step definitions while maintaining unified architecture
     * 
     * @param managedApplicationName The managed application name
     * @return ManagedApplicationContext with complete tracking
     */
    public ManagedApplicationContext launchAndTrackApplication(String managedApplicationName) {
        return launchApplicationContext(managedApplicationName);
    }

    // ==== LEGACY COMPATIBILITY METHODS (DEPRECATED) ====
    // These methods maintain backward compatibility while the architecture transitions
      /**
     * @deprecated Use getRunningApplicationContext() instead
     */
    @Deprecated
    public ManagedApplicationContext getRunningProcess(String applicationName) {
        List<ManagedProcess> processes = managedProcesses.get(applicationName);
        if (processes == null || processes.isEmpty()) {
            return null;
        }
        
        // Get active instance
        Integer activeIndex = activeProcessIndices.get(applicationName);
        if (activeIndex != null) {
            ManagedProcess activeProcess = findProcessByIndex(processes, activeIndex);
            if (activeProcess != null && activeProcess.isStillRunning()) {
                return activeProcess.processInfo;
            }
        }
        
        // Clean up dead processes and find first alive one
        processes.removeIf(p -> !p.isStillRunning());
        if (!processes.isEmpty()) {
            ManagedProcess firstAlive = processes.get(0);
            activeProcessIndices.put(applicationName, firstAlive.instanceIndex);
            return firstAlive.processInfo;
        }
          // No alive processes
        managedProcesses.remove(applicationName);
        activeProcessIndices.remove(applicationName);
        processCounters.remove(applicationName);
        originalLaunchInfo.remove(applicationName); // Clear stored launch info
        return null;
    }
      /**
     * Switch to specific instance of application
     */
    public ManagedApplicationContext switchToInstance(String applicationName, int instanceIndex) {
        List<ManagedProcess> processes = managedProcesses.get(applicationName);
        if (processes == null || processes.isEmpty()) {
            logger.warn("Switch failed: No instances found for application '{}'", applicationName);
            return null;
        }
        
        ManagedProcess targetProcess = findProcessByIndex(processes, instanceIndex);
        if (targetProcess == null) {
            logger.warn("Switch failed: Instance [{}] not found for application '{}'", instanceIndex, applicationName);
            return null;
        }
        
        if (!targetProcess.isStillRunning()) {
            logger.warn("Switch failed: Instance [{}] is no longer running for application '{}'", instanceIndex, applicationName);
            processes.remove(targetProcess);
            return null;
        }
        
        // Set as active instance
        activeProcessIndices.put(applicationName, instanceIndex);        // Focus the window
        WindowController windowController = new WindowController();
        try {
            // Try to focus using window handle directly
            if (targetProcess.processInfo.getWindowHandle() != null) {
                windowController.setCurrentWindow(targetProcess.processInfo.getWindowHandle());
                windowController.activateWindow();                logger.info("Switched: Application '{}' to instance [{}] - PID: {}", 
                    applicationName, instanceIndex, targetProcess.processInfo.getProcessId());
                return targetProcess.processInfo;
            } else {
                logger.warn("Switch partial: Instance [{}] found but no window handle for '{}'", instanceIndex, applicationName);
                return targetProcess.processInfo;
            }
        } catch (Exception e) {            logger.warn("Switch partial: Instance [{}] found but could not focus window for '{}': {}", 
                instanceIndex, applicationName, e.getMessage());
            return targetProcess.processInfo;
        }
    }
      /**
     * Get all running instances for an application
     */
    public List<ManagedApplicationContext> getAllInstances(String applicationName) {
        List<ManagedProcess> processes = managedProcesses.get(applicationName);
        if (processes == null) {
            return new ArrayList<>();
        }
        
        // Clean up dead processes
        processes.removeIf(p -> !p.isStillRunning());
        
        // Return ProcessInfo for all alive instances
        return processes.stream()
            .map(p -> p.processInfo)
            .collect(ArrayList::new, (list, processInfo) -> list.add(processInfo), ArrayList::addAll);
    }
    
    /**
     * Get instance count for application
     */
    public int getInstanceCount(String applicationName) {
        List<ManagedProcess> processes = managedProcesses.get(applicationName);
        if (processes == null) {
            return 0;
        }
        
        // Clean up dead processes
        processes.removeIf(p -> !p.isStillRunning());
        return processes.size();
    }
      /**
     * Terminate active instance of specific application
     */
    public boolean terminateApplication(String applicationName) {        List<ManagedProcess> processes = managedProcesses.get(applicationName);
        if (processes == null || processes.isEmpty()) {
            logger.warn("Terminate failed: No instances found for application '{}'", applicationName);
            return false;
        }
        
        // Get active instance
        Integer activeIndex = activeProcessIndices.get(applicationName);
        if (activeIndex != null) {
            ManagedProcess activeProcess = findProcessByIndex(processes, activeIndex);
            if (activeProcess != null) {
                boolean terminated = activeProcess.terminate();
                processes.remove(activeProcess);
                  // If no more instances, clean up completely
                if (processes.isEmpty()) {
                    managedProcesses.remove(applicationName);
                    activeProcessIndices.remove(applicationName);
                    processCounters.remove(applicationName);
                    originalLaunchInfo.remove(applicationName); // Clear stored launch info
                } else {
                    // Set next available instance as active
                    activeProcessIndices.put(applicationName, processes.get(0).instanceIndex);
                }
                  logger.info("Terminated: Application '{}' instance [{}] - Success: {}", 
                    applicationName, activeIndex, terminated);
                return terminated;
            }
        }
        
        // No active instance found, terminate first available
        if (!processes.isEmpty()) {
            ManagedProcess firstProcess = processes.get(0);
            boolean terminated = firstProcess.terminate();
            processes.remove(firstProcess);
              if (processes.isEmpty()) {
                managedProcesses.remove(applicationName);
                activeProcessIndices.remove(applicationName);
                processCounters.remove(applicationName);
                originalLaunchInfo.remove(applicationName); // Clear stored launch info
            } else {
                activeProcessIndices.put(applicationName, processes.get(0).instanceIndex);
            }
              logger.info("Terminated: Application '{}' instance [{}] - Success: {}", 
                applicationName, firstProcess.instanceIndex, terminated);
            return terminated;
        }
        
        return false;
    }
    
    /**
     * Terminate specific instance of application
     */
    public boolean terminateInstance(String applicationName, int instanceIndex) {
        List<ManagedProcess> processes = managedProcesses.get(applicationName);        if (processes == null || processes.isEmpty()) {
            logger.warn("Terminate failed: No instances found for application '{}'", applicationName);
            return false;
        }
        
        ManagedProcess targetProcess = findProcessByIndex(processes, instanceIndex);
        if (targetProcess == null) {
            logger.warn("Terminate failed: Instance [{}] not found for application '{}'", instanceIndex, applicationName);
            return false;
        }
        
        boolean terminated = targetProcess.terminate();
        processes.remove(targetProcess);
          // Update active instance if we terminated it
        Integer activeIndex = activeProcessIndices.get(applicationName);
        if (activeIndex != null && activeIndex == instanceIndex) {
            if (!processes.isEmpty()) {
                activeProcessIndices.put(applicationName, processes.get(0).instanceIndex);
            } else {
                activeProcessIndices.remove(applicationName);
            }
        }
          // Clean up if no more instances
        if (processes.isEmpty()) {
            managedProcesses.remove(applicationName);
            activeProcessIndices.remove(applicationName);
            processCounters.remove(applicationName);
            originalLaunchInfo.remove(applicationName); // Clear stored launch info
        }
          logger.info("Terminated: Application '{}' instance [{}] - Success: {}", 
            applicationName, instanceIndex, terminated);
        return terminated;
    }
    
    /**
     * Terminate ALL instances of specific application
     */
    public boolean terminateAllInstances(String applicationName) {
        List<ManagedProcess> processes = managedProcesses.get(applicationName);
        if (processes == null || processes.isEmpty()) {
            logger.warn("Terminate all failed: No instances found for application '{}'", applicationName);
            return false;
        }
        
        int terminatedCount = 0;
        int totalCount = processes.size();
        
        // Terminate all instances
        for (ManagedProcess process : new ArrayList<>(processes)) {
            if (process.terminate()) {
                terminatedCount++;
            }
        }
          // Clean up completely
        managedProcesses.remove(applicationName);
        activeProcessIndices.remove(applicationName);
        processCounters.remove(applicationName);
        originalLaunchInfo.remove(applicationName); // Clear stored launch info
          logger.info("Terminated all: Application '{}' - {}/{} instances terminated", 
            applicationName, terminatedCount, totalCount);
        return terminatedCount == totalCount;
    }    /**
     * Clean up all managed processes - CRITICAL for test cleanup
     */
    public void terminateAll() {
        logger.info("Cleanup: Terminating all managed processes...");
        
        int totalTerminated = 0;
        int totalProcesses = 0;
        
        // Copy to avoid concurrent modification
        Map<String, List<ManagedProcess>> processesToTerminate = new HashMap<>(managedProcesses);
          for (Map.Entry<String, List<ManagedProcess>> entry : processesToTerminate.entrySet()) {
            String appName = entry.getKey();
            List<ManagedProcess> processes = new ArrayList<>(entry.getValue()); // Copy list to avoid concurrent modification
            
            // System protection: Skip terminating system-critical applications
            if (isSystemCriticalApplication(appName)) {
                logger.warn("System protection: Skipping termination of system-critical application: '{}'", appName);
                // Remove from tracking but don't terminate the process
                managedProcesses.remove(appName);
                activeProcessIndices.remove(appName);
                processCounters.remove(appName);
                originalLaunchInfo.remove(appName);
                continue;
            }
            
            totalProcesses += processes.size();
            
            logger.info("Terminating application: '{}' - {} instances", appName, processes.size());
            
            for (ManagedProcess process : processes) {                try {
                    logger.info("Terminating: Application '{}' instance [{}] - PID: {}", 
                        appName, process.instanceIndex, process.processInfo.getProcessId());
                        
                    if (process.terminate()) {
                        totalTerminated++;                        logger.info("Cleanup successful: Application '{}' instance [{}] - Terminated successfully", 
                            appName, process.instanceIndex);
                    } else {                        logger.warn("Cleanup failed: Application '{}' instance [{}] - Termination failed, process may be orphaned", 
                            appName, process.instanceIndex);
                    }
                } catch (Exception e) {                    logger.warn("Cleanup warning: Failed to terminate '{}' instance [{}]: {}", 
                        appName, process.instanceIndex, e.getMessage());
                }
            }
        }
          // Clean up all tracking data
        managedProcesses.clear();
        activeProcessIndices.clear();
        processCounters.clear();
        originalLaunchInfo.clear(); // Clear all stored launch info
          logger.info("Cleanup complete: {}/{} processes terminated", totalTerminated, totalProcesses);
        
        // Additional verification: Check for any orphaned processes
        if (totalTerminated < totalProcesses) {
            logger.warn("Orphan check: Some processes may not have terminated properly. Running system-level cleanup...");
            
            try {
                // Kill any remaining cmd.exe processes that might be orphaned
                ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", 
                    "Get-Process -Name cmd -ErrorAction SilentlyContinue | Where-Object {$_.StartTime -gt (Get-Date).AddMinutes(-5)} | Stop-Process -Force");
                pb.start().waitFor(3, TimeUnit.SECONDS);
                
                // Kill any remaining explorer.exe processes we may have launched (be careful here)
                // We'll skip this to avoid killing the main Windows Explorer
                
                logger.info("Orphan cleanup: System-level cleanup completed");
            } catch (Exception e) {
                logger.warn("Orphan cleanup warning: {}", e.getMessage());
            }
        }
    }

    /**
     * Force cleanup all processes including system-level cleanup
     * Used by shutdown hook and emergency cleanup
     */
    public void forceCleanupAllProcesses() {
        logger.info("Force cleanup: Emergency termination of all processes...");
        
        // First try graceful cleanup
        terminateAll();
        
        // Then force-kill any remaining mock processes
        try {
            // Kill any lingering AS400TerminalMock processes
            ProcessBuilder pb = new ProcessBuilder("taskkill", "/IM", "AS400TerminalMock.exe", "/F");
            pb.start().waitFor(3, TimeUnit.SECONDS);
            
            // Kill any lingering SAPGUIMock processes  
            pb = new ProcessBuilder("taskkill", "/IM", "SAPGUIMock.exe", "/F");
            pb.start().waitFor(3, TimeUnit.SECONDS);
            
            // Kill any lingering ExcelMock processes
            pb = new ProcessBuilder("taskkill", "/IM", "ExcelMock.exe", "/F");
            pb.start().waitFor(3, TimeUnit.SECONDS);
            
            // Kill any lingering OracleFormsMock processes
            pb = new ProcessBuilder("taskkill", "/IM", "OracleFormsMock.exe", "/F");
            pb.start().waitFor(3, TimeUnit.SECONDS);
            
            // Kill any lingering java.exe processes running our mock apps (be careful here)
            pb = new ProcessBuilder("powershell.exe", "-Command", 
                "Get-Process -Name java -ErrorAction SilentlyContinue | Where-Object {$_.CommandLine -like '*Mock*'} | Stop-Process -Force");
            pb.start().waitFor(3, TimeUnit.SECONDS);
            
            logger.info("Force cleanup: System-level cleanup completed");
            
        } catch (Exception e) {
            logger.warn("Force cleanup warning: System cleanup failed: {}", e.getMessage());
        }
    }
      /**
     * Determine how to launch the application
     */
    private LaunchInfo determineLaunchStrategy(String applicationName) {
        logger.debug("Determining launch strategy for: '{}'", applicationName);
        
        // Strategy 1: JAR Mock Applications (highest priority for our tests)
        String jarBaseName = getJarBaseName(applicationName);
        String jarSuffix = getJarSuffix(applicationName);
        String jarPath = String.format("target/mock-apps/%s-%s.jar", jarBaseName, jarSuffix);
        
        logger.debug("Checking JAR: '{}'", jarPath);
        if (Files.exists(Paths.get(jarPath))) {
            logger.info("Found JAR: '{}'", jarPath);
            return new LaunchInfo(
                LaunchInfo.LaunchType.JAR,
                jarPath,
                getExpectedWindowTitle(applicationName),
                "java.exe", // Actual process name for JAR applications
                new String[]{"java", "-jar", jarPath}
            );
        } else {
            logger.debug("JAR not found: '{}'", jarPath);
        }
        
        // Strategy 2: Native Executables
        String exePath = String.format("target/executables/%sMock/%sMock.exe", 
            capitalizeFirstLetter(applicationName), capitalizeFirstLetter(applicationName));
        
        if (Files.exists(Paths.get(exePath))) {
            return new LaunchInfo(
                LaunchInfo.LaunchType.NATIVE,
                exePath,
                getExpectedWindowTitle(applicationName),
                new File(exePath).getName(),
                new String[]{exePath}
            );
        }
          // Strategy 3: System Applications
        String systemExe = getSystemExecutable(applicationName);
        if (systemExe != null) {
            // Special handling for explorer.exe to force new File Explorer window
            if ("explorer.exe".equals(systemExe)) {
                return new LaunchInfo(
                    LaunchInfo.LaunchType.SYSTEM,
                    systemExe,
                    getExpectedWindowTitle(applicationName),
                    systemExe,
                    new String[]{systemExe, System.getProperty("user.home")}
                );
            } else {
                return new LaunchInfo(
                    LaunchInfo.LaunchType.SYSTEM,
                    systemExe,
                    getExpectedWindowTitle(applicationName),
                    systemExe,
                    new String[]{systemExe}
                );
            }
        }
        
        return null;
    }
    
    /**
     * Launch process using the determined strategy
     */
    private Process launchProcess(LaunchInfo launchInfo) {
        try {
            ProcessBuilder pb = new ProcessBuilder(launchInfo.command);
            
            if (launchInfo.launchType == LaunchInfo.LaunchType.JAR) {
                pb.directory(new File("target/mock-apps"));
            } else if (launchInfo.launchType == LaunchInfo.LaunchType.NATIVE) {
                pb.directory(new File(launchInfo.executablePath).getParentFile());
            }
            
            Process process = pb.start();            logger.info("Process started: Command: {}, PID: {}", 
                String.join(" ", launchInfo.command), process.pid());
            
            return process;
            
        } catch (Exception e) {
            logger.error("Process start failed: {}", e.getMessage(), e);
            return null;
        }
    }    /**
     * HUMAN-LIKE: Simple process launch verification
     * Just ensure the process started - let OCR and image recognition handle the rest
     */    private ManagedApplicationContext waitForWindowAndCreateProcessInfo(String expectedWindowTitle, 
                                                         String expectedProcessName, 
                                                         Process launcherProcess, 
                                                         int timeoutSeconds) {logger.info("Process launch: Started PID {} ('{}') - giving time to initialize", 
            launcherProcess.pid(), expectedProcessName);
        
        long launcherPid = launcherProcess.pid();
        
        // Give the process a moment to initialize (like a human would)
        try {
            Thread.sleep(1000); // 1 second for process to start up
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        
        // Check if process is still alive (basic verification)
        if (launcherProcess.isAlive()) {
            // Try to find a window (but don't fail if we can't - OCR will handle it)
            WinDef.HWND windowHandle = findMainWindowByPID((int) launcherPid);
            String windowTitle = windowHandle != null ? 
                getWindowTitleFromHandle(windowHandle) : "Process Running (OCR will locate)";
              logger.info("Process ready: PID {} is running - '{}'", launcherPid, windowTitle);
            return createContextFromPID("unknown", (int) launcherPid);
        }
          // For system applications, they might delegate immediately - check for system process
        logger.info("System delegation: Launcher exited, searching for running '{}'", expectedProcessName);
        ManagedApplicationContext systemProcess = findProcessByName(expectedProcessName);
        if (systemProcess != null) {            
            logger.info("System delegation: Found delegated process {} - PID: {}", 
                expectedProcessName, systemProcess.getProcessId());
            return systemProcess;
        }
        
        // Additional fallback for explorer.exe - use tasklist command
        if ("explorer.exe".equalsIgnoreCase(expectedProcessName)) {
            ManagedApplicationContext explorerProcess = findExplorerProcess();
            if (explorerProcess != null) {                
                logger.info("Explorer fallback: Found explorer.exe - PID: {}", 
                    explorerProcess.getProcessId());
                return explorerProcess;
            }
        }
          logger.warn("Process launch: PID {} exited quickly - may have delegated or failed", launcherPid);
        // Return basic ManagedApplicationContext anyway - let OCR handle finding the actual application
        return createContextFromPID("unknown", (int) launcherPid);
    }
      /**
     * Find process by name using system calls (PID-centric approach)
     */
    private ManagedApplicationContext findProcessByName(String processName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "get", "Name,ProcessId", "/format:csv");
            Process proc = pb.start();
            
            try (InputStream inputStream = proc.getInputStream()) {
                byte[] buffer = new byte[4096];
                StringBuilder output = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }
                  String[] lines = output.toString().split("\\r?\\n");
                for (String line : lines) {
                    if (line.contains(processName) && line.contains(",")) {                        // Parse CSV format: Node,Name,ProcessId
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            String name = parts[1].trim();
                            String pidStr = parts[2].trim();
                            
                            // Skip header line
                            if (name.equalsIgnoreCase("Name") || pidStr.equalsIgnoreCase("ProcessId")) {
                                continue;
                            }
                            
                            if (name.equalsIgnoreCase(processName)) {
                                try {
                                    int pid = Integer.parseInt(pidStr);
                                    
                                    // Try to find the main window for this PID
                                    WinDef.HWND windowHandle = findMainWindowByPID(pid);
                                    String windowTitle = windowHandle != null ? 
                                        getWindowTitleFromHandle(windowHandle) : "No Window";
                                    
                                    logger.info("Found delegated process: PID: {}, Name: '{}', Window: '{}'", 
                                        pid, name, windowTitle);
                                    
                                    return createContextFromPID(windowTitle.equals("No Window") ? "unknown" : windowTitle, pid);
                                } catch (NumberFormatException e) {
                                    continue; // Skip invalid PID
                                }
                            }
                        }
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            logger.warn("Error finding process by name '{}': {}", processName, e.getMessage());
            return null;
        }
    }    /**
     * Find main window by PID - EXPLORER WINDOWS ONLY, NO PROGRAM MANAGER
     */
    private WinDef.HWND findMainWindowByPID(int pid) {
        try {
            // Use JNA to enumerate windows and find main window for this PID
            final WinDef.HWND[] explorerWindow = new WinDef.HWND[1];
            final WinDef.HWND[] otherWindow = new WinDef.HWND[1];
            
            User32.INSTANCE.EnumWindows((hWnd, data) -> {
                IntByReference processId = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hWnd, processId);
                
                if (processId.getValue() == pid && User32.INSTANCE.IsWindowVisible(hWnd)) {
                    // Check if this looks like a main window (has title and is not a child)
                    char[] buffer = new char[512];
                    User32.INSTANCE.GetWindowText(hWnd, buffer, 512);
                    String windowTitle = Native.toString(buffer);
                    
                    if (!windowTitle.trim().isEmpty() && User32.INSTANCE.GetParent(hWnd) == null) {
                        // Get window class name to distinguish Explorer windows
                        char[] classBuffer = new char[256];
                        User32.INSTANCE.GetClassName(hWnd, classBuffer, 256);
                        String className = Native.toString(classBuffer);
                        
                        // REJECT Program Manager completely - we never want desktop shell
                        if ("Program Manager".equals(windowTitle) || "Progman".equals(className)) {
                            logger.debug("⛔ REJECTING Program Manager window for PID {} - we only want file Explorer windows", pid);
                            return true; // Continue enumeration, skip this window
                        }
                        
                        // PRIORITY: CabinetWClass is file Explorer - this is what we want!
                        if ("CabinetWClass".equals(className)) {
                            explorerWindow[0] = hWnd;
                            logger.debug("✅ Found CabinetWClass Explorer window: '{}' for PID {}", windowTitle, pid);
                            return false; // Stop enumeration - this is what we want
                        } else if (otherWindow[0] == null) {
                            // Save any other non-Program Manager window as fallback
                            otherWindow[0] = hWnd;
                            logger.debug("📝 Found other window: '{}' (class: {}) for PID {}", windowTitle, className, pid);
                        }
                    }
                }
                return true; // Continue enumeration
            }, null);
            
            // Return Explorer window if found, otherwise any other non-Program Manager window
            WinDef.HWND result = explorerWindow[0] != null ? explorerWindow[0] : otherWindow[0];
            if (result != null) {
                char[] buffer = new char[512];
                User32.INSTANCE.GetWindowText(result, buffer, 512);
                String title = Native.toString(buffer);
                logger.debug("🎯 Selected window for PID {}: '{}'", pid, title);
            } else {
                logger.debug("❌ No suitable windows found for PID {} (Program Manager excluded)", pid);
            }
            return result;
            
        } catch (Exception e) {
            logger.debug("Error finding window for PID {}: {}", pid, e.getMessage());
            return null;
        }
    }
    
    /**
     * Get expected window title for application
     */
    private String getExpectedWindowTitle(String applicationName) {
        switch (applicationName.toLowerCase().replace("_", "")) {
            case "as400":
            case "as400terminal":
                return "AS400 Terminal Emulator - Mock";
            case "sap":
            case "sapgui":
                return "SAP GUI - Mock";
            case "oracle":
            case "oracleforms":
                return "Oracle Forms - Mock";
            case "excel":
                return "Excel - Mock";
            case "calculator":
                return "Calculator";
            case "notepad":
                return "Notepad";
            default:
                return applicationName.substring(0, 1).toUpperCase() + applicationName.substring(1) + " - Mock";
        }
    }    /**
     * Get JAR base name mapping
     */
    private String getJarBaseName(String applicationName) {
        switch (applicationName.toLowerCase().replace("_", "").replace("mock", "")) {
            case "as400":
            case "as400terminal":
                return "AS400TerminalMock";
            case "sap":
            case "sapgui":
                return "SAPGUIMock";
            case "oracle":
            case "oracleforms":
                return "OracleFormsMock";
            case "excel":
                return "ExcelMock";
            default:
                return capitalizeFirstLetter(applicationName) + "Mock";
        }
    }
    
    /**
     * Map application names to JAR suffix patterns
     */
    private String getJarSuffix(String applicationName) {
        switch (applicationName.toLowerCase().replace("_", "").replace("mock", "")) {
            case "as400":
            case "as400terminal":
                return "as400-mock";
            case "sap":
            case "sapgui":
                return "sap-mock";
            case "oracle":
            case "oracleforms":
                return "oracle-forms-mock";
            case "excel":
                return "excel-mock";
            default:
                return applicationName.toLowerCase().replace("_", "-") + "-mock";
        }
    }    /**
     * Get system executable for built-in Windows applications
     */
    private String getSystemExecutable(String applicationName) {
        switch (applicationName.toLowerCase()) {
            case "calculator": return "calc.exe";
            case "notepad": return "notepad.exe";
            case "paint": return "mspaint.exe";
            case "wordpad": return "wordpad.exe";
            case "file_explorer": 
            case "explorer": return "explorer.exe";
            default: return null;
        }
    }
    
    /**
     * Get window title from window handle (helper method)
     */
    private String getWindowTitleFromHandle(WinDef.HWND windowHandle) {
        try {
            char[] buffer = new char[512];
            com.sun.jna.platform.win32.User32.INSTANCE.GetWindowText(windowHandle, buffer, 512);
            return com.sun.jna.Native.toString(buffer);
        } catch (Exception e) {
            logger.warn("Failed to get window title from handle: {}", e.getMessage());
            return "Unknown Window";
        }
    }
    
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Launch information container
     */
    private static class LaunchInfo {
        enum LaunchType { JAR, NATIVE, SYSTEM }
        
        final LaunchType launchType;
        final String executablePath;
        final String expectedWindowTitle;
        final String expectedProcessName;
        final String[] command;
        
        LaunchInfo(LaunchType launchType, String executablePath, String expectedWindowTitle, 
                  String expectedProcessName, String[] command) {
            this.launchType = launchType;
            this.executablePath = executablePath;
            this.expectedWindowTitle = expectedWindowTitle;
            this.expectedProcessName = expectedProcessName;
            this.command = command;
        }
    }    /**
     * Managed process container for tracking and cleanup with multi-instance support
     */    private static class ManagedProcess {
        @SuppressWarnings("unused")
        final String applicationName;
        final Process process;
        final ManagedApplicationContext processInfo;
        @SuppressWarnings("unused")
        final LaunchInfo launchInfo;
        @SuppressWarnings("unused")
        final long startTime;
        final int instanceIndex;
          ManagedProcess(String applicationName, Process process, ManagedApplicationContext processInfo, LaunchInfo launchInfo, int instanceIndex) {
            this.applicationName = applicationName;
            this.process = process;
            this.processInfo = processInfo;
            this.launchInfo = launchInfo;
            this.startTime = System.currentTimeMillis();
            this.instanceIndex = instanceIndex;
        }
          boolean isStillRunning() {
            // For system applications like explorer.exe, the launcher process may exit immediately
            // while delegating to an existing process. In such cases, we should rely on the 
            // actual system process check rather than the Java Process object.
            boolean javaProcessAlive = process.isAlive();
            boolean systemProcessAlive = processInfo.isProcessStillRunning();
            
            // If system process is alive, consider it running regardless of Java process state
            // This handles cases where launcher exits but delegates to existing process
            if (systemProcessAlive) {
                return true;
            }
            
            // If system process is dead, require both to be consistent
            return javaProcessAlive && systemProcessAlive;
        }        boolean terminate() {
            try {
                // For external processes, use system-level termination
                if (!process.isAlive() || process.getClass().getName().contains("ProcessManager")) {
                    // This is an external/mock process, terminate via PID
                    logger.info("External termination: Using PID-based termination for external process");
                    return terminateExternalProcess(processInfo.getProcessId());
                }
                
                // For regular launched processes, use Java Process API
                if (process.isAlive()) {
                    process.destroy();
                    boolean terminated = process.waitFor(5, TimeUnit.SECONDS);
                    
                    if (!terminated) {
                        // Force termination
                        process.destroyForcibly();
                        process.waitFor(3, TimeUnit.SECONDS);
                    }
                }
                
                // Verify termination by checking both Process state and actual system process
                boolean processGone = !process.isAlive();
                boolean systemProcessGone = !processInfo.isProcessStillRunning();
                
                if (processGone && systemProcessGone) {
                    logger.info("Process terminated: Both Java process and system process are terminated");
                    return true;
                } else if (processGone && !systemProcessGone) {
                    logger.warn("Partial termination: Java process terminated but system process still running - attempting taskkill");
                    // Fallback to system-level termination
                    try {
                        ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(processInfo.getProcessId()));
                        pb.start().waitFor(3, TimeUnit.SECONDS);
                        Thread.sleep(500); // Give time for cleanup
                        return !processInfo.isProcessStillRunning();
                    } catch (Exception e) {
                        logger.warn("Fallback termination failed: {}", e.getMessage());
                        return false;
                    }
                } else {                    logger.warn("Termination incomplete: Process state - Java: {}, System: {}", 
                        !processGone, !systemProcessGone);
                    return false;
                }
                
            } catch (Exception e) {
                logger.warn("Termination warning: Exception during process termination: {}", e.getMessage());
                try {
                    // Last resort: Force system-level termination
                    ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(processInfo.getProcessId()));
                    pb.start().waitFor(3, TimeUnit.SECONDS);
                    Thread.sleep(500);
                    return !processInfo.isProcessStillRunning();
                } catch (Exception e2) {
                    logger.error("Termination failed: All termination attempts failed: {}", e2.getMessage());
                    return false;
                }
            }
        }
    }
      /**
     * Create LaunchInfo from executable path
     */
    private LaunchInfo createLaunchInfoFromPath(String executablePath, String applicationName) {
        File execFile = new File(executablePath);
        String fileName = execFile.getName().toLowerCase();
        
        if (fileName.endsWith(".jar")) {
            return new LaunchInfo(
                LaunchInfo.LaunchType.JAR,
                executablePath,
                getExpectedWindowTitleFromName(applicationName),
                "java.exe",
                new String[]{"java", "-jar", executablePath}
            );        } else if (fileName.endsWith(".exe")) {
            // For native executables, the expected process name is the actual .exe file
            String expectedProcessName = execFile.getName(); // e.g., "AS400TerminalMock.exe"
            
            // Special handling for explorer.exe to force new File Explorer window
            if ("explorer.exe".equals(fileName)) {
                return new LaunchInfo(
                    LaunchInfo.LaunchType.NATIVE,
                    executablePath,
                    getExpectedWindowTitleFromName(applicationName),
                    expectedProcessName,
                    new String[]{executablePath, System.getProperty("user.home")}
                );
            } else {
                return new LaunchInfo(
                    LaunchInfo.LaunchType.NATIVE,
                    executablePath,
                    getExpectedWindowTitleFromName(applicationName),
                    expectedProcessName,
                    new String[]{executablePath}
                );
            }
        } else {
            logger.error("Unsupported executable type: {}", fileName);
            return null;
        }
    }    /**
     * Create LaunchInfo from executable path with command line arguments
     */
    private LaunchInfo createLaunchInfoFromPathWithArguments(String executablePath, String arguments, String applicationName) {
        File execFile = new File(executablePath);
        String fileName = execFile.getName().toLowerCase();
        
        if (fileName.endsWith(".jar")) {
            // For JAR files, append arguments after the jar file
            List<String> commandList = new ArrayList<>();
            commandList.add("java");
            commandList.add("-jar");
            commandList.add(executablePath);
            if (arguments != null && !arguments.trim().isEmpty()) {
                commandList.addAll(Arrays.asList(arguments.trim().split("\\s+")));
            }
            
            return new LaunchInfo(
                LaunchInfo.LaunchType.JAR,
                executablePath,
                getExpectedWindowTitleFromName(applicationName),
                "java.exe",
                commandList.toArray(new String[0])
            );
        } else if (fileName.endsWith(".exe")) {
            // For native executables, the expected process name is the actual .exe file
            String expectedProcessName = execFile.getName(); // e.g., "AS400TerminalMock.exe"
            
            List<String> commandList = new ArrayList<>();
            commandList.add(executablePath);
            if (arguments != null && !arguments.trim().isEmpty()) {
                commandList.addAll(Arrays.asList(arguments.trim().split("\\s+")));
            }
            
            return new LaunchInfo(
                LaunchInfo.LaunchType.NATIVE,
                executablePath,
                getExpectedWindowTitleFromName(applicationName),
                expectedProcessName,
                commandList.toArray(new String[0])
            );
        } else {
            logger.error("Unsupported executable type: {}", fileName);
            return null;
        }
    }
    /**
     * Register managed process with multi-instance support
     */
    private int registerManagedProcess(String applicationName, Process process, ManagedApplicationContext processInfo, LaunchInfo launchInfo) {
        // Initialize lists if first instance
        managedProcesses.computeIfAbsent(applicationName, k -> new ArrayList<>());
        processCounters.computeIfAbsent(applicationName, k -> new AtomicInteger(0));
        
        // Create managed process with instance index
        int instanceIndex = processCounters.get(applicationName).getAndIncrement();
        ManagedProcess managedProcess = new ManagedProcess(
            applicationName, process, processInfo, launchInfo, instanceIndex
        );
        
        // Add to list
        managedProcesses.get(applicationName).add(managedProcess);
        
        // Set as active instance
        activeProcessIndices.put(applicationName, instanceIndex);
        
        // ENTERPRISE INTEGRATION: Also add to trackedContexts for getRunningApplicationContext() lookups
        trackedContexts.computeIfAbsent(applicationName, k -> new ArrayList<>()).add(processInfo);
        activeContextIndices.put(applicationName, 0); // Set as active context
        
        logger.info("Registered: Application '{}' instance [{}] - Total instances: {}", 
            applicationName, instanceIndex, managedProcesses.get(applicationName).size());
        
        return instanceIndex;
    }
    
    /**
     * Get expected window title from application name  
     */
    private String getExpectedWindowTitleFromName(String applicationName) {
        // Try configured mapping first
        String configuredTitle = getExpectedWindowTitle(applicationName);
        if (!configuredTitle.contains("Mock")) {
            return configuredTitle;
        }
        
        // For direct path execution, use application name as basis
        return applicationName.substring(0, 1).toUpperCase() + applicationName.substring(1);
    }
      /**
     * Find process by instance index
     */
    private ManagedProcess findProcessByIndex(List<ManagedProcess> processes, int instanceIndex) {
        return processes.stream()
            .filter(p -> p.instanceIndex == instanceIndex)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Resolve executable path - handles both absolute paths and system executables    /**
     * Resolve executable path - handle system executables like cmd.exe
     * PUBLIC METHOD for step definitions to use
     */
    public String resolveExecutablePath(String executablePath) {
        // If already absolute path and exists, return as-is
        if (Paths.get(executablePath).isAbsolute() && Files.exists(Paths.get(executablePath))) {
            return executablePath;
        }
        
        // If relative path and exists, return as-is
        if (Files.exists(Paths.get(executablePath))) {
            return executablePath;
        }
        
        // Try to find system executable
        if (executablePath.toLowerCase().endsWith(".exe")) {
            String systemPath = findSystemExecutable(executablePath);
            if (systemPath != null) {
                return systemPath;
            }
        }
        
        // If just executable name without .exe extension, add it and try again
        if (!executablePath.toLowerCase().endsWith(".exe")) {
            String withExe = executablePath + ".exe";
            String systemPath = findSystemExecutable(withExe);
            if (systemPath != null) {
                return systemPath;
            }
        }
        
        return null; // Executable not found
    }
    
    /**
     * Find system executable in Windows system directories
     */
    private String findSystemExecutable(String executableName) {
        // Common Windows system paths
        String[] systemPaths = {
            System.getenv("WINDIR") + "\\System32\\" + executableName,
            System.getenv("WINDIR") + "\\SysWOW64\\" + executableName,
            System.getenv("WINDIR") + "\\" + executableName
        };
        
        for (String path : systemPaths) {
            if (path != null && Files.exists(Paths.get(path))) {
                logger.debug("Found system executable: '{}'", path);
                return path;
            }
        }
        
        // Try PATH environment variable
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            String[] pathDirs = pathEnv.split(";");
            for (String dir : pathDirs) {
                String fullPath = dir.trim() + "\\" + executableName;
                if (Files.exists(Paths.get(fullPath))) {
                    logger.debug("Found executable in PATH: '{}'", fullPath);
                    return fullPath;
                }
            }
        }
        
        return null;
    }    /**
     * Find explorer.exe process using tasklist command (fallback method)
     */
    private ManagedApplicationContext findExplorerProcess() {
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq explorer.exe", "/FO", "CSV");
            Process proc = pb.start();
            
            try (InputStream inputStream = proc.getInputStream()) {
                byte[] buffer = new byte[4096];
                StringBuilder output = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }
                
                String[] lines = output.toString().split("\\r?\\n");
                for (String line : lines) {
                    // Skip header line
                    if (line.startsWith("\"Image Name\"")) continue;
                    
                    if (line.contains("explorer.exe")) {
                        // Parse CSV format: "Image Name","PID","Session Name","Session#","Mem Usage"
                        String[] parts = line.split("\",\"");
                        if (parts.length >= 2) {
                            String pidStr = parts[1].replace("\"", "");
                            try {
                                int pid = Integer.parseInt(pidStr);
                                
                                // Try to find the main window for this PID
                                WinDef.HWND windowHandle = findMainWindowByPID(pid);
                                String windowTitle = windowHandle != null ? 
                                    getWindowTitleFromHandle(windowHandle) : "Windows Explorer";
                                  logger.info("Explorer found: PID: {}, Window: '{}'", pid, windowTitle);
                                return createContextFromPID("explorer", pid);
                            } catch (NumberFormatException e) {
                                continue; // Skip invalid PID
                            }
                        }
                    }
                }
            }
            
            return null;
              } catch (Exception e) {
            logger.warn("Error finding explorer.exe process: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * System protection: Check if application is system-critical and should not be terminated
     * Protects Windows desktop and other essential system processes from accidental termination
     */
    private boolean isSystemCriticalApplication(String applicationName) {
        if (applicationName == null) {
            return false;
        }
        
        String appName = applicationName.toLowerCase();
        
        // System-critical applications that should NEVER be terminated by automation
        return appName.contains("explorer") ||           // Windows Explorer - DESKTOP!
               appName.contains("windowsexplorer") ||    // Our managed Explorer instances
               appName.contains("fileexplorer") ||       // File Explorer variants
               appName.contains("dwm") ||                // Desktop Window Manager
               appName.contains("winlogon") ||           // Windows Logon Process
               appName.contains("csrss") ||              // Client Server Runtime Process
               appName.contains("lsass") ||              // Local Security Authority
               appName.contains("smss") ||               // Session Manager
               appName.contains("wininit") ||            // Windows Initialization Process
               appName.contains("services") ||           // Windows Services
               appName.contains("svchost") ||            // Service Host Process               appName.contains("taskhost") ||           // Task Host Process
               appName.contains("taskmgr");              // Task Manager
    }    /**
     * Enhanced multi-stage termination process for external processes (not managed by ProcessManager)
     * Stage 1: Graceful termination (taskkill without /F)
     * Stage 2: Force termination (taskkill /F)
     * Stage 3: Termination by process name (fallback)
     * Stage 3.5: Window closing via ALT+F4 (if process has windows)
     * Stage 4: Final verification
     */
    public static boolean terminateExternalProcess(int pid) {
        logger.info("Multi-stage termination: Starting robust termination for PID {}", pid);
        
        if (!isProcessStillRunning(pid)) {
            logger.debug("Process already terminated: PID {} is not running", pid);
            return true;
        }
        
        // Stage 1: Graceful termination
        if (tryGracefulTermination(pid)) {
            logger.info("Graceful termination successful: PID {} terminated gracefully", pid);
            return true;
        }
        
        // Stage 2: Force termination
        if (tryForceTermination(pid)) {
            logger.info("Force termination successful: PID {} terminated forcefully", pid);
            return true;
        }
        
        // Stage 3: Process name-based termination
        if (tryTerminationByName(pid)) {
            logger.info("Name-based termination successful: PID {} terminated by process name", pid);
            return true;
        }
        
        // Stage 3.5: Window closing via ALT+F4 (graceful GUI close)
        if (tryWindowClosing(pid)) {
            logger.info("Window close termination successful: PID {} terminated via window close", pid);
            return true;
        }
        
        // Stage 4: Final verification
        boolean isTerminated = !isProcessStillRunning(pid);
        if (isTerminated) {
            logger.info("Termination verification successful: PID {} is confirmed terminated", pid);
        } else {
            logger.error("Termination failed: PID {} survived all termination attempts", pid);
        }
        
        return isTerminated;
    }/**
     * Stage 1: Graceful termination attempt
     */
    private static boolean tryGracefulTermination(int pid) {        try {
            logger.debug("Attempting graceful termination for PID {}", pid);
            ProcessBuilder pb = new ProcessBuilder("taskkill", "/PID", String.valueOf(pid));
            Process killProcess = pb.start();
            
            boolean completed = killProcess.waitFor(5, TimeUnit.SECONDS);
            if (completed && killProcess.exitValue() == 0) {
                Thread.sleep(1000); // Give time for cleanup
                return !isProcessStillRunning(pid);
            }
            
            logger.debug("Graceful termination failed: Exit code {} for PID {}", 
                completed ? killProcess.exitValue() : "timeout", pid);
            return false;
            
        } catch (Exception e) {
            logger.debug("Graceful termination exception: {}", e.getMessage());
            return false;
        }
    }
      /**
     * Stage 2: Force termination attempt
     */    private static boolean tryForceTermination(int pid) {
        try {
            logger.debug("Force termination: Attempting for PID {}", pid);
            ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/PID", String.valueOf(pid));
            Process killProcess = pb.start();
            
            boolean completed = killProcess.waitFor(5, TimeUnit.SECONDS);
            if (completed && killProcess.exitValue() == 0) {
                Thread.sleep(1500); // Give more time for force cleanup
                return !isProcessStillRunning(pid);
            }
            
            // Even if taskkill reports failure, check if process is actually gone
            if (completed) {
                Thread.sleep(1500);
                boolean processGone = !isProcessStillRunning(pid);
                if (processGone) {
                    logger.debug("Force termination success: Process gone despite exit code {} for PID {}", 
                        killProcess.exitValue(), pid);
                    return true;
                }
                logger.debug("Force termination failed: Exit code {} and process still running for PID {}", 
                    killProcess.exitValue(), pid);
            } else {
                logger.debug("Force termination timeout: Command timed out for PID {}", pid);
            }
            
            return false;
            
        } catch (Exception e) {
            logger.debug("Force termination exception: {}", e.getMessage());
            return false;
        }
    }
      /**
     * Stage 3: Termination by process name (fallback for when PID doesn't work)
     */
    private static boolean tryTerminationByName(int pid) {
        try {
            // First, get the process name for this PID
            String processName = getProcessNameByPID(pid);
            if (processName == null || processName.trim().isEmpty()) {
                logger.debug("Name termination: Could not determine process name for PID {}", pid);
                return false;
            }
            
            logger.debug("Termination by name: Attempting termination of '{}' for PID {}", processName, pid);
            ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/IM", processName);
            Process killProcess = pb.start();
            
            boolean completed = killProcess.waitFor(5, TimeUnit.SECONDS);
            if (completed) {
                Thread.sleep(2000); // Give extra time for cleanup
                return !isProcessStillRunning(pid);
            }
              return false;
            
        } catch (Exception e) {
            logger.debug("Name termination exception: {}", e.getMessage());
            return false;
        }
    }
      /**
     * Stage 3.5: Window closing via ALT+F4 (graceful GUI application close)
     * Attempts to close application windows gracefully before process termination
     */
    private static boolean tryWindowClosing(int pid) {
        try {
            logger.debug("Window close: Attempting window close via ALT+F4 for PID {}", pid);
            
            // Create a temporary WindowController instance for this operation
            WindowController windowController = new WindowController();
            
            // Try to set current window by PID
            boolean windowFound = windowController.setCurrentWindowByPID(pid);
            if (!windowFound) {
                logger.debug("Window close: No window found for PID {}", pid);
                return false;
            }
            
            // Focus the window to ensure ALT+F4 is sent to the correct target
            windowController.activateWindow();
            Thread.sleep(500); // Give time for window to come to foreground
            
            // Send ALT+F4 to close the window gracefully
            windowController.sendKeyCombo("ALT", "F4");
            
            // Wait for the application to close gracefully
            Thread.sleep(3000); // Give application time to close gracefully
            
            // Verify the process is gone
            boolean processGone = !isProcessStillRunning(pid);
            if (processGone) {
                logger.debug("Window close success: Process closed gracefully for PID {}", pid);
                return true;
            } else {
                logger.debug("Window close partial: Window closed but process still running for PID {}", pid);
                return false;
            }
            
        } catch (Exception e) {
            logger.debug("Window close exception: Failed to close window for PID {}: {}", pid, e.getMessage());
            return false;
        }
    }
    
    /**
     * Get process name by PID using wmic
     */
    private static String getProcessNameByPID(int pid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", 
                "ProcessId=" + pid, "get", "Name", "/format:csv");
            Process proc = pb.start();
            
            try (InputStream inputStream = proc.getInputStream()) {
                byte[] buffer = new byte[4096];
                StringBuilder output = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }
                
                String[] lines = output.toString().split("\\r?\\n");
                for (String line : lines) {
                    if (line.contains(",") && !line.contains("Node") && !line.contains("Name")) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            String name = parts[1].replace("\"", "").trim();
                            if (!name.isEmpty()) {
                                return name;
                            }
                        }
                    }
                }
            }
            
            proc.waitFor(3, TimeUnit.SECONDS);
            return null;
              } catch (Exception e) {
            logger.debug("Get process name failed: Failed to get name for PID {}: {}", pid, e.getMessage());
            return null;
        }
    }    /**
     * Robust process existence check using multiple methods
     */
    private static boolean isProcessStillRunning(int pid) {
        try {
            // Method 1: Use wmic to check if process exists
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", 
                "ProcessId=" + pid, "get", "ProcessId", "/format:csv");
            Process proc = pb.start();
            
            try (InputStream inputStream = proc.getInputStream()) {
                byte[] buffer = new byte[1024];
                StringBuilder output = new StringBuilder();
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.append(new String(buffer, 0, bytesRead));
                }
                
                String result = output.toString();
                boolean processFound = result.contains(String.valueOf(pid));
                
                proc.waitFor(3, TimeUnit.SECONDS);
                
                logger.debug("Process check: PID {} exists = {}", pid, processFound);
                return processFound;
            }
              } catch (Exception e) {
            logger.debug("Process check failed: Failed to check PID {}: {}", pid, e.getMessage());
            // If we can't check, assume it's still running to be safe
            return true;
        }
    }
      // ===== WIN32 HELPER METHODS =====
    
    /**
     * Single page function - Register successful launch
     */
    private ManagedApplicationContext registerSuccessfulLaunch(String applicationName, Process process, 
                                               ManagedApplicationContext processInfo, LaunchInfo launchInfo) {
        // Parameter validation
        Objects.requireNonNull(applicationName, "Application name cannot be null");
        Objects.requireNonNull(process, "Process cannot be null");
        Objects.requireNonNull(processInfo, "ProcessInfo cannot be null");
        Objects.requireNonNull(launchInfo, "LaunchInfo cannot be null");
        
        try {
            // Register for multi-instance tracking and cleanup
            int instanceIndex = registerManagedProcess(applicationName, process, processInfo, launchInfo);
            
            // Store original launch info for consistent multi-instance launching
            originalLaunchInfo.put(applicationName, launchInfo);
            
            logger.info("Launch successful: Application '{}' [{}] launched - PID: {}, Window: '{}'", 
                applicationName, instanceIndex, processInfo.getProcessId(), processInfo.getWindowTitle());
            return processInfo;
            
        } catch (Exception e) {
            logger.error("Failed to register successful launch for '{}': {}", applicationName, e.getMessage());
            return processInfo; // Return process info even if registration fails
        }
    }
    
    /**
     * Single page function - Cleanup failed launch
     */
    private void cleanupFailedLaunch(Process process, String applicationName) {
        // Parameter validation
        Objects.requireNonNull(process, "Process cannot be null");
        Objects.requireNonNull(applicationName, "Application name cannot be null");
        
        try {
            if (process.isAlive()) {
                logger.warn("Cleaning up failed launch for '{}': Terminating process", applicationName);
                process.destroyForcibly();
                
                // Fixed timeout bound
                boolean terminated = process.waitFor(5, TimeUnit.SECONDS);
                if (!terminated) {
            logger.warn("Failed to terminate process within timeout for '{}'", applicationName);
                }
            }
            
            logger.error("Launch failed: Window not found for '{}'", applicationName);
            
        } catch (Exception e) {
            logger.error("Exception during cleanup for '{}': {}", applicationName, e.getMessage());
        }
    }
}
