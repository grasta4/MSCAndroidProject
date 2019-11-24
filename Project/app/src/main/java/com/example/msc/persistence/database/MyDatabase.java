package com.example.msc.persistence.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.msc.persistence.dao.*;
import com.example.msc.persistence.entities.*;

@Database(entities = {Task.class, User.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TaskDao getTaskDao();
    public abstract UserDao getUserDao();
}
