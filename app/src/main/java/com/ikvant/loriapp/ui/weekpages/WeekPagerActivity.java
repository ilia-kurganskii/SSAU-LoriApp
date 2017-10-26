package com.ikvant.loriapp.ui.weekpages;

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

public class WeekPagerActivity extends BaseActivity {

    @Inject
    protected EntryController entryController;

    @Inject
    protected WeekPagerPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        WeekPagerFragment weekPagerFragment = (WeekPagerFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (weekPagerFragment == null) {
            weekPagerFragment = WeekPagerFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), weekPagerFragment, R.id.content);
        }

        presenter.setView(weekPagerFragment);
    }

    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, WeekPagerActivity.class);
        activity.startActivity(intent);
    }
}

