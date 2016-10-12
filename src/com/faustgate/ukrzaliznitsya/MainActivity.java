package com.faustgate.ukrzaliznitsya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends Activity {
    private Calendar date = Calendar.getInstance();
    private String stationFromId = "";
    private String stationFromName = "";
    private String stationToId = "";
    private String stationToName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        date.add(Calendar.DAY_OF_MONTH, 1);

        DelayAutoCompleteTextView stationFromEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationFrom);
        DelayAutoCompleteTextView stationToEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationTo);
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
                //   if ((!stationFromId.equals("") && !stationToId.equals(""))) {
                String data = MessageFormat.format("{0} {1} {2} {3} {4}", stationFromName, stationFromId, stationToName,
                        stationToId, getDateString());
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                UZRequests uzr = new UZRequests();
                String trainData = uzr.searchForTickets(stationFromId, stationToId, getDateString());

                Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                intent.putExtra("trains", trainData);
                startActivity(intent);

            }
        });


        //bookTitle.setThreshold(4);
        StationAutoCompleteAdapter adapter = new StationAutoCompleteAdapter(getApplicationContext());
        stationFromEdit.setAdapter(adapter);
        stationFromEdit.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        stationFromEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> station = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                stationFromName = station.get("title");
                stationFromId = station.get("station_id");
                stationFromEdit.setText(stationFromName);
            }
        });

        stationToEdit.setAdapter(adapter);
        stationToEdit.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        stationToEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> station = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                stationToName = station.get("title");
                stationToId = station.get("station_id");
                stationToEdit.setText(stationToName);
            }
        });


    }

    private String getDateString() {
        SimpleDateFormat spf = new SimpleDateFormat("dd.MM.yyyy");
        return spf.format(date.getTime());
    }
}
