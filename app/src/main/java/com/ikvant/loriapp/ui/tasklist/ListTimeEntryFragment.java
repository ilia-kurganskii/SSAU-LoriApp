package com.ikvant.loriapp.ui.tasklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.ui.editenrty.EditTimeEntryActivity;
import com.ikvant.loriapp.ui.search.SearchActivity;

import java.util.List;

/**
 * Created by ikvant.
 */

public class ListTimeEntryFragment extends Fragment implements Contract.View, ListAdapter.OnItemClickListener {

    public static final int EDIT_ACTIVIY_CODE = 1;
    private View root;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private SwipeRefreshLayout refreshLayout;

    private LinearLayoutManager layoutManager;

    private Contract.Presenter presenter;

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                presenter.onEndOfPage();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.a_task_list, container, false);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = root.findViewById(R.id.time_entry_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.setLayoutManager(layoutManager);

        listAdapter = new ListAdapter();
        listAdapter.setClickItemListener(this);
        recyclerView.setAdapter(listAdapter);

        refreshLayout = root.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(() -> {
            presenter.reload();
        });

        root.findViewById(R.id.add_fab).setOnClickListener(v -> {
            presenter.createNewEntry();
        });

        root.findViewById(R.id.search_fab).setOnClickListener(v -> {
            presenter.searchEntries();
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ACTIVIY_CODE && resultCode == Activity.RESULT_OK) {
            String id = data.getStringExtra(EditTimeEntryActivity.EXTRA_ID);
            int code = data.getIntExtra(EditTimeEntryActivity.EXTRA_RESULT, -1);
            switch (code) {
                case EditTimeEntryActivity.CHANGED:
                    presenter.onChangeEntry(id);
                    break;
                case EditTimeEntryActivity.CREATED:
                    presenter.onCreateEntry(id);
                    break;
                case EditTimeEntryActivity.DELETED:
                    presenter.onDeleteEntry(id);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    public void setTimeEntries(List<TimeEntry> entryList) {
        listAdapter.setItems(entryList);
    }

    @Override
    public void addTimeEntries(List<TimeEntry> entryList) {
        listAdapter.addItems(entryList);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
        if (isResumed()) {
            presenter.onResume();
        }
    }


    @Override
    public void showEditEntryScreen(String id) {
        EditTimeEntryActivity.startMeForResult(this, id, EDIT_ACTIVIY_CODE);
    }

    public void showNewEntryScreen() {
        EditTimeEntryActivity.startMeForResult(this, EDIT_ACTIVIY_CODE);
    }

    @Override
    public void showSearchScreen() {
        SearchActivity.startMe(getActivity());
    }

    @Override
    public void showOfflineMessage() {
        Snackbar.make(root, R.string.error_offline, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingIndicator(boolean isLoading) {
        if (listAdapter.isEmpty() || !isLoading) {
            refreshLayout.setRefreshing(isLoading);
        }
        listAdapter.setLoading(isLoading);

    }

    @Override
    public void deleteItem(TimeEntry item) {
        listAdapter.deleteItem(item);
    }

    @Override
    public void insertItem(TimeEntry item) {
        listAdapter.insertItem(item);
    }

    @Override
    public void changeItem(TimeEntry item) {
        listAdapter.changeItem(item);
    }

    @Override
    public void onClick(View view, String id) {
        presenter.openEntryDetail(id);
    }

    public static ListTimeEntryFragment newInstance() {
        return new ListTimeEntryFragment();
    }
}
