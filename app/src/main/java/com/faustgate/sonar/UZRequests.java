package com.faustgate.sonar;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

class UZRequests {
    private static UZRequests mInstance = null;
    private Context mContext = null;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> formData = new HashMap<>();
    private Map<String, List<String>> lastHeaders;
    private List<String> cookies = new ArrayList<>();
    private List<String> ticketFields = Arrays.asList("from", "to", "train", "date", "ord", "charline", "wagon_num",
                                                      "wagon_class", "wagon_type", "firstname", "lastname", "bedding",
                                                      "child", "stud", "transportation", "reserve", "place_num");
    private List<String> formFields = Arrays.asList("roundtrip");
    private List<JSONObject> lastPlacesInfo = new ArrayList<>();
  //  private JSONObject lastTr = new ArrayList<>();
    private String auth_token, reservationId, lastFromStationId, lastTillStationId, lastDepDate,lastTrainsInfo, lastTrainId;
    private ArrayList<HashMap<String, String>> ticketsDescriptions;
    private boolean isAuthDataPresent = false;
    private boolean isBuying = false;
    private boolean refreshTokens = true;
    private boolean ticketsFound = false;
    private JSONObject lastTrainInfo;



    private String currentDepDate, currentStationTillId, currentStationFromId;
    private Date dateDep;
    List<String> tmp = new ArrayList<>();

    private UZRequests() {
        headers.put("Referer", "http://booking.uz.gov.ua");
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36");
    }


    static UZRequests getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UZRequests();
        }
        mInstance.setContext(context);
        return mInstance;
    }

    static UZRequests getInstance() {
        if (mInstance == null) {
            mInstance = new UZRequests();
        }
        return mInstance;
    }

    private void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private void initFormData() {
        if (formData.size() > 0)
            formData.clear();
    }

    List<HashMap<String, String>> getStationsInfo(String station_name) {
        String first_letters = station_name.toLowerCase();
        String res;
        JSONObject dataJsonObj;

        initFormData();

        List<HashMap<String, String>> stations = new ArrayList<>();
        try {
            res = new GetUZData().execute(mContext.getString(R.string.base_url) + "/train_search/station/?term=" + first_letters).get();
            JSONArray stt = new JSONArray(res);
            for (int i = 0; i < stt.length(); i++) {
                HashMap<String, String> station = new HashMap<>();
                JSONObject sttt = stt.getJSONObject(i);
                station.put("title", sttt.getString("title"));
                station.put("station_id", sttt.getString("value"));
                stations.add(station);
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return stations;
    }

    String searchForTrains(String station_from_id, String station_to_id, Date date) {
        initFormData();

        String strDateDep = new SimpleDateFormat(mContext.getString(R.string.date_format)).format(date.getTime());

        if (!station_from_id.equals(lastFromStationId) || !station_to_id.equals(lastTillStationId) || !strDateDep.equals(lastDepDate)) {
            dateDep = date;
            formData.put("from", station_from_id);
            formData.put("to", station_to_id);
            formData.put("date", strDateDep);
            formData.put("time", "00:00");
            // formData.put("get_tpl", "1");
            formData.put("another_ec", "0");
            try {
                Object res = new GetUZData().execute(mContext.getString(R.string.base_url) + "/train_search/").get();
                lastTrainsInfo = res.toString();
//                lastTrainsInfo = StringEscapeUtils.unescapeJava(res.toString());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            lastDepDate = strDateDep;
            lastFromStationId = station_from_id;
            lastTillStationId = station_to_id;
        }
        return lastTrainsInfo;
    }

    List<JSONObject> searchForTickets(JSONObject trainInfo) {

        List<JSONObject> placesInfo = new ArrayList<>();
        Object res;

        try {
            String trainId = trainInfo.getString("num");
            String date    = trainInfo.getJSONObject("from").getString("srcDate");

            lastTrainInfo = trainInfo;

            if ((!currentStationFromId.equals(lastFromStationId) || !currentStationTillId.equals(lastTillStationId) ||
                    !date.equals(lastDepDate) || !trainId.equals(lastTrainId))) {


                JSONArray car_types = trainInfo.getJSONArray("types");
                initFormData();
                formData.put("from", currentStationFromId);
                formData.put("to", currentStationTillId);
                formData.put("date", date);
                formData.put("train", trainId);
                formData.put("another_ec", "0");

                for (int i = 0; i < car_types.length(); i++) {
                    try {
                        if (formData.containsKey("wagon_type_id"))
                            formData.remove("wagon_type_id");
                        String placeId = trainInfo.getJSONArray("types").getJSONObject(i).getString("id");
                        formData.put("wagon_type_id", placeId);

                        res = new GetUZData().execute(mContext.getString(R.string.base_url) + "/train_wagons/").get();

                        JSONObject coachesInfo = new JSONObject(res.toString());

                        coachesInfo.remove("content");

                        JSONArray availableCars = coachesInfo.getJSONObject("data").getJSONArray("wagons");
                        for (int j = 0; j < availableCars.length(); j++) {
                            JSONObject currentCoachInfo = availableCars.getJSONObject(j);
                            String coachNum = currentCoachInfo.getString("num");
                            String coachClass = currentCoachInfo.getString("class");
                            if (formData.containsKey("wagon_num"))
                                formData.remove("wagon_num");
                            if (formData.containsKey("wagon_class"))
                                formData.remove("wagon_class");
                            formData.put("wagon_num", coachNum);
                            formData.put("wagon_class", coachClass);
                            formData.put("wagon_type", placeId.replaceAll("[0-9]", ""));
                            res = new GetUZData().execute(mContext.getString(R.string.base_url) + "/train_wagon/").get();
                            tmp.add(res.toString());
                            JSONObject coachInfo = new JSONObject(res.toString());
                            JSONObject placesList = coachInfo.getJSONObject("data");
                            availableCars.getJSONObject(j).put("places_list", placesList);
                        }

                        placesInfo.add(coachesInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                lastPlacesInfo = placesInfo;

            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return lastPlacesInfo;
    }

    String buyTickets(ArrayList<HashMap<String, String>> ticketDescriptions) {
        String result = null;
        try {
            initFormData();
            String trainId       = lastTrainInfo.getString("num");
            String stationFromId = lastFromStationId;
            String stationTillId = lastTillStationId;
            formData.put("from", stationFromId);
            formData.put("to",   stationTillId);
            formData.put("date", new SimpleDateFormat("yyyy-MM-dd").format(dateDep));
            formData.put("train", trainId);
            formData.put("round_trip", "0");

            ticketsDescriptions = ticketDescriptions;
            isBuying = true;
            Object res = new GetUZData().execute(mContext.getString(R.string.base_url) + "/cart/add/").get();
            isBuying = false;
            result = res.toString();
           // result = StringEscapeUtils.unescapeJava(res.toString());
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return "result";
    }

    String revokeTickets() {
        String result = "";
        try {
            initFormData();
            formData.put("reserve_ids", reservationId);
            Object res = new GetUZData().execute(mContext.getString(R.string.base_url) + "cart/revocation/").get();
            result = res.toString();
           // result = StringEscapeUtils.unescapeJava(res.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    void setRefreshTokens() {
        refreshTokens = true;
    }

    private String getAuthData(String page) {
        String token = "";
//        try {
//            String script = page.substring(page.lastIndexOf("<script>"), page.lastIndexOf("</script>"));
//            script = script.replace(rem_head, "");
//            token = new JJDecoder(script).decode().split("\"")[3];
//        } catch (Exception e) {
//            LogSystem.e("UKRZaliznitsya", MessageFormat.format("Error in getAuthData, page is: {}", page));
//            e.printStackTrace();
//            return "";
//        }
        //   headers.put("GV-Token", token);
        headers.put("GV-Ajax", "1");
        StringBuilder str = new StringBuilder();
        for (String el : cookies) {
            str.append(el);
            str.append(";");
        }
        String cookie = str.toString().replace("Set-Cookie:", "");
        headers.put("Cookie", cookie);
        LogSystem.i("UKRZaliznitsya", "Cookies \"" + cookie + " \" saved ");
        refreshTokens = false;
        return token;
    }

    String getAuthToken() {
        return headers.get("GV-Token");
    }

    String getAuthCookie() {
        return headers.get("Cookie");
    }

    public void setDateDep(Date date_dep) {
        dateDep = date_dep;
    }

    private String getStringFromStream(InputStream stream) {
        String res = "";
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            res = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private class GetUZData extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            if ((!isAuthDataPresent) || refreshTokens) {
                LogSystem.i("UKRZaliznitsya", "No cookies saved, renewing...");
                String page = performRequest(mContext.getString(R.string.base_url));
                auth_token = getAuthData(page);
                isAuthDataPresent = true;
            }
            return performRequest(urls[0]);
        }

        protected void onPostExecute(String response) {
//            try {
//                JSONObject dataJsonObj = new JSONObject(response);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }


        private String performRequest(String url_adr) {
            String response_string = "";
            try {
                URL uzURL = new URL(url_adr);
                byte[] postData;
                String formDataStr = "";
                InputStream inputStream;

                HttpURLConnection uzConnection = (HttpURLConnection) uzURL.openConnection();
                uzConnection.setDoOutput(true);
                uzConnection.setInstanceFollowRedirects(false);
                uzConnection.setRequestMethod("POST");
                uzConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                uzConnection.setRequestProperty("charset", "utf-8");



                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    uzConnection.setRequestProperty(key, value);
                }

                if (isBuying) {

                    for (String field : formFields) {
                        if (formData.containsKey(field)) {
                            String value = formData.get(field);
                            if (formDataStr.length() > 0) {
                                formDataStr += "&";
                            }
                            formDataStr += MessageFormat.format("{0}={1}", field, URLEncoder.encode(value));
                        }
                    }

                    for (int i = 0; i < ticketsDescriptions.size(); i++) {
                        HashMap<String, String> currentTicketDescription = ticketsDescriptions.get(i);
                        for (String field : ticketFields) {
                            if (currentTicketDescription.containsKey(field)) {
                                if (formDataStr.length() > 0) {
                                    formDataStr += "&";
                                }
                                formDataStr += MessageFormat.format("places[{0}][{1}]={2}", i, field, URLEncoder.encode(currentTicketDescription.get(field)));
                            }
                        }
                    }
                    //postData = new StringEntity("from=2208001&to=2200001&train=106%D0%A8&date=2016-11-30&round_trip=0&places[0][ord]=0&places[0][charline]=%D0%91&places[0][wagon_num]=3&places[0][wagon_class]=%D0%91&places[0][wagon_type]=%D0%9A&places[0][firstname]=dfghdfgh&places[0][lastname]=fghdfg&places[0][bedding]=1&places[0][child]=&places[0][stud]=&places[0][transportation]=0&places[0][reserve]=0&places[0][place_num]=4", "UTF-8");

                } else {
                    for (Map.Entry<String, String> entry : formData.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (formDataStr.length() > 0) {
                            formDataStr += "&";
                        }
                        formDataStr += MessageFormat.format("{0}={1}", key, URLEncoder.encode(value));
                    }
                }
                postData = formDataStr.getBytes("UTF-8");

                uzConnection.setUseCaches( false );
                try {
                    DataOutputStream wr = new DataOutputStream( uzConnection.getOutputStream());
                    wr.write( postData );
                    wr.flush();
                } catch (IOException e){
                    e.printStackTrace();
                }

                String msg = MessageFormat.format("Request on {0} with params {1}", url_adr, postData.toString());
                LogSystem.i("UKRZaliznitsya", msg);


                try {
                    inputStream = uzConnection.getInputStream();
                } catch (Exception e) {
                    inputStream = uzConnection.getErrorStream();
                }

                StringBuilder buffer = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                response_string = buffer.toString();


                if (cookies.size() == 0)
                    cookies = uzConnection.getHeaderFields().get("Set-Cookie");
                if (isBuying)
                    try {
                        reservationId =  uzConnection.getHeaderFields().get("Uz-Txn").get(0);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LogSystem.i("UKRZaliznitsya", MessageFormat.format("Response: {0}", response_string));
            return response_string;
        }
    }

    public String getCurrentStationFromId() {
        return currentStationFromId;
    }

    public void setCurrentStationFromId(String currentStationFromId) {
        this.currentStationFromId = currentStationFromId;
    }

    public String getCurrentStationTillId() {
        return currentStationTillId;
    }

    public void setCurrentStationTillId(String currentStationTillId) {
        this.currentStationTillId = currentStationTillId;
    }

    public String getCurrentDepDate() {
        return currentDepDate;
    }

    public void setCurrentDepDate(String currentDepDate) {
        this.currentDepDate = currentDepDate;
    }

}
