package com.faustgate.sonar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

public class CustomDatePickerDialog extends DatePickerDialog {

    public CustomDatePickerDialog(Context context, OnDateSetListener callBack) {
        super(context,
                callBack,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1);

        getDatePicker().setCalendarViewShown(true);
        getDatePicker().setSpinnersShown(false);

        final Calendar c = Calendar.getInstance();

        getDatePicker().setMinDate(c.getTimeInMillis() - 1000);

        c.add(Calendar.DAY_OF_MONTH, 44);
        getDatePicker().setMaxDate(c.getTimeInMillis());

        c.add(Calendar.DAY_OF_MONTH, -43);
        getDatePicker().updateDate(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1 );
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        super.onDateChanged(view, year, monthOfYear, dayOfMonth);
    }
}