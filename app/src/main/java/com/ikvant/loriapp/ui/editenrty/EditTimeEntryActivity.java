package com.ikvant.loriapp.ui.editenrty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.timeentry.TimeEntryController;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.utils.SimpleCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class EditTimeEntryActivity extends BaseActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_ID = "EXTRA_ID";

    @Inject
    protected TimeEntryController entryController;

    private AppCompatSpinner taskSpinner;
    private EditText description;
    private EditText dateEditText;
    private EditText time;

    private TaskSpinnerAdapter taskAdapter;

    private TimeEntry currentTimeEntry;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_edit_time_entry);

        taskSpinner = findViewById(R.id.edit_task_spinner);
        description = findViewById(R.id.edit_description);
        dateEditText = findViewById(R.id.edit_date);
        time = findViewById(R.id.edit_time);

        taskAdapter = new TaskSpinnerAdapter(this);
        taskSpinner.setAdapter(taskAdapter);

        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Task task = taskAdapter.getItem(position);
                currentTimeEntry.setTask(task);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        time.setOnClickListener(v -> {
            showTimePickerDialogDialog();
        });

        dateEditText.setOnClickListener(v -> {
            showDateDialogPicker();
        });

        String id = getIntent().getStringExtra(EXTRA_ID);

        entryController.load(id, new SimpleCallback<TimeEntry>() {
            @Override
            public void onSuccess(TimeEntry data) {
                currentTimeEntry = data;
                //taskSpinner.setSelection(taskAdapter.getPosition(data.getTask()));
                description.setText(data.getDescription());
                setDateEditText(data.getDate());
                setTimeInMinutes(data.getTimeInMinutes());
            }
        });

        entryController.loadTasks(new SimpleCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> data) {
                taskAdapter.addAll(data);
            }
        });


        findViewById(R.id.edit_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTimeEntry.setDescription(description.getText().toString());
                entryController.save(currentTimeEntry, new SimpleCallback<TimeEntry>() {
                    @Override
                    public void onSuccess(TimeEntry data) {
                        super.onSuccess(data);
                        finish();
                    }
                });
            }
        });

        findViewById(R.id.edit_delete).setOnClickListener((view)->{
            entryController.delete(id, new SimpleCallback<Void>(){
                @Override
                public void onSuccess(Void data) {
                    super.onSuccess(data);
                    finish();
                }
            });

        });
    }

    public void showTimePickerDialogDialog() {
        int hour = currentTimeEntry.getTimeInMinutes() / 60;
        int minute = currentTimeEntry.getTimeInMinutes() % 60;
        new TimePickerDialog(EditTimeEntryActivity.this, this, hour, minute, true)
                .show();
    }

    public void showDateDialogPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimeEntry.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(EditTimeEntryActivity.this, this, year, month, day).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        currentTimeEntry.setTimeInMinutes(hourOfDay * 60 + minute);
        setTimeInMinutes(hourOfDay * 60 + minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimeEntry.getDate());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        currentTimeEntry.setDate(calendar.getTime());
        setDateEditText(calendar.getTime());
    }

    private void setTimeInMinutes(int timeInMinutes) {
        time.setText(String.format("%dh:%2dm", timeInMinutes / 60, timeInMinutes % 60));
    }

    private void setDateEditText(Date date) {
        dateEditText.setText(dateFormat.format(date));
    }

    public static void startMe(Activity activity, String id) {
        Intent intent = new Intent(activity, EditTimeEntryActivity.class);
        intent.putExtra(EXTRA_ID, id);
        activity.startActivity(intent);
    }
}
