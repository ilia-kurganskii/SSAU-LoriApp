package com.ikvant.loriapp.dagger;

import android.arch.persistence.room.Room;

import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.database.LoriDatabase;
import com.ikvant.loriapp.database.project.ProjectDao;
import com.ikvant.loriapp.database.tags.TagDao;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.database.user.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.AndroidSupportInjectionModule;

@Module(includes = AndroidSupportInjectionModule.class)
public class DatabaseModule {
	@Singleton
	@Provides
	LoriDatabase provideDb(LoriApp app) {
		return Room.databaseBuilder(app, LoriDatabase.class, "lori.db").build();
	}

	@Singleton
	@Provides
	TokenDao provideTokenDao(LoriDatabase db) {
		return db.tokenDao();
	}

	@Singleton
	@Provides
	TimeEntryDao provideTimeEntryDao(LoriDatabase db) {
		return db.timeEntryDao();
	}

	@Singleton
	@Provides
	TaskDao provideTaskDao(LoriDatabase db) {
		return db.taskDao();
	}

	@Singleton
	@Provides
	UserDao provideUserDao(LoriDatabase db) {
		return db.userDao();
	}

	@Singleton
	@Provides
	ProjectDao provideProjectDao(LoriDatabase db) {
		return db.projectDao();
	}

	@Singleton
	@Provides
	TagDao provideTagsDao(LoriDatabase db) {
		return db.tagDao();
	}
}
