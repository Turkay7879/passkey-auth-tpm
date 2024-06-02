package com.ege.passkeytpm.core.api.exception;

public class SessionAlreadyExistsException extends Exception {
    public SessionAlreadyExistsException() {
        super("User already has an active session!");
    }
}
