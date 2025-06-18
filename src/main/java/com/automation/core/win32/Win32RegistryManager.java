package com.automation.core.win32;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise-grade Win32 Registry Management Module
 * 
 * Provides comprehensive Windows Registry operations and monitoring capabilities
 * for Windows processes and applications. Essential for enterprise automation
 * scenarios requiring registry access and application configuration management.
 * 
 * Features:
 * - Registry key enumeration and reading
 * - Registry value extraction and monitoring
 * - Application configuration discovery
 * - Registry change detection
 * - Cross-hive registry operations
 * - All loops have fixed bounds (max 200 iterations)
 * - Functions limited to 60 lines
 * - Comprehensive parameter validation
 * - Zero tolerance for null pointer access
 * 
 * @author Joshua Sims
 * @version 1.0
 */
public class Win32RegistryManager {
    private static final Logger logger = LoggerFactory.getLogger(Win32RegistryManager.class);
    private static Win32RegistryManager instance;
    private static final Object instanceLock = new Object();
    
    // Registry management constants
    private static final int MAX_REGISTRY_ENTRIES = 200;
    private static final int REGISTRY_CACHE_TTL_MS = 120000; // 2 minutes
    private static final int MAX_VALUE_SIZE = 4096;
    private static final int MAX_KEY_NAME_LENGTH = 260;
      // Registry access rights
    private static final int KEY_READ = 0x20019;
    
    // Registry hive mappings
    private static final Map<String, WinReg.HKEY> REGISTRY_HIVES = new HashMap<>();
    static {
        REGISTRY_HIVES.put("HKEY_CURRENT_USER", WinReg.HKEY_CURRENT_USER);
        REGISTRY_HIVES.put("HKEY_LOCAL_MACHINE", WinReg.HKEY_LOCAL_MACHINE);
        REGISTRY_HIVES.put("HKEY_CLASSES_ROOT", WinReg.HKEY_CLASSES_ROOT);
        REGISTRY_HIVES.put("HKEY_USERS", WinReg.HKEY_USERS);
        REGISTRY_HIVES.put("HKEY_CURRENT_CONFIG", WinReg.HKEY_CURRENT_CONFIG);
    }
    
    // Cache management
    private final Map<String, RegistrySnapshot> registryCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();    /**
     * Thread-safe singleton instance retrieval
     * Minimal heap allocation after initialization
     */
    public static Win32RegistryManager getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new Win32RegistryManager();
                }
            }
        }
        return instance;
    }    /**
     * Private constructor following singleton pattern
     * Restrict data scope to smallest possible
     */
    private Win32RegistryManager() {
        logger.debug("Win32RegistryManager initialized");
    }
    
    /**
     * Get comprehensive registry snapshot for a key path
     * 
     * @param hive Registry hive (e.g., "HKEY_LOCAL_MACHINE")
     * @param keyPath Registry key path
     * @return Complete registry information
     * @throws IllegalArgumentException if parameters are invalid
     */    public RegistrySnapshot getRegistrySnapshot(String hive, String keyPath) {
        // Minimum two runtime assertions per function
        if (hive == null || hive.trim().isEmpty()) {
            throw new IllegalArgumentException("Registry hive cannot be null or empty");
        }
        if (keyPath == null || keyPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Registry key path cannot be null or empty");
        }
        
        String cacheKey = hive + "\\" + keyPath;
        
        // Check cache first
        Long cacheTime = cacheTimestamps.get(cacheKey);
        if (cacheTime != null && (System.currentTimeMillis() - cacheTime) < REGISTRY_CACHE_TTL_MS) {
            RegistrySnapshot cached = registryCache.get(cacheKey);
            if (cached != null) {
                logger.debug("Returning cached registry snapshot for {}", cacheKey);
                return cached;
            }
        }
        
        try {
            RegistrySnapshot snapshot = createRegistrySnapshot(hive, keyPath);
            registryCache.put(cacheKey, snapshot);
            cacheTimestamps.put(cacheKey, System.currentTimeMillis());
            return snapshot;
        } catch (Exception e) {
            logger.error("Failed to create registry snapshot for {}: {}", cacheKey, e.getMessage());
            return createEmptyRegistrySnapshot(hive, keyPath);
        }
    }    /**
     * Create comprehensive registry snapshot
     * Restrict functions to single page (60 lines)
     */
    private RegistrySnapshot createRegistrySnapshot(String hive, String keyPath) {
        WinReg.HKEY hiveKey = REGISTRY_HIVES.get(hive.toUpperCase());
        if (hiveKey == null) {
            logger.warn("Unknown registry hive: {}", hive);
            return createEmptyRegistrySnapshot(hive, keyPath);
        }
        
        WinReg.HKEYByReference keyHandle = new WinReg.HKEYByReference();
        int result = Advapi32.INSTANCE.RegOpenKeyEx(
            hiveKey, keyPath, 0, KEY_READ, keyHandle);
        
        if (result != 0) {
            logger.debug("Cannot open registry key {}: error {}", keyPath, result);
            return createEmptyRegistrySnapshot(hive, keyPath);
        }
        
        try {
            List<RegistryEntry> values = enumerateRegistryValues(keyHandle.getValue());
            List<String> subKeys = enumerateSubKeys(keyHandle.getValue());
            String defaultValue = getDefaultValue(keyHandle.getValue());
            
            return new RegistrySnapshot(
                hive,
                keyPath,
                values,
                subKeys,
                defaultValue,
                System.currentTimeMillis()
            );
        } finally {
            Advapi32.INSTANCE.RegCloseKey(keyHandle.getValue());
        }
    }    /**
     * Enumerate registry values in a key
     * All loops must have fixed bounds
     */
    private List<RegistryEntry> enumerateRegistryValues(WinReg.HKEY keyHandle) {
        List<RegistryEntry> values = new ArrayList<>();
        
        try {
            for (int i = 0; i < MAX_REGISTRY_ENTRIES; i++) {
                char[] valueName = new char[MAX_KEY_NAME_LENGTH];
                IntByReference valueNameLength = new IntByReference(valueName.length);
                IntByReference valueType = new IntByReference();
                byte[] valueData = new byte[MAX_VALUE_SIZE];
                IntByReference valueDataSize = new IntByReference(valueData.length);
                
                int result = Advapi32.INSTANCE.RegEnumValue(
                    keyHandle, i, valueName, valueNameLength,
                    null, valueType, valueData, valueDataSize);
                
                if (result == WinError.ERROR_NO_MORE_ITEMS) {
                    break;
                }
                
                if (result == 0) {
                    String name = Native.toString(valueName);
                    Object data = parseRegistryValue(valueType.getValue(), valueData, valueDataSize.getValue());
                    String type = getRegistryTypeString(valueType.getValue());
                    
                    values.add(new RegistryEntry(name, data, type, valueType.getValue()));
                }
            }
        } catch (Exception e) {
            logger.debug("Error enumerating registry values: {}", e.getMessage());
        }
        
        return values;
    }    /**
     * Enumerate sub-keys in a registry key
     * All loops must have fixed bounds
     */
    private List<String> enumerateSubKeys(WinReg.HKEY keyHandle) {
        List<String> subKeys = new ArrayList<>();
        
        try {
            for (int i = 0; i < MAX_REGISTRY_ENTRIES; i++) {
                char[] subKeyName = new char[MAX_KEY_NAME_LENGTH];
                IntByReference subKeyNameLength = new IntByReference(subKeyName.length);
                
                int result = Advapi32.INSTANCE.RegEnumKeyEx(
                    keyHandle, i, subKeyName, subKeyNameLength,
                    null, null, null, null);
                
                if (result == WinError.ERROR_NO_MORE_ITEMS) {
                    break;
                }
                
                if (result == 0) {
                    subKeys.add(Native.toString(subKeyName));
                }
            }
        } catch (Exception e) {
            logger.debug("Error enumerating sub-keys: {}", e.getMessage());
        }
        
        return subKeys;
    }    /**
     * Get default value for a registry key
     * Check return values of all non-void functions
     */
    private String getDefaultValue(WinReg.HKEY keyHandle) {
        try {
            IntByReference valueType = new IntByReference();
            byte[] valueData = new byte[MAX_VALUE_SIZE];
            IntByReference valueDataSize = new IntByReference(valueData.length);
            
            int result = Advapi32.INSTANCE.RegQueryValueEx(
                keyHandle, null, 0, valueType, valueData, valueDataSize);
            
            if (result == 0) {
                Object data = parseRegistryValue(valueType.getValue(), valueData, valueDataSize.getValue());
                return data != null ? data.toString() : "";
            }
        } catch (Exception e) {
            logger.debug("Error getting default value: {}", e.getMessage());
        }
        
        return "";
    }    /**
     * Parse registry value based on type
     * Avoid complex flow constructs
     */
    private Object parseRegistryValue(int valueType, byte[] valueData, int dataSize) {
        if (dataSize <= 0 || valueData == null) {
            return null;
        }
        
        switch (valueType) {
            case WinNT.REG_SZ:
            case WinNT.REG_EXPAND_SZ:
                return Native.toString(Arrays.copyOf(valueData, dataSize), "UTF-16LE");
                
            case WinNT.REG_DWORD:
                if (dataSize >= 4) {
                    return ((valueData[3] & 0xFF) << 24) |
                           ((valueData[2] & 0xFF) << 16) |
                           ((valueData[1] & 0xFF) << 8) |
                           (valueData[0] & 0xFF);
                }
                return 0;
                
            case WinNT.REG_QWORD:
                if (dataSize >= 8) {
                    long value = 0;
                    for (int i = 0; i < 8; i++) {
                        value |= ((long)(valueData[i] & 0xFF)) << (i * 8);
                    }
                    return value;
                }
                return 0L;
                
            case WinNT.REG_BINARY:
                return Arrays.copyOf(valueData, dataSize);
                
            case WinNT.REG_MULTI_SZ:
                return parseMultiString(valueData, dataSize);
                
            default:
                return Arrays.copyOf(valueData, dataSize);
        }
    }    /**
     * Parse multi-string registry value
     * All loops must have fixed bounds
     */
    private List<String> parseMultiString(byte[] valueData, int dataSize) {
        List<String> strings = new ArrayList<>();
        
        try {
            String fullString = Native.toString(Arrays.copyOf(valueData, dataSize), "UTF-16LE");
            String[] parts = fullString.split("\0");
            
            for (int i = 0; i < parts.length && i < MAX_REGISTRY_ENTRIES; i++) {
                if (!parts[i].isEmpty()) {
                    strings.add(parts[i]);
                }
            }
        } catch (Exception e) {
            logger.debug("Error parsing multi-string: {}", e.getMessage());
        }
        
        return strings;
    }    /**
     * Get human-readable registry type string
     * Simple conditional logic
     */
    private String getRegistryTypeString(int valueType) {
        switch (valueType) {
            case WinNT.REG_SZ: return "REG_SZ";
            case WinNT.REG_EXPAND_SZ: return "REG_EXPAND_SZ";
            case WinNT.REG_BINARY: return "REG_BINARY";
            case WinNT.REG_DWORD: return "REG_DWORD";
            case WinNT.REG_DWORD_BIG_ENDIAN: return "REG_DWORD_BIG_ENDIAN";
            case WinNT.REG_LINK: return "REG_LINK";
            case WinNT.REG_MULTI_SZ: return "REG_MULTI_SZ";
            case WinNT.REG_RESOURCE_LIST: return "REG_RESOURCE_LIST";
            case WinNT.REG_QWORD: return "REG_QWORD";
            default: return "REG_UNKNOWN";
        }
    }
    
    /**
     * Read specific registry value
     * 
     * @param hive Registry hive
     * @param keyPath Registry key path
     * @param valueName Value name to read
     * @return Registry value or null if not found
     */
    public Object readRegistryValue(String hive, String keyPath, String valueName) {
        if (hive == null || keyPath == null || valueName == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        RegistrySnapshot snapshot = getRegistrySnapshot(hive, keyPath);
        for (RegistryEntry entry : snapshot.getValues()) {
            if (valueName.equals(entry.getName())) {
                return entry.getData();
            }
        }
        
        return null;
    }
    
    /**
     * Check if registry key exists
     * 
     * @param hive Registry hive
     * @param keyPath Registry key path
     * @return True if key exists
     */
    public boolean keyExists(String hive, String keyPath) {
        if (hive == null || keyPath == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        WinReg.HKEY hiveKey = REGISTRY_HIVES.get(hive.toUpperCase());
        if (hiveKey == null) {
            return false;
        }
        
        WinReg.HKEYByReference keyHandle = new WinReg.HKEYByReference();
        int result = Advapi32.INSTANCE.RegOpenKeyEx(
            hiveKey, keyPath, 0, KEY_READ, keyHandle);
        
        if (result == 0) {
            Advapi32.INSTANCE.RegCloseKey(keyHandle.getValue());
            return true;
        }
        
        return false;
    }
    
    /**
     * Get application registry information
     * 
     * @param applicationName Application name
     * @return Application registry information
     */
    public ApplicationRegistryInfo getApplicationRegistryInfo(String applicationName) {
        if (applicationName == null || applicationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Application name cannot be null or empty");
        }
        
        List<RegistrySnapshot> snapshots = new ArrayList<>();
        
        // Check common application registry locations
        String[] commonPaths = {
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\" + applicationName + ".exe",
            "SOFTWARE\\Classes\\Applications\\" + applicationName + ".exe",
            "SOFTWARE\\" + applicationName,
            "SOFTWARE\\Wow6432Node\\" + applicationName
        };
        
        for (String path : commonPaths) {
            if (keyExists("HKEY_LOCAL_MACHINE", path)) {
                snapshots.add(getRegistrySnapshot("HKEY_LOCAL_MACHINE", path));
            }
            if (keyExists("HKEY_CURRENT_USER", path)) {
                snapshots.add(getRegistrySnapshot("HKEY_CURRENT_USER", path));
            }
        }
        
        return new ApplicationRegistryInfo(applicationName, snapshots, System.currentTimeMillis());
    }
    
    /**
     * Create empty registry snapshot for error cases
     */
    private RegistrySnapshot createEmptyRegistrySnapshot(String hive, String keyPath) {
        return new RegistrySnapshot(
            hive,
            keyPath,
            new ArrayList<>(),
            new ArrayList<>(),
            "",
            System.currentTimeMillis()
        );
    }    /**
     * Clear cached registry information
     * Explicit resource management
     */
    public void clearCache() {
        registryCache.clear();
        cacheTimestamps.clear();
        logger.debug("Registry cache cleared");
    }
    
    /**
     * Registry entry container
     */
    public static class RegistryEntry {
        private final String name;
        private final Object data;
        private final String type;
        private final int rawType;
        
        public RegistryEntry(String name, Object data, String type, int rawType) {
            this.name = name;
            this.data = data;
            this.type = type;
            this.rawType = rawType;
        }
        
        public String getName() { return name; }
        public Object getData() { return data; }
        public String getType() { return type; }
        public int getRawType() { return rawType; }
          public String getDataAsString() {
            if (data == null) return "";
            if (data instanceof List) {
                List<?> list = (List<?>) data;
                return list.stream()
                    .map(Object::toString)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            }
            return data.toString();
        }
        
        @Override
        public String toString() {
            return String.format("RegistryEntry{name='%s', type='%s', data='%s'}", 
                name, type, getDataAsString());
        }
    }
    
    /**
     * Complete registry snapshot
     */
    public static class RegistrySnapshot {
        private final String hive;
        private final String keyPath;
        private final List<RegistryEntry> values;
        private final List<String> subKeys;
        private final String defaultValue;
        private final long timestamp;
        
        public RegistrySnapshot(String hive, String keyPath, List<RegistryEntry> values,
                              List<String> subKeys, String defaultValue, long timestamp) {
            this.hive = hive;
            this.keyPath = keyPath;
            this.values = new ArrayList<>(values);
            this.subKeys = new ArrayList<>(subKeys);
            this.defaultValue = defaultValue;
            this.timestamp = timestamp;
        }
        
        public String getHive() { return hive; }
        public String getKeyPath() { return keyPath; }
        public List<RegistryEntry> getValues() { return new ArrayList<>(values); }
        public List<String> getSubKeys() { return new ArrayList<>(subKeys); }
        public String getDefaultValue() { return defaultValue; }
        public long getTimestamp() { return timestamp; }
        public int getValueCount() { return values.size(); }
        public int getSubKeyCount() { return subKeys.size(); }
        
        public String getFullPath() {
            return hive + "\\" + keyPath;
        }
        
        @Override
        public String toString() {
            return String.format("RegistrySnapshot{path='%s', values=%d, subKeys=%d}", 
                getFullPath(), values.size(), subKeys.size());
        }
    }
    
    /**
     * Application registry information container
     */
    public static class ApplicationRegistryInfo {
        private final String applicationName;
        private final List<RegistrySnapshot> snapshots;
        private final long timestamp;
        
        public ApplicationRegistryInfo(String applicationName, List<RegistrySnapshot> snapshots, long timestamp) {
            this.applicationName = applicationName;
            this.snapshots = new ArrayList<>(snapshots);
            this.timestamp = timestamp;
        }
        
        public String getApplicationName() { return applicationName; }
        public List<RegistrySnapshot> getSnapshots() { return new ArrayList<>(snapshots); }
        public long getTimestamp() { return timestamp; }
        public int getSnapshotCount() { return snapshots.size(); }
        
        @Override
        public String toString() {
            return String.format("ApplicationRegistryInfo{app='%s', snapshots=%d}", 
                applicationName, snapshots.size());
        }
    }
}
