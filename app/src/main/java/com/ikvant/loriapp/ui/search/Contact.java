package com.ikvant.loriapp.ui.search;

import com.ikvant.loriapp.ui.tasklist.Contract;

import java.util.Date;

/**
 * Created by ikvant.
 */

public class Contact {
    interface Presenter extends Contract.Presenter {
        void searchByText(String text);

        void searchByDate(Date from, Date to);
    }
}
