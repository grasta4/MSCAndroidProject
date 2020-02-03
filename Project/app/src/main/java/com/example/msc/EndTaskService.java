package com.example.msc;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.dao.TaskDao;
import com.example.msc.util.BackgroundTask;

import java.util.concurrent.ExecutionException;

public class EndTaskService extends IntentService {

    public EndTaskService() {
        super("EndTaskService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String removeItem = intent.getStringExtra("toDelete");
        TaskLocations.taskLocations.remove(removeItem);
        TaskLocations.locationID.remove(removeItem);

        try {
            new BackgroundTask<Void>(() -> {
                final TaskDao taskDao = MyDatabaseAccessor.getInstance(this.getApplicationContext()).getTaskDao();

                taskDao.deleteByName(removeItem);
                return null;

            }).execute().get();
        } catch (final ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }

        if (MapsActivity.isRunning) {
            Intent intents = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intents);
        } else if (EndTaskActivity.isRunning) {
            Intent intents = new Intent(this, EndTaskActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intents);
        }
    }


}
