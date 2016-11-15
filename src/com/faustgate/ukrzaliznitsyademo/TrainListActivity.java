package com.faustgate.ukrzaliznitsyademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

/**
 * Created by sergey.puronen on 10/12/16.
 */
public class TrainListActivity extends Activity {
    private JSONObject obj = null;
    private JSONArray trains = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String trainsData = intent.getStringExtra("trains");

        try {
            obj = new JSONObject(trainsData);
            trains = obj.getJSONArray("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TrainListAdapter trainAdapter = new TrainListAdapter(getApplicationContext(), trains);

        setContentView(R.layout.select_trains_layout);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(trainAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    String data = MessageFormat.format("{0} {1}", position, id);
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                    String curTrain = trains.getJSONObject(position).toString();

                    Intent intent = new Intent(TrainListActivity.this, PlacesActivity.class);
                    intent.putExtra("train", curTrain);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}