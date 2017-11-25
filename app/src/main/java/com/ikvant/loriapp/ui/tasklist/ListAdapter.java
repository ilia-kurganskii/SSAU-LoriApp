package com.ikvant.loriapp.ui.tasklist;

import android.annotation.SuppressLint;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ikvant.loriapp.utils.DateUtils.getWeekDayIndex;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder> {
    private static final int ID_TASK = 1;
    private static final int ID_HEADER = 2;
    private static final int ID_FOOTER = 3;
    private List<Object> items = new ArrayList<>(0);
    private OnItemClickListener listener;

    private int lastWeekIndex;
    private boolean isLoading;

    public void setItems(List<TimeEntry> list) {
        items.clear();
        lastWeekIndex = -1;
        addAll(list);
        notifyDataSetChanged();
    }

    public void addItems(List<TimeEntry> list) {
        int lastPosition = items.size();
        addAll(list);
        int newItemsSize = items.size() - lastPosition;
        notifyItemRangeInserted(lastPosition, newItemsSize);
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
            case ID_FOOTER:
                return new FooterHolder(parent);
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
        if (holder instanceof FooterHolder) {
            ((FooterHolder) holder).bind(isLoading && !items.isEmpty());
        } else {
            holder.bindData(items.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            return ID_FOOTER;
        } else if (items.get(position) instanceof Integer) {
            return ID_HEADER;
        }
        return ID_TASK;
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void setClickItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void deleteItem(TimeEntry item) {
        int position = items.indexOf(item);
        if (position != -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void insertItem(TimeEntry item) {
        int position = Collections.binarySearch(items, getWeekDayIndex(item.getDate()), (o1, key) -> {
            int weekDayIndex = 0;
            if (o1 instanceof TimeEntry) {
                weekDayIndex = getWeekDayIndex(((TimeEntry) o1).getDate());
            } else {
                weekDayIndex = ((Integer) o1);
            }
            return Integer.compare((Integer) key, weekDayIndex);
        });
        if (position < 0) {
            position = -position - 1;
        }
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void changeItem(TimeEntry item) {
        int position = items.indexOf(item);
        if (position != -1) {
            items.set(position, item);
            notifyItemChanged(position);
        }
    }

    private void addAll(List<TimeEntry> list) {
        for (TimeEntry entry : list) {
            int weekIndex = DateUtils.getWeekIndex(entry.getDate());
            if (lastWeekIndex != weekIndex) {
                lastWeekIndex = weekIndex;
                items.add(weekIndex);
            }
            items.add(entry);
        }
    }


    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyItemChanged(items.size());
    }

    private class TaskHolder extends ItemViewHolder<TimeEntry> implements View.OnClickListener {
        private final DateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());

        private TextView description;
        private TextView taskName;
        private TextView tags;
        private TextView time;
        private AppCompatImageView syncView;
        private TextView letter;

        private String id;

        TaskHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.i_task, parent, false));
            itemView.setOnClickListener(this);
            description = itemView.findViewById(R.id.i_description);
            taskName = itemView.findViewById(R.id.i_task);
            time = itemView.findViewById(R.id.i_time);
            tags = itemView.findViewById(R.id.i_tags);
            syncView = itemView.findViewById(R.id.i_sync);
            letter = itemView.findViewById(R.id.i_letter);
        }

        @SuppressLint("DefaultLocale")
        public void bind(TimeEntry entry) {
            description.setText(entry.getDescription());
            taskName.setText(entry.getTaskName());
            time.setText(String.format("%2dh %2dm", entry.getTimeInMinutes() / 60, entry.getTimeInMinutes() % 60));
            syncView.setVisibility(entry.isSync() ? View.INVISIBLE : View.VISIBLE);
            tags.setText(getFormattedTags(entry.getTags()));
            id = entry.getId();
            letter.setText(dateFormat.format(entry.getDate()));
        }


        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, id);
            }
        }

        private String getFormattedTags(List<Tag> tags) {
            StringBuilder result = new StringBuilder();
            for (Tag tag : tags) {
                result.append(tag.getName()).append(" ");
            }
            return result.toString();
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

    private class FooterHolder extends ItemViewHolder<Boolean> {
        private ContentLoadingProgressBar progressBar;

        FooterHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.i_footer, parent, false));
            progressBar = itemView.findViewById(R.id.i_progress);
        }

        @Override
        public void bind(Boolean isVisible) {
            if (isVisible) {
                progressBar.show();
            } else {
                progressBar.hide();
            }
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
