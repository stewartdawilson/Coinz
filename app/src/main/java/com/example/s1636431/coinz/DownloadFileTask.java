package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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

    private final String TAG = "DownloadFileTask";

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
            return "";
        }
    }

    private String loadFileFromNetwork(String urlString) throws IOException {
        try {
            return readStream(downloadUrl(new URL(urlString)));
        } catch(IOException e) {
            return e.toString();
        }
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
    private String readStream(InputStream stream) {
        java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A"); // decodes the geojson file
        return s.hasNext() ? s.next() : ""; // returns the geojson file in as a String

    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        if(result.isEmpty()) {
            Toast.makeText(activity, "Unable to download coins map.", Toast.LENGTH_LONG).show(); // if result was empty stop
        } else {
            Log.d("RESULT", result);
            DownloadCompleteRunner.dowloadComplete(result);
            MapMarkers mapMarkers =  new MapMarkers(map,this.activity, result);
            mapMarkers.addCoinz(result, this.activity, map); // Once downloaded, add coinz to map
        }


    }

}