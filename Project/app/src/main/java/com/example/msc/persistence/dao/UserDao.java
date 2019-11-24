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
    public void AddUser(final User task);

    @Update
    public void UpdateUser(final User task);

    @Delete
    public void DeleteUser(final User task);

    @Query("select * from user where user.username ==:username")
    public User getUserByUsername(final String username);
}