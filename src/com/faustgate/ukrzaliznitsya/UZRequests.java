package com.faustgate.ukrzaliznitsya;

import android.os.AsyncTask;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by werwolf on 8/6/16.
 */

public class UZRequests {
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> data = new HashMap<>();
    private Map<String, List<String>> lastHeaders;
    private Header[] cookies = new Header[]{};

    private boolean isAuthDataPresent = false;
    private String auth_token;
    private String rem_head = "<script>var em = $v.rot13(GV.site.email_support);$$v('#contactEmail')." +
            "attach({ href: 'mailto:' + em, innerHTML: em});" +
            "$v.domReady(function () {Common.performModule();" +
            "Common.pageInformation();" +
            "Common.setOpacHover($$v('#footer .cards_ribbon a, #footer .left a')," +
            " 50);Common.setOpacHover($$v('#footer .right a'), 70);});var _gaq =" +
            " _gaq || [];_gaq.push(['_setAccount', 'UA-33134148-1']);_gaq.push" +
            "(['_trackPageview']);";
    private String rem_foot = "(function () {var ga = document.createElement('script');" +
            "ga.async = true;" +
            "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + " +
            "'.google-analytics.com/ga.js';" +
            "var s = document.getElementsByTagName('script')[0];s.parentNode.insertBefore(ga, s);})();";


    public UZRequests() {
//        debug = False
//
//        cookies, token = get_auth_data.get_auth_data()
        //headers.put("Content-Type", "application/x-www-form-urlencoded");
        // headers.put('Cookie', cookies);
        headers.put("Referer", "http://booking.uz.gov.ua");
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36");

        data.put("another_ec", "0");
        data.put("time_dep", "00:00");
        data.put("time_dep_till", "");

        JSONObject result;

    }

    public List<HashMap<String, String>> getStationsInfo(String station_name) {
        String first_letters = station_name.substring(0, 2).toLowerCase();
        String res;
        JSONObject dataJsonObj;

        List<HashMap<String, String>> stations = new ArrayList<>();
        try {
            res = new GetUZData().execute("http://booking.uz.gov.ua/en/purchase/station/" + first_letters).get();
            dataJsonObj = new JSONObject(res);
            JSONArray stt = dataJsonObj.getJSONArray("value");
            for (int i = 0; i < stt.length(); i++) {
                HashMap<String, String> station = new HashMap<>();
                JSONObject sttt = stt.getJSONObject(i);
                station.put("title", sttt.getString("title"));
                station.put("station_id", sttt.getString("station_id"));
                stations.add(station);
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return stations;
    }

    public String searchForTrains(String station_from_id, String station_to_id, String date) {
        data.put("station_id_from", station_from_id);
        data.put("station_id_till", station_to_id);
        data.put("date_dep", date);
        String result = null;
        try {
            Object res = new GetUZData().execute("http://booking.uz.gov.ua/en/purchase/search/").get();
            result = StringEscapeUtils.unescapeJava(res.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<JSONObject> searchForTickets(JSONObject trainInfo) {

        List<JSONObject> placesInfo = new ArrayList<>();
        Object res;

        try {
            String trainId = trainInfo.getString("num");
            String fromStationId = trainInfo.getJSONObject("from").getString("station_id");
            String toStationId = trainInfo.getJSONObject("till").getString("station_id");
            String date = trainInfo.getJSONObject("from").getString("date");

            JSONArray car_types = trainInfo.getJSONArray("types");

            data.put("station_id_from", fromStationId);
            data.put("station_id_till", toStationId);
            data.put("date_dep", date);
            data.put("train", trainId);

            for (int i = 0; i < car_types.length(); i++) {
                if (data.containsKey("coach_type"))
                    data.remove("coach_type");
                String placeId = trainInfo.getJSONArray("types").getJSONObject(i).getString("letter");
                data.put("coach_type", placeId);

                res = new GetUZData().execute("http://booking.uz.gov.ua/en/purchase/coaches/").get();

                JSONObject coachesInfo = new JSONObject(res.toString());
                JSONArray availableCars = coachesInfo.getJSONArray("coaches");
                for (int j = 0; j < availableCars.length(); j++) {
                    JSONObject currentCoachInfo = availableCars.getJSONObject(j);
                    String coachNum = currentCoachInfo.getString("num");
                    String coachClass = currentCoachInfo.getString("coach_class");
                    String coachTypeId = currentCoachInfo.getString("coach_type_id");
                    if (data.containsKey("coach_num"))
                        data.remove("coach_num");
                    if (data.containsKey("coach_class"))
                        data.remove("coach_class");
                    if (data.containsKey("coach_type_id"))
                        data.remove("coach_type_id");
                    data.put("coach_num", coachNum);
                    data.put("coach_class", coachClass);
                    data.put("coach_type_id", coachTypeId);
                    res = new GetUZData().execute("http://booking.uz.gov.ua/en/purchase/coach/").get();
                    JSONObject coachInfo = new JSONObject(res.toString());
                    JSONObject placesList = coachInfo.getJSONObject("value");
                    availableCars.getJSONObject(j).put("places_list", placesList);
                    String sdf = "affas";

                }

                placesInfo.add(coachesInfo);
            }
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return placesInfo;
    }

    public String buyTickets(JSONObject trainInfo, ArrayList<HashMap<String, String>> ticketDescriptions) {
        String result = null;
        try {

            String trainId = trainInfo.getString("num");
            String fromStationId = trainInfo.getJSONObject("from").getString("station_id");
            String toStationId = trainInfo.getJSONObject("till").getString("station_id");
            String date = trainInfo.getJSONObject("from").getString("date");
            data.put("station_id_from", fromStationId);
            data.put("station_id_till", toStationId);
            data.put("date_dep", date);
            data.put("train", trainId);
            data.put("round_trip", "0");

            for (int i = 0; i < ticketDescriptions.size(); i++) {
                HashMap<String, String> currentTicketDescription = ticketDescriptions.get(i);
                for (Object key : currentTicketDescription.keySet()) {
                    String data_key = MessageFormat.format("places[{0}][{1}]", i, key);
                    String data_value = currentTicketDescription.get(key);
                    data.put(data_key, data_value);
                }
            }

            Object res = new GetUZData().execute("http://booking.uz.gov.ua/en/purchase/cart/add/").get();
            result = StringEscapeUtils.unescapeJava(res.toString());
            String asdf = "asdfa";
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        return "result";
    }

    private String getAuthToken(String page) {
        String token = "";
        String script = page.substring(page.lastIndexOf("<script>"), page.lastIndexOf("</script>"));
        script = script.replace(rem_head, "").replace(rem_foot, "");
        try {
            token = new JJDecoder(script).decode().split("\"")[3];
        } catch (Exception e) {
            e.printStackTrace();
        }
        headers.put("GV-Token", token);
        headers.put("GV-Ajax", "1");
        String str = "";
        for (Header el : cookies) {
            str += el;
            str += ";";
        }
        String cookie = str.replace("Set-Cookie:", "");
        headers.put("Cookie", cookie);
        return token;
    }

    private class GetUZData extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            if (!isAuthDataPresent) {
                String page = performRequest("http://booking.uz.gov.ua/en");
                auth_token = getAuthToken(page);
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

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url_adr);

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    post.addHeader(key, value);
                }

                List<NameValuePair> pairs = new ArrayList<>();

                for (Map.Entry<String, String> entry : data.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    pairs.add(new BasicNameValuePair(key, value));
                }
                //UrlEncodedFormEntity form_data = new UrlEncodedFormEntity(pairs, "UTF-8");
                String data = EntityUtils.toString(new UrlEncodedFormEntity(pairs, "UTF-8"));
                data = data.replaceAll("%5B", "[").replace("%5D", "]");
                StringEntity se = new StringEntity(data, "UTF-8");
                se.setContentType("application/x-www-form-urlencoded");
                se.setContentEncoding("UTF-8");
                post.setEntity(se);


//                StringBuilder builder1 = new StringBuilder();
//                BufferedReader reader1 = new BufferedReader(new InputStreamReader(form_data.getContent()));
//                String line1;
//                while ((line1 = reader1.readLine()) != null) {
//                    builder1.append(line1);
//                }
//                String form_data_string = builder1.toString();
                //post.setEntity(form_data);


//                OkHttpClient client = new OkHttpClient();
//                FormBody.Builder body = new FormBody.Builder();

//                Request.Builder requestBuilder = new Request.Builder();
//                requestBuilder.url(url_adr);
//                requestBuilder.post(body.build());
//                for (Map.Entry<String, String> entry : headers.entrySet()) {
//                    String key = entry.getKey();
//                    String value = entry.getValue();
//                    requestBuilder.addHeader(key, value);
//                }
//                Request request = requestBuilder.build();
//                Response response = client.newCall(request).execute();
                HttpResponse response = client.execute(post);

                if (cookies.length == 0)
                    cookies = response.getHeaders("Set-Cookie");


                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                response_string = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response_string;
        }
    }
}
