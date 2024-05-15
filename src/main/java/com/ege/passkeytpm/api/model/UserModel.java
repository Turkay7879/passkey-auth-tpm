package com.ege.passkeytpm.api.model;

import com.ege.passkeytpm.core.impl.pojo.UserPasskeyImpl;

import java.util.List;

public class UserModel {
    private String id;
    private String email;
    private String username;
    private String password;
    private String challenge;
    private String digest;
    private List<UserPasskeyImpl> userPasskeys;

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserPasskeyImpl> getUserPasskeys() {
        return userPasskeys;
    }

    public void setUserPasskeys(List<UserPasskeyImpl> userPasskeys) {
        this.userPasskeys = userPasskeys;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
