/**
 * Win32SystemInfo - Comprehensive Windows system information gathering
 *  * This component provides detailed system intelligence including:
 * - Hardware configuration and capabilities
 * - Operating system version and build information
 * - Performance metrics and system health
 * - Network and security information
 * 
 * @author Joshua Sims
 * @version 1.0
 * @since 2025-06-17
 */
package com.automation.core.win32;

import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise-grade Windows system information collector
 */
public class Win32SystemInfo {
    private static final Logger logger = LoggerFactory.getLogger(Win32SystemInfo.class);
    private static Win32SystemInfo instance;
    private final Map<String, Object> systemCache;
    private long lastCacheUpdate;
    private static final long CACHE_VALIDITY_MS = 300000; // 5 minutes
      // Simple control flow - Private constructor for singleton
    private Win32SystemInfo() {
        this.systemCache = new ConcurrentHashMap<>();
        this.lastCacheUpdate = 0;
        logger.info("Win32SystemInfo initialized successfully");
    }
    
    // Check return value - Validated singleton pattern
    public static synchronized Win32SystemInfo getInstance() {
        if (instance == null) {
            instance = new Win32SystemInfo();
        }
        return instance;
    }
    
    /**
     * System information data structure
     */
    public static class SystemInformation {
        private final String computerName;
        private final String osVersion;
        private final String architecture;
        private final int processorCount;
        private final long totalMemory;
        private final long availableMemory;
        private final String systemDirectory;
        private final String windowsDirectory;
        private final Map<String, String> environmentVariables;
          // Minimum two assertions per function
        public SystemInformation(String computerName, String osVersion, String architecture,
                               int processorCount, long totalMemory, long availableMemory,
                               String systemDirectory, String windowsDirectory,
                               Map<String, String> environmentVariables) {
            if (computerName == null || computerName.trim().isEmpty()) {
                throw new IllegalArgumentException("Computer name cannot be null or empty");
            }
            if (processorCount <= 0) {
                throw new IllegalArgumentException("Processor count must be positive");
            }
            
            this.computerName = computerName;
            this.osVersion = osVersion != null ? osVersion : "Unknown";
            this.architecture = architecture != null ? architecture : "Unknown";
            this.processorCount = processorCount;
            this.totalMemory = totalMemory;
            this.availableMemory = availableMemory;
            this.systemDirectory = systemDirectory != null ? systemDirectory : "";
            this.windowsDirectory = windowsDirectory != null ? windowsDirectory : "";
            this.environmentVariables = environmentVariables != null ? 
                new HashMap<>(environmentVariables) : new HashMap<>();
        }
        
        // Restrict data scope - Getters only
        public String getComputerName() { return computerName; }
        public String getOsVersion() { return osVersion; }
        public String getArchitecture() { return architecture; }
        public int getProcessorCount() { return processorCount; }
        public long getTotalMemory() { return totalMemory; }
        public long getAvailableMemory() { return availableMemory; }
        public String getSystemDirectory() { return systemDirectory; }
        public String getWindowsDirectory() { return windowsDirectory; }
        public Map<String, String> getEnvironmentVariables() { 
            return new HashMap<>(environmentVariables); 
        }
        
        @Override
        public String toString() {
            return String.format("SystemInfo{computer='%s', os='%s', arch='%s', cpu=%d, memory=%d/%d}", 
                               computerName, osVersion, architecture, processorCount, 
                               availableMemory, totalMemory);
        }
    }
      /**
     * Gather comprehensive system information
     * Restrict function to single page (60 lines max)
     */
    public SystemInformation gatherSystemInformation() {
        try {
            // Get computer name
            String computerName = getComputerName();
            
            // Get OS version information
            String osVersion = getOSVersion();
            
            // Get system architecture
            String architecture = getSystemArchitecture();
            
            // Get processor information
            int processorCount = getProcessorCount();
            
            // Get memory information
            MemoryInfo memInfo = getMemoryInformation();
            
            // Get system directories
            String systemDir = getSystemDirectory();
            String windowsDir = getWindowsDirectory();
            
            // Get environment variables (limited set)
            Map<String, String> envVars = getKeyEnvironmentVariables();
            
            return new SystemInformation(
                computerName,
                osVersion,
                architecture,
                processorCount,
                memInfo.totalMemory,
                memInfo.availableMemory,
                systemDir,
                windowsDir,
                envVars
            );
            
        } catch (Exception e) {
            logger.error("Failed to gather system information: {}", e.getMessage());
            // Return minimal fallback information
            return new SystemInformation(
                "Unknown",
                "Unknown",
                "Unknown",
                1,
                0,
                0,
                "",
                "",
                new HashMap<>()
            );
        }
    }
    
    /**
     * Memory information structure
     */
    private static class MemoryInfo {
        final long totalMemory;
        final long availableMemory;
        
        MemoryInfo(long totalMemory, long availableMemory) {
            this.totalMemory = totalMemory;
            this.availableMemory = availableMemory;
        }
    }
      /**
     * Get computer name
     * Single page function
     */
    private String getComputerName() {
        try {
            char[] buffer = new char[256];
            IntByReference size = new IntByReference(buffer.length);
            
            boolean success = Kernel32.INSTANCE.GetComputerName(buffer, size);
            if (success) {
                return new String(buffer, 0, size.getValue());
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get computer name: {}", e.getMessage());
        }
        
        return "Unknown";
    }
      /**
     * Get operating system version
     * Single page function
     */
    private String getOSVersion() {
        try {
            WinNT.OSVERSIONINFO versionInfo = new WinNT.OSVERSIONINFO();
            boolean success = Kernel32.INSTANCE.GetVersionEx(versionInfo);
            
            if (success) {
                return String.format("Windows %d.%d Build %d", 
                                   versionInfo.dwMajorVersion,
                                   versionInfo.dwMinorVersion,
                                   versionInfo.dwBuildNumber);
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get OS version: {}", e.getMessage());
        }
        
        return "Unknown";
    }    /**
     * Get system architecture
     * Single page function
     */
    private String getSystemArchitecture() {
        try {
            // Use alternative approach for JNA compatibility
            String arch = getSystemArchitectureAlternative();
            if (arch != null && !arch.isEmpty()) {
                return arch;
            }
            
            // Fallback to environment variable
            String envArch = System.getenv("PROCESSOR_ARCHITECTURE");
            if (envArch != null) {
                switch (envArch.toUpperCase()) {
                    case "X86": return "x86";
                    case "AMD64": return "x64";
                    case "IA64": return "IA64";
                    default: return envArch;
                }
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get system architecture: {}", e.getMessage());
        }
        
        return "Unknown";
    }
      /**
     * Get processor count
     * Single page function
     */
    private int getProcessorCount() {
        try {
            Kernel32.SYSTEM_INFO systemInfo = new Kernel32.SYSTEM_INFO();
            Kernel32.INSTANCE.GetSystemInfo(systemInfo);
            
            return systemInfo.dwNumberOfProcessors.intValue();
            
        } catch (Exception e) {
            logger.debug("Failed to get processor count: {}", e.getMessage());
        }
        
        return 1; // Default fallback
    }
      /**
     * Get memory information
     * Single page function
     */
    private MemoryInfo getMemoryInformation() {
        try {
            Kernel32.MEMORYSTATUSEX memStatus = new Kernel32.MEMORYSTATUSEX();
            boolean success = Kernel32.INSTANCE.GlobalMemoryStatusEx(memStatus);
            
            if (success) {
                return new MemoryInfo(
                    memStatus.ullTotalPhys.longValue(),
                    memStatus.ullAvailPhys.longValue()
                );
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get memory information: {}", e.getMessage());
        }
        
        return new MemoryInfo(0, 0);
    }    /**
     * Get system directory
     * Single page function
     */
    private String getSystemDirectory() {
        try {
            // Use alternative approach for JNA compatibility
            String systemDir = getSystemDirectoryAlternative();
            if (systemDir != null && !systemDir.isEmpty()) {
                return systemDir;
            }
            
            // Fallback to environment variable
            String winDir = System.getenv("WINDIR");
            if (winDir != null) {
                return winDir + "\\System32";
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get system directory: {}", e.getMessage());
        }
        
        return "";
    }    /**
     * Get Windows directory
     * Single page function
     */
    private String getWindowsDirectory() {
        try {
            // Use alternative approach for JNA compatibility
            String winDir = getWindowsDirectoryAlternative();
            if (winDir != null && !winDir.isEmpty()) {
                return winDir;
            }
            
            // Fallback to environment variable
            String envWinDir = System.getenv("WINDIR");
            if (envWinDir != null) {
                return envWinDir;
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get Windows directory: {}", e.getMessage());
        }
        
        return "";
    }
      /**
     * Get key environment variables
     * Single page function
     */
    private Map<String, String> getKeyEnvironmentVariables() {
        Map<String, String> envVars = new HashMap<>();
        
        // List of important environment variables
        String[] keyVars = {
            "PATH", "TEMP", "TMP", "USERNAME", "COMPUTERNAME",
            "PROCESSOR_ARCHITECTURE", "NUMBER_OF_PROCESSORS",
            "OS", "PATHEXT"
        };
          // Fixed bounds loop
        for (int i = 0; i < keyVars.length; i++) {
            try {
                String value = System.getenv(keyVars[i]);
                if (value != null) {
                    envVars.put(keyVars[i], value);
                }
            } catch (Exception e) {
                logger.debug("Failed to get env var {}: {}", keyVars[i], e.getMessage());
            }
        }
        
        return envVars;
    }
      /**
     * Get cached system information
     * Check return values
     */
    public SystemInformation getCachedSystemInformation() {
        long currentTime = System.currentTimeMillis();
        
        // Check if cache is still valid
        if (currentTime - lastCacheUpdate < CACHE_VALIDITY_MS && 
            systemCache.containsKey("systemInfo")) {
            return (SystemInformation) systemCache.get("systemInfo");
        }
        
        // Gather fresh information
        SystemInformation freshInfo = gatherSystemInformation();
        systemCache.put("systemInfo", freshInfo);
        lastCacheUpdate = currentTime;
        
        return freshInfo;
    }
      /**
     * Clear system information cache
     * Simple control flow
     */
    public void clearCache() {
        systemCache.clear();
        lastCacheUpdate = 0;
        logger.debug("System information cache cleared");
    }
      /**
     * Get system health metrics
     * Single page function
     */
    public Map<String, Object> getSystemHealthMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            SystemInformation sysInfo = getCachedSystemInformation();
            
            // Calculate memory usage percentage
            double memoryUsagePercent = 0.0;
            if (sysInfo.getTotalMemory() > 0) {
                memoryUsagePercent = ((double)(sysInfo.getTotalMemory() - sysInfo.getAvailableMemory()) / 
                                    sysInfo.getTotalMemory()) * 100.0;
            }
            
            metrics.put("memoryUsagePercent", memoryUsagePercent);
            metrics.put("totalMemoryMB", sysInfo.getTotalMemory() / (1024 * 1024));
            metrics.put("availableMemoryMB", sysInfo.getAvailableMemory() / (1024 * 1024));
            metrics.put("processorCount", sysInfo.getProcessorCount());
            metrics.put("osVersion", sysInfo.getOsVersion());
            metrics.put("architecture", sysInfo.getArchitecture());
            
        } catch (Exception e) {
            logger.error("Failed to get system health metrics: {}", e.getMessage());
        }
        
        return metrics;
    }
      /**
     * Validate system capabilities
     * Single page function
     */
    public boolean validateSystemCapabilities() {        try {
            SystemInformation sysInfo = getCachedSystemInformation();
            
            // Basic validation checks
            boolean hasValidMemory = sysInfo.getTotalMemory() > 0;
            boolean hasValidCPU = sysInfo.getProcessorCount() > 0;
            boolean hasValidName = !sysInfo.getComputerName().equals("Unknown");
            
            return hasValidMemory && hasValidCPU && hasValidName;
            
        } catch (Exception e) {
            logger.error("System capability validation failed: {}", e.getMessage());
            return false;
        }
    }
      /**
     * Alternative method to get system architecture for JNA compatibility
     * Single page function
     */
    private String getSystemArchitectureAlternative() {
        try {
            // Try to get architecture from Java system properties first
            String osArch = System.getProperty("os.arch");
            if (osArch != null) {
                switch (osArch.toLowerCase()) {
                    case "x86": 
                    case "i386": return "x86";
                    case "amd64": 
                    case "x86_64": return "x64";
                    case "ia64": return "IA64";
                    default: return osArch;
                }
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get architecture alternative: {}", e.getMessage());
        }
        
        return null;
    }
      /**
     * Alternative method to get system directory for JNA compatibility
     * Single page function
     */
    private String getSystemDirectoryAlternative() {
        try {
            // Try multiple approaches for getting system directory
            String systemRoot = System.getenv("SystemRoot");
            if (systemRoot != null) {
                return systemRoot + "\\System32";
            }
            
            // Fallback to common Windows paths
            String[] commonPaths = {
                "C:\\Windows\\System32",
                "C:\\WINNT\\System32"
            };
            
            for (String path : commonPaths) {
                if (new java.io.File(path).exists()) {
                    return path;
                }
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get system directory alternative: {}", e.getMessage());
        }
        
        return null;
    }
      /**
     * Alternative method to get Windows directory for JNA compatibility
     * Single page function
     */
    private String getWindowsDirectoryAlternative() {
        try {
            // Try multiple approaches for getting Windows directory
            String systemRoot = System.getenv("SystemRoot");
            if (systemRoot != null) {
                return systemRoot;
            }
            
            String winDir = System.getenv("WINDIR");
            if (winDir != null) {
                return winDir;
            }
            
            // Fallback to common Windows paths
            String[] commonPaths = {
                "C:\\Windows",
                "C:\\WINNT"
            };
            
            for (String path : commonPaths) {
                if (new java.io.File(path).exists()) {
                    return path;
                }
            }
            
        } catch (Exception e) {
            logger.debug("Failed to get Windows directory alternative: {}", e.getMessage());
        }
        
        return null;
    }
}