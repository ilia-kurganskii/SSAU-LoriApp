package com.ikvant.loriapp.ui.tasklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.ui.editenrty.EditTimeEntryActivity;
import com.ikvant.loriapp.utils.SimpleCallback;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class TaskEntryListActivity extends BaseActivity implements ListAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    @Inject
    protected EntryController entryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_task_list);
        recyclerView = findViewById(R.id.time_entry_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        listAdapter = new ListAdapter();
        listAdapter.setClickItemListener(this);
        recyclerView.setAdapter(listAdapter);

        findViewById(R.id.add).setOnClickListener((view) -> {
            EditTimeEntryActivity.startMe(this);
        });

        SwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                entryController.synс(new SimpleCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        super.onSuccess(data);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        super.onFailure(throwable);
                        refreshLayout.setRefreshing(false);

                    }
                });
            }
        });

        entryController.synс(new SimpleCallback<Void>());
    }

    @Override
    protected void onResume() {
        super.onResume();
        entryController.loadTimeEntries(new SimpleCallback<List<TimeEntry>>() {
            @Override
            public void onSuccess(List<TimeEntry> data) {
                listAdapter.setItems(data);
            }
        });
    }

    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, TaskEntryListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View view, int position) {
        TimeEntry entry = listAdapter.getItems().get(position);
        EditTimeEntryActivity.startMe(this, entry.getId());
    }
}

