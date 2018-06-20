package com.faustgate.sonar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ActivityCoachList extends Activity {
    private JSONArray car_types;
    private ArrayAdapter<String> adapter;
    private ArrayList<HashMap<String, String>> placesDescription = new ArrayList<>();
    private HashMap<String, String> currentTicketDescription = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String trainData = intent.getStringExtra("train");
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");

        List<String> placeLetters = new ArrayList<>();
        List<String> placeTypes = new ArrayList<>();
        List<HashMap<String, String>> carDescriptions = new ArrayList<>();
        placeTypes.add("All");

        setContentView(R.layout.select_coach_layout);
        Spinner coachTypeFilterSpinner = (Spinner) findViewById(R.id.spinnerPlaceTypeFilter);
        JSONObject curTrain;

        try {
            curTrain = new JSONObject(trainData);
            List<JSONObject> ticketsData = UZRequests.getInstance().searchForTickets(curTrain);

            car_types = curTrain.getJSONArray("types");

            for (int i = 0; i < car_types.length(); i++) {
                JSONObject placeType = car_types.getJSONObject(i);
                placeTypes.add(placeType.getString("title"));
                placeLetters.add(placeType.getString("id"));
            }

            for (JSONObject ticketType : ticketsData) {
                JSONArray places = ticketType.getJSONObject("data").getJSONArray("wagons");
                for (int i = 0; i < places.length(); i++) {
                    HashMap<String, String> currentCarDescription = new HashMap<>();

                    currentCarDescription.put("number", places.getJSONObject(i).getString("num"));
                    currentCarDescription.put("coach_class", places.getJSONObject(i).getString("class"));
                    currentCarDescription.put("places_list", places.getJSONObject(i).getString("places_list"));
                    currentCarDescription.put("places_count", places.getJSONObject(i).getString("free"));

                    Iterator prices = places.getJSONObject(i).getJSONObject("prices").keys();
                    while (prices.hasNext()) {
                        String key = (String) prices.next();
                        currentCarDescription.put("price", places.getJSONObject(i).getJSONObject("prices").getString(key));
                    }
                    String carClassLetter = places.getJSONObject(i).getString("type_id");
                    String carClassName = places.getJSONObject(i).getString("type");
                    for (int j = 0; j < car_types.length(); j++) {
                        if (car_types.getJSONObject(j).getString("id").equals(carClassLetter)) {
                            carClassName = car_types.getJSONObject(j).getString("title");
                        }
                    }
                    currentCarDescription.put("carClassLetter", carClassLetter);
                    currentCarDescription.put("carClassName", carClassName);
                    carDescriptions.add(currentCarDescription);
                }
            }


            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, placeTypes);
            coachTypeFilterSpinner.setAdapter(adapter);
            coachTypeFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        for (JSONObject ticketType : ticketsData) {
                            if (position != 0) {

                                JSONArray places = ticketType.getJSONArray("coaches");
                                for (int i = 0; i < places.length(); i++) {

                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ListView lv = (ListView) findViewById(R.id.listView);
            AdapterCoachList carAdapter = new AdapterCoachList(getApplicationContext(), carDescriptions);
            lv.setAdapter(carAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(ActivityCoachList.this, ActivityPlaces2.class);

                    intent.putExtra("dat", new JSONObject(carDescriptions.get(position)).toString());
                    try {
                        intent.putExtra("train_num", curTrain.getString("num"));
                        intent.putExtra("from", curTrain.getJSONObject("from").getString("code"));
                        intent.putExtra("to", curTrain.getJSONObject("to").getString("code"));
                        intent.putExtra("date", curTrain.getJSONObject("from").getString("srcDate"));
                        intent.putExtra("from_date", curTrain.getJSONObject("from").getString("sortTime"));
                        intent.putExtra("to_date",   curTrain.getJSONObject("to").getString("sortTime"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("name", name);
                    intent.putExtra("surname", surname);
                    //intent.putExtra("ticketsData", ticketsData.toString());

                    startActivity(intent);
                }
            });


//            Button buy_button = (Button) findViewById(R.id.button2);
//            buy_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {

            //       }
            //    });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}