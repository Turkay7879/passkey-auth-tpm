package com.ege.passkeytpm.core.api.exception;

public class MissingSessionException extends Exception {
    public MissingSessionException() {
        super("User has no active session!");
    }
}
