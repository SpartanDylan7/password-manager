package org.eagle.passwordmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public final class User {
    private int userId;
    private final String userName;
    private String email;
    private char[] password;
    private String authToken;
    private String passwordHash;
    private final ObservableList<Login> userItems = FXCollections.observableArrayList();

    public User(String userName) {
        this(userName, "");
    }

    public User(String userName, String password) {
        if (userName.isEmpty() || userName.isBlank()) {
            throw new IllegalArgumentException("user name is required");
        }
        this.userName = userName.trim();
        setPassword(password);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.toCharArray();
        if (!(password.isEmpty() || password.isBlank())) {
            setPasswordHash();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ObservableList<Login> getUserItems() {
        return userItems;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setPasswordHash() {
        String sha1 = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(charsToBytes(password));
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        passwordHash = sha1;
    }

    /**
     * This method will be used by @SecretsManager.class to encrypt and decrypt login item info such as userName, password and notes
     *
     * @return
     */
    public String getPasswordHash() {
        return passwordHash;
    }


    private byte[] charsToBytes(char[] chars) {
        final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
    }


}