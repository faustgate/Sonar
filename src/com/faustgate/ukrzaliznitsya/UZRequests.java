package com.faustgate.ukrzaliznitsya;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by werwolf on 8/6/16.
 */
public class UZRequests {
    private Map<String, String> headers = new HashMap<>();
    private Map<String, Object> data = new HashMap<>();

    private String requestType = "POST";

    private Map<String, List<String>> lastHeaders;
    boolean isAuthDataPresent = false;
    private boolean isRequestComplete = false;
    String auth_token;

    public UZRequests() {
//        debug = False
//
//        cookies, token = get_auth_data.get_auth_data()
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        // headers.put('Cookie', cookies);
        headers.put("Referer", "http://booking.uz.gov.ua");
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36");


        data.put("another_ec", 0);
        data.put("station_from", "0");
        data.put("station_till", "0");
        data.put("time_dep", "00:00");
        data.put("time_dep_till", "");


        JSONObject result;

    }

    public String get_station_id(String station, String point) {
        String first_letters = station.substring(0, 2).toLowerCase();

        new GetUZData().execute("http://booking.uz.gov.ua/en/purchase/station/od");
        // String response = performRequest(,  null);
        return "sdf";
    }

    private class GetUZData extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            if (!isAuthDataPresent) {
                String page = performRequest("http://booking.uz.gov.ua/en");
                UZGetAuthData uzgd = new UZGetAuthData();
                String auth_token = uzgd.getAuthToken(page);
                isAuthDataPresent=true;
            }
            String response = performRequest(urls[0]);
            return response;

        }

        protected void onPostExecute(String response) {
            try {
                JSONObject dataJsonObj = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private String performRequest(String url_adr) {
            String resultJson = "";
            try {
                URL url = new URL(url_adr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(500);


                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(requestType);
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    urlConnection.setRequestProperty(key, value);
                    // do what you have to do here
                    // In your case, an other loop.
                }

                urlConnection.connect();

                lastHeaders = urlConnection.getHeaderFields();

                // Send POST output.
                //OutputStream os = urlConnection.getOutputStream();
//                os.write(json_data.getBytes());
//                os.flush();

                int responseCode = urlConnection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);


                InputStream inputStream;
                try {
                    inputStream = urlConnection.getInputStream();
                } catch (Exception e) {
                    inputStream = urlConnection.getErrorStream();
                }

                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (ProtocolException e) {
                e.printStackTrace();
                return "null";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "null";
            } catch (IOException e) {
                e.printStackTrace();
                return "null";
            }
            return resultJson;
        }


    }


//        aval_stations_data = json.loads(r '{0}'.format(response.text),
//                'unicode-escape')
//        for station in aval_stations_data['value']:
//        cur.execute(
//                'INSERT INTO stations_ru ('
//        'name, '
//        'station_id) VALUES (?,?)',
//                (station['title'], station['station_id']))
//        print('')
//        con.commit()
//        exit(0)
//        if debug:
//        print(aval_stations_data)
//        if aval_stations_data['error'] or len (
//                aval_stations_data['value']) == 0:
//        print(
//                "No suitable station found, please enter station name again or press Enter to exit")
//        return _get_station_id('', point)
//        aval_stations = aval_stations_data['value']
//
//        if len(aval_stations) > 1:
//        print(
//                "Oops, more than one station correspondent conditions you have entered, " \
//                "please select needed from list bellow")
//        count = 1
//        for station in aval_stations:
//        print(u '{0}. {1}'.format(count, station['title']))
//        count += 1
//        station_index = input("Please enter station index [1]:")
//        if station_index == '':
//        return aval_stations[0]['station_id']
//            else:
//        return aval_stations[int(station_index) - 1]['station_id']
//            else:
//        return aval_stations[0]['station_id']
//    }
//    def init_station_from(self, station_name=''):
//            # data['station_from'] = station_name
//    data['station_id_from'] = _get_station_id(station_name,
//            '"from"')
//
//    def init_station_to(self, station_name=''):
//            # data['station_till'] = station_name
//    data['station_id_till'] = _get_station_id(station_name,
//            '"to"')
//
//    def init_date(self):
//    prompt = "Please enter data in DD.MM.YYYY format [{0}]: "
//    date = input(prompt.format(dt.date.strftime(date, "%d.%m.%Y")))
//            if date != '':
//            try:
//    date = dt.datetime.strptime(date, "%d.%m.%Y")
//    except ValueError:
//            if input("Wrong date, try again?") in ['Y', 'y']:
//            init_date()
//
//    def _send_request(self, adress):
//            if "coach" not in adress:
//    data['date_dep'] = dt.date.strftime(date, "%d.%m.%Y")
//    resp_enc = urllib.parse.urlencode(data)
//    headers['Content-Length'] = len(resp_enc)
//    headers['GV-Ajax'] = 1
//    headers['GV-Token'] = token
//
//        if debug:
//    print(resp_enc)
//
//    response = requests.post(adress, resp_enc, headers=headers)
//            if debug:
//    print(response.status_code, response.reason)
//    data = json.loads(response.text)
//            # print(u'{0}'.format(data["value"]["num"]))
//            if debug:
//    print(data)
//
//        if 'error' in data:
//            if data['error']:
//            if data['value'] == "По заданому Вами напрямку місць немає":
//    print(
//                        "{0} - {1}".format(dt.date.strftime(date,
//                                                            "%d.%m.%Y"),
//    data['value']))
//            return -1
//    print("An error occurred, see server message bellow")
//    print(data['value'])
//        if 'value' in data:
//            return data['value']
//            return data
//
//    def get_trains(self):
//    search_address = 'http://booking.uz.gov.ua/purchase/search/'
//    res = _send_request(search_address)
//            if res == -1:
//    uin = input('There is no places for selected date/direction, '
//                        'would you like find nearest date with places [Y/n]: ')
//            if uin not in ['', 'Y', 'y']:
//    exit()
//            while True:
//    res = _send_request(search_address)
//            if res == -1:
//    date += dt.timedelta(1)
//            time.sleep(1)
//            else:
//            break
//            return res
//
//    def get_places(self, train, dep_time, coach_type):
//    ret = {}
//    data['date_dep'] = dep_time
//    data['train'] = train
//    data['coach_type'] = coach_type
//            cars_response = _send_request(
//            'http://booking.uz.gov.ua/ru/purchase/coaches/')
//        for car in cars_response['coaches']:
//    data['coach_type_id'] = cars_response['coach_type_id']
//    data['coach_num'] = car['num']
//    place_response = _send_request(
//            'http://booking.uz.gov.ua/ru/purchase/coach/')
//    ret[car['num']] = []
//    places = []
//            for key in place_response['places']:
//            for place in place_response['places'][key]:
//            places.append(int(place))
//    ret[car['num']] += places
//        return ret


}
