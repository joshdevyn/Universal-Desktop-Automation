package com.automation.exceptions;

/**
 * Exception thrown when OCR operations fail
 */
public class OCRException extends RuntimeException {
    
    private String imagePath;
    private String ocrEngine;
    private double confidence;
    
    public OCRException(String message) {
        super(message);
    }
    
    public OCRException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OCRException(String message, String imagePath) {
        super(message);
        this.imagePath = imagePath;
    }
    
    public OCRException(String message, String imagePath, Throwable cause) {
        super(message, cause);
        this.imagePath = imagePath;
    }
    
    public OCRException(String message, String imagePath, String ocrEngine, double confidence) {
        super(message);
        this.imagePath = imagePath;
        this.ocrEngine = ocrEngine;
        this.confidence = confidence;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public String getOcrEngine() {
        return ocrEngine;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        
        if (imagePath != null) {
            sb.append(" [Image: ").append(imagePath).append("]");
        }
        
        if (ocrEngine != null) {
            sb.append(" [OCR Engine: ").append(ocrEngine).append("]");
        }
        
        if (confidence > 0) {
            sb.append(" [Confidence: ").append(confidence).append("%]");
        }
        
        return sb.toString();
    }
}
