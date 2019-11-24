package com.example.msc.persistence;

import android.content.Context;
import androidx.room.Room;
import com.example.msc.persistence.database.MyDatabase;

public final class MyDatabaseAccessor {
    private static MyDatabase myDatabaseInstance;

    private MyDatabaseAccessor() {

    }

    public static final MyDatabase getInstance(final Context context) {
        if(myDatabaseInstance == null)
            myDatabaseInstance = Room.databaseBuilder(context, MyDatabase.class, "my_db").build();

        return myDatabaseInstance;
    }
}
