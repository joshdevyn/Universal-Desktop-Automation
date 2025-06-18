package com.automation.core;

import com.automation.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenCapture provides screenshot and image manipulation capabilities
 */
public class ScreenCapture {
    private static final Logger logger = LoggerFactory.getLogger(ScreenCapture.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private Robot robot;
    private String screenshotDirectory;
    
    public ScreenCapture() {
        try {
            this.robot = new Robot();
            this.screenshotDirectory = "src/main/resources/images/screenshots";
            createDirectoryIfNotExists(screenshotDirectory);
        } catch (AWTException e) {
            logger.error("Failed to initialize Robot for screen capture", e);
            throw new RuntimeException("Failed to initialize Robot", e);
        }
    }
    
    public ScreenCapture(String screenshotDirectory) {
        this();
        this.screenshotDirectory = screenshotDirectory;
        createDirectoryIfNotExists(screenshotDirectory);
    }
    
    /**
     * Capture full screen
     */
    public BufferedImage captureFullScreen() {
        logger.debug("Capturing full screen");
        
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }
    
    /**
     * Capture specific screen region
     */
    public BufferedImage captureRegion(int x, int y, int width, int height) {
        logger.debug("Capturing region: ({}, {}) - {}x{}", x, y, width, height);
        
        Rectangle region = new Rectangle(x, y, width, height);
        return robot.createScreenCapture(region);
    }
    
    /**
     * Capture specific window
     */
    public BufferedImage captureWindow(Rectangle windowBounds) {
        logger.debug("Capturing window: {}", windowBounds);
        
        return robot.createScreenCapture(windowBounds);
    }
    
    /**
     * Save screenshot with timestamp
     */
    public String saveScreenshot(BufferedImage image) {
        return saveScreenshot(image, generateTimestampFilename());
    }
    
    /**
     * Save screenshot with custom filename
     */
    public String saveScreenshot(BufferedImage image, String filename) {
        try {
            if (!filename.toLowerCase().endsWith(".png")) {
                filename += ".png";
            }
            
            File file = new File(screenshotDirectory, filename);
            ImageIO.write(image, "PNG", file);
            
            String fullPath = file.getAbsolutePath();
            logger.info("Screenshot saved: {}", fullPath);
            return fullPath;
            
        } catch (IOException e) {
            logger.error("Failed to save screenshot: {}", filename, e);
            return null;
        }
    }
    
    /**
     * Save screenshot with timestamp
     */
    public String saveScreenshotWithTimestamp(BufferedImage image, String prefix) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s_%s.png", prefix, timestamp);
        return saveScreenshot(image, filename);
    }

    /**
     * Save screenshot to specific directory
     */
    public String saveScreenshotToDirectory(BufferedImage image, String filename, String directory) {
        try {
            File targetDir = new File(directory);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            
            if (!filename.toLowerCase().endsWith(".png")) {
                filename += ".png";
            }
            
            File file = new File(targetDir, filename);
            ImageIO.write(image, "PNG", file);
            
            String fullPath = file.getAbsolutePath();
            logger.info("Screenshot saved to directory: {}", fullPath);
            return fullPath;
            
        } catch (IOException e) {
            logger.error("Failed to save screenshot to directory: {}", directory, e);
            return null;
        }
    }
    
    /**
     * Capture and save with timestamp
     */
    public String captureAndSaveWithTimestamp(String prefix) {
        BufferedImage screenshot = captureFullScreen();
        return saveScreenshotWithTimestamp(screenshot, prefix);
    }

    /**
     * Capture region and save with timestamp
     */
    public String captureRegionAndSaveWithTimestamp(int x, int y, int width, int height, String prefix) {
        BufferedImage screenshot = captureRegion(x, y, width, height);
        return saveScreenshotWithTimestamp(screenshot, prefix);
    }

    /**
     * Capture and save to specific directory
     */
    public String captureAndSaveToDirectory(String filename, String directory) {
        BufferedImage screenshot = captureFullScreen();
        return saveScreenshotToDirectory(screenshot, filename, directory);
    }

    /**
     * Capture region and save to specific directory
     */
    public String captureRegionAndSaveToDirectory(int x, int y, int width, int height, String filename, String directory) {
        BufferedImage screenshot = captureRegion(x, y, width, height);
        return saveScreenshotToDirectory(screenshot, filename, directory);
    }

    /**
     * Capture evidence screenshot with description
     */
    public String captureEvidence(String description) {
        String sanitizedDescription = sanitizeFileName(description);
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("evidence_%s_%s.png", sanitizedDescription, timestamp);
        
        BufferedImage screenshot = captureFullScreen();
        String evidenceDir = screenshotDirectory + "/evidence";
        return saveScreenshotToDirectory(screenshot, filename, evidenceDir);
    }

    /**
     * Create baseline screenshot
     */
    public String createBaseline(String baselineName) {
        String sanitizedName = sanitizeFileName(baselineName);
        String filename = sanitizedName + ".png";
        
        BufferedImage screenshot = captureFullScreen();
        String baselineDir = screenshotDirectory + "/baselines";
        return saveScreenshotToDirectory(screenshot, filename, baselineDir);
    }

    /**
     * Capture full desktop (all monitors)
     */
    public BufferedImage captureFullDesktop() {
        logger.debug("Capturing full desktop (all monitors)");
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        
        if (screens.length == 1) {
            // Single monitor - use regular full screen capture
            return captureFullScreen();
        }
        
        // Multiple monitors - capture all
        Rectangle allScreensBounds = new Rectangle();
        for (GraphicsDevice screen : screens) {
            Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
            allScreensBounds = allScreensBounds.union(screenBounds);
        }
        
        return robot.createScreenCapture(allScreensBounds);
    }

    /**
     * Capture and save full desktop
     */
    public String captureAndSaveFullDesktop() {
        BufferedImage desktopScreenshot = captureFullDesktop();
        return saveScreenshotWithTimestamp(desktopScreenshot, "full_desktop");
    }

    /**
     * Load image from file
     */
    public BufferedImage loadImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                logger.error("Image file not found: {}", imagePath);
                return null;
            }

            BufferedImage image = ImageIO.read(imageFile);
            logger.debug("Loaded image: {} ({}x{})", imagePath, image.getWidth(), image.getHeight());
            return image;
            
        } catch (IOException e) {
            logger.error("Failed to load image: {}", imagePath, e);
            return null;
        }
    }
    
    /**
     * Compare two images for similarity
     */
    public double compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1 == null || img2 == null) {
            return 0.0;
        }
        
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            logger.warn("Images have different dimensions");
            return 0.0;
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
        logger.debug("Image similarity: {:.2f}%", similarity * 100);
        return similarity;
    }
    
    /**
     * Compare images with tolerance for slight differences
     */
    public double compareImagesWithTolerance(BufferedImage img1, BufferedImage img2, int tolerance) {
        if (img1 == null || img2 == null) {
            return 0.0;
        }
        
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return 0.0;
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
     * Crop image to specific region
     */
    public BufferedImage cropImage(BufferedImage image, int x, int y, int width, int height) {
        if (image == null) {
            return null;
        }
        
        // Ensure coordinates are within image bounds
        x = Math.max(0, Math.min(x, image.getWidth()));
        y = Math.max(0, Math.min(y, image.getHeight()));
        width = Math.min(width, image.getWidth() - x);
        height = Math.min(height, image.getHeight() - y);
        
        if (width <= 0 || height <= 0) {
            logger.warn("Invalid crop dimensions");
            return null;
        }
        
        return image.getSubimage(x, y, width, height);
    }
    
    /**
     * Resize image
     */
    public BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
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
     * Convert image to grayscale
     */
    public BufferedImage convertToGrayscale(BufferedImage originalImage) {
        if (originalImage == null) {
            return null;
        }
        
        BufferedImage grayImage = new BufferedImage(
            originalImage.getWidth(),
            originalImage.getHeight(),
            BufferedImage.TYPE_BYTE_GRAY
        );
        
        Graphics2D g2d = grayImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        
        return grayImage;
    }
    
    /**
     * Sanitize filename by removing invalid characters
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "unnamed";
        }
        
        // Remove or replace invalid file name characters
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_")
                      .replaceAll("_{2,}", "_")  // Replace multiple underscores with single
                      .replaceAll("^_+|_+$", "") // Remove leading/trailing underscores
                      .toLowerCase();
    }

    /**
     * Create directory if it doesn't exist
     */
    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Created directory: {}", directoryPath);
            } else {
                logger.warn("Failed to create directory: {}", directoryPath);
            }
        }
    }

    /**
     * Get screenshot directory
     */
    public String getScreenshotDirectory() {
        return screenshotDirectory;
    }

    /**
     * Set screenshot directory
     */
    public void setScreenshotDirectory(String screenshotDirectory) {
        this.screenshotDirectory = screenshotDirectory;
        createDirectoryIfNotExists(screenshotDirectory);
    }

    /**
     * Generate timestamp filename
     */
    private String generateTimestampFilename() {
        return "screenshot_" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".png";
    }
    
    /**
     * Wait for screen to stabilize (useful for animations)
     */
    public boolean waitForScreenStability(Rectangle region, int stabilityTimeMs, int timeoutMs) {
        logger.debug("Waiting for screen stability in region: {}", region);
        
        long startTime = System.currentTimeMillis();
        BufferedImage previousImage = null;
        long lastChangeTime = startTime;
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            BufferedImage currentImage = robot.createScreenCapture(region);
            
            if (previousImage != null) {
                double similarity = compareImages(previousImage, currentImage);
                if (similarity < 0.98) { // 98% similarity threshold
                    lastChangeTime = System.currentTimeMillis();
                }
            }
            
            if (System.currentTimeMillis() - lastChangeTime >= stabilityTimeMs) {
                logger.debug("Screen stabilized after {} ms", System.currentTimeMillis() - startTime);
                return true;
            }
            
            previousImage = currentImage;
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        logger.warn("Screen did not stabilize within timeout");
        return false;
    }

    /**
     * Capture full screen and save to temporary file
     * @return File object representing the captured screenshot
     */
    public File captureScreen() {
        try {
            BufferedImage screenshot = captureFullScreen();
            String tempFileName = "temp_screenshot_" + System.currentTimeMillis() + ".png";
            String tempPath = saveScreenshot(screenshot, tempFileName);
            return new File(tempPath);
        } catch (Exception e) {
            logger.error("Failed to capture screen to file", e);
            throw new RuntimeException("Failed to capture screen to file", e);
        }
    }
    
    /**
     * Capture specific region using Rectangle parameter
     * @param region Rectangle defining the region to capture
     * @return BufferedImage of the captured region
     */
    public BufferedImage captureRegion(Rectangle region) {
        if (region == null) {
            logger.warn("Region is null, capturing full screen instead");
            return captureFullScreen();
        }
        
        logger.debug("Capturing region: {}", region);
        return robot.createScreenCapture(region);
    }
    
    /**
     * Capture specific region and save to temporary file
     * @param region Rectangle defining the region to capture
     * @return File object representing the captured region screenshot
     */
    public File captureRegionToFile(Rectangle region) {
        try {
            BufferedImage regionScreenshot = captureRegion(region);
            String tempFileName = "temp_region_" + System.currentTimeMillis() + ".png";
            String tempPath = saveScreenshot(regionScreenshot, tempFileName);
            return new File(tempPath);
        } catch (Exception e) {
            logger.error("Failed to capture region to file: {}", region, e);
            throw new RuntimeException("Failed to capture region to file", e);
        }
    }

    /**
     * Save BufferedImage to file with automatic filename generation
     * 
     * @param image BufferedImage to save
     * @param prefix Filename prefix
     * @return File object of the saved image
     */
    public File saveBufferedImageToFile(BufferedImage image, String prefix) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = prefix + "_" + timestamp + ".png";
            String screenshotPath = ConfigManager.getProperty("screenshot.path.base", "target/screenshots");
            
            // Ensure screenshot directory exists
            Path screenshotDir = Paths.get(screenshotPath);
            Files.createDirectories(screenshotDir);
            
            File outputFile = new File(screenshotDir.toFile(), filename);
            ImageIO.write(image, "PNG", outputFile);
            
            logger.debug("Saved BufferedImage to file: {}", outputFile.getAbsolutePath());
            return outputFile;
            
        } catch (IOException e) {
            logger.error("Failed to save BufferedImage to file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }

    /**
     * Saves a BufferedImage to a temporary file.
     * The temporary file will be created in the system's default temporary-file directory.
     *
     * @param image The BufferedImage to save.
     * @param baseName A base name to be used in generating the temporary file's name.
     * @return A File object representing the saved temporary image, or null if saving failed.
     */
    public File saveBufferedImageToTempFile(BufferedImage image, String baseName) {
        if (image == null) {
            logger.warn("Cannot save null image to temporary file.");
            return null;
        }
        // Sanitize baseName for use in filename
        String sanitizedBaseName = sanitizeFileName(baseName);
        try {
            // Create a temporary file with a unique name
            // Suffix must be at least 3 chars long for createTempFile if not null.
            File tempFile = File.createTempFile(sanitizedBaseName + "_", ".png");
            ImageIO.write(image, "PNG", tempFile);
            logger.info("Saved BufferedImage to temporary file: {}", tempFile.getAbsolutePath());
            return tempFile;
        } catch (IOException e) {
            logger.error("Failed to save BufferedImage to temporary file (baseName: {}): {}", sanitizedBaseName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Captures the full screen and saves it as an error screenshot.
     * The screenshot is saved in a specified subdirectory of the main screenshot directory.
     *
     * @param description A description of the error, used in the filename.
     * @param errorScreenshotsSubDir The subdirectory name (e.g., "errors") for storing error screenshots.
     * @return The absolute path to the saved screenshot file, or null if failed.
     */
    public String captureAndSaveErrorScreenshot(String description, String errorScreenshotsSubDir) {
        try {
            BufferedImage screenshot = captureFullScreen();
            String sanitizedDescription = sanitizeFileName(description);
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String filename = String.format("ERROR_%s_%s.png", sanitizedDescription, timestamp);

            String errorDir = Paths.get(getScreenshotDirectory(), errorScreenshotsSubDir).toString();
            createDirectoryIfNotExists(errorDir); // Ensure the error subdirectory exists

            return saveScreenshotToDirectory(screenshot, filename, errorDir);
        } catch (Exception e) {
            logger.error("Failed to capture and save error screenshot for description '{}': {}", description, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Calculate similarity between two BufferedImages
     * @param image1 First image
     * @param image2 Second image
     * @return Similarity score between 0.0 and 1.0
     */
    public double calculateSimilarity(BufferedImage image1, BufferedImage image2) {
        return compareImages(image1, image2);
    }
}
