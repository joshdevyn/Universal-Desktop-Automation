package com.automation.exceptions;

/**
 * Base exception class for automation framework errors
 */
public class AutomationException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public AutomationException(String message) {
        super(message);
    }
    
    public AutomationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AutomationException(Throwable cause) {
        super(cause);
    }
}
