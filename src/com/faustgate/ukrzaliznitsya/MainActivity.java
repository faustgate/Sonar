package com.faustgate.ukrzaliznitsya;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private Calendar date = Calendar.getInstance();
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
    private boolean isDepStationCorrect = false;
    private boolean isArrStationCorrect = false;
    private DelayAutoCompleteTextView stationFromEdit;
    private DelayAutoCompleteTextView stationToEdit;
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        date.add(Calendar.DAY_OF_MONTH, 1);

        stationFromEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationFrom);
        stationToEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationTo);
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
                    String trainData = UZRequests.getInstance().searchForTrains(stationFromId, stationToId, date.getTime());
                    Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                    intent.putExtra("trains", trainData);
                    startActivity(intent);
                }
            }
        });

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateDialog();
                }
            }
        });
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
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
        stationFromEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isDepStationCorrect) {
                        if (adapter.getCount() > 0) {
                            HashMap<String, String> station = adapter.getItem(0);
                            initStationFrom(station);
                            adapter.clear();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong departure station", Toast.LENGTH_LONG).show();
                        }
                    }
                    adapter.clear();
                } else
                    stationFromEdit.setSelection(stationFromEdit.getText().length());
            }
        });
        stationFromEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                stationToEdit.requestFocus();
                return true;
            }
        });
        stationFromEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isDepStationCorrect = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        stationToEdit.setAdapter(adapter);
        stationToEdit.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar2));
        stationToEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> station = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                initStationTo(station);
                adapter.clear();
                stationToEdit.setSelection(stationToEdit.getText().length());
            }
        });
        stationToEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isArrStationCorrect) {
                        if (adapter.getCount() > 0) {
                            HashMap<String, String> station = adapter.getItem(0);
                            initStationTo(station);
                            adapter.clear();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong arrival station", Toast.LENGTH_LONG).show();
                        }
                    }
                    adapter.clear();
                } else
                    stationToEdit.setSelection(stationToEdit.getText().length());
            }
        });
        stationToEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!isArrStationCorrect) {
                    if (adapter.getCount() > 0) {
                        HashMap<String, String> station = adapter.getItem(0);
                        initStationTo(station);
                        adapter.clear();
                        isArrStationCorrect = true;
                    }
                }
                if (isDepStationCorrect) {
                    start();
                } else {
                    stationFromEdit.requestFocus();
                }
                return true;
            }
        });
        stationToEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isArrStationCorrect = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showDateDialog() {
        new DatePickerDialog(MainActivity.this, dateSetListener, date
                .get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void initStationFrom(HashMap<String, String> station) {
        stationFromName = station.get("title");
        stationFromId = station.get("station_id");
        stationFromEdit.setText(stationFromName);
        isDepStationCorrect = true;
    }

    private void initStationTo(HashMap<String, String> station) {
        stationToName = station.get("title");
        stationToId = station.get("station_id");
        stationToEdit.setText(stationToName);
        isArrStationCorrect = true;
    }

    private String getDateString() {
        SimpleDateFormat spf = new SimpleDateFormat("MM.dd.yyyy");
        return spf.format(date.getTime());
    }

    private void start() {
        if ((!isDepStationCorrect) || stationFromEdit.getText().length() == 0) {
            new AlertDialog.Builder(MainActivity.this).setTitle("Wrong departure station!")
                    .setMessage("Departure station wrong or empty")
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
            stationFromEdit.requestFocus();
            return;
        }
        if (!isArrStationCorrect || stationToEdit.getText().length() == 0) {
            new AlertDialog.Builder(MainActivity.this).setTitle("Wrong arrival station!")
                    .setMessage("Arrival station wrong or empty")
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
            stationToEdit.requestFocus();
            return;
        }
        if (stationFromId.equals(stationToId)) {
            new AlertDialog.Builder(MainActivity.this).setTitle("Same stations")
                    .setMessage("Arrival and departure stations are the same")
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
            stationFromEdit.requestFocus();
            return;
        }

        String data = MessageFormat.format("{0} {1} {2} {3} {4}", stationFromName, stationFromId, stationToName,
                stationToId, getDateString());
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
        String trainData = UZRequests.getInstance().searchForTrains(stationFromId, stationToId, date.getTime());

        Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
        intent.putExtra("trains", trainData);
        startActivity(intent);
    }


    private void updateLabel() {
        String myFormat = "EEEE, MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(date.getTime()));
    }
}
