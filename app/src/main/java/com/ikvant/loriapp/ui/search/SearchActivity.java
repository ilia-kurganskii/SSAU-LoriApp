package com.ikvant.loriapp.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.ui.BaseActivity;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

public class SearchActivity extends BaseActivity {

    @Inject
    protected SearchPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        enableHomeUp();

        SearchFragment weekPagerFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (weekPagerFragment == null) {
            weekPagerFragment = SearchFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), weekPagerFragment, R.id.content);
        }

        presenter.setView(weekPagerFragment);
    }


    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

}
