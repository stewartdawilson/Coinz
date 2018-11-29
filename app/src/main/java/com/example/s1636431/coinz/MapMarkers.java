package com.example.s1636431.coinz;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MapMarkers {

    private Activity activity;
    private MapboxMap map;
    private String result;
    private MarkerOptions marker;


    public static List<MarkerOptions> markers = new ArrayList<>();

    public static HashMap<String, Feature> features =  new HashMap<>();

    public static JSONObject rates;

    private boolean taken;

    MapMarkers(MapboxMap map, Activity activity, String result) {
        this.map = map;
        this.activity =  activity;
        this.result =  result;
    }




    public void addCoinz(String result, Activity activity, MapboxMap map) {
        markers.clear();

        // Make call to firebase to get user info.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 // Get users collected array, which holds which coins they've already collected.
                 ArrayList<String> collected = (ArrayList<String>) task.getResult().getData().get("collected");
                 Log.d("RESULT", result);

                 // Decode geoJson
                 FeatureCollection featureCollection = FeatureCollection.fromJson(result);
                 try {
                     JSONObject json = new JSONObject(result);
                     Log.d("TESTING", json.toString());
                     rates = (JSONObject) json.get("rates");

                     } catch (JSONException e) {
                     e.printStackTrace();
                 }


                 // Iterate over every feature (coin) in the geojson file.
                 for (int i = 0; i< Objects.requireNonNull(featureCollection.features()).size(); i++) {
                     taken = false;


                     Feature fc = Objects.requireNonNull(featureCollection.features()).get(i);
                     // Check is users collected the coin already and set boolean accordingly
                     if (collected != null) {
                         for (int j = 0; j<collected.size(); j++) {
                             if(Objects.requireNonNull(fc.properties()).get("id").getAsString().equals(collected.get(j))) {
                                 taken = true;
                                 break;
                             }
                         }
                     }

                     // If taken, skip this feature (coin)
                     if(taken) {
                         continue;
                     }

                     Point p = (Point) fc.geometry();

                     // Create icons for map markers
                     Bitmap bitmap = createIcons(fc);


                     JsonObject j = fc.properties();
                     // Add marker to map according to the currency type
                     if (j != null) {
                         switch (j.get("currency").getAsString()) {
                             case "QUID":


                                 features.put(j.get("id").toString(), fc);

                                 Icon ic_quid = IconFactory.getInstance(activity).fromBitmap(bitmap);

                                 marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_quid);
                                 markers.add(marker);
                                 map.addMarker(marker);

                                 break;
                             case "SHIL":

                                 features.put(j.get("id").toString(), fc);

                                 Icon ic_shil = IconFactory.getInstance(activity).fromBitmap(bitmap);

                                 marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_shil);
                                 markers.add(marker);
                                 map.addMarker(marker);

                                 break;
                             case "DOLR":

                                 features.put(j.get("id").toString(), fc);

                                 Icon ic_dolr = IconFactory.getInstance(activity).fromBitmap(bitmap);

                                 marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_dolr);
                                 markers.add(marker);
                                 map.addMarker(marker);
                                 break;
                             case "PENY":

                                 features.put(j.get("id").toString(), fc);

                                 Icon ic_peny = IconFactory.getInstance(activity).fromBitmap(bitmap);

                                 marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_peny);
                                 markers.add(marker);
                                 map.addMarker(marker);

                                 break;
                         }
                     }
                 }

             }
         });
    }

    private Bitmap createIcons(Feature fc) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.mapbox_marker_icon_default, activity.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(Objects.requireNonNull(vectorDrawable).getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, Color.parseColor(Objects.requireNonNull(fc.properties()).get("marker-color").getAsString()));
        vectorDrawable.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setTypeface(Typeface.create("Arial",Typeface.BOLD));
        canvas.drawText(Objects.requireNonNull(fc.properties()).get("marker-symbol").getAsString(), 25, 50, paint);
        return bitmap;
    }
}
