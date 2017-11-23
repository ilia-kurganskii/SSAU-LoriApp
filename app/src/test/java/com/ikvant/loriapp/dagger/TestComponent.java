package com.ikvant.loriapp.dagger;

import com.ikvant.loriapp.ui.editenrty.EditEntryPresenterTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
		TestNetworkModule.class,TestDatabaseModule.class
})
public interface TestComponent{
	void inject(EditEntryPresenterTest test);
}
