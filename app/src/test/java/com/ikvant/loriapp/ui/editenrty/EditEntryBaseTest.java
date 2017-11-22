package com.ikvant.loriapp.ui.editenrty;

import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.project.ProjectDao;
import com.ikvant.loriapp.database.tags.TagDao;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.ProjectController;
import com.ikvant.loriapp.state.entry.TagsController;
import com.ikvant.loriapp.state.entry.TaskController;
import com.ikvant.loriapp.state.entry.TimeEntryController;
import com.ikvant.loriapp.utils.AppExecutors;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditEntryBaseTest {



	@Mock
	protected Contract.View view;

	@Mock
	protected ProjectDao projectDao;

	@Mock
	protected ApiService service;

	@Mock
	protected LoriApiService apiService;

	@Mock
	protected TagDao tagDao;

	@Mock
	protected TimeEntryDao timeEntryDao;

	@Mock
	protected AppExecutors executors;

	@Mock
	protected TimeEntryController timeEntryController;

	@InjectMocks
	protected TaskController taskController;

	@InjectMocks
	protected ProjectController projectController;

	@InjectMocks
	protected TagsController tagsController;

	
}