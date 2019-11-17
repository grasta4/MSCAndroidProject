package com.example.msc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewMap(View v) {
        Intent mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);
    }

    public void newTask(View v) {
        Intent newTaskIntent = new Intent(this, AddTaskActivity.class);
        startActivity(newTaskIntent);
    }
}
