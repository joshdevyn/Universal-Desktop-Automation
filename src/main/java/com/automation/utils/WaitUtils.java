package com.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * WaitUtils provides smart waiting strategies for automation
 */
public class WaitUtils {
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_POLLING_INTERVAL_MS = 500;
    
    private int defaultTimeoutSeconds;
    private int defaultPollingIntervalMs;
    
    public WaitUtils() {
        this.defaultTimeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
        this.defaultPollingIntervalMs = DEFAULT_POLLING_INTERVAL_MS;
    }
    
    public WaitUtils(int defaultTimeoutSeconds, int defaultPollingIntervalMs) {
        this.defaultTimeoutSeconds = defaultTimeoutSeconds;
        this.defaultPollingIntervalMs = defaultPollingIntervalMs;
    }
    
    /**
     * Wait for a condition to be true
     */
    public boolean waitForCondition(Supplier<Boolean> condition, int timeoutSeconds) {
        return waitForCondition(condition, timeoutSeconds, defaultPollingIntervalMs);
    }
    
    /**
     * Wait for a condition to be true with custom polling interval
     */
    public boolean waitForCondition(Supplier<Boolean> condition, int timeoutSeconds, int pollingIntervalMs) {
        logger.debug("Waiting for condition with timeout: {}s, polling: {}ms", timeoutSeconds, pollingIntervalMs);
        
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                if (condition.get()) {
                    logger.debug("Condition met after {}ms", System.currentTimeMillis() - startTime);
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Exception occurred while checking condition: {}", e.getMessage());
            }
            
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Wait interrupted");
                return false;
            }
        }
        
        logger.warn("Condition not met within timeout: {}s", timeoutSeconds);
        return false;
    }
    
    /**
     * Wait for a condition to be true using default timeout
     */
    public boolean waitForCondition(Supplier<Boolean> condition) {
        return waitForCondition(condition, defaultTimeoutSeconds);
    }
    
    /**
     * Wait for a specific value to be returned
     */
    public <T> T waitForValue(Supplier<T> valueSupplier, T expectedValue, int timeoutSeconds) {
        return waitForValue(valueSupplier, expectedValue, timeoutSeconds, defaultPollingIntervalMs);
    }
    
    /**
     * Wait for a specific value to be returned with custom polling interval
     */
    public <T> T waitForValue(Supplier<T> valueSupplier, T expectedValue, int timeoutSeconds, int pollingIntervalMs) {
        logger.debug("Waiting for value: {} with timeout: {}s", expectedValue, timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                T currentValue = valueSupplier.get();
                if (expectedValue.equals(currentValue)) {
                    logger.debug("Expected value found after {}ms", System.currentTimeMillis() - startTime);
                    return currentValue;
                }
            } catch (Exception e) {
                logger.warn("Exception occurred while getting value: {}", e.getMessage());
            }
            
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Wait interrupted");
                return null;
            }
        }
        
        logger.warn("Expected value not found within timeout: {}s", timeoutSeconds);
        return null;
    }
    
    /**
     * Wait for a non-null value to be returned
     */
    public <T> T waitForNonNullValue(Supplier<T> valueSupplier, int timeoutSeconds) {
        return waitForNonNullValue(valueSupplier, timeoutSeconds, defaultPollingIntervalMs);
    }
    
    /**
     * Wait for a non-null value to be returned with custom polling interval
     */
    public <T> T waitForNonNullValue(Supplier<T> valueSupplier, int timeoutSeconds, int pollingIntervalMs) {
        logger.debug("Waiting for non-null value with timeout: {}s", timeoutSeconds);
        
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                T value = valueSupplier.get();
                if (value != null) {
                    logger.debug("Non-null value found after {}ms", System.currentTimeMillis() - startTime);
                    return value;
                }
            } catch (Exception e) {
                logger.warn("Exception occurred while getting value: {}", e.getMessage());
            }
            
            try {
                Thread.sleep(pollingIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Wait interrupted");
                return null;
            }
        }
        
        logger.warn("Non-null value not found within timeout: {}s", timeoutSeconds);
        return null;
    }
    
    /**
     * Wait for a condition to become false
     */
    public boolean waitForConditionToBecomeFalse(Supplier<Boolean> condition, int timeoutSeconds) {
        return waitForConditionToBecomeFalse(condition, timeoutSeconds, defaultPollingIntervalMs);
    }
    
    /**
     * Wait for a condition to become false with custom polling interval
     */
    public boolean waitForConditionToBecomeFalse(Supplier<Boolean> condition, int timeoutSeconds, int pollingIntervalMs) {
        logger.debug("Waiting for condition to become false with timeout: {}s", timeoutSeconds);
        
        return waitForCondition(() -> !condition.get(), timeoutSeconds, pollingIntervalMs);
    }
    
    /**
     * Simple sleep with interruption handling
     */
    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted after {}ms", milliseconds);
        }
    }
    
    /**
     * Exponential backoff wait
     */
    public boolean waitWithExponentialBackoff(Supplier<Boolean> condition, int timeoutSeconds, int initialDelayMs, double multiplier, int maxDelayMs) {
        logger.debug("Waiting with exponential backoff - timeout: {}s, initial delay: {}ms, multiplier: {}, max delay: {}ms", 
                    timeoutSeconds, initialDelayMs, multiplier, maxDelayMs);
        
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        int currentDelay = initialDelayMs;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                if (condition.get()) {
                    logger.debug("Condition met after {}ms", System.currentTimeMillis() - startTime);
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Exception occurred while checking condition: {}", e.getMessage());
            }
            
            try {
                Thread.sleep(currentDelay);
                currentDelay = Math.min((int) (currentDelay * multiplier), maxDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Wait interrupted");
                return false;
            }
        }
        
        logger.warn("Condition not met within timeout: {}s", timeoutSeconds);
        return false;
    }
    
    /**
     * Wait with retry logic
     */
    public <T> T waitWithRetry(Supplier<T> operation, int maxRetries, int retryDelayMs) {
        return waitWithRetry(operation, maxRetries, retryDelayMs, null);
    }
    
    /**
     * Wait with retry logic and expected value
     */
    public <T> T waitWithRetry(Supplier<T> operation, int maxRetries, int retryDelayMs, T expectedValue) {
        logger.debug("Executing operation with retry - max retries: {}, delay: {}ms", maxRetries, retryDelayMs);
        
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                T result = operation.get();
                
                if (expectedValue == null || expectedValue.equals(result)) {
                    logger.debug("Operation successful on attempt {}", attempt);
                    return result;
                }
                
                logger.debug("Attempt {} returned unexpected value: {}", attempt, result);
                
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", attempt, e.getMessage());
            }
            
            if (attempt < maxRetries) {
                sleep(retryDelayMs);
            }
        }
        
        logger.error("Operation failed after {} attempts", maxRetries);
        if (lastException instanceof RuntimeException) {
            throw (RuntimeException) lastException;
        } else if (lastException != null) {
            throw new RuntimeException("Operation failed after retries", lastException);
        }
        
        return null;
    }
    
    /**
     * Wait for multiple conditions to be true
     */
    @SafeVarargs
    public final boolean waitForAllConditions(int timeoutSeconds, Supplier<Boolean>... conditions) {
        return waitForCondition(() -> {
            for (Supplier<Boolean> condition : conditions) {
                if (!condition.get()) {
                    return false;
                }
            }
            return true;
        }, timeoutSeconds);
    }    /**
     * Wait for any condition to be true
     */
    @SafeVarargs
    public final boolean waitForAnyCondition(int timeoutSeconds, Supplier<Boolean>... conditions) {
        return waitForCondition(() -> {
            for (Supplier<Boolean> condition : conditions) {
                if (condition.get()) {
                    return true;
                }
            }
            return false;
        }, timeoutSeconds);
    }
    
    /**
     * Wait with timeout and get the result
     */
    public <T> T waitAndGet(Supplier<T> supplier, int timeoutSeconds) {
        return waitAndGet(supplier, timeoutSeconds, defaultPollingIntervalMs);
    }
    
    /**
     * Wait with timeout and get the result with custom polling interval
     */
    public <T> T waitAndGet(Supplier<T> supplier, int timeoutSeconds, int pollingIntervalMs) {
        long startTime = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;
        T result = null;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                result = supplier.get();
                if (result != null) {
                    return result;
                }
            } catch (Exception e) {
                logger.debug("Exception during wait: {}", e.getMessage());
            }
            
            sleep(pollingIntervalMs);
        }
        
        return result;
    }
    
    // Getters and Setters
    
    public int getDefaultTimeoutSeconds() {
        return defaultTimeoutSeconds;
    }
    
    public void setDefaultTimeoutSeconds(int defaultTimeoutSeconds) {
        this.defaultTimeoutSeconds = defaultTimeoutSeconds;
    }
    
    public int getDefaultPollingIntervalMs() {
        return defaultPollingIntervalMs;
    }
    
    public void setDefaultPollingIntervalMs(int defaultPollingIntervalMs) {
        this.defaultPollingIntervalMs = defaultPollingIntervalMs;
    }
}
