package com.ikvant.loriapp.ui.editenrty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.project.Project;
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
    private AppCompatSpinner projectSpinner;
    private EditText description;
    private EditText dateEditText;
    private EditText time;
    private EditText editTags;
    private Button save;
    private Button delete;
    private ContentLoadingProgressBar progressBar;
    private View content;

    private String[] nameTags;
    private boolean[] checkedTags;

    private Contract.Presenter presenter;

    private ArrayAdapter<Task> taskAdapter;
    private ArrayAdapter<Project> projectAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.a_edit_time_entry, container, false);
        taskSpinner = root.findViewById(R.id.edit_task_spinner);
        projectSpinner = root.findViewById(R.id.edit_project_spinner);
        description = root.findViewById(R.id.edit_description);
        dateEditText = root.findViewById(R.id.edit_date);
        editTags = root.findViewById(R.id.edit_tags);
        save = root.findViewById(R.id.edit_save);
        delete = root.findViewById(R.id.edit_delete);
        time = root.findViewById(R.id.edit_time);
        progressBar = root.findViewById(R.id.progress);
        content = root.findViewById(R.id.content);

        taskAdapter = new ArrayAdapter<>(getContext(), R.layout.i_spinner_task, R.id.task_name);
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

        projectAdapter = new ArrayAdapter<Project>(getContext(), R.layout.i_spinner_task, R.id.task_name);
        projectSpinner.setAdapter(projectAdapter);

        projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setProject(position);
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

        editTags.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.select_tags)
                    .setMultiChoiceItems(nameTags, checkedTags, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            presenter.checkTag(indexSelected, isChecked);
                        }
                    }).setPositiveButton("OK", (dialog1, id) -> {
                        presenter.saveTags();
                    }).setNegativeButton("Cancel", (dialog12, id) -> {
                    }).create();
            dialog.show();
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
    public void setProjects(List<Project> projects) {
        projectAdapter.clear();
        projectAdapter.addAll(projects);
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
    public void setProject(int position) {
        projectSpinner.setSelection(position);
    }

    @Override
    public void setTags(String[] nameTags, boolean[] selectedTags, List<String> selectedListTags) {
        this.nameTags = nameTags;
        this.checkedTags = selectedTags;
        StringBuilder tags = new StringBuilder();
        for (String name : selectedListTags) {
            tags.append(name).append(" ");
        }
        editTags.setText(tags.toString());
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
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_LONG).show();
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
