package com.automation.exceptions;

/**
 * Exception thrown when a window cannot be found or accessed
 */
public class WindowNotFoundException extends RuntimeException {
    
    private String windowTitle;
    private String windowClass;
    
    public WindowNotFoundException(String message) {
        super(message);
    }
    
    public WindowNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WindowNotFoundException(String message, String windowTitle) {
        super(message);
        this.windowTitle = windowTitle;
    }
    
    public WindowNotFoundException(String message, String windowTitle, String windowClass) {
        super(message);
        this.windowTitle = windowTitle;
        this.windowClass = windowClass;
    }
    
    public WindowNotFoundException(String message, String windowTitle, Throwable cause) {
        super(message, cause);
        this.windowTitle = windowTitle;
    }
    
    public String getWindowTitle() {
        return windowTitle;
    }
    
    public String getWindowClass() {
        return windowClass;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        
        if (windowTitle != null) {
            sb.append(" [Window Title: ").append(windowTitle).append("]");
        }
        
        if (windowClass != null) {
            sb.append(" [Window Class: ").append(windowClass).append("]");
        }
        
        return sb.toString();
    }
}
