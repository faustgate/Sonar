package com.faustgate.ukrzaliznitsya;

import android.content.Context;
import android.view.Gravity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
            convertView = inflater.inflate(R.layout.item_train, parent, false);
        }
        JSONObject train = null, from_obj, to_obj;
        String number = "";
        String from_name = "";
        String from_time = "";
        String to_name = "";
        String to_time = "";
        JSONArray places_types_obj;
        List<HashMap<String, String>> places_types = new ArrayList<>();
        try {
            train = (JSONObject) mObjects.get(position);
            from_obj = train.getJSONObject("from");
            to_obj = train.getJSONObject("till");

            number = train.getString("num");
            from_name = from_obj.getString("station");
            from_time = from_obj.getString("src_date");
            to_name = to_obj.getString("station");
            to_time = to_obj.getString("src_date");
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
        if (train != null) {
            ((TextView) convertView.findViewById(R.id.train_num)).setText(number);
            ((TextView) convertView.findViewById(R.id.train_direction)).setText(MessageFormat.format("{0} -> {1}", from_name, to_name));
            ((TextView) convertView.findViewById(R.id.train_dep_date_field)).setText(from_time);
            ((TextView) convertView.findViewById(R.id.train_arr_date_field)).setText(to_time);

            LinearLayout mainLayout = (LinearLayout) convertView.findViewById(R.id.places_placeholder);
            mainLayout.removeAllViews();
            for (int idx = 0; idx < places_types.size(); idx++) {
                LinearLayout linearLayout = new LinearLayout(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setLayoutParams(lp);
                TextView tv1 = new TextView(mContext);
                tv1.setText(MessageFormat.format("{}:", places_types.get(idx).get("letter")));
                TextView tv2 = new TextView(mContext);
                tv2.setText(places_types.get(idx).get("places"));
                if (places_types.size() == 2 && idx == 0) {
                    tv1.setPadding(0, getPixelsFromDP(5), 0, 0);
                    tv2.setPadding(0, getPixelsFromDP(5), 0, 0);
                }
                if (places_types.size() == 2 && idx == 1) {
                    tv1.setPadding(0, 0, 0, getPixelsFromDP(5));
                    tv2.setPadding(0, 0, 0, getPixelsFromDP(5));
                }
                linearLayout.addView(tv1);
                linearLayout.addView(tv2);
                mainLayout.addView(linearLayout);
            }
        }
        return convertView;
    }

    private int getPixelsFromDP(int sizeInDp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f);
    }
}