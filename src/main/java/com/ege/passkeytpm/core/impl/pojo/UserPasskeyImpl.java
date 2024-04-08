package com.ege.passkeytpm.core.impl.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_user_passkey")
public class UserPasskeyImpl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_id")
    private Long dbId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private UserImpl user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "key_hash", nullable = false)
    private String keyHash;

    public UserPasskeyImpl() {
    }

    public UserPasskeyImpl(Long dbId, UserImpl user, LocalDateTime createdAt, String publicKey, String keyHash) {
        this.dbId = dbId;
        this.user = user;
        this.createdAt = createdAt;
        this.publicKey = publicKey;
        this.keyHash = keyHash;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }
}
