package com.faustgate.ukrzaliznitsya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by werwolf on 8/24/16.
 */

class StationAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;
    private List<String> mResults;
    private ArrayList<String> names = new ArrayList<>();
    private List<HashMap<String, String>> stations = new ArrayList<>();
    private UZRequests uzr = UZRequests.getInstance();

    StationAutoCompleteAdapter(Context context) {
        mContext = context;
        mResults = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public HashMap<String, String> getItem(int index) {
        return stations.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        stations.clear();
        mResults.clear();
        names.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_station, parent, false);
        }
        //String book = getItem(position);
        ((TextView) convertView.findViewById(R.id.text1)).setText(names.get(position));
        //((TextView) convertView.findViewById(R.id.text2)).setText(ids.get(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    stations = findStations(constraint.toString());
                    for (HashMap<String, String> station : stations) {
                        names.add(station.get("title"));
                    }
                    // Assign the data to the FilterResults
                    filterResults.values = names;
                    filterResults.count = names.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = names;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<HashMap<String, String>> findStations(String stationName) {
        List<HashMap<String, String>> stations;
        names.clear();
        try {
            stations = uzr.getStationsInfo(stationName);
            if (stations.size() == 0) {
                Toast da = Toast.makeText(mContext, "No stations were found by given criteria", Toast.LENGTH_SHORT);
                da.show();
            }
            return stations;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}