package com.ege.passkeytpm.api.model;

import com.ege.passkeytpm.core.impl.pojo.UserPasskeyImpl;

import java.util.List;

public class UserModel {
    private String id;
    private String username;
    private String password;
    private List<UserPasskeyImpl> passkeyToAdd;

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<UserPasskeyImpl> getPasskeyToAdd() {
        return passkeyToAdd;
    }

    public void setPasskeyToAdd(List<UserPasskeyImpl> passkeyToAdd) {
        this.passkeyToAdd = passkeyToAdd;
    }
}
