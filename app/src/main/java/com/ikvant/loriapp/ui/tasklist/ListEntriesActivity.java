package com.ikvant.loriapp.ui.tasklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class ListEntriesActivity extends BaseActivity {

    @Inject
    protected EntryController entryController;

    @Inject
    protected TaskEntryPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        ListTimeEntryFragment weekPagerFragment = (ListTimeEntryFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (weekPagerFragment == null) {
            weekPagerFragment = ListTimeEntryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), weekPagerFragment, R.id.content);
        }

        presenter.setView(weekPagerFragment);
    }

    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, ListEntriesActivity.class);
        activity.startActivity(intent);
    }
}

