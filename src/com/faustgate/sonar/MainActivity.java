package com.faustgate.sonar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private Calendar date = Calendar.getInstance();
    Spinner buySpinner;
    private String stationFromId = "";
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
    private Map<String, String> targetTicketDescription = new HashMap<>();
    private String foundData;
    private String action = "no";
    private String name = "";
    private String surname = "";
    private String notification = "statbar";
    private String ticketType;
    private boolean isBuying;


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

            updateLabel();
        }
    };
    private AdapterView.OnItemSelectedListener ICCarTypesClickListener    = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    targetTicketDescription.put("carType", "");
                    break;
                case 1:
                    targetTicketDescription.put("carType", "С1");
                    break;
                case 2:
                    targetTicketDescription.put("carType", "С2");
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
                    targetTicketDescription.put("carType", "");
                    break;
                case 1:
                    targetTicketDescription.put("carType", "П");
                    break;
                case 2:
                    targetTicketDescription.put("carType", "К");
                    break;
                case 3:
                    targetTicketDescription.put("carType", "Л");
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

        stationFromEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationFrom);
        stationToEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationTo);

        Button search = (Button) findViewById(R.id.button);

        dateEditText = (EditText) findViewById(R.id.dateEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        surnameEditText = (EditText) findViewById(R.id.surnameEditText);

        DatePickerDialog datePickerDialog = new CustomDatePickerDialog(MainActivity.this, dateSetListener);
        updateLabel();

        Spinner trainTypesSpinner = (Spinner) findViewById(R.id.trainTypeSpinner);
        Spinner carTypeSpinner = (Spinner) findViewById(R.id.carTypeSpinner);
        buySpinner = (Spinner) findViewById(R.id.buySpinner);
        Spinner ticketTypeSpinner = (Spinner) findViewById(R.id.ticketTypeSpinner);
        Spinner placesAmountSpinner = (Spinner) findViewById(R.id.ticketAmountPicker);


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
                        targetTicketDescription.put("trainType", "");
                        targetTicketDescription.put("carType", "");
                        carTypeSpinner.setOnItemSelectedListener(usualСarTypesClickListener);
                        carTypeSpinner.setEnabled(false);
                        break;
                    case 1:
                        carTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, usualTrainTypes));
                        targetTicketDescription.put("trainType", "0");
                        carTypeSpinner.setOnItemSelectedListener(usualСarTypesClickListener);
                        carTypeSpinner.setEnabled(true);
                        break;
                    case 2:
                        carTypeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ICCarTypes));
                        targetTicketDescription.put("trainType", "1");
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
                name = nameEditText.getText().toString();
                surname = surnameEditText.getText().toString();
                UZRequests.getInstance().setCurrentStationFromId(stationFromId);
                UZRequests.getInstance().setCurrentStationTillId(stationToId);
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

        StationAutoCompleteAdapter adapter = new StationAutoCompleteAdapter(getApplicationContext());
        StationAutoCompleteAdapter adapter2 = new StationAutoCompleteAdapter(getApplicationContext());

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
        stationToEdit.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar2));
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
        stationFromId = station.get("station_id");
        stationFromEdit.setText(stationFromName);
        stationFromEdit.setSelection(stationFromEdit.getText().length());
        isDepStationCorrect = true;
    }

    private void initStationTo(HashMap<String, String> station) {
        stationToId = station.get("station_id");
        stationToName = station.get("title");
        stationToEdit.setText(stationToName);
        stationToEdit.setSelection(stationToEdit.getText().length());
        isArrStationCorrect = true;
    }

    private void start() {

        if (validate()) {
            String data = MessageFormat.format("{0} {1} {2} {3} {4}",
                    stationFromName,
                    stationFromId,
                    stationToName,
                    stationToId,
                    getDateString());
     //       Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
            String trainData = UZRequests.getInstance().searchForTrains(stationFromId,
                    stationToId,
                    date.getTime());
            try {
                JSONObject resp = new JSONObject(trainData);
                JSONArray corrTrains = new JSONArray();
                JSONArray corrPlacesTrains = new JSONArray();
                if (resp.getString("error").equals("true")) {
                    if (resp.has("value")) {
                        if (resp.getString("value").contains("No places")) {
                            showTrainSearchOptionsMessage();
                        }
                    }
                    return;
                }
                if (!resp.has("value")) {
                    return;
                }
                JSONArray trains = resp.getJSONArray("value");
                if (trains.length() == 0) {
                    showTrainSearchOptionsMessage();
                    return;
                }
                if (targetTicketDescription.get("trainType").equals("")) {
                    corrTrains = trains;
                } else {
                    for (int i = 0; i < trains.length(); i++) {
                        JSONObject train = trains.getJSONObject(i);
                        if (targetTicketDescription.get("trainType").contains(train.getString("category")))
                            corrTrains.put(train);
                    }
                }

                if (corrTrains.length() == 0) {
                    foundData = trains.toString();
                    showTrainOptionsMessage();
                }


                if (targetTicketDescription.get("carType").equals("")) {
                    corrPlacesTrains = corrTrains;
                } else {
                    for (int j = 0; j < corrTrains.length(); j++) {
                        JSONObject train = corrTrains.getJSONObject(j);
                        JSONArray types = train.getJSONArray("types");
                        for (int k = 0; k < types.length(); k++) {
                            String typeId = types.getJSONObject(k).getString("letter");
                            if (targetTicketDescription.get("carType").contains(typeId))
                                corrPlacesTrains.put(train);
                        }
                    }

                    if (corrPlacesTrains.length() == 0) {
                        foundData = corrTrains.toString();
                        showPlacesOptionsMessage();
                    }

                }
                if (corrPlacesTrains.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                    intent.putExtra("trains", corrPlacesTrains.toString());
                    intent.putExtra("name", name);
                    intent.putExtra("surname", surname);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


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
        if (stationFromId.equals(stationToId)) {
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
        new AlertDialog.Builder(MainActivity.this).setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    private void showTrainOptionsMessage() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.no_trains_head))
                .setMessage(getString(R.string.no_trains_body))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.no_trains_find_nearest),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                findNearestTrainsWithPlaces();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(getString(R.string.no_trains_show_available), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                        intent.putExtra("trains", foundData);
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNeutralButton(getString(R.string.modify_search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);
    }

    private void showPlacesOptionsMessage() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.no_places_head))
                .setMessage(getString(R.string.no_places_body))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.no_places_find_nearest),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                findNearestTrainsWithPlaces();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(getString(R.string.no_places_show_available), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                        intent.putExtra("trains", foundData);
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNeutralButton(getString(R.string.modify_search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);
    }

    private void showTrainSearchOptionsMessage() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.no_avail_trains_head))
                .setMessage(getString(R.string.no_avail_trains_body))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.no_avail_trains_find_nearest),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                findNearestTrainsWithPlaces();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(getString(R.string.no_avail_trains_enable_monitor),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                activateMonitor();
                                dialog.cancel();
                            }
                        })
                .setNeutralButton(getString(R.string.modify_search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLines(4);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);
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

    private void findNearestTrainsWithPlaces() {
        Calendar locCal = Calendar.getInstance();
        locCal.set(Calendar.YEAR, date.get(Calendar.YEAR));
        locCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
        locCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

        for (int day = 0; day < 45; day++) {
            locCal.add(Calendar.DAY_OF_MONTH, 1);
            String trainData = UZRequests.getInstance().searchForTrains(stationFromId,
                    stationToId,
                    locCal.getTime());
            try {
                JSONObject resp = new JSONObject(trainData);
                JSONArray corrTrains = new JSONArray();
                JSONArray corrPlacesTrains = new JSONArray();
                if (resp.getString("error").equals("true") || (!resp.has("value"))) {
                    continue;
                }
                JSONArray trains = resp.getJSONArray("value");
                if (trains.length() == 0) {
                    continue;
                }
                if (targetTicketDescription.get("trainType").equals("")) {
                    corrTrains = trains;
                } else {
                    for (int i = 0; i < trains.length(); i++) {
                        JSONObject train = trains.getJSONObject(i);
                        if (targetTicketDescription.get("trainType").contains(train.getString("category")))
                            corrTrains.put(train);
                    }
                }

                if (corrTrains.length() == 0) {
                    continue;
                }

                if (targetTicketDescription.get("carType").equals("")) {
                    corrPlacesTrains = corrTrains;
                } else {
                    for (int j = 0; j < corrTrains.length(); j++) {
                        JSONObject train = corrTrains.getJSONObject(j);
                        JSONArray types = train.getJSONArray("types");
                        for (int k = 0; k < types.length(); k++) {
                            String typeId = types.getJSONObject(k).getString("letter");
                            if (targetTicketDescription.get("carType").contains(typeId))
                                corrPlacesTrains.put(train);
                        }
                    }
                }

                if (corrPlacesTrains.length() == 0) {
                    continue;
                }

                Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                intent.putExtra("trains", corrPlacesTrains.toString());
                startActivity(intent);
                break;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void activateMonitor() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle(getString(R.string.monitor_settings_head));

        Intent intent = new Intent(this, TicketFinderService.class);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.US);

        dialog.setContentView(R.layout.monitor_settings_layout);
        RadioButton rbNoAct = (RadioButton) dialog.findViewById(R.id.rbNoAction);
        RadioButton rbBlock1 = (RadioButton) dialog.findViewById(R.id.rbBlockOnce);
        RadioButton rbBlockCont = (RadioButton) dialog.findViewById(R.id.rbBlockContinuous);
        RadioButton rbNotifStatBar = (RadioButton) dialog.findViewById(R.id.rbStatusBarNotification);
        RadioButton rbNotifShort = (RadioButton) dialog.findViewById(R.id.rbShortNotification);
        RadioButton rbNotifLong = (RadioButton) dialog.findViewById(R.id.rbLongNotification);
        Button start = (Button) dialog.findViewById(R.id.btnStartMon);

        rbNoAct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    action = "no";
            }
        });
        rbBlock1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    action = "1";
            }
        });
        rbBlockCont.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    action = "cont";
            }
        });

        rbNotifStatBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    notification = "statbar";
            }
        });
        rbNotifShort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    notification = "short";
            }
        });
        rbNotifLong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    notification = "long";
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("stationFromId", stationFromId);
                intent.putExtra("stationToId", stationToId);
                intent.putExtra("date", sdf.format(date.getTime()));
                intent.putExtra("action", action);
                intent.putExtra("notification", notification);
                intent.putExtra("ticketType", ticketType);
                intent.putExtra("buy", isBuying);
                intent.putExtra("name", name);
                intent.putExtra("surname", surname);
                startService(intent);
                dialog.hide();
            }
        });

        dialog.show();


    }

}
