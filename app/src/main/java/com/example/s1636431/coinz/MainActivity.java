package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, View.OnClickListener  {

    private String TAG = "MainActivity";
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Location last_location;
    private Long startTime = 0L;
    private Boolean timetrial = false;
    private Boolean timetrial_used = false;


    static public List<Coinz> walletList = new ArrayList<Coinz>();
    static public HashMap<String, Double> wallet = new HashMap<>();
    static public ArrayList<String> collected = new ArrayList<>();
    static public Object wallet_data = new Object();
    static public String mainemail;
    static public String bank_amount;
    static public Long coins_collected;



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


        // Check if the user logged in from an existing account or just signed up
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
    // Setting up onClick
    public void onClick(View view) {
        if(view==btMenu) {
            startActivity(new Intent(MainActivity.this,MenuActivity.class));
        } else if(view==btTime) {
            if(timetrial_used) {
                Toast.makeText(MainActivity.this, "You've already used time trial today!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Time trial started!",
                        Toast.LENGTH_LONG).show();
                startTimeTrial();
            }

        }
    }

    // Begin time trial bonus feature
    private void startTimeTrial() {
        startTime = System.currentTimeMillis(); // Get current time to signify start of time trial
        timetrial = true;
        timetrial_used = true;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);

        Map<String, Object> data = new HashMap<>();
        data.put("time_trial", timetrial_used);

        dRef.set(data, SetOptions.merge()); // Update that time trial has been used for the day on database
    }


    @Override
    @SuppressLint("SimpleDateFormat")
    /*
       When map is ready to be displayed, make call to firebase database to get current users'
       last login date inorder to check if its a new day or the same day.
    */
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        wipeMap(map);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String date_db = Objects.requireNonNull(task.getResult()).getString("last_login");
                Date current_date = new Date();
                String date_text = new SimpleDateFormat("yyyy/MM/dd").format(current_date);


                // If the dates are equal, don't change anything and download the same map.
                // Else, clear the wallet and the collected array, update last login with new date, then download new map.
                if (date_db != null) {
                    if(date_db.equals(date_text)) {
                        String mapURL = "http://homepages.inf.ed.ac.uk/stg/coinz/" + date_db + "/coinzmap.geojson";
                        Log.d(TAG, mapURL);
                        DownloadFileTask downloadTask = new DownloadFileTask(map, MainActivity.this);
                        downloadTask.execute(mapURL);
                        enableLocation();
                    } else {

                        Map<String, Object> data = new HashMap<>();
                        HashMap<String, Double> wallet;
                        wallet = new HashMap<>();
                        ArrayList<String> collected;
                        collected = new ArrayList<>();
                        Date last_login = new Date();
                        String modifiedDate= new SimpleDateFormat("yyyy/MM/dd").format(last_login);

                        timetrial_used = false; // reset time trial usage since new day

                        data.put("wallet", wallet);
                        data.put("collected", collected);
                        data.put("last_login", modifiedDate);
                        data.put("time_trial", timetrial_used);


                        dRef.set(data, SetOptions.merge()); // Update fields on database

                        String mapURL = "http://homepages.inf.ed.ac.uk/stg/coinz/" + modifiedDate + "/coinzmap.geojson";
                        Log.d(TAG, mapURL);
                        DownloadFileTask downloadTask = new DownloadFileTask(map, MainActivity.this);
                        downloadTask.execute(mapURL);
                        enableLocation();
                    }
                }


            }
        });

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

    /*
        When the location changes, check if this is the first time moving, then check to see if coins are in distance and
        finally set the camera.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
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

    /*
    Taken from https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula,
     to calculate distance travelled by user to new location.
     */
    private double calculateDistance(Location location_now,Location location_prev) {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(location_prev.getLatitude() - location_now.getLatitude());
        // deg2rad below
        double dLon = deg2rad(location_prev.getLongitude() - location_now.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(location_now.getLatitude())) * Math.cos(deg2rad(location_prev.getLatitude())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // Distance in km
        return R*c;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    /*
        Function is responsible for checking the coin distance and collecting and removing the ones
        within 25m of the player.
     */
    public void checkCoinDistance(Location location) {
        if (MapMarkers.markers.isEmpty()) {
            return;
        } else {
            // Make call to firebase to get user info.
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dRef = db.collection("User").document(mainemail);
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    // Get the pieces of data from firebase that we need to update for coin collection
                    bank_amount = task.getResult().getData().get("bank").toString();
                    wallet = (HashMap<String, Double>) task.getResult().getData().get("wallet");
                    collected = (ArrayList<String>) task.getResult().getData().get("collected");
                    coins_collected = (Long) task.getResult().getData().get("coins_collected");

                    Log.d(TAG, String.format("Wallet: %s", wallet_data.toString()));
                    Map<String, Object> data = new HashMap<>();

                    // For every marker in the map, check if the coins within 20m of the players location
                    for (int i = 0; i < MapMarkers.markers.size(); i++) {

                        if (MapMarkers.markers.get(i).getPosition().distanceTo(new LatLng(location.getLatitude(), location.getLongitude())) < 20) {
                            // Check to see if time trial is activate and if the time is over
                            if(System.currentTimeMillis()>(startTime+30000)&&timetrial){
                                timetrial=false;
                                Toast.makeText(MainActivity.this, "Time trial over!",
                                        Toast.LENGTH_LONG).show();
                            }


                            Feature fc = MapMarkers.features.get(MapMarkers.markers.get(i).getTitle()); // Get feature that corresponds to this coin
                            Log.d(TAG, MapMarkers.markers.get(i).getTitle());

                            Double value = fc.properties().get("value").getAsDouble();
                            String name = fc.properties().get("currency").getAsString();
                            String id_fc = fc.properties().get("id").getAsString();
                            Log.d(TAG, name);
                            try {
                                Double rate = MapMarkers.rates.getDouble(name);
                                Log.d(TAG, "Converting coin " + name + " to GOLD and adding GOLD to wallet with value" + (value * rate) + "");
                                if(timetrial) { // If time trial is active double the coin value, otherwise keep normal value.
                                    wallet.put(id_fc, 2*(value * rate));
                                } else {
                                    wallet.put(id_fc, (value * rate));
                                }
                                Log.d(TAG, wallet.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Coinz coin = new Coinz(name, value, MainActivity.this);
                            walletList.add(coin);
                            Log.d(TAG, coin.toString());
                            collected.add(fc.properties().get("id").getAsString()); // Update collected array

                            map.removeMarker(MapMarkers.markers.get(i).getMarker()); // Remove marker from the map
                            MapMarkers.markers.remove(i);
                            coins_collected++;
                        }

                    }
                    // Get distance user has travelled after moving, update the overall distance with new one.
                    // Update wallet, collected array, and other fields on firebase with new values.
                    Double dist = calculateDistance(originLocation, last_location);
                    Double old_dist = task.getResult().getDouble("distance");
                    if(old_dist!=null) {
                        Double new_dist = dist+old_dist;
                        Long gold = Math.round(MainActivity.wallet.values().stream().mapToDouble(Number::doubleValue).sum())+ Long.valueOf(bank_amount);
                        data.put("gold_alltime", gold);
                        data.put("distance", new_dist);
                        data.put("wallet", wallet);
                        data.put("collected", collected);
                        data.put("coins_collected", coins_collected);
                        dRef.set(data, SetOptions.merge());
                    }


                }
            });
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(MainActivity.this, "Need" + permissionsToExplain.get(0)+ " inorder to play Coinz.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        } else {
            Log.d(TAG,"UPDATE: permission not granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"UPDATE: Got permssion");
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
