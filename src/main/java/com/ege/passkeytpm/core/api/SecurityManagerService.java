package com.ege.passkeytpm.core.api;

public interface SecurityManagerService {
    String encryptPassword(char[] clearText) throws Exception;
    boolean verifyPassword(String hash, String salt, char[] clearText) throws Exception;
    String encrypt(String text) throws Exception;
    String decrypt(String text) throws Exception;
    String hash(String text) throws Exception;
}
