package com.example.msc.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @NonNull
    @PrimaryKey
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String email;

    @NonNull
    private long registered;

    @Ignore
    public User() {

    }

    public User(@NonNull final String username, @NonNull final String password, @NonNull final String email, @NonNull final long registered) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.registered = registered;
    }

    @NonNull
    public String getUsername() {
        return username.trim();
    }

    public void setUsername(@NonNull final String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password.trim();
    }

    public void setPassword(@NonNull final String password) {
        this.password = password;
    }

    @NonNull
    public String getEmail() {
        return email.trim();
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public long getRegistered() {
        return registered;
    }

    public void setRegistered(@NonNull final long registered) {
        this.registered = registered;
    }
}