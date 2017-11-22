package com.ikvant.loriapp.dagger;

import com.ikvant.loriapp.LoriApp;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;

/**
 * Created by ikvant.
 */

@Singleton
@Component(modules = {
        AppModule.class,DatabaseModule.class, AndroidBindingModule.class
})
public interface AppComponent extends AndroidInjector<LoriApp> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<LoriApp> {
    }

}