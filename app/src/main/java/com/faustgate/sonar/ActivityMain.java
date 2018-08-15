package com.faustgate.sonar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityMain extends Activity {
    private Calendar date = Calendar.getInstance();
    Spinner buySpinner, ticketTypeSpinner, placesAmountSpinner, carTypeSpinner, trainTypesSpinner;
    private String stationFromName = "";
    private String stationToId = "";
    private String stationToName = "";
    private EditText dateEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private List<String> trainTypes, ICCarTypes, usualTrainTypes, operationTypes, ticketTypes;
    private List<String> placesAmount = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
    private boolean isDepStationCorrect = false;
    private boolean isArrStationCorrect = false;
    private DelayAutoCompleteTextView stationFromEdit, stationToEdit;
    private String ticketType;
    private boolean isBuying;
    private OrderDescription currentTicket = OrderDescription.getInstance();


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (date.getTimeInMillis() - System.currentTimeMillis() >= 86400000) {
                buySpinner.setEnabled(true);
            } else {
                buySpinner.setSelection(0);
                buySpinner.setEnabled(false);
            }

            OrderDescription.getInstance().setDepartureDate(date.getTime());
            updateLabel();
        }
    };
    private AdapterView.OnItemSelectedListener ICCarTypesClickListener    = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    currentTicket.setWagonType("all");
                    break;
                case 1:
                    currentTicket.setWagonType("С1");
                    break;
                case 2:
                    currentTicket.setWagonType("С2");
                    break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private AdapterView.OnItemSelectedListener usualСarTypesClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    currentTicket.setWagonType("all");
                    break;
                case 1:
                    currentTicket.setWagonType("П");
                    break;
                case 2:
                    currentTicket.setWagonType("К");
                    break;
                case 3:
                    currentTicket.setWagonType("Л");
                    break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        LogSystem.setDebugLogging(isDebuggable);
        trainTypes = initTrainTypes();
        ICCarTypes = initICCarTypes();
        usualTrainTypes = initUsualCarTypes();
        operationTypes = Arrays.asList(getString(R.string.operation_buying),
                getString(R.string.operation_booking));
        ticketTypes = Arrays.asList(getString(R.string.ticket_usual),
                getString(R.string.ticket_child),
                getString(R.string.ticket_stud));

        stationFromEdit = findViewById(R.id.stationFrom);
        stationToEdit   = findViewById(R.id.stationTo);

        Button search = findViewById(R.id.button);

        dateEditText    = findViewById(R.id.dateEditText);
        nameEditText    = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);

        DatePickerDialog datePickerDialog = new CustomDatePickerDialog(ActivityMain.this, dateSetListener);
        updateLabel();

        buySpinner          = findViewById(R.id.buySpinner);
        carTypeSpinner      = findViewById(R.id.carTypeSpinner);
        trainTypesSpinner   = findViewById(R.id.trainTypeSpinner);
        ticketTypeSpinner   = findViewById(R.id.ticketTypeSpinner);
        placesAmountSpinner = findViewById(R.id.ticketAmountPicker);


        buySpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, operationTypes));
        trainTypesSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, trainTypes));
        ticketTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ticketTypes));
        placesAmountSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, placesAmount));

        buySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isBuying = position == 0;
                ticketTypeSpinner.setEnabled(position == 0);
                if (position != 0) {
                    ticketTypeSpinner.setSelection(0);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        trainTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        carTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, usualTrainTypes));
                        currentTicket.setTrainType("all");
                        currentTicket.setTicketType("all");
                        carTypeSpinner.setOnItemSelectedListener(usualСarTypesClickListener);
                        carTypeSpinner.setEnabled(false);
                        break;
                    case 1:
                        carTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, usualTrainTypes));
                        currentTicket.setTrainType("usual");
                        carTypeSpinner.setOnItemSelectedListener(usualСarTypesClickListener);
                        carTypeSpinner.setEnabled(true);
                        break;
                    case 2:
                        carTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ICCarTypes));
                        currentTicket.setTrainType("IC");
                        carTypeSpinner.setOnItemSelectedListener(ICCarTypesClickListener);
                        carTypeSpinner.setEnabled(true);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ticketTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ticketType = "";
                        break;
                    case 1:
                        ticketType = "child";
                        break;
                    case 2:
                        ticketType = "stud";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buySpinner.setEnabled(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTicket.setName(nameEditText.getText().toString());
                currentTicket.setSurname(surnameEditText.getText().toString());
                start();
            }
        });

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show();
                }
            }
        });
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        AdapterStationAutoComplete adapter = new AdapterStationAutoComplete(getApplicationContext());
        AdapterStationAutoComplete adapter2 = new AdapterStationAutoComplete(getApplicationContext());

        stationFromEdit.setAdapter(adapter);
        stationFromEdit.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        stationFromEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> station = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                initStationFrom(station);
                adapter.clear();
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
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong departure station", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    adapter.clear();
                    stationFromEdit.setSelection(stationFromEdit.getText().length());
                }
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

        stationToEdit.setAdapter(adapter2);
        stationToEdit.setLoadingIndicator(findViewById(R.id.progress_bar2));
        stationToEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String, String> station = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                initStationTo(station);
                stationToEdit.setSelection(stationToEdit.getText().length());
                adapter2.clear();
            }
        });
        stationToEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isArrStationCorrect) {
                        if (adapter2.getCount() > 0) {
                            HashMap<String, String> station = adapter2.getItem(0);
                            initStationTo(station);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong arrival station", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    adapter2.clear();
                    stationToEdit.setSelection(stationToEdit.getText().length());
                }
            }
        });
        stationToEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (adapter2.getCount() > 0) {
                    HashMap<String, String> station = adapter2.getItem(0);
                    initStationTo(station);
                    adapter2.clear();
                }
                if (isDepStationCorrect && isArrStationCorrect) {
                    dateEditText.requestFocus();
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

    private String getDateString() {
        SimpleDateFormat spf = new SimpleDateFormat("MM.dd.yyyy");
        return spf.format(date.getTime());
    }

    private void initStationFrom(HashMap<String, String> station) {
        stationFromName = station.get("title");
        currentTicket.setStationFromId(station.get("station_id"));
        stationFromEdit.setText(stationFromName);
        stationFromEdit.setSelection(stationFromEdit.getText().length());
        isDepStationCorrect = true;
    }

    private void initStationTo(HashMap<String, String> station) {
        stationToName = station.get("title");
        currentTicket.setStationToId(station.get("station_id"));
        stationToId = station.get("station_id");
        stationToEdit.setText(stationToName);
        stationToEdit.setSelection(stationToEdit.getText().length());
        isArrStationCorrect = true;
    }

    private void start() {

        if (validate()) {
            String data = MessageFormat.format("{0} {1} {2} {3} {4}",
                    stationFromName,
                    OrderDescription.getInstance().getStationFromId(),
                    stationToName,
                    OrderDescription.getInstance().getStationToId(),
                    OrderDescription.getInstance().getDepartureDate());
            //       Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ActivityMain.this, ActivityTrainList.class);
            startActivity(intent);
        }

    }

    private void updateLabel() {
        String myFormat = getString(R.string.user_date_format);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, getResources().getConfiguration().locale);
        if (dateEditText.length() == 0) {
            date.add(Calendar.DAY_OF_MONTH, 1);
        }
        String dateStr = sdf.format(date.getTime());
        dateStr = dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1);
        dateEditText.setText(dateStr);
    }

    private boolean validate() {
        if (!isDepStationCorrect) {
            showErrorMessage(getString(R.string.err_wrong_dep_station_head), getString(R.string.err_wrong_dep_station_body));
            stationFromEdit.requestFocus();
            return false;
        }
        if (!isArrStationCorrect) {
            showErrorMessage(getString(R.string.err_wrong_arr_station_head),
                    getString(R.string.err_wrong_arr_station_body));
            stationToEdit.requestFocus();
            return false;
        }
        if (currentTicket.getStationFromId().equals(stationToId)) {
            showErrorMessage(getString(R.string.err_same_stations_head),
                    getString(R.string.err_same_stations_head));
            stationFromEdit.requestFocus();
            return false;
        }
        if (nameEditText.length() == 0) {
            showErrorMessage(getString(R.string.err_fill_name_head),
                    getString(R.string.err_fill_name_body));
            nameEditText.requestFocus();
            return false;
        }
        if (surnameEditText.length() == 0) {
            showErrorMessage(getString(R.string.err_fill_surname_head),
                    getString(R.string.err_fill_surname_body));
            surnameEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void showErrorMessage(String title, String message) {
        new AlertDialog.Builder(ActivityMain.this).setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    private ArrayList<String> initTrainTypes() {
        ArrayList<String> trainTypesLoc = new ArrayList<>();
        trainTypesLoc.add(getString(R.string.all));
        trainTypesLoc.add(getString(R.string.train_usual));
        trainTypesLoc.add(getString(R.string.train_ic));
        return trainTypesLoc;
    }

    private ArrayList<String> initICCarTypes() {
        ArrayList<String> ICCarTypes = new ArrayList<>();
        ICCarTypes.add(getString(R.string.all));
        ICCarTypes.add(getString(R.string.ic_class1));
        ICCarTypes.add(getString(R.string.ic_class2));
        return ICCarTypes;
    }

    private ArrayList<String> initUsualCarTypes() {
        ArrayList<String> usualCarTypes = new ArrayList<>();
        usualCarTypes.add(getString(R.string.all));
        usualCarTypes.add(getString(R.string.train_class3));
        usualCarTypes.add(getString(R.string.train_class2));
        usualCarTypes.add(getString(R.string.train_class1));
        return usualCarTypes;
    }



}
