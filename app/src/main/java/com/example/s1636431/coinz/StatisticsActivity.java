package com.example.s1636431.coinz;

import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import static java.lang.String.format;
/*
    Displays statistics for the player.
 */
public class StatisticsActivity extends AppCompatActivity {




    private final static double walkingFactor = 0.57;
    private static double stepsCount;
    private static double caloriesBurnt;
    private static NumberFormat formatter = new DecimalFormat("#0.00");


    private TextView txtCalories;
    private TextView txtFriends;
    private TextView txtSteps;
    private TextView txtDistance;
    private TextView txtGold;
    private TextView txtEmail;
    private TextView txtCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getData();

        txtCoins =  (TextView) findViewById(R.id.txtCoinsCollected);
        txtCalories = (TextView) findViewById(R.id.txtCaloriesStats);
        txtDistance = (TextView) findViewById(R.id.txtDistanceStats);
        txtEmail = (TextView) findViewById(R.id.txtEmailStats);
        txtFriends = (TextView) findViewById(R.id.txtFriendsStats);
        txtSteps = (TextView) findViewById(R.id.txtStepsStats);
        txtGold = (TextView) findViewById(R.id.txtGoldStats);




    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                String email = MainActivity.mainemail;
                ArrayList<HashMap<String, String>> friends = (ArrayList<HashMap<String, String>>) task.getResult().getData().get("friends");
                Double distance = (Double) task.getResult().getData().get("distance");
                String gold_alltime = task.getResult().getData().get("gold_alltime").toString();
                String weight = task.getResult().getData().get("weight").toString();
                String height = task.getResult().getData().get("height").toString();
                String coins_collected = task.getResult().getData().get("coins_collected").toString();

                caloriesBurned(weight, height, distance);


                // Display all the stats
                txtDistance.setText(format("Total distance travelled (km): %s", formatter.format(distance)));
                txtEmail.setText(String.format("Email: %s", email));
                txtFriends.setText(String.format("Total number of friends: %s",friends.size()));
                txtSteps.setText(String.format("Total number of steps taken: %s", stepsCount));
                txtGold.setText(String.format("Overall Gold collected: %s", gold_alltime));
                txtCalories.setText(format("Total number of calories burnt: %s", formatter.format(caloriesBurnt)));
                txtCoins.setText(String.format("Total number of coinz collected: %s", coins_collected));
            }
        });
    }



    /*
    Taken from https://fitness.stackexchange.com/questions/25472/how-to-calculate-calorie-from-pedometer and adapted using formula found online.
    Calculates the calories burned for the user using his distance, weight and height.
     */
    public void caloriesBurned(String weight_text, String height_text, Double distance) {


        int weight = Integer.parseInt(weight_text); // kg
        double height_inches = Double.parseDouble(height_text)*39.37; // inches


        double step_size_inches = height_inches * 0.413; //inches/stride
        double step_size_feet = step_size_inches / 12; //feet/stride

        double distance_feet = distance*3280.84;

        stepsCount = Math.round(distance_feet/step_size_feet);

        Log.d("MILEAGE", Double.toString(distance_feet));

        Log.d("STEPSIZE", Double.toString(step_size_feet));


        Log.d("FITBIT", Double.toString(stepsCount));


        double caloriesBurnedPerMile = walkingFactor * (weight * 2.2);


        double stepCountMile = 5280 / step_size_feet;

        double conversationFactor = caloriesBurnedPerMile / stepCountMile;

        caloriesBurnt = stepsCount * conversationFactor;
    }
}
