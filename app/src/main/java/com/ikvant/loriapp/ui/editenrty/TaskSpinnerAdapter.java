package com.ikvant.loriapp.ui.editenrty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.task.Task;

import java.util.List;
import java.util.Map;

/**
 * Created by ikvant.
 */

public class TaskSpinnerAdapter extends ArrayAdapter<Task>{

    public TaskSpinnerAdapter(@NonNull Context context) {
        super(context, R.layout.i_spinner_task, R.id.task_name);
    }
}
