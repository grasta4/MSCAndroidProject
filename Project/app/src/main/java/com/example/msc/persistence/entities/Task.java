package com.example.msc.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task", primaryKeys = {"name", "user"})
public class Task {

    @NonNull
    private String name;

    @NonNull
    private double latitude;

    @NonNull
    private double longitude;

    @NonNull
    private String user;

    @Ignore
    public Task() {

    }

    public Task(final String name, final double latitude, final double longitude, final String user) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getLatitude() {return latitude;}

    public void setLatitude(final double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}

    public void setLongitude(final double longitude) {this.longitude = longitude;}

    public String getUser() {
        return user.trim();
    }

    public void setUser(final String user) {
        this.user = user;
    }
}