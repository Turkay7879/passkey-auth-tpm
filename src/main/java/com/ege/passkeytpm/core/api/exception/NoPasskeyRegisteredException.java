package com.ege.passkeytpm.core.api.exception;

public class NoPasskeyRegisteredException extends Exception {
    public NoPasskeyRegisteredException() {
        super("User has no passkey registered to their account!");
    }
}
