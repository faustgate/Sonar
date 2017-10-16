package com.faustgate.sonar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

class CoachListAdapter extends BaseAdapter {

    private final Context mContext;
    private List<HashMap<String, String>> mObjects;

    CoachListAdapter(Context context, List<HashMap<String, String>> values) {
        mContext = context;
        mObjects = values;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public HashMap<String, String> getItem(int index) {
        return mObjects.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_coach, parent, false);
        }
        HashMap<String, String> car = mObjects.get(position);
        String number = car.get("number");
        String carClassName = car.get("carClassName");
        String placeCount = car.get("places_count");
        double price = 0;
        try {
            price = Integer.parseInt(car.get("price")) / 100.0;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String new_price = String.valueOf(price);

        ((TextView) convertView.findViewById(R.id.car_num)).setText(number);
        ((TextView) convertView.findViewById(R.id.price_placeholder)).setText(new_price);
        ((TextView) convertView.findViewById(R.id.car_type)).setText(carClassName);
        ((TextView) convertView.findViewById(R.id.place_num_placeholder)).setText(placeCount);

        return convertView;
    }
}