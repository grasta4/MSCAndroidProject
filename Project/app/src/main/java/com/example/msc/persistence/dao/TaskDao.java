package com.example.msc.persistence.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.msc.persistence.entities.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    public long AddTask(final Task task);

    @Update
    public int UpdateTask(final Task task);

    @Delete
    public int DeleteTask(final Task task);

    @Query("select * from task where task.user ==:user")
    public List <Task> getTasksByUser(final String user);
}
