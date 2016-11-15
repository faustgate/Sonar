package com.faustgate.ukrzaliznitsya;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private Calendar myCalendar = Calendar.getInstance();
    private String stationFromId = "";
    private String stationFromName = "";
    private String stationToId = "";
    private String stationToName = "";
    private EditText dateEditText;
    private List<String> trainTypes = Arrays.asList("Обычный", "Интерсити");
    private List<String> ICCarTypes = Arrays.asList("C1", "C2");
    private List<String> usualTrainTypes = Arrays.asList("Плацкарт", "Купе", "Люкс");
    private List<String> operationTypes = Arrays.asList("Покупка", "Бронь");
    private List<String> ticketTypes = Arrays.asList("Обычный", "Детский", "Студенческий");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myCalendar.add(Calendar.DAY_OF_MONTH, 1);

        DelayAutoCompleteTextView stationFromEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationFrom);
        DelayAutoCompleteTextView stationToEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationTo);
        // DatePicker start_date = (DatePicker) findViewById(R.id.datePicker);
        Button search = (Button) findViewById(R.id.button);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        updateLabel();
        Spinner trainTypesSpinner = (Spinner) findViewById(R.id.trainTypeSpinner);
        Spinner carTypeSpinner = (Spinner) findViewById(R.id.carTypeSpinner);
        Spinner buySpinner = (Spinner) findViewById(R.id.buySpinner);
        Spinner ticketTypeSpinner = (Spinner) findViewById(R.id.ticketTypeSpinner);

        trainTypesSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, trainTypes));
        buySpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, operationTypes));
        ticketTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ticketTypes));

        buySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    ticketTypeSpinner.setEnabled(false);
                } else {
                    ticketTypeSpinner.setEnabled(true);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        trainTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    carTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ICCarTypes));
                } else {
                    carTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, usualTrainTypes));
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stationFromId.equals("") && !stationToId.equals("")) {
                    String data = MessageFormat.format("{0} {1} {2} {3} {4}", stationFromName, stationFromId, stationToName,
                            stationToId, getDateString());
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                    String trainData = UZRequests.getInstance().searchForTrains(stationFromId, stationToId, myCalendar.getTime());
                    Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                    intent.putExtra("trains", trainData);
                    startActivity(intent);
                }
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(MainActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        //bookTitle.setThreshold(4);
        StationAutoCompleteAdapter adapter = new StationAutoCompleteAdapter(getApplicationContext());
        stationFromEdit.setAdapter(adapter);
        stationFromEdit.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        stationFromEdit.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
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
        stationToEdit.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
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
        SimpleDateFormat spf = new SimpleDateFormat("MM.dd.yyyy");
        return spf.format(myCalendar.getTime());
    }

    private void updateLabel() {
        String myFormat = "EEEE, MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }
}
