package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.project.ProjectDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class ProjectController {

    private LoriApiService apiService;
    private AppExecutors executors;
    private ProjectDao projectDao;
    private List<Project> cacheTasks;

    private boolean cacheIsDirty = true;

    @Inject
    public ProjectController(LoriApiService apiService, AppExecutors executors, ProjectDao projectDao) {
        this.apiService = apiService;
        this.executors = executors;
        this.projectDao = projectDao;
    }

    public void loadProjects(final LoadDataCallback<List<Project>> callback) {
        if (!cacheIsDirty) {
            callback.onSuccess(cacheTasks);
        }
        executors.background().execute(() -> {
            try {
                List<Project> newProjects = apiService.getProjects();
                projectDao.saveAll(newProjects.toArray(new Project[newProjects.size()]));
                cacheTasks = newProjects;
                cacheIsDirty = false;
                executors.mainThread().execute(() -> callback.onSuccess(newProjects));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    cacheTasks = projectDao.loadAll();
                    cacheIsDirty = false;
                    executors.mainThread().execute(() -> callback.networkUnreachable(cacheTasks));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });
    }
}