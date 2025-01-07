package com.sunshine;

import org.jboss.aerogear.security.otp.Totp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The {@code TwoFA} class extends the {@code Security} class to specifically handle two-factor authentication (2FA) features.
 * This class includes methods for generating Time-based One-Time Password (TOTP) URIs, verifying user inputted TOTP codes,
 * and generating QR codes that can be scanned for configuring TOTP in user applications.
 * <p>
 * A {@code TwoFA} object encapsulates all information required for 2FA including the issuer of the 2FA, a username which is a unique
 * identifier within the context of the 2FA issuer, and a secret key which is a unique base32-encoded string used in generating TOTPs.
 * <p>
 * This class also provides utility functions such as resizing images and converting them to ASCII representations,
 * which could be useful for various presentation or logging purposes.
 * <p>
 * Usage example:
 * <pre>
 * TwoFA twoFA = new TwoFA("Sunshine Bank", "user123", "base32secret");
 * String totpUri = twoFA.generateTOTPURI(twoFA);
 * boolean isCodeValid = twoFA.verifyCode("123456");
 * BufferedImage qrImage = twoFA.generateQRCodeImage(totpUri, 200, 200);
 * twoFA.enableTwoFactorForAccount();
 * </pre>
 * 
 * Note: This class assumes the presence of a {@code Totp}, {@code QRCodeWriter}, and {@code MatrixToImageWriter} class or library for handling TOTP verification
 * and QR code generation.
 * 
 * @author Mark and Dong Han
 * @version 1.0
 */
public class TwoFA extends Security{

    /**
     * Constructs a new TwoFA object with the specified issuer, username, and secret key.
     * <p>
     * This constructor initializes a new TwoFA object by calling the parent class constructor with the provided parameters.
     * The issuer is typically the name of the application or service implementing the two-factor authentication.
     * The username is a unique identifier for the user within the context of the issuer.
     * The secret key is a unique, base32-encoded string for the user, used in generating the TOTP (Time-based One-Time Password).
     * </p>
     * @param issuer the name of the application or service implementing the two-factor authentication
     * @param username a unique identifier for the user
     * @param secretKey a unique, base32-encoded string for the user
 */
    public TwoFA(String issuer, String username, String secretKey) {
        super(issuer, username, secretKey);
    }

    /**
     * Generates a Time-based One-Time Password (TOTP) URI for the given security object.
     * <p>
     * This method URL encodes the issuer, username, and secret key of the security object.
     * It then constructs a TOTP URI using the encoded values.
     * The TOTP URI is in the format "otpauth://totp/{issuer}:{username}?secret={secretKey}&issuer={issuer}".
     * </p>
     * @param security the Security object for which to generate the TOTP URI
     * @return the generated TOTP URI
     * @throws UnsupportedEncodingException if the named encoding is not supported
     */
    protected String generateTOTPURI(Security security) throws UnsupportedEncodingException {
        String encodedIssuer = this.issuer != null ? URLEncoder.encode(this.issuer, StandardCharsets.UTF_8.name()) : "";
        String encodedUserName = this.username != null ? URLEncoder.encode(this.username, StandardCharsets.UTF_8.name()) : "";
        String encodedSecretKey = this.secretKey != null ? URLEncoder.encode(this.secretKey, StandardCharsets.UTF_8.name()) : "";

        return "otpauth://totp/"
                + encodedIssuer + ":" + encodedUserName
                + "?secret=" + encodedSecretKey
                + "&issuer=" + encodedIssuer;
    }


    /**
     * Verifies a user-inputted TOTP (Time-based One-Time Password) code against the secret key.
     * <p>
     * This method creates a new Totp object using the secret key and attempts to verify the user-inputted code.
     * If the code is valid, it returns true. If the code is invalid or not a number, it returns false.
     * </p>
     * @param userInputCode the TOTP code inputted by the user
     * @return true if the code is valid, false otherwise
     */
    public boolean verifyCode(String userInputCode) {
        Totp totp = new Totp(this.secretKey);
        try {
            return totp.verify(userInputCode);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Generates a QR code image from a TOTP (Time-based One-Time Password) URI.
     * <p>
     * This method uses the ZXing library to generate a QR code. It creates a QRCodeWriter,
     * which is used to encode the TOTP URI into a BitMatrix. The BitMatrix is then converted
     * into a BufferedImage using the MatrixToImageWriter utility class.
     * </p>
     * @param totpUri the TOTP URI to encode into the QR code
     * @param width the width of the QR code image
     * @param height the height of the QR code image
     * @return a BufferedImage representing the QR code
     * @throws WriterException if an error occurs while encoding the TOTP URI
     */
    protected BufferedImage generateQRCodeImage(String totpUri, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(totpUri, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Enables two-factor authentication for the customer.
     * <p>
     * This method generates a secret key if one does not already exist for the account.
     * It then creates a new Security object with the issuer, username, and secret key, and adds it to the list of all Security objects.
     * A Time-based One-Time Password (TOTP) URI is generated using the account details and the secret key.
     * A QR code image is generated from the TOTP URI and saved under the resources/QRCodes directory.
     * The QR code image is then resized and converted to ASCII, which is printed to the console.
     * </p>
     * @throws IOException if an error occurs while writing the QR code image to a file.
     */
    public void enableTwoFactorForAccount() throws IOException {

        if (this.getSecretKey() == null) {
            this.generateSecretKey();   // Set the secret key on the security object
            
        }
        
        Security existingSecurity = Security.getSecurityByUsername(this.username);
        if (existingSecurity.getUsername() == null || existingSecurity.getUsername().equals("")) {
            existingSecurity = new Security(this.issuer, this.username, this.getSecretKey());
            Security.allSecurity.add(existingSecurity);
        } else {
            existingSecurity.setSecretKey(this.getSecretKey());
        }
        Customer.saveArrayListToCSV();


        // Generate the TOTP URI using the details of the user's account and the secret key
        String totpUri = generateTOTPURI(existingSecurity);

        // Generate the QR code image
        BufferedImage qrCodeImage = null;
        try {
            qrCodeImage = generateQRCodeImage(totpUri, 200, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Output the QR code image to a file, or display it on your UI
        File outputFile = new File("./resources/QRCodes/"+this.username+".png");
        try {
            ImageIO.write(qrCodeImage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display or provide the QR code to the user by your preferred method
        try {
            File imageFile = new File(outputFile.getPath());

            if (imageFile.exists()) {
                BufferedImage originalImage = ImageIO.read(imageFile);

                // Resize the image (adjust the width and height as needed)
                BufferedImage resizedImage = resizeImage(originalImage, originalImage.getWidth() / 4, originalImage.getHeight() / 4);
                BufferedImage newresizedImage = resizeImage(resizedImage, 100, 50);

                // Convert to ASCII
                String asciiArt = convertToAscii(newresizedImage);

                System.out.println(asciiArt);
            } else {
                System.out.println("Image file not found");
}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resizes an image to the specified width and height.
     * <p>
     * This method creates a new BufferedImage of the target size and type INT_RGB.
     * It then creates a Graphics2D object from the resized image and draws the original image onto it.
     * The original image is scaled to fit the target size.
     * The Graphics2D object is then disposed to free up system resources.
     * </p>
     * @param originalImage the original image to resize
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @return a new BufferedImage that is a resized version of the original image
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        
        // Draw the original image to the resized image
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        
        return resizedImage;
    }

    /**
     * Converts an image to ASCII art.
     * <p>
     * This method iterates over each pixel in the image. For each pixel, it calculates the brightness
     * by converting the color to grayscale. It then maps the brightness to an ASCII character
     * ('█' for dark pixels, ' ' for light pixels). The ASCII characters are appended to a StringBuilder
     * to create the ASCII art. Each row of pixels is followed by a newline character.
     * </p>
     * @param image the image to convert to ASCII art
     * @return a String representing the ASCII art
     */
    private static String convertToAscii(BufferedImage image) {
        StringBuilder asciiArtStr = new StringBuilder();
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // Get pixel color
                int color = image.getRGB(x, y);
                // Convert the color to grayscale (brightness)
                int brightness = (color >> 16 & 0xff) + (color >> 8 & 0xff) + (color & 0xff) / 3;
                // Map brightness to block character or space
                char asciiChar = brightness < 128 ? '█' : ' ';
                asciiArtStr.append(asciiChar);
            }
            asciiArtStr.append("\n");
        }
        
        return asciiArtStr.toString();
    }

    // get Security object by username
    public static Security getSecurityByUsername(String username){
        for (Security sec : allSecurity){
            if (sec.getUsername().equals(username)){
                return sec;
            }
        }
        return null;
    }

}