package com.example.msc.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.time.Instant;

@Entity(tableName = "task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String location;

    @NonNull
    private Instant timestamp;

    @NonNull
    private String user;

    @Ignore
    public Task() {

    }

    public Task(final int id, final String name, final String location, final Instant timestamp, final String user) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.timestamp = timestamp;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }
}