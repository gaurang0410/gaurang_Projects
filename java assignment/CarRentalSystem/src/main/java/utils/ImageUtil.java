package utils;

/**
 * Utility class for handling car image URLs
 * Ensures consistent image loading across the application
 */
public class ImageUtil {
    
    public static final String DEFAULT_CAR_IMAGE = "images/default-car.png";
    public static final String UPLOAD_DIR = "uploads";
    
    /**
     * Normalize and validate image URL
     * Ensures proper path formatting for both uploaded and external images
     */
    public static String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return DEFAULT_CAR_IMAGE;
        }
        
        String url = imageUrl.trim();
        
        // If it's an external URL (http/https), return as-is
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        
        // If it's already an uploaded file path, ensure it starts with /
        if (url.startsWith("uploads/") || url.startsWith(UPLOAD_DIR)) {
            return url.startsWith("/") ? url : "/" + url;
        }
        
        // If it's a relative path without uploads prefix, add it
        if (!url.startsWith("/") && !url.startsWith("uploads")) {
            return "/" + url;
        }
        
        return url;
    }
    
    /**
     * Get fallback image path for broken images
     */
    public static String getFallbackImage() {
        return DEFAULT_CAR_IMAGE;
    }
    
    /**
     * Check if image URL is external (http/https)
     */
    public static boolean isExternalUrl(String imageUrl) {
        return imageUrl != null && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"));
    }
    
    /**
     * Check if image URL is an uploaded file
     */
    public static boolean isUploadedFile(String imageUrl) {
        return imageUrl != null && (imageUrl.contains("uploads") || imageUrl.contains(UPLOAD_DIR));
    }
    
    /**
     * Get image path suitable for JSP src attribute
     */
    public static String getImagePath(String imageUrl) {
        String normalized = normalizeImageUrl(imageUrl);
        
        // If already absolute (starts with /), return as-is
        if (normalized.startsWith("/")) {
            return normalized;
        }
        
        // For relative paths, prepend nothing (JSP context handles it)
        return normalized;
    }
}
