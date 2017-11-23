package com.ikvant.loriapp.ui.editenrty;

import com.ikvant.loriapp.dagger.DaggerTestComponent;
import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.NonEntryException;
import com.ikvant.loriapp.state.entry.ProjectController;
import com.ikvant.loriapp.state.entry.TagsController;
import com.ikvant.loriapp.state.entry.TaskController;
import com.ikvant.loriapp.state.entry.TimeEntryController;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRule;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditEntryPresenterTest {

	public static final String ENTRY_ID = "1";
	public static final int ENTRY_DURATION = 200;
	public static final Date ENTRY_DATE = new Date();
	public static final String ENTRY_DESCRIPTION = "Description";

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Inject
	protected TimeEntryController timeEntryController;

	@Inject
	protected TagsController tagsController;

	@Inject
	protected TaskController taskController;

	@Inject
	protected ProjectController projectController;

	@Inject
	protected TaskDao taskDao;

	@Inject
	protected ApiService apiService;

	@Inject
	protected TimeEntryDao timeEntryDao;

	@Mock
	private Contract.View view;

	@Captor
	private ArgumentCaptor<TimeEntry> entryCaptor;

	private Contract.Presenter presenter;

	@Before
	public void setupPresenter() {
		DaggerTestComponent.create().inject(this);
		EditEntryPresenter presenter = new EditEntryPresenter(timeEntryController, taskController, projectController, tagsController);

		presenter.setView(ENTRY_ID, view);
        this.presenter = presenter;
	}

	@Test
	public void testLoadingIndicator() {
		presenter.onStart();
		verify(view, Mockito.times(1)).showLoadingIndicator(true);
	}

	@Test
	public void testShowError(){
		when(timeEntryDao.load(any())).thenReturn(null);

		presenter.onStart();
		verify(view, timeout(5000)).showErrorMessage(any());
	}

	@Test
	public void testShowInfoEntry(){
		when(timeEntryDao.load(any())).thenReturn(getTestTimeEntry());

		presenter.onStart();
		verify(view, timeout(5000)).showLoadingIndicator(false);

		verify(view).setDescription(ENTRY_DESCRIPTION);
		verify(view).setDate(ENTRY_DATE);
		verify(view).setTime(ENTRY_DURATION);
	}

	@Test
	public void testCheckEmptyTask(){
		when(timeEntryDao.load(any())).thenReturn(getTestTimeEntry());

		presenter.onStart();
		verify(view, timeout(5000)).showLoadingIndicator(false);

		presenter.saveEntry();
		verify(view, timeout(5000)).setResultChanged(any());
	}

	@Test
	public void testChangeDescription(){
		when(timeEntryDao.load(any())).thenReturn(getTestTimeEntry());
		presenter.onStart();

		verify(view, timeout(5000)).showLoadingIndicator(false);

		presenter.setDescription("NEW DESCRIPTION");
		presenter.saveEntry();

		verify(timeEntryDao).save(entryCaptor.capture());
		Assert.assertEquals(entryCaptor.getValue().getDescription(), "NEW DESCRIPTION");
	}

	private TimeEntry getTestTimeEntry() {
		TimeEntry timeEntry = new TimeEntry();
		timeEntry.setId(ENTRY_ID);
		timeEntry.setDescription(ENTRY_DESCRIPTION);
		timeEntry.setDate(ENTRY_DATE);
		timeEntry.setTimeInMinutes(ENTRY_DURATION);
		timeEntry.setTask(new Task("1"));
		return timeEntry;
	}
}