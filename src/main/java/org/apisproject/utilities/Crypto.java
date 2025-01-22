package org.apisproject.utilities;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Crypto {

    private static final String ALGORITHM = "AES";


   /* public static String encrypt(String raw, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(raw.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }*/

    public static String decrypt(String encrypted, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes);
    }

    public static String getMd5Hash(String input)
    {
        try
        {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32)
            {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String generateSHA256(String value) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");


            byte[] encodedhash = digest.digest(value.getBytes());


            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) throws Exception {
        String raw = "Hello, World!";
        String key = "1234567890123456";

        String encryptedString = encrypt(raw, key);
        System.out.println("Encrypted String: " + encryptedString);

        String decryptedString = decrypt(encryptedString, key);
        System.out.println("Decrypted String: " + decryptedString);


        String valueName = "LewisNjeru";
        System.out.println("HashCode Generated for the string is: " + getMd5Hash(valueName));

        String value = "Hel";
        String hash = generateSHA256(value);

        System.out.println("Sh256 Value: " + value);
        System.out.println("SHA-256 Hash: " + hash);
    }
}