package com.faustgate.sonar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by werwolf on 11/19/16.
 */
public class TicketFinderService extends Service {
    private String stationFromId = "";
    private String stationToId = "";
    private String action = "";
    private String notification = "";
    private String ticketType = "";
    private String name = "";
    private String surname = "";
    private boolean isBuying;
    private ArrayList<HashMap<String, String>> placesDescription = new ArrayList<>();
    private JSONObject curTrain;
    private String place_num = "";


    private static final int NOTIFY_ID = 1;

    private Date date;
    private Timer mTimer;
    private String foundData;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy", Locale.US);

        stationFromId = intent.getStringExtra("stationFromId");
        stationToId = intent.getStringExtra("stationToId");
        action = intent.getStringExtra("action");
        notification = intent.getStringExtra("notification");
        ticketType = intent.getStringExtra("ticketType");
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        isBuying = intent.getBooleanExtra("buy", true);

        try {
            date = sdf.parse(intent.getStringExtra("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        MyTimerTask mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 1000 * 10, 1000 * 60); // Check every 1 min


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onTicketFound() {
        mTimer.cancel();
        mTimer = null;
        stopSelf();
        performAction();
        showNotification();
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            String trainData = UZRequests.getInstance().searchForTrains(stationFromId,
                    stationToId,
                    date);
            try {
                JSONObject resp = new JSONObject(trainData);
                if (resp.getString("error").equals("true") || (!resp.has("value"))) {
                    return;
                }
                JSONArray trains = resp.getJSONArray("value");
                if (trains.length() > 0) {
                    foundData = trains.toString();
                    onTicketFound();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showNotification() {
        PendingIntent resultPendingIntent;
        if (action.equals("no")) {
            Intent resultIntent = new Intent(this, TrainListActivity.class);
            resultIntent.putExtra("trains", foundData);
            resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
        } else {
            Intent resultIntent = new Intent(this, BuyTicketActivity.class);
            resultIntent.putExtra("trains", foundData);
            resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
        }

        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this).setSmallIcon(android.R.drawable.sym_action_email)
                .setTicker("Билеты найдены")
                .setContentTitle("Билеты найдены")
                .setContentText("Появились доступные билеты")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        if (notification.equals("short")) {
            notBuilder.setVibrate(new long[]{1, 1000});
            notBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFY_ID, notBuilder.build());
        if (notification.equals("long")) {
            SoundNotifier.getInstance(getApplicationContext()).startSound();
        }
    }

    private void bookTicket() {
        HashMap<String, String> currentTicketDescription;
        try {
            if (placesDescription.size() == 0) {
                curTrain = new JSONArray(foundData).getJSONObject(0);

                List<JSONObject> ticketsData = UZRequests.getInstance().searchForTickets(curTrain);

                JSONObject coachObject = ticketsData.get(0).getJSONArray("coaches").getJSONObject(0);

                //     ArrayList<String> places = new ArrayList<>();

                JSONObject placesObject = coachObject.getJSONObject("places_list").getJSONObject("places");
                Iterator iterator = placesObject.keys();
                if (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    place_num = placesObject.getJSONArray(key).getString(0);
//                for (int i = 0; i < placesObject.getJSONArray(key).length(); i++) {
//                    places.add(placesObject.getJSONArray(key).getString(i));
                    //   }
                }


                currentTicketDescription = new HashMap<>();
                currentTicketDescription.put("wagon_num", coachObject.getString("num"));
                currentTicketDescription.put("charline", coachObject.getString("coach_class"));
                currentTicketDescription.put("wagon_class", coachObject.getString("coach_class"));
                currentTicketDescription.put("wagon_type", coachObject.getString("type"));

                currentTicketDescription.put("place_num", place_num);
                currentTicketDescription.put("ord", "0");

                currentTicketDescription.put("firstname", name);
                currentTicketDescription.put("lastname", surname);

                currentTicketDescription.put("bedding", "1");

                currentTicketDescription.put("child", ticketType.equals("child") ? "child" : "");
                currentTicketDescription.put("stud", ticketType.equals("stud") ? "stud" : "");
////
//            currentTicketDescription.put("child", "");
//            currentTicketDescription.put("stud", "");
                currentTicketDescription.put("transportation", "0");
                currentTicketDescription.put("reserve", isBuying ? "0" : "1");

                placesDescription.add(currentTicketDescription);
            } else {
                UZRequests.getInstance().revokeTickets();
                UZRequests.getInstance().setRefreshTokens();
                Thread.sleep(10000);

            }
            UZRequests.getInstance().buyTickets(curTrain, placesDescription);
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }


    private void performAction() {
        switch (action) {
            case "1":
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;
                }
                bookTicket();
                break;
            case "cont":
                Timer mimer = new Timer();
                mimer.schedule(new MyTimerTask2(), 1, 1000 * 60 * 4); // Check every 16 min
                break;
        }

    }

    private class MyTimerTask2 extends TimerTask {
        @Override
        public void run() {
            bookTicket();
        }
    }


}
