package com.automation.exceptions;

/**
 * Exception thrown when image matching operations fail
 */
public class ImageMatchException extends RuntimeException {
    
    private String templatePath;
    private String screenshotPath;
    private double similarity;
    private double threshold;
    
    public ImageMatchException(String message) {
        super(message);
    }
    
    public ImageMatchException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ImageMatchException(String message, String templatePath) {
        super(message);
        this.templatePath = templatePath;
    }
    
    public ImageMatchException(String message, String templatePath, Throwable cause) {
        super(message, cause);
        this.templatePath = templatePath;
    }
    
    public ImageMatchException(String message, String templatePath, double similarity, double threshold) {
        super(message);
        this.templatePath = templatePath;
        this.similarity = similarity;
        this.threshold = threshold;
    }
    
    public ImageMatchException(String message, String templatePath, String screenshotPath, double similarity, double threshold) {
        super(message);
        this.templatePath = templatePath;
        this.screenshotPath = screenshotPath;
        this.similarity = similarity;
        this.threshold = threshold;
    }
    
    public String getTemplatePath() {
        return templatePath;
    }
    
    public String getScreenshotPath() {
        return screenshotPath;
    }
    
    public double getSimilarity() {
        return similarity;
    }
    
    public double getThreshold() {
        return threshold;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        
        if (templatePath != null) {
            sb.append(" [Template: ").append(templatePath).append("]");
        }
        
        if (screenshotPath != null) {
            sb.append(" [Screenshot: ").append(screenshotPath).append("]");
        }
        
        if (similarity > 0) {
            sb.append(" [Similarity: ").append(String.format("%.2f%%", similarity * 100)).append("]");
        }
        
        if (threshold > 0) {
            sb.append(" [Threshold: ").append(String.format("%.2f%%", threshold * 100)).append("]");
        }
        
        return sb.toString();
    }
}
