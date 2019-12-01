package com.example.msc;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder> {

    public static ArrayList<String> taskLocations; // takes the task description of the saved tasks in TaskLocations.class
    private OnTaskListener mOnTaskListener; // on click listener for removing tasks (passed to all relevant functions/classes)

    public RecyclerAdapter(HashMap<String, LatLng> taskImportData, OnTaskListener onTaskListener) {
        this.mOnTaskListener = onTaskListener;

        // import task data from HashMap inside TaskLocations.class
        taskLocations = new ArrayList<String>();
        for (HashMap.Entry<String, LatLng> entry : taskImportData.entrySet()) {
            taskLocations.add(entry.getKey());
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView taskTextView; // text view that displays the task descriptions
        OnTaskListener onTaskListener;

        public TaskViewHolder(TextView v, OnTaskListener onTaskListener) {
            super(v);
            taskTextView = v;
            this.onTaskListener = onTaskListener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // takes the position of the task in the layout
            // important to determine which task to delete from HashMap
            onTaskListener.onTaskClick(getAdapterPosition());
        }
    }



    @NonNull
    @Override
    public RecyclerAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // textview template fetched from endtask_textview.xml
        // open endtask_textview.xml to change font size etc.
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.endtask_textview, viewGroup, false);

        RecyclerAdapter.TaskViewHolder viewHolder = new TaskViewHolder(v, mOnTaskListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder viewHolder, int i) {
        viewHolder.taskTextView.setText(taskLocations.get(i)); // sets the descriptions to the text view
    }

    @Override
    public int getItemCount() {
        return taskLocations.size(); // not really used in our view
    }

    // interface that enables the connection to EndTaskActivity.java to call the onClick functions
    public interface OnTaskListener {
        void onTaskClick(int position);
    }
}
