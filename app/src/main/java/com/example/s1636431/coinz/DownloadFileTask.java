package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
    This class is responsible for the downloading of the geojson file (the map). Once the onPostExecute is called
    i.e. the download is finished, then its passed in the MapMarkers class which begins to populate the map.
 */

public class DownloadFileTask extends AsyncTask<String, Void, String> {

    private MapboxMap map;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    DownloadFileTask(MapboxMap map, Activity activity) {
        this.map = map;
        this.activity =  activity;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadFileFromNetwork(urls[0]);
        } catch (IOException e) {
            return
                    "Unable to load content. Check your network connection"
                    ;
        }
    }

    private String loadFileFromNetwork(String urlString) throws IOException {
        return readStream(downloadUrl(new URL(urlString)));
    }

    private InputStream downloadUrl(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000); // milliseconds
        conn.setConnectTimeout(15000); // milliseconds
        conn.setRequestMethod(
                "GET"
        );
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    @NonNull
    private String readStream(InputStream stream)
            throws IOException {
        java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        Log.d("RESULT", result);
        DownloadCompleteRunner.dowloadComplete(result);
        MapMarkers mapMarkers =  new MapMarkers(map,this.activity, result);
        mapMarkers.addCoinz(result, this.activity, map);


    }

}