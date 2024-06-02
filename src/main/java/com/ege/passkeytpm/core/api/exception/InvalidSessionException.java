package com.ege.passkeytpm.core.api.exception;

public class InvalidSessionException extends Exception {
    public InvalidSessionException() {
        super("Session or session id is invalid!");
    }
}
