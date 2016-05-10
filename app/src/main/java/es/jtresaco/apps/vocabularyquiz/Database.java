package es.jtresaco.apps.vocabularyquiz;

import org.apache.http.client.HttpClient;

import java.net.URL;

/**
 * Created by javier on 5/10/16.
 */
public class Database {

    public boolean connect() {
        URL url = new URL(link);
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(new URI(link));
    }
}
