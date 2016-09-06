package com.faustgate.ukrzaliznitsya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by werwolf on 8/24/16.
 */

public class TrainListAdapter extends BaseAdapter {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private JSONArray mObjects;


    public TrainListAdapter(Context context, JSONArray values) {
        mContext = context;
        mObjects = values;
    }

    @Override
    public int getCount() {
        return mObjects.length();
    }

    @Override
    public JSONObject getItem(int index) {
        JSONObject res = null;
        try {
            res = (JSONObject) mObjects.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.train_item, parent, false);
        }
        JSONObject train = null;
        String number = "";
        String from_name = "";
        String to_name = "";
        JSONArray places_types_obj = null;
        List<HashMap<String, String>> places_types = new ArrayList<>();
        try {
            train = (JSONObject) mObjects.get(position);
            number = train.getString("num");
            from_name = train.getJSONObject("from").getString("station");
            to_name = train.getJSONObject("till").getString("station");
            places_types_obj = train.getJSONArray("types");
            if (places_types_obj != null) {
                for (int i = 0; i < places_types_obj.length(); i++) {
                    HashMap<String, String> type = new HashMap<>();
                    Iterator places = places_types_obj.getJSONObject(i).keys();
                    while (places.hasNext()) {
                        String key = (String) places.next();
                        type.put(key, places_types_obj.getJSONObject(i).getString(key));
                    }
                    places_types.add(type);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String sdf = "asdf";
        if (train != null) {
            ((TextView) convertView.findViewById(R.id.train_num)).setText(number);
            ((TextView) convertView.findViewById(R.id.train_direction)).setText(MessageFormat.format("{0} - {1}", from_name, to_name));
            int j = 1;
            LinearLayout mainLayout = (LinearLayout) convertView.findViewById(R.id.places_placeholder);
            for (Map<String, String> place_type : places_types) {
                LinearLayout placeLayout = (LinearLayout) convertView.findViewById(R.id.place_item);
                ((TextView) placeLayout.findViewById(R.id.place_header)).setText(place_type.get("letter"));
                ((TextView) placeLayout.findViewById(R.id.place_field)).setText(place_type.get("places"));
                mainLayout.addView(placeLayout);
            }
            //        ((TextView) convertView.findViewById(R.id.text1)).setText(names.get(position));
//        ((TextView) convertView.findViewById(R.id.text1)).setText(names.get(position));
//        ((TextView) convertView.findViewById(R.id.text1)).setText(names.get(position));
//        ((TextView) convertView.findViewById(R.id.text1)).setText(names.get(position));
//        ((TextView) convertView.findViewById(R.id.text1)).setText(names.get(position));
//        //((TextView) convertView.findViewById(R.id.text2)).setText(ids.get(position));
        }
        return convertView;
    }
}