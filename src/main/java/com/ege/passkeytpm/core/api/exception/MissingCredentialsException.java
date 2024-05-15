package com.ege.passkeytpm.core.api.exception;

public class MissingCredentialsException extends Exception {
    public MissingCredentialsException() {
        super("Object for authentication is null or has missing credentials!");
    }
}
