package com.ikvant.loriapp.ui.tasklist;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.Collections;
import java.util.List;

/**
 * Created by ikvant.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.TaskHolder> {
    private List<TimeEntry> items = Collections.emptyList();
    private OnItemClickListener listener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public void setItems(List<TimeEntry> list) {
        items = list;
        notifyDataSetChanged();
    }

    public List<TimeEntry> getItems() {
        return items;
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
        holder.bind(items.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setClickItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView desciription;
        private TextView taskName;
        private TextView time;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public TaskHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemView.setOnClickListener(this);
            desciription = itemView.findViewById(R.id.i_description);
            taskName = itemView.findViewById(R.id.i_task);
            time = itemView.findViewById(R.id.i_time);
        }

        @SuppressLint("DefaultLocale")
        public void bind(TimeEntry entry) {
            desciription.setText(entry.getDescription());
            taskName.setText(entry.getTaskName());
            time.setText(String.format("%2dh %2dm", entry.getTimeInMinutes() / 60, entry.getTimeInMinutes() % 60));
        }


        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
}
