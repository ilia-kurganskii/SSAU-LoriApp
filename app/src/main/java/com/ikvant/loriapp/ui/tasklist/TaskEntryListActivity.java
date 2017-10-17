package com.ikvant.loriapp.ui.tasklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by ikvant.
 */

public class TaskEntryListActivity extends DaggerAppCompatActivity {

    @Inject
    protected EntryController entryController;

    @Inject
    protected TaskEntryPresenter taskEntryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        ListTimeEntryFragment tasksFragment = (ListTimeEntryFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (tasksFragment == null) {
            tasksFragment = ListTimeEntryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), tasksFragment, R.id.content);
        }

        taskEntryPresenter.setView(tasksFragment);
    }

    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, TaskEntryListActivity.class);
        activity.startActivity(intent);
    }
}

