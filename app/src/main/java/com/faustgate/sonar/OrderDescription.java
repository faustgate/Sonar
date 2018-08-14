package com.faustgate.sonar;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class OrderDescription {
    private static final OrderDescription ourInstance = new OrderDescription();
    private Context mContext = ApplicationSonar.getContext();

    private Date departureDate = new Date();
    private String stationFromId = "";
    private String stationToId = "";
    private String ticketType = "";
    private String trainType = "";
    private String wagonType = "";
    private String placeId = "";
    private String surname = "";
    private String name = "";
    private JSONObject trainData;
    private JSONObject wagonData;


    public static OrderDescription getInstance() {
        return ourInstance;
    }

    private OrderDescription() {
    }

    public JSONObject getTrainData() {
        return trainData;
    }

    public JSONObject getWagonData() {
        return wagonData;
    }

    public String getDepartureDate() {
        return new SimpleDateFormat(mContext.getString(R.string.date_format)).format(departureDate.getTime());
    }

    public String getStationFromId() {
        return stationFromId;
    }

    public String getStationToId() {
        return stationToId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public String getTrainType() {
        return trainType;
    }

    public String getWagonType() {
        return wagonType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }


    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public void setStationFromId(String stationFromId) {
        this.stationFromId = stationFromId;
    }

    public void setStationToId(String stationToId) {
        this.stationToId = stationToId;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public void setWagonType(String wagonType) {
        this.wagonType = wagonType;
    }

    public void setTrainData(JSONObject trainData) {
        this.trainData = trainData;
    }

    public void setWagonData(JSONObject wagonData) {
        this.wagonData = wagonData;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getTicketDescription() {
        HashMap<String, String> currentTicketDescription = new HashMap<>();
        try {
            currentTicketDescription.put("price", getWagonData().getString("price"));
            currentTicketDescription.put("charline", getWagonData().getString("charline"));
            currentTicketDescription.put("wagon_num", getWagonData().getString("number"));
            currentTicketDescription.put("wagon_class", getWagonData().getString("coach_class"));
            currentTicketDescription.put("wagon_railway", getWagonData().getString("railway"));
            String wagonType = getWagonData().getString("carClassLetter");
            if (wagonType.contains("С"))
                wagonType = wagonType.replaceAll("[0-9]", "");
            currentTicketDescription.put("to", getStationToId());
            currentTicketDescription.put("from", getStationFromId());
            currentTicketDescription.put("date", getDepartureDate());
            currentTicketDescription.put("train", getTrainData().getString("num"));
            currentTicketDescription.put("to_date", getTrainData().getJSONObject("to").getString("sortTime"));
            currentTicketDescription.put("lastname", getSurname());
            currentTicketDescription.put("firstname", getName());
            currentTicketDescription.put("place_num", StringUtils.leftPad(getPlaceId(), 3, '0'));
            currentTicketDescription.put("from_date", getTrainData().getJSONObject("from").getString("sortTime"));
            currentTicketDescription.put("wagon_type", wagonType);

            currentTicketDescription.put("ord", "0");
            currentTicketDescription.put("child", "");
            currentTicketDescription.put("student", "");
            currentTicketDescription.put("bedding", "1");
            currentTicketDescription.put("reserve", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentTicketDescription;
    }

}
