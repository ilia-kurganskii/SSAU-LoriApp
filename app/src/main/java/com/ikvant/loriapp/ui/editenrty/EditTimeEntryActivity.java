package com.ikvant.loriapp.ui.editenrty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.state.entry.TaskController;
import com.ikvant.loriapp.state.entry.TimeEntryController;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

public class EditTimeEntryActivity extends BaseActivity {
    private static final String EXTRA_ID = "EXTRA_ID";

    @Inject
    protected TimeEntryController timeEntryController;

    @Inject
    protected TaskController taskController;

    @Inject
    protected EditEntryPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getIntent().getStringExtra(EXTRA_ID);

        setContentView(R.layout.content);

        EditTimeEntryFragment tasksFragment = (EditTimeEntryFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (tasksFragment == null) {
            tasksFragment = EditTimeEntryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), tasksFragment, R.id.content);
        }

        presenter.setView(id, tasksFragment);
    }

    public static void startMe(Activity activity, String id) {
        Intent intent = new Intent(activity, EditTimeEntryActivity.class);
        intent.putExtra(EXTRA_ID, id);
        activity.startActivity(intent);
    }

    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, EditTimeEntryActivity.class);
        activity.startActivity(intent);
    }
}
