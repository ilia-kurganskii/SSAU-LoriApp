package com.ikvant.loriapp.dagger;

import android.arch.persistence.room.Room;

import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.database.LoriDatabase;
import com.ikvant.loriapp.database.project.ProjectDao;
import com.ikvant.loriapp.database.tags.TagDao;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.database.user.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestDatabaseModule {
	@Singleton
	@Provides
	TokenDao provideTokenDao() {
		return mock(TokenDao.class);
	}

	@Singleton
	@Provides
	TimeEntryDao provideTimeEntryDao() {
		return mock(TimeEntryDao.class);
	}

	@Singleton
	@Provides
	TaskDao provideTaskDao() {
		return mock(TaskDao.class);
	}

	@Singleton
	@Provides
	UserDao provideUserDao() {
		return mock(UserDao.class);
	}

	@Singleton
	@Provides
	ProjectDao provideProjectDao() {
		return mock(ProjectDao.class);
	}

	@Singleton
	@Provides
	TagDao provideTagsDao() {
		return mock(TagDao.class);
	}
}
