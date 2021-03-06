package com.faustgate.sonar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

class AdapterPlaceList extends BaseAdapter {

    private final Context mContext;
    private List<HashMap<String, String>> mObjects;

    AdapterPlaceList(Context context, List<HashMap<String, String>> values) {
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
            convertView = inflater.inflate(R.layout.item_place, parent, false);
        }
        HashMap<String, String> car = mObjects.get(position);
        String trainNumber = car.get("train");
        String carNumber = car.get("wagon_num");
        String placeNumber = car.get("place_num");
        String carClassName = car.get("carClassName");
        String fromDate = getDateFromTimestamp(car.get("from_date"));
        String toDate = getDateFromTimestamp(car.get("to_date"));
        double price = 0;
        try {
            price = Integer.parseInt(car.get("price")) / 100.0;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String new_price = String.valueOf(price);

        ((TextView) convertView.findViewById(R.id.train_placeholder)).setText(trainNumber);
        ((TextView) convertView.findViewById(R.id.price_placeholder)).setText(new_price);
        ((TextView) convertView.findViewById(R.id.dep_placeholder)).setText(fromDate);
        ((TextView) convertView.findViewById(R.id.arr_placeholder)).setText(toDate);
        ((TextView) convertView.findViewById(R.id.car_placeholder)).setText(carNumber);
        ((TextView) convertView.findViewById(R.id.place_placeholder)).setText(placeNumber);
       // ((TextView) convertView.findViewById(R.id.car_type)).setText(carClassName);

        return convertView;
    }

    private String getDateFromTimestamp(String timestamp){
        String myFormat = mContext.getString(R.string.user_date_format);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, mContext.getResources().getConfiguration().locale);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(timestamp) * 1000);
        String dateStr = sdf.format(calendar.getTime());
        dateStr = dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1);
        return dateStr;
    }
}