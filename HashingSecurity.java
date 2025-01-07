package com.sunshine;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The {@code HashingSecurity} class provides cryptographic hashing utilities
 * for enhancing data security, particularly for sensitive information like passwords.
 * It offers functionality to generate cryptographic salts, hash strings with these salts,
 * and convert between hexadecimal strings and byte arrays.
 * <p>
 * The class uses SHA-256 for hashing, a widely recognized secure algorithm. Salts are
 * generated using {@link SecureRandom} to ensure unpredictability in the resulting hash,
 * making it more resistant to dictionary and brute-force attacks. The class also provides
 * utility methods for converting the resultant hash to a hexadecimal String, and vice versa,
 * for ease of storage and comparison.
 * <p>
 * This class is designed to be used in any system that requires strong cryptographic security
 * measures for password storage and verification, to ensure that even if password data is
 * compromised, the actual passwords cannot be easily retrieved.
 * <p>
 * Example usage:
 * <pre>
 * byte[] salt = HashingSecurity.generateSalt();
 * String hashedPassword = HashingSecurity.hashString("myPassword123", salt);
 * </pre>
 * 
 * @author Mark and Dong Han
 * @version 1.0
 */

// HashingSecurity class
public class HashingSecurity {

    /**
     * Generates a secure random salt.
     * @return A byte array containing the salt.
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a string using SHA-256 with a salt.
     * @param password The string to hash.
     * @param salt The salt to use in the hashing process.
     * @return A hexadecimal string of the hashed value.
     * @throws NoSuchAlgorithmException If SHA-256 is not available.
     * @throws UnsupportedEncodingException If UTF-8 encoding is not supported.
     */
    public static String hashString(String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        return bytesToHex(hash);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     * @param bytes The byte array to convert.
     * @return A hexadecimal string representation of the byte array.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Converts a hexadecimal string to a byte array.
     * @param s The hexadecimal string to convert.
     * @return A byte array representing the hexadecimal string.
     */
    public static byte[] hexStringToByte(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
