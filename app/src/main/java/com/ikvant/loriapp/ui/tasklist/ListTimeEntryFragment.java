package com.ikvant.loriapp.ui.tasklist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.ui.editenrty.EditTimeEntryActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ikvant.
 */

public class ListTimeEntryFragment extends Fragment implements Contract.View, ListAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private TextView dateDiapason;

    private DateFormat dateFormat = new SimpleDateFormat("dd MMM");

    private Contract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.a_task_list, container, false);
        // Set up tasks view
        recyclerView = root.findViewById(R.id.time_entry_list);
        dateDiapason = root.findViewById(R.id.week_diapason);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new ListAdapter();
        listAdapter.setClickItemListener(this);
        recyclerView.setAdapter(listAdapter);

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
    public void showTimeEntries(List<TimeEntry> entryList) {
        listAdapter.setItems(entryList);
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

    @Override
    public void showDiapasonLabel(Date start, Date end) {
        dateDiapason.setText(String.format("%s - %s", dateFormat.format(start), dateFormat.format(end)));
    }

    @Override
    public void onClick(View view, int position) {
        presenter.openEntryDetail(position);
    }

    public static ListTimeEntryFragment newInstance() {
        return new ListTimeEntryFragment();
    }
}
