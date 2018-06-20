package com.faustgate.sonar;

import android.app.Activity;
import android.app.AlertDialog;
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

public class ActivityTrainList extends Activity {
    private JSONObject obj = null;
    private JSONArray foundTrains;
    private JSONArray targetTrains = new JSONArray();
    private boolean showTrainsWithoutPlaces;
    private String name;
    private String surname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SoundNotifier.getInstance(getApplicationContext()).stopSound();
        Intent intent1 = new Intent(this, TicketFinderService.class);
        stopService(intent1);

        Intent intent = getIntent();
        String trainsData = intent.getStringExtra("trains");
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");

        try {
            foundTrains = new JSONArray(trainsData);
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
                targetTrains = foundTrains;
            } else {
                for (int i = 0; i < foundTrains.length(); i++) {
                    if (foundTrains.getJSONObject(i).getJSONArray("types").length() > 0) {
                        targetTrains.put(foundTrains.getJSONObject(i));
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
                        String curTrain = selectedTrain.toString();

                        Intent intent = new Intent(ActivityTrainList.this, ActivityCoachList.class);
                        intent.putExtra("train", curTrain);
                        intent.putExtra("name", name);
                        intent.putExtra("surname", surname);

                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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