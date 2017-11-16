package com.ikvant.loriapp.dagger;

/**
 * Created by ikvant.
 */

import com.ikvant.loriapp.ui.editenrty.EditTimeEntryActivity;
import com.ikvant.loriapp.ui.login.LoginActivity;
import com.ikvant.loriapp.ui.search.SearchActivity;
import com.ikvant.loriapp.ui.tasklist.ListEntriesActivity;

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
    abstract ListEntriesActivity listActivity();

    @ContributesAndroidInjector
    abstract EditTimeEntryActivity editTimeEntryActivity();

    @ContributesAndroidInjector
    abstract SearchActivity searchActivity();
}