package com.ege.passkeytpm.core.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ObjectFactory {
    private static ObjectFactory instance;
    private final Logger logger = LoggerFactory.getLogger(ObjectFactory.class);
    private final int RSA_PUBLIC_EXPONENT = 65537;

    public static ObjectFactory getInstance() {
        if (instance == null) {
            instance = new ObjectFactory();
        }
        return instance;
    }

    public PublicKey model2Bean(String publicKeyStr) {
        try {
            byte[] pubKeyBytes = hexString2ByteArray(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            BigInteger modulus = new BigInteger(1, pubKeyBytes);
            BigInteger exponent = BigInteger.valueOf(RSA_PUBLIC_EXPONENT);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);

            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            logger.error("Error while converting hex string to public key", e);
        }

        return null;
    }

    public byte[] hexString2ByteArray(String hexStr) {
        int len = hexStr != null ? hexStr.trim().length() : 0;
        if (len > 0) {
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) +
                                       Character.digit(hexStr.charAt(i+1), 16));
            }
            return data;
        }

        return new byte[0];
    }
}
