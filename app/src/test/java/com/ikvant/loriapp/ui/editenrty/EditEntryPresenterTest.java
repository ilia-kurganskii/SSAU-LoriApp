package com.ikvant.loriapp.ui.editenrty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

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

	private static final List<Task> taskList = new ArrayList<Task>(2){{
		add(new Task("1", "Task1"));
		add(new Task("2", "Task2"));
		add(new Task("3", "Task3"));
	}};

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

	@Captor
	private ArgumentCaptor<TimeEntry> entryCaptor;

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

		mockReturnTestTasks();
		mockReturnTestEntry();

		verify(view).setDescription(ENTRY_DESCRIPTION);
		verify(view).setDate(ENTRY_DATE);
		verify(view).setTime(ENTRY_DURATION);
	}

	@Test
	public void testCheckEmptyTask(){
		presenter.onStart();

		verify(taskController).loadTasks(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().onSuccess(Collections.emptyList());

		mockReturnTestEntry();

		presenter.saveEntry();

		verify(view).showErrorMessage(any());
	}

	@Test
	public void testChangeDescription(){
		presenter.onStart();
		mockReturnTestTasks();
		mockReturnTestEntry();

		presenter.setDescription("NEW DESCRIPTION");
		presenter.saveEntry();

		verify(entryController).updateTimeEntry(entryCaptor.capture(), any());
		Assert.assertEquals(entryCaptor.getValue().getDescription(), "NEW DESCRIPTION");
	}

	private TimeEntry getTestTimeEntry() {
		TimeEntry timeEntry = new TimeEntry();
		timeEntry.setId(ENTRY_ID);
		timeEntry.setDescription(ENTRY_DESCRIPTION);
		timeEntry.setDate(ENTRY_DATE);
		timeEntry.setTimeInMinutes(ENTRY_DURATION);
		return timeEntry;
	}

	private void mockReturnTestTasks(){
		verify(taskController).loadTasks(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().onSuccess(taskList);
	}

	private void mockReturnTestEntry(){
		verify(entryController).loadTimeEntry(eq(ENTRY_ID), entryCallbackCaptor.capture());
		entryCallbackCaptor.getValue().onSuccess(getTestTimeEntry());
	}
}