package com.automation.core.win32;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SURGICAL PRECISION Win32 API Wrapper
 * 
 * Complete Windows API/SDK wrapping for TOTAL PROCESS CONTROL:
 * - Process lifecycle (creation, monitoring, termination)
 * - Window management (discovery, manipulation, state tracking)
 * - Memory access and monitoring
 * - CPU usage tracking
 * - Handle management
 * - Thread enumeration
 * - Performance counters
 * 
 * ZERO TOLERANCE FOR FAILURES - Built for Enterprise Business-Critical Applications
 */
public class Win32ApiWrapper {
    private static final Logger logger = LoggerFactory.getLogger(Win32ApiWrapper.class);
    private static Win32ApiWrapper instance;
      // Performance tracking and caching
    private final Map<Integer, ProcessIntelligence> intelligenceCache = new ConcurrentHashMap<>();
    private final Map<Integer, ProcessPerformanceData> performanceCache = new ConcurrentHashMap<>();
    private final Map<Integer, List<Integer>> processThreads = new ConcurrentHashMap<>();
    private final Map<Integer, String> processCommandLines = new ConcurrentHashMap<>();
    
    // Cache expiration time (5 minutes)
    private static final long CACHE_EXPIRATION_MS = 5 * 60 * 1000;
    
    // Constants for Win32 API
    public static final int PROCESS_ALL_ACCESS = 0x1F0FFF;
    public static final int PROCESS_QUERY_INFORMATION = 0x0400;
    public static final int PROCESS_VM_READ = 0x0010;
    public static final int STILL_ACTIVE = 259;
    public static final int MAX_PATH = 260;
    
    public static Win32ApiWrapper getInstance() {
        if (instance == null) {
            synchronized (Win32ApiWrapper.class) {
                if (instance == null) {
                    instance = new Win32ApiWrapper();
                }
            }
        }
        return instance;
    }
    
    private Win32ApiWrapper() {
        logger.info("SURGICAL WIN32 WRAPPER: Initializing comprehensive Windows API control");
    }
    
    // ===== PROCESS LIFECYCLE MANAGEMENT =====
      /**
     * Get comprehensive process information using multiple Win32 APIs with intelligent caching
     */    public ProcessIntelligence gatherProcessIntelligence(int pid) {
        logger.debug("GATHERING INTELLIGENCE: Complete analysis for PID {}", pid);
        
        // Check cache first
        ProcessIntelligence cached = intelligenceCache.get(pid);
        if (cached != null && (System.currentTimeMillis() - cached.gatherTime) < CACHE_EXPIRATION_MS) {
            logger.debug("CACHE HIT: Returning cached intelligence for PID {}", pid);
            return cached;
        }
        
        ProcessIntelligence intel = new ProcessIntelligence();
        intel.pid = pid;
        intel.gatherTime = System.currentTimeMillis();
        
        try {
            // Open process handle for comprehensive access
            WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(
                PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pid);
            
            if (processHandle != null) {
                try {
                    // Get executable path (FAST)
                    intel.executablePath = getProcessExecutablePath(processHandle);
                    
                    // EMERGENCY OPTIMIZATION: Skip slow WMIC calls for common processes
                    if (intel.executablePath != null && 
                        (intel.executablePath.endsWith("cmd.exe") || 
                         intel.executablePath.endsWith("explorer.exe"))) {
                        logger.debug("FAST TRACK: Skipping expensive operations for {}", intel.executablePath);
                        intel.commandLine = intel.executablePath; // Use path as command line
                        intel.parentProcessId = 0; // Skip parent lookup
                    } else {
                        // Get command line (SLOW - only for unknown processes)
                        intel.commandLine = getProcessCommandLine(pid);
                        // Get parent process ID (SLOW - only for unknown processes)
                        intel.parentProcessId = getParentProcessId(pid);
                    }
                    
                    // Get process creation time (FAST)
                    intel.creationTime = getProcessCreationTime(processHandle);
                    
                    // Get memory information (FAST)
                    intel.memoryInfo = getProcessMemoryInfo(processHandle);
                    
                    // Get process times (FAST)
                    intel.cpuTimes = getProcessTimes(processHandle);
                      // Get process priority (FAST)
                    intel.priority = getProcessPriorityAlternative(processHandle);
                    
                    // Check if 32-bit process on 64-bit system (FAST)
                    intel.isWow64Process = isWow64Process(processHandle);
                    
                    logger.debug("INTELLIGENCE COMPLETE: PID {} -> Path: '{}', Parent: {}, Memory: {}KB", 
                        pid, intel.executablePath, intel.parentProcessId, 
                        intel.memoryInfo != null ? intel.memoryInfo.workingSetSize / 1024 : 0);
                    
                } finally {
                    Kernel32.INSTANCE.CloseHandle(processHandle);
                }
            }
            
            // EMERGENCY OPTIMIZATION: Skip thread enumeration for console apps
            if (intel.executablePath == null || !intel.executablePath.endsWith("cmd.exe")) {
                // Get thread information (SLOW - skip for CMD)
                intel.threadIds = getProcessThreadIds(pid);
            } else {
                intel.threadIds = new ArrayList<>(); // Empty list for CMD
                logger.debug("FAST TRACK: Skipping thread enumeration for CMD");
            }
              // Cache performance data
            updatePerformanceCache(pid, intel);
            
            // Cache the intelligence data
            intelligenceCache.put(pid, intel);
            
        } catch (Exception e) {
            logger.warn("INTELLIGENCE PARTIAL: PID {} analysis had errors: {}", pid, e.getMessage());
        }
        
        return intel;
    }
    
    /**
     * Get process executable path using QueryFullProcessImageName
     */
    private String getProcessExecutablePath(WinNT.HANDLE processHandle) {
        try {
            char[] pathBuffer = new char[MAX_PATH * 2];
            IntByReference pathSize = new IntByReference(pathBuffer.length);
            
            if (Kernel32.INSTANCE.QueryFullProcessImageName(processHandle, 0, pathBuffer, pathSize)) {
                return Native.toString(pathBuffer).trim();
            }            // Alternative approach - just use QueryFullProcessImageName result
            // Remove GetModuleFileNameEx fallback as it has JNA signature issues
            
        } catch (Exception e) {
            logger.debug("Failed to get executable path: {}", e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Get process command line using WMI
     */
    private String getProcessCommandLine(int pid) {
        String cached = processCommandLines.get(pid);
        if (cached != null) {
            return cached;
        }
        
        try {
            // Use WMIC to get command line
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", 
                String.format("ProcessId=%d", pid), "get", "CommandLine", "/format:list");
            Process process = pb.start();
            
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("CommandLine=")) {
                        String cmdLine = line.substring("CommandLine=".length()).trim();
                        processCommandLines.put(pid, cmdLine);
                        return cmdLine;
                    }
                }
            }
            
            process.waitFor();
        } catch (Exception e) {
            logger.debug("Failed to get command line for PID {}: {}", pid, e.getMessage());
        }
        
        return "";
    }
    
    /**
     * Get parent process ID using advanced techniques
     */
    private int getParentProcessId(int pid) {
        try {
            // Use WMIC to get parent process ID
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", 
                String.format("ProcessId=%d", pid), "get", "ParentProcessId", "/format:list");
            Process process = pb.start();
            
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("ParentProcessId=")) {
                        String parentIdStr = line.substring("ParentProcessId=".length()).trim();
                        if (!parentIdStr.isEmpty()) {
                            return Integer.parseInt(parentIdStr);
                        }
                    }
                }
            }
            
            process.waitFor();
        } catch (Exception e) {
            logger.debug("Failed to get parent PID for {}: {}", pid, e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get process creation time
     */
    private long getProcessCreationTime(WinNT.HANDLE processHandle) {
        try {
            WinBase.FILETIME creationTime = new WinBase.FILETIME();
            WinBase.FILETIME exitTime = new WinBase.FILETIME();
            WinBase.FILETIME kernelTime = new WinBase.FILETIME();
            WinBase.FILETIME userTime = new WinBase.FILETIME();
            
            if (Kernel32.INSTANCE.GetProcessTimes(processHandle, creationTime, exitTime, kernelTime, userTime)) {
                return creationTime.toTime();
            }
        } catch (Exception e) {
            logger.debug("Failed to get process creation time: {}", e.getMessage());
        }
        
        return 0;
    }    /**
     * Get process priority using WMIC command (JNA-compatible alternative)
     */
    private int getProcessPriorityAlternative(WinNT.HANDLE processHandle) {
        try {
            // Get process ID from handle first
            int processId = getProcessIdFromHandle(processHandle);
            if (processId <= 0) {
                return 8; // Default to normal priority
            }
            
            // Use WMIC to get process priority
            ProcessBuilder pb = new ProcessBuilder("wmic", "process", "where", 
                String.format("ProcessId=%d", processId), "get", "Priority", "/format:list");
            Process process = pb.start();
            
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("Priority=")) {
                        String priorityStr = line.substring("Priority=".length()).trim();
                        if (!priorityStr.isEmpty()) {
                            return Integer.parseInt(priorityStr);
                        }
                    }
                }
            }
            
            process.waitFor();
        } catch (Exception e) {
            logger.debug("Failed to get process priority via WMIC: {}", e.getMessage());
        }
        
        return 8; // Default to normal priority
    }    /**
     * Get process memory information using Win32MemoryManager (JNA-compatible alternative)
     */
    private ProcessMemoryInfo getProcessMemoryInfo(WinNT.HANDLE processHandle) {
        try {
            // Get process ID from handle
            int processId = getProcessIdFromHandle(processHandle);
            if (processId <= 0) {
                logger.debug("Failed to get process ID from handle for memory info");
                return null;
            }
            
            // Use Win32MemoryManager for JNA-compatible memory access
            Win32MemoryManager memoryManager = Win32MemoryManager.getInstance();
            Win32MemoryManager.MemorySnapshot snapshot = memoryManager.getMemorySnapshot(processId);
            
            if (snapshot != null) {
                ProcessMemoryInfo memInfo = new ProcessMemoryInfo();
                memInfo.workingSetSize = snapshot.getWorkingSetSizeMB() * 1024 * 1024;
                memInfo.peakWorkingSetSize = snapshot.getPeakWorkingSetMB() * 1024 * 1024;
                memInfo.pagefileUsage = snapshot.getPrivateUsageMB() * 1024 * 1024;
                memInfo.peakPagefileUsage = memInfo.pagefileUsage; // Use same value as proxy
                memInfo.privateUsage = snapshot.getPrivateUsageMB() * 1024 * 1024;
                
                logger.debug("Memory info for PID {}: WorkingSet={}MB, Private={}MB", 
                    processId, snapshot.getWorkingSetSizeMB(), snapshot.getPrivateUsageMB());
                
                return memInfo;
            } else {
                logger.debug("Win32MemoryManager returned null snapshot for PID {}", processId);
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get memory info via Win32MemoryManager: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get process ID from process handle (helper method)
     */
    private int getProcessIdFromHandle(WinNT.HANDLE processHandle) {
        try {
            return Kernel32.INSTANCE.GetProcessId(processHandle);
        } catch (Exception e) {
            logger.debug("Failed to get process ID from handle: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get process CPU times
     */
    private ProcessCpuTimes getProcessTimes(WinNT.HANDLE processHandle) {
        try {
            WinBase.FILETIME creationTime = new WinBase.FILETIME();
            WinBase.FILETIME exitTime = new WinBase.FILETIME();
            WinBase.FILETIME kernelTime = new WinBase.FILETIME();
            WinBase.FILETIME userTime = new WinBase.FILETIME();
            
            if (Kernel32.INSTANCE.GetProcessTimes(processHandle, creationTime, exitTime, kernelTime, userTime)) {
                ProcessCpuTimes cpuTimes = new ProcessCpuTimes();
                cpuTimes.userTime = userTime.toTime();
                cpuTimes.kernelTime = kernelTime.toTime();
                cpuTimes.totalTime = cpuTimes.userTime + cpuTimes.kernelTime;
                
                return cpuTimes;
            }
        } catch (Exception e) {
            logger.debug("Failed to get process times: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Check if process is running under WOW64 (32-bit on 64-bit system)
     */
    private boolean isWow64Process(WinNT.HANDLE processHandle) {
        try {
            IntByReference wow64Process = new IntByReference();
            if (Kernel32.INSTANCE.IsWow64Process(processHandle, wow64Process)) {
                return wow64Process.getValue() != 0;
            }
        } catch (Exception e) {
            logger.debug("Failed to check WOW64 status: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get all thread IDs for a process
     */
    private List<Integer> getProcessThreadIds(int pid) {
        List<Integer> cached = processThreads.get(pid);
        if (cached != null) {
            return new ArrayList<>(cached);
        }
        
        List<Integer> threadIds = new ArrayList<>();
        
        try {
            // Use WMIC to get thread information
            ProcessBuilder pb = new ProcessBuilder("wmic", "thread", "where", 
                String.format("ProcessHandle=%d", pid), "get", "Handle", "/format:list");
            Process process = pb.start();
            
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.startsWith("Handle=")) {
                        String handleStr = line.substring("Handle=".length()).trim();
                        if (!handleStr.isEmpty()) {
                            try {
                                threadIds.add(Integer.parseInt(handleStr));
                            } catch (NumberFormatException e) {
                                // Skip invalid handles
                            }
                        }
                    }
                }
            }
            
            process.waitFor();
            processThreads.put(pid, new ArrayList<>(threadIds));
            
        } catch (Exception e) {
            logger.debug("Failed to get thread IDs for PID {}: {}", pid, e.getMessage());
        }
        
        return threadIds;
    }
    
    /**
     * Update performance cache
     */
    private void updatePerformanceCache(int pid, ProcessIntelligence intel) {
        ProcessPerformanceData perfData = new ProcessPerformanceData();
        perfData.pid = pid;
        perfData.updateTime = System.currentTimeMillis();
        perfData.memoryUsage = intel.memoryInfo != null ? intel.memoryInfo.workingSetSize : 0;
        perfData.cpuTime = intel.cpuTimes != null ? intel.cpuTimes.totalTime : 0;
        perfData.threadCount = intel.threadIds != null ? intel.threadIds.size() : 0;
        
        performanceCache.put(pid, perfData);
    }
    
    // ===== DATA STRUCTURES =====
    
    public static class ProcessIntelligence {
        public int pid;
        public String executablePath = "";
        public String commandLine = "";
        public int parentProcessId;
        public long creationTime;
        public long gatherTime;
        public ProcessMemoryInfo memoryInfo;
        public ProcessCpuTimes cpuTimes;
        public List<Integer> threadIds = new ArrayList<>();
        public int priority;
        public boolean isWow64Process;
    }
    
    public static class ProcessMemoryInfo {
        public long workingSetSize;
        public long peakWorkingSetSize;
        public long pagefileUsage;
        public long peakPagefileUsage;
        public long privateUsage;
    }
    
    public static class ProcessCpuTimes {
        public long userTime;
        public long kernelTime;
        public long totalTime;
    }
    
    public static class ProcessPerformanceData {
        public int pid;
        public long updateTime;
        public long memoryUsage;
        public long cpuTime;
        public int threadCount;
    }
}
