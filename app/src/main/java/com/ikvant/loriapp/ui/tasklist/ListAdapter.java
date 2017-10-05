package com.ikvant.loriapp.ui.tasklist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ikvant.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.TaskHolder> {
    private List<TimeEntry> mDataset = Collections.emptyList();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public void setItems(List<TimeEntry> list) {
        mDataset = list;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.TaskHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.i_task, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new TaskHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setName(mDataset.get(position).getDescription());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView nameTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public TaskHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameTextView = itemView.findViewById(R.id.task_name);
        }

        public void setName(String name) {
            nameTextView.setText(name);
        }

    }
}
