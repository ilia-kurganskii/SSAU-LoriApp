package com.ikvant.loriapp.ui.search;

import com.ikvant.loriapp.ui.tasklist.Contract;

import java.util.Date;

/**
 * Created by ikvant.
 */

public interface Contact {
    interface View extends Contract.View {
        void setDateFrom(Date date);

        void setDateTo(Date to);

        void showDateFromDialog(int day, int month, int year);

        void showDateToDialog(int day, int month, int year);
    }

    interface Presenter extends Contract.Presenter {
        void search();

        void searchByDateEnable(boolean enable);

        void setDateFrom(int dayOfMonth, int month, int year);

        void setDateTo(int dayOfMonth, int month, int year);

        void setText(String text);

        void openFromDialog();

        void openToDialog();
    }
}
