package com.ikvant.loriapp.dagger;

/**
 * Created by ikvant.
 */

import com.ikvant.loriapp.ui.LoginActivity;
import com.ikvant.loriapp.ui.editenrty.EditTimeEntryActivity;
import com.ikvant.loriapp.ui.weekpages.WeekPagerActivity;
import com.ikvant.loriapp.ui.weekpages.WeekPagerFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ikvant.
 */

@Module
abstract class AndroidBindingModule {
    @ContributesAndroidInjector
    abstract LoginActivity loginActivity();

    @ContributesAndroidInjector
    abstract WeekPagerActivity listActivity();

    @ContributesAndroidInjector
    abstract EditTimeEntryActivity editTimeEntryActivity();

    @ContributesAndroidInjector
    abstract WeekPagerFragment weekPagerFragment();
}