package com.faustgate.sonar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityTrainList extends Activity {
    private JSONObject obj = null;
    private Calendar date = Calendar.getInstance();
    private JSONArray allTrains, validTrains, targetTrains;
    private boolean showTrainsWithoutPlaces;
    private String name;
    private String surname;
    private String notification = "statbar";
    private String action = "no";

    private OrderDescription currentTicket = OrderDescription.getInstance();
    private UZRequests UZWorker = UZRequests.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SoundNotifier.getInstance(getApplicationContext()).stopSound();
        Intent intent1 = new Intent(this, TicketFinderService.class);
        stopService(intent1);

        Intent intent = getIntent();

        try {

            JSONObject UZResponse = new JSONObject(UZWorker.searchForTrains(currentTicket));

            processUZResponse(UZResponse);

            genList();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.select_trains_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_train_list, menu);
        CheckBox cb_showTrainsWithoutPlaces = menu.findItem(R.id.switchId).getActionView().findViewById(R.id.switchTrainsWOPlaces);
        showTrainsWithoutPlaces = cb_showTrainsWithoutPlaces.isChecked();
        cb_showTrainsWithoutPlaces.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showTrainsWithoutPlaces = isChecked;
                genList();
            }
        });
        genList();
        return true;
    }


    private void genList() {
        targetTrains = new JSONArray();
        try {
            if (showTrainsWithoutPlaces) {
                targetTrains = allTrains;
            } else {
                for (int i = 0; i < validTrains.length(); i++) {
                    if (validTrains.getJSONObject(i).getJSONArray("types").length() > 0) {
                        targetTrains.put(validTrains.getJSONObject(i));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        AdapterTrainList trainAdapter = new AdapterTrainList(getApplicationContext(), targetTrains);


        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(trainAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                try {

                    String data = MessageFormat.format("{0} {1}", position, id);
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();

                    JSONObject selectedTrain = targetTrains.getJSONObject(position);

                    if (selectedTrain.getJSONArray("types").length() == 0) {
                        String boo = "boo";
                    } else {
                        currentTicket.setTrainData(selectedTrain);
                        Intent intent = new Intent(ActivityTrainList.this, ActivityCoachList.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void processUZResponse(JSONObject resp) {
        JSONArray corrTrains = new JSONArray();
        JSONArray corrPlacesTrains = new JSONArray();
        try {
            allTrains = resp.getJSONObject("data").getJSONArray("list");


            if (resp.has("warning")) {
                if (resp.getString("warning").contains("No places")) {
                    showTrainSearchOptionsMessage();
                }
            }


            if (currentTicket.getTrainType().equals("all")) {
                corrTrains = allTrains;
            } else {
                for (int i = 0; i < allTrains.length(); i++) {
                    JSONObject train = allTrains.getJSONObject(i);
                    if (train.getString("category").equals(currentTicket.getTrainType()))
                        corrTrains.put(train);
                }
            }

            if (corrTrains.length() == 0) {
                showTrainOptionsMessage();
            }


            if (currentTicket.getWagonType().equals("all")) {
                corrPlacesTrains = corrTrains;
            } else {
                for (int j = 0; j < corrTrains.length(); j++) {
                    JSONObject train = corrTrains.getJSONObject(j);
                    JSONArray types = train.getJSONArray("types");
                    for (int k = 0; k < types.length(); k++) {
                        String typeId = types.getJSONObject(k).getString("letter");
                        if (typeId.contains(currentTicket.getWagonType()))
                            corrPlacesTrains.put(train);
                    }
                }

                if (corrPlacesTrains.length() == 0) {
                    // foundData = corrTrains.toString();
                    showPlacesOptionsMessage();
                }

            }
            validTrains = corrPlacesTrains;
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showTrainSearchOptionsMessage() {
        AlertDialog dialog = new AlertDialog.Builder(ActivityTrainList.this)
                .setTitle(getString(R.string.no_avail_trains_head))
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

    private void findNearestTrainsWithPlaces() {
        Calendar locCal = Calendar.getInstance();
        locCal.set(Calendar.YEAR, date.get(Calendar.YEAR));
        locCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
        locCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

        for (int day = 0; day < 45; day++) {
            locCal.add(Calendar.DAY_OF_MONTH, 1);
            String trainData = UZRequests.getInstance().searchForTrains(currentTicket);
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
                if (currentTicket.getTrainType().equals("all")) {
                    corrTrains = trains;
                } else {
                    for (int i = 0; i < trains.length(); i++) {
                        JSONObject train = trains.getJSONObject(i);
                        if (train.getString("category").equals(currentTicket.getTrainType()))
                            corrTrains.put(train);
                    }
                }

                if (corrTrains.length() == 0) {
                    continue;
                }

                if (currentTicket.getWagonType().equals("all")) {
                    corrPlacesTrains = corrTrains;
                } else {
                    for (int j = 0; j < corrTrains.length(); j++) {
                        JSONObject train = corrTrains.getJSONObject(j);
                        JSONArray types = train.getJSONArray("types");
                        for (int k = 0; k < types.length(); k++) {
                            String typeId = types.getJSONObject(k).getString("letter");
                            if (typeId.equals(currentTicket.getWagonType()))
                                corrPlacesTrains.put(train);
                        }
                    }
                }

                if (corrPlacesTrains.length() == 0) {
                    continue;
                }

                Intent intent = new Intent(ActivityTrainList.this, ActivityTrainList.class);
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
                //   intent.putExtra("stationFromId", stationFromId);
                //   intent.putExtra("stationToId", stationToId);
                intent.putExtra("date", sdf.format(date.getTime()));
                intent.putExtra("action", action);
                intent.putExtra("notification", notification);
                // intent.putExtra("ticketType", ticketType);
                //  intent.putExtra("buy", isBuying);
                intent.putExtra("name", name);
                intent.putExtra("surname", surname);
                startService(intent);
                dialog.hide();
            }
        });

        dialog.show();


    }

    private void showTrainOptionsMessage() {
        AlertDialog dialog = new AlertDialog.Builder(ActivityTrainList.this).setTitle(getString(R.string.no_trains_head))
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
                        Intent intent = new Intent(ActivityTrainList.this, ActivityTrainList.class);
                        //   intent.putExtra("trains", foundData);
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
        AlertDialog dialog = new AlertDialog.Builder(ActivityTrainList.this).setTitle(getString(R.string.no_places_head))
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
                        Intent intent = new Intent(ActivityTrainList.this, ActivityTrainList.class);
                        //    intent.putExtra("trains", foundData);
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

//    private void showTrainSearchOptionsMessage() {
//        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.no_places_train_head))
//                .setMessage(getString(R.string.no_places_train_body))
//                .setCancelable(true)
//                .setNegativeButton(getString(R.string.no_places_train_find_nearest),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                findNearestTrainsWithPlaces();
//                                dialog.cancel();
//                            }
//                        })
//                .setPositiveButton(getString(R.string.no_avail_trains_enable_monitor),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                activateMonitor();
//                                dialog.cancel();
//                            }
//                        })
//                .setNeutralButton(getString(R.string.modify_search), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                }).create();
//
//        dialog.show();
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLines(4);
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);
//
//        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setLines(4);
//        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);
//
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLines(4);
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f);
//    }


}