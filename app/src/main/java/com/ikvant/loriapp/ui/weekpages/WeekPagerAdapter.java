package com.ikvant.loriapp.ui.weekpages;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.ui.tasklist.ListTimeEntryFragment;
import com.ikvant.loriapp.ui.tasklist.TaskEntryPresenter;

import java.util.Set;

/**
 * Created by ikvant.
 */
public class WeekPagerAdapter extends FragmentStatePagerAdapter {
    private SparseArray<Set<TimeEntry>> weekArray = new SparseArray<>();

    private EntryController entryController;

    public WeekPagerAdapter(FragmentManager fm, EntryController entryController) {
        super(fm);
        this.entryController = entryController;
    }

    public void setWeekArray(SparseArray<Set<TimeEntry>> weekArray) {
        this.weekArray = weekArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return weekArray.size();
    }

    @Override
    public Fragment getItem(int position) {
        ListTimeEntryFragment fragment = ListTimeEntryFragment.newInstance();
        TaskEntryPresenter presenter = new TaskEntryPresenter(weekArray.keyAt(position), entryController);
        presenter.setView(fragment);
        return fragment;
    }
}
