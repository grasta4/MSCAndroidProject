package com.example.msc;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        public CardView cardView; // wraps the text views inside
        public TextView taskTextView; // text view that displays the task descriptions
        public TextView infoText;
        public ImageView taskIcon;
        OnTaskListener onTaskListener;

        public TaskViewHolder(View v, OnTaskListener onTaskListener) {
            super(v);
            cardView = v.findViewById(R.id.card_view);
            taskTextView = v.findViewById(R.id.task_description);
            infoText = v.findViewById(R.id.info_text);
            taskIcon = v.findViewById(R.id.task_icon);
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

        // cardview template fetched from endtask_cardview.xml
        // open endtask_cardview.xml to change font size etc.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.endtask_cardview, viewGroup, false);

        RecyclerAdapter.TaskViewHolder viewHolder = new TaskViewHolder(v, mOnTaskListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder viewHolder, int i) {
        viewHolder.taskTextView.setText(taskLocations.get(i)); // sets the descriptions to the text view
        viewHolder.infoText.setText("Tap to remove.");
        viewHolder.taskIcon.setImageResource(R.drawable.clipboard);
    }

    @Override
    public int getItemCount() {
        return taskLocations.size(); // not really used in our view
    }

    // interface that enables the connection to EndTaskActivity.java to call the onClick functions
    public interface OnTaskListener {
        void onTaskClick(int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
