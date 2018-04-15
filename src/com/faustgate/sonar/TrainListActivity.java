package com.faustgate.sonar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

public class TrainListActivity extends Activity {
    private JSONObject obj = null;
    private JSONArray foundTrains;
    private JSONArray targetTrains = new JSONArray();
    private CheckBox cb_showTrainsWithoutPlaces;
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
        cb_showTrainsWithoutPlaces = (CheckBox) menu.findItem(R.id.switchId).getActionView().findViewById(R.id.switchTrainsWOPlaces);
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


    private void genList(){
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

        TrainListAdapter trainAdapter = new TrainListAdapter(getApplicationContext(), targetTrains);


        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(trainAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    String data = MessageFormat.format("{0} {1}", position, id);
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                    String curTrain = targetTrains.getJSONObject(position).toString();

                    Intent intent = new Intent(TrainListActivity.this, CoachListActivity.class);
                    intent.putExtra("train", curTrain);
                    intent.putExtra("name", name);
                    intent.putExtra("surname", surname);

                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}