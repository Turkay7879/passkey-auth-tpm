package com.ege.passkeytpm.api.model;

public class ChallengeResponseModel {
    private String challenge;
    private String keyAuth;

    public ChallengeResponseModel() {}

    public ChallengeResponseModel(String challenge, String keyAuth) {
        this.challenge = challenge;
        this.keyAuth = keyAuth;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getKeyAuth() {
        return keyAuth;
    }

    public void setKeyAuth(String keyAuth) {
        this.keyAuth = keyAuth;
    }
}
