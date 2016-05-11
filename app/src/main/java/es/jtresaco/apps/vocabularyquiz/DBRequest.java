package es.jtresaco.apps.vocabularyquiz;


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

    private static final String LOGIN_URL = "www.javiertresaco.com/service/vocabulary/";

    private static String getFullAddress(String action) {
        if(action == null) return null;
        String file;
        switch (action) {
            case ACTION_LOGIN:
                file = "login.php";
                break;
            case ACTION_ADDWORD:
                file = "AddWord.php";
                break;
            default:
                return null;
        }
        return LOGIN_URL+file;
    }

    protected static JSONObject send(JSONObject data) {
        JSONObject response = null;
        HttpURLConnection client = null;

        try {
            String sUrl = getFullAddress(data.getString("action"));
            if(sUrl == null) return null;
            URL url = new URL(sUrl);
            client = (HttpURLConnection) url.openConnection();
            client.setDoOutput(true);
            client.setDoInput(true);
            client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            client.setRequestMethod("POST");
            //client.setFixedLengthStreamingMode(request.toString().getBytes("UTF-8").length);
            client.setReadTimeout(3000);
            client.setConnectTimeout(4000);
            client.connect();

            //Log.d(LOG_TAG, "doInBackground(Request)" + args[0]);

            OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
            writer.write(data.toString());
            writer.flush();
            writer.close();

            InputStream input = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d(LOG_TAG, "doInBackground(Resp)" + result.toString());

            response = new JSONObject(result.toString());
            Log.d(LOG_TAG, "Events sent, " + (response.getBoolean("success")?"Parse Succesful":"Some events contained errors"));
        } catch (JSONException e){
            Log.d(LOG_TAG, "JSONException" + e.toString());
            return null;
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException" + e.toString());
            return null;
        } finally {
            client.disconnect();
        }

        return response;
    }

}


