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
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapMarkers {

    private Activity activity;
    private MapboxMap map;
    private String result;
    private MarkerOptions marker;


    public static List<MarkerOptions> markers = new ArrayList<>();

    public static HashMap<String, Feature> features =  new HashMap<>();

    public static JSONObject rates;

    public boolean taken;

    public MapMarkers(MapboxMap map, Activity activity, String result) {
        this.map = map;
        this.activity =  activity;
        this.result =  result;
    }




    public void addCoinz(String result, Activity activity, MapboxMap map) {
        markers.clear();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 ArrayList<String> collected = (ArrayList<String>) task.getResult().getData().get("collected");
                 FeatureCollection featureCollection = FeatureCollection.fromJson(result);

                 try {
                     JSONObject json = new JSONObject(result);
                     Log.d("TESTING", json.toString());
                     rates = (JSONObject) json.get("rates");


                 } catch (JSONException e) {
                     e.printStackTrace();
                 }

                 for (int i = 0; i<featureCollection.features().size(); i++) {
                     taken = false;


                     Feature fc = featureCollection.features().get(i);
                     for (int j = 0; j<collected.size(); j++) {
                         if(fc.properties().get("id").getAsString().equals(collected.get(j))) {
                             taken = true;

                         }
                     }
                     if(taken==true ) {
                         continue;
                     }

                     Point p = (Point) fc.geometry();


                     Drawable vectorDrawable = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.mapbox_marker_icon_default, activity.getTheme());
                     Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                             vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                     Canvas canvas = new Canvas(bitmap);

                     vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                     DrawableCompat.setTint(vectorDrawable, Color.parseColor(fc.properties().get("marker-color").getAsString()));
                     vectorDrawable.draw(canvas);

                     Paint paint = new Paint();
                     paint.setColor(Color.WHITE);
                     paint.setTextAlign(Paint.Align.CENTER);
                     paint.setTextSize(50);
                     paint.setTypeface(Typeface.create("Arial",Typeface.BOLD));
                     canvas.drawText(fc.properties().get("marker-symbol").getAsString(), 25, 50, paint);


                     JsonObject j = fc.properties();
                     if (j.get("currency").getAsString().equals("QUID")) {


                         features.put(j.get("id").toString(), fc);

                         Icon ic_quid = IconFactory.getInstance(activity).fromBitmap(bitmap);

                         marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_quid);
                         markers.add(marker);
                         map.addMarker(marker);

                     } else if (j.get("currency").getAsString().equals("SHIL")) {

                         features.put(j.get("id").toString(), fc);

                         Icon ic_shil = IconFactory.getInstance(activity).fromBitmap(bitmap);

                         marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_shil);
                         markers.add(marker);
                         map.addMarker(marker);

                     } else if (j.get("currency").getAsString().equals("DOLR")) {

                         features.put(j.get("id").toString(), fc);

                         Icon ic_dolr = IconFactory.getInstance(activity).fromBitmap(bitmap);

                         marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_dolr);
                         markers.add(marker);
                         map.addMarker(marker);
                     } else if (j.get("currency").getAsString().equals("PENY")) {

                         features.put(j.get("id").toString(), fc);

                         Icon ic_peny = IconFactory.getInstance(activity).fromBitmap(bitmap);

                         marker = new MarkerOptions().position(new LatLng(p.latitude(), p.longitude())).title(j.get("id").toString()).setIcon(ic_peny);
                         markers.add(marker);
                         map.addMarker(marker);

                     }
                 }

             }
         });
    }
}
