package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.graphics.Camera;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, View.OnClickListener  {

    private String tag = "MainActivity";
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location location;
    private Location originLocation;
    private Location last_location;
    private Long startTime = 0L;
    private Boolean timetrial = false;

    static public List<Coinz> walletList = new ArrayList<Coinz>();
    static public HashMap<String, Double> wallet = new HashMap<>();
    static public ArrayList<String> collected = new ArrayList<>();
    static public Object wallet_data = new Object();
    static public String mainemail;
    static public String bank_amount;



    Button btMenu;
    FloatingActionButton btTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);

        btMenu = (Button) findViewById(R.id.btMenu);
        btTime = (FloatingActionButton) findViewById(R.id.btTimeTrial);
        btTime.setOnClickListener(this);
        btMenu.setOnClickListener(this);

        if(LoginActivity.loggedIn) {
            mainemail = LoginActivity.emailID;
            SignUpActivity.emailID = "";
        }
        if (SignUpActivity.signedIn) {
            mainemail = SignUpActivity.emailID;
            LoginActivity.emailID = "";
        }


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onClick(View view) {
        if(view==btMenu) {
            startActivity(new Intent(MainActivity.this,MenuActivity.class));
        } else if(view==btTime) {
            startTimeTrial();
        }
    }

    private void startTimeTrial() {
        startTime = System.currentTimeMillis();
        timetrial = true;
    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        wipeMap(map);

        Date date = new Date();
        String modifiedDate= new SimpleDateFormat("yyyy/MM/dd").format(date);
        String mapURL = "https://raw.githubusercontent.com/hhowley/JSON_Test/master/appleton.geojson";
        Log.d("TEST", mapURL);
        DownloadFileTask downloadTask = new DownloadFileTask(map, this);
        downloadTask.execute(mapURL);

        enableLocation();
    }

    private void wipeMap(MapboxMap map) {
        for(Marker marker : map.getMarkers()) {
            map.removeMarker(marker);
        }
    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initialiseLocationEngine();
            initialiseLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initialiseLocationEngine() {
        locationEngine =  new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation !=null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initialiseLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()),13.0));

    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TEST", "WORK YOU PIECE OF SHIT");
        if (location != null) {
            if(originLocation != null) {
                last_location = originLocation;
            } else {
                last_location = location;
            }
            originLocation = location;
            checkCoinDistance(location);
            setCameraPosition(location);

        }
    }

    private double calculateDistance(Location location_now,Location location_prev) {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(location_prev.getLatitude() - location_now.getLatitude());
        // deg2rad below
        double dLon = deg2rad(location_prev.getLongitude() - location_now.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(location_now.getLatitude())) * Math.cos(deg2rad(location_prev.getLatitude())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }


    public boolean checkCoinDistance(Location location) {

        if (MapMarkers.markers.equals(null)) {
            return false;
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dRef = db.collection("User").document(mainemail);
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    bank_amount = task.getResult().getData().get("bank").toString();

                    wallet = (HashMap<String, Double>) task.getResult().getData().get("wallet");
                    collected = (ArrayList<String>) task.getResult().getData().get("collected");

                    Log.d("WALLET", wallet_data.toString());

                    Map<String, Object> data = new HashMap<>();
                    for (int i = 0; i < MapMarkers.markers.size(); i++) {

                        if (MapMarkers.markers.get(i).getPosition().distanceTo(new LatLng(location.getLatitude(), location.getLongitude())) < 20) {

                            if(System.currentTimeMillis()>(startTime+30000)){
                                timetrial=false;
                            }

                            Feature fc = MapMarkers.features.get(MapMarkers.markers.get(i).getTitle());
                            Log.d("TEST", MapMarkers.markers.get(i).getTitle());

                            Double value = fc.properties().get("value").getAsDouble();
                            String name = fc.properties().get("currency").getAsString();
                            String id_fc = fc.properties().get("id").getAsString();
                            Log.d("TESTING NAME", name);
                            try {
                                Double rate = MapMarkers.rates.getDouble(name);
                                Log.d("ADDING GOLD", "Converting coin " + name + " to GOLD and adding GOLD to wallet with value" + (value * rate) + "");
                                if(timetrial) {
                                    wallet.put(id_fc, 2*(value * rate));
                                } else {
                                    wallet.put(id_fc, (value * rate));
                                }
                                Log.d("WALLET AMOUNT", wallet.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Coinz coin = new Coinz(name, value, MainActivity.this);
                            walletList.add(coin);
                            Log.d("Adding coinz to wallet", coin.toString());
                            collected.add(fc.properties().get("id").getAsString());
                            map.removeMarker(MapMarkers.markers.get(i).getMarker());
                            MapMarkers.markers.remove(i);

                        }

                    }
                    Double dist = calculateDistance(originLocation, last_location);
                    Double old_dist = task.getResult().getDouble("distance");
                    Double new_dist = dist+old_dist;

                    Long gold = Math.round(MainActivity.wallet.values().stream().mapToDouble(Number::doubleValue).sum())+ Long.valueOf(bank_amount);
                    data.put("gold_alltime", gold);
                    data.put("distance", new_dist);
                    data.put("wallet", wallet);
                    data.put("collected", collected);
                    dRef.set(data, SetOptions.merge());
                }
            });
            return true;
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // present toast or shit
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        } else {
            Log.d("UPDATE","permission not granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("UPDATE","Got permssion");
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        // stop memory leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }




}
