package com.example.s1636431.coinz;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadFileTask extends AsyncTask<String, Void, String> {

    private MapboxMap map;
    private Activity activity;

    public DownloadFileTask(MapboxMap map, Activity activity) {
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
        Log.d("TEST", s.hasNext() ? s.next() : "");
        return s.hasNext() ? s.next() : "";
//        // Read input from stream, build result as a string
//        StringBuilder sb = new StringBuilder();
//        BufferedReader r = new BufferedReader(new InputStreamReader(stream),1000);
//        for (String line = r.readLine(); line != null; line =r.readLine()){
//            sb.append(line);
//        }
//        stream.close();
//        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        DownloadCompleteRunner.dowloadComplete(result);

        ArrayList<LatLng> points = new ArrayList<>();

        GeoJsonSource source = new GeoJsonSource("geojson", result);
        map.addSource(source);
        map.addLayer(new LineLayer("geojson", "geojson"));

        FeatureCollection featureCollection = FeatureCollection.fromJson(result);

        List<Feature> features = featureCollection.features();

        for (Feature f : features) {
            if (f.geometry() instanceof Point) {
                LatLng coordinates = (LatLng) ((Point) f.geometry()).coordinates();
                map.addMarker(
                        new MarkerOptions().position(coordinates)
                );
            }
        }


    }

}