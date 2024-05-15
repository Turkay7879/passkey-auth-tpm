package com.ege.passkeytpm.core.impl.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
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
    private byte[] SECRET_KEY               = new byte[0];

    @PostConstruct
    public void init() throws Exception {
        String secretKeyString = Optional
            .ofNullable(System.getenv("PASSKEY_SPRINGBOOT_SALT_SECRET"))
            .orElseThrow(() -> new Exception("Security manager service cannot be started without PASSKEY_SPRINGBOOT_SALT_SECRET environment variable!"));

        byte[] data = new byte[secretKeyString.length() / 2];
        for (int i = 0; i < secretKeyString.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(secretKeyString.charAt(i), 16) << 4) + Character.digit(secretKeyString.charAt(i+1), 16));
        }
        SECRET_KEY = new byte[data.length];
        System.arraycopy(data, 0, SECRET_KEY, 0, data.length);
        Arrays.fill(data, (byte) 0);
    }

    @PreDestroy
    public void destroy() {
        Arrays.fill(SECRET_KEY, (byte) 0);
    }

    @Override
    public String encryptPassword(char[] clearText) throws Exception {
        byte[] salt = generateIV();

        KeySpec spec = new PBEKeySpec(clearText, salt, 2048, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_INST);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        Arrays.fill(clearText, (char) 0);
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

        Arrays.fill(clearText, (char) 0);
        return Arrays.equals(hashBytes, hashToVerify);
    }

    @Override
    public String encrypt(String text) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(text.toCharArray()));
    }

    private byte[] encrypt(char[] text) throws Exception {
        byte[] data = charArr2ByteArr(text);
        byte[] iv = generateIV();

        SecretKey aes = new SecretKeySpec(SECRET_KEY, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, aes, ivParameterSpec);
        byte[] cipherData = cipher.doFinal(data);

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", cipherData);
        dataMap.put("iv", iv);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(dataMap);
        oos.flush();

        byte[] result = baos.toByteArray();

        oos.flush();
        baos.flush();
        oos.close();
        baos.close();
        Arrays.fill(data, (byte) 0);
        Arrays.fill(iv, (byte) 0);
        Arrays.fill(cipherData, (byte) 0);

        return result;
    }

    @Override
    public String decrypt(String text) throws Exception {
        byte[] data = Base64.getDecoder().decode(text);

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);

        HashMap<String, Object> dataMap = (HashMap<String, Object>) ois.readObject();
        Arrays.fill(data, (byte) 0);
        ois.close();
        bais.close();

        Object encData = dataMap.get("data");
        Object iv = dataMap.get("iv");
        if (encData == null || iv == null)
            throw new Exception("Invalid secret data! Cannot continue with decryption");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(encData);
        oos.flush();
        byte[] encDataBytes = baos.toByteArray();
        baos.flush();

        oos.writeObject(iv);
        oos.flush();
        byte[] ivBytes = baos.toByteArray();
        baos.flush();

        oos.close();
        baos.close();

        SecretKey aes = new SecretKeySpec(SECRET_KEY, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, aes, ivParameterSpec);
        byte[] clearData = cipher.doFinal(encDataBytes);

        Arrays.fill(encDataBytes, (byte) 0);
        Arrays.fill(ivBytes, (byte) 0);

        return new String(clearData);
    }

    private byte[] charArr2ByteArr(char[] charArr) {
        if (charArr == null || charArr.length == 0) {
            return null;
        }
        byte[] byteArr = new byte[charArr.length];
        for (int i = 0; i < charArr.length; i++) {
            byteArr[i] = (byte) charArr[i];
        }
        Arrays.fill(charArr, (char) 0);
        return byteArr;
    }
    
    private byte[] generateIV() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private byte[] processSalt(byte[] salt, boolean decrypt) throws Exception {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY, 0, SECRET_KEY.length, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(decrypt ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(salt);
    }

    @Override
    public String hash(String text) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public String generateNonce() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    public boolean verifySignature(PublicKey publicKey, byte[] data, byte[] digest) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(digest);
    }
}
