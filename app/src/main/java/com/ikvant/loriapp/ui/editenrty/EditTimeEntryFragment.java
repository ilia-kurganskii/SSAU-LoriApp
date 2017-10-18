package com.ikvant.loriapp.ui.editenrty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.task.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ikvant.
 */

public class EditTimeEntryFragment extends Fragment implements Contract.View, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private AppCompatSpinner taskSpinner;
    private EditText description;
    private EditText dateEditText;
    private EditText time;
    private Button save;
    private Button delete;
    private ContentLoadingProgressBar progressBar;
    private View content;

    private Contract.Presenter presenter;

    private TaskSpinnerAdapter taskAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.a_edit_time_entry, container, false);
        taskSpinner = root.findViewById(R.id.edit_task_spinner);
        description = root.findViewById(R.id.edit_description);
        dateEditText = root.findViewById(R.id.edit_date);
        save = root.findViewById(R.id.edit_save);
        delete = root.findViewById(R.id.edit_delete);
        time = root.findViewById(R.id.edit_time);
        progressBar = root.findViewById(R.id.progress);
        content = root.findViewById(R.id.content);

        taskAdapter = new TaskSpinnerAdapter(getContext());
        taskSpinner.setAdapter(taskAdapter);

        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setTask(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        time.setOnClickListener(v -> {
            presenter.openTimePickerDialog();
        });

        dateEditText.setOnClickListener(v -> {
            presenter.openDataPickerDialog();
        });


        save.setOnClickListener(v -> {
            presenter.setDescription(description.getText().toString());
            presenter.saveEntry();
        });

        delete.setOnClickListener((view) -> {
            presenter.deleteEntry();
        });

        setHasOptionsMenu(true);

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
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        presenter.setTime(hourOfDay, minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        presenter.setDate(dayOfMonth, month, year);
    }

    @Override
    public void showLoadingIndicator(boolean show) {
        if (show) {
            progressBar.show();
            content.setVisibility(View.GONE);
        } else {
            progressBar.hide();
            content.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showCreateButton() {
        save.setText(R.string.create);
    }

    @Override
    public void showModifyButton() {
        save.setText(R.string.save);
    }

    @Override
    public void showDeleteButton() {
        delete.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTasks(List<Task> tasks) {
        taskAdapter.clear();
        taskAdapter.addAll(tasks);
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDate(Date data) {
        dateEditText.setText(dateFormat.format(data));
    }

    @Override
    public void setTime(int duration) {
        time.setText(String.format("%dh:%2dm", duration / 60, duration % 60));
    }

    @Override
    public void setDescription(String text) {
        description.setText(text);
    }

    @Override
    public void setTask(int position) {
        taskSpinner.setSelection(position);
    }

    @Override
    public void showTimePicker(int hour, int minute) {
        new TimePickerDialog(getContext(), this, hour, minute, true).show();
    }

    @Override
    public void showDatePicker(int day, int month, int year) {
        new DatePickerDialog(getContext(), this, year, month, day).show();
    }

    @Override
    public void showErrorMessage(String text) {

    }

    @Override
    public void showOfflineMessage() {

    }

    @Override
    public void goBack() {
        getActivity().finish();
    }

    public static EditTimeEntryFragment newInstance() {
        return new EditTimeEntryFragment();
    }
}
