package com.example.msc.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.msc.persistence.entities.User;

@Dao
public interface UserDao {
    @Insert
    public long AddUser(final User task);

    @Update
    public int UpdateUser(final User task);

    @Delete
    public int DeleteUser(final User task);

    @Query("select * from user where user.username ==:username")
    public User getUserByUsername(final String username);
}