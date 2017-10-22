package com.ikvant.loriapp.ui.weekpages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.ui.editenrty.EditTimeEntryActivity;
import com.ikvant.loriapp.ui.search.SearchActivity;

import java.util.Set;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * Created by ikvant.
 */

public class WeekPagerFragment extends DaggerFragment implements Contract.View {
    private WeekPagerAdapter adapter;

    private ViewPager weekPager;
    private SwipeRefreshLayout refreshLayout;
    private View root;

    @Inject
    protected EntryController controller;

    private Contract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.a_week_pager, container, false);
        // Set up tasks view
        adapter = new WeekPagerAdapter(getChildFragmentManager(), controller);

        weekPager = root.findViewById(R.id.week_pager);
        weekPager.setAdapter(adapter);


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

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void setWeeks(SparseArray<Set<TimeEntry>> weekArray) {
        adapter.setWeekArray(weekArray);
    }

    @Override
    public void showLoadingIndicator(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    public void showNewEntryScreen() {
        EditTimeEntryActivity.startMe(getActivity());
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
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showSearchScreen() {
        SearchActivity.startMe(getActivity());
    }

    public static WeekPagerFragment newInstance() {
        return new WeekPagerFragment();
    }

}
