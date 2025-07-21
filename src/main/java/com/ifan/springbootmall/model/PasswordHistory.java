package com.ifan.springbootmall.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_history")
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pwd_id")
    private Long pwdId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "pwd_hash")
    private String pwdHash;

    @Column(name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    public Long getPwdId() {
        return pwdId;
    }

    public void setPwdId(Long pwdId) {
        this.pwdId = pwdId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPwdHash() {
        return pwdHash;
    }

    public void setPwdHash(String pwdHash) {
        this.pwdHash = pwdHash;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
