package com.automation.models;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of OCR text extraction operation
 * Contains extracted text, confidence level, and metadata
 */
public class OCRResult {
    private String text;
    private double confidence;
    private Rectangle boundingBox;
    private long timestamp;
    private List<WordResult> words;
    
    public OCRResult() {
        this.words = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public OCRResult(String text, double confidence) {
        this();
        this.text = text;
        this.confidence = confidence;
    }
    
    public OCRResult(String text, double confidence, Rectangle boundingBox) {
        this(text, confidence);
        this.boundingBox = boundingBox;
    }
    
    /**
     * Get the extracted text
     * @return Extracted text
     */
    public String getText() {
        return text != null ? text : "";
    }
    
    /**
     * Set the extracted text
     * @param text Extracted text
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Get the confidence level (0-100)
     * @return Confidence percentage
     */
    public double getConfidence() {
        return confidence;
    }
    
    /**
     * Set the confidence level
     * @param confidence Confidence percentage (0-100)
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    /**
     * Get the bounding box of the extracted text
     * @return Rectangle representing the text region
     */
    public Rectangle getBoundingBox() {
        return boundingBox;
    }
    
    /**
     * Set the bounding box of the extracted text
     * @param boundingBox Rectangle representing the text region
     */
    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    /**
     * Get the timestamp when OCR was performed
     * @return Timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Set the timestamp
     * @param timestamp Timestamp in milliseconds
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Get individual word results
     * @return List of word results
     */
    public List<WordResult> getWords() {
        return words;
    }
    
    /**
     * Set individual word results
     * @param words List of word results
     */
    public void setWords(List<WordResult> words) {
        this.words = words != null ? words : new ArrayList<>();
    }
    
    /**
     * Add a word result
     * @param word Word result to add
     */
    public void addWord(WordResult word) {
        if (word != null) {
            this.words.add(word);
        }
    }
    
    /**
     * Check if the OCR result has high confidence
     * @param threshold Minimum confidence threshold
     * @return True if confidence is above threshold
     */
    public boolean hasHighConfidence(double threshold) {
        return confidence >= threshold;
    }
    
    /**
     * Check if the OCR result contains specific text (case insensitive)
     * @param searchText Text to search for
     * @return True if text contains the search text
     */
    public boolean containsText(String searchText) {
        if (text == null || searchText == null) {
            return false;
        }
        return text.toLowerCase().contains(searchText.toLowerCase());
    }
    
    /**
     * Get text length
     * @return Length of extracted text
     */
    public int getTextLength() {
        return text != null ? text.length() : 0;
    }
    
    /**
     * Check if OCR result is empty
     * @return True if no text was extracted
     */
    public boolean isEmpty() {
        return text == null || text.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("OCRResult{text='%s', confidence=%.2f%%, words=%d}", 
            text, confidence, words.size());
    }
    
    /**
     * Represents a single word in the OCR result
     */
    public static class WordResult {
        private String word;
        private double confidence;
        private Rectangle boundingBox;
        
        public WordResult() {}
        
        public WordResult(String word, double confidence) {
            this.word = word;
            this.confidence = confidence;
        }
        
        public WordResult(String word, double confidence, Rectangle boundingBox) {
            this.word = word;
            this.confidence = confidence;
            this.boundingBox = boundingBox;
        }
        
        public String getWord() {
            return word;
        }
        
        public void setWord(String word) {
            this.word = word;
        }
        
        public double getConfidence() {
            return confidence;
        }
        
        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
        
        public Rectangle getBoundingBox() {
            return boundingBox;
        }
        
        public void setBoundingBox(Rectangle boundingBox) {
            this.boundingBox = boundingBox;
        }
        
        @Override
        public String toString() {
            return String.format("WordResult{word='%s', confidence=%.2f%%}", word, confidence);
        }
    }
}
