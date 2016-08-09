package com.faustgate.ukrzaliznitsya;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyActivity extends Activity {
    private Calendar date = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        date.add(Calendar.DAY_OF_MONTH, 1);

        EditText from_txt_in = (EditText) findViewById(R.id.editText);
        EditText to_txt_in = (EditText) findViewById(R.id.editText2);
        DatePicker start_date = (DatePicker) findViewById(R.id.datePicker);
        Button search = (Button) findViewById(R.id.button);

        start_date.init(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.set(year, monthOfYear, dayOfMonth);
                    }
                });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = MessageFormat.format("{0} {1} {2}", from_txt_in.getText().toString(),
                        to_txt_in.getText().toString(),
                        getDateString());
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                UZRequests uzr = new UZRequests();
                String tst = uzr.get_station_id("sadf","dsfg");
            }
        });

    }

    private String getDateString() {
        SimpleDateFormat spf = new SimpleDateFormat("dd.MM.yyyy");
        return spf.format(date.getTime());
    }
}
