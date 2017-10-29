package com.ikvant.loriapp.ui.search;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.ui.editenrty.EditTimeEntryActivity;
import com.ikvant.loriapp.ui.tasklist.ListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by ikvant.
 */

public class SearchFragment extends Fragment implements Contact.View {


    private DateFormat dateFormat = new SimpleDateFormat("dd MMM");

    private Contact.Presenter presenter;

    private EditText searchText;
    private EditText from;
    private EditText to;
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private View root;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.a_search, container, false);

        searchText = root.findViewById(R.id.search_text);
        from = root.findViewById(R.id.search_from);
        to = root.findViewById(R.id.search_to);

        to.setOnClickListener(v -> {
            presenter.openToDialog();

        });

        from.setOnClickListener(v -> {
            presenter.openFromDialog();
        });


        SwitchCompat dateSwitch = root.findViewById(R.id.search_switch);
        dateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.searchByDateEnable(isChecked);
            if (isChecked) {
                from.setVisibility(View.VISIBLE);
                to.setVisibility(View.VISIBLE);
                searchText.setVisibility(View.GONE);
            } else {
                from.setVisibility(View.GONE);
                to.setVisibility(View.GONE);
                searchText.setVisibility(View.VISIBLE);
            }
        });

        root.findViewById(R.id.search_button).setOnClickListener(view -> {
            presenter.setText(searchText.getText().toString());
            presenter.search();
        });


        recyclerView = root.findViewById(R.id.searched_entry_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        return root;
    }


    @Override
    public void setDateFrom(Date date) {
        from.setText(dateFormat.format(date));
    }

    @Override
    public void setDateTo(Date date) {
        to.setText(dateFormat.format(date));
    }

    @Override
    public void showDateFromDialog(int day, int month, int year) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> presenter.setDateFrom(dayOfMonth, month1, year1), year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void showDateToDialog(int day, int month, int year) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> presenter.setDateTo(dayOfMonth, month1, year1), year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void showTimeEntries(SparseArray<Set<TimeEntry>> entryList) {
        adapter.setItems(entryList);
    }

    @Override
    public void setPresenter(Contact.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showEditEntryScreen(String id) {
        EditTimeEntryActivity.startMe(getActivity(), id);
    }

    @Override
    public void showLoadingIndicator(boolean isLoading) {

    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
}
