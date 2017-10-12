package com.ikvant.loriapp.ui.editenrty;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.timeentry.TimeEntryController;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.utils.SimpleCallback;

import java.util.List;

import javax.inject.Inject;

public class EditTimeEntryActivity extends BaseActivity {
    public static final String EXTRA_ID = "EXTRA_ID";

    @Inject
    TimeEntryController entryController;

    private AppCompatSpinner taskSpinner;
    private EditText description;

    private TaskSpinnerAdapter taskAdapter;

    private TimeEntry currentTimeEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_edit_time_entry);

        taskSpinner = findViewById(R.id.edit_task_spinner);
        description = findViewById(R.id.edit_description);

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

        String id = getIntent().getStringExtra(EXTRA_ID);

        entryController.load(id, new SimpleCallback<TimeEntry>(){
            @Override
            public void onSuccess(TimeEntry data) {
                currentTimeEntry = data;
                //taskSpinner.setSelection(taskAdapter.getPosition(data.getTask()));
                description.setText(data.getDescription());
            }
        });

        entryController.loadTasks(new SimpleCallback<List<Task>>(){
            @Override
            public void onSuccess(List<Task> data) {
                taskAdapter.addAll(data);
            }
        });



        findViewById(R.id.edit_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTimeEntry.setDescription(description.getText().toString());
                entryController.save(currentTimeEntry, new SimpleCallback<TimeEntry>(){
                    @Override
                    public void onSuccess(TimeEntry data) {
                        super.onSuccess(data);
                        finish();
                    }
                });
            }
        });
    }

    public static void startMe(Activity activity, String id) {
        Intent intent = new Intent(activity, EditTimeEntryActivity.class);
        intent.putExtra(EXTRA_ID,id);
        activity.startActivity(intent);
    }
}
