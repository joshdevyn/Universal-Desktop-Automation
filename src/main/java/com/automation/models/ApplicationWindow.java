package com.automation.models;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ApplicationWindow represents a Windows application window
 */
public class ApplicationWindow {
    private String name;
    private String title;
    private String className;
    private Rectangle bounds;
    private boolean isActive;
    private Map<String, Object> properties;
    
    public ApplicationWindow() {
        this.properties = new HashMap<>();
    }
    
    public ApplicationWindow(String name, String title) {
        this();
        this.name = name;
        this.title = title;
    }
    
    public ApplicationWindow(String name, String title, String className) {
        this(name, title);
        this.className = className;
    }
    
    public ApplicationWindow(String name, String title, Rectangle bounds) {
        this(name, title);
        this.bounds = bounds;
    }
    
    public ApplicationWindow(String name, String title, String className, Rectangle bounds) {
        this(name, title, className);
        this.bounds = bounds;
    }
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }
    
    public Object getProperty(String key) {
        return this.properties.get(key);
    }
    
    public String getPropertyAsString(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }
    
    public Integer getPropertyAsInteger(String key) {
        Object value = getProperty(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public Boolean getPropertyAsBoolean(String key) {
        Object value = getProperty(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
    
    // Utility methods
    
    public int getX() {
        return bounds != null ? bounds.x : 0;
    }
    
    public int getY() {
        return bounds != null ? bounds.y : 0;
    }
    
    public int getWidth() {
        return bounds != null ? bounds.width : 0;
    }
    
    public int getHeight() {
        return bounds != null ? bounds.height : 0;
    }
    
    public Point getCenter() {
        if (bounds != null) {
            return new Point(
                bounds.x + bounds.width / 2,
                bounds.y + bounds.height / 2
            );
        }
        return new Point(0, 0);
    }
    
    public boolean contains(Point point) {
        return bounds != null && bounds.contains(point);
    }
    
    public boolean contains(int x, int y) {
        return bounds != null && bounds.contains(x, y);
    }
    
    public Rectangle getRelativeRegion(int x, int y, int width, int height) {
        if (bounds == null) {
            return new Rectangle(x, y, width, height);
        }
        
        return new Rectangle(
            bounds.x + x,
            bounds.y + y,
            width,
            height
        );
    }
    
    public Rectangle getRelativeRegion(Rectangle relativeRect) {
        return getRelativeRegion(
            relativeRect.x,
            relativeRect.y,
            relativeRect.width,
            relativeRect.height
        );
    }
    
    @Override
    public String toString() {
        return String.format("ApplicationWindow{name='%s', title='%s', className='%s', bounds=%s, active=%s}", 
                           name, title, className, bounds, isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ApplicationWindow that = (ApplicationWindow) obj;
        
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return className != null ? className.equals(that.className) : that.className == null;
    }
    
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        return result;
    }
}
