package com.faustgate.ukrzaliznitsya;

import android.os.AsyncTask;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private List<String> cookies = new ArrayList<>();

    private boolean isAuthDataPresent = false;
    private boolean isRequestComplete = false;
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
        headers.put("Content-Type", "application/x-www-form-urlencoded");
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
        String res = "";
        JSONObject dataJsonObj;

        List<HashMap<String, String>> stations = new ArrayList<>();
        try {
            res = new GetUZData().execute("http://booking.uz.gov.ua/en/purchase/station/" + first_letters).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // String response = performRequest(,  null);
        try {
            dataJsonObj = new JSONObject(res);
            JSONArray stt = dataJsonObj.getJSONArray("value");
            for (int i = 0; i < stt.length(); i++) {
                HashMap<String, String> station = new HashMap<>();
                JSONObject sttt = stt.getJSONObject(i);
                station.put("title", sttt.getString("title"));
                station.put("station_id", sttt.getString("station_id"));
                stations.add(station);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stations;
    }

    public JSONObject searchForTickets(String station_from_id, String station_to_id, String date) {
        data.put("station_id_from", station_from_id);
        data.put("station_id_till", station_to_id);
        data.put("date_dep", date);
        try {
            Object res = new GetUZData().execute("http://booking.uz.gov.ua/purchase/search/").get();
            String str = StringEscapeUtils.unescapeJava(res.toString());
//            String test_data = "{\"value\":[{\"num\":\"136\\u0428\",\"model\":0,\"category\":0,\"travel_time\":\"12:10\",\"from\":{\"station_id\":2208001,\"station\":\"\\u041e\\u0434\\u0435\\u0441\\u0430-\\u0413\\u043e\\u043b\\u043e\\u0432\\u043d\\u0430\",\"date\":1474465020,\"src_date\":\"2016-09-21 16:37:00\"},\"till\":{\"station_id\":2218000,\"station\":\"\\u0427\\u0435\\u0440\\u043d\\u0456\\u0432\\u0446\\u0456\",\"date\":1474508820,\"src_date\":\"2016-09-22 04:47:00\"},\"types\":[{\"title\":\"\\u041b\\u044e\\u043a\\u0441\",\"letter\":\"\\u041b\",\"places\":8},{\"title\":\"\\u041a\\u0443\\u043f\\u0435\",\"letter\":\"\\u041a\",\"places\":6},{\"title\":\"\\u041f\\u043b\\u0430\\u0446\\u043a\\u0430\\u0440\\u0442\",\"letter\":\"\\u041f\",\"places\":54}]},{\"num\":\"108\\u0428\",\"model\":0,\"category\":0,\"travel_time\":\"12:18\",\"from\":{\"station_id\":2208001,\"station\":\"\\u041e\\u0434\\u0435\\u0441\\u0430-\\u0413\\u043e\\u043b\\u043e\\u0432\\u043d\\u0430\",\"date\":1474482360,\"src_date\":\"2016-09-21 21:26:00\"},\"till\":{\"station_id\":2218000,\"station\":\"\\u0423\\u0436\\u0433\\u043e\\u0440\\u043e\\u0434\",\"date\":1474526640,\"src_date\":\"2016-09-22 09:44:00\"},\"types\":[{\"title\":\"\\u041b\\u044e\\u043a\\u0441\",\"letter\":\"\\u041b\",\"places\":2},{\"title\":\"\\u041a\\u0443\\u043f\\u0435\",\"letter\":\"\\u041a\",\"places\":38},{\"title\":\"\\u041f\\u043b\\u0430\\u0446\\u043a\\u0430\\u0440\\u0442\",\"letter\":\"\\u041f\",\"places\":41}]}],\"error\":null,\"data\":null,\"captcha\":null}";
//            return new JSONObject(test_data);
            return new JSONObject(str);
        } catch (InterruptedException | JSONException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
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
        for (String el : cookies) {
            str += el;
        }
        headers.put("Cookie", str);
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
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder body = new FormBody.Builder();
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    body.add(key, value);
                }
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(url_adr);
                requestBuilder.post(body.build());
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    requestBuilder.addHeader(key, value);
                }
                Request request = requestBuilder.build();
                Response response = client.newCall(request).execute();

                cookies = response.headers("Set-Cookie");

                response_string = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response_string;
        }
    }
}
