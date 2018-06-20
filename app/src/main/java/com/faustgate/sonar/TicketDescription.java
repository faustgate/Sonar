package com.faustgate.sonar;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketDescription {
    private static final TicketDescription ourInstance = new TicketDescription();
    private Context mContext = ApplicationSonar.getContext();

    private String stationFromId = "";
    private String stationToId = "";
    private String name = "";
    private String surname = "";
    private String ticketType = "";
    private Date   departureDate = new Date();


    public static TicketDescription getInstance() {
        return ourInstance;
    }

    private TicketDescription() {
    }

    public String getStationFromId() {
        return stationFromId;
    }

    public void setStationFromId(String stationFromId) {
        this.stationFromId = stationFromId;
    }

    public String getStationToId() {
        return stationToId;
    }

    public void setStationToId(String stationToId) {
        this.stationToId = stationToId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getDepartureDate() {
        return new SimpleDateFormat(mContext.getString(R.string.date_format)).format(departureDate.getTime());
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }
}
