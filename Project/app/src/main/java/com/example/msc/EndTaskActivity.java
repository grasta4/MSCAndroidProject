package com.example.msc;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.msc.persistence.MyDatabaseAccessor;
import com.example.msc.persistence.dao.TaskDao;
import com.example.msc.util.BackgroundTask;

import java.util.concurrent.ExecutionException;

public class EndTaskActivity extends AppCompatActivity implements RecyclerAdapter.OnTaskListener {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView activeTasksView;
    private RecyclerView.Adapter recyclerAdapter;

    public static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_task);
        activeTasksView = findViewById(R.id.recycler_view);
        activeTasksView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        activeTasksView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(TaskLocations.taskLocations, this);
        activeTasksView.setAdapter(recyclerAdapter);

        isRunning = true;
    }

    @Override
    public void onTaskClick(int position) {
        final int positions = position; // position of the item in the recycler view


        // basic alert to prevent mistakingly deleting tasks
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Confirm Removal");
        alertBuilder.setMessage("Are you sure you want to remove the task?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // get the task description as a string from the recycler view
                        String string = RecyclerAdapter.taskLocations.get(positions);
                        // take this string to remove the object from the HashMap (location + description)
                        TaskLocations.taskLocations.remove(string);
                        TaskLocations.locationID.remove(string);

                        // take string to remove task from database
                        try {
                            new BackgroundTask<Void>(() -> {
                                final TaskDao taskDao = MyDatabaseAccessor.getInstance(EndTaskActivity.this.getApplicationContext()).getTaskDao();

                                taskDao.deleteByName(string);
                                return null;

                            }).execute().get();
                        } catch (final ExecutionException | InterruptedException exception) {
                            exception.printStackTrace();
                        }
                        // restarts the view to erase the text view (otherwise it would still show the task)
                        recreate();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertdialog = alertBuilder.create();
        alertdialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }
}
