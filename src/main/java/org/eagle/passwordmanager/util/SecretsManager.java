package org.eagle.passwordmanager.util;

import org.eagle.passwordmanager.model.Login;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * SecretsManager class is used to encrypt and decrypt items
 */
public final class SecretsManager {
    private static final String keyStr = "8e8df8cfcf65592745976d6629b469b5b4a3441ad3d96371f14ede7c12261101caa3251ed5eafe6afda56e684daa4090";
    private static SecretKeySpec secretKey;

    private SecretsManager() {
    }

    private static void setKey(String myKey) {
        MessageDigest sha;
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String password) {
        if (strToEncrypt.equals("")) {
            return "";
        }
        try {
            return Base64.getEncoder().encodeToString(encryptBytes(strToEncrypt, password));
        } catch (Exception ignored) {

        }
        return null;
    }

    public static byte[] encryptBytes(String strToEncrypt, String password) {
        if (strToEncrypt.equals("")) {
            return null;
        }
        try {
            setKey(keyStr + password);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {

        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String password) {
        if (strToDecrypt.equals("")) {
            return "";
        }
        try {
            return decryptBytes(Base64.getDecoder().decode(strToDecrypt), password);
        } catch (Exception ignored) {

        }
        return null;
    }

    public static String decryptBytes(byte[] bytesToDecrypt, String password) {
        if (bytesToDecrypt == null) {
            return null;
        }
        try {
            setKey(keyStr + password);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(bytesToDecrypt));
        } catch (Exception ignored) {

        }
        return null;
    }

    /**
     * Creates an encrypted Login
     * @param login
     * @return
     */
    public static Login encryptLogin(Login login) {
        String passwordHash = UsersManager.getInstance().getUserPasswordHash();
        int id = login.getLoginId();
        String name = encrypt(login.getName(), passwordHash);
        String userName = encrypt(login.getUserName(), passwordHash);
        String password = encrypt(login.getPassword(), passwordHash);
        String url = encrypt(login.getURL(), passwordHash);
        String notes = encrypt(login.getNotes(), passwordHash);

        return new Login(id, name, userName, password, url, notes);
    }

    /**
     * Decrypts a Login
     * @param login
     */
    public static void decryptLogin(Login login) {
        String passwordHash = UsersManager.getInstance().getUserPasswordHash();
        String name = decrypt(login.getName(), passwordHash);
        String userName = decrypt(login.getUserName(), passwordHash);
        String password = decrypt(login.getPassword(), passwordHash);
        String url = decrypt(login.getURL(), passwordHash);
        String notes = decrypt(login.getNotes(), passwordHash);
        login.setName(name);
        login.setUserName(userName);
        login.setPassword(password);
        login.setURL(url);
        login.setNotes(notes);
    }

}
