package com.ikvant.loriapp.ui.search;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.ui.tasklist.Contract;
import com.ikvant.loriapp.ui.tasklist.ListTimeEntryFragment;
import com.ikvant.loriapp.utils.ActivityUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SearchActivity extends DaggerAppCompatActivity implements Contact.View {
    private DateFormat dateFormat = new SimpleDateFormat("dd MMM");

    @Inject
    protected SearchPresenter presenter;

    private EditText searchText;
    private EditText from;
    private EditText to;

    private ListTimeEntryFragment listTimeEntryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_search);

        searchText = findViewById(R.id.search_text);
        from = findViewById(R.id.search_from);
        to = findViewById(R.id.search_to);

        to.setOnClickListener(v -> {
            presenter.openToDialog();

        });

        from.setOnClickListener(v -> {
            presenter.openFromDialog();
        });


        SwitchCompat dateSwitch = findViewById(R.id.search_switch);
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

        listTimeEntryFragment = (ListTimeEntryFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (listTimeEntryFragment == null) {
            listTimeEntryFragment = ListTimeEntryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), listTimeEntryFragment, R.id.content);
        }

        presenter.setView(this);

        findViewById(R.id.search_button).setOnClickListener(view -> {
            presenter.setText(searchText.getText().toString());
            presenter.search();
        });
    }


    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this,
                (view, year1, month1, dayOfMonth) -> presenter.setDateFrom(dayOfMonth, month1, year1), year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void showDateToDialog(int day, int month, int year) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(SearchActivity.this,
                (view, year1, month1, dayOfMonth) -> presenter.setDateTo(dayOfMonth, month1, year1), year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void showTimeEntries(List<TimeEntry> entryList) {
        listTimeEntryFragment.showTimeEntries(entryList);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        listTimeEntryFragment.setPresenter(presenter);
    }

    @Override
    public void showEditEntryScreen(String id) {
        listTimeEntryFragment.showEditEntryScreen(id);
    }

    @Override
    public void showDiapasonLabel(Date start, Date end) {
        listTimeEntryFragment.showDiapasonLabel(start, end);
    }
}
