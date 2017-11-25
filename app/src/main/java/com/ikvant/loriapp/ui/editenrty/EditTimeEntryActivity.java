package com.ikvant.loriapp.ui.editenrty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

public class EditTimeEntryActivity extends BaseActivity {
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_RESULT = "result";

    public static final int CREATED = 0;
    public static final int DELETED = 1;
    public static final int CHANGED = 2;

    @Inject
    protected EditEntryPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        enableHomeUp();

        EditTimeEntryFragment tasksFragment = (EditTimeEntryFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (tasksFragment == null) {
            tasksFragment = EditTimeEntryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), tasksFragment, R.id.content);
        }

        String id = getIntent().getStringExtra(EXTRA_ID);
        presenter.setView(id, tasksFragment);
    }

    public static void startMeForResult(Fragment fragment, String id, int code) {
        Intent intent = new Intent(fragment.getActivity(), EditTimeEntryActivity.class);
        intent.putExtra(EXTRA_ID, id);
        fragment.startActivityForResult(intent, code);
    }

    public static void startMeForResult(Fragment fragment, int code) {
        startMeForResult(fragment, null, code);
    }
}
