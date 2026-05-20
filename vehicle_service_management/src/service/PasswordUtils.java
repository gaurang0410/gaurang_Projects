package service;

import java.security.MessageDigest;

public class PasswordUtils {

    /**
     * Hashes the password using SHA-256.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Hashing error: " + e.getMessage());
            return password; // Fallback to plain text if hashing fails (emergency)
        }
    }

    /**
     * Verifies the input password against the stored password.
     * Supports both hashed and plain text comparisons.
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (storedPassword == null || inputPassword == null) return false;

        boolean isHashed = isLikelyHashed(storedPassword);
        System.out.println("Verifying password. Stored format: " + (isHashed ? "HASHED" : "PLAIN"));

        if (isHashed) {
            String hashedInput = hashPassword(inputPassword);
            boolean match = storedPassword.equals(hashedInput);
            System.out.println("Hash comparison: " + (match ? "MATCH" : "MISMATCH"));
            return match;
        } else {
            // Plain text comparison
            boolean match = storedPassword.equals(inputPassword);
            System.out.println("Plain text comparison: " + (match ? "MATCH" : "MISMATCH"));
            return match;
        }
    }

    /**
     * Checks if the password looks like a SHA-256 hash (64 hex characters).
     */
    public static boolean isLikelyHashed(String password) {
        return password != null && password.length() == 64 && password.matches("^[0-9a-fA-F]+$");
    }
}
