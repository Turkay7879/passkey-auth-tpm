package com.ege.passkeytpm.core.api;

public interface SecurityManagerService {
    String encryptPassword(char[] clearText) throws Exception;
    boolean verifyPassword(String hash, String salt, char[] clearText) throws Exception;
}
