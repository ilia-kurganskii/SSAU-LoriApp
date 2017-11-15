package com.ikvant.loriapp.ui.tasklist;

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
        EditTimeEntryActivity.startMe(getActivity(), id);
    }

    public void showNewEntryScreen() {
        EditTimeEntryActivity.startMe(getActivity());
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
        refreshLayout.setRefreshing(isLoading);
    }

    @Override
    public void onClick(View view, String id) {
        presenter.openEntryDetail(id);
    }

    public static ListTimeEntryFragment newInstance() {
        return new ListTimeEntryFragment();
    }
}
