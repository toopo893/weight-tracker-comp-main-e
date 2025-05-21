package com.weighttracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
     @Column(nullable = true) 
     private String email;
    
    private Double targetWeight;
    
    private boolean enabled = true;
    
    // コンストラクタ
    public User() {
    }

    // emailなし登録用の簡易コンストラクタ（ユーザー名とパスワードのみ使用）
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
    }

    // ゲッターとセッター
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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

    public String getEmail() {
         return email;
     }
    
     public void setEmail(String email) {
         this.email = email;
     }

    public Double getTargetWeight() {
        return targetWeight;
    }
    
    public void setTargetWeight(Double targetWeight) {
        this.targetWeight = targetWeight;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
