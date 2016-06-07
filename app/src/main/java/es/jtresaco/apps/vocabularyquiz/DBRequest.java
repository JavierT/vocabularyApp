package es.jtresaco.apps.vocabularyquiz;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DBRequest {

    private static final String LOG_TAG="Database";

    public static final String ACTION_LOGIN="LOGIN";
    public static final String ACTION_ADDWORD="ADDWORD";
    public static final String ACTION_GETWORD ="GETWORD";
    public static final String ACTION_GETLESSONS ="GETLESSONS";

    private static final String LOGIN_URL = "YOUR OWN WEBSITE HERE";



    protected static JSONObject send(JSONObject data) {
        JSONObject response = null;
        HttpURLConnection client = null;

        try {

            URL url = new URL(LOGIN_URL);

            System.setProperty("http.keepAlive", "false");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("connection", "close");

            conn.connect();

            /*client = (HttpURLConnection) url.openConnection();
            client.setDoOutput(true);
            client.setDoInput(true);
            client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            client.setRequestMethod("POST");
            //client.setFixedLengthStreamingMode(request.toString().getBytes("UTF-8").length);
            client.setReadTimeout(3000);
            client.setConnectTimeout(4000);
            client.connect();*/

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data.toString());
            writer.flush();
            writer.close();

            InputStream in = new BufferedInputStream(client.getInputStream());
            String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            response = new JSONObject(result);

        } catch (JSONException e){
            Log.d(LOG_TAG, "JSONException" + e.toString());
            return null;
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException" + e.toString());
            return null;
        } finally {
            //if(client!=null) client.disconnect();
        }

        return response;
    }

}


