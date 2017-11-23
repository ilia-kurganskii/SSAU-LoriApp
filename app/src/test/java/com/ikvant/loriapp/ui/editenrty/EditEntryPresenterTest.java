package com.ikvant.loriapp.ui.editenrty;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import com.ikvant.loriapp.dagger.DaggerTestComponent;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.state.entry.ProjectController;
import com.ikvant.loriapp.state.entry.TagsController;
import com.ikvant.loriapp.state.entry.TaskController;
import com.ikvant.loriapp.state.entry.TimeEntryController;

import javax.inject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditEntryPresenterTest {

	private static final String ENTRY_ID = "1";
	private static final int ENTRY_DURATION = 200;
	private static final Date ENTRY_DATE = new Date();
	private static final String ENTRY_DESCRIPTION = "Description";

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Inject
	TimeEntryController timeEntryController;

	@Inject
	TagsController tagsController;

	@Inject
	TaskController taskController;

	@Inject
	ProjectController projectController;

	@Inject
	TaskDao taskDao;

	@Inject
	ApiService apiService;

	@Inject
	TimeEntryDao timeEntryDao;

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
	public void testShowError() {
		when(timeEntryDao.load(any())).thenReturn(null);

		presenter.onStart();
		verify(view, timeout(5000)).showErrorMessage(any());
	}

	@Test
	public void testShowInfoEntry() {
		when(timeEntryDao.load(any())).thenReturn(getTestTimeEntry());

		presenter.onStart();
		verify(view, timeout(5000)).showLoadingIndicator(false);

		verify(view).setDescription(ENTRY_DESCRIPTION);
		verify(view).setDate(ENTRY_DATE);
		verify(view).setTime(ENTRY_DURATION);
	}

	@Test
	public void testCheckEmptyTask() {
		when(timeEntryDao.load(any())).thenReturn(getTestTimeEntry());

		presenter.onStart();
		verify(view, timeout(5000)).showLoadingIndicator(false);

		presenter.saveEntry();
		verify(view, timeout(5000)).setResultChanged(any());
	}

	@Test
	public void testChangeDescription() {
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