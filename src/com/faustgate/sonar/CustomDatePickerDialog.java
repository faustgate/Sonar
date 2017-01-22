package com.faustgate.sonar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by werwolf on 11/27/16.
 */
public class CustomDatePickerDialog extends DatePickerDialog {

    private int maxYear, minYear;
    private int maxMonth, minMonth;
    private int maxDay, minDay;

    public CustomDatePickerDialog(Context context, OnDateSetListener callBack) {
        super(context,
                callBack,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1);

        final Calendar c = Calendar.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getDatePicker().setMinDate(c.getTimeInMillis() - 1000);
        } else {
            minYear = c.get(Calendar.YEAR);
            minMonth = c.get(Calendar.MONTH);
            minDay = c.get(Calendar.DAY_OF_MONTH);
        }

        c.add(Calendar.DAY_OF_MONTH, 44);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getDatePicker().setMaxDate(c.getTimeInMillis());
        } else {
            maxYear = c.get(Calendar.YEAR);
            maxMonth = c.get(Calendar.MONTH);
            maxDay = c.get(Calendar.DAY_OF_MONTH);
        }
        c.add(Calendar.DAY_OF_MONTH, -41);


    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.onDateChanged(view, year, monthOfYear, dayOfMonth);
        } else {
            if (year > maxYear)
                view.updateDate(maxYear, maxMonth, maxDay);
            if (year < minYear)
                view.updateDate(minYear, minMonth, minDay);

            if (monthOfYear > maxMonth && year == maxYear)
                view.updateDate(maxYear, maxMonth, maxDay);
            if (monthOfYear < minMonth && year == minYear)
                view.updateDate(minYear, minMonth, minDay);

            if (dayOfMonth > maxDay && year == maxYear && monthOfYear == maxMonth)
                view.updateDate(maxYear, maxMonth, maxDay);
            if (dayOfMonth < minDay && year == minYear && monthOfYear == minMonth)
                view.updateDate(minYear, minMonth, minDay);
        }
    }
}