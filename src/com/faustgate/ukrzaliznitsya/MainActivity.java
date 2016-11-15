package com.faustgate.ukrzaliznitsya;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends Activity {
    private Calendar date = Calendar.getInstance();
    private String stationFromId = "";
    private String stationFromName = "";
    private String stationToId = "";
    private String stationToName = "";
    private boolean isDepStationCorrect = false;
    private boolean isArrStationCorrect = false;
    private DelayAutoCompleteTextView stationFromEdit;
    private DelayAutoCompleteTextView stationToEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        date.add(Calendar.DAY_OF_MONTH, 1);

        stationFromEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationFrom);
        stationToEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationTo);
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
                start();
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
                initStationFrom(station);
                adapter.clear();
                stationFromEdit.setSelection(stationFromEdit.getText().length());
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

}
