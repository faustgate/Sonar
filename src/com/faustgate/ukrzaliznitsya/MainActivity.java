package com.faustgate.ukrzaliznitsya;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    private String stationFromId = "";
    private String stationFromName = "";
    private String stationToId = "";
    private String stationToName = "";
    private EditText dateEditText;
    private List<String> trainTypes = initTrainTypes();
    private List<String> ICCarTypes = initICCarTypes();
    private List<String> usualTrainTypes = initUsualCarTypes();
    private List<String> operationTypes = Arrays.asList("Покупка", "Бронь");
    private List<String> ticketTypes = Arrays.asList("Обычный", "Детский", "Студенческий");
    private boolean isDepStationCorrect = false;
    private boolean isArrStationCorrect = false;
    private DelayAutoCompleteTextView stationFromEdit, stationToEdit;
    private Map<String, String> targetTicketDescription = new HashMap<>();
    private String foundData;


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

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
    private AdapterView.OnItemSelectedListener ICCarTypesClickListener = new AdapterView.OnItemSelectedListener() {
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

        stationFromEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationFrom);
        stationToEdit = (DelayAutoCompleteTextView) findViewById(R.id.stationTo);
        // DatePicker start_date = (DatePicker) findViewById(R.id.datePicker);
        Button search = (Button) findViewById(R.id.button);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        DatePickerDialog datePickerDialog = initDateDialog();
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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                stationFromEdit.setText(stationFromName);
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
                            stationFromEdit.setSelection(stationFromEdit.getText().length());
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
                            stationToEdit.setSelection(stationToEdit.getText().length());
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
                if (!isArrStationCorrect) {
                    if (adapter2.getCount() > 0) {
                        HashMap<String, String> station = adapter.getItem(0);
                        initStationTo(station);
                        adapter2.clear();
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

    private DatePickerDialog initDateDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                dateSetListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(date.getTimeInMillis());
        date.add(Calendar.DAY_OF_MONTH, 1);

        datePickerDialog.getDatePicker().init(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH), null);

        date.add(Calendar.DAY_OF_MONTH, 43);
        datePickerDialog.getDatePicker().setMaxDate(date.getTimeInMillis());
        date.add(Calendar.DAY_OF_MONTH, -43);
        return datePickerDialog;
    }

    private String getDateString() {
        SimpleDateFormat spf = new SimpleDateFormat("MM.dd.yyyy");
        return spf.format(date.getTime());
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

    private void start() {
        if (validate()) {
            String data = MessageFormat.format("{0} {1} {2} {3} {4}",
                    stationFromName,
                    stationFromId,
                    stationToName,
                    stationToId,
                    getDateString());
            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
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
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void updateLabel() {
        String myFormat = "EEEE, MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(date.getTime()));
    }

    private boolean validate() {
        if (!isDepStationCorrect) {
            showErrorMessage("Wrong departure station!", "Departure station wrong or empty");
            stationFromEdit.requestFocus();
            return false;
        }
        if (!isArrStationCorrect) {
            showErrorMessage("Wrong arrival station!", "Arrival station wrong or empty");
            stationToEdit.requestFocus();
            return false;
        }
        if (stationFromId.equals(stationToId)) {
            showErrorMessage("Same stations", "Arrival and departure stations are the same");
            stationFromEdit.requestFocus();
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
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Искомых поездов нет")
                .setMessage("На выбраную дату поездов искомого типа нет,\n" +
                        "но есть поезда других типов\n" +
                        "выберите дальнейшие действия:")
                .setCancelable(false)
                .setNegativeButton("Найти ближайшую \n дату с доступными поездами \n искомого типа",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                findNearestTrainsWithPlaces();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Показать доступные\nпоезда на выбраную дату", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                        intent.putExtra("trains", foundData);
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Изменить поиск", new DialogInterface.OnClickListener() {
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
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Искомых мест нет")
                .setMessage("На выбраную дату мест искомого типа нет,\n" +
                        "но есть места других типов\n" +
                        "выберите дальнейшие действия:")
                .setCancelable(false)
                .setNegativeButton("Найти ближайшую \n дату с доступными местами \n искомого типа",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                findNearestTrainsWithPlaces();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Показать доступные\nместа на выбраную дату", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, TrainListActivity.class);
                        intent.putExtra("trains", foundData);
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Изменить поиск", new DialogInterface.OnClickListener() {
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
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Мест нет")
                .setMessage("На выбраную дату мест нет,\n" +
                        "выберите дальнейшие действия:")
                .setCancelable(true)
                .setNegativeButton("Найти ближайшую\nдату с доступными местами",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                findNearestTrainsWithPlaces();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Включить монитор и\nждать появления\nдоступных мест на\nвыбраную дату",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                activateMonitor();
                                dialog.cancel();
                            }
                        })
                .setNeutralButton("Изменить поиск", new DialogInterface.OnClickListener() {
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
        trainTypesLoc.add("Все");
        trainTypesLoc.add("Обычный");
        trainTypesLoc.add("Интерсити");
        return trainTypesLoc;
    }

    private ArrayList<String> initICCarTypes() {
        ArrayList<String> ICCarTypes = new ArrayList<>();
        ICCarTypes.add("Все");
        ICCarTypes.add("С1");
        ICCarTypes.add("С2");
        return ICCarTypes;
    }

    private ArrayList<String> initUsualCarTypes() {
        ArrayList<String> usualCarTypes = new ArrayList<>();
        usualCarTypes.add("Все");
        usualCarTypes.add("Плацкарт");
        usualCarTypes.add("Купе");
        usualCarTypes.add("Люкс");
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

    }

}
