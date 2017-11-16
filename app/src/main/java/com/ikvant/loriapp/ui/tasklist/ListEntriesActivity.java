package com.ikvant.loriapp.ui.tasklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class ListEntriesActivity extends BaseActivity {

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

    public static void startMe(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), ListEntriesActivity.class);
        fragment.startActivity(intent);
    }
}

