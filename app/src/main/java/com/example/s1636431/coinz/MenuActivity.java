package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
/*
    Menu page for app. Has buttons that allow user to go back to map, bank gold from their wallet, go to the
    community section, and go to stats screen.
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MenuActivity";

    private Integer number;
    private Double bank_amount = 0.0;
    private Long bank_amount_final;




    TextView textUser, textBank;
    ImageView userProfile;
    Button btCommunity;
    Button btBank;
    Button btPlay;
    Button btStat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textUser = (TextView) findViewById(R.id.userID);
        textBank = (TextView) findViewById(R.id.bankID);

        btCommunity = (Button) findViewById(R.id.btCommunity);
        btBank = (Button) findViewById(R.id.btBank);
        btPlay = (Button) findViewById(R.id.btPlay);
        btStat = (Button) findViewById(R.id.btStat);

        btCommunity.setOnClickListener(this);
        btBank.setOnClickListener(this);
        btPlay.setOnClickListener(this);
        btStat.setOnClickListener(this);



        Log.d("USER", MainActivity.mainemail);
        textUser.setText(MainActivity.mainemail);
        if(MainActivity.bank_amount==null) {
            textBank.setText(String.format("Bank: %s Gold", 0));
        } else {
            textBank.setText(String.format("Bank: %s Gold", MainActivity.bank_amount));
        }
        getProfilePicture();
    }


    @Override
    public void onClick(View view) {
        if (view == btBank) {
            updateBank();

        } else if (view == btCommunity) {
            startActivity(new Intent(MenuActivity.this, CommunityActivity.class));
        } else if (view == btPlay) {
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
        } else if (view == btStat) {
            startActivity(new Intent(MenuActivity.this, StatisticsActivity.class));
        }
    }


    /*
        Function responsible for bank submission. Uses a pop up dialog where the player enters the number of
        coins he wishes to deposit.
     */
    @SuppressLint("InflateParams")
    private void updateBank() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View inflate_view = layoutInflater.inflate(R.layout.bank_box, null);
        builder.setMessage("BANKING!")
                .setView(inflate_view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etBank = inflate_view.findViewById(R.id.etBank);

                        if(etBank.getText().toString().isEmpty()) {
                            etBank.setError("Please enter a number");
                            etBank.requestFocus();
                            return;
                        }
                        number = Integer.parseInt(etBank.getText().toString());


                        // Make firebase call to get player info.
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
                        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Map<String, Object> data = new HashMap<>();

                                HashMap<String, Double> wallet = (HashMap<String, Double>) task.getResult().getData().get("wallet");
                                Long amount_banked = (Long) task.getResult().getData().get("amount_banked");

                                Log.d(TAG, wallet.toString());

                                // Perform various checks. First check if the player wallet is empty, then check if the player has banked
                                // 25 coins already, then check if number input by player is valid i.e. not negative or not over 25
                                if (!wallet.isEmpty()) {
                                    if(amount_banked!=null){
                                        if(!(amount_banked+number>25)) {
                                            if(number<=wallet.size() && number>=0) {

                                                // Sort wallet in descending order, so the player deposits the most valuable coins first.
                                                Map<String, Double> sortedWallet = new LinkedHashMap<>();
                                                wallet.entrySet()
                                                        .stream()
                                                        .sorted(comparingByValue(reverseOrder()))
                                                        .forEachOrdered(x -> sortedWallet.put(x.getKey(), x.getValue()));

                                                int count = 0;
                                                bank_amount = 0.0;
                                                bank_amount_final = 0L;
                                                // Update bank amount and remove coin from wallet
                                                for (String key : sortedWallet.keySet()) {
                                                    if(count==number) {
                                                        break;
                                                    }
                                                    bank_amount += sortedWallet.get(key);
                                                    count++;
                                                    wallet.remove(key);
                                                }
                                                Log.d(TAG, bank_amount.toString());

                                                bank_amount_final = Math.round(bank_amount);

                                                Long bank = (Long) task.getResult().getData().get("bank");
                                                bank += bank_amount_final;

                                                amount_banked = number.longValue();

                                                // Update bank, amount_banked (number of coins deposited), and the players wallet on firebase
                                                data.put("amount_banked", amount_banked);
                                                data.put("bank", bank);
                                                data.put("wallet", wallet);
                                                dRef.update(data);

                                                textBank.setText(String.format("Bank: %s Gold", bank.toString()));
                                                Toast.makeText(MenuActivity.this, "Deposited " + number + " coins!",
                                                        Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(MenuActivity.this, "Can't bank " + number + " coins because number exceeds wallet size.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(MenuActivity.this, R.string.toastBankedLimit,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(MenuActivity.this, R.string.toastEmptyWallet,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    /*
       Function responsible for getting the players profile image.
    */
    public void getProfilePicture() {
        userProfile = (ImageView) findViewById(R.id.userImage);

        // Make call to firebase to get player info.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Check if player has a profile picture, otherwise set default image
                if(!task.getResult().getData().get("user_image").toString().isEmpty()) {
                    String profile_url = task.getResult().getData().get("user_image").toString();


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();

                    StorageReference path = storageReference.child(profile_url);

                    final long one_megabyte = 1024*1024;

                    // Downloads the image on firestore and puts it into a bitmap
                    path.getBytes(one_megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                            Bitmap bitmap;
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            userProfile.setImageBitmap(bitmap); // set imageview to downloaded image
                        }
                    });
                } else {
                    userProfile.setImageDrawable(getDrawable(R.drawable.ic_user));
                }


            }
        });


    }


}
