package com.automation.core;

import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ImageMatcher provides image recognition and matching capabilities using SikuliX
 */
public class ImageMatcher {
    private static final Logger logger = LoggerFactory.getLogger(ImageMatcher.class);
    
    private Screen screen;
    private double defaultSimilarity = 0.8;
    private String templateDirectory;
    
    public ImageMatcher() {
        this("src/main/resources/images/templates");
    }
    
    public ImageMatcher(String templateDirectory) {
        this.templateDirectory = templateDirectory;
        this.screen = new Screen();
        
        // Create template directory if it doesn't exist
        createDirectoryIfNotExists(templateDirectory);
        
        logger.info("ImageMatcher initialized with template directory: {}", templateDirectory);
    }
    
    /**
     * Find image on screen
     */
    public Match findImage(String imagePath) {
        return findImage(imagePath, defaultSimilarity);
    }
    
    /**
     * Find image on screen with custom similarity threshold
     */
    public Match findImage(String imagePath, double similarity) {
        try {
            String fullPath = getFullImagePath(imagePath);
            Pattern pattern = new Pattern(fullPath).similar((float) similarity);
            
            Match match = screen.find(pattern);
            
            if (match != null) {
                logger.debug("Image found: {} at ({}, {}) with similarity {:.2f}", 
                    imagePath, match.x, match.y, match.getScore());
            }
            
            return match;
            
        } catch (FindFailed e) {
            logger.debug("Image not found: {}", imagePath);
            return null;
        } catch (Exception e) {
            logger.error("Error finding image: {}", imagePath, e);
            return null;
        }
    }
    
    /**
     * Find all instances of an image on screen
     */
    public List<Match> findAllImages(String imagePath) {
        return findAllImages(imagePath, defaultSimilarity);
    }
    
    /**
     * Find all instances of an image on screen with custom similarity
     */
    public List<Match> findAllImages(String imagePath, double similarity) {
        List<Match> matches = new ArrayList<>();
        
        try {
            String fullPath = getFullImagePath(imagePath);
            Pattern pattern = new Pattern(fullPath).similar((float) similarity);
            
            Iterator<Match> findAll = screen.findAll(pattern);
            while (findAll.hasNext()) {
                matches.add(findAll.next());
            }
            
            logger.debug("Found {} instances of image: {}", matches.size(), imagePath);
            
        } catch (FindFailed e) {
            logger.debug("No instances of image found: {}", imagePath);
        } catch (Exception e) {
            logger.error("Error finding all images: {}", imagePath, e);
        }
        
        return matches;
    }
    
    /**
     * Wait for image to appear on screen
     */
    public Match waitForImage(String imagePath, int timeoutSeconds) {
        return waitForImage(imagePath, timeoutSeconds, defaultSimilarity);
    }
    
    /**
     * Wait for image to appear on screen with custom similarity
     */
    public Match waitForImage(String imagePath, int timeoutSeconds, double similarity) {
        try {
            String fullPath = getFullImagePath(imagePath);
            Pattern pattern = new Pattern(fullPath).similar((float) similarity);
            
            Match match = screen.wait(pattern, timeoutSeconds);
            
            if (match != null) {
                logger.info("Image appeared: {} after waiting", imagePath);
            }
            
            return match;
            
        } catch (FindFailed e) {
            logger.warn("Image did not appear within {} seconds: {}", timeoutSeconds, imagePath);
            return null;
        } catch (Exception e) {
            logger.error("Error waiting for image: {}", imagePath, e);
            return null;
        }
    }
    
    /**
     * Wait for image to disappear from screen
     */
    public boolean waitForImageToDisappear(String imagePath, int timeoutSeconds) {
        return waitForImageToDisappear(imagePath, timeoutSeconds, defaultSimilarity);
    }
    
    /**
     * Wait for image to disappear from screen with custom similarity
     */
    public boolean waitForImageToDisappear(String imagePath, int timeoutSeconds, double similarity) {        try {
            String fullPath = getFullImagePath(imagePath);
            Pattern pattern = new Pattern(fullPath).similar((float) similarity);
            
            screen.waitVanish(pattern, timeoutSeconds);
            logger.info("Image disappeared: {}", imagePath);
            return true;
            
        } catch (Exception e) {
            logger.warn("Image still visible after {} seconds: {}", timeoutSeconds, imagePath);
            return false;
        }
    }
    
    /**
     * Click on image if found
     */
    public boolean clickOnImage(String imagePath) {
        return clickOnImage(imagePath, defaultSimilarity);
    }
    
    /**
     * Click on image if found with custom similarity
     */
    public boolean clickOnImage(String imagePath, double similarity) {
        Match match = findImage(imagePath, similarity);
        
        if (match != null) {
            try {
                screen.click(match);
                logger.info("Clicked on image: {}", imagePath);
                return true;
            } catch (Exception e) {
                logger.error("Failed to click on image: {}", imagePath, e);
                return false;
            }
        }
        
        logger.warn("Cannot click - image not found: {}", imagePath);
        return false;
    }
    
    /**
     * Double click on image if found
     */
    public boolean doubleClickOnImage(String imagePath) {
        return doubleClickOnImage(imagePath, defaultSimilarity);
    }
    
    /**
     * Double click on image if found with custom similarity
     */
    public boolean doubleClickOnImage(String imagePath, double similarity) {
        Match match = findImage(imagePath, similarity);
        
        if (match != null) {
            try {
                screen.doubleClick(match);
                logger.info("Double-clicked on image: {}", imagePath);
                return true;
            } catch (Exception e) {
                logger.error("Failed to double-click on image: {}", imagePath, e);
                return false;
            }
        }
        
        logger.warn("Cannot double-click - image not found: {}", imagePath);
        return false;
    }
    
    /**
     * Right click on image if found
     */
    public boolean rightClickOnImage(String imagePath) {
        return rightClickOnImage(imagePath, defaultSimilarity);
    }
    
    /**
     * Right click on image if found with custom similarity
     */
    public boolean rightClickOnImage(String imagePath, double similarity) {
        Match match = findImage(imagePath, similarity);
        
        if (match != null) {
            try {
                screen.rightClick(match);
                logger.info("Right-clicked on image: {}", imagePath);
                return true;
            } catch (Exception e) {
                logger.error("Failed to right-click on image: {}", imagePath, e);
                return false;
            }
        }
        
        logger.warn("Cannot right-click - image not found: {}", imagePath);
        return false;
    }
    
    /**
     * Type text after clicking on image
     */
    public boolean typeOnImage(String imagePath, String text) {
        return typeOnImage(imagePath, text, defaultSimilarity);
    }
    
    /**
     * Type text after clicking on image with custom similarity
     */
    public boolean typeOnImage(String imagePath, String text, double similarity) {
        if (clickOnImage(imagePath, similarity)) {
            try {
                screen.type(text);
                logger.info("Typed '{}' after clicking on image: {}", text, imagePath);
                return true;
            } catch (Exception e) {
                logger.error("Failed to type text after clicking on image: {}", imagePath, e);
                return false;
            }
        }
        
        return false;
    }
      /**
     * Compare two images for similarity
     */
    public double compareImages(String image1Path, String image2Path) {
        try {
            String fullPath1 = getFullImagePath(image1Path);
            String fullPath2 = getFullImagePath(image2Path);
            
            Pattern pattern1 = new Pattern(fullPath1);
            Pattern pattern2 = new Pattern(fullPath2);
            
            // Find pattern1 on screen first
            Match match1 = screen.find(pattern1);
            if (match1 == null) {
                logger.debug("First image not found on screen: {}", image1Path);
                return 0.0;
            }
            
            // Try to find pattern2 in the same region as pattern1
            Region matchRegion = new Region(match1.x, match1.y, match1.w, match1.h);
            try {
                Match match2 = matchRegion.find(pattern2);
                if (match2 != null) {
                    // Return the similarity score of the second pattern match
                    double similarity = match2.getScore();
                    logger.debug("Images compared - similarity: {:.2f}", similarity);
                    return similarity;
                }
            } catch (FindFailed e) {
                logger.debug("Second image not found in comparison region: {}", image2Path);
            }
            
            return 0.0;
            
        } catch (Exception e) {
            logger.error("Error comparing images: {} vs {}", image1Path, image2Path, e);
            return 0.0;
        }
    }
    
    /**
     * Capture region around found image
     */
    public BufferedImage captureImageRegion(String imagePath, int expandPixels) {
        Match match = findImage(imagePath);
        
        if (match != null) {
            try {
                Region expandedRegion = match.grow(expandPixels);
                ScreenImage screenImage = screen.capture(expandedRegion);
                
                logger.debug("Captured region around image: {}", imagePath);
                return screenImage.getImage();
                
            } catch (Exception e) {
                logger.error("Failed to capture region around image: {}", imagePath, e);
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Find image within a specific region
     */
    public Match findImageInRegion(String imagePath, Rectangle regionBounds) {
        return findImageInRegion(imagePath, regionBounds, defaultSimilarity);
    }
    
    /**
     * Find image within a specific region with custom similarity
     */
    public Match findImageInRegion(String imagePath, Rectangle regionBounds, double similarity) {
        try {
            Region region = new Region(
                regionBounds.x, regionBounds.y,
                regionBounds.width, regionBounds.height
            );
            
            String fullPath = getFullImagePath(imagePath);
            Pattern pattern = new Pattern(fullPath).similar((float) similarity);
            
            Match match = region.find(pattern);
            
            if (match != null) {
                logger.debug("Image found in region: {} at ({}, {})", 
                    imagePath, match.x, match.y);
            }
            
            return match;
            
        } catch (FindFailed e) {
            logger.debug("Image not found in region: {}", imagePath);
            return null;
        } catch (Exception e) {
            logger.error("Error finding image in region: {}", imagePath, e);
            return null;
        }
    }
    
    /**
     * Save screen region as template image
     */
    public String saveTemplate(Rectangle region, String templateName) {
        try {
            Region screenRegion = new Region(
                region.x, region.y,
                region.width, region.height
            );
            
            ScreenImage screenImage = screen.capture(screenRegion);
            
            if (!templateName.toLowerCase().endsWith(".png")) {
                templateName += ".png";
            }
            
            String filePath = new File(templateDirectory, templateName).getAbsolutePath();
            screenImage.save(filePath);
            
            logger.info("Template saved: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Failed to save template: {}", templateName, e);
            return null;
        }
    }

    /**
     * Calculate similarity between two images (Files)
     */
    public double calculateSimilarity(File image1File, File image2File) {
        try {
            BufferedImage img1 = javax.imageio.ImageIO.read(image1File);
            BufferedImage img2 = javax.imageio.ImageIO.read(image2File);
            return calculateSimilarity(img1, img2);
        } catch (Exception e) {
            logger.error("Error calculating similarity between files: {} and {}", 
                image1File.getName(), image2File.getName(), e);
            return 0.0;
        }
    }

    /**
     * Calculate similarity between two BufferedImages
     */
    public double calculateSimilarity(BufferedImage img1, BufferedImage img2) {
        if (img1 == null || img2 == null) {
            return 0.0;
        }

        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            logger.debug("Images have different dimensions - resizing for comparison");
            // Resize to smaller dimensions for comparison
            int width = Math.min(img1.getWidth(), img2.getWidth());
            int height = Math.min(img1.getHeight(), img2.getHeight());
            
            img1 = resizeImage(img1, width, height);
            img2 = resizeImage(img2, width, height);
        }

        int width = img1.getWidth();
        int height = img1.getHeight();
        long totalPixels = (long) width * height;
        long matchingPixels = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (img1.getRGB(x, y) == img2.getRGB(x, y)) {
                    matchingPixels++;
                }
            }
        }

        double similarity = (double) matchingPixels / totalPixels;
        logger.debug("Image similarity calculated: {:.2f}%", similarity * 100);
        return similarity;
    }

    /**
     * Calculate similarity with tolerance for slight color differences
     */
    public double calculateSimilarityWithTolerance(BufferedImage img1, BufferedImage img2, int tolerance) {
        if (img1 == null || img2 == null) {
            return 0.0;
        }

        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            // Resize to smaller dimensions for comparison
            int width = Math.min(img1.getWidth(), img2.getWidth());
            int height = Math.min(img1.getHeight(), img2.getHeight());
            
            img1 = resizeImage(img1, width, height);
            img2 = resizeImage(img2, width, height);
        }

        int width = img1.getWidth();
        int height = img1.getHeight();
        long totalPixels = (long) width * height;
        long similarPixels = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color c1 = new Color(img1.getRGB(x, y));
                Color c2 = new Color(img2.getRGB(x, y));
                
                if (isColorSimilar(c1, c2, tolerance)) {
                    similarPixels++;
                }
            }
        }

        double similarity = (double) similarPixels / totalPixels;
        logger.debug("Image similarity with tolerance {}: {:.2f}%", tolerance, similarity * 100);
        return similarity;
    }

    /**
     * Check if two colors are similar within tolerance
     */
    private boolean isColorSimilar(Color c1, Color c2, int tolerance) {
        return Math.abs(c1.getRed() - c2.getRed()) <= tolerance &&
               Math.abs(c1.getGreen() - c2.getGreen()) <= tolerance &&
               Math.abs(c1.getBlue() - c2.getBlue()) <= tolerance;
    }

    /**
     * Resize image helper method
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        if (originalImage == null) {
            return null;
        }
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return resizedImage;
    }

    /**
     * Get full path for image file
     */
    private String getFullImagePath(String imagePath) {
        File imageFile = new File(imagePath);
        
        if (imageFile.isAbsolute() && imageFile.exists()) {
            return imagePath;
        }
        
        // Try in template directory
        File templateFile = new File(templateDirectory, imagePath);
        if (templateFile.exists()) {
            return templateFile.getAbsolutePath();
        }
        
        // Try adding .png extension if not present
        if (!imagePath.toLowerCase().endsWith(".png")) {
            templateFile = new File(templateDirectory, imagePath + ".png");
            if (templateFile.exists()) {
                return templateFile.getAbsolutePath();
            }
        }
        
        // Return original path (may cause error if file doesn't exist)
        return imagePath;
    }
    
    /**
     * Create directory if it doesn't exist
     */
    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Created template directory: {}", directoryPath);
            } else {
                logger.warn("Failed to create template directory: {}", directoryPath);
            }
        }
    }
    
    // Getters and Setters
    
    public double getDefaultSimilarity() {
        return defaultSimilarity;
    }
    
    public void setDefaultSimilarity(double defaultSimilarity) {
        this.defaultSimilarity = defaultSimilarity;
        logger.debug("Default similarity set to: {:.2f}", defaultSimilarity);
    }
    
    public String getTemplateDirectory() {
        return templateDirectory;
    }
    
    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
        createDirectoryIfNotExists(templateDirectory);
    }
    
    public Screen getScreen() {
        return screen;
    }
    
    /**
     * Find image in screenshot using BufferedImage objects
     * Returns Rectangle with coordinates or null if not found
     */
    public Rectangle findImage(BufferedImage screenshot, BufferedImage template) {
        if (screenshot == null || template == null) {
            logger.warn("Screenshot or template image is null");
            return null;
        }
        
        try {
            // Convert BufferedImages to temporary files for SikuliX
            File screenshotFile = File.createTempFile("screenshot", ".png");
            File templateFile = File.createTempFile("template", ".png");
            
            javax.imageio.ImageIO.write(screenshot, "PNG", screenshotFile);
            javax.imageio.ImageIO.write(template, "PNG", templateFile);
            
            // Use file-based findImage method
            Rectangle result = findImage(screenshotFile, templateFile);
            
            // Clean up temporary files
            screenshotFile.delete();
            templateFile.delete();
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error finding image using BufferedImage objects", e);
            return null;
        }
    }
    
    /**
     * Find image in screenshot using File objects
     * Returns Rectangle with coordinates or null if not found
     */
    public Rectangle findImage(File screenshot, File template) {
        if (screenshot == null || template == null || !screenshot.exists() || !template.exists()) {
            logger.warn("Screenshot or template file is null or doesn't exist");
            return null;
        }
          try {
            // We'll load both images and use our custom similarity calculation
            BufferedImage screenshotImg = javax.imageio.ImageIO.read(screenshot);
            BufferedImage templateImg = javax.imageio.ImageIO.read(template);
            
            // Search for template in screenshot using sliding window
            Rectangle match = findImageInScreenshot(screenshotImg, templateImg);
            
            if (match != null) {
                logger.debug("Image found at ({}, {}) with size {}x{}", 
                    match.x, match.y, match.width, match.height);
            }
            
            return match;
            
        } catch (Exception e) {
            logger.error("Error finding image using File objects", e);
            return null;
        }
    }
    
    /**
     * Find template image in screenshot using sliding window approach
     */
    private Rectangle findImageInScreenshot(BufferedImage screenshot, BufferedImage template) {
        if (screenshot == null || template == null) {
            return null;
        }
        
        int screenshotWidth = screenshot.getWidth();
        int screenshotHeight = screenshot.getHeight();
        int templateWidth = template.getWidth();
        int templateHeight = template.getHeight();
        
        if (templateWidth > screenshotWidth || templateHeight > screenshotHeight) {
            logger.warn("Template image is larger than screenshot");
            return null;
        }
        
        double bestSimilarity = 0.0;
        Rectangle bestMatch = null;
        
        // Sliding window search
        for (int x = 0; x <= screenshotWidth - templateWidth; x += 5) { // Step by 5 pixels for performance
            for (int y = 0; y <= screenshotHeight - templateHeight; y += 5) {
                // Extract region from screenshot
                BufferedImage region = screenshot.getSubimage(x, y, templateWidth, templateHeight);
                
                // Calculate similarity
                double similarity = calculateSimilarity(region, template);
                
                if (similarity > bestSimilarity && similarity >= defaultSimilarity) {
                    bestSimilarity = similarity;
                    bestMatch = new Rectangle(x, y, templateWidth, templateHeight);
                    
                    // If we found a very good match, stop searching
                    if (similarity > 0.95) {
                        break;
                    }
                }
            }
            
            // Early exit if we found a very good match
            if (bestSimilarity > 0.95) {
                break;
            }
        }
        
        if (bestMatch != null) {
            logger.debug("Best match found with similarity: {:.2f}%", bestSimilarity * 100);
        }
        
        return bestMatch;
    }
    
    /**
     * Load image from file (utility method)
     */
    public BufferedImage loadImage(String imagePath) {
        try {
            String fullPath = getFullImagePath(imagePath);
            File imageFile = new File(fullPath);
            
            if (!imageFile.exists()) {
                logger.error("Image file not found: {}", fullPath);
                return null;
            }
            
            BufferedImage image = javax.imageio.ImageIO.read(imageFile);
            logger.debug("Loaded image: {} ({}x{})", fullPath, image.getWidth(), image.getHeight());
            return image;
            
        } catch (Exception e) {
            logger.error("Error loading image: {}", imagePath, e);
            return null;
        }
    }
}
