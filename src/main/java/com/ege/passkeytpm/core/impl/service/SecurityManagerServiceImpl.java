package com.ege.passkeytpm.core.impl.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.ege.passkeytpm.core.api.SecurityManagerService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class SecurityManagerServiceImpl implements SecurityManagerService {
    private final SecureRandom secureRandom = new SecureRandom();
    private final String SECRET_KEY_INST    = "PBKDF2WithHmacSHA512";
    private String SECRET_KEY               = null;

    @PostConstruct
    public void init() throws Exception {
        SECRET_KEY = Optional
            .ofNullable(System.getenv("PASSKEY_SPRINGBOOT_SALT_SECRET"))
            .orElseThrow(() -> new Exception("Security manager service cannot be started without PASSKEY_SPRINGBOOT_SALT_SECRET environment variable!"));
    }

    @PreDestroy
    public void destroy() {
        SECRET_KEY = null;
    }

    @Override
    public String encryptPassword(char[] clearText) throws Exception {
        byte[] salt = generateSalt();

        KeySpec spec = new PBEKeySpec(clearText, salt, 2048, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_INST);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        for (int i = 0; i < clearText.length; i++) {
            clearText[i] = 0;
        }

        byte[] saltEncrypted = processSalt(salt, false);
        return Base64.getEncoder().encodeToString(hash) + "," + Base64.getEncoder().encodeToString(saltEncrypted);
    }

    @Override
    public boolean verifyPassword(String hash, String salt, char[] clearText) throws Exception {
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        byte[] hashBytes = Base64.getDecoder().decode(hash);

        byte[] clearSalt = processSalt(saltBytes, true);

        KeySpec spec = new PBEKeySpec(clearText, clearSalt, 2048, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_INST);
        byte[] hashToVerify = factory.generateSecret(spec).getEncoded();

        for (int i = 0; i < clearText.length; i++) {
            clearText[i] = 0;
        }

        return Arrays.equals(hashBytes, hashToVerify);
    }
    
    private byte[] generateSalt() {
        byte[] salt = new byte[256];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private byte[] processSalt(byte[] salt, boolean decrypt) throws Exception {
        byte[] secretBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(secretBytes, 0, secretBytes.length, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(decrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, secretKey);

        byte[] result = cipher.doFinal(salt);
        return result;
    }
}
