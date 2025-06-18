package com.automation.models;

import java.awt.*;

/**
 * ScreenRegion represents a rectangular region on the screen
 */
public class ScreenRegion {
    private String name;
    private Rectangle bounds;
    private String description;
    private double confidence;
    private boolean isVisible;
    
    public ScreenRegion() {
        this.confidence = 1.0;
        this.isVisible = true;
    }
    
    public ScreenRegion(String name, Rectangle bounds) {
        this();
        this.name = name;
        this.bounds = bounds;
    }
    
    public ScreenRegion(String name, int x, int y, int width, int height) {
        this(name, new Rectangle(x, y, width, height));
    }
    
    public ScreenRegion(String name, Rectangle bounds, String description) {
        this(name, bounds);
        this.description = description;
    }
    
    public ScreenRegion(String name, int x, int y, int width, int height, String description) {
        this(name, x, y, width, height);
        this.description = description;
    }
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    
    public void setBounds(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void setVisible(boolean visible) {
        isVisible = visible;
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
    
    public Point getTopLeft() {
        return bounds != null ? new Point(bounds.x, bounds.y) : new Point(0, 0);
    }
    
    public Point getTopRight() {
        return bounds != null ? new Point(bounds.x + bounds.width, bounds.y) : new Point(0, 0);
    }
    
    public Point getBottomLeft() {
        return bounds != null ? new Point(bounds.x, bounds.y + bounds.height) : new Point(0, 0);
    }
    
    public Point getBottomRight() {
        return bounds != null ? new Point(bounds.x + bounds.width, bounds.y + bounds.height) : new Point(0, 0);
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
    
    public boolean intersects(ScreenRegion other) {
        return bounds != null && other.bounds != null && bounds.intersects(other.bounds);
    }
    
    public boolean intersects(Rectangle rectangle) {
        return bounds != null && bounds.intersects(rectangle);
    }
    
    public ScreenRegion getIntersection(ScreenRegion other) {
        if (bounds != null && other.bounds != null) {
            Rectangle intersection = bounds.intersection(other.bounds);
            if (!intersection.isEmpty()) {
                return new ScreenRegion(
                    name + "_intersection_" + other.name,
                    intersection,
                    "Intersection of " + name + " and " + other.name
                );
            }
        }
        return null;
    }
    
    public ScreenRegion getUnion(ScreenRegion other) {
        if (bounds != null && other.bounds != null) {
            Rectangle union = bounds.union(other.bounds);
            return new ScreenRegion(
                name + "_union_" + other.name,
                union,
                "Union of " + name + " and " + other.name
            );
        }
        return null;
    }
    
    public ScreenRegion expand(int pixels) {
        return expand(pixels, pixels, pixels, pixels);
    }
    
    public ScreenRegion expand(int left, int top, int right, int bottom) {
        if (bounds != null) {
            Rectangle expandedBounds = new Rectangle(
                bounds.x - left,
                bounds.y - top,
                bounds.width + left + right,
                bounds.height + top + bottom
            );
            
            return new ScreenRegion(
                name + "_expanded",
                expandedBounds,
                "Expanded " + description
            );
        }
        return null;
    }
    
    public ScreenRegion shrink(int pixels) {
        return shrink(pixels, pixels, pixels, pixels);
    }
    
    public ScreenRegion shrink(int left, int top, int right, int bottom) {
        if (bounds != null) {
            Rectangle shrunkBounds = new Rectangle(
                bounds.x + left,
                bounds.y + top,
                bounds.width - left - right,
                bounds.height - top - bottom
            );
            
            // Ensure the region is still valid
            if (shrunkBounds.width > 0 && shrunkBounds.height > 0) {
                return new ScreenRegion(
                    name + "_shrunk",
                    shrunkBounds,
                    "Shrunk " + description
                );
            }
        }
        return null;
    }
    
    public ScreenRegion moveTo(int x, int y) {
        if (bounds != null) {
            Rectangle movedBounds = new Rectangle(
                x,
                y,
                bounds.width,
                bounds.height
            );
            
            return new ScreenRegion(
                name + "_moved",
                movedBounds,
                "Moved " + description
            );
        }
        return null;
    }
    
    public ScreenRegion moveBy(int deltaX, int deltaY) {
        if (bounds != null) {
            Rectangle movedBounds = new Rectangle(
                bounds.x + deltaX,
                bounds.y + deltaY,
                bounds.width,
                bounds.height
            );
            
            return new ScreenRegion(
                name + "_offset",
                movedBounds,
                "Offset " + description
            );
        }
        return null;
    }
    
    public ScreenRegion resize(int width, int height) {
        if (bounds != null) {
            Rectangle resizedBounds = new Rectangle(
                bounds.x,
                bounds.y,
                width,
                height
            );
            
            return new ScreenRegion(
                name + "_resized",
                resizedBounds,
                "Resized " + description
            );
        }
        return null;
    }
    
    public ScreenRegion scale(double factor) {
        if (bounds != null) {
            int newWidth = (int) (bounds.width * factor);
            int newHeight = (int) (bounds.height * factor);
            int newX = bounds.x + (bounds.width - newWidth) / 2;
            int newY = bounds.y + (bounds.height - newHeight) / 2;
            
            Rectangle scaledBounds = new Rectangle(newX, newY, newWidth, newHeight);
            
            return new ScreenRegion(
                name + "_scaled",
                scaledBounds,
                "Scaled " + description + " by " + factor
            );
        }
        return null;
    }
    
    public double getArea() {
        return bounds != null ? bounds.width * bounds.height : 0;
    }
    
    public double getAspectRatio() {
        return bounds != null && bounds.height != 0 ? (double) bounds.width / bounds.height : 0;
    }
    
    public boolean isEmpty() {
        return bounds == null || bounds.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("ScreenRegion{name='%s', bounds=%s, description='%s', confidence=%.2f, visible=%s}",
                           name, bounds, description, confidence, isVisible);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ScreenRegion that = (ScreenRegion) obj;
        
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return bounds != null ? bounds.equals(that.bounds) : that.bounds == null;
    }
    
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (bounds != null ? bounds.hashCode() : 0);
        return result;
    }
}
