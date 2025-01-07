package com.sunshine;

import org.jboss.aerogear.security.otp.api.Base32;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;


/**
 * The {@code Security} class represents a security entity for managing two-factor authentication (2FA). It encapsulates details such as the issuer of the 2FA, the user's name, and the
 * secret key used for generating and verifying 2FA codes. This class provides methods for initializing security details from a CSV file,
 * generating new secret keys for 2FA, and verifying if a secret key is unique among all users.
 * <p>
 * The class maintains a static list of all {@code Security} objects created, allowing for easy management and retrieval of user
 * security settings. Methods are provided for checking
 * the existence of a secret key, generating new ones, and retrieving {@code Security} instances based on usernames.
 * <p>
 * Usage examples include initializing the security settings from a CSV file upon application startup, enabling 2FA for users by
 * generating a new secret key.
 * <p>
 * Note: This class works with TwoFA.java to enable 2FA for users and verify the TOTP code entered by the user.
 *
 * @author Mark and Dong Han
 * @version 1.1
 */

public class Security {

    /**
     * Store all security objects in a list
     */
    public static ArrayList<Security> allSecurity;
    
    /**
     * The issuer of the two-factor authentication (2FA).
     */
    protected String issuer;

    /**
     * The user name or identifier for whom the 2FA is set up. This should uniquely identify the user within the context 
     * of the application or service.
     */
    protected String username;

    /**
     * The secret key used for generating and verifying the TOTP (Time-based One-Time Password) codes.
     */
    protected String secretKey;

    /**
     * Constructs a new Security instance with the specified issuer, username, and secret key.
     * <p>
     * This constructor initializes a new Security object by setting the issuer, username, and secret key fields.
     * The issuer is typically the name of the application or service implementing the two-factor authentication.
     * The username is a unique identifier for the user within the context of the issuer.
     * The secret key is a unique string for the user, used in generating and verifying TOTP (Time-based One-Time Password) codes.
     * </p>
     * @param issuer the name of the application or service implementing the two-factor authentication
     * @param username a unique identifier for the user
     * @param secretKey a unique string for the user, used for TOTP code generation and verification
     */
    public Security(String issuer, String username, String secretKey) {
        this.issuer = issuer; // sunshine bank
        this.username = username; 
        this.secretKey = secretKey; // Stored in customer csv
    }

    /**
     * Initializes the Security instances from a CSV file.
     * <p>
     * This method reads a CSV file line by line. Each line represents a user's security data.
     * It splits each line by comma to extract the user's attributes, specifically the username and secret key.
     * It then creates a new Security instance with these attributes and a fixed issuer ("Sunshine Bank").
     * The created Security instance is added to the allSecurity list.
     * </p>
     * @param path the path to the CSV file
     * @throws IOException if an I/O error occurs when reading the file
     * @throws ParseException if an error occurs when parsing the CSV data
     */
    public static void initSecurityCSV(String path) throws IOException, ParseException {
        allSecurity = new ArrayList<Security>();
    //Customer.setPath(path);
    // Read the file
    BufferedReader br = new BufferedReader(new FileReader(path));
    // Read the header and ignore
    String line = br.readLine(); 
    // Read the file line by line
    while ((line = br.readLine()) != null) {
        // Split the line by comma
        String[] attributes = line.split(",");

        // Create a new customer based on the data
        String username = attributes[1];
        //String saltedValue = attributes[3];
        String secretKey = attributes[5];
        String issuer = "Sunshine Bank";
        Security security = new Security(issuer, username, secretKey);
        //security.setSaltedValue(saltedValue);
        allSecurity.add(security);
    }
    // Close the file
    br.close();
    }

    /**
     * Gets the issuer name.
     * @return The issuer name.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the issuer name.
     * @param issuer The new issuer name.
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Gets the user name.
     * @return The user name.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user name.
     * @param username The new user name.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the secret key for two-factor authentication (2FA).
     * <p>
     * This method returns the secret key if it exists and is not empty or "null". 
     * It checks if the customer has 2FA enabled by verifying that the secret key is not null, empty, blank, or the string "null".
     * If any of these conditions are met, the method returns null, indicating that 2FA is not enabled for the customer.
     * </p>
     * @return the secret key for 2FA if it exists and is valid, null otherwise
     */
    public String getSecretKey() {
        if (this.secretKey == null || this.secretKey.isEmpty() || this.secretKey.isBlank() || this.secretKey.equals("null")) {
            return null;
        }
        return this.secretKey;
    }

    /**
     * Sets the secret key.
     * @param secretKey The new secret key.
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Enables two-factor authentication (2FA) for an existing account by generating a unique secret key.
     * <p>
     * This method generates a secure, random Base32 string to be used as the secret key for 2FA.
     * It ensures the uniqueness of the secret key by checking if the generated key is already in use.
     * If the key is in use, it generates a new one. This process repeats until a unique key is found.
     * The unique key is then set as the secret key for the account.
     * </p>
     */
    public void generateSecretKey() {
        String secretKey;
        do {
            secretKey = Base32.random(); // Generates a secure, random Base32 string
        } while (isSecretKeyUsed(secretKey));
        this.setSecretKey(secretKey);
    }

    /**
     * Checks if a given secret key is already used by another customer.
     * <p>
     * This method iterates over all the Security instances in the allSecurity list.
     * For each instance, it compares the given secret key with the instance's secret key.
     * If a match is found, it returns true, indicating that the secret key is already in use.
     * If no match is found after checking all instances, it returns false, indicating that the secret key is unique.
     * </p>
     * @param secretKey the secret key to check for uniqueness
     * @return true if the secret key is already in use, false otherwise
     */
    private boolean isSecretKeyUsed(String secretKey) {
        for (Security sec : allSecurity) {
            if (secretKey.equals(sec.getSecretKey())) {
                return true; // Secret key already exists
            }
        }
    return false; // Secret key is unique
    }

    /**
     * Retrieves a Security object based on the username.
     * <p>
     * This method iterates over all the Security instances in the allSecurity list.
     * For each instance, it compares the given username with the instance's username, ignoring case differences.
     * If a match is found, it returns the matching Security instance.
     * If no match is found after checking all instances, it returns a new Security instance with empty fields.
     * </p>
     * @param username the username to search for
     * @return the matching Security instance if found, a new Security instance with empty fields otherwise
     */
    public static Security getSecurityByUsername(String username) {
        for (Security security : allSecurity) {
            if (security.getUsername().equalsIgnoreCase(username)) {
                return security;
            }
        }
        return new Security("","",""); // Return null if no matching customer is found
    }
}