package com.ege.passkeytpm.core.impl.pojo;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "t_user")
public class UserImpl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_id")
    private Long dbId;

    @Column(name = "id")
    private String id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "mail", nullable = false)
    private String mail;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "salt", nullable = false, length = 384)
    private String salt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<UserPasskeyImpl> passkeys;

    public UserImpl(Long dbId, String id, String userName, String mail, String password, String salt,
            LocalDateTime createdAt) {
        this.dbId = dbId;
        this.id = id;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.salt = salt;
        this.createdAt = createdAt;
    }

    public UserImpl() {
        this(null, null, null, null, null, null, null);
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<UserPasskeyImpl> getPasskeys() {
        return passkeys;
    }

    public void setPasskeys(Set<UserPasskeyImpl> passkeys) {
        this.passkeys = passkeys;
    }
}
