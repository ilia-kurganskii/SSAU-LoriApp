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

import java.util.List;

/**
 * Created by ikvant.
 */

public class ListTimeEntryFragment extends Fragment implements Contract.View, ListAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private SwipeRefreshLayout refreshLayout;

    private View root;

    private Contract.Presenter presenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.a_task_list, container, false);
        this.root = root;
        // Set up tasks view
        recyclerView = root.findViewById(R.id.time_entry_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new ListAdapter();
        listAdapter.setClickItemListener(this);
        recyclerView.setAdapter(listAdapter);

        root.findViewById(R.id.add).setOnClickListener((view) -> {
            presenter.createNewEntry();
        });

        refreshLayout = root.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(() -> presenter.reload());

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void showTimeEntries(List<TimeEntry> entryList) {
        listAdapter.setItems(entryList);
    }

    @Override
    public void setRefresh(boolean refresh) {
        refreshLayout.setRefreshing(refresh);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showNewEntryScreen() {
        EditTimeEntryActivity.startMe(getActivity());
    }

    @Override
    public void showEditEntryScreen(String id) {
        EditTimeEntryActivity.startMe(getActivity(), id);
    }

    @Override
    public void showOfflineMessage() {
        Snackbar.make(root, "You are offline", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view, int position) {
        presenter.openEntryDetail(position);
    }

    public static ListTimeEntryFragment newInstance() {
        return new ListTimeEntryFragment();
    }
}
