package com.faustgate.sonar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class ActivityPlaces2 extends Activity {
    private JSONArray car_types;
    private ArrayAdapter<String> adapter;
    private ArrayList<HashMap<String, String>> placesDescription = new ArrayList<>();
    private Map<String, String[]> scheme_map = new HashMap<>();
    ImageView base_layout;
    private Drawable scheme;
    private Drawable available;
    private Drawable selected;
    private Drawable busy;
    private JSONObject currentCarDescription;
    OrderDescription currentTicket = OrderDescription.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isExtended = false;
        setContentView(R.layout.select_place_layout2);
        // base_layout = (ImageView) findViewById(R.id.base_layout);

        scheme_map.put("К36", new String[]{"images/coupe_36.png", "schemes/coupe_36.json", "2044", "293", "34", "34"});
        scheme_map.put("К40", new String[]{"images/coupe_40.png", "schemes/coupe_40.json", "2044", "273", "32", "32"});
        scheme_map.put("П", new String[]{"images/plazcart.png", "schemes/plazcart.json", "2044", "293", "32", "32"});
        scheme_map.put("Л", new String[]{"images/lux.png", "schemes/lux.json", "2044", "293", "83", "31"});
        scheme_map.put("С1", new String[]{"images/ic_class_1.png", "schemes/ic_class_1.json", "2292", "335", "82", "82"});
        scheme_map.put("С2", new String[]{"images/ic_class_2.png", "schemes/ic_class_2.json", "2292", "335", "35", "40"});

        Map<String, String>[] place_map;

        AdapterPlaceList placeListAdapter = new AdapterPlaceList(getApplicationContext(), placesDescription);
        ListView lv = (ListView) findViewById(R.id.place_list);
        Button addToCartButton = (Button) findViewById(R.id.add_to_cart_button);

        lv.setAdapter(placeListAdapter);


        String places_scheme;

        try {
            currentCarDescription = currentTicket.getWagonData();
//            String trainNum = currentTicket.getTrainData().getString("num");
//            String dateFrom = currentTicket.getTrainData().getJSONObject("from").getString("sortTime");
//            String dateTo = currentTicket.getTrainData().getJSONObject("to").getString("sortTime");


            //   String key = "Л";
            String key = currentCarDescription.getString("carClassLetter");


            if (key.contains("С")) {
                available = Drawable.createFromStream(getAssets().open("images/available_ic.png"), null);
                busy = Drawable.createFromStream(getAssets().open("images/busy_ic.png"), null);
                selected = Drawable.createFromStream(getAssets().open("images/selected_ic.png"), null);
            } else {
                available = Drawable.createFromStream(getAssets().open("images/available.png"), null);
                busy = Drawable.createFromStream(getAssets().open("images/busy.png"), null);
                selected = Drawable.createFromStream(getAssets().open("images/selected.png"), null);
            }


            JSONObject freePlacesList = new JSONObject(currentCarDescription.getString("places_list")).getJSONObject("places");

            JSONArray places = new JSONArray();
            Iterator places_iter = freePlacesList.keys();

            while (places_iter.hasNext()) {
                places = freePlacesList.getJSONArray((String) places_iter.next());
            }

            List<Integer> freePlaces = new ArrayList<>();

            for (int j = 0; j < places.length(); j++) {
                freePlaces.add(Integer.parseInt(places.getString(j)));
            }


            if (key.equals("К")) {
                for (int i = 37; i <= 40; i++) {
                    if (freePlaces.contains(i)) {
                        isExtended = true;
                        break;
                    }
                }

                if (isExtended) {
                    key += "40";
                } else {
                    key += "36";
                }
            }

            //           String scheme_info = scheme_map.get(currentCarDescription.getString("carClassLetter"));
            String[] scheme_info = scheme_map.get(key);

            ImageView img2 = new ImageView(getApplicationContext());
            img2.setImageDrawable(Drawable.createFromStream(getAssets().open(scheme_info[0]), null));
            img2.setMinimumWidth(Integer.parseInt(scheme_info[2]));
            img2.setMinimumHeight(Integer.parseInt(scheme_info[3]));


            places_scheme = getPlaceMap(scheme_info[1]);

            AbsoluteLayout al = (AbsoluteLayout) findViewById(R.id.absoluteLayout);

            al.addView(img2);
            JSONArray gen_places = new JSONArray(places_scheme);

            place_map = new Map[gen_places.length()];
            for (int k = 0; k < gen_places.length(); k++) {
                place_map[k] = new HashMap<>();
            }

            for (int i = 0; i < gen_places.length(); i++) {
                int curID = gen_places.getJSONObject(i).getInt("place");
                long curX = gen_places.getJSONObject(i).getLong("X");
                long curY = gen_places.getJSONObject(i).getLong("Y");

                place_map[curID - 1].put("X", String.valueOf(curX));
                place_map[curID - 1].put("Y", String.valueOf(curY));


                ImageView cur_view = new ImageView(getApplicationContext());

                if (freePlaces.contains(curID)) {
                    cur_view.setImageDrawable(available);
                    cur_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Integer placeId = v.getId();

                            if (!Boolean.valueOf(place_map[placeId - 1].get("occupied"))) {
                                boolean isSelected = Boolean.valueOf(place_map[placeId - 1].get("selected"));
                                ImageView img = findViewById(v.getId());

                                isSelected = !isSelected;

                                if (isSelected) {
                                    currentTicket.setPlaceId(placeId.toString());
                                    img.setImageDrawable(selected);
                                    placesDescription.add(currentTicket.getTicketDescription());

                                } else {
                                    for (int i = 0; i < placesDescription.size(); i++) {
                                        if (placesDescription.get(i).get("place_num").equals(StringUtils.leftPad(String.valueOf(placeId), 3, '0'))) {
                                            placesDescription.remove(i);
                                        }
                                    }
                                    img.setImageDrawable(available);
                                }
                                place_map[placeId - 1].put("selected", String.valueOf(isSelected));
                            }
                            placeListAdapter.notifyDataSetChanged();
                            addToCartButton.setEnabled(placesDescription.size() > 0);
                        }
                    });
                } else {
                    cur_view.setImageDrawable(busy);
                }

                cur_view.setX(getPixelsFromDP(curX));
                cur_view.setY(getPixelsFromDP(curY));

                cur_view.setScaleType(ImageView.ScaleType.FIT_XY);

                cur_view.setMinimumHeight(getPixelsFromDP(Integer.parseInt(scheme_info[4])));

                cur_view.setMinimumWidth(getPixelsFromDP(Integer.parseInt(scheme_info[5])));

                cur_view.setId(curID);

                al.addView(cur_view);
            }


            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationSonar.getInstance().ticketStorage.addAll(placesDescription);
                    Intent intent = new Intent(ActivityPlaces2.this, ActivityStart.class);
                    startActivity(intent);
                }
            });

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private int getPixelsFromDP(float sizeInDp) {
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f);
    }

    private String getPlaceMap(String file_name) {
        String places_scheme = "";
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json;
            json = getAssets().open(file_name);

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