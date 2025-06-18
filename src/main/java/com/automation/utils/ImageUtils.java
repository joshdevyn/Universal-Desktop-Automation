package com.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

/**
 * ImageUtils provides image processing and manipulation utilities
 */
public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    
    /**
     * Load image from file path
     */
    public static BufferedImage loadImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                logger.error("Image file not found: {}", imagePath);
                return null;
            }
            
            BufferedImage image = ImageIO.read(imageFile);
            logger.debug("Image loaded successfully: {}", imagePath);
            return image;
            
        } catch (IOException e) {
            logger.error("Failed to load image: {}", imagePath, e);
            return null;
        }
    }
    
    /**
     * Save image to file
     */
    public static boolean saveImage(BufferedImage image, String outputPath) {
        if (image == null) {
            logger.error("Cannot save null image");
            return false;
        }
        
        try {
            File outputFile = new File(outputPath);
            
            // Create parent directories if they don't exist
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Determine format from file extension
            String format = getImageFormat(outputPath);
            ImageIO.write(image, format, outputFile);
            
            logger.debug("Image saved successfully: {}", outputPath);
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to save image: {}", outputPath, e);
            return false;
        }
    }
    
    /**
     * Get image format from file extension
     */
    private static String getImageFormat(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "JPEG";
            case "png":
                return "PNG";
            case "bmp":
                return "BMP";
            case "gif":
                return "GIF";
            default:
                return "PNG"; // Default to PNG
        }
    }
    
    /**
     * Convert image to grayscale
     */
    public static BufferedImage convertToGrayscale(BufferedImage originalImage) {
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
     * Resize image to specified dimensions
     */
    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
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
     * Scale image by factor
     */
    public static BufferedImage scaleImage(BufferedImage originalImage, double scaleFactor) {
        if (originalImage == null) {
            return null;
        }
        
        int newWidth = (int) (originalImage.getWidth() * scaleFactor);
        int newHeight = (int) (originalImage.getHeight() * scaleFactor);
        
        return resizeImage(originalImage, newWidth, newHeight);
    }
    
    /**
     * Crop image to specified rectangle
     */
    public static BufferedImage cropImage(BufferedImage image, Rectangle cropArea) {
        if (image == null || cropArea == null) {
            return null;
        }
        
        // Ensure crop area is within image bounds
        int x = Math.max(0, Math.min(cropArea.x, image.getWidth()));
        int y = Math.max(0, Math.min(cropArea.y, image.getHeight()));
        int width = Math.min(cropArea.width, image.getWidth() - x);
        int height = Math.min(cropArea.height, image.getHeight() - y);
        
        if (width <= 0 || height <= 0) {
            logger.warn("Invalid crop area: {}", cropArea);
            return null;
        }
        
        return image.getSubimage(x, y, width, height);
    }
    
    /**
     * Crop image to specified coordinates
     */
    public static BufferedImage cropImage(BufferedImage image, int x, int y, int width, int height) {
        return cropImage(image, new Rectangle(x, y, width, height));
    }
    
    /**
     * Enhance image contrast
     */
    public static BufferedImage enhanceContrast(BufferedImage originalImage, float contrast) {
        if (originalImage == null) {
            return null;
        }
        
        BufferedImage enhancedImage = new BufferedImage(
            originalImage.getWidth(),
            originalImage.getHeight(),
            originalImage.getType()
        );
        
        Graphics2D g2d = enhancedImage.createGraphics();
        
        // Apply contrast enhancement
        java.awt.image.RescaleOp rescaleOp = new java.awt.image.RescaleOp(contrast, 128 * (1 - contrast), null);
        g2d.drawImage(originalImage, rescaleOp, 0, 0);
        g2d.dispose();
        
        return enhancedImage;
    }
    
    /**
     * Sharpen image using convolution
     */
    public static BufferedImage sharpenImage(BufferedImage originalImage) {
        if (originalImage == null) {
            return null;
        }
        
        float[] sharpenMatrix = {
            0.0f, -1.0f, 0.0f,
            -1.0f, 5.0f, -1.0f,
            0.0f, -1.0f, 0.0f
        };
        
        Kernel sharpenKernel = new Kernel(3, 3, sharpenMatrix);
        ConvolveOp sharpenOp = new ConvolveOp(sharpenKernel, ConvolveOp.EDGE_NO_OP, null);
        
        return sharpenOp.filter(originalImage, null);
    }
    
    /**
     * Blur image using convolution
     */
    public static BufferedImage blurImage(BufferedImage originalImage) {
        if (originalImage == null) {
            return null;
        }
        
        float blurValue = 1.0f / 9.0f;
        float[] blurMatrix = {
            blurValue, blurValue, blurValue,
            blurValue, blurValue, blurValue,
            blurValue, blurValue, blurValue
        };
        
        Kernel blurKernel = new Kernel(3, 3, blurMatrix);
        ConvolveOp blurOp = new ConvolveOp(blurKernel, ConvolveOp.EDGE_NO_OP, null);
        
        return blurOp.filter(originalImage, null);
    }
    
    /**
     * Apply threshold to create binary image
     */
    public static BufferedImage applyThreshold(BufferedImage originalImage, int threshold) {
        if (originalImage == null) {
            return null;
        }
        
        BufferedImage binaryImage = new BufferedImage(
            originalImage.getWidth(),
            originalImage.getHeight(),
            BufferedImage.TYPE_BYTE_BINARY
        );
        
        Graphics2D g2d = binaryImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        
        // Apply threshold
        for (int x = 0; x < binaryImage.getWidth(); x++) {
            for (int y = 0; y < binaryImage.getHeight(); y++) {
                Color color = new Color(originalImage.getRGB(x, y));
                int gray = (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                
                if (gray < threshold) {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        
        return binaryImage;
    }
    
    /**
     * Preprocess image for better OCR accuracy
     */
    public static BufferedImage preprocessForOCR(BufferedImage originalImage) {
        if (originalImage == null) {
            return null;
        }
        
        // Convert to grayscale
        BufferedImage processedImage = convertToGrayscale(originalImage);
        
        // Scale up for better recognition
        processedImage = scaleImage(processedImage, 2.0);
        
        // Enhance contrast
        processedImage = enhanceContrast(processedImage, 1.5f);
        
        // Sharpen
        processedImage = sharpenImage(processedImage);
        
        return processedImage;
    }
    
    /**
     * Compare two images and return similarity percentage
     */
    public static double compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1 == null || img2 == null) {
            return 0.0;
        }
        
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            // Resize images to same size for comparison
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
        
        return (double) matchingPixels / totalPixels;
    }
    
    /**
     * Compare images with tolerance for slight differences
     */
    public static double compareImagesWithTolerance(BufferedImage img1, BufferedImage img2, int tolerance) {
        if (img1 == null || img2 == null) {
            return 0.0;
        }
        
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            // Resize images to same size for comparison
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
        
        return (double) similarPixels / totalPixels;
    }
    
    /**
     * Check if two colors are similar within tolerance
     */
    private static boolean isColorSimilar(Color c1, Color c2, int tolerance) {
        return Math.abs(c1.getRed() - c2.getRed()) <= tolerance &&
               Math.abs(c1.getGreen() - c2.getGreen()) <= tolerance &&
               Math.abs(c1.getBlue() - c2.getBlue()) <= tolerance;
    }
    
    /**
     * Create a thumbnail of the image
     */
    public static BufferedImage createThumbnail(BufferedImage originalImage, int maxWidth, int maxHeight) {
        if (originalImage == null) {
            return null;
        }
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Calculate scaling factor to maintain aspect ratio
        double scaleX = (double) maxWidth / originalWidth;
        double scaleY = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);
        
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);
        
        return resizeImage(originalImage, newWidth, newHeight);
    }
    
    /**
     * Get image dimensions as string
     */
    public static String getImageDimensions(BufferedImage image) {
        if (image == null) {
            return "null";
        }
        return image.getWidth() + "x" + image.getHeight();
    }
    
    /**
     * Check if image is null or empty
     */
    public static boolean isImageValid(BufferedImage image) {
        return image != null && image.getWidth() > 0 && image.getHeight() > 0;
    }
}
