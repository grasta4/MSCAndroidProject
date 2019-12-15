package com.example.msc;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import androidx.annotation.Nullable;

public class EndTaskService extends IntentService {

    public EndTaskService() {
        super("EndTaskService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String removeItem = intent.getStringExtra("toDelete");
        Log.d("myApp", "onHandleIntent: "+removeItem );
        TaskLocations.taskLocations.remove(removeItem);
        TaskLocations.locationID.remove(removeItem);
        Log.d("myApp", "onHandleIntent: deleted" );

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
