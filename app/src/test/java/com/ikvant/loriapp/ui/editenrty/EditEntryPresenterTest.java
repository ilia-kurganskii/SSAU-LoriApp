package com.ikvant.loriapp.ui.editenrty;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.TaskController;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EditEntryPresenterTest {

	public static final String ENTRY_ID = "1";
	public static final int ENTRY_DURATION = 200;
	public static final Date ENTRY_DATE = new Date();
	public static final String ENTRY_DESCRIPTION = "Description";

	@Mock
	private Contract.View view;

	@Mock
	private EntryController entryController;

	@Mock
	private TaskController taskController;

	@Captor
	private ArgumentCaptor<LoadDataCallback<List<Task>>> tasksCallbackCaptor;

	@Captor
	private ArgumentCaptor<LoadDataCallback<TimeEntry>> entryCallbackCaptor;


	private Contract.Presenter presenter;

	@Before
	public void setupPresenter() {
		MockitoAnnotations.initMocks(this);
		EditEntryPresenter presenter = new EditEntryPresenter(entryController, taskController);
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
		presenter.onStart();

		verify(taskController).loadTasks(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().onFailure(new NetworkApiException());

		verify(view).showErrorMessage(any());
	}

	@Test
	public void testShowOfflineMessage(){
		presenter.onStart();

		verify(taskController).loadTasks(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().networkUnreachable(Collections.emptyList());

		verify(view).showOfflineMessage();
	}

	@Test
	public void testShowInfoEntry(){
		presenter.onStart();
		verify(taskController).loadTasks(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().onSuccess(Collections.emptyList());

		verify(entryController).loadTimeEntry(eq(ENTRY_ID), entryCallbackCaptor.capture());
		entryCallbackCaptor.getValue().onSuccess(getTestTimeEntry());

		verify(view).setDescription(ENTRY_DESCRIPTION);
		verify(view).setDate(ENTRY_DATE);
		verify(view).setTime(ENTRY_DURATION);
	}

	@Test
	public void testCheckEmptyTask(){
		presenter.onStart();
		verify(taskController).loadTasks(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().onSuccess(Collections.emptyList());

		verify(entryController).loadTimeEntry(eq(ENTRY_ID), entryCallbackCaptor.capture());
		entryCallbackCaptor.getValue().onSuccess(getTestTimeEntry());
		presenter.saveEntry();

		verify(view).showErrorMessage(any());
	}

	private TimeEntry getTestTimeEntry() {
		TimeEntry timeEntry = new TimeEntry();
		timeEntry.setId(ENTRY_ID);
		timeEntry.setDescription(ENTRY_DESCRIPTION);
		timeEntry.setDate(ENTRY_DATE);
		timeEntry.setTimeInMinutes(ENTRY_DURATION);
		return timeEntry;
	}
}