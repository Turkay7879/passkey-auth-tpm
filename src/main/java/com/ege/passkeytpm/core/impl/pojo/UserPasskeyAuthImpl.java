package com.ege.passkeytpm.core.impl.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_user_passkey_auth_session")
public class UserPasskeyAuthImpl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_id")
    private Long dbId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private UserImpl user;

    @Column(name = "nonce", nullable = false)
    private String challenge;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_valid", nullable = false)
    private boolean isValid;

    public UserPasskeyAuthImpl() {}

    public UserPasskeyAuthImpl(UserImpl user, String challenge) {
        LocalDateTime now = LocalDateTime.now();
        this.user = user;
        this.challenge = challenge;
        this.createdAt = now;
        this.expiresAt = now.plusSeconds(30);
        this.isValid = true;
    }

    public UserPasskeyAuthImpl(Long dbId, UserImpl user, String challenge, LocalDateTime createdAt, LocalDateTime expiresAt, boolean isValid) {
        this.dbId = dbId;
        this.user = user;
        this.challenge = challenge;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isValid = isValid;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public UserImpl getUser() {
        return user;
    }

    public void setUser(UserImpl user) {
        this.user = user;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean valid) {
        isValid = valid;
    }
}
