package com.ikvant.loriapp.ui.editenrty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.state.entry.LoadDataCallback;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditEntry_PresenterTest extends EditEntryBaseTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	public static final String ENTRY_ID = "1";
	public static final int ENTRY_DURATION = 200;
	public static final Date ENTRY_DATE = new Date();
	public static final String ENTRY_DESCRIPTION = "Description";

	protected static final List<Task> taskList = new ArrayList<Task>(2){{
		add(new Task("1", "Task1"));
		add(new Task("2", "Task2"));
		add(new Task("3", "Task3"));
	}};
	public static final String TASK_NAME = "Task name";
	public static final String TASK_ID = "taskId";
	public static final String PROJECT_ID = "projectId";
	public static final String PROJECT_NAME = "Project1";


	@Captor
	private ArgumentCaptor<LoadDataCallback<List<Task>>> tasksCallbackCaptor;

	@Captor
	private ArgumentCaptor<LoadDataCallback<List<Project>>> projectCallbackCaptor;

	@Captor
	private ArgumentCaptor<LoadDataCallback<TimeEntry>> entryCallbackCaptor;

	@Captor
	private ArgumentCaptor<TimeEntry> entryCaptor;

	private EditEntryPresenter presenter;

	@Before
	public void setupPresenter() {
		when(executors.background()).thenReturn(Executors.newFixedThreadPool(3));
		when(executors.mainThread()).thenReturn(Executors.newSingleThreadExecutor());

		when(projectDao.loadAll()).thenReturn(Collections.singletonList(new Project("id")));

		presenter = new EditEntryPresenter(timeEntryController, taskController, projectController, tagsController);
		presenter.setView(ENTRY_ID, view);
	}

	@Test
	public void testLoadingIndicator() {
		presenter.onStart();
		verify(view, Mockito.times(1)).showLoadingIndicator(true);
	}

	@Test
	public void testShowError(){
		presenter.onStart();

		verify(taskController).load(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().onFailure(new NetworkApiException());

		verify(view).showErrorMessage(any());
	}

	@Test
	public void testShowOfflineMessage(){
		presenter.onStart();

		verify(taskController).load(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().networkUnreachable(Collections.emptyList());

		verify(view).showOfflineMessage();
	}

	@Test
	public void testShowInfoEntry(){
		presenter.onStart();

		mockReturnTestEntry();

		verify(view).setDescription(ENTRY_DESCRIPTION);
		verify(view).setDate(ENTRY_DATE);
		verify(view).setTime(ENTRY_DURATION);
	}

	@Test
	public void testCheckEmptyTask(){
		presenter.onStart();

		verify(taskController).load(tasksCallbackCaptor.capture());
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

		verify(timeEntryController).updateTimeEntry(entryCaptor.capture(), any());
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

	private Task getTestTask() {
		Task task = new Task(TASK_ID, TASK_NAME);
		task.setProject(getTestProject());
		return task;
	}

	private Project getTestProject() {
		Project project = new Project(PROJECT_ID);
		project.setName(PROJECT_NAME);
		return project;
	}

	private void mockReturnTestProject(){
		verify(projectController).load(projectCallbackCaptor.capture());
	}

	private void mockReturnTestTasks(){
		verify(taskController).load(tasksCallbackCaptor.capture());
		tasksCallbackCaptor.getValue().onSuccess(taskList);
	}

	private void mockReturnTestEntry(){
		verify(timeEntryController).loadTimeEntry(eq(ENTRY_ID), entryCallbackCaptor.capture());
		entryCallbackCaptor.getValue().onSuccess(getTestTimeEntry());
	}
}
