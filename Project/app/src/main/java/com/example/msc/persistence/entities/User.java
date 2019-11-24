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
    private String registered;

    private String email;

    @Ignore
    public User() {

    }

    public User(final String username, final String password, final String registered, final String email) {
        this.username = username;
        this.password = password;
        this.registered = registered;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(final String registered) {
        this.registered = registered;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}