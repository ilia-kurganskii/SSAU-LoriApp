package com.ikvant.loriapp.dagger;

/**
 * Created by ikvant.
 */

import com.ikvant.loriapp.ui.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ikvant.
 */

@Module
abstract class AndroidBindingModule {
    @ContributesAndroidInjector
    abstract LoginActivity loginActivity();
}