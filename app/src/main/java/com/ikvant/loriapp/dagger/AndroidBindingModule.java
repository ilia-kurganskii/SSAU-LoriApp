package com.ikvant.loriapp.dagger;

/**
 * Created by ikvant.
 */

import com.ikvant.loriapp.ui.LoginActivity;
import com.ikvant.loriapp.ui.tasklist.TaskEntryListActivity;

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
    abstract TaskEntryListActivity listActivity();
}