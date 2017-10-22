package com.ikvant.loriapp.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.ui.tasklist.ListTimeEntryFragment;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SearchActivity extends DaggerAppCompatActivity {

    @Inject
    protected SearchPresenter presenter;

    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_search);

        searchText = findViewById(R.id.search_text);

        ListTimeEntryFragment listTimeEntryFragment = (ListTimeEntryFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (listTimeEntryFragment == null) {
            listTimeEntryFragment = ListTimeEntryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), listTimeEntryFragment, R.id.content);
        }

        presenter.setView(listTimeEntryFragment);

        findViewById(R.id.search_button).setOnClickListener(view -> {
            presenter.searchByText(searchText.getText().toString());
        });
    }


    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }
}
