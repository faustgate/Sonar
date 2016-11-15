package com.faustgate.ukrzaliznitsyademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sergey.puronen on 10/12/16.
 */
public class PlacesActivity extends Activity {
    private JSONArray car_types;
    private ArrayAdapter<String> adapter;
    private ArrayList<HashMap<String, String>> placesDescription = new ArrayList<>();
    private HashMap<String, String> currentTicketDescription = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String trainData = intent.getStringExtra("train");

        List<String> placeTypes = new ArrayList<>();
        List<String> carNumbers = new ArrayList<>();
        List<String> placeNumbers = new ArrayList<>();
        setContentView(R.layout.select_place_layout);
        Spinner placeTypeSelectSpinner = (Spinner) findViewById(R.id.spinnerTypeSelect);
        Spinner carSelectSpinner = (Spinner) findViewById(R.id.spinnerCarSelect);
        Spinner placeSelectSpinner = (Spinner) findViewById(R.id.spinnerPlaceSelect);
        placeTypeSelectSpinner.setEnabled(false);
        carSelectSpinner.setEnabled(false);
        placeSelectSpinner.setEnabled(false);


        try {
            JSONObject curTrain = new JSONObject(trainData);

            List<JSONObject> ticketsData = UZRequests.getInstance().searchForTickets(curTrain);

            car_types = curTrain.getJSONArray("types");


            placeTypes.add("Выберите тип места");
            for (int i = 0; i < car_types.length(); i++) {
                JSONObject placeType = car_types.getJSONObject(i);
                placeTypes.add(MessageFormat.format("{0} - {1}", placeType.getString("title"), placeType.getString("places")));
            }
            placeTypeSelectSpinner.setEnabled(true);
            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, placeTypes);
            placeTypeSelectSpinner.setAdapter(adapter);
            placeTypeSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (position > 0) {
                            if (carNumbers.size() > 0) {
                                carNumbers.clear();
                            }
                            carNumbers.add("Выберите вагон");

                            String placeType = car_types.getJSONObject(position - 1).getString("letter");
                            for (JSONObject ticketData : ticketsData) {
                                JSONArray coachesList = ticketData.getJSONArray("coaches");
                                for (int i = 0; i < coachesList.length(); i++) {
                                    String currentCarTypeId = coachesList.getJSONObject(i).getString("type");
                                    if (placeType.equals(currentCarTypeId)) {
                                        carNumbers.add(coachesList.getJSONObject(i).getString("num"));
                                        String agfa = "zsgz";
                                    }
                                }
                            }
                        } else {
                            carSelectSpinner.setEnabled(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (carNumbers.size() > 1) {
                        carSelectSpinner.setEnabled(true);
                        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, carNumbers);
                        carSelectSpinner.setAdapter(adapter);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            carSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (position > 0) {
                            if (placeNumbers.size() > 0) {
                                placeNumbers.clear();
                            }
                            placeNumbers.add("Выберите место");
                            String carNumber = carNumbers.get(position);
                            for (JSONObject ticketData : ticketsData) {
                                JSONArray coachesList = ticketData.getJSONArray("coaches");
                                for (int i = 0; i < coachesList.length(); i++) {
                                    String currentCarNumber = coachesList.getJSONObject(i).getString("num");
                                    if (carNumber.equals(currentCarNumber)) {
                                        JSONObject coachObject = coachesList.getJSONObject(i);

                                        currentTicketDescription.put("wagon_num", coachObject.getString("num"));
                                        currentTicketDescription.put("charline", coachObject.getString("coach_class"));
                                        currentTicketDescription.put("wagon_class", coachObject.getString("coach_class"));
                                        currentTicketDescription.put("wagon_type", coachObject.getString("type"));

                                        JSONObject placesObject = coachObject.getJSONObject("places_list").getJSONObject("places");

                                        Iterator iterator = placesObject.keys();
                                        while (iterator.hasNext()) {
                                            String key = (String) iterator.next();
                                            JSONArray placesList = placesObject.getJSONArray(key);
                                            for (int j = 0; j < placesList.length(); j++)
                                                placeNumbers.add(placesList.get(j).toString());
                                            String agfa = "zsgz";
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (placeNumbers.size() > 1) {
                        placeSelectSpinner.setEnabled(true);
                        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, placeNumbers);
                        placeSelectSpinner.setAdapter(adapter);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            placeSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentTicketDescription.put("place_num", placeNumbers.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            Button buy_button = (Button) findViewById(R.id.button2);
            buy_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentTicketDescription.put("ord", "0");
                    currentTicketDescription.put("firstname", "gsdfgs");
                    currentTicketDescription.put("lastname", "sdfgsfgs");
                    currentTicketDescription.put("bedding", "1");
                    currentTicketDescription.put("child", "");
                    currentTicketDescription.put("stud", "");
                    currentTicketDescription.put("transportation", "0");
                    currentTicketDescription.put("reserve", "0");

                    placesDescription.add(currentTicketDescription);
                    String ticketsData = UZRequests.getInstance().buyTickets(curTrain, placesDescription);
                    placesDescription.clear();
                    Intent intent = new Intent(PlacesActivity.this, BuyTicketActivity.class);
                    intent.putExtra("sesCookie", UZRequests.getInstance().getAuthCookie());
                    intent.putExtra("authToken", UZRequests.getInstance().getAuthToken());
                    startActivity(intent);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}