package com.faustgate.sonar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PlacesActivity2 extends Activity {
    private JSONArray car_types;
    private ArrayAdapter<String> adapter;
    private ArrayList<HashMap<String, String>> placesDescription = new ArrayList<>();
    private HashMap<String, String> currentTicketDescription = new HashMap<>();
    Map<String, String> scheme_map = new HashMap<>();
    ImageView base_layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String trainData = intent.getStringExtra("dat");
        String ticketsData = intent.getStringExtra("ticketsData");
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        setContentView(R.layout.select_place_layout2);
        // base_layout = (ImageView) findViewById(R.id.base_layout);

        scheme_map.put("К", "coupe_36.png");
        scheme_map.put("П", "plazcart.svg");
        scheme_map.put("Л", "lux.svg");
        scheme_map.put("С2", "ic_class_2.svg");

        Map<String, String>[] place_map;


        String places_scheme = "[{\"plac e\": 1, \"X\": 2 \"Y\": 123}, {\"place\": 5, \"X\": 267, \"Y\": 123}, {\"place\": 9, \"X\": 325, \"Y\": 123}, {\"place\": 13, \"X\": 383, \"Y\": 123}, {\"place\": 17, \"X\": 441, \"Y\": 123}, {\"place\": 21, \"X\": 499, \"Y\": 123}, {\"place\": 25, \"X\": 557, \"Y\": 123}, {\"place\": 29, \"X\": 615, \"Y\": 123}, {\"place\": 33, \"X\": 673, \"Y\": 123}, {\"place\": 37, \"X\": 731, \"Y\": 123}, {\"place\": 41, \"X\": 789, \"Y\": 123}, {\"place\": 45, \"X\": 847, \"Y\": 123}, {\"place\": 49, \"X\": 905, \"Y\": 123}, {\"place\": 53, \"X\": 963, \"Y\": 123}, {\"place\": 2, \"X\": 209, \"Y\": 92}, {\"place\": 6, \"X\": 267, \"Y\": 92}, {\"place\": 10, \"X\": 325, \"Y\": 92}, {\"place\": 14, \"X\": 383, \"Y\": 92}, {\"place\": 18, \"X\": 441, \"Y\": 92}, {\"place\": 22, \"X\": 499, \"Y\": 92}, {\"place\": 26, \"X\": 557, \"Y\": 92}, {\"place\": 30, \"X\": 615, \"Y\": 92}, {\"place\": 34, \"X\": 673, \"Y\": 92}, {\"place\": 38, \"X\": 731, \"Y\": 92}, {\"place\": 42, \"X\": 789, \"Y\": 92}, {\"place\": 46, \"X\": 847, \"Y\": 92}, {\"place\": 50, \"X\": 905, \"Y\": 92}, {\"place\": 54, \"X\": 963, \"Y\": 92}, {\"place\": 3, \"X\": 199, \"Y\": 35}, {\"place\": 7, \"X\": 249, \"Y\": 35}, {\"place\": 11, \"X\": 299, \"Y\": 35}, {\"place\": 15, \"X\": 349, \"Y\": 35}, {\"place\": 19, \"X\": 399, \"Y\": 35}, {\"place\": 23, \"X\": 449, \"Y\": 35}, {\"place\": 27, \"X\": 499, \"Y\": 35}, {\"place\": 31, \"X\": 549, \"Y\": 35}, {\"place\": 35, \"X\": 599, \"Y\": 35}, {\"place\": 39, \"X\": 649, \"Y\": 35}, {\"place\": 43, \"X\": 699, \"Y\": 35}, {\"place\": 47, \"X\": 749, \"Y\": 35}, {\"place\": 51, \"X\": 799, \"Y\": 35}, {\"place\": 55, \"X\": 849, \"Y\": 35}, {\"place\": 4, \"X\": 199, \"Y\": 5}, {\"place\": 8, \"X\": 249, \"Y\": 5}, {\"place\": 12, \"X\": 299, \"Y\": 5}, {\"place\": 16, \"X\": 349, \"Y\": 5}, {\"place\": 20, \"X\": 399, \"Y\": 5}, {\"place\": 24, \"X\": 449, \"Y\": 5}, {\"place\": 28, \"X\": 499, \"Y\": 5}, {\"place\": 32, \"X\": 549, \"Y\": 5}, {\"place\": 36, \"X\": 599, \"Y\": 5}, {\"place\": 40, \"X\": 649, \"Y\": 5}, {\"place\": 44, \"X\": 699, \"Y\": 5}, {\"place\": 48, \"X\": 749, \"Y\": 5}, {\"place\": 52, \"X\": 799, \"Y\": 5}, {\"place\": 56, \"X\": 849, \"Y\": 5}]\n";

        try {

//            JSONObject currentCarDescription = new JSONObject(trainData);
//            JSONArray ticketsDescription = new JSONArray(ticketsData);

            //           String scheme_name = scheme_map.get(currentCarDescription.getString("carClassLetter"));
            String scheme_name = scheme_map.get("К");

            ImageView img2 = new ImageView(getApplicationContext());
            img2.setImageDrawable(Drawable.createFromStream(getAssets().open(scheme_name), null));
            img2.setMinimumWidth(2044);
            img2.setMinimumHeight(293);


            places_scheme = getPlaceMap();

            AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.absoluteLayout);

            al.addView(img2);
            JSONArray places = new JSONArray(places_scheme);

            place_map = new Map[places.length()];
            for (int k = 0; k < places.length(); k++) {
                place_map[k] = new HashMap<>();
            }

            for (int i = 0; i < places.length(); i++) {
                int curID = places.getJSONObject(i).getInt("place");
                long curX = places.getJSONObject(i).getLong("X");
                long curY = places.getJSONObject(i).getLong("Y");

                place_map[curID - 1].put("X", String.valueOf(curX));
                place_map[curID - 1].put("X", String.valueOf(curY));


                ImageView cur_view = new ImageView(getApplicationContext());
                if (i % 3 == 0)
                    cur_view.setImageDrawable(getResources().getDrawable(R.drawable.busy));
                else
                    cur_view.setImageDrawable(getResources().getDrawable(R.drawable.available));

                cur_view.setX(getPixelsFromDP(curX));
                cur_view.setY(getPixelsFromDP(curY));
                cur_view.setMinimumHeight(getPixelsFromDP(41));
                cur_view.setMinimumWidth(getPixelsFromDP(41));

                cur_view.setId(curID);

                cur_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int placeId = v.getId();
                        if (!Boolean.valueOf(place_map[placeId - 1].get("occupied"))) {
                            boolean selected = Boolean.valueOf(place_map[placeId - 1].get("selected"));
                            ImageView img = (ImageView) findViewById(v.getId());

                            selected = !selected;

                            if (selected)
                                img.setImageDrawable(getResources().getDrawable(R.drawable.selected));
                            else {
                                img.setImageDrawable(getResources().getDrawable(R.drawable.available));
                            }
                            place_map[placeId - 1].put("selected", String.valueOf(selected));
                        }
                    }
                });

                al.addView(cur_view);
            }
//            currentTicketDescription.put("wagon_num", currentCarDescription.getString("number"));
//            currentTicketDescription.put("charline", currentCarDescription.getString("coach_class"));
//            currentTicketDescription.put("wagon_class", currentCarDescription.getString("coach_class"));
//            currentTicketDescription.put("wagon_type", currentCarDescription.getString("carClassLetter"));
//

            currentTicketDescription.put("ord", "0");
            currentTicketDescription.put("firstname", name);
            currentTicketDescription.put("lastname", surname);
            currentTicketDescription.put("bedding", "1");
            currentTicketDescription.put("child", "");
            currentTicketDescription.put("stud", "");
            currentTicketDescription.put("transportation", "0");
            currentTicketDescription.put("reserve", "0");
            placesDescription.add(currentTicketDescription);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private int getPixelsFromDP(float sizeInDp) {
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f);
    }

    private String getPlaceMap() {
        String places_scheme = "";
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = null;
            json = getAssets().open("ic_class11_scheme.json");

            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
            places_scheme = buf.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return places_scheme;
    }

}