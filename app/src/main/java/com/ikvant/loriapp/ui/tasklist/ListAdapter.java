package com.ikvant.loriapp.ui.tasklist;

import android.annotation.SuppressLint;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder> {
    private static final int ID_TASK = 1;
    private static final int ID_HEADER = 2;
    private List<Object> items = new ArrayList<>(0);
    private OnItemClickListener listener;

    public void setItems(SparseArray<Set<TimeEntry>> list) {
        items.clear();
        for (int i = 0; i < list.size(); i++) {
            items.add(list.keyAt(i));
            items.addAll(list.valueAt(i));
        }
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        switch (viewType) {
            case ID_TASK:
                return new TaskHolder(parent);
            case ID_HEADER:
                return new HeaderHolder(parent);
        }
        return new TaskHolder(parent);
    }

    @Override
    public long getItemId(int position) {
        Object item = items.get(position);
        if (item instanceof TimeEntry) {
            return ID_TASK;
        }
        if (item instanceof Integer) {
            return ID_HEADER;
        }
        return super.getItemId(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindData(items.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Integer) {
            return ID_HEADER;
        }
        return ID_TASK;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setClickItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private class TaskHolder extends ItemViewHolder<TimeEntry> implements View.OnClickListener {
        private TextView desciription;
        private TextView taskName;
        private TextView time;
        private AppCompatImageView syncView;

        private String id;

        TaskHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.i_task, parent, false));
            itemView.setOnClickListener(this);
            desciription = itemView.findViewById(R.id.i_description);
            taskName = itemView.findViewById(R.id.i_task);
            time = itemView.findViewById(R.id.i_time);
            syncView = itemView.findViewById(R.id.i_sync);
        }

        @SuppressLint("DefaultLocale")
        public void bind(TimeEntry entry) {
            desciription.setText(entry.getDescription());
            taskName.setText(entry.getTaskName());
            time.setText(String.format("%2dh %2dm", entry.getTimeInMinutes() / 60, entry.getTimeInMinutes() % 60));
            syncView.setImageResource(entry.isSync() ? R.drawable.ic_sync : R.drawable.ic_not_sync);

            id = entry.getId();
        }


        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, id);
            }
        }
    }

    private class HeaderHolder extends ItemViewHolder<Integer> {
        private TextView dayDiapason;
        private DateFormat dateFormat = new SimpleDateFormat("dd MMM");


        HeaderHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.i_header, parent, false));
            dayDiapason = itemView.findViewById(R.id.week_diapason);
        }

        @Override
        public void bind(Integer weekIndex) {
            Date start = DateUtils.getStartDate(weekIndex);
            Date end = DateUtils.getEndDate(weekIndex);
            dayDiapason.setText(String.format("%s - %s", dateFormat.format(start), dateFormat.format(end)));
        }
    }

    abstract class ItemViewHolder<T> extends RecyclerView.ViewHolder {

        ItemViewHolder(View itemView) {
            super(itemView);
        }

        void bindData(Object o) {
            bind((T) o);
        }

        public abstract void bind(T object);
    }

    public interface OnItemClickListener {
        void onClick(View view, String id);
    }
}
