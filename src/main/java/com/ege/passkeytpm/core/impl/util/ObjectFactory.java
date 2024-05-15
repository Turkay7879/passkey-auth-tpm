package com.ege.passkeytpm.core.impl.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class ObjectFactory {
    private static ObjectFactory instance;

    public static ObjectFactory getInstance() {
        if (instance == null) {
            instance = new ObjectFactory();
        }
        return instance;
    }

    public PublicKey model2Bean(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = new BigInteger(publicKeyStr, 16).toByteArray();
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }
}
